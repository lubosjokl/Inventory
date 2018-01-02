package com.innovativeproposals.inventorypokus2.InventarDetail;

/**
 * Created by Lubos on 28.12.17.
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
        if(isValue!=null && isValue.length()>0) {
            // tento datum je string, retazec zobrazim po medzeru, cize bez casu, ktory tam vsak nemusi byt
            int kde = isValue.indexOf(" ");
            if(kde > 0) {
                isValue = isValue.substring(0, kde);
                lastInventory.setText(isValue);
            } else
                lastInventory.setText(isValue);

        }
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
        if(isValue!=null && isValue.length()>0) {
            // tento datum je string, retazec zobrazim po medzeru, cize bez casu
            int kde = isValue.indexOf(" ");
            if(kde > 0) {
                isValue = isValue.substring(0, kde);
                txt_Added.setText(isValue);
            }
        } else
            txt_Added.setText(null);

        isValue = inventar.getDatum_discarded();
        TextView txt_Discarded = findViewById(R.id.txt_Discarded);
        if(isValue!=null && isValue.length()>0) {
            // tento datum je string, retazec zobrazim po medzeru, cize bez casu
            int kde = isValue.indexOf(" ");
            isValue = isValue.substring(0, kde);
            txt_Discarded.setText(isValue);
        } else
            txt_Discarded.setText(null);

        isValue = inventar.getPrice();
        TextView priceET = findViewById(R.id.priceET);
        if(isValue!=null) {
            //String abc = "Cena :" + isValue + " EUR";
            priceET.setText(getString(R.string.Price) + isValue + " EUR");
        }

        String myLokacia = null;
        TextView location = findViewById(R.id.location);
        isValue = inventar.getRommCode();
        if(isValue!=null) {
            myLokacia = dm.dajNazovLokality(isValue);
            location.setText(myLokacia);
        }
        // spinner na typy majetku
        String tempSpinnerTyp = null;
        isValue = inventar.getTypeMajetku();
        if(isValue!=null)
            tempSpinnerTyp = isValue;

        Spinner spinner_InventoryType = findViewById(R.id.spinner_InventoryType);
        List<String> spinnerListTypyMajetku = new ArrayList<String>(); // nacitaj dynamicky spinner z databazy

        try {
            spinnerListTypyMajetku = dm.dajTypyMajetku();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        ArrayAdapter<String> adapterTypyMajetku = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, spinnerListTypyMajetku);
        adapterTypyMajetku .setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spinner_InventoryType.setAdapter(adapterTypyMajetku);
        // nastav spinner ak ma hodnotu
        if(tempSpinnerTyp != null)
            spinner_InventoryType.setSelection(adapterTypyMajetku.getPosition(tempSpinnerTyp));

        // spinner na zodpovednu osobu
        String tempZodpovednaOsoba = null;
        isValue = inventar.getZodpovednaOsoba();
        if(isValue!=null)
            tempZodpovednaOsoba = isValue;

        Spinner spinner_Responsible = findViewById(R.id.spinner_responsible);
        List<String> spinnerZodpovedneOsoby = new ArrayList<String>(); // nacitaj dynamicky spinner z databazy

        try {
            spinnerZodpovedneOsoby = dm.dajZodpovedneOsoby();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adapterZodpovedneOsoby = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, spinnerZodpovedneOsoby);
        adapterZodpovedneOsoby .setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spinner_Responsible.setAdapter(adapterZodpovedneOsoby);
        // nastav spinner ak ma hodnotu
        if(tempZodpovednaOsoba != null)
            spinner_Responsible.setSelection(adapterZodpovedneOsoby.getPosition(tempZodpovednaOsoba));



    }
}
