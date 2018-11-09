package com.innovativeproposals.inventorypokus2.InventarVMiestnosti;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.innovativeproposals.inventorypokus2.Constants;
import com.innovativeproposals.inventorypokus2.R;

public class addInventarManualne extends AppCompatActivity implements View.OnClickListener {

    //OnHeadlineSelectedListener mCallback;

    EditText newItem;
    Button btNewItem;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
      //  inventar = null;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //   inventar = getIntent().getParcelableExtra(Constants.INTENT_INVENTORY);
            //  origRoomCode = extras.getString("roomcode"); // musim ziskat aj roomcode, pri nasnimani inventara v inej miestnosti sa musi zapisat jej kod
        }

        setContentView(R.layout.add_artikel_manualne);

        newItem = findViewById(R.id.newEAN);

        // newItem.setText("zadajte ean");

        btNewItem = findViewById(R.id.buttonNewArtikelOK);
        btNewItem.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {


        Intent returnIntent = new Intent();
        returnIntent.putExtra("newEAN", newItem.getText().toString());
        setResult(0,returnIntent);

        this.finish();
    }
}
