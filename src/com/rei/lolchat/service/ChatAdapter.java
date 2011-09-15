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
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.ChatState;
import org.jivesoftware.smackx.ChatStateListener;

import android.os.Environment;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.rei.lolchat.service.aidl.IChat;
import com.rei.lolchat.service.aidl.IMessageListener;

/**
 * An adapter for smack's Chat class.
 * @author darisk
 */
public class ChatAdapter extends IChat.Stub {
    private static final int HISTORY_MAX_SIZE = 50;
    private static final String TAG = "ChatAdapter";

    private final Chat mAdaptee;
    private final Contact mParticipant;
    private String mState;
    private boolean mIsOpen;
    private final List<Message> mMessages;
    private final RemoteCallbackList<IMessageListener> mRemoteListeners = new RemoteCallbackList<IMessageListener>();
    private final MsgListener mMsgListener = new MsgListener();
    private boolean mIsHistory;
    private File mHistoryPath;
    private String mAccountUser;

    /**
     * Constructor.
     * @param chat The chat to adapt
     */
    public ChatAdapter(final Chat chat) {
	mAdaptee = chat;
	mParticipant = new Contact(chat.getParticipant());
	mMessages = new LinkedList<Message>();
	mAdaptee.addMessageListener(mMsgListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Contact getParticipant() throws RemoteException {
	return mParticipant;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessage(com.rei.lolchat.service.Message message) throws RemoteException {
	org.jivesoftware.smack.packet.Message send = new org.jivesoftware.smack.packet.Message();
	send.setTo(message.getTo());
	send.setBody(message.getBody());
	send.setThread(message.getThread());
	send.setSubject(message.getSubject());
	
	org.jivesoftware.smack.packet.Message.Type type;
	switch(message.getType()){
		case Message.MSG_TYPE_CHAT:
			type = org.jivesoftware.smack.packet.Message.Type.chat;
			break;
		case Message.MSG_TYPE_GROUP_CHAT:
			type = org.jivesoftware.smack.packet.Message.Type.groupchat;
			break;
		case Message.MSG_TYPE_NORMAL:
			type = org.jivesoftware.smack.packet.Message.Type.normal;
			break;
		case Message.MSG_TYPE_ERROR:
			type = org.jivesoftware.smack.packet.Message.Type.error;
			break;
		default:
			type = org.jivesoftware.smack.packet.Message.Type.chat;
			break;
	}
	send.setType(type);
	try {
		
	    mAdaptee.sendMessage(send);
	    mMessages.add(message);
	} catch (XMPPException e) {
	    e.printStackTrace();
	}
	String state = Environment.getExternalStorageState();
	if (mIsHistory && Environment.MEDIA_MOUNTED.equals(state))
	    saveHistory(message, mAccountUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addMessageListener(IMessageListener listen) {
	if (listen != null)
	    mRemoteListeners.register(listen);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeMessageListener(IMessageListener listen) {
	if (listen != null) {
	    mRemoteListeners.unregister(listen);
	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getState() throws RemoteException {
	return mState;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setState(String state) throws RemoteException {
	mState = state;
    }

    /**
     * Get the adaptee for the Chat.
     * @return The real chat object
     */
    public Chat getAdaptee() {
	return mAdaptee;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOpen(boolean isOpen) {
	this.mIsOpen = isOpen;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOpen() {
	return mIsOpen;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Message> getMessages() throws RemoteException {
	return Collections.unmodifiableList(mMessages);
    }

    /**
     * Add a message in the chat history.
     * @param msg the message to add
     */
    void addMessage(Message msg) {
	if (mMessages.size() == HISTORY_MAX_SIZE)
	    mMessages.remove(0);
	mMessages.add(msg);
	if (!"".equals(msg.getBody()) && msg.getBody() != null) {
	    String state = Environment.getExternalStorageState();
	    if (mIsHistory && Environment.MEDIA_MOUNTED.equals(state))
		saveHistory(msg, msg.getFrom());
	}
    }

    /**
     * Save message in SDCard.
     * @param msg the message receive
     * @param contactName the name of the contact
     */
    public void saveHistory(Message msg, String contactName) {
	File path = getHistoryPath();
    	File filepath;
    	if (contactName.equals(msg.getFrom()))
    	    filepath = new File(path, StringUtils.parseBareAddress(contactName));
    	else
    	    filepath = new File(path, StringUtils.parseBareAddress(msg.getTo()));
    	path.mkdirs();
	try {
	    FileWriter file = new FileWriter(filepath, true);
	    String log = msg.getTimestamp() + " " + contactName + " " + msg.getBody()
		+ System.getProperty("line.separator");
	    file.write(log);
	    file.close();
	} catch (IOException e) {
	    Log.e(TAG, "Error writing chat history", e);
	}
    }

    /**
     * set History enable/disable.
     * @param isHisory history state
     */
    public void setHistory(boolean isHisory) {
	this.mIsHistory = isHisory;
    }

    /**
     * get History state.
     * @return mIsHistory
     */
    public boolean getHistory() {
	return mIsHistory;
    }

    /**
     * Set Account user name.
     * @param accountUser user name
     */
    public void setAccountUser(String accountUser) {
	mAccountUser = accountUser;
    }

    /**
     * get Account user name.
     * @return mAccountUser
     */
    public String getAccountUser() {
	return mAccountUser;
    }

    /**
     * set History path.
     * @param historyPath history path
     */
    public void setHistoryPath(File historyPath) {
	this.mHistoryPath = historyPath;
    }

    /**
     * get History path.
     * @return mHistoryPath;
     */
    public File getHistoryPath() {
	return mHistoryPath;
    }

    /**
     * Listener.
     */
    private class MsgListener implements ChatStateListener {
	/**
	 * Constructor.
	 */
	public MsgListener() {
	}

	@Override
	public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message message) {
	    Message msg = new Message(message);
	    //TODO add que les message pas de type errors
	    ChatAdapter.this.addMessage(msg);
	    final int n = mRemoteListeners.beginBroadcast();
	    for (int i = 0; i < n; i++) {
		IMessageListener listener = mRemoteListeners.getBroadcastItem(i);
		try {
		    if (listener != null)
			listener.processMessage(ChatAdapter.this, msg);
		} catch (RemoteException e) {
		    Log.w(TAG, "Error while diffusing message to listener", e);
		}
	    }
	    mRemoteListeners.finishBroadcast();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stateChanged(Chat chat, ChatState state) {
	    mState = state.name();
	    final int n = mRemoteListeners.beginBroadcast();

	    for (int i = 0; i < n; i++) {
		IMessageListener listener = mRemoteListeners.getBroadcastItem(i);
		try {
		    listener.stateChanged(ChatAdapter.this);
		} catch (RemoteException e) {
		    Log.w(TAG, e.getMessage());
		}
	    }
	    mRemoteListeners.finishBroadcast();
	}
    }
}
