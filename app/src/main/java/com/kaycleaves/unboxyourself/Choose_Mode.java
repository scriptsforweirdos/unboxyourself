package com.kaycleaves.unboxyourself;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

/*
 * Allows selection of modes.
 * Created by Kay Cleaves
 */
public class Choose_Mode extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_choose_mode);
    }

    // Set GPS mode
    public void setModeGPS(View view) {
        // store to shared prefs tracking_mode
        SharedPreferences sharedPref = this.getSharedPreferences("com.kaycleaves.unboxyourself",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.tracking_mode), "GPS");
        editor.commit();

        // take them to the GPS configuration and close this activity permanently.
        Intent intent = new Intent(this, GPS_Configuration.class);
        startActivity(intent);
        finish();
    }

    // Set manual mode
    public void setModeManual(View view) {
        SharedPreferences sharedPref = this.getSharedPreferences("com.kaycleaves.unboxyourself",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.tracking_mode), "Manual");
        editor.commit();

        Intent intent = new Intent(this, Manual_Configuration.class);
        startActivity(intent);
        finish();
    }

    // set wifi mode
    public void setModeWifi(View view) {
        SharedPreferences sharedPref = this.getSharedPreferences("com.kaycleaves.unboxyourself",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.tracking_mode), "Wifi");
        editor.commit();

        Intent intent = new Intent(this, Wifi_Configuration.class);
        startActivity(intent);
        finish();
    }

}
