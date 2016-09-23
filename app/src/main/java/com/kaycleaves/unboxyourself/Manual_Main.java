package com.kaycleaves.unboxyourself;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


public class Manual_Main extends AppCompatActivity {
    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual__main);


        // find out when they last left the house
        String lastdate = tail();

        showLastOuting(lastdate);
    }

    public void onResume() {
        super.onResume();
        String lastdate = tail();
        showLastOuting(lastdate);
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

    private void showLastOuting(String ld) {
        TextView v = (TextView)findViewById(R.id.lastOuting);
        if (isValidDate(ld)) {
            // calculate difference in dates
            int datediff = calcDateDiff(ld);
            String output = "";
            if (datediff == 0) {
                output = "Congrats! You left the house today!";
            } else if (datediff > 0) {
                output = "You last left the house on " + ld + ". That was " + datediff + " days ago.";
            } else { // negative number - they've been messing with the database or their time zone changed drastically
                output = "You last left the house ... tomorrow? Big time zone changes can confuse our tracker.";
            }
            v.setText(output);
        } else {
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

    private int calcDateDiff(String startdate) {
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

    public void logOuting(View view) {
        // get the current date
        sdf.setTimeZone(TimeZone.getDefault());
        String today = sdf.format(new Date());

        /* old flatfile code
        // append it to the end of the log file
        String FILENAME = "Unbox Yourself Log.txt";
        String newlog = "\n\r" + today;
        FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_APPEND);
        fos.write(newlog.getBytes());
        fos.close();
        */
        DatabaseHandler db = new DatabaseHandler(this);
        db.addOuting(new Outing(today, "Manual"));
        showLastOuting(today);
        // update the display
        Toast.makeText(Manual_Main.this, "Outing saved!", Toast.LENGTH_LONG).show();
    }

    private String tail() {
        DatabaseHandler db = new DatabaseHandler(this);
        Outing out = db.getLastOuting();

        String lastout = out.getDate();
        return lastout;
    }
}
