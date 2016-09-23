package com.kaycleaves.unboxyourself;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class Intro_3_Manual extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_intro_3__manual);
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
