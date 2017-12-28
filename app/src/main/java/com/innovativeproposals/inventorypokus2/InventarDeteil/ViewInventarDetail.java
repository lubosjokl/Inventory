package com.innovativeproposals.inventorypokus2.InventarDeteil;

/**
 * Created by Lubos on 28.12.17.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.innovativeproposals.inventorypokus2.R;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;

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

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        String myBarcode = "";

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            myBarcode= extras.getString("barcode");
        }

        setContentView(R.layout.inventar_detail);

        TextView textView = findViewById(R.id.barcodeET);
        textView.setText(myBarcode);

        ArrayList zoznamHM  = null;
        try {
            zoznamHM = dm.dajZaznamy(myBarcode);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // data nacitaj z classu
        if(zoznamHM.size()!=0)
        {




            /*
                            hm.put("id", cursor.getString(0));
                hm.put("itembarcode", cursor.getString(1));
                hm.put("itemdescription", cursor.getString(2));
                hm.put("roomcodenew", cursor.getString(3));
                hm.put("status", cursor.getString(4));
                hm.put("datum", cursor.getString(5));
                hm.put("datumDispose", cursor.getString(6));
                hm.put("datumREAL", cursor.getString(7));
                hm.put("serialnr", cursor.getString(8));
                hm.put("zodpovednaosoba", cursor.getString(9));
                hm.put("datumzaradenia", cursor.getString(10));
                hm.put("extranotice", cursor.getString(11));
                hm.put("typmajetku", cursor.getString(12));

             */


            //EditText editText1 = findViewById(R.id.descriptionET);
            //editText1.setText(zoznamHM.hashCode());



       /*     ListView lw = getListView();
            lw.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                //kliknutie na polozku zoznamu
                public void onItemClick(AdapterView<?> parent,
                                        View view, int position, long id)
                {
                    itembarcodeET = (TextView) view.findViewById(R.id.itembarcodeET);
                    itemdescriptionET = (TextView) view.findViewById(R.id.itemdescriptionET);

                    statusET = (TextView) view.findViewById(R.id.statusET);
                    datumET = (TextView) view.findViewById(R.id.datumET);

                    String sKnihaId = itembarcodeET.getText().toString();

                    Intent theIndent = new Intent(getApplication(),
                            ListMiestnosti.class);
                    theIndent.putExtra("kododdelenia", sKnihaId);
                    startActivity(theIndent);
                }
            });
            ListAdapter adapter = new SimpleAdapter( ListInventarVMiestnosti.this,
                    zoznamHM, R.layout.inventar_vmiestnosti_riadok,
                    new String[] { "itembarcode","itemdescription","status","datum"}, new int[] {R.id.itembarcodeET,R.id.itemdescriptionET,
                    R.id.statusET,R.id.datumET});

            setListAdapter(adapter);*/
        }
    }
}
