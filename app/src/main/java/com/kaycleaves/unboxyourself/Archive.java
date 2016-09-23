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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        displayOutings();
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

    private void displayOutings() {
        DatabaseHandler db = new DatabaseHandler(this);
        List<Outing> allOutings = db.getAllOutings();
        TableLayout ll = (TableLayout)findViewById(R.id.archiveTable);

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
        /* uncomment to pad out scrollview
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

    public void exportDB(View view) {
        if (externalStorageReady()) {
            //export to unboxyourself.csv

            DatabaseHandler db = new DatabaseHandler(this);
            List<Outing> allOutings = db.getAllOutings();

            String folder_main = "UnboxYourself Export";

            File exportDir = new File(Environment.getExternalStorageDirectory(), folder_main);
            String exportPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + folder_main + "/unboxyourself.csv";

            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

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
                Toast.makeText(Archive.this, "Archive exported to " + exportPath, Toast.LENGTH_LONG).show();
            } catch (Exception sqlex) {
                Log.e("ExportDB", sqlex.getMessage(), sqlex);
            }
        } else {
            Toast.makeText(Archive.this, "No Writeable External Storage found, could not export!", Toast.LENGTH_LONG).show();
        }
    }

    public boolean externalStorageReady() {
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
