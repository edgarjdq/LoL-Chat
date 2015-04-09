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
package com.rei.lolchat.ui;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.util.StringUtils;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.rei.lolchat.R;
import com.rei.lolchat.BeemApplication;
import com.rei.lolchat.service.Contact;
import com.rei.lolchat.service.Massage;
import com.rei.lolchat.service.PresenceAdapter;
import com.rei.lolchat.service.aidl.IBeemRosterListener;
import com.rei.lolchat.service.aidl.IChat;
import com.rei.lolchat.service.aidl.IChatMUC;
import com.rei.lolchat.service.aidl.IChatManager;
import com.rei.lolchat.service.aidl.IChatManagerListener;
import com.rei.lolchat.service.aidl.IMessageListener;
import com.rei.lolchat.service.aidl.IRoster;
import com.rei.lolchat.service.aidl.IXmppFacade;
import com.rei.lolchat.ui.dialogs.builders.ChatList;
import com.rei.lolchat.utils.BeemBroadcastReceiver;
import com.rei.lolchat.utils.Status;

/**
 * This class represents an activity which allows the user to chat with his/her contacts.
 * @author Jean-Manuel Da Silva <dasilvj at beem-project dot com>
 */
public class Chat extends Activity implements TextView.OnEditorActionListener {

    private static final String TAG = "Chat";
    private static final Intent SERVICE_INTENT = new Intent();
    static {
	SERVICE_INTENT.setComponent(new ComponentName("com.rei.lolchat", "com.rei.lolchat.BeemService"));
    }
    private Handler mHandler = new Handler();

    private IRoster mRoster;
    private Contact mContact;

    private TextView mContactNameTextView;
    private TextView mContactStatusMsgTextView;
    private TextView mContactChatState;
    private ImageView mContactStatusIcon;
    private LayerDrawable mAvatarStatusDrawable;
    private ListView mMessagesListView;
    private EditText mInputField;
    private Button mSendButton;
    private final Map<Integer, Bitmap> mStatusIconsMap = new HashMap<Integer, Bitmap>();

    private final List<MessageText> mListMessages = new ArrayList<MessageText>();

   
    private IChatManager mChatManager;
    private final IMessageListener mMessageListener = new OnMessageListener();
    private final IChatManagerListener mChatManagerListener = new ChatManagerListener();
    private MessagesListAdapter mMessagesListAdapter = new MessagesListAdapter();

