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

import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smackx.packet.DelayInformation;
import org.jivesoftware.smack.packet.PacketExtension;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.rei.lolchat.ui.Chat;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

/**
 * This class represents a instant message.
 * @author darisk
 */
public class Message implements Parcelable {

    /** Normal message type. Theese messages are like an email, with subject. */
    public static final int MSG_TYPE_NORMAL = 100;

    /** Chat message type. */
    public static final int MSG_TYPE_CHAT = 200;

    /** Group chat message type. */
    public static final int MSG_TYPE_GROUP_CHAT = 300;

    /** Error message type. */
    public static final int MSG_TYPE_ERROR = 400;

    /** Parcelable.Creator needs by Android. */
    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {

	@Override
	public Message createFromParcel(Parcel source) {
	    return new Message(source);
	}

	@Override
	public Message[] newArray(int size) {
	    return new Message[size];
	}
    };

    private int mType;
    private String mBody;
    private String mSubject;
    private String mTo;
    private String mFrom;
    private String mThread;
    private Date mTimestamp;
    private boolean mHL ;
    
    //some lol specific stuff
    private boolean isInvite;
    private String mBodyXml;
    private String inviteId;
    private String userName;
    private String profileIconId;
    private String gameType;
    private String mapId;
    private String queueId;
    private String gameDifficulty;
    private Context context ;
    
    /**
     * Constructor.
     * @param to the destinataire of the message
     * @param type the message type
     */
    public Message(final String to, final int type) {
	mTo = to;
	mType = type;
	mBody = "";
	mSubject = "";
	mThread = "";
	mFrom = null;
	mTimestamp = new Date();
	mHL = false;
    }

    /**
     * Constructor a message of type chat.
     * @param to the destinataire of the message
     */
    public Message(final String to) {
	this(to, MSG_TYPE_CHAT);
    }

    /**
     * Construct a message from a smack message packet.
     * @param smackMsg Smack message packet
     */
    public Message(final org.jivesoftware.smack.packet.Message smackMsg) {
		this(smackMsg.getTo());
		switch (smackMsg.getType()) {
		    case chat:
			mType = MSG_TYPE_CHAT;
			break;
		    case groupchat:
			mType = MSG_TYPE_GROUP_CHAT;
			break;
		    case normal:
			mType = MSG_TYPE_NORMAL;
			break;
		    case error:
			mType = MSG_TYPE_ERROR;
			break;
		    default:
			mType = MSG_TYPE_NORMAL;
			break;
		}
		this.mFrom = smackMsg.getFrom();
		mHL = false;
		//TODO better handling of error messages
		if (mType == MSG_TYPE_ERROR) {
		    XMPPError er = smackMsg.getError();
		    String msg = er.getMessage();
		    if (msg != null)
			mBody = msg;
		    else
			mBody = er.getCondition();
		} else {
		    mBody = smackMsg.getBody();
		    mSubject = smackMsg.getSubject();
		    mThread = smackMsg.getThread();
		    /*
		     * TODO: Figure out why this blocks all incomming messages from being displayed...
		     * 
			This should work, but it doesnt. very strange...
			
			if(mSubject.equals("GAME_INVITE") || mSubject.equals("GAME_INVITE_ACK")){
			
			*/
		   
		    //if there are more than 10 > in this string, its probably xml...
		    int charCount = mBody.replaceAll("[^>]", "").length();
			if(charCount > 10){
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
			    is.setCharacterStream(new StringReader(mBody));
			    
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
				
				isInvite = true;
				//TODO: isInviteAck = true;
				inviteId = getTagValue("inviteId", doc);	
				userName = getTagValue("userName", doc);	
				profileIconId = getTagValue("profileIconId", doc);	
				gameType = getTagValue("gameType", doc);	
				mapId = getTagValue("mapId", doc);	
				queueId = getTagValue("queueId", doc);	
				gameDifficulty = getTagValue("gameDifficulty", doc);
				mBodyXml = mBody;	
				
				mBody = mSubject+": "+gameType + " " + gameDifficulty;
				
			}
		}
		PacketExtension pTime = smackMsg.getExtension("delay", "urn:xmpp:delay");
		if (pTime instanceof DelayInformation) {
		    mTimestamp = ((DelayInformation) pTime).getStamp();
		} else {
		    mTimestamp = new Date();
		}
	    }
	
	    /**
	     * Construct a message from a parcel.
	     * @param in parcel to use for construction
	     */
	    private Message(final Parcel in) {
		mType = in.readInt();
		mTo = in.readString();
		mBody = in.readString();
		mSubject = in.readString();
		mThread = in.readString();
		mFrom = in.readString();
		mTimestamp = new Date(in.readLong());
		mHL = false;
		isInvite = false;
		inviteId = "";
	    userName = "";
	    profileIconId = "";
	    gameType = "";
	    mapId = "";
	    queueId = "";
	    gameDifficulty = "";
	
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
    /**
     * {@inheritDoc}
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
	// TODO Auto-generated method stub
	dest.writeInt(mType);
	dest.writeString(mTo);
	dest.writeString(mBody);
	dest.writeString(mSubject);
	dest.writeString(mThread);
	dest.writeString(mFrom);
	dest.writeLong(mTimestamp.getTime());
    }

    /**
     * Get the type of the message.
     * @return the type of the message.
     */
    public int getType() {
	return mType;
    }

    /**
     * Set the type of the message.
     * @param type the type to set
     */
    public void setType(int type) {
	mType = type;
    }

    /**
     * Get the body of the message.
     * @return the Body of the message
     */
    public String getBody() {
    	return mBody;
    }
    public String getBodyXml(){
    	return mBodyXml;
    }
    public boolean isInvite() {
    	return isInvite;
    }

    public String getgameType() {
    	return gameType;
    }
    public String getinviteId() {
    	return inviteId;
    }
    public String getgameDifficulty() {
    	return gameDifficulty;
    }
    /**
     * Set the body of the message.
     * @param body the body to set
     */
    public void setBody(String body) {
	mBody = body;
    }

    /**
     * Get the subject of the message.
     * @return the subject
     */
    public String getSubject() {
	return mSubject;
    }

    /**
     * Set the subject of the message.
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
	mSubject = subject;
    }

    /**
     * Get the destinataire of the message.
     * @return the destinataire of the message
     */
    public String getTo() {
	return mTo;
    }

    /**
     * Set the destinataire of the message.
     * @param to the destinataire to set
     */
    public void setTo(String to) {
	mTo = to;
    }

    /**
     * Set the from field of the message.
     * @param from the mFrom to set
     */
    public void setFrom(String from) {
	this.mFrom = from;
    }

    /**
     * Get the from field of the message.
     * @return the mFrom
     */
    public String getFrom() {
	return mFrom;
    }

    /**
     * Get the thread of the message.
     * @return the thread
     */
    public String getThread() {
	return mThread;
    }

    /**
     * Set the thread of the message.
     * @param thread the thread to set
     */
    public void setThread(String thread) {
	mThread = thread;
    }

    /**
     * Set the Date of the message.
     *
     * @param date date of the message.
     */
    public void setTimestamp(Date date) {
	mTimestamp = date;
    }

    /**
     * Get the Date of the message.
     *
     * @return if it is a delayed message get the date the message was sended.
     */
    public Date getTimestamp() {
	return mTimestamp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int describeContents() {
	// TODO Auto-generated method stub
	return 0;
    }
    public void setHL(boolean mHL) {
		this.mHL = mHL;
	}
	public boolean isHL() {
		return mHL;
	}
}
