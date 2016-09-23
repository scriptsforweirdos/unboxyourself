package com.kaycleaves.unboxyourself;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Handler;


public class Intro_3_GPS extends AppCompatActivity {

    Button addressButton;
    Button startButton;
    TextView addressTV;
    TextView latLongTV;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_intro_3__gps);

        // disable the start button until location is saved.
        startButton = (Button)findViewById(R.id.startGPS);
        startButton.setEnabled(false);

        String selectedMode = getMode(this, "tracking_mode");
        displayMode(selectedMode);

        addressTV = (TextView) findViewById(R.id.addressTV);
        latLongTV = (TextView) findViewById(R.id.latLongTV);

        addressButton = (Button) findViewById(R.id.addressButton);
        addressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                // hide the keyboard
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

                EditText editText = (EditText) findViewById(R.id.addressET);
                String address = editText.getText().toString();
                // geocode it
                GeocodingLocation locationAddress = new GeocodingLocation();
                locationAddress.getAddressFromLocation(address,
                        getApplicationContext(), new GeocoderHandler());
            }
        });


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

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            latLongTV.setText(locationAddress);
            startButton.setEnabled(true);
        }
    }

    public void loadGPSMain(View view) {

        // store the fact that they finished the intro
        SharedPreferences sharedPref = this.getSharedPreferences("com.kaycleaves.unboxyourself",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("First Run Complete", "Yes");
        editor.commit();

        // Move them to the main manual screen
        Intent intent = new Intent(this, GPS_Main.class);
        startActivity(intent);
        finish();
    }

}
