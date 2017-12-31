package com.innovativeproposals.inventorypokus2.InventarVMiestnosti;

/**
 * Created by Lubos on 27.12.17.
 */

import android.app.ActivityOptions;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.innovativeproposals.inventorypokus2.InventarDeteil.ViewInventarDetail;
import com.innovativeproposals.inventorypokus2.Miestnosti.ListMiestnosti;
import com.innovativeproposals.inventorypokus2.Models.Inventar;
import com.innovativeproposals.inventorypokus2.R;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ListInventarVMiestnosti extends AppCompatActivity {
    Intent intent;

    TextView itembarcodeET;
    TextView itemdescriptionET;

    TextView statusET;
    TextView datumET;
    List<Inventar> zoznamHM = null;

    DataModelInventarVMiestnosti dm = new DataModelInventarVMiestnosti(this); // pri kopirovani do inej triedy zmen

    //id	,[itembarcode] ,		,[itemdescription]		,[roomcodenew]		,[status]		,[datum]		,[datumDispose]		,[datumREAL], serialnr

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String myRoomcode = "";

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            myRoomcode = extras.getString("roomcode");
        }

        setContentView(R.layout.inventar_vmiestnosti);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(myRoomcode);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            zoznamHM = dm.dajNoveZaznamy(myRoomcode,"");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if (zoznamHM.size() != 0) {
            ListView lw = (ListView) findViewById(R.id.list_inventar_v_miestnosti);
            lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                //kliknutie na polozku zoznamu
                public void onItemClick(AdapterView<?> parent,
                                        View view, int position, long id) {
                    itembarcodeET = (TextView) view.findViewById(R.id.itembarcodeET);
                    itemdescriptionET = (TextView) view.findViewById(R.id.itemdescriptionET);

                    statusET = (TextView) view.findViewById(R.id.statusET);
                    datumET = (TextView) view.findViewById(R.id.datumET);

                    String sKnihaId = itembarcodeET.getText().toString();

                    Inventar inventar = zoznamHM.get(position);

                    Intent theIndent = new Intent(getApplication(),
                            ViewInventarDetail.class);
                    theIndent.putExtra("barcode", sKnihaId);
                    theIndent.putExtra("inventar_object", inventar);

                    View imageView = view.findViewById(R.id.detailView_Image); // ma natvrdo v layoute devinovany src
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            ListInventarVMiestnosti.this, imageView,"detailView_Image");
                    startActivity(theIndent, options.toBundle());
                }
            });
            CustomListInventoryAdapter adapterNew = new CustomListInventoryAdapter(this,R.layout.inventar_vmiestnosti_riadok, zoznamHM);

            lw.setAdapter(adapterNew);
        }
    }
}

