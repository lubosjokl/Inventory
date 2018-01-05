package com.innovativeproposals.inventorypokus2.Miestnosti;

/**
 * Created by Lubos on 26.12.17.
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

import com.innovativeproposals.inventorypokus2.InventarVMiestnosti.ListInventarVMiestnosti;
import com.innovativeproposals.inventorypokus2.R;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;


public class ListMiestnosti extends AppCompatActivity
{
    Intent intent;

    TextView roomcodeET;
    TextView roomdescriptionEF;
    TextView postupET;


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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(extras.getString("description"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ArrayList<HashMap<String, String>> zoznamHM  = null;
        try {
            zoznamHM = dm.dajZaznamy(myKodOddelenia);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if(zoznamHM.size()!=0)
        {
            ListView lw = (ListView)findViewById(R.id.list_miestnosti);
            lw.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                //kliknutie na polozku zoznamu
                public void onItemClick(AdapterView<?> parent,
                                        View view, int position, long id)
                {

                    roomcodeET = (TextView) view.findViewById(R.id.roomcodeET);
                    roomdescriptionEF = (TextView) view.findViewById(R.id.roomdescriptionET);
                    postupET = (TextView) view.findViewById(R.id.postupET);

                    String sKnihaId = roomcodeET.getText().toString();

                    Intent theIndent = new Intent(getApplication(),
                            ListInventarVMiestnosti.class);
                    theIndent.putExtra("roomcode", sKnihaId);
                    theIndent.putExtra("roomdescription", roomdescriptionEF.getText());
                    theIndent.putExtra("PostupSpracovania", postupET.getText());
                    startActivity(theIndent);
                }
            });
            ListAdapter adapter = new SimpleAdapter( ListMiestnosti.this,
                    zoznamHM, R.layout.miestnosti_riadok,
                    new String[] { "roomcode","roomdescription","PostupSpracovania"}, new int[] {R.id.roomcodeET,R.id.roomdescriptionET, R.id.postupET});

            lw.setAdapter(adapter);
        }
    }
}


