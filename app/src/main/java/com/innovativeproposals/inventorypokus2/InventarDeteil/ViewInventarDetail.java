package com.innovativeproposals.inventorypokus2.InventarDeteil;

/**
 * Created by Lubos on 28.12.17.
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.innovativeproposals.inventorypokus2.Models.Inventar;
import com.innovativeproposals.inventorypokus2.R;

import java.io.ByteArrayInputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ViewInventarDetail extends AppCompatActivity
{
    Intent intent;

    TextView itembarcodeET;
    TextView itemdescriptionET;
    TextView statusET;
    TextView datumET;
    TextView datumDisposeET;
    TextView datumREALET;
    //TextView serialnrET;

    //id	,[itembarcode] ,		,[itemdescription]		,[roomcodenew]		,[status]		,[datum]		,[datumDispose]		,[datumREAL], serialnr

    DataModelInventarDetail dm = new DataModelInventarDetail(this); // pri kopirovani do inej triedy zmen


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.inventar_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        String isValue = null;
        Inventar inventar = null;

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            inventar = getIntent().getParcelableExtra("inventar_object");
        }

        setContentView(R.layout.inventar_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar.setTitle("test");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView myImage = findViewById(R.id.detailView_Image);

        if(inventar.getImage() != null && inventar.getImage().length > 1) {
            ByteArrayInputStream imageStream = new ByteArrayInputStream(inventar.getImage());
            Bitmap theImage = BitmapFactory.decodeStream(imageStream);
            myImage.setImageBitmap(theImage);
        }

        TextView barcode = findViewById(R.id.barcodeET);
        barcode.setText(inventar.getItemBarcode());

        isValue = inventar.getItemDescription();
        TextView description = findViewById(R.id.descriptionET);
        if(isValue!=null)
            description.setText(isValue);

        isValue = inventar.getDatum();
        TextView lastInventory = findViewById(R.id.lastInventory);
        if(isValue!=null)
            lastInventory.setText(isValue);


        isValue = inventar.getPoznamka();
        TextView notice = findViewById(R.id.notice);
        if(isValue!=null)
            notice.setText(isValue);


        isValue = inventar.getSerialNr();
        TextView txt_SerialNr = findViewById(R.id.txt_SerialNr);
        if(isValue!=null)
            txt_SerialNr.setText(isValue);

        isValue = inventar.getDatum_added();
        TextView txt_Added = findViewById(R.id.txt_Added);
        if(isValue!=null)
            txt_Added.setText(isValue);

        isValue = inventar.getDatum_discarded();
        TextView txt_Discarded = findViewById(R.id.txt_Discarded);
        if(isValue!=null)
            txt_Discarded.setText(isValue);

        isValue = inventar.getPrice();
        TextView priceET = findViewById(R.id.priceET);
        if(isValue!=null) {
            //String abc = "Cena :" + isValue + " EUR";
            priceET.setText(getString(R.string.Price) + isValue + " EUR");
        }

/*

        dest.writeString(rommCode);
        dest.writeString(status);
        dest.writeString(zodpovednaOsoba);

 */

        String tempSpinnerTyp = "neznamy";
        isValue = inventar.getTypeMajetku();
        if(isValue!=null)
            tempSpinnerTyp = isValue;

        Spinner spinner_InventoryType = findViewById(R.id.spinner_InventoryType);
        Spinner spinner_Responsible = findViewById(R.id.spinner_responsible);
        List<String> spinnerList = new ArrayList<String>(); // nacitaj dynamicky spinner z databazy
        spinnerList.add(tempSpinnerTyp);
        spinnerList.add("Polozka 2");
        spinnerList.add("Polozka 3");

        // pridaj dalsi spinner na zodpovednu osobu

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, spinnerList);
        adapter .setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spinner_InventoryType.setAdapter(adapter);
        spinner_Responsible.setAdapter(adapter);


    }
}