    private final ServiceConnection mConn = new BeemServiceConnection();
    private final BeemBroadcastReceiver mBroadcastReceiver = new BeemBroadcastReceiver();
    private final BeemRosterListener mBeemRosterListener = new BeemRosterListener();
    private IXmppFacade mXmppFacade;
    private String mCurrentAvatarId;
    private boolean mBinded;
    private boolean mCompact;
    private Context context ;
    private String mSubject = "";
    private String mInviteBody = "";
    /**
     * Constructor.
     */
    public Chat() {
	super();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    protected void onCreate(Bundle savedBundle) {
	super.onCreate(savedBundle);
	this.registerReceiver(mBroadcastReceiver, new IntentFilter(BeemBroadcastReceiver.BEEM_CONNECTION_CLOSED));
	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
	mCompact = settings.getBoolean(BeemApplication.USE_COMPACT_CHAT_UI_KEY, false);
	// UI
	if (!mCompact) {
	    setContentView(R.layout.chat);
	    mContactNameTextView = (TextView) findViewById(R.id.chat_contact_name);
	    mContactStatusMsgTextView = (TextView) findViewById(R.id.chat_contact_status_msg);
	    mContactChatState = (TextView) findViewById(R.id.chat_contact_chat_state);
	    mContactStatusIcon = (ImageView) findViewById(R.id.chat_contact_status_icon);
	    mAvatarStatusDrawable = (LayerDrawable) mContactStatusIcon.getDrawable();
	    mAvatarStatusDrawable.setLayerInset(1, 75, 75, -8, -8);
	} else {
	    setContentView(R.layout.chat_compact);
	} 
	mMessagesListView = (ListView) findViewById(R.id.chat_messages);
	mMessagesListView.setAdapter(mMessagesListAdapter);
	mInputField = (EditText) findViewById(R.id.chat_input);
	mInputField.setOnEditorActionListener(this);
	mInputField.requestFocus();
	mSendButton = (Button) findViewById(R.id.chat_send_message);
	mSendButton.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
	    	sendMessage(); 
	    }
	});
	prepareIconsStatus();
    }

    @Override
    protected void onResume() {
	super.onResume();
	mContact = new Contact(getIntent().getData());
	if (!mBinded) {
	    bindService(SERVICE_INTENT, mConn, BIND_AUTO_CREATE);
	    mBinded = true;
	}
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    protected void onDestroy() {
	super.onDestroy();
	this.unregisterReceiver(mBroadcastReceiver);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    protected void onPause() {
	super.onPause();
	try {
		IChat mChat = mChatManager.getChat(mContact) ;
	    if (mChat != null) {
		mChat.setOpen(false);
		mChat.removeMessageListener(mMessageListener);
	    }
	    IChatMUC mChatMUC = mChatManager.getMUCChat(mContact) ;
	    if (mChatMUC != null) {
		mChatMUC.setOpen(false);
		mChatMUC.removeMessageListener(mMessageListener);
	    }
	    if (mRoster != null)
		mRoster.removeRosterListener(mBeemRosterListener);
	    if (mChatManager != null)
		mChatManager.removeChatCreationListener(mChatManagerListener);
	} catch (RemoteException e) {
	    Log.e(TAG, e.getMessage());
	}catch (NullPointerException e){
		Log.e(TAG, "error? "+e.getMessage());
	}
	if (mBinded) {
	    unbindService(mConn);
	    mBinded = false;
	}
	mXmppFacade = null;
	mRoster = null;
	
	mChatManager = null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    protected void onNewIntent(Intent intent) {
	super.onNewIntent(intent);
	setIntent(intent);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
	super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
	super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public final boolean onCreateOptionsMenu(Menu menu) {
	super.onCreateOptionsMenu(menu);
	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.chat, menu);
	return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public final boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	    case R.id.chat_menu_contacts_list:
		Intent contactListIntent = new Intent(this, ContactList.class);
		contactListIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(contactListIntent);
		break;
	    case R.id.chat_menu_change_chat:
		try {
		    final List<Contact> openedChats = mChatManager.getOpenedChatList();
		    Log.d(TAG, "opened chats = " + openedChats);
		    Dialog chatList = new ChatList(Chat.this, openedChats).create();
		    chatList.show();
		} catch (RemoteException e) {
		    Log.e(TAG, e.getMessage());
		}
		break;
	    case R.id.chat_menu_close_chat:
		try {
			IChat mChat = mChatManager.getChat(mContact) ;
		    mChatManager.destroyChat(mChat);
		    IChatMUC mMUCChat = mChatManager.getMUCChat(mContact) ;
		    mChatManager.destroyMUCChat(mMUCChat);
		} catch (RemoteException e) {
		    Log.e(TAG, e.getMessage());
		}
		this.finish();
		break;
	    default:
		return false;
	}
	return true;
    }

    /**
     * Change the displayed chat.
     * @param contact the targeted contact of the new chat
     * @throws RemoteException If a Binder remote-invocation error occurred.
     */
    private void changeCurrentChat(Contact contact) throws RemoteException {
    	if (mContact.isMUC()) {
	    	IChatMUC mChat = mChatManager.getMUCChat(mContact) ;
		    if (mChat != null) {
			    mChat.setOpen(false);
			    mChat.removeMessageListener(mMessageListener);
			}
	    } else {
	    	IChat mChat = mChatManager.getChat(mContact) ;
		    if (mChat != null) {
			    mChat.setOpen(false);
			    mChat.removeMessageListener(mMessageListener);
			}
	    }
	    
	    if (contact.isMUC()) {
			IChatMUC newChat = mChatManager.getMUCChat(contact);
			if (newChat != null) {
			    newChat.setOpen(true);
			    newChat.addMessageListener(mMessageListener);
			}
			mContact = contact;
	    } else {
			IChat newChat = mChatManager.getChat(contact);
			if (newChat != null) {
			    newChat.setOpen(true);
			    newChat.addMessageListener(mMessageListener);
			    mChatManager.deleteChatNotification(newChat);
			}
			mContact = mRoster.getContact(contact.getJID());
	    }
	    String res = contact.getSelectedRes();
		if (mContact == null)
		    mContact = contact;
		if (!"".equals(res)) {
		    mContact.setSelectedRes(res);
		}
		updateContactInformations();
		updateContactStatusIcon();
	
		playRegisteredTranscript();
    }

    /**
     * Get all messages from the current chat and refresh the activity with them.
     * @throws RemoteException If a Binder remote-invocation error occurred.
     */
    private void playRegisteredTranscript() throws RemoteException {
    	mListMessages.clear();
    	List<MessageText> msgList = null ;
		if (mContact.isMUC()) {
			IChatMUC mChat = mChatManager.getMUCChat(mContact) ;
			if (mChat != null) {
				msgList = convertMessagesList(mChat.getMessages());
			}
		} else {
			IChat mChat = mChatManager.getChat(mContact) ;
			if (mChat != null) {
				msgList = convertMessagesList(mChat.getMessages());
			}
		}
		
		if (msgList != null) {
		    mListMessages.addAll(msgList);
		    mMessagesListAdapter.notifyDataSetChanged();
		}
    }

    
    
    /**
     * Convert a list of Message coming from the service to a list of MessageText that can be displayed in UI.
     * @param chatMassages the list of Message
     * @return a list of message that can be displayed.
     */
    private List<MessageText> convertMessagesList(List<Massage> chatMassages) {
	List<MessageText> result = new ArrayList<MessageText>(chatMassages.size());
	String remoteName = mContact.getName();
	String localName = getString(R.string.chat_self);
	MessageText lastMessage = null;
	for (Massage m : chatMassages) {
	    String name = remoteName;
	    String fromBareJid = StringUtils.parseBareAddress(m.getFrom());
	    if (m.getType() == Massage.MSG_TYPE_ERROR) {
			lastMessage = null;
			result.add(new MessageText(fromBareJid, name, m.getBody(), true, false, m.getTimestamp()));
	    } else if (m.getType() == Massage.MSG_TYPE_CHAT || m.getType() == Massage.MSG_TYPE_GROUP_CHAT) {
			if (m.getType() == Massage.MSG_TYPE_GROUP_CHAT) {
				name = StringUtils.parseResource(m.getFrom());
			}
		    	
		    if (fromBareJid == null) { //nofrom or from == yours
			    name = localName;
			    fromBareJid = "";
			}
			if (m.getBody() != null) {
				if (lastMessage != null && lastMessage.getBareJid().equals(fromBareJid) && lastMessage.getName().equals(name)) {
			    	if (m.isHL()) {
			    		lastMessage.setHL(true) ;
			    	}
					lastMessage.setMessage(lastMessage.getMessage().concat("\n" + m.getBody()));
				} else {
					lastMessage = new MessageText(fromBareJid, name, m.getBody(), false, m.isHL(), m.getTimestamp());
					result.add(lastMessage);
			    }
			}
	    } else if (m.getType() == Massage.MSG_TYPE_NORMAL){
	    	if(m.isInvite()){
				lastMessage = new MessageText(fromBareJid, name, m.getBody(), false, m.isHL(), m.getTimestamp(), m);
				result.add(lastMessage);
			}
	    }
	}
	return result;
    }

    /**
     * {@inheritDoc}.
     */
    private final class BeemServiceConnection implements ServiceConnection {

	/**
	 * Constructor.
	 */
	public BeemServiceConnection() {
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
	    mXmppFacade = IXmppFacade.Stub.asInterface(service);
	    try {
		mRoster = mXmppFacade.getRoster();
		if (mRoster != null)
		    mRoster.addRosterListener(mBeemRosterListener);
		mChatManager = mXmppFacade.getChatManager();
		if (mChatManager != null) {
		    mChatManager.addChatCreationListener(mChatManagerListener);
		    changeCurrentChat(mContact);
		}
		if (mContact!= null && mContact.isMUC()) {
			IChatMUC muc = mChatManager.getMUCChat(mContact) ;
			if (muc == null) {
				Log.d(TAG,"Service connected : "+mContact.getJIDWithRes()) ;
				IChatMUC mChatMUC = mChatManager.createMUCChat(mContact, mMessageListener) ;
				mChatMUC.addMessageListener(mMessageListener) ;
				mChatMUC.setOpen(true);
			}
		}
	    } catch (RemoteException e) {
		Log.e(TAG, e.getMessage());
	    }
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public void onServiceDisconnected(ComponentName name) {
	    mXmppFacade = null;
	    try {
		mRoster.removeRosterListener(mBeemRosterListener);
		mChatManager.removeChatCreationListener(mChatManagerListener);
	    } catch (RemoteException e) {
		Log.e(TAG, e.getMessage());
	    }
	}
    }

    /**
     * {@inheritDoc}.
     */
    private class BeemRosterListener extends IBeemRosterListener.Stub {

	/**
	 * Constructor.
	 */
	public BeemRosterListener() {
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public void onEntriesAdded(List<String> addresses) throws RemoteException {
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public void onEntriesDeleted(List<String> addresses) throws RemoteException {
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public void onEntriesUpdated(List<String> addresses) throws RemoteException {
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public void onPresenceChanged(final PresenceAdapter presence) throws RemoteException {
	    if (mContact.getJID().equals(StringUtils.parseBareAddress(presence.getFrom()))) {
		mHandler.post(new Runnable() {
		    @Override
		    public void run() {
			mContact.setStatus(presence.getStatus());
			mContact.setMsgState(presence.getStatusText());
			updateContactInformations();
			updateContactStatusIcon();
		    }
		});
	    }
	}
    }

    /**
     * {@inheritDoc}.
     */
    private class OnMessageListener extends IMessageListener.Stub {

	/**
	 * Constructor.
	 */
	public OnMessageListener() {
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public void processMessage(IChat chat, final Massage msg) throws RemoteException {
	    final String fromBareJid = StringUtils.parseBareAddress(msg.getFrom());
	    if (mContact.getJID().equals(fromBareJid)) {
		mHandler.post(new Runnable() {

		    @Override
		    public void run() {
			if (msg.getType() == Massage.MSG_TYPE_ERROR) {
			    mListMessages.add(new MessageText(fromBareJid, mContact.getName(),
			    msg.getBody(), true, false, msg.getTimestamp()));
			    mMessagesListAdapter.notifyDataSetChanged();
			} else if (msg.getBody() != null) {
			    MessageText lastMessage = null;
			    if (mListMessages.size() != 0)
				lastMessage = mListMessages.get(mListMessages.size() - 1);
			    String name ;
	               if (mContact.isMUC()) {
	                   name = StringUtils.parseResource(msg.getFrom());
	               } else {
	                   name = mContact.getName();
	               }
	               if (lastMessage != null && lastMessage.getBareJid().equals(fromBareJid) && lastMessage.getName().equals(name) && !msg.isInvite()) {
				    	if (msg.isHL()) {
				    		lastMessage.setHL(true) ;
				    	}
						lastMessage.setMessage(lastMessage.getMessage().concat("\n" + msg.getBody()));
	                    lastMessage.setTimestamp(msg.getTimestamp());
	                    mListMessages.set(mListMessages.size() - 1, lastMessage);
	                } else if (msg.getBody() != null) {
	                	if(msg.isInvite()){
							mListMessages.add(new MessageText(fromBareJid, name, msg.getBody(), false, msg.isHL(), msg.getTimestamp(), msg));
						}else{
							mListMessages.add(new MessageText(fromBareJid, name, msg.getBody(), false, msg.isHL(), msg.getTimestamp()));
						}
				    	
	                    mMessagesListAdapter.notifyDataSetChanged();
	                }
			   
				}
		    }
		  });
		}
	}
	@Override
    public void processMUCMessage(IChatMUC chat, final Massage msg) throws RemoteException {
        processMessage(null, msg) ;
    }
	/**
	 * {@inheritDoc}.
	 */
	@Override
	public void stateChanged(IChat chat) throws RemoteException {
	    final String state = chat.getState();
	    mHandler.post(new Runnable() {
		@Override
		public void run() {
		    String text = null;
		    if ("active".equals(state)) {
			text = Chat.this.getString(R.string.chat_state_active);
		    } else if ("composing".equals(state)) {
			text = Chat.this.getString(R.string.chat_state_composing);
		    } else if ("gone".equals(state)) {
			text = Chat.this.getString(R.string.chat_state_gone);
		    } else if ("inactive".equals(state)) {
			text = Chat.this.getString(R.string.chat_state_inactive);
		    } else if ("paused".equals(state)) {
			text = Chat.this.getString(R.string.chat_state_active);
		    }
		    if (!mCompact)
			mContactChatState.setText(text);
		}
	    });

	}
    }

    /**
     * Update the contact informations.
     */
    private void updateContactInformations() {
	// Check for a contact name update
	String name = mContact.getName();
	String level = "";
	String res = mContact.getSelectedRes();
	if (!"".equals(res))
	    name += "(" + res + ")";
	if (!mCompact) {
		if(mContact.getLevel() != null){
			level = "["+mContact.getLevel()+"] ";
		}
		String gameStatus = "";
		if(mContact.getGameStatus() != null){
			gameStatus = "["+mContact.getGameStatus()+"] ";
		}
	    if (!(mContactNameTextView.getText().toString().equals(name)))
	    	mContactNameTextView.setText(level+name);
	    //Check for a contact status message update
	    if (!(mContactStatusMsgTextView.getText().toString().equals(mContact.getMsg()))) {
	    	mContactStatusMsgTextView.setText(gameStatus+mContact.getMsg());
	    }
	} else {
	    Mode m = Status.getPresenceModeFromStatus(mContact.getStatus());
	    if (m == null)
		setTitle(getString(R.string.chat_name) + " " + name
		    + " (" + getString(R.string.contact_status_msg_offline) + ")");
	    else
		setTitle(getString(R.string.chat_name) + " " + name + " (" + m.name() + ")");
	}
    }



    /**
     * Update the contact status icon.
     */
    private void updateContactStatusIcon() {
	if (mCompact)
	    return;
	String id = mContact.getAvatarId();
	if (id == null)
	    id = "";
	Log.d(TAG, "update contact icon  : " + id);
	if (!id.equals(mCurrentAvatarId)) {
	    Drawable avatar = getAvatarDrawable(mContact.getAvatarId());
	    mAvatarStatusDrawable.setDrawableByLayerId(R.id.avatar, avatar);
	    mCurrentAvatarId = id;
	}
	mContactStatusIcon.setImageLevel(mContact.getStatus());
    }

    /**
     * Get a Drawable containing the avatar icon.
     *
     * @param avatarId the avatar id to retrieve or null to get default
     * @return a Drawable
     */
    private Drawable getAvatarDrawable(String avatarId) {
	Drawable avatarDrawable = null;
	if(avatarId == null){
    	avatarDrawable = getResources().getDrawable(R.drawable.lol128);
    }else{
    	try{
    	avatarDrawable = getResources().getDrawable(getResources().getIdentifier("profile"+avatarId, "drawable", getPackageName()));
    	}catch (Exception e) {
    		avatarDrawable = getResources().getDrawable(R.drawable.lol128);
    	}
    }
	return avatarDrawable;
    }

    /**
     * Prepare the status icons map.
     */
    private void prepareIconsStatus() {
	mStatusIconsMap.put(Status.CONTACT_STATUS_AVAILABLE, BitmapFactory.decodeResource(getResources(),
	    android.R.drawable.presence_online));
	mStatusIconsMap.put(Status.CONTACT_STATUS_AVAILABLE_FOR_CHAT, BitmapFactory.decodeResource(getResources(),
	    android.R.drawable.presence_online));
	mStatusIconsMap.put(Status.CONTACT_STATUS_AWAY, BitmapFactory.decodeResource(getResources(),
	    android.R.drawable.presence_away));
	mStatusIconsMap.put(Status.CONTACT_STATUS_BUSY, BitmapFactory.decodeResource(getResources(),
	    android.R.drawable.presence_busy));
	mStatusIconsMap.put(Status.CONTACT_STATUS_DISCONNECT, BitmapFactory.decodeResource(getResources(),
	    android.R.drawable.presence_offline));
	mStatusIconsMap.put(Status.CONTACT_STATUS_UNAVAILABLE, BitmapFactory.decodeResource(getResources(),
	    R.drawable.status_requested));
    }

    /**
     * {@inheritDoc}.
     */
    private class MessagesListAdapter extends BaseAdapter {

	/**
	 * Constructor.
	 */
	public MessagesListAdapter() {
	}

	/**
	 * Returns the number of messages contained in the messages list.
	 * @return The number of messages contained in the messages list.
	 */
	@Override
	public int getCount() {
	    return mListMessages.size();
	}

	/**
	 * Return an item from the messages list that is positioned at the position passed by parameter.
	 * @param position The position of the requested item.
	 * @return The item from the messages list at the requested position.
	 */
	@Override
	public Object getItem(int position) {
	    return mListMessages.get(position);
	}

	/**
	 * Return the id of an item from the messages list that is positioned at the position passed by parameter.
	 * @param position The position of the requested item.
	 * @return The id of an item from the messages list at the requested position.
	 */
	@Override
	public long getItemId(int position) {
	    return position;
	}

	/**
	 * Return the view of an item from the messages list.
	 * @param position The position of the requested item.
	 * @param convertView The old view to reuse if possible.
	 * @param parent The parent that this view will eventually be attached to.
	 * @return A View corresponding to the data at the specified position.
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
	    View sv;
	    if (convertView == null) {
		LayoutInflater inflater = Chat.this.getLayoutInflater();
		    sv = inflater.inflate(R.layout.chat_msg_row, null);
	    } else {
		sv = convertView;
	    }
	    DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
	    final MessageText msg = mListMessages.get(position);
	    if(msg.isInvite()){
	    	Button joinGame = (Button) sv.findViewById(R.id.join_game);
	    	//joinGame.setVisibility(View.VISIBLE);
	    	joinGame.setOnClickListener(new View.OnClickListener() {
	    		  public void onClick(View v) {
	    			  	Massage m = msg.getInvite();
	    			  	/*context = Chat.this.getApplicationContext();
	    			    String room = "1742641244";
		  		    	String pseudo = "test";
		  			    Contact c = new Contact(room, true);
		  			    Intent i = new Intent(context, Chat.class);
		  			    i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP
		  					| Intent.FLAG_ACTIVITY_NEW_TASK);
		  			    i.setData(c.toUri(pseudo));
		  			    context.startActivity(i);*/
	    			  //	
	    			  mSubject = "GAME_INVITE_LIST_STATUS";
	    			  mSubject = "GAME_INVITE_ACCEPT";
	    			  mInviteBody = m.getBodyXml();//.replace(">", "&gt;").replace("<", "&lt;");
	    			  //mInviteBody = "<body><participants><invitee status=\"ACCEPTED\" name=\"The17\" /></participants></body>";
	    			  //mInviteBody = "&lt;body&gt;&lt;participants&gt;&lt;invitee status=&quot;ACCEPTED&quot; name=&quot;the17&quot; /&gt;&lt;/participants&gt;&lt;/body&gt;";
	    			  mInputField.setText(mInviteBody);
	    			  sendMessage();
	    			  /*mSubject = "GAME_INVITE_ACCEPT_ACK";
	    			  mInviteBody = m.getBodyXml();//.replace(">", "&gt;").replace("<", "&lt;")
	    			  mInputField.setText(mInviteBody);
	    			  sendMessage();*/
	    			  mSubject = "";
	    			  mInviteBody = "";
	    		  }
	    		}
	    	);
	    }
	    
	    TextView msgName = (TextView) sv.findViewById(R.id.chatmessagename);
	    msgName.setText(msg.getName());
	    msgName.setTextColor(Color.WHITE);
	    if (msg.isHL()) {
	    	msgName.setTextColor(Color.RED);
	    } else {
	    	msgName.setTextColor(Color.WHITE);
	    }
	    msgName.setError(null);
	    TextView msgText = (TextView) sv.findViewById(R.id.chatmessagetext);
	    msgText.setText(msg.getMessage());
	    registerForContextMenu(msgText);
	    TextView msgDate = (TextView) sv.findViewById(R.id.chatmessagedate);
	    String date = df.format(msg.getTimestamp());
	    msgDate.setText(date);
	    if (msg.isError()) {
		String err = getString(R.string.chat_error);
		msgName.setText(err);
		msgName.setTextColor(Color.RED);
		msgName.setError(err);
	    }
	    return sv;
	}
    }

    
    private class Invite{
    	Massage m;
    	public Massage getMessage(){
    		return m;
    	}
    }
    /**
     * Class which simplify an Xmpp text message.
     * @author Jean-Manuel Da Silva <dasilvj at beem-project dot com>
     */
    private class MessageText {
	private String mBareJid;
	private String mName;
	private String mMessage;
	private boolean mIsError;
	private boolean mHL;
	private Date mTimestamp;
	private Massage mInviteMassage;
	private boolean mIsInvite;

	/**
	 * Constructor.
	 * @param bareJid A String containing the bare JID of the message's author.
	 * @param name A String containing the name of the message's author.
	 * @param message A String containing the message.
	 */
	public MessageText(final String bareJid, final String name, final String message) {
	    mBareJid = bareJid;
	    mName = name;
	    mMessage = message;
	    mIsError = false;
	}

	/**
	 * Constructor.
	 * @param bareJid A String containing the bare JID of the message's author.
	 * @param name A String containing the name of the message's author.
	 * @param message A String containing the message.
	 * @param isError if the message is an error message.
	 */
	public MessageText(final String bareJid, final String name, final String message,
	    final boolean isError) {
	    mBareJid = bareJid;
	    mName = name;
	    mMessage = message;
	    mIsError = isError;
	}

	/**
	 * Constructor.
	 * @param bareJid A String containing the bare JID of the message's author.
	 * @param name A String containing the name of the message's author.
	 * @param message A String containing the message.
	 * @param isError if the message is an error message.
	 * @param date the time of the message.
	 */
	public MessageText(final String bareJid, final String name, final String message,
		final boolean isError, final boolean isHL, Date date) {
	    mBareJid = bareJid;
	    mName = name;
	    mMessage = message;
	    mIsError = isError;
		mHL = isHL ;
	    mTimestamp = date;
	}
	public MessageText(final String bareJid, final String name, final String message,
			final boolean isError, final boolean isHL, Date date, Massage inviteMassage) {
		    mBareJid = bareJid;
		    mName = name;
		    mMessage = message;
		    mIsError = isError;
			mHL = isHL ;
		    mTimestamp = date;
		    mInviteMassage = inviteMassage;
		    mIsInvite = true;
		}
	public boolean isInvite(){
		return mIsInvite;
	}
	public Massage getInvite(){
		return mInviteMassage;
	}
	/**
	 * JID attribute accessor.
	 * @return A String containing the bare JID of the message's author.
	 */
	public String getBareJid() {
	    return mBareJid;
	}

	/**
	 * Name attribute accessor.
	 * @return A String containing the name of the message's author.
	 */
	public String getName() {
	    return mName;
	}

	/**
	 * Message attribute accessor.
	 * @return A String containing the message.
	 */
	public String getMessage() {
	    return mMessage;
	}

	/**
	 * JID attribute mutator.
	 * @param bareJid A String containing the author's bare JID of the message.
	 */
	@SuppressWarnings("unused")
	public void setBareJid(String bareJid) {
	    mBareJid = bareJid;
	}

	/**
	 * Name attribute mutator.
	 * @param name A String containing the author's name of the message.
	 */
	@SuppressWarnings("unused")
	public void setName(String name) {
	    mName = name;
	}

	/**
	 * Message attribute mutator.
	 * @param message A String containing a message.
	 */
	public void setMessage(String message) {
	    mMessage = message;
	}

	/**
	 * Get the message type.
	 * @return true if the message is an error message.
	 */
	public boolean isError() {
	    return mIsError;
	}

	public boolean isHL() {
		return mHL; 
	}
	
	public void setHL(boolean hl) {
		mHL = hl ;
	}
	/**
	 * Set the Date of the message.
	 * @param date date of the message.
	 */
	public void setTimestamp(Date date) {
	    mTimestamp = date;
	}

	/**
	 * Get the Date of the message.
	 * @return if it is a delayed message get the date the message was sended.
	 */
	public Date getTimestamp() {
	    return mTimestamp;
	}

    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	if (v == mInputField && actionId == EditorInfo.IME_ACTION_SEND) {
	    sendMessage();
	    return true;
	}
	return false;
    }

    /**
     * Send an XMPP message.
     */
    private void sendMessage() {
	final String inputContent = mInputField.getText().toString();

	if (!"".equals(inputContent)) {
		Massage msgToSend ;
		if (mContact.isMUC()) {
			msgToSend = new Massage(mContact.getJID(), Massage.MSG_TYPE_GROUP_CHAT);
		} else {
			if(mSubject.equals("GAME_INVITE_ACCEPT"))
				msgToSend = new Massage(mContact.getJIDWithRes(), Massage.MSG_TYPE_NORMAL);
			else
				msgToSend = new Massage(mContact.getJIDWithRes(), Massage.MSG_TYPE_CHAT);
		}
		
		msgToSend.setSubject(mSubject);
		msgToSend.setBody(inputContent);
	    
		
		
	    try {
	    	if (mContact.isMUC()) {
	    		IChatMUC mChatMUC = mChatManager.getMUCChat(mContact) ;
				mChatMUC.sendMessage(msgToSend);
	    	} else {
	    		IChat mChat = mChatManager.getChat(mContact) ;
				if (mChat == null) {
				    mChat = mChatManager.createChat(mContact, mMessageListener);
				    mChat.setOpen(true);
				}
				mChat.sendMessage(msgToSend);
			    final String self = getString(R.string.chat_self);
			    MessageText lastMessage = null;
			    if (mListMessages.size() != 0)
				lastMessage = mListMessages.get(mListMessages.size() - 1);
	
			    if (lastMessage != null && lastMessage.getName().equals(self)) {
			    	lastMessage.setMessage(lastMessage.getMessage().concat("\n" + inputContent));
			    	lastMessage.setTimestamp(new Date());
			    } else {
			    	mListMessages.add(new MessageText(self, self, inputContent, false, false, new Date()));
			    }
	    	}
	    } catch (RemoteException e) {
		Log.e(TAG, e.getMessage());
	    }

	   
	    mMessagesListAdapter.notifyDataSetChanged();
	    mInputField.setText(null);
	}
    }

    /**
     * This class is in charge of getting the new chat in the activity if someone talk to you.
     */
    private class ChatManagerListener extends IChatManagerListener.Stub {

	/**
	 * Constructor.
	 */
	public ChatManagerListener() {
	}

	@Override
	public void chatCreated(IChat chat, boolean locally) {
	    if (locally)
		return;
	    try {
		String contactJid = mContact.getJIDWithRes();
		String chatJid = chat.getParticipant().getJIDWithRes();
		if (chatJid.equals(contactJid)) {
		    // This should not be happened but to be sure
			IChat mChat = mChatManager.getChat(mContact) ;
		    if (mChat != null) {
				mChat.setOpen(false);
				mChat.removeMessageListener(mMessageListener);
		    }
		    mChat = chat;
		    mChat.setOpen(true);
		    mChat.addMessageListener(mMessageListener);
		    mChatManager.deleteChatNotification(mChat);
		}
	    } catch (RemoteException ex) {
		Log.e(TAG, "A remote exception occurs during the creation of a chat", ex);
	    }
	}
    }
}
