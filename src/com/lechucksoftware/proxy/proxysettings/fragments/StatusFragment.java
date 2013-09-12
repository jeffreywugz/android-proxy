package com.lechucksoftware.proxy.proxysettings.fragments;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.Constants;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.utils.BugReportingUtils;
import com.shouldit.proxy.lib.APL;
import com.shouldit.proxy.lib.APLConstants;
import com.shouldit.proxy.lib.ProxyConfiguration;


/**
 * Created by marco on 21/05/13.
 */
public class StatusFragment extends EnhancedFragment
{
    private static final String TAG = "StatusFragment";
    public static StatusFragment instance;
    private Button statusButton;

//    private Constants.StatusFragmentStates currentStatus;
//    private Constants.StatusFragmentStates clickedStatus;
//    private View statusSpace;

    /**
     * Create a new instance of StatusFragment
     */
    public static StatusFragment getInstance()
    {
        if (instance == null)
            instance = new StatusFragment();

        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.status, container, false);
        statusButton = (Button) view.findViewById(R.id.status_button);
//        statusSpace = view.findViewById(R.id.status_space);
//        statusButton.setBackgroundColor(getResources().getColor(R.color.Holo_Green_Light));
        return view;
    }

    public void refreshUI()
    {
        if (isAdded())
        {
            if (statusButton != null)
            {
//                hide();

                ProxyConfiguration selConf = ApplicationGlobals.getSelectedConfiguration();

                if (selConf != null)
                {
                    if (selConf.isCurrentNetwork())
                    {
                        if(selConf.status != null)
                        {
                            if (selConf.status.getCheckingStatus() == APLConstants.CheckStatusValues.CHECKED)
                            {
                                setStatus(Constants.StatusFragmentStates.CONNECTED, selConf.getAPConnectionStatus());
                            }
                            else
                            {
                                setStatus(Constants.StatusFragmentStates.CHECKING, getResources().getString(R.string.checking_action));
                            }
                        }
                        else
                        {
                            setStatus(Constants.StatusFragmentStates.CHECKING, getResources().getString(R.string.checking_action));
                        }
                    }
                    else if (selConf.ap.getLevel() > -1)
                    {
                        setStatus(Constants.StatusFragmentStates.CONNECT_TO, getResources().getString(R.string.connect_to_wifi_action,selConf.ap.ssid));
                    }
                    else
                    {
                        setStatus(Constants.StatusFragmentStates.NOT_AVAILABLE, selConf.getAPConnectionStatus());
                    }
                }
                else
                {
                    // No configuration selected
                    if (!APL.getWifiManager().isWifiEnabled())
                    {
                        // Wi-Fi disabled -> ask to enable!
                        setStatus(Constants.StatusFragmentStates.ENABLE_WIFI, getResources().getString(R.string.enable_wifi_action));
                    }
                    else
                    {
                        // Wi-Fi enabled
                        if (ApplicationGlobals.isConnectedToWiFi())
                        {
                            // Connected to Wi-Fi ap

                        }
                        else
                        {
                            if (ApplicationGlobals.getInstance().getNotConfiguredWifi().values().size() > 0)
                            {
                                // Wi-Fi AP available to connection -> Go to Wi-Fi Settings
                                setStatus(Constants.StatusFragmentStates.GOTO_AVAILABLE_WIFI, getResources().getString(R.string.setupap_wifi_action));
                            }
                            else
                            {
                                // Wi-Fi AP not available to connection
//                                setStatusInternal(getResources().getString(R.string.enable_wifi_action), configureNewWifiAp, R.color.Holo_Green_Light);
                            }
                        }
                    }
                }
            }
        }
    }

    public void setStatus(Constants.StatusFragmentStates status, String message)
    {
        setStatus(status,message,Boolean.FALSE);
    }

    public void setStatus(Constants.StatusFragmentStates status, String message, Boolean isInProgress)
    {
//        if (status == clickedStatus)
//        {
//            return;
//        }

        switch (status)
        {
            case CONNECTED:
                setStatusInternal(message, null, R.drawable.btn_blue_holo_dark,true);
                break;
            case CHECKING:
                setStatusInternal(message, null, R.drawable.btn_blue_holo_dark,false);
                break;
            case CONNECT_TO:
                setStatusInternal(message, connectToWifi, R.drawable.btn_green_holo_dark,true);
                break;
            case NOT_AVAILABLE:
                setStatusInternal(message, null, R.drawable.btn_blue_holo_dark,false);
                break;
            case ENABLE_WIFI:
                setStatusInternal(message, enableWifi, R.drawable.btn_red_holo_dark,true);
                break;
            case GOTO_AVAILABLE_WIFI:
                setStatusInternal(message, configureNewWifiAp, R.drawable.btn_green_holo_dark,true);
                break;
            case NONE:
//            default:
//                hide();
        }
    }

    private void setStatusInternal(String status, View.OnClickListener listener, int resId, boolean enabled)
    {
//        clickedStatus = null;

        if (listener != null)
            statusButton.setText(String.format("%s...", status));
        else
            statusButton.setText(status);

        statusButton.setBackgroundResource(resId);
        statusButton.setEnabled(enabled);
        statusButton.setOnClickListener(listener);
//        show();
    }

    View.OnClickListener enableWifi = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
//            hide();
            try
            {
                APL.enableWifi();
            }
            catch (Exception e)
            {
                BugReportingUtils.sendException(new Exception("Exception during StatusFragment enableWifi action: " + e.toString()));
            }
//            clickedStatus = Constants.StatusFragmentStates.ENABLE_WIFI;
        }
    };

    View.OnClickListener connectToWifi = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
//            hide();
            ApplicationGlobals.connectToAP(ApplicationGlobals.getSelectedConfiguration());
//            clickedStatus = Constants.StatusFragmentStates.CONNECT_TO;
        }
    };

    View.OnClickListener configureNewWifiAp = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
//            hide();
            Intent intent = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
            startActivity(intent);
//            clickedStatus = Constants.StatusFragmentStates.GOTO_AVAILABLE_WIFI;
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();
//        clickedStatus = null;

        refreshUI();
    }

    @Override
    public void onPause()
    {
        super.onPause();
//        clickedStatus = null;
    }

//    private void hide()
//    {
//        FragmentManager fm = this.getFragmentManager();
//        FragmentTransaction fts = fm.beginTransaction();
//        fts.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//        fts.hide(StatusFragment.getInstance());
//        fts.commit();
//
////        statusButton.setVisibility(View.GONE);
////        statusSpace.setVisibility(View.GONE);
//    }

//    private void show()
//    {
//        FragmentManager fm = this.getFragmentManager();
//        FragmentTransaction fts = fm.beginTransaction();
//        fts.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//        fts.show(StatusFragment.getInstance());
//        fts.commit();
//
////        statusButton.setVisibility(View.VISIBLE);
////        statusSpace.setVisibility(View.VISIBLE);
//    }
}
