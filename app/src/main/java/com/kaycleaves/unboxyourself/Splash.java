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

/*
 * This is what displays on first run.
 * Created by Kay Cleaves
 * Encourages users to make a psychological commitment to leaving the house.
 */

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkFirstRun(this);
        //Remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
    }

    // called when use clicks button. Take them to Choose their mode
    public void loadIntro2(View view) {
        Intent intent = new Intent(this, Choose_Mode.class);
        startActivity(intent);
        finish();
    }

    // If this isn't their first run, take them right into the app.
    private void checkFirstRun(Context c) {
        // check for the first run flag in shared prefs
        SharedPreferences settings = c.getSharedPreferences("com.kaycleaves.unboxyourself", 0);
        String value = settings.getString("First Run Complete", "");

        //Log.i("First Run Complete", value);

        if (value.equals("Yes")) { // First run complete
            String selectedMode = settings.getString("tracking_mode", "");
            //Log.i("Mode", selectedMode);
            if (selectedMode.equals("Manual")) {
                //Log.i("Redirect", "Manual");
                loadManualMain();
            } else if (selectedMode.equals("GPS")) {
                //Log.i("Redirect", "GPS");
                loadGPSMain();
            } else if (selectedMode.equals("Wifi")) {
                //Log.i("Redirect", "Wifi");
                loadWifiMain();
            } else { // they haven't set a mode yet
                // set their First run back to no, as they aren't done yet.
                //Log.i("Redirect", "Reset First Run to No");
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
