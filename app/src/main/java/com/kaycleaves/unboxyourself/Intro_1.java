package com.kaycleaves.unboxyourself;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class Intro_1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkFirstRun(this);
        //Remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_intro_1);
    }

    // called when use clicks button
    public void loadIntro2(View view) {
        Intent intent = new Intent(this, Intro_2.class);
        startActivity(intent);
        finish();
    }

    private void checkFirstRun(Context c) {
        SharedPreferences settings = c.getSharedPreferences("com.kaycleaves.unboxyourself", 0);
        String value = settings.getString("First Run Complete", "");
        Log.i("First Run Complete", value);
        if (value.equals("Yes")) { // First run complete
            String selectedMode = settings.getString("tracking_mode", "");
            Log.i("Mode", selectedMode);
            if (selectedMode.equals("Manual")) {
                Log.i("Redirect", "Manual");
                loadManualMain();
            } else if (selectedMode.equals("GPS")) {
                Log.i("Redirect", "GPS");
                loadGPSMain();
            } else if (selectedMode.equals("Wifi")) {
                Log.i("Redirect", "Wifi");
                loadWifiMain();
            } else { // they haven't set a mode yet
                // set their First run back to no, as they aren't done yet.
                Log.i("Redirect", "Reset First Run to No");
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("First Run Complete", "No");
                editor.commit();
            }
        }
    }

    private void loadManualMain() {
        Intent intent = new Intent(this, Manual_Main.class);
        startActivity(intent);
        finish();
    }

    private void loadGPSMain() {
        Intent intent = new Intent(this, GPS_Main.class);
        startActivity(intent);
        finish();
    }

    private void loadWifiMain() {
        Intent intent = new Intent(this, Wifi_Main.class);
        startActivity(intent);
        finish();
    }
}
