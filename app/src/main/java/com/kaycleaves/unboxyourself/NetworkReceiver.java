package com.kaycleaves.unboxyourself;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Roolet on 9/16/2016.
 */
public class NetworkReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(final Context context, final Intent intent) {


        // check if they're in wifi mode
        String mode = getMode(context, "tracking_mode");

        // if they are, proceed.
        if (mode.equals("Wifi")) {
            Log.d("Receiver", "Wifi Mode confirmed!");
            // get their current SSID
            String homeNetwork = Wifi_Main.getHomeNetwork(context, "Home_SSID");
            // find their current network
            String currentNetwork = Wifi_Main.getCurrentSsid(context);

            // log any changes
            logifDifferent(context, currentNetwork, homeNetwork);

            // update the view
            Toast.makeText(context, "Outing saved!", Toast.LENGTH_LONG).show();
        }
    }

    static private String getMode(Context c, String key) {
        SharedPreferences settings = c.getSharedPreferences("com.kaycleaves.unboxyourself", 0);
        settings = c.getSharedPreferences("com.kaycleaves.unboxyourself", 0);
        String value = settings.getString(key, "");
        return value;
    }



    private void logifDifferent(Context context, String current, String home) {
        if (!current.equals(home)) {
            logOuting(context);
        }
    }

    public void logOuting(Context context) {
        // get the current date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        sdf.setTimeZone(TimeZone.getDefault());
        String today = sdf.format(new Date());

        DatabaseHandler db = new DatabaseHandler(context);
        db.addOuting(new Outing(today, "Manual"));

    }

}
