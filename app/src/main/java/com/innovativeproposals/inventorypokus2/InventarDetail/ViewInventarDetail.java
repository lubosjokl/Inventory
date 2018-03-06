package com.innovativeproposals.inventorypokus2.InventarDetail;

/**
 * Created by Lubos on 28.12.17.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.innovativeproposals.inventorypokus2.Constants;
import com.innovativeproposals.inventorypokus2.InventarVMiestnosti.ListInventarVMiestnosti;
import com.innovativeproposals.inventorypokus2.Models.Inventar;
import com.innovativeproposals.inventorypokus2.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class ViewInventarDetail extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1;

    String origRoomCode;
    Inventar inventar;

    TextView itemdescriptionET;
    Spinner spinner_inventoryType;
    Spinner spinner_responsible;
    EditText txt_Notice;
    EditText txt_SerialNr;
    FloatingActionButton fab_takePhoto; // tlacitko na fotenie
    ImageView detailView_Image;
    AppBarLayout appBarLayout;

    DataModelInventarDetail dm = new DataModelInventarDetail(this); // pri kopirovani do inej triedy zmen co?

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //toto potrebujeme, inac nebudeme mat save button
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

            case R.id.menu_item_save:

                try {
                  //  Object aa = itemdescriptionET.getText();
                    String xx = "" + itemdescriptionET.getText();
                    if (xx.equals("")) {
                        Toast.makeText(this, R.string.meno_musi_byt_vyplnene, Toast.LENGTH_LONG).show();
                        return false;

                    }
                }
                catch (Exception e) {
                    Toast.makeText(this, R.string.meno_musi_byt_vyplnene, Toast.LENGTH_LONG).show();
                    return false;
                }

                //serializuj data spat
                inventar.setItemDescription(itemdescriptionET.getText().toString());
                inventar.setTypeMajetku(spinner_inventoryType.getSelectedItem().toString());
                inventar.setPoznamka(txt_Notice.getText().toString());
                inventar.setSerialNr(txt_SerialNr.getText().toString());
                inventar.setStatus("10");
                inventar.setZodpovednaOsoba(spinner_responsible.getSelectedItem().toString());
                inventar.setImage(getImageBytesFromImageView());

/* ********************************************************************* TEST
                testovanie inventara
                pozitivny (exitujuci kod v databaze)
                        - z info scan ok
                    - z miestnosti - dotyk ok
                    - scan z miestnosti ok

                negativny (neexistujuci barcode)
                scan z miestnosti ( novy )
                scan z info ok
*/


                //tu uloz nove data do databazy
                dm.ulozInventar(inventar, origRoomCode);

                if(inventar.getImage() != null)
                    dm.ulozObrazok(inventar);

                //ukonci aktualnu aktivitu
                Intent returnIntent = new Intent();
                returnIntent.putExtra(Constants.INTENT_INVENTORY, inventar);

                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private byte[] getImageBytesFromImageView() {
        try {
            BitmapDrawable drawable = (BitmapDrawable) detailView_Image.getDrawable();

            if(drawable==null)
                return null; // neexistuje obrazok

            Bitmap bitmap = drawable.getBitmap();
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            //tu vieme zmenit kvalitu obrazku
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
            return output.toByteArray();
        } catch (Exception ex) {
            Log.d("error",ex.toString());
            return null;
        }
    }

    private void locateControls() {

        itemdescriptionET = findViewById(R.id.descriptionET);
        spinner_inventoryType = findViewById(R.id.spinner_InventoryType);
        txt_Notice = findViewById(R.id.notice);
        txt_SerialNr = findViewById(R.id.txt_SerialNr);
        spinner_responsible = findViewById(R.id.spinner_responsible);
        fab_takePhoto = findViewById(R.id.fab_inventar_detail_take_picture);
        detailView_Image = findViewById(R.id.detailView_Image);
        appBarLayout = findViewById(R.id.appBar  );
    }

    @Override
    // foto
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data); // vraj treba

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Log.d("CameraDemo", "Pic saved");
            detailView_Image.setImageBitmap(photo);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        String isValue = "";
        inventar = null;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            inventar = getIntent().getParcelableExtra(Constants.INTENT_INVENTORY);
            origRoomCode = extras.getString("roomcode"); // musim ziskat aj roomcode, pri nasnimani inventara v inej miestnosti sa musi zapisat jej kod
        }

        setContentView(R.layout.inventar_detail);
        locateControls(); // priradi premennym ekvivalenty z layoutu

        //on click listener for take camera
        fab_takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // foto
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (inventar.getImage() != null && inventar.getImage().length > 1) {
            detailView_Image.setVisibility(View.VISIBLE); // VISIBLE, INVISIBLE
            ByteArrayInputStream imageStream = new ByteArrayInputStream(inventar.getImage());
            Bitmap theImage = BitmapFactory.decodeStream(imageStream);
            detailView_Image.setImageBitmap(theImage); // bolo myImage
        } else {
            // appBarLayout.setVisibility(View.INVISIBLE);

            // zmensim vysku pokial nie je obrazok
            //float heightDp = getResources().getDisplayMetrics().heightPixels / 3;
            float heightDp = 170; // natvrdo aby sa zmestila aj ikona fotoaparata
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)appBarLayout.getLayoutParams();
            lp.height = (int)heightDp;

        }

        TextView barcode = findViewById(R.id.barcodeET);
        barcode.setText(inventar.getItemBarcode());

        isValue = inventar.getItemDescription();
        TextView description = findViewById(R.id.descriptionET);
        if (isValue != null)
            description.setText(isValue);

        isValue = inventar.getDatum();
        TextView lastInventory = findViewById(R.id.lastInventory);
        if (isValue != null && isValue.length() > 0) {
            // tento datum je string, retazec zobrazim po medzeru, cize bez casu, ktory tam vsak nemusi byt
            int kde = isValue.indexOf(" ");
            if (kde > 0) {
                isValue = isValue.substring(0, kde);
                lastInventory.setText(isValue);
            } else
                lastInventory.setText(isValue);
        }

        isValue = inventar.getPoznamka();
        if (isValue != null)
            txt_Notice.setText(isValue);

        isValue = inventar.getSerialNr();
        if (isValue != null)
            txt_SerialNr.setText(isValue);

        isValue = inventar.getDatum_added(); // zaradeny
        TextView txt_Added = findViewById(R.id.txt_Added);
        if (isValue != null && isValue.length() > 0) {
            // tento datum je string, retazec zobrazim po medzeru, cize bez casu
            int kde = isValue.indexOf(" ");
            if (kde > 0) {
                isValue = isValue.substring(0, kde);
                txt_Added.setText(isValue + " / ");
            }
        } else
            txt_Added.setText(" / ");

        isValue = inventar.getDatum_discarded(); // vyradeny
        TextView txt_Discarded = findViewById(R.id.txt_Discarded);
        if (isValue != null && isValue.length() > 0) {
            // tento datum je string, retazec zobrazim po medzeru, cize bez casu
            int kde = isValue.indexOf(" ");
            isValue = isValue.substring(0, kde);
            txt_Discarded.setText(isValue);
        } else
            txt_Discarded.setText(null);

        isValue = inventar.getPrice();
        TextView priceET = findViewById(R.id.priceET);
        if (isValue != null) {
            priceET.setText(getString(R.string.Price) + " " + isValue + " EUR");
        }

        String myLokacia = null;
        TextView location = findViewById(R.id.location);
        isValue = inventar.getRommCode();
        if (isValue != null) {
            myLokacia = dm.dajNazovLokality(isValue);
            location.setText(myLokacia);
        }
        // spinner na typy majetku
        String tempSpinnerTyp = null;
        isValue = inventar.getTypeMajetku();
        if (isValue != null)
            tempSpinnerTyp = isValue;

        /*
        List<String> spinnerListTypyMajetku = new ArrayList<String>(); // nacitaj dynamicky spinner z databazy
        spinnerListTypyMajetku = dm.dajTypyMajetku();
        ArrayAdapter<String> adapterTypyMajetku = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, spinnerListTypyMajetku);*/

        ArrayAdapter<String> adapterTypyMajetku = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, Constants.spinnerListTypyMajetku);
        adapterTypyMajetku.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spinner_inventoryType.setAdapter(adapterTypyMajetku);
        // nastav spinner ak ma hodnotu
        if (tempSpinnerTyp != null)
            spinner_inventoryType.setSelection(adapterTypyMajetku.getPosition(tempSpinnerTyp));

        // spinner na zodpovednu osobu
        String tempZodpovednaOsoba = null;
        isValue = inventar.getZodpovednaOsoba();
        if (isValue != null)
            tempZodpovednaOsoba = isValue;
        /*
        List<String> spinnerZodpovedneOsoby = new ArrayList<String>(); // nacitaj dynamicky spinner z databazy
        spinnerZodpovedneOsoby = dm.dajZodpovedneOsoby();
        ArrayAdapter<String> adapterZodpovedneOsoby = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, spinnerZodpovedneOsoby);
        */

        ArrayAdapter<String> adapterZodpovedneOsoby = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, Constants.spinnerZodpovedneOsoby);
        adapterZodpovedneOsoby.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spinner_responsible.setAdapter(adapterZodpovedneOsoby);
        // nastav spinner ak ma hodnotu
        if (tempZodpovednaOsoba != null)
            spinner_responsible.setSelection(adapterZodpovedneOsoby.getPosition(tempZodpovednaOsoba));

        // ukrytie klavesnice
        CoordinatorLayout layout = (CoordinatorLayout) findViewById(R.id.layout_detail);
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                hideKeyboard(view);
                return false;
            }
        });

    }

/*
    public boolean onTouch(View v, MotionEvent event) {
        // scrolovanie pro EditBoxoch
        v.getParent().getParent().requestDisallowInterceptTouchEvent(true);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                v.getParent().getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return false;
    }
*/

    protected void hideKeyboard(View view)
    {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
