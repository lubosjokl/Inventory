package com.innovativeproposals.inventorypokus2.InventarVMiestnosti;

/**
 * Created by Lubos on 27.12.17.
 */

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.innovativeproposals.inventorypokus2.Miestnosti.ListMiestnosti;
import com.innovativeproposals.inventorypokus2.R;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;


public class ListInventarVMiestnosti extends AppCompatActivity {
    Intent intent;

    TextView itembarcodeET;
    TextView itemdescriptionET;

    TextView statusET;
    TextView datumET;
    TextView datumDisposeET;
    TextView datumREALET;
    //TextView serialnrET;

    //id	,[itembarcode] ,		,[itemdescription]		,[roomcodenew]		,[status]		,[datum]		,[datumDispose]		,[datumREAL], serialnr

    DataModelInventarVMiestnosti dm = new DataModelInventarVMiestnosti(this); // pri kopirovani do inej triedy zmen

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String myRoomcode = "";
        String title_name = "";

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            myRoomcode = extras.getString("roomcode");
            title_name = extras.getString("roomdescription");
        }

        setContentView(R.layout.inventar_vmiestnosti);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title_name);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        TextView textView = findViewById(R.id.miestnostiET);
//        textView.setText(myRoomcode);


        ArrayList<HashMap<String, String>> zoznamHM = null;
        try {
            zoznamHM = dm.dajZaznamy(myRoomcode);
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
                    // serialnrET = (TextView) view.findViewById(R.id.serialnrET);

                    String sKnihaId = itembarcodeET.getText().toString();

                    Intent theIndent = new Intent(getApplication(),
                            ListMiestnosti.class);
                    theIndent.putExtra("kododdelenia", sKnihaId);
                    startActivity(theIndent);
                }
            });
            ListAdapter adapter = new SimpleAdapter(ListInventarVMiestnosti.this,
                    zoznamHM, R.layout.inventar_vmiestnosti_riadok,
                    new String[]{"itembarcode", "itemdescription", "status", "datum"}, new int[]{R.id.itembarcodeET, R.id.itemdescriptionET,
                    R.id.statusET, R.id.datumET});

            lw.setAdapter(adapter);
        }
    }
}
