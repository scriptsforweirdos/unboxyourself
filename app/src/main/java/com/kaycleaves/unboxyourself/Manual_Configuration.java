package com.kaycleaves.unboxyourself;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import java.io.IOException;
/*
 * Manual configuration
 * Created by Kay Cleaves
 * This really doesn't do anything but confirm that they've chosen Manual Mode and allows them to
 * start the app. It's included more for UX consistency, as the other two modes have more
 * substantial configuration required.
 */

public class Manual_Configuration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_manual_configuration);
    }

    public void loadManualMain(View view) throws IOException {

        // store the fact that they finished the intro
        SharedPreferences sharedPref = this.getSharedPreferences("com.kaycleaves.unboxyourself",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("First Run Complete", "Yes");
        editor.commit();


        // Move them to the main manual screen
        Intent intent = new Intent(this, Manual_Main.class);
        startActivity(intent);
        finish();
    }


}
