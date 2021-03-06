package com.lechucksoftware.proxy.proxysettings.services;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.constants.Intents;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.utils.EventReportingUtils;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;

import java.util.List;

public class MaintenanceService extends IntentService
{
    public static final String CALLER_INTENT = "CallerIntent";
    public static String TAG = MaintenanceService.class.getSimpleName();
    private boolean isHandling = false;
    private static MaintenanceService instance;

    public MaintenanceService()
    {
        super("MaintenanceService");
        App.getLogger().v(TAG, "MaintenanceService constructor");
    }

    public static MaintenanceService getInstance()
    {
        return instance;
    }

    public boolean isHandlingIntent()
    {
        return isHandling;
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        instance = this;
        isHandling = true;

        App.getLogger().startTrace(TAG, "maintenanceService", Log.DEBUG);

        handleIntentLogic(intent);

        App.getLogger().stopTrace(TAG, "maintenanceService", Log.DEBUG);
        isHandling = false;
    }

    private void handleIntentLogic(Intent intent)
    {
        if (intent != null && intent.hasExtra(CALLER_INTENT))
        {
            Intent callerIntent = (Intent) intent.getExtras().get(CALLER_INTENT);

            if (callerIntent != null)
            {
                try
                {
                    if (callerIntent.getAction().equals(Intents.PROXY_SETTINGS_STARTED))
                    {
                        checkDBstatus();
                        checkProxiesCountryCodes();
                        Utils.checkDemoMode(getApplicationContext());
                    }
                    else if (callerIntent.getAction().equals(Intents.PROXY_SAVED))
                    {
                        checkProxiesCountryCodes();
                    }
                    else
                    {
                        App.getLogger().e(TAG, "Intent not handled: " + callerIntent.toString());
                    }
                }
                catch (Exception e)
                {
                    EventReportingUtils.sendException(new Exception("Exception during maintenanceService", e));
                }
            }
        }

        return;
    }

    private void checkDBstatus()
    {
        /**
         * Add IN USE TAG
         */
//        getInUseProxyTag();
    }

//    private TagEntity getInUseProxyTag()
//    {
//        TagEntity inUseTag = null;
//        long id  = App.getDBManager().findTag("IN USE");
//        if (id != -1)
//        {
//            inUseTag = App.getDBManager().getTag(id);
//        }
//        else
//        {
//            inUseTag = new TagEntity();
//            inUseTag.tag = "IN USE";
//            inUseTag.tagColor = UIUtils.getTagsColor(this, 0);
//            App.getDBManager().upsertTag(inUseTag);
//        }
//
//        return inUseTag;
//    }

    private void checkProxiesCountryCodes()
    {
        List<ProxyEntity> proxies = App.getDBManager().getProxyWithEmptyCountryCode();

        for (ProxyEntity proxy : proxies)
        {
            App.getLogger().startTrace(TAG, "Get proxy country code", Log.DEBUG);

            try
            {
                String countryCode = Utils.getProxyCountryCode(proxy);
                if (!TextUtils.isEmpty(countryCode))
                {
                    proxy.setCountryCode(countryCode);
                    App.getDBManager().upsertProxy(proxy);
                }
            }
            catch (Exception e)
            {
                EventReportingUtils.sendException(e);
                break;
            }

            App.getLogger().stopTrace(TAG, "Get proxy country code", proxy.toString(), Log.DEBUG);
        }
    }
}