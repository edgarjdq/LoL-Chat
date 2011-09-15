/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/robi/projects/android/beam/beam/src/com/rei/lolchat/service/aidl/IChatMUC.aidl
 */
package com.rei.lolchat.service.aidl;
/**
 * An aidl interface for ChatMUC session.
 */
public interface IChatMUC extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.rei.lolchat.service.aidl.IChatMUC
{
private static final java.lang.String DESCRIPTOR = "com.rei.lolchat.service.aidl.IChatMUC";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.rei.lolchat.service.aidl.IChatMUC interface,
 * generating a proxy if needed.
 */
public static com.rei.lolchat.service.aidl.IChatMUC asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.rei.lolchat.service.aidl.IChatMUC))) {
return ((com.rei.lolchat.service.aidl.IChatMUC)iin);
}
return new com.rei.lolchat.service.aidl.IChatMUC.Stub.Proxy(obj);
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
case TRANSACTION_sendMessage:
{
data.enforceInterface(DESCRIPTOR);
com.rei.lolchat.service.Message _arg0;
if ((0!=data.readInt())) {
_arg0 = com.rei.lolchat.service.Message.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.sendMessage(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getRoom:
{
data.enforceInterface(DESCRIPTOR);
com.rei.lolchat.service.Contact _result = this.getRoom();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_addMessageListener:
{
data.enforceInterface(DESCRIPTOR);
com.rei.lolchat.service.aidl.IMessageListener _arg0;
_arg0 = com.rei.lolchat.service.aidl.IMessageListener.Stub.asInterface(data.readStrongBinder());
this.addMessageListener(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_removeMessageListener:
{
data.enforceInterface(DESCRIPTOR);
com.rei.lolchat.service.aidl.IMessageListener _arg0;
_arg0 = com.rei.lolchat.service.aidl.IMessageListener.Stub.asInterface(data.readStrongBinder());
this.removeMessageListener(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getState:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getState();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_setOpen:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setOpen(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_isOpen:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isOpen();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setState:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.setState(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getMessages:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<com.rei.lolchat.service.Message> _result = this.getMessages();
reply.writeNoException();
reply.writeTypedList(_result);
return true;
}
case TRANSACTION_getMembers:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<com.rei.lolchat.service.Contact> _result = this.getMembers();
reply.writeNoException();
reply.writeTypedList(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.rei.lolchat.service.aidl.IChatMUC
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
	 * Send a message.
	 * @param message	the message to send
	 */
public void sendMessage(com.rei.lolchat.service.Message message) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((message!=null)) {
_data.writeInt(1);
message.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_sendMessage, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public com.rei.lolchat.service.Contact getRoom() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.rei.lolchat.service.Contact _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getRoom, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.rei.lolchat.service.Contact.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * Add a message listener.
	 * @param listener the listener to add.
	 */
public void addMessageListener(com.rei.lolchat.service.aidl.IMessageListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_addMessageListener, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * Remove a message listener.
	 * @param listener the listener to remove.
	 */
public void removeMessageListener(com.rei.lolchat.service.aidl.IMessageListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_removeMessageListener, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public java.lang.String getState() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getState, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setOpen(boolean isOpen) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((isOpen)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setOpen, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean isOpen() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isOpen, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setState(java.lang.String state) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(state);
mRemote.transact(Stub.TRANSACTION_setState, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public java.util.List<com.rei.lolchat.service.Message> getMessages() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<com.rei.lolchat.service.Message> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getMessages, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArrayList(com.rei.lolchat.service.Message.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.util.List<com.rei.lolchat.service.Contact> getMembers() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<com.rei.lolchat.service.Contact> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getMembers, _data, _reply, 0);
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
static final int TRANSACTION_sendMessage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getRoom = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_addMessageListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_removeMessageListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_getState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_setOpen = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_isOpen = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_setState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_getMessages = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_getMembers = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
}
/**
	 * Send a message.
	 * @param message	the message to send
	 */
public void sendMessage(com.rei.lolchat.service.Message message) throws android.os.RemoteException;
public com.rei.lolchat.service.Contact getRoom() throws android.os.RemoteException;
/**
	 * Add a message listener.
	 * @param listener the listener to add.
	 */
public void addMessageListener(com.rei.lolchat.service.aidl.IMessageListener listener) throws android.os.RemoteException;
/**
	 * Remove a message listener.
	 * @param listener the listener to remove.
	 */
public void removeMessageListener(com.rei.lolchat.service.aidl.IMessageListener listener) throws android.os.RemoteException;
public java.lang.String getState() throws android.os.RemoteException;
public void setOpen(boolean isOpen) throws android.os.RemoteException;
public boolean isOpen() throws android.os.RemoteException;
public void setState(java.lang.String state) throws android.os.RemoteException;
public java.util.List<com.rei.lolchat.service.Message> getMessages() throws android.os.RemoteException;
public java.util.List<com.rei.lolchat.service.Contact> getMembers() throws android.os.RemoteException;
}
