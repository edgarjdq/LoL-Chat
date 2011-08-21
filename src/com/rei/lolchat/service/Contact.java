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

import java.io.IOException;
import java.io.StringReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.rei.lolchat.utils.Status;

/**
 * This class contains informations on a jabber contact.
 * @author darisk
 */
public class Contact implements Parcelable {

    /** Parcelable.Creator needs by Android. */
    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {

	@Override
	public Contact createFromParcel(Parcel source) {
	    return new Contact(source);
	}

	@Override
	public Contact[] newArray(int size) {
	    return new Contact[size];
	}
    };

    private int mID;
    private int mStatus;
    private final String mJID;
    private String mSelectedRes;
    private String mMsgState;
    
    private String mProfileIcon;
    private String mLevel;
    private String mWins;
    private String mLeaves;
    private String mQueueType;
    private String mRankedWins;
    private String mRankedLosses;
    private String mRankedRating;
    private String mStatusMsg;
    private String mSkinname;
    private String mTimeStamp;
    private String mGameStatus;
    
    
    private List<String> mRes;
    private final List<String> mGroups = new ArrayList<String>();
    private String mName;
    private String mAvatarId;
    private boolean mIsMUC;
    /**
     * Construct a contact from a parcel.
     * @param in parcel to use for construction
     */
    private Contact(final Parcel in) {
	mID = in.readInt();
	mStatus = in.readInt();
	mJID = in.readString();
	mSelectedRes = in.readString();
	mName = in.readString();
	mMsgState = in.readString();
	mAvatarId = in.readString();
	mRes = new ArrayList<String>();
	in.readStringList(mRes);
	in.readStringList(mGroups);
    }

    /**
     * Constructor.
     * @param jid JID of the contact
     */
    public Contact(final String jid) {
	mJID = StringUtils.parseBareAddress(jid);
	mName = mJID;
	mStatus = Status.CONTACT_STATUS_DISCONNECT;
	mMsgState = null;
	mRes = new ArrayList<String>();
	String res = StringUtils.parseResource(jid);
	mSelectedRes = res;
	if (!"".equals(res))
	    mRes.add(res);
    }

    /**
     * Create a contact from a Uri.
     * @param uri an uri for the contact
     * @throws IllegalArgumentException if it is not a xmpp uri
     */
    public Contact(final Uri uri) {
	if (!"xmpp".equals(uri.getScheme()))
	    throw new IllegalArgumentException();
	String enduri = uri.getEncodedSchemeSpecificPart();
	String fjid = StringUtils.parseBareAddress(enduri);
	if (fjid.charAt(0) == '$') {
		mJID = fjid.substring(1) ;
		mIsMUC = true ;
	} else {
        mJID = fjid ;
		mIsMUC = false ;
	}
	mName = mJID;
	mStatus = Status.CONTACT_STATUS_DISCONNECT;
	mMsgState = null;
	mLevel = "over 9000";
	mRes = new ArrayList<String>();
	String res = StringUtils.parseResource(enduri);
	mSelectedRes = res;
	mRes.add(res);
    }
    public Contact(final String jid, boolean isMuc) {
    	this(jid) ;
    	this.mIsMUC = true ;
    }
    /**
     * Make an xmpp uri for a spcific jid.
     *
     * @param jid the jid to represent as an uri
     * @return an uri representing this jid.
     */
    public static Uri makeXmppUri(String jid) {
	StringBuilder build = new StringBuilder("xmpp:");
	String name = StringUtils.parseName(jid);
	build.append(name);
	if (!"".equals(name))
	    build.append('@');
	build.append(StringUtils.parseServer(jid));
	String resource = StringUtils.parseResource(jid);
	if (!"".equals(resource)) {
	    build.append('/');
	    build.append(resource);
	}
	Uri u = Uri.parse(build.toString());
	return u;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
	dest.writeInt(mID);
	dest.writeInt(mStatus);
	dest.writeString(mJID);
	dest.writeString(mSelectedRes);
	dest.writeString(mName);
	dest.writeString(mMsgState);
	dest.writeString(mAvatarId);
	dest.writeStringList(getMRes());
	dest.writeStringList(getGroups());
    }

    /**
     * Add a group for the contact.
     * @param group the group
     */
    public void addGroup(String group) {
	if (!mGroups.contains(group))
	    mGroups.add(group);
    }

