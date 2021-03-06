package com.lechucksoftware.proxy.proxysettings.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by marco on 18/09/13.
 */
public class InstallationStatistics
{
    private static final String TAG = InstallationStatistics.class.getSimpleName();
    public long launchCount;
    public Date launhcFirstDate;

    public static InstallationStatistics GetInstallationDetails(Context applicationContext)
    {
        SharedPreferences prefs = applicationContext.getSharedPreferences(Constants.PREFERENCES_FILENAME, 0);
        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launch_count = prefs.getLong(Constants.PREFERENCES_APP_LAUNCH_COUNT, 0) + 1;
        editor.putLong(Constants.PREFERENCES_APP_LAUNCH_COUNT, launch_count);

        // Get date of first launch
        long date_firstLaunch = prefs.getLong(Constants.PREFERENCES_APP_DATE_FIRST_LAUNCH, 0);
        if (date_firstLaunch == 0)
        {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong(Constants.PREFERENCES_APP_DATE_FIRST_LAUNCH, date_firstLaunch);
        }

        DateFormat df = DateFormat.getDateTimeInstance();
        Date resultdate = new Date(date_firstLaunch);
        App.getLogger().e(TAG, String.format("App launched #%d times since %s", launch_count, df.format(resultdate)));
        editor.commit();

        InstallationStatistics details = new InstallationStatistics();
        details.launchCount = launch_count;
        details.launhcFirstDate = resultdate;
        return details;
    }
}
