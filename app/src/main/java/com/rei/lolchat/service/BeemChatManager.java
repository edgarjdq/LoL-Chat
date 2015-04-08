/*
    BEEM is a videoconference application on the Android Platform.

    Copyright (C) 2009 by Frederic-Charles Barthelery,
                          Jean-Manuel Da Silva,
                          Nikita Kozlov,
                          Philippe Lago,
                          Jean Baptiste Vergely,
                          Vincent Veronis.

    This file is part of BEEM.

    BEEM is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BEEM is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with BEEM.  If not, see <http://www.gnu.org/licenses/>.

    Please send bug reports with examples or suggestions to
    contact@beem-project.com or http://dev.beem-project.com/

    Epitech, hereby disclaims all copyright interest in the program "Beem"
    written by Frederic-Charles Barthelery,
               Jean-Manuel Da Silva,
               Nikita Kozlov,
               Philippe Lago,
               Jean Baptiste Vergely,
               Vincent Veronis.

    Nicolas Sadirac, November 26, 2009
    President of Epitech.

    Flavien Astraud, November 26, 2009
    Head of the EIP Laboratory.

*/
package com.rei.lolchat.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.MultiUserChat;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;

import com.rei.lolchat.BeemApplication;
import com.rei.lolchat.BeemService;
import com.rei.lolchat.service.aidl.IChat;
import com.rei.lolchat.service.aidl.IChatMUC;
import com.rei.lolchat.service.aidl.IChatManager;
import com.rei.lolchat.service.aidl.IChatManagerListener;
import com.rei.lolchat.service.aidl.IMessageListener;
import com.rei.lolchat.service.aidl.IRoster;

/**
 * An adapter for smack's ChatManager. This class provides functionnality to handle chats.
 * @author darisk
 */
public class BeemChatManager extends IChatManager.Stub {

    private static final String TAG = "BeemChatManager";
    private final ChatManager mAdaptee;
    private final Map<String, ChatAdapter> mChats = new HashMap<String, ChatAdapter>();
    private final Map<String, ChatMUCAdapter> mMUCChats = new HashMap<String, ChatMUCAdapter>();
    private final ChatListener mChatListener = new ChatListener();
    private final RemoteCallbackList<IChatManagerListener> mRemoteChatCreationListeners =
	new RemoteCallbackList<IChatManagerListener>();
    private final BeemService mService;

    /**
     * Constructor.
     * @param chatManager the smack ChatManager to adapt
     * @param service the service which runs the chat manager
     */
    public BeemChatManager(final ChatManager chatManager, final BeemService service) {
	mService = service;
	mAdaptee = chatManager;
	mAdaptee.addChatListener(mChatListener);
    }

    @Override
    public void addChatCreationListener(IChatManagerListener listener) throws RemoteException {
	if (listener != null)
	    mRemoteChatCreationListeners.register(listener);
    }

    /**
     * Create a chat session.
     * @param contact the contact you want to chat with
     * @param listener listener to use for chat events on this chat session
     * @return the chat session
     */
    @Override
    public IChat createChat(Contact contact, IMessageListener listener) {
	String jid = contact.getJIDWithRes();
	return createChat(jid, listener);
    }

    /**
     * Create a chat session.
     * @param jid the jid of the contact you want to chat with
     * @param listener listener to use for chat events on this chat session
     * @return the chat session
     */
    public IChat createChat(String jid, IMessageListener listener) {
	String key = jid;
	ChatAdapter result;
	if (mChats.containsKey(key)) {
	    result = mChats.get(key);
	    result.addMessageListener(listener);
	    return result;
	}
	Chat c = mAdaptee.createChat(key, null);
	// maybe a little probleme of thread synchronization
	// if so use an HashTable instead of a HashMap for mChats
	result = getChat(c);
	result.addMessageListener(listener);
	return result;
    }
    /**
     * Create a MUC chat session.
     * @param jid the jid of the MUC
     * @param listener listener to use for chat events on this chat session
     * @return the chat session
     */
    public IChatMUC createMUCChat(Contact contact, IMessageListener listener) {
	String jid = contact.getJIDWithRes();
	Log.d(TAG, "Get chat key1 = ");
	return createMUCChat(jid, listener);
    }
    
