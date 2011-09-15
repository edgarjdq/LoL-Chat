/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/robi/projects/android/beam/beam/src/com/rei/lolchat/service/aidl/IChatManager.aidl
 */
package com.rei.lolchat.service.aidl;
/**
 * Aidl interface for a chat manager.
 * The chat manager will manage all the chat sessions.
 */
public interface IChatManager extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.rei.lolchat.service.aidl.IChatManager
{
private static final java.lang.String DESCRIPTOR = "com.rei.lolchat.service.aidl.IChatManager";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.rei.lolchat.service.aidl.IChatManager interface,
 * generating a proxy if needed.
 */
public static com.rei.lolchat.service.aidl.IChatManager asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.rei.lolchat.service.aidl.IChatManager))) {
return ((com.rei.lolchat.service.aidl.IChatManager)iin);
}
return new com.rei.lolchat.service.aidl.IChatManager.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_createChat:
{
data.enforceInterface(DESCRIPTOR);
com.rei.lolchat.service.Contact _arg0;
if ((0!=data.readInt())) {
_arg0 = com.rei.lolchat.service.Contact.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
com.rei.lolchat.service.aidl.IMessageListener _arg1;
_arg1 = com.rei.lolchat.service.aidl.IMessageListener.Stub.asInterface(data.readStrongBinder());
com.rei.lolchat.service.aidl.IChat _result = this.createChat(_arg0, _arg1);
reply.writeNoException();
reply.writeStrongBinder((((_result!=null))?(_result.asBinder()):(null)));
return true;
}
case TRANSACTION_createMUCChat:
{
data.enforceInterface(DESCRIPTOR);
com.rei.lolchat.service.Contact _arg0;
if ((0!=data.readInt())) {
_arg0 = com.rei.lolchat.service.Contact.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
com.rei.lolchat.service.aidl.IMessageListener _arg1;
_arg1 = com.rei.lolchat.service.aidl.IMessageListener.Stub.asInterface(data.readStrongBinder());
com.rei.lolchat.service.aidl.IChatMUC _result = this.createMUCChat(_arg0, _arg1);
reply.writeNoException();
reply.writeStrongBinder((((_result!=null))?(_result.asBinder()):(null)));
return true;
}
case TRANSACTION_getChat:
{
data.enforceInterface(DESCRIPTOR);
com.rei.lolchat.service.Contact _arg0;
if ((0!=data.readInt())) {
_arg0 = com.rei.lolchat.service.Contact.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
com.rei.lolchat.service.aidl.IChat _result = this.getChat(_arg0);
reply.writeNoException();
reply.writeStrongBinder((((_result!=null))?(_result.asBinder()):(null)));
return true;
}
case TRANSACTION_getMUCChat:
{
data.enforceInterface(DESCRIPTOR);
com.rei.lolchat.service.Contact _arg0;
if ((0!=data.readInt())) {
_arg0 = com.rei.lolchat.service.Contact.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
com.rei.lolchat.service.aidl.IChatMUC _result = this.getMUCChat(_arg0);
reply.writeNoException();
reply.writeStrongBinder((((_result!=null))?(_result.asBinder()):(null)));
return true;
}
case TRANSACTION_destroyChat:
{
data.enforceInterface(DESCRIPTOR);
com.rei.lolchat.service.aidl.IChat _arg0;
_arg0 = com.rei.lolchat.service.aidl.IChat.Stub.asInterface(data.readStrongBinder());
this.destroyChat(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_destroyMUCChat:
{
data.enforceInterface(DESCRIPTOR);
com.rei.lolchat.service.aidl.IChatMUC _arg0;
_arg0 = com.rei.lolchat.service.aidl.IChatMUC.Stub.asInterface(data.readStrongBinder());
this.destroyMUCChat(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_deleteChatNotification:
{
data.enforceInterface(DESCRIPTOR);
com.rei.lolchat.service.aidl.IChat _arg0;
_arg0 = com.rei.lolchat.service.aidl.IChat.Stub.asInterface(data.readStrongBinder());
this.deleteChatNotification(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_addChatCreationListener:
{
data.enforceInterface(DESCRIPTOR);
com.rei.lolchat.service.aidl.IChatManagerListener _arg0;
_arg0 = com.rei.lolchat.service.aidl.IChatManagerListener.Stub.asInterface(data.readStrongBinder());
this.addChatCreationListener(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_removeChatCreationListener:
{
data.enforceInterface(DESCRIPTOR);
com.rei.lolchat.service.aidl.IChatManagerListener _arg0;
_arg0 = com.rei.lolchat.service.aidl.IChatManagerListener.Stub.asInterface(data.readStrongBinder());
this.removeChatCreationListener(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getOpenedChatList:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<com.rei.lolchat.service.Contact> _result = this.getOpenedChatList();
reply.writeNoException();
reply.writeTypedList(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.rei.lolchat.service.aidl.IChatManager
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
/**
    	 * Create a chat session with a contact.
    	 * @param contact	the contact to chat with
    	 * @param listener	the callback to call when a new message comes from this chat session
    	 * @return 		the chat session
    	 */
public com.rei.lolchat.service.aidl.IChat createChat(com.rei.lolchat.service.Contact contact, com.rei.lolchat.service.aidl.IMessageListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.rei.lolchat.service.aidl.IChat _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((contact!=null)) {
_data.writeInt(1);
contact.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_createChat, _data, _reply, 0);
_reply.readException();
_result = com.rei.lolchat.service.aidl.IChat.Stub.asInterface(_reply.readStrongBinder());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public com.rei.lolchat.service.aidl.IChatMUC createMUCChat(com.rei.lolchat.service.Contact contact, com.rei.lolchat.service.aidl.IMessageListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.rei.lolchat.service.aidl.IChatMUC _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((contact!=null)) {
_data.writeInt(1);
contact.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_createMUCChat, _data, _reply, 0);
_reply.readException();
_result = com.rei.lolchat.service.aidl.IChatMUC.Stub.asInterface(_reply.readStrongBinder());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * Get an existing Chat session with a contact.
	 * @return null if the chat session does not exist.
	 */
public com.rei.lolchat.service.aidl.IChat getChat(com.rei.lolchat.service.Contact contact) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.rei.lolchat.service.aidl.IChat _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((contact!=null)) {
_data.writeInt(1);
contact.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_getChat, _data, _reply, 0);
_reply.readException();
_result = com.rei.lolchat.service.aidl.IChat.Stub.asInterface(_reply.readStrongBinder());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public com.rei.lolchat.service.aidl.IChatMUC getMUCChat(com.rei.lolchat.service.Contact contact) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.rei.lolchat.service.aidl.IChatMUC _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((contact!=null)) {
_data.writeInt(1);
contact.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_getMUCChat, _data, _reply, 0);
_reply.readException();
_result = com.rei.lolchat.service.aidl.IChatMUC.Stub.asInterface(_reply.readStrongBinder());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
    	 * Destroy a chat session with a contact.
    	 * @param chat	the chat session
    	 */
public void destroyChat(com.rei.lolchat.service.aidl.IChat chat) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((chat!=null))?(chat.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_destroyChat, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void destroyMUCChat(com.rei.lolchat.service.aidl.IChatMUC chat) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((chat!=null))?(chat.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_destroyMUCChat, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * @param chat the chat.
         */
public void deleteChatNotification(com.rei.lolchat.service.aidl.IChat chat) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((chat!=null))?(chat.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_deleteChatNotification, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * Register a callback to call when a new chat session is created.
	 * @param listener	the callback to add
	 */
public void addChatCreationListener(com.rei.lolchat.service.aidl.IChatManagerListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_addChatCreationListener, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * Remove a callback for the creation of new chat session.
	 * @param listener	the callback to remove.
	 */
public void removeChatCreationListener(com.rei.lolchat.service.aidl.IChatManagerListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_removeChatCreationListener, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * Get a list of contact which we are currently chatting.
	 * @return list of contact.
	 */
public java.util.List<com.rei.lolchat.service.Contact> getOpenedChatList() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<com.rei.lolchat.service.Contact> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getOpenedChatList, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArrayList(com.rei.lolchat.service.Contact.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_createChat = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_createMUCChat = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_getChat = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_getMUCChat = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_destroyChat = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_destroyMUCChat = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_deleteChatNotification = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_addChatCreationListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_removeChatCreationListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_getOpenedChatList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
}
/**
    	 * Create a chat session with a contact.
    	 * @param contact	the contact to chat with
    	 * @param listener	the callback to call when a new message comes from this chat session
    	 * @return 		the chat session
    	 */
public com.rei.lolchat.service.aidl.IChat createChat(com.rei.lolchat.service.Contact contact, com.rei.lolchat.service.aidl.IMessageListener listener) throws android.os.RemoteException;
public com.rei.lolchat.service.aidl.IChatMUC createMUCChat(com.rei.lolchat.service.Contact contact, com.rei.lolchat.service.aidl.IMessageListener listener) throws android.os.RemoteException;
/**
	 * Get an existing Chat session with a contact.
	 * @return null if the chat session does not exist.
	 */
public com.rei.lolchat.service.aidl.IChat getChat(com.rei.lolchat.service.Contact contact) throws android.os.RemoteException;
public com.rei.lolchat.service.aidl.IChatMUC getMUCChat(com.rei.lolchat.service.Contact contact) throws android.os.RemoteException;
/**
    	 * Destroy a chat session with a contact.
    	 * @param chat	the chat session
    	 */
public void destroyChat(com.rei.lolchat.service.aidl.IChat chat) throws android.os.RemoteException;
public void destroyMUCChat(com.rei.lolchat.service.aidl.IChatMUC chat) throws android.os.RemoteException;
/**
	 * @param chat the chat.
         */
public void deleteChatNotification(com.rei.lolchat.service.aidl.IChat chat) throws android.os.RemoteException;
/**
	 * Register a callback to call when a new chat session is created.
	 * @param listener	the callback to add
	 */
public void addChatCreationListener(com.rei.lolchat.service.aidl.IChatManagerListener listener) throws android.os.RemoteException;
/**
	 * Remove a callback for the creation of new chat session.
	 * @param listener	the callback to remove.
	 */
public void removeChatCreationListener(com.rei.lolchat.service.aidl.IChatManagerListener listener) throws android.os.RemoteException;
/**
	 * Get a list of contact which we are currently chatting.
	 * @return list of contact.
	 */
public java.util.List<com.rei.lolchat.service.Contact> getOpenedChatList() throws android.os.RemoteException;
}
