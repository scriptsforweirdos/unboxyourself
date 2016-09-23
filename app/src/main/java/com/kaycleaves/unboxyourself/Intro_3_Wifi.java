package com.kaycleaves.unboxyourself;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Intro_3_Wifi extends AppCompatActivity {

    Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_intro_3__wifi);

        // disable the start button until location is saved.
        startButton = (Button)findViewById(R.id.wifiStart);
        startButton.setEnabled(false);

        String selectedMode = getMode(this, "tracking_mode");
        displayMode(selectedMode);
        listNetworks();
    }

    private void displayMode(String selectedMode) {
        TextView v = (TextView)findViewById(R.id.selected_mode);
        v.setText(selectedMode);
    }

    static private String getMode(Context c, String key) {
        SharedPreferences settings = c.getSharedPreferences("com.kaycleaves.unboxyourself", 0);
        settings = c.getSharedPreferences("com.kaycleaves.unboxyourself", 0);
        String value = settings.getString(key, "");
        return value;
    }

    private void listNetworks() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();

        List<String> networkIDs = new ArrayList<String>();
        try {
            for (WifiConfiguration existingConfig : existingConfigs) {
                networkIDs.add(existingConfig.SSID);
            }
        } catch (NullPointerException e) {
            networkIDs.add("Please Turn on Wifi");
        }
        Collections.sort(networkIDs);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,networkIDs);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner networkSpinner = (Spinner) findViewById(R.id.ssidspinner);

        networkSpinner.setAdapter(dataAdapter);
    }

    public void refreshNetworks(View view) {
        listNetworks();
    }

    public void saveSSID(View view) throws IOException {
        Spinner spinner = (Spinner) findViewById(R.id.ssidspinner);
        String selectedSSID = spinner.getSelectedItem().toString();
        selectedSSID = selectedSSID.replace("\"",""); // remove quotes

        SharedPreferences sharedPref = this.getSharedPreferences("com.kaycleaves.unboxyourself",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("Home_SSID", selectedSSID);
        editor.commit();

        TextView wifiOutput = (TextView) findViewById(R.id.wifiOutput);
        String outText = "Home network set to\n" + selectedSSID;
        wifiOutput.setText(outText);
        startButton.setEnabled(true);
        Log.i("Unbox SSID", selectedSSID);
    }

    public void loadWifiMain(View view) {

        // store the fact that they finished the intro
        SharedPreferences sharedPref = this.getSharedPreferences("com.kaycleaves.unboxyourself",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("First Run Complete", "Yes");
        editor.commit();

        // Move them to the main manual screen
        Intent intent = new Intent(this, Wifi_Main.class);
        startActivity(intent);
        finish();
    }


}