    public IChatMUC createMUCChat(String jid, IMessageListener listener) {
		String key = StringUtils.parseBareAddress(jid);
		String nick = StringUtils.parseResource(jid);
		ChatMUCAdapter result;
		Log.d(TAG, "Get chat key2 = "+jid);
		if (mMUCChats.containsKey(key)) {
		    result = mMUCChats.get(key);
		    result.addMessageListener(listener);
		    return result;
		}
		MultiUserChat c = new MultiUserChat(mService.getmConnection().getAdaptee(),key);
		result = new ChatMUCAdapter(c, mService, nick);
		mMUCChats.put(key, result);
		result.addMessageListener(listener);
		result.addMessageListener(mChatListener);
		return result;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void destroyChat(IChat chat) throws RemoteException {
	// Can't remove it. otherwise we will lose all futur message in this chat
	// chat.removeMessageListener(mChatListener);
	if (chat == null)
	    return;
	deleteChatNotification(chat);
	mChats.remove(chat.getParticipant().getJID());
    }
    @Override
    public void destroyMUCChat(IChatMUC chat) throws RemoteException {
		if (chat == null)
		    return;
		((ChatMUCAdapter)chat).getAdaptee().leave() ;
		mMUCChats.remove(chat.getRoom().getJID());
		
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteChatNotification(IChat chat) {
	try {
	    mService.deleteNotification(chat.getParticipant().getJID().hashCode());
	} catch (RemoteException e) {
	    Log.v(TAG, "Remote exception ", e);
	}
    }

    /**
     * Get an existing ChatAdapter or create it if necessary.
     * @param chat The real instance of smack chat
     * @return a chat adapter register in the manager
     */
    private ChatAdapter getChat(Chat chat) {
	String key = chat.getParticipant();
	if (mChats.containsKey(key)) {
	    return mChats.get(key);
	}
	ChatAdapter res = new ChatAdapter(chat);
	boolean history = PreferenceManager.getDefaultSharedPreferences(mService.getBaseContext()).getBoolean(
	    "settings_key_history", false);
	String accountUser = PreferenceManager.getDefaultSharedPreferences(mService.getBaseContext()).getString(
	    BeemApplication.ACCOUNT_USERNAME_KEY, "");
	String historyPath = PreferenceManager.getDefaultSharedPreferences(mService.getBaseContext()).getString(
	    BeemApplication.CHAT_HISTORY_KEY, "");
	if ("".equals(historyPath)) historyPath = "/Android/data/com.rei.lolchat/chat/";
	res.setHistory(history);
	res.setAccountUser(accountUser);
	res.setHistoryPath(new File(Environment.getExternalStorageDirectory(), historyPath));
	Log.d(TAG, "getChat put " + key);
	mChats.put(key, res);
	return res;
    }

    @Override
	public ChatAdapter getChat(Contact contact) {
		String key = contact.getJIDWithRes();
		return mChats.get(key);
	 }
	public ChatMUCAdapter getMUCChat(Contact contact) {
		String key = contact.getJID();
    	return mMUCChats.get(key);
    }

    /**
     * This methods permits to retrieve the list of contacts who have an opened chat session with us.
     * @return An List containing Contact instances.
     * @throws RemoteException If a Binder remote-invocation error occurred.
     */
    public List<Contact> getOpenedChatList() throws RemoteException {
	List<Contact> openedChats = new ArrayList<Contact>();
	IRoster mRoster = mService.getBind().getRoster();
	for (ChatAdapter chat : mChats.values()) {
	    if (chat.getMessages().size() > 0) {
	    	Contact t = mRoster.getContact(chat.getParticipant().getJIDWithRes());
		if (t == null)
			t = new Contact(chat.getParticipant().getJIDWithRes());
		openedChats.add(t);
	    }
	}
	return openedChats;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeChatCreationListener(IChatManagerListener listener) throws RemoteException {
	if (listener != null)
	    mRemoteChatCreationListeners.unregister(listener);
    }

    /**
     * A listener for all the chat creation event that happens on the connection.
     * @author darisk
     */
    private class ChatListener extends IMessageListener.Stub implements ChatManagerListener {

	/**
	 * Constructor.
	 */
	public ChatListener() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void chatCreated(Chat chat, boolean locally) {
	    IChat newchat = getChat(chat);
	    Log.d(TAG, "Chat" + chat.toString() + " created locally " + locally + " with " + chat.getParticipant());
	    try {
			newchat.addMessageListener(mChatListener);
			final int n = mRemoteChatCreationListeners.beginBroadcast();
			Log.d(TAG, "Chat " +newchat.getMessages());
			for (int i = 0; i < n; i++) {
			    IChatManagerListener listener = mRemoteChatCreationListeners.getBroadcastItem(i);
			    listener.chatCreated(newchat, locally);
			}
			mRemoteChatCreationListeners.finishBroadcast();
	    } catch (RemoteException e) {
			// The RemoteCallbackList will take care of removing the
			// dead listeners.
			Log.w(TAG, " Error while triggering remote connection listeners in chat creation", e);
	    }
	}

	/**
	 * Create the PendingIntent to launch our activity if the user select this chat notification.
	 * @param chat A ChatAdapter instance
	 * @return A Chat activity PendingIntent
	 */
	private PendingIntent makeChatIntent(Contact c) {
	    Intent chatIntent = new Intent(mService, com.rei.lolchat.ui.Chat.class);
	    chatIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP
		| Intent.FLAG_ACTIVITY_NEW_TASK);
	    chatIntent.setData(c.toUri());
	    PendingIntent contentIntent = PendingIntent.getActivity(mService, 0, chatIntent,
		PendingIntent.FLAG_UPDATE_CURRENT);
	    return contentIntent;
	}

	/**
	 * Set a notification of a new chat.
	 * @param chat The chat to access by the notification
	 * @param msgBody the body of the new message
	 */
	private void notifyNewChat(Contact c, String msgBody) {
        CharSequence tickerText = "";
		try {
			tickerText = mService.getBind().getRoster().getContact(c.getName()).getName();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Notification notification = new Notification(com.rei.lolchat.R.drawable.ic_stat_lol, tickerText, System
		    .currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo(mService, tickerText, msgBody, makeChatIntent(c));
		mService.sendNotification(c.getJID().hashCode(), notification);
	}
	/**
     * Set a notification of a new chat.
     * @param chat The chat to access by the notification
     */
    private void notifyNewChat(IChat chat, String msgBody) {
        try {
            notifyNewChat(chat.getParticipant(), msgBody);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
   /**
    * Set a notification of a MUC chat.
    * @param chat The chat to access by the notification
    */
   private void notifyMUCChat(IChatMUC chat, String msgBody) {
       Log.d(TAG,"poeuet") ;
       try {
               notifyNewChat(chat.getRoom(), msgBody) ;
           } catch (RemoteException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
           }
    }
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processMessage(final IChat chat, Message message) {
		try {
		String body = message.getBody();
		if (!chat.isOpen() && body != null) {
		    if (chat instanceof ChatAdapter) {
			mChats.put(chat.getParticipant().getJID(), (ChatAdapter) chat);
		    }
		    notifyNewChat(chat, body);
		}
	    } catch (RemoteException e) {
		Log.e(TAG, e.getMessage());
	    }
	}
	@Override
    public void processMUCMessage(IChatMUC chat, Message message)
        throws RemoteException {
        String body = message.getBody();
		boolean onlyhl = PreferenceManager.getDefaultSharedPreferences(mService).getBoolean("notification_hls", false) ;
		if (!chat.isOpen() && message.getBody() != null && ( message.isHL() || !onlyhl)) {
             notifyMUCChat(chat, body) ;
        }
    }
	@Override
	public void stateChanged(final IChat chat) {
	}
    }
}
