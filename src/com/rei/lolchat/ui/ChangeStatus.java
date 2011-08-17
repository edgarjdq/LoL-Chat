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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.rei.lolchat.BeemService;
import com.rei.lolchat.BeemApplication;
import com.rei.lolchat.R;
import com.rei.lolchat.service.aidl.IXmppFacade;
import com.rei.lolchat.service.UserInfo;
import com.rei.lolchat.providers.AvatarProvider;
import com.rei.lolchat.utils.BeemBroadcastReceiver;
import com.rei.lolchat.utils.BeemConnectivity;
import com.rei.lolchat.utils.Status;

import java.io.File;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * This Activity is used to change the status.
 * @author nikita
 */
public class ChangeStatus extends Activity {

    private static final Intent SERVICE_INTENT = new Intent();
    static {
	SERVICE_INTENT.setComponent(new ComponentName("com.rei.lolchat", "com.rei.lolchat.BeemService"));
    }

    private static final String TAG = ChangeStatus.class.getSimpleName();
    private static final int AVAILABLE_FOR_CHAT_IDX = 0;
    private static final int AVAILABLE_IDX = 1;
    private static final int BUSY_IDX = 2;
    private static final int AWAY_IDX = 3;
    private static final int UNAVAILABLE_IDX = 4;
    private static final int DISCONNECTED_IDX = 5;

    private static final int ICON_SIZE = 80;

    private static final int SELECT_PHOTO_DLG = 0;

    private static final int CAMERA_WITH_DATA = 0;
    private static final int PHOTO_PICKED_WITH_DATA = 1;

    private static final File PHOTO_DIR = new File(
            Environment.getExternalStorageDirectory() + "/DCIM/Camera");

    private static final String KEY_CURRENT_PHOTO_FILE = "currentphotofile";

    private static final Uri MY_AVATAR_URI = Uri.parse(AvatarProvider.CONTENT_URI + "/my_avatar");

    private EditText mStatusMessageEditText;
    private EditText mStatusLevelEditText;
    private EditText mStatusWinsEditText;
    private EditText mStatusLeavesEditText;
    private EditText mStatusMsgEditText;
    Dialog dialog;  
    private Toast mToast;
    private Button mOk;
    private Button mContact;
    private Spinner mSpinner;
    private ImageButton mAvatar;
    private Uri mAvatarUri;

    private SharedPreferences mSettings;
    private ArrayAdapter<CharSequence> mAdapter;
    private IXmppFacade mXmppFacade;
    private final ServiceConnection mServConn = new BeemServiceConnection();
    private final OnClickListener mOnClickOk = new MyOnClickListener();
    private final BeemBroadcastReceiver mReceiver = new BeemBroadcastReceiver();
    private boolean mShowCurrentAvatar = true;
    private boolean mDisableAvatar;
    private File mCurrentPhotoFile;
    private int profileIcon = 23;
    GridView gridView;
    public final int CATEGORY_ID = 0;
    /**
     * Constructor.
     */
    public ChangeStatus() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	Log.d(TAG, "oncreate");
	setContentView(R.layout.changestatus);

	mOk = (Button) findViewById(R.id.ChangeStatusOk);
	mOk.setOnClickListener(mOnClickOk);

	mContact = (Button) findViewById(R.id.OpenContactList);
	mContact.setOnClickListener(mOnClickOk);

	BeemApplication app = (BeemApplication) getApplication();
	
	

	mSettings = PreferenceManager.getDefaultSharedPreferences(this);
	mStatusMessageEditText = (EditText) findViewById(R.id.ChangeStatusMessage);
	mStatusLevelEditText = (EditText) findViewById(R.id.editTextLevel);
	mStatusLeavesEditText = (EditText) findViewById(R.id.editTextLeaves);
	mStatusWinsEditText = (EditText) findViewById(R.id.editTextWins);
	mStatusMsgEditText = (EditText) findViewById(R.id.editTextMsg);
	
	
	
	mStatusMsgEditText.setOnKeyListener(keyListener);
	mStatusWinsEditText.setOnKeyListener(keyListener);
	mStatusLeavesEditText.setOnKeyListener(keyListener);
	mStatusLevelEditText.setOnKeyListener(keyListener);
	
