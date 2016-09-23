package com.kaycleaves.unboxyourself;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class Intro_2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_intro_2);
    }

    public void setModeGPS(View view) {
        SharedPreferences sharedPref = this.getSharedPreferences("com.kaycleaves.unboxyourself",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.tracking_mode), "GPS");
        editor.commit();

        Intent intent = new Intent(this, Intro_3_GPS.class);
        startActivity(intent);
        finish();
    }


    public void setModeManual(View view) {
        SharedPreferences sharedPref = this.getSharedPreferences("com.kaycleaves.unboxyourself",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.tracking_mode), "Manual");
        editor.commit();

        Intent intent = new Intent(this, Intro_3_Manual.class);
        startActivity(intent);
        finish();
    }

    public void setModeWifi(View view) {
        SharedPreferences sharedPref = this.getSharedPreferences("com.kaycleaves.unboxyourself",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.tracking_mode), "Wifi");
        editor.commit();

        Intent intent = new Intent(this, Intro_3_Wifi.class);
        startActivity(intent);
        finish();
    }

}
