/**
 * Created by Kay Cleaves on 9/13/2016.
 * Used by GPS configuration to determine home coordinates.
 * NOT used by the main GPS function.
 */

package com.kaycleaves.unboxyourself;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeocodingLocation  {
    private static final String TAG = "GeocodingLocation";

    public static void getAddressFromLocation(final String locationAddress,
                                              final Context context, final Handler handler) {
        // use geo data to obtain their address
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    // get a list of all the data associated with this address
                    List addressList = geocoder.getFromLocationName(locationAddress, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = (Address) addressList.get(0);

                        // make a string of their lat & lng for output to screen.
                        StringBuilder sb = new StringBuilder();
                        sb.append(address.getLatitude()).append("\n");
                        sb.append(address.getLongitude()).append("\n");
                        result = sb.toString();

                        // store lat & lng to their prefs
                        SharedPreferences sharedPref = context.getSharedPreferences("com.kaycleaves.unboxyourself",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("latitude", String.valueOf(address.getLatitude()));
                        editor.putString("longitude", String.valueOf(address.getLongitude()));
                        editor.commit();

                    }
                } catch (IOException e) {
                    //Log.e(TAG, "Unable to connect to Geocoder", e);
                } finally {
                    // craft a message to send back to the view.
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) { // we have data!
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Latitude and Longitude:" + result + " has been saved!";
                        bundle.putString("address", result);
                        message.setData(bundle);
                    } else { // no data! uh oh!
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Address: " + locationAddress +
                                "\n Unable to get Latitude and Longitude for this address location.";
                        bundle.putString("address", result);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }
}
