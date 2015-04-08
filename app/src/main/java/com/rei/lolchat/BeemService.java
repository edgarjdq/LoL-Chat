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
package com.rei.lolchat;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.provider.DelayInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smack.proxy.ProxyInfo;
import org.jivesoftware.smack.proxy.ProxyInfo.ProxyType;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.pubsub.provider.PubSubProvider;
import org.jivesoftware.smackx.pubsub.provider.ItemsProvider;
import org.jivesoftware.smackx.pubsub.provider.ItemProvider;
import org.jivesoftware.smackx.pubsub.provider.EventProvider;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import com.rei.lolchat.service.XmppConnectionAdapter;
import com.rei.lolchat.service.XmppFacade;
import com.rei.lolchat.service.aidl.IXmppFacade;
import com.rei.lolchat.utils.BeemBroadcastReceiver;
import com.rei.lolchat.utils.BeemConnectivity;
import com.rei.lolchat.utils.Status;
import com.rei.lolchat.smack.avatar.AvatarMetadataProvider;
import com.rei.lolchat.smack.avatar.AvatarProvider;
import com.rei.lolchat.smack.caps.CapsProvider;

/**
 * This class is for the Beem service.
 * It must contains every global informations needed to maintain the background service.
 * The connection to the xmpp server will be made asynchronously when the service
 * will start.
 * @author darisk
 */
public class BeemService extends Service {

    /** The id to use for status notification. */
    public static final int NOTIFICATION_STATUS_ID = 100;

    private static final String TAG = "BeemService";
    private static final int DEFAULT_XMPP_PORT = 5222;
    //private static final String COMMAND_NAMESPACE = "http://jabber.org/protocol/commands";

    private NotificationManager mNotificationManager;
    private XmppConnectionAdapter mConnection;
    private SharedPreferences mSettings;
    private String mLogin;
    private String mPassword;
    private String mHost;
    private String mService;
    private int mPort;
    private ConnectionConfiguration mConnectionConfiguration;
    private ProxyInfo mProxyInfo;
    private boolean mUseProxy;
    private IXmppFacade.Stub mBind;

    private BeemBroadcastReceiver mReceiver = new BeemBroadcastReceiver();
    private BeemServiceBroadcastReceiver mOnOffReceiver = new BeemServiceBroadcastReceiver();
    private BeemServicePreferenceListener mPreferenceListener = new BeemServicePreferenceListener();

    private boolean mOnOffReceiverIsRegistered;

    /**
     * Constructor.
     */
    public BeemService() {
    }

    /**
     * Initialize the connection.
     */
    private void initConnectionConfig() {
    SmackConfiguration.setPacketReplyTimeout(20000);
    SASLAuthentication.supportSASLMechanism("PLAIN");
	mUseProxy = mSettings.getBoolean(BeemApplication.PROXY_USE_KEY, false);
	if (mUseProxy) {
	    String stype = mSettings.getString(BeemApplication.PROXY_TYPE_KEY, "HTTP");
	    String phost = mSettings.getString(BeemApplication.PROXY_SERVER_KEY, "");
	    String puser = mSettings.getString(BeemApplication.PROXY_USERNAME_KEY, "");
	    String ppass = mSettings.getString(BeemApplication.PROXY_PASSWORD_KEY, "");
	    int pport = Integer.parseInt(mSettings.getString(BeemApplication.PROXY_PORT_KEY, "1080"));
	    ProxyInfo.ProxyType type = ProxyType.valueOf(stype);
	    mProxyInfo = new ProxyInfo(type, phost, pport, puser, ppass);
	} else {
	    mProxyInfo = ProxyInfo.forNoProxy();
	}
	if (mSettings.getBoolean("settings_key_specific_server", false)){
		mConnectionConfiguration = new ConnectionConfiguration(mHost, mPort, mService, mProxyInfo);
	    
	}else
	    mConnectionConfiguration = new ConnectionConfiguration(mService, mProxyInfo);

	mConnectionConfiguration.setSelfSignedCertificateEnabled(true);
	mConnectionConfiguration.setVerifyChainEnabled(false);
	
	TrustManager[] trustManagers = null;
	if (trustManagers == null) {
        trustManagers = new TrustManager[] { new FakeX509TrustManager() };
    }
    SSLContext context = null;
	try {
        context = SSLContext.getInstance("TLS");
        context.init(null, trustManagers, new SecureRandom());
    } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
    } catch (KeyManagementException e) {
        e.printStackTrace();
    }
	mConnectionConfiguration.setSocketFactory(context.getSocketFactory());
	
