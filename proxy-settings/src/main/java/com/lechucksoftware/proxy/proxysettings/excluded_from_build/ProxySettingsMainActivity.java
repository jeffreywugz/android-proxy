package com.lechucksoftware.proxy.proxysettings.excluded_from_build;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import android.util.Log;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.ui.help.DisclaimerFragmentActivity;
import com.lechucksoftware.proxy.proxysettings.utils.EventReportingUtils;
import com.shouldit.proxy.lib.APL;
import com.shouldit.proxy.lib.log.LogWrapper;


public class ProxySettingsMainActivity extends FragmentActivity
{
	public static String TAG = "ProxySettingsActivity";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

        LogWrapper.startTrace(TAG,"STARTUP", Log.ERROR,true);

//        LogWrapper.d(TAG, "Creating ProxySettingsMainActivity");

        // Ensure that SETUP for Libraries are called
        APL.setup(ProxySettingsMainActivity.this);
        EventReportingUtils.setup(ProxySettingsMainActivity.this);

		// Restore preferences
		SharedPreferences settings = getSharedPreferences(Constants.PREFERENCES_FILENAME, 0);
		boolean acceptedDisclaimer = settings.getBoolean(Constants.PREFERENCES_ACCEPTED_DISCLAIMER, false);

		if (acceptedDisclaimer || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)) // Disable disclaimer for API 12 = Honeycomb 3.1
		{
			LogWrapper.d(TAG, "Starting ProxySettingsCallerActivity activity");
			Intent i = new Intent(getApplicationContext(), ProxySettingsCallerActivity.class);
			startActivity(i);
		}
		else
		{
			LogWrapper.d(TAG, "Starting DisclaimerActivity activity");
			Intent disclaimer = new Intent(getApplicationContext(), DisclaimerFragmentActivity.class);
			startActivity(disclaimer);
		}

		finish();
	}
	
	public void onBackPressed()
	{
//		LogWrapper.d(TAG, "Back Pressed");
		return;
	}

	@Override
	protected void onStart()
	{
		super.onStart();
//		LogWrapper.d(TAG, "Start");
	}

	@Override
	protected void onResume()
	{
		super.onResume();
//		LogWrapper.d(TAG, "Resume");
	}

	@Override
	protected void onPause()
	{
		super.onPause();
//		LogWrapper.d(TAG, "Pause");
	}

	@Override
	protected void onStop()
	{
//		LogWrapper.d(TAG, "Stop");
		super.onStop();
	}

	@Override
	protected void onDestroy()
	{
//		LogWrapper.d(TAG, "Destroy");
		super.onDestroy();
	}

}