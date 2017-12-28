package com.innovativeproposals.inventorypokus2.Poschodie;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
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


/**
 * Created by Lubos on 26.12.17.
 */

/*
SELECT [Id]         ,[divizia]        ,[oddelenie]        ,[kododdelenia]        FROM [oddelenie] */

public class ListPoschodie extends ListActivity
{
    Intent intent;
    TextView poschodieID;
    TextView diviziaET;
    TextView poschodieET;
    TextView KodOddeleniaET;

    DataModelPoschodie dm = new DataModelPoschodie(this); // pri kopirovani do inej triedy zmen

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        String myDivizia = "";

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            myDivizia= extras.getString("diviziaET");
        }

        setContentView(R.layout.poschodie);

        TextView textView = findViewById(R.id.poschodieET);
        textView.setText(myDivizia);


        ArrayList<HashMap<String, String>> zoznamHM  = null;
        try {
            zoznamHM = dm.dajZaznamy(myDivizia);
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
                  //  poschodieID = (TextView) view.findViewById(R.id.poschodieID);
                    diviziaET = (TextView) view.findViewById(R.id.diviziaET);
                    poschodieET = (TextView) view.findViewById(R.id.poschodieET);
                    KodOddeleniaET = (TextView) view.findViewById(R.id.kodOddeleniaET);

                    String sKnihaId = KodOddeleniaET.getText().toString();

                    Intent theIndent = new Intent(getApplication(),
                            ListMiestnosti.class);
                    theIndent.putExtra("kododdelenia", sKnihaId);
                    startActivity(theIndent);
                }
            });
            ListAdapter adapter = new SimpleAdapter( ListPoschodie.this,
                    zoznamHM, R.layout.poschodie_riadok,
                            new String[] { "divizia","oddelenie","kododdelenia"}, new int[] {R.id.diviziaET,R.id.poschodieET,R.id.kodOddeleniaET});

                    setListAdapter(adapter);
        }
    }
}

