package com.kaycleaves.unboxyourself;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class Archive extends AppCompatActivity {
/*
 * This class displays all of the outings stored in the database.
 */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);
        // show the outings
        displayOutings();
    }

    // menu display
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

    private void displayOutings() {
        // open the database
        DatabaseHandler db = new DatabaseHandler(this);

        // retrieve all outings
        List<Outing> allOutings = db.getAllOutings();

        // make a table
        TableLayout ll = (TableLayout)findViewById(R.id.archiveTable);

        // step through the database to make table rows for each outing.
        // Each row will have 3 cells: ID, date and mode.
        for (Outing outing:allOutings) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            row.setBackgroundResource(R.drawable.table_row);

            TextView idcell = new TextView(this);
            TextView datecell = new TextView(this);
            TextView modecell = new TextView(this);

            idcell.setPadding(5, 5, 5, 5);
            datecell.setPadding(5, 5, 5, 5);
            modecell.setPadding(5, 5, 5, 5);

            idcell.setTextSize(14);
            datecell.setTextSize(14);
            modecell.setTextSize(14);

            idcell.setGravity(Gravity.CENTER_HORIZONTAL);
            modecell.setGravity(Gravity.RIGHT);

            idcell.setText(String.valueOf(outing.getID()));
            datecell.setText(outing.getDate());
            modecell.setText(outing.getMode());

            row.addView(idcell);
            row.addView(datecell);
            row.addView(modecell);
            ll.addView(row);
        }
        /* uncomment to pad out scrollview with lipsum for testing layout
        for (int i = 0; i < 200; i++) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            row.setBackgroundResource(R.drawable.table_row);

            TextView idcell = new TextView(this);
            TextView datecell = new TextView(this);
            TextView modecell = new TextView(this);

            idcell.setPadding(5, 5, 5, 5);
            datecell.setPadding(5, 5, 5, 5);
            modecell.setPadding(5, 5, 5, 5);

            idcell.setTextSize(14);
            datecell.setTextSize(14);
            modecell.setTextSize(14);

            idcell.setGravity(Gravity.CENTER_HORIZONTAL);
            modecell.setGravity(Gravity.RIGHT);

            idcell.setText(String.valueOf(i));
            datecell.setText("Lorem");
            modecell.setText("Ipsum");

            row.addView(idcell);
            row.addView(datecell);
            row.addView(modecell);
            ll.addView(row);
        }
        */
    }

    // export database to text file "unboxyourself.csv"
    public void exportDB(View view) {
        // must have external storage!
        if (externalStorageReady()) {
            // connect to the database and retrieve all the outings
            DatabaseHandler db = new DatabaseHandler(this);
            List<Outing> allOutings = db.getAllOutings();
            // make a directory in external storage
            String folder_main = "UnboxYourself Export";
            File exportDir = new File(Environment.getExternalStorageDirectory(), folder_main);

            // make the full path where we will store our file
            String exportPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + folder_main + "/unboxyourself.csv";

            // make the directory if it doesn't already exist.
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            // create and write the file.
            File file = new File(exportDir, "unboxyourself.csv");
            try {
                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                String arrStr[] = {"ID", "Date", "Mode"};
                csvWrite.writeNext(arrStr);
                for (Outing outing : allOutings) {
                    String dataStr[] = {String.valueOf(outing.getID()), outing.getDate(), outing.getMode()};
                    csvWrite.writeNext(dataStr);
                }
                csvWrite.close();
                // Show a confirmation.
                Toast.makeText(Archive.this, "Archive exported to " + exportPath, Toast.LENGTH_LONG).show();
            } catch (Exception sqlex) {
                //Log.e("ExportDB", sqlex.getMessage(), sqlex);
            }
        } else {
            // if there's no storage
            Toast.makeText(Archive.this, "No Writeable External Storage found, could not export!", Toast.LENGTH_LONG).show();
        }
    }

    public boolean externalStorageReady() {
        // Checks if external storage is installed and writable.
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // we can read and write
            return true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return false;
        } else {
            return false;
        }
    }
}
