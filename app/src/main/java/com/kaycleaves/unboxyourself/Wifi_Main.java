package com.kaycleaves.unboxyourself;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.CursorIndexOutOfBoundsException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Wifi_Main extends AppCompatActivity {

    public String currentNetwork;
    IntentFilter filter1;
    public boolean paused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_main);
        mainFunction();
    }

    public void onResume() {
        super.onResume();

        // update current network connection
        String homeNetwork = getHomeNetwork(this, "Home_SSID");
        // find their current network
        String currentNetwork = getCurrentSsid(this);
        displayCurrentNetwork(homeNetwork, currentNetwork);
    }

    public void onStop() {
        super.onStop();
        try {
            unregisterReceiver(updateReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // respond to menu item selection
        switch(item.getItemId()) {
            case R.id.modeChange:
                startActivity(new Intent(this, Intro_2.class));
                return true;
            case R.id.viewArchive:
                startActivity(new Intent(this, Archive.class));
                return true;
            case R.id.addressChange:
                startActivity(new Intent(this, Intro_3_GPS.class));
                return true;
            case R.id.networkChange:
                startActivity(new Intent(this, Intro_3_Wifi.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private final BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // check if they're in wifi mode
            String mode = getMode(context, "tracking_mode");

            // if they are, proceed.
            if (mode.equals("Wifi")) {
                Log.d("Receiver", "Wifi Mode confirmed!");
                // get their current SSID
                String homeNetwork = getHomeNetwork(context, "Home_SSID");
                // find their current network
                String currentNetwork = getCurrentSsid(context);

                // log any changes
                displayCurrentNetwork(homeNetwork, currentNetwork);
            }
        }
    };

    private void mainFunction() {
        String homeNetwork = getHomeNetwork(this, "Home_SSID");
        // find their current network
        String currentNetwork = getCurrentSsid(this);
        filter1 = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(updateReceiver, filter1);
    }

    static private String getMode(Context c, String key) {
        SharedPreferences settings = c.getSharedPreferences("com.kaycleaves.unboxyourself", 0);
        settings = c.getSharedPreferences("com.kaycleaves.unboxyourself", 0);
        String value = settings.getString(key, "");
        return value;
    }

    public static String getHomeNetwork(Context c, String key) {
        SharedPreferences settings = c.getSharedPreferences("com.kaycleaves.unboxyourself", 0);
        settings = c.getSharedPreferences("com.kaycleaves.unboxyourself", 0);
        String value = settings.getString(key, "");
        Log.d("Home_SSID", value);
        return value;
    }

    public static String getCurrentSsid(Context context) {
        String ssid = null;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null) {
            ssid = "No Network";
            Log.d("current network", ssid);
            return ssid;
        }

        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                ssid = connectionInfo.getSSID().replace("\"","");
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                ssid = "Mobile";
            }
        }
        Log.d("current network", ssid);
        return ssid;
    }

    public void displayCurrentNetwork(String home, String current) {
        String output;
        String output2;
        TextView cn = (TextView)findViewById(R.id.currentNetwork);
        TextView lo = (TextView)findViewById(R.id.lastOuting);

        if (current.equals("No Network")) {
            output = "None";
            output2 = getString(R.string.Congratulations);
            logOuting();
        } else if (current.equals("Mobile")) {
            output = "Mobile";
            output2 = getString(R.string.Congratulations);
            logOuting();
        } else if (current.equals(home)) {
            output = home;
            // get their last outing
            String lastOut = tail();
            output2 = getLastOuting(lastOut);
            Log.d("Match", "current matches home");
        } else { // they're on wifi but not at home
            output = current;
            output2 = getString(R.string.Congratulations);
            logOuting();
            Log.d("Match", "current does not match home");
        }

        cn.setText(output);
        lo.setText(output2);
    }

    private String getLastOuting(String ld) {
        String output = null;
        if (isValidDate(ld)) {
            // calculate difference in dates
            int datediff = calcDateDiff(ld);
            if (datediff == 0) {
                output = "Congrats! You left the house today!";
            } else if (datediff > 0) {
                output = "You last left the house on " + ld + ". That was " + datediff + " days ago.";
            } else { // negative number - they've been messing with the database or their time zone changed drastically
                output = "You last left the house ... tomorrow? Big time zone changes can confuse our tracker.";
            }
        } else {
            output = "No adventures yet!";
        }
        return output;

    }

    private boolean isValidDate(String inDate) {
        // This method from http://www.java2s.com/Tutorial/Java/0120__Development/CheckifaStringisavaliddate.htm
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        sdf.setLenient(false);
        try {
            sdf.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    static int calcDateDiff(String startdate) {
        // this method from http://stackoverflow.com/questions/20165564/calculating-days-between-two-dates-with-in-java
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        sdf.setTimeZone(TimeZone.getDefault());
        String today = sdf.format(new Date());

        try {
            Date start = sdf.parse(startdate);
            Date end = sdf.parse(today);
            long diff = end.getTime() - start.getTime();
            int value = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            return value;
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return 0;
    }

    public void logOuting() {
        // get the current date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        sdf.setTimeZone(TimeZone.getDefault());
        String today = sdf.format(new Date());

        DatabaseHandler db = new DatabaseHandler(this);
        db.addOuting(new Outing(today, "Wifi"));

        // update the display
        Toast.makeText(Wifi_Main.this, "Outing saved!", Toast.LENGTH_LONG).show();
    }

    public String tail() {
        DatabaseHandler db = new DatabaseHandler(this);
        String lastout = "";
        try {
            Outing out = db.getLastOuting();
            lastout = out.getDate();
        } catch (CursorIndexOutOfBoundsException e) {
            lastout = "None";
        }
        return lastout;

    }

    public void pauseTracker(View view) {
        Button pausebutton = (Button)findViewById(R.id.pauseButtonWifi);
        TextView cn = (TextView)findViewById(R.id.currentNetwork);
        if (paused) {
            paused = false;
            mainFunction();
            pausebutton.setText("Pause Tracker");
        } else {
            paused = true;
            unregisterReceiver(updateReceiver);
            pausebutton.setText("Start Tracker");
            cn.setText("Tracker Paused");
        }
        Log.i("pause status", String.valueOf(paused));
    }
}
