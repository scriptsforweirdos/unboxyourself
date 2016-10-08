package com.kaycleaves.unboxyourself;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/*
 * GPS tracker
 * Created by Kay Cleaves
 * Location listener built in.
 * Requires permissions request in manifest.
 */

public class GPS_Main extends AppCompatActivity {

    double currentLat;
    double currentLng;
    TextView currentLocationDisplay = (TextView) findViewById(R.id.currentLocation);
    double distanceBetween;
    Location homecoords;
    double homeLat;
    double homeLng;
    private LocationManager lm;
    private MyLocationListener myListener;
    Button pausebutton = (Button)findViewById(R.id.pauseButton);
    public boolean paused = false;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps__main);

        /* our GPS listener requires location permissions. We call the permission check and then
        the listener from within it */
        coordPermissionWrapper();
    }

    public void onStop() {
        /* GPS stuff takes a ton of battery life. If the user kills the app we need to disable
        the listener and let them know that it's been turned off */
        super.onStop();

        // adjust the boolean
        paused = true;
        // turn off the GPS listener
        lm.removeUpdates(myListener);
        // Give feedback to the view.
        pausebutton.setText("Start Tracker");
        currentLocationDisplay.setText("Tracker Paused");
    }

    // menu rendering
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    // menu behavior
    public boolean onOptionsItemSelected(MenuItem item) {
        // respond to menu item selection
        switch(item.getItemId()) {
            case R.id.modeChange:
                startActivity(new Intent(this, Choose_Mode.class));
                return true;
            case R.id.viewArchive:
                startActivity(new Intent(this, Archive.class));
                return true;
            case R.id.addressChange:
                startActivity(new Intent(this, GPS_Configuration.class));
                return true;
            case R.id.networkChange:
                startActivity(new Intent(this, Wifi_Configuration.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    // Get permission to use the GPS
    private void coordPermissionWrapper() {
        /**
         *  This is based on
         *  https://inthecheesefactory.com/blog/things-you-need-to-know-about-android-m-permission-developer-edition/en
         */
        // Do we have fine location permission?
        int hasFineLocPermission = ContextCompat.checkSelfPermission(GPS_Main.this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        // No? Let's ask for it!
        if (hasFineLocPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(GPS_Main.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // pop up a dialog box
                showMessageOKCancel("You need to allow us to access your GPS",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(GPS_Main.this,
                                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_CODE_ASK_PERMISSIONS);
                            }
                        });
                return;
            }
            ActivityCompat.requestPermissions(GPS_Main.this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }

        // check if GPS is on
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) { // not on
            //Log.i("GPS Status", "GPS OFF");
            TextView currentLocationDisplay = (TextView) findViewById(R.id.currentLocation);
            String currentLocation = "Please enable GPS.";
            currentLocationDisplay.setText(currentLocation);
        } else { // GPS is on! Yay! we can track them.
            //Log.i("GPS Status", "GPS ON");
            getCurrentCoords();

        }
    }

    // dialog box display
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(GPS_Main.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    // Ok now let's actually get their coordinates.
    private void getCurrentCoords() {
        Criteria criteria = new Criteria();
        // we're logging when they move at least 80 meters, so let's stick with coarse accuracy.
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setCostAllowed(false);

        // Get the best provider based on criteria
        String provider = lm.getBestProvider(criteria,false);
        // Get the last known location recorded by that provider.
        Location location = lm.getLastKnownLocation(provider);

        // Invoke the location listener
        myListener = new MyLocationListener();

        if (location != null) { // we have a location!
            myListener.onLocationChanged(location); // record movement.
        } else {
            // go to settings because there is no last known location
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        int minTime = 60000 * 10; // time in milliseconds = 10 minute intervals
        //int minTime = 200; // for testing
        int minDistance = 10; // distance in meters
        lm.requestLocationUpdates(provider, minTime, minDistance, myListener);
    }

    // Listener gets updates from GPS
    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            /* This is what happens when someone moves at least 10 meters. */
            if (location != null) {  // they may turn their GPS off so we always have to check.
                // Where are they now?
                currentLat = location.getLatitude();
                //Log.i("CurrentLat", String.valueOf(currentLat));
                currentLng = location.getLongitude();
                //Log.i("CurrentLng", String.valueOf(currentLng));

                // Where are their stored home coordinates?
                homecoords = getHomeCoords("latitude", "longitude");
                homeLat = homecoords.getLatitude();
                //Log.i("Home Lat", String.valueOf(homeLat));
                homeLng = homecoords.getLongitude();
                //Log.i("Home Lng", String.valueOf(homeLng));

                // Output current coords to view.
                showCurrentLocation(currentLat, currentLng);

                // Find out how far away they are from home. calculateDistance outputs meters.
                double distance = calculateDistance(location, homecoords);
                showDistance(distance);
                if (distance > 80) {
                    logOuting();
                }
                // Output distance to view.
                showOuting(distance);

            } else {
                //Log.i("Location error", "Location is null");
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // blank method necessary for listener to function.
        }

        @Override
        public void onProviderEnabled(String provider) {
            // blank method necessary for listener to function.
        }

        @Override
        public void onProviderDisabled(String provider) {
            // blank method necessary for listener to function.
        }
    }

    private void showCurrentLocation(Double lat, Double lng) {
        // format location for nice reading.
        TextView currentLocationDisplay = (TextView) findViewById(R.id.currentLocation);
        String currentLocation = lat + ", " + lng;
        currentLocationDisplay.setText(currentLocation);
    }

    private Location getHomeCoords(String key1, String key2) {
        // retrieves stored home coordinates from shared prefs.
        SharedPreferences settings = getSharedPreferences("com.kaycleaves.unboxyourself", 0);
        String Lat = settings.getString(key1, "");
        String Lng = settings.getString(key2, "");
        Location homelocation = new Location("");
        homelocation.setLatitude(Double.parseDouble(Lat));
        homelocation.setLongitude(Double.parseDouble(Lng));

        return homelocation;

    }

    private double calculateDistance(Location current, Location home) {
        // We do the math! (We do the monster math)
        distanceBetween = current.distanceTo(home);
        return distanceBetween;
    }

    private void showDistance(Double distance) {
        // Gives distance from home in meters and miles
        // distance passed is in meters.
        TextView distanceOut = (TextView) findViewById(R.id.distanceFromHome);
        String output = "";

        // Round to 2 decimal places.
        DecimalFormat df = new DecimalFormat("#.##");
        double distanceMeters = Double.valueOf(df.format(distance));
        // convert to miles
        double miles = distance * 0.0006213712;
        double distanceMiles = Double.valueOf(df.format(miles));

        // Nicer language - should convert this to stored strings for easier translation
        output = "That's " + distanceMeters + " meters (or " + distanceMiles + " miles) from home.";
        distanceOut.setText(output);
    }

    private void logOuting() {
        // store their adventures to the database!
        // get the current date in proper format
        sdf.setTimeZone(TimeZone.getDefault());
        String today = sdf.format(new Date());

        // Database writing
        DatabaseHandler db = new DatabaseHandler(this);
        db.addOuting(new Outing(today, "GPS"));
        // update the display
        Toast.makeText(GPS_Main.this, "Outing saved!", Toast.LENGTH_LONG).show();

    }

    private void showOuting(Double distance) {
        // formatting new outings for view
        TextView v = (TextView)findViewById(R.id.outingResults);
        // get the last outing from the database
        String ld = tail();
        String outingtext = "";

        if (distance > 80) { // we logged an outing.
            outingtext = "Congrats! You're out of the house!";
            v.setText(outingtext);
        } else if (isValidDate(ld)) { // they're not out right now but they've gone out before.
            // calculate difference in dates
            int datediff = calcDateDiff(ld);

            if (datediff == 0) {
                outingtext = "Congrats! You left the house today!";
            } else if (datediff > 0) {
                outingtext = "You last left the house on " + ld + ". That was " + datediff + " days ago.";
            } else { // negative number - they've been messing with the database or their time zone changed drastically
                outingtext = "You last left the house ... tomorrow? Big time zone changes can confuse our tracker.";
            }
            v.setText(outingtext);
        } else { // They've never gone out.
            v.setText("No adventures yet!");
        }
    }

    private boolean isValidDate(String inDate) {
        // This method from http://www.java2s.com/Tutorial/Java/0120__Development/CheckifaStringisavaliddate.htm
        sdf.setLenient(false);
        try {
            sdf.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    private String tail() {
        // Gets the most recent outing in the database
        DatabaseHandler db = new DatabaseHandler(this);
        Outing out = db.getLastOuting();

        String lastout = out.getDate();
        return lastout;
    }

    private int calcDateDiff(String startdate) {
        // Calculates difference between dates
        // this method from http://stackoverflow.com/questions/20165564/calculating-days-between-two-dates-with-in-java
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

    public void pauseTracker(View view) {
        // don't want us snooping for a while? Ok turn it off.
        pausebutton = (Button)findViewById(R.id.pauseButton);

        currentLocationDisplay = (TextView) findViewById(R.id.currentLocation);
        if (paused) { // turn it back on.
            paused = false;
            getCurrentCoords();
            pausebutton.setText("Pause Tracker");
        } else { // turn it off.
            paused = true;
            lm.removeUpdates(myListener);
            pausebutton.setText("Start Tracker");
            currentLocationDisplay.setText("Tracker Paused");
        }
        //Log.i("pause status", String.valueOf(paused));
    }
}