	if (mSettings.getBoolean(BeemApplication.SMACK_DEBUG_KEY, false))
	    mConnectionConfiguration.setDebuggerEnabled(true);
	mConnectionConfiguration.setSendPresence(true);
	// maybe not the universal path, but it works on most devices (Samsung Galaxy, Google Nexus One)
	mConnectionConfiguration.setTruststoreType("BKS");
	mConnectionConfiguration.setTruststorePath("/system/etc/security/cacerts.bks");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBinder onBind(Intent intent) {
	Log.d(TAG, "ONBIND()");
	return mBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
	Log.d(TAG, "ONUNBIND()");
	if (!mConnection.getAdaptee().isConnected()) {
	    this.stopSelf();
	}
	return true;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate() {
		super.onCreate();
		registerReceiver(mReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		mSettings = PreferenceManager.getDefaultSharedPreferences(this);
		mSettings.registerOnSharedPreferenceChangeListener(mPreferenceListener);
		if (mSettings.getBoolean(BeemApplication.USE_AUTO_AWAY_KEY, false)) {
		    mOnOffReceiverIsRegistered = true;
		    registerReceiver(mOnOffReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		    registerReceiver(mOnOffReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
		}
		String tmpJid = mSettings.getString(BeemApplication.ACCOUNT_USERNAME_KEY, "").trim() + "@pvp.net";
		mLogin = StringUtils.parseName(tmpJid);
		mPassword = "AIR_"+mSettings.getString(BeemApplication.ACCOUNT_PASSWORD_KEY, "");
		mPort = DEFAULT_XMPP_PORT;
		mService = StringUtils.parseServer(tmpJid);
		mHost = mService;
	
		if (mSettings.getBoolean("settings_key_specific_server", false)) {
		    mHost = mSettings.getString("settings_key_xmpp_server", "").trim();
		    if ("".equals(mHost))
			mHost = mService;
		    String tmpPort = mSettings.getString("settings_key_xmpp_port", "5223");
		    if (!"".equals(tmpPort))
			mPort = Integer.parseInt(tmpPort);
		}
		if (mSettings.getBoolean(BeemApplication.FULL_JID_LOGIN_KEY, false) ||
		    "gmail.com".equals(mService) || "googlemail.com".equals(mService))  {
		    mLogin = tmpJid;
		}
	
		initConnectionConfig();
		configure(ProviderManager.getInstance());
	
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mConnection = new XmppConnectionAdapter(mConnectionConfiguration, mLogin, mPassword, this);
	
		Roster.setDefaultSubscriptionMode(SubscriptionMode.manual);
		mBind = new XmppFacade(mConnection);
		Log.d(TAG, "Create BeemService");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
	super.onDestroy();
	mNotificationManager.cancelAll();
	unregisterReceiver(mReceiver);
	mSettings.unregisterOnSharedPreferenceChangeListener(mPreferenceListener);
	if (mOnOffReceiverIsRegistered)
	    unregisterReceiver(mOnOffReceiver);
	if (mConnection.isAuthentificated() && BeemConnectivity.isConnected(this))
	    mConnection.disconnect();
	Log.i(TAG, "Stopping the service");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStart(Intent intent, int startId) {
	super.onStart(intent, startId);
	Log.d(TAG, "onStart");
	try {
	    mConnection.connectAsync();
	} catch (RemoteException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Show a notification using the preference of the user.
     * @param id the id of the notification.
     * @param notif the notification to show
     */
    public void sendNotification(int id, Notification notif) {
	if (mSettings.getBoolean(BeemApplication.NOTIFICATION_VIBRATE_KEY, true))
	    notif.defaults |= Notification.DEFAULT_VIBRATE;
	notif.defaults |= Notification.DEFAULT_LIGHTS;
	String ringtoneStr = mSettings.getString(BeemApplication.NOTIFICATION_SOUND_KEY, Settings.System.DEFAULT_NOTIFICATION_URI.toString());
	notif.sound = Uri.parse(ringtoneStr);
	mNotificationManager.notify(id, notif);
    }

    /**
     * Delete a notification.
     * @param id the id of the notification
     */
    public void deleteNotification(int id) {
	mNotificationManager.cancel(id);
    }

    /**
     * Reset the status to online after a disconnect.
     */
    public void resetStatus() {
	Editor edit = mSettings.edit();
	edit.putInt(BeemApplication.STATUS_KEY, 1);
	edit.commit();
    }

    /**
     * Initialize Jingle from an XmppConnectionAdapter.
     * @param adaptee XmppConnection used for jingle.
     */
    public void initJingle(XMPPConnection adaptee) {
    }

    /**
     * Return a bind to an XmppFacade instance.
     * @return IXmppFacade a bind to an XmppFacade instance
     */
    public IXmppFacade getBind() {
    	return mBind;
    }
    public XmppConnectionAdapter getmConnection() {
    	return mConnection;
 	}
    /**
     * Get the preference of the service.
     * @return the preference
     */
    public SharedPreferences getServicePreference() {
    	return mSettings;
    }

    /**
     * Get the notification manager system service.
     *
     * @return the notification manager service.
     */
    public NotificationManager getNotificationManager() {
	return mNotificationManager;
    }

    /**
     * A sort of patch from this thread: http://www.igniterealtime.org/community/thread/31118. Avoid ClassCastException
     * by bypassing the classloading shit of Smack.
     * @param pm The ProviderManager.
     */
    private void configure(ProviderManager pm) {
	Log.d(TAG, "configure");
	// Service Discovery # Items
	pm.addIQProvider("query", "http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());
	// Service Discovery # Info
	pm.addIQProvider("query", "http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());

	// Privacy
	//pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
	// Delayed Delivery only the new version
	pm.addExtensionProvider("delay", "urn:xmpp:delay", new DelayInfoProvider());

	// Service Discovery # Items
	pm.addIQProvider("query", "http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());
	// Service Discovery # Info
	pm.addIQProvider("query", "http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());

	// Chat State
	ChatStateExtension.Provider chatState = new ChatStateExtension.Provider();
	pm.addExtensionProvider("active", "http://jabber.org/protocol/chatstates", chatState);
	pm.addExtensionProvider("composing", "http://jabber.org/protocol/chatstates",
	    chatState);
	pm.addExtensionProvider("paused", "http://jabber.org/protocol/chatstates", chatState);
	pm.addExtensionProvider("inactive", "http://jabber.org/protocol/chatstates", chatState);
	pm.addExtensionProvider("gone", "http://jabber.org/protocol/chatstates", chatState);
	// capabilities
	pm.addExtensionProvider("c", "http://jabber.org/protocol/caps", new CapsProvider());
	//Pubsub
	pm.addIQProvider("pubsub", "http://jabber.org/protocol/pubsub", new PubSubProvider());
	pm.addExtensionProvider("items", "http://jabber.org/protocol/pubsub", new ItemsProvider());
	pm.addExtensionProvider("items", "http://jabber.org/protocol/pubsub", new ItemsProvider());
	pm.addExtensionProvider("item", "http://jabber.org/protocol/pubsub", new ItemProvider());

	pm.addExtensionProvider("items", "http://jabber.org/protocol/pubsub#event", new ItemsProvider());
	pm.addExtensionProvider("item", "http://jabber.org/protocol/pubsub#event", new ItemProvider());
	pm.addExtensionProvider("event", "http://jabber.org/protocol/pubsub#event", new EventProvider());

	//PEP avatar
	pm.addExtensionProvider("metadata", "urn:xmpp:avatar:metadata", new AvatarMetadataProvider());
	pm.addExtensionProvider("data", "urn:xmpp:avatar:data", new AvatarProvider());

    }

    /**
     * Listen on preference changes.
     */
    private class BeemServicePreferenceListener implements SharedPreferences.OnSharedPreferenceChangeListener {

	/**
	 * ctor.
	 */
	public BeemServicePreferenceListener() {
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	    if (BeemApplication.USE_AUTO_AWAY_KEY.equals(key)) {
		if (sharedPreferences.getBoolean(BeemApplication.USE_AUTO_AWAY_KEY, false)) {
		    mOnOffReceiverIsRegistered = true;
		    registerReceiver(mOnOffReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		    registerReceiver(mOnOffReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
		} else {
		    mOnOffReceiverIsRegistered = false;
		    unregisterReceiver(mOnOffReceiver);
		}
	    }
	    
	}
    }

    /**
     * Listen on some Intent broadcast, ScreenOn and ScreenOff.
     */
    private class BeemServiceBroadcastReceiver extends BroadcastReceiver {

	private String mOldStatus;
	private int mOldMode;

	/**
	 * Constructor.
	 */
	public BeemServiceBroadcastReceiver() {
	}

	@Override
	public void onReceive(final Context context, final Intent intent) {
	    String intentAction = intent.getAction();
	    if (intentAction.equals(Intent.ACTION_SCREEN_OFF)) {
		mOldMode = mConnection.getPreviousMode();
		mOldStatus = mConnection.getPreviousStatus();
		if (mConnection.isAuthentificated())
		    mConnection.changeStatus(Status.CONTACT_STATUS_AWAY,
			    mSettings.getString(BeemApplication.AUTO_AWAY_MSG_KEY, "Away"));
	    } else if (intentAction.equals(Intent.ACTION_SCREEN_ON)) {
		if (mConnection.isAuthentificated())
		    mConnection.changeStatus(mOldMode, mOldStatus);
	    }
	}
    }
    static class FakeX509TrustManager implements X509TrustManager {
        private static TrustManager[] trustManagers;
        private final X509Certificate[] _AcceptedIssuers = new X509Certificate[] {};
        static SSLContext context = null;
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public boolean isClientTrusted(X509Certificate[] chain) {
            return true;
        }

        public boolean isServerTrusted(X509Certificate[] chain) {
            return true;
        }

        public X509Certificate[] getAcceptedIssuers() {
            return _AcceptedIssuers;
        }
        public SSLContext getContext(){
        	return context;
        }
        public static void allowAllSSL() {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
            
            if (trustManagers == null) {
                trustManagers = new TrustManager[] { new FakeX509TrustManager() };
            }
            try {
                context = SSLContext.getInstance("TLS");
                context.init(null, trustManagers, new SecureRandom());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
            HttpsURLConnection.setDefaultSSLSocketFactory(context
                                                          .getSocketFactory());
        }
    }
}

