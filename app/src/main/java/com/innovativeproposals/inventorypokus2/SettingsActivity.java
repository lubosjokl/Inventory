package com.innovativeproposals.inventorypokus2;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity {

    private EditText etServerAddress,etServerPort, etDayesOfInventory;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.inventar_detail_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etServerAddress = findViewById(R.id.etServerAddress);
        etServerPort = findViewById(R.id.etServerPort);
        etDayesOfInventory = findViewById(R.id.etDaysOfInventory);

    }

    public void saveAccountData(View view) {

        // Globalne SharedPreferences - obsahuje meno balika
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName()+Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);

        // ziskam odkaz na editor
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // zapis hodnot
        editor.putString(Constants.KEY_ADDRESS,etServerAddress.getText().toString());
        editor.putString(Constants.KEY_PORT,etServerPort.getText().toString());
        editor.putInt(Constants.KEY_DAYS_OF_INVENTORY,5); // defaultna hodnota
        // uloz Asynchronne
        editor.apply(); // moze sa pouzit editor.commit(), ale ta vracia boolean hodnotu a uklada Synchronne
    }

    public void loadAccountData(View view) {

        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName()+Constants.PREF_FILE_NAME,MODE_PRIVATE);

        String address = sharedPreferences.getString(Constants.KEY_ADDRESS,"http://192.168.1.1"); // druhy parameter je defaultna hodnota
        String port = sharedPreferences.getString(Constants.KEY_PORT,"11235");
        int daysOfInventory = sharedPreferences.getInt(Constants.KEY_DAYS_OF_INVENTORY,5);

        etServerAddress.setText(address);
        etServerPort.setText(port);
        etDayesOfInventory.setText(daysOfInventory);

    }

}
