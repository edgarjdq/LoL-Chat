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
package com.rei.lolchat.service.aidl;
import  com.rei.lolchat.service.Contact;
import  com.rei.lolchat.service.Massage;
import  com.rei.lolchat.service.aidl.IMessageListener;
/**
 * An aidl interface for ChatMUC session.
 */
interface IChatMUC {
	/**
	 * Send a message.
	 * @param message	the message to send
	 */
	void sendMessage(in Massage message);
	
	Contact getRoom() ;
	/**
	 * Add a message listener.
	 * @param listener the listener to add.
	 */
	void addMessageListener(in IMessageListener listener);
	/**
	 * Remove a message listener.
	 * @param listener the listener to remove.
	 */
	void removeMessageListener(in IMessageListener listener);
	String getState();
	void setOpen(in boolean isOpen);
	boolean isOpen();
	void setState(in String state);
	List<Massage> getMessages();
	List<Contact> getMembers();
}