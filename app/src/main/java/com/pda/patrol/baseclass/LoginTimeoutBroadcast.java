package com.pda.patrol.baseclass;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class LoginTimeoutBroadcast {

	private final LoginTimeoutReceiver
	mReceiver = new LoginTimeoutReceiver();
	private Activity mActivity;
	
	public LoginTimeoutBroadcast(Activity activity){
		this.mActivity = activity;
	}
	
	public void registerReceiver() {
		IntentFilter filter = new IntentFilter();  
        filter.addAction(Constants.LOGIN_TIMEOUT_BROADCAST);
        mActivity.registerReceiver(mReceiver, filter);
	}
	
	public void unregisterReceiver() {
		mActivity.unregisterReceiver(mReceiver);
	}
	
	private class LoginTimeoutReceiver extends BroadcastReceiver {
		private static final String TAG = "LoginTimeoutReceiver";
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, intent.getAction());

			if (intent.getAction().equals(Constants.LOGIN_TIMEOUT_BROADCAST)) {

//				if (!mActivity.isFinishing()) {
//					LogOutUtil.logout(mActivity);
//				}
			}
			
		}
	}

	private void gotoLoginActivity() {

	}
}
