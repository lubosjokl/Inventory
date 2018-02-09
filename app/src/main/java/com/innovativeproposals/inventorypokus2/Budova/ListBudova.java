package com.innovativeproposals.inventorypokus2.Budova;

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

import com.innovativeproposals.inventorypokus2.Poschodie.ListPoschodie;
import com.innovativeproposals.inventorypokus2.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Lubos on 26.12.17.
 */

public class ListBudova extends AppCompatActivity
{

    TextView diviziaET;
    DataModelBudova dm = new DataModelBudova(this);

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.budova);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ArrayList<HashMap<String, String>> zoznamHM =dm.dajZaznamy();

        if(zoznamHM.size()!=0)
        {
            ListView lw = (ListView) findViewById(R.id.list_budovy);
            lw.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                //kliknutie na polozku zoznamu
                public void onItemClick(AdapterView<?> parent,
                                        View view, int position, long id)
                {
                    diviziaET = (TextView) view.findViewById(R.id.diviziaET);

                    String sParameter = diviziaET.getText().toString();
                    Intent theIndent = new Intent(getApplication(),
                            ListPoschodie.class);
                    theIndent.putExtra("diviziaET", sParameter);

                    startActivity(theIndent);
                }
            });
            ListAdapter adapter = new SimpleAdapter( ListBudova.this,
                    zoznamHM,
                    R.layout.budova_riadok,
                    new String[] { "divizia"}, new int[] {R.id.diviziaET});

            lw.setAdapter(adapter);
        }
    }
}