	mStatusMsgEditText.setText(getPreferenceString(BeemApplication.MSG_KEY));
	mStatusWinsEditText.setText(getPreferenceString(BeemApplication.WINS_KEY));
	mStatusLeavesEditText.setText(getPreferenceString(BeemApplication.LEAVES_KEY));
	mStatusLevelEditText.setText(getPreferenceString(BeemApplication.LEVEL_KEY));
	profileIcon = mSettings.getInt(BeemApplication.PROFILE_KEY, 23);
	
	mAvatar = (ImageButton) findViewById(R.id.avatarButton);
	mAvatar.setOnClickListener(mOnClickOk);
	mAvatar.setImageDrawable(getResources().getDrawable(BeemApplication.PROFILEIDS[profileIcon]));
	
	refreshStatusXml();
	 
    mSpinner = (Spinner) findViewById(R.id.ChangeStatusSpinner);
	mAdapter = ArrayAdapter.createFromResource(this, R.array.status_types, android.R.layout.simple_spinner_item);
	mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	mSpinner.setAdapter(mAdapter);

	mToast = Toast.makeText(this, R.string.ChangeStatusOk, Toast.LENGTH_LONG);
	mSpinner.setSelection(getPreferenceStatusIndex());

	this.registerReceiver(mReceiver, new IntentFilter(BeemBroadcastReceiver.BEEM_CONNECTION_CLOSED));
    }
    
    OnKeyListener keyListener = new OnKeyListener() {                 
        public boolean onKey(View v, int keyCode, KeyEvent event) {
        	refreshStatusXml();
        	return false;
        }
    };
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume() {
	super.onResume();
	if (!BeemConnectivity.isConnected(getApplicationContext())) {
	    Intent i = new Intent(this, Login.class);
	    startActivity(i);
	    finish();
	}
	bindService(new Intent(this, BeemService.class), mServConn, BIND_AUTO_CREATE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause() {
	super.onPause();
	unbindService(mServConn);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
	super.onDestroy();
	this.unregisterReceiver(mReceiver);
    }

    /*
     * The activity is often reclaimed by the system memory.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mCurrentPhotoFile != null) {
            outState.putString(KEY_CURRENT_PHOTO_FILE, mCurrentPhotoFile.toString());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        String fileName = savedInstanceState.getString(KEY_CURRENT_PHOTO_FILE);
        if (fileName != null) {
            mCurrentPhotoFile = new File(fileName);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }



    @Override
    protected Dialog onCreateDialog(int id) {     
        
        switch(id) {     
  
        case CATEGORY_ID:     
          
         AlertDialog.Builder builder;     
            Context mContext = this;     
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);     
            View layout = inflater.inflate(R.layout.icon_select,(ViewGroup) findViewById(R.id.layout_root));     
            gridView = (GridView) layout.findViewById(R.id.profile_icon);
            gridView.setAdapter(new ImageAdapter(this));
             
            gridView.setOnItemClickListener(new OnItemClickListener()     
            {  
                public void onItemClick(AdapterView parent, View v,int position, long id) {     
                 dialog.dismiss();  
                 profileIcon = position;
                 Editor edit = mSettings.edit();
     			 edit.putInt(BeemApplication.PROFILE_KEY, position);
     			 edit.commit();
     			 mAvatar.setImageDrawable(getResources().getDrawable(BeemApplication.PROFILEIDS[profileIcon]));
     		 	 refreshStatusXml();
                }     
            });  
              
            ImageView close = (ImageView) layout.findViewById(R.id.close);  
            close.setOnClickListener(new View.OnClickListener() {  
    public void onClick(View v){  
              dialog.dismiss();  
            }  
            });  
              
            builder = new AlertDialog.Builder(mContext);     
            builder.setView(layout);     
            dialog = builder.create();     
            break;     
        default:     
            dialog = null;     
        }     
        return dialog;     
    }   

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  // Ignore failed requests
        if (resultCode != RESULT_OK) return;
    }

    /**
     * Return the status index from status the settings.
     * @return the status index from status the settings.
     */
    private int getPreferenceStatusIndex() {
	return mSettings.getInt(BeemApplication.STATUS_KEY, AVAILABLE_IDX);
    }

    /**
     * Return the status text from status the settings.
     * @param id status text id.
     * @return the status text from status the settings.
     */
    private String getPreferenceString(int id) {
	return mSettings.getString(getString(id), "");
    }
    private String getPreferenceString(String id) {
    	return mSettings.getString(id, "");
    }
    /**
     * convert status text to.
     * @param item selected item text.
     * @return item position in the array.
     */
    private int getStatusForService(String item) {
	return Status.CONTACT_STATUS_AVAILABLE_FOR_CHAT;
    }

    /**
     * ClickListener for the avatarButton.
     *
     * @param button the avatar button
     */
    private void onAvatarButton(View button) {
    	showDialog(CATEGORY_ID);
    }

   

    private void refreshStatusXml(){
    	String msg = mStatusMsgEditText.getText().toString();
    	String level = mStatusLevelEditText.getText().toString();
    	String wins = mStatusWinsEditText.getText().toString();
    	String leaves = mStatusLeavesEditText.getText().toString();
        String lolXml = "<body><gameStatus>outOfGame</gameStatus>" +
        		"<profileIcon>"+profileIcon+"</profileIcon>" +
        		"<level>"+level+"</level>" +
        		"<wins>"+wins+"</wins>" +
        		"<leaves>"+leaves+"</leaves>" +
        		
        		"<rankedWins>0</rankedWins>" +
        		"<rankedLosses>0</rankedLosses><rankedRating>0</rankedRating>" +
        		"<statusMsg>"+msg+"</statusMsg></body>";
    	mStatusMessageEditText.setText(lolXml);
    }

    /**
     * connection to service.
     * @author nikita
     */
    private class BeemServiceConnection implements ServiceConnection {

	/**
	 * constructor.
	 */
	public BeemServiceConnection() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
	    mXmppFacade = IXmppFacade.Stub.asInterface(service);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onServiceDisconnected(ComponentName name) {
	    mXmppFacade = null;
	}
    }

    /**
     * User have clicked on ok.
     * @author nikita
     */
    private class MyOnClickListener implements OnClickListener {

		/**
		 * constructor.
		 */
		public MyOnClickListener() {
		}
	
		@Override
		public void onClick(View v) {
		    if (v == mOk) {
			String msg = mStatusMessageEditText.getText().toString();
			String msgStatus = mStatusMsgEditText.getText().toString();
	    	String level = mStatusLevelEditText.getText().toString();
	    	String wins = mStatusWinsEditText.getText().toString();
	    	String leaves = mStatusLeavesEditText.getText().toString();
			
			int status = getStatusForService((String) mSpinner.getSelectedItem());
			Editor edit = mSettings.edit();
			edit.putString(BeemApplication.STATUS_TEXT_KEY, msg);
			edit.putString(BeemApplication.LEVEL_KEY, level);
			edit.putString(BeemApplication.WINS_KEY, wins);
			edit.putString(BeemApplication.LEAVES_KEY, leaves);
			edit.putString(BeemApplication.MSG_KEY, msgStatus);
			
			if (status == Status.CONTACT_STATUS_DISCONNECT) {
			    stopService(new Intent(ChangeStatus.this, BeemService.class));
			} else {
			    try {
				mXmppFacade.changeStatus(status, msg.toString());
				edit.putInt(BeemApplication.STATUS_KEY, mSpinner.getSelectedItemPosition());
			    } catch (RemoteException e) {
				e.printStackTrace();
			    }
			    mToast.show();
			}
			edit.commit();
			ChangeStatus.this.finish();
		    } else if (v == mContact) {
			startActivity(new Intent(ChangeStatus.this, ContactList.class));
			ChangeStatus.this.finish();
		    } else if (v == mAvatar)
			onAvatarButton(v);
		}
    }
    
    public class ImageAdapter extends BaseAdapter 
    {
        private Context context;

        public ImageAdapter(Context c) 
        {
            context = c;
        }

        //---returns the number of images---
        public int getCount() {
            return BeemApplication.PROFILEIDS.length;
        }

        //---returns the ID of an item--- 
        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        //---returns an ImageView view---
        public View getView(int position, View convertView, ViewGroup parent) 
        {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(context);
                imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(5, 5, 5, 5);
            } else {
                imageView = (ImageView) convertView;
            }
            imageView.setImageResource(BeemApplication.PROFILEIDS[position]);
            return imageView;
        }

        
    }   
    
}
