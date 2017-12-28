package com.innovativeproposals.inventorypokus2.Miestnosti;

/**
 * Created by Lubos on 26.12.17.
 */

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.innovativeproposals.inventorypokus2.InventarVMiestnosti.ListInventarVMiestnosti;
import com.innovativeproposals.inventorypokus2.R;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;


public class ListMiestnosti extends ListActivity
{
    Intent intent;

    TextView roomcodeET;
    TextView roomdescriptionEF;


    DataModelMiestnosti dm = new DataModelMiestnosti(this); // pri kopirovani do inej triedy zmen

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        String myKodOddelenia = "";

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            myKodOddelenia= extras.getString("kododdelenia");
        }

        setContentView(R.layout.miestnosti);

       // TextView textView = findViewById(R.id.poschodieET);
       // textView.setText(myDivizia);


        ArrayList<HashMap<String, String>> zoznamHM  = null;
        try {
            zoznamHM = dm.dajZaznamy(myKodOddelenia);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if(zoznamHM.size()!=0)
        {
            ListView lw = getListView();
            lw.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                //kliknutie na polozku zoznamu
                public void onItemClick(AdapterView<?> parent,
                                        View view, int position, long id)
                {

                    roomcodeET = (TextView) view.findViewById(R.id.roomcodeET);
                    roomdescriptionEF = (TextView) view.findViewById(R.id.roomdescriptionET);

                    String sKnihaId = roomcodeET.getText().toString();

                    // pokracuj ListInventarVMiestnosti

                    Intent theIndent = new Intent(getApplication(),
                            ListInventarVMiestnosti.class);
                    theIndent.putExtra("roomcode", sKnihaId);
                    startActivity(theIndent);
                }
            });
            ListAdapter adapter = new SimpleAdapter( ListMiestnosti.this,
                    zoznamHM, R.layout.miestnosti_riadok,
                    //        new String[] { "_id","divizia","oddelenie","kododdelenia"}, new int[] {R.id.divizaId,
                    //      R.id.diviziaET,R.id.poschodieET,R.id.kodDiviziaET});
                    new String[] { "roomcode","roomdescription"}, new int[] {R.id.roomcodeET,R.id.roomdescriptionET});

            setListAdapter(adapter);
        }
    }
}