    /**
     * Remove the contact from a group.
     * @param group the group to delete the contact from.
     */
    public void delGroup(String group) {
	mGroups.remove(group);
    }

    /**
     * Add a resource for this contact.
     * @param res the resource to add
     */
    public void addRes(String res) {
	if (!mRes.contains(res))
	    mRes.add(res);
    }

    /**
     * Delete a resource for this contact.
     * @param res the resource de delete
     */
    public void delRes(String res) {
	mRes.remove(res);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int describeContents() {
	return 0;
    }

    /**
     * Get the groups the contact is in.
     * @return the mGroups
     */
    public List<String> getGroups() {
	return mGroups;
    }

    /**
     * Get the id of the contact on the phone contact list.
     * @return the mID
     */
    public int getID() {
	return mID;
    }

    /**
     * Get the Jabber ID of the contact.
     * @return the Jabber ID
     */
    public String getJID() {
	return mJID;
    }

    /**
     * Get selected resource.
     * @return the selected resource.
     */
    public String getSelectedRes() {
	return mSelectedRes;
    }

    /**
     * Get the list of resource for the contact.
     * @return the mRes
     */
    public List<String> getMRes() {
	return mRes;
    }

    /**
     * Get the message status of the contact.
     * @return the message status of the contact.
     */
    public String getMsgState() {
	return mMsgState;
    }
    /**
     * Get the Level of the contact.
     * @return the Level of the contact.
     */
    public String getLevel() {
	return mLevel;
    }
    public String getLeaves() {
    	return mLeaves;
        }
    public String getRankedWins() {
    	return mRankedWins;
        }
    public String getRankedLosses() {
    	return mRankedLosses;
        }
    public String getRankedRating() {
    	return mRankedRating;
        }
    public String getSkinname() {
    	return mSkinname;
        }
    public Timestamp getTimeStamp() {
    	long unixTime = Long.parseLong(mTimeStamp);  
    	long timestamp = unixTime; 
    	Timestamp d = new Timestamp(timestamp);  
    	return d;
        }
    public String getWins() {
    	return mWins;
        }
    public String getDuration(){
    	String diff = "";
		Date t1 = (Date) getTimeStamp();
		Date t2 = (Date) new Timestamp(System.currentTimeMillis());
		
		long timeDiff = Math.abs(t1.getTime() - t2.getTime());
		long difference = TimeUnit.MILLISECONDS.toSeconds(timeDiff) / 60;
		String plural = "";
		if(difference > 2)
			plural = "s";
		diff = String.format("%d min"+plural, difference );
		return diff;
    }
    
    public String getMsg() {
    	return mStatusMsg;
    }
    public String getGameStatus() {
    	return mGameStatus;
    }
    /**
     * Get the name of the contact.
     * @return the mName
     */
    public String getName() {
	return mName;
    }

    /**
     * Get the status of the contact.
     * @return the mStatus
     */
    public int getStatus() {
	return mStatus;
    }
    /**
     * Return whether the contact is a MUC room or not
     */
    public boolean isMUC() {
    	return mIsMUC ;
    }
    /**
     * Get the avatar id of the contact.
     *
     * @return the avatar id or null if there is not
     */
    public String getAvatarId() {
	return mProfileIcon;
    }

    /**
     * Set the groups the contact is in.
     * @param groups list of groups
     */
    public void setGroups(Collection<RosterGroup> groups) {
	this.mGroups.clear();
	for (RosterGroup rosterGroup : groups) {
	    mGroups.add(rosterGroup.getName());
	}
    }

    /**
     * Set the groups the contact is in.
     * @param groups the mGroups to set
     */
    public void setGroups(List<String> groups) {
	mGroups.clear();
	mGroups.addAll(groups);
    }

    /**
     * set the id of te contact on the phone contact list.
     * @param mid the mID to set
     */
    public void setID(int mid) {
	mID = mid;
    }

    /**
     * Set the avatar id of the contact.
     *
     * @param avatarId the avatar id
     */
    public void setAvatarId(String avatarId) {
	mAvatarId = avatarId;
    }

    /**
     * Set the resource of the contact.
     * @param resource to set.
     */
    public void setSelectedRes(String resource) {
	mSelectedRes = resource;
    }

    /**
     * Set a list of resource for the contact.
     * @param mRes the mRes to set
     */
    public void setMRes(List<String> mRes) {
	this.mRes = mRes;
    }

    /**
     * Set the message status of the contact.
     * @param msgState the message status of the contact to set
     */
    public void setMsgState(String msgState) {
	mMsgState = msgState;
    }

    /**
     * Set the name of the contact.
     * @param name the mName to set
     */
    public void setName(String name) {
	if (name == null || "".equals(name)) {
	    this.mName = this.mJID;
	    this.mName = StringUtils.parseName(this.mName);
	    if (this.mName == null || "".equals(this.mName))
		this.mName = this.mJID;
	} else {
	    this.mName = name;
	}
    }

    /**
     * Set the status of the contact.
     * @param status the mStatus to set
     */
    public void setStatus(int status) {
	mStatus = status;
    }
    private static String getTagValue(String tag, Document doc) {
        Element firstElementWithName = (Element) doc.getElementsByTagName(tag).item(0);
        String value = "";
        if(firstElementWithName != null){
	        Node firstChild = firstElementWithName.getFirstChild() ;
	        if(firstChild == null) return "";
	        value = firstChild.getNodeValue() ;
        }
        return value ;
    }
    public void setGameStatus(String msgState){
    	if(!msgState.equals("Disconnected") && !msgState.equals("")){
    		DocumentBuilder db = null;
    		try {
    			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    		} catch (ParserConfigurationException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		} catch (FactoryConfigurationError e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}
    	    InputSource is = new InputSource();
    	    is.setCharacterStream(new StringReader(msgState));
    	    
    	    Document doc = null;
    		try {
    			doc = db.parse(is);
    		} catch (SAXException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		
    	    
    		mProfileIcon = getTagValue("profileIcon", doc);	
    		mLevel = getTagValue("level", doc);	
    		mWins = getTagValue("wins", doc);	
    		mLeaves = getTagValue("leaves", doc);	
    		mQueueType = getTagValue("queueType", doc);	
    		mRankedWins = getTagValue("rankedWins", doc);	
    		mRankedLosses = getTagValue("rankedLosses", doc);	
    		mRankedRating = getTagValue("rankedRating", doc);	
    		mStatusMsg = getTagValue("statusMsg", doc);	
    		mSkinname = getTagValue("skinname", doc);
    		mTimeStamp = getTagValue("timeStamp", doc);
    		mGameStatus = getTagValue("gameStatus", doc);
    	}
    }
    /**
     * Set the status of the contact using a presence packet.
     * @param presence the presence containing status
     */
    public void setStatus(Presence presence) {
	mStatus = Status.getStatusFromPresence(presence);
	mMsgState = presence.getStatus();
	setGameStatus(mMsgState);
	
    }

    /**
     * Set status for the contact.
     * @param presence The presence packet which contains the status
     */
    public void setStatus(PresenceAdapter presence) {
	mStatus = presence.getStatus();
	mMsgState = presence.getStatusText();
	setGameStatus(mMsgState);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
	if (mJID != null)
	    return mJID + "/[" + mRes + "]";
	return super.toString();
    }

    /**
     * Get a URI to access the contact.
     * @return the URI
     */
    public Uri toUri() {
	return makeXmppUri(mJID);
    }

    /**
     * Get a URI to access the specific contact on this resource.
     * @param resource the resource of the contact
     * @return the URI
     */
    public Uri toUri(String resource) {
	StringBuilder build = new StringBuilder("xmpp:");
	if (this.isMUC()) {
		build.append("$") ;
	}
	String name = StringUtils.parseName(mJID);
	build.append(name);
	if (!"".equals(name))
	    build.append('@');
	build.append(StringUtils.parseServer(mJID));
	if (!"".equals(resource)) {
	    build.append('/');
	    build.append(resource);
	}
	Uri u = Uri.parse(build.toString());
	return u;
    }

    /**
     * Get a JID to access the specific contact on this resource.
     * @return the JID.
     */
    public String getJIDWithRes() {
	StringBuilder build = new StringBuilder(mJID);
	if (!"".equals(mSelectedRes))
	    build.append('/').append(mSelectedRes);
	return build.toString();
    }

    @Override
    public boolean equals(Object other) {
	if (!(other instanceof Contact))
	    return false;
	if (other == this)
	    return true;
	Contact c = (Contact) other;
	return c.getJID().equals(getJID());
    }

    @Override
    public int hashCode() {
	return mJID.hashCode();
    }

}
