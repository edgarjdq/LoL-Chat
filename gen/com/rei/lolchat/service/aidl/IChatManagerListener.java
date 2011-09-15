/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/robi/projects/android/beam/beam/src/com/rei/lolchat/service/aidl/IChatManagerListener.aidl
 */
package com.rei.lolchat.service.aidl;
/**
 * Aidl interface for ChatManager listener.
 * This listener will execute on events like creation of chat session.
 */
public interface IChatManagerListener extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.rei.lolchat.service.aidl.IChatManagerListener
{
private static final java.lang.String DESCRIPTOR = "com.rei.lolchat.service.aidl.IChatManagerListener";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.rei.lolchat.service.aidl.IChatManagerListener interface,
 * generating a proxy if needed.
 */
public static com.rei.lolchat.service.aidl.IChatManagerListener asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.rei.lolchat.service.aidl.IChatManagerListener))) {
return ((com.rei.lolchat.service.aidl.IChatManagerListener)iin);
}
return new com.rei.lolchat.service.aidl.IChatManagerListener.Stub.Proxy(obj);
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
case TRANSACTION_chatCreated:
{
data.enforceInterface(DESCRIPTOR);
com.rei.lolchat.service.aidl.IChat _arg0;
_arg0 = com.rei.lolchat.service.aidl.IChat.Stub.asInterface(data.readStrongBinder());
boolean _arg1;
_arg1 = (0!=data.readInt());
this.chatCreated(_arg0, _arg1);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.rei.lolchat.service.aidl.IChatManagerListener
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
    	 * Call when a new chat session is created.
    	 * @param chat		the created chat session
    	 * @param locally	true if the session is create by a chat manager.
    	 */
public void chatCreated(com.rei.lolchat.service.aidl.IChat chat, boolean locally) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((chat!=null))?(chat.asBinder()):(null)));
_data.writeInt(((locally)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_chatCreated, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_chatCreated = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
/**
    	 * Call when a new chat session is created.
    	 * @param chat		the created chat session
    	 * @param locally	true if the session is create by a chat manager.
    	 */
public void chatCreated(com.rei.lolchat.service.aidl.IChat chat, boolean locally) throws android.os.RemoteException;
}
