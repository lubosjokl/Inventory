package com.innovativeproposals.inventorypokus2.InventarVMiestnosti;

/**
 * Created by Lubos on 27.12.17.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.BundleCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.innovativeproposals.inventorypokus2.InventarDetail.ViewInventarDetail;
import com.innovativeproposals.inventorypokus2.Models.Inventar;
import com.innovativeproposals.inventorypokus2.R;

import java.net.URISyntaxException;
import java.util.List;


public class ListInventarVMiestnosti extends AppCompatActivity {
    Intent intent;

    TextView itembarcodeET;
    TextView itemdescriptionET;

    TextView statusET;
    TextView datumET;
    List<Inventar> zoznamHM = null;

    DataModelInventarVMiestnosti dm = new DataModelInventarVMiestnosti(this); // pri kopirovani do inej triedy zmen
    CustomListInventoryAdapter customListAdapter;

    public final static String INTENT_INVENTORY = "inventar";

    //id	,[itembarcode] ,		,[itemdescription]		,[roomcodenew]		,[status]		,[datum]		,[datumDispose]		,[datumREAL], serialnr

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);


        MenuItem myActionMenuItem = menu.findItem( R.id.search);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Toast like print
                if( ! searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                myActionMenuItem.collapseActionView();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                //tu sa odohrava event, kde sa zmenil search text

                customListAdapter.filter(s);

                Log.i("QUERY CHANGED", "Search Text: " + s);
                return false;
            }
        });

        // Associate searchable configuration with the SearchView
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
//        searchView.setSearchableInfo(
//                searchManager.getSearchableInfo(getComponentName()));
//
        return true;
//        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode != 1) return;

        if(resultCode == Activity.RESULT_OK){
            Inventar returnedObject = data.getParcelableExtra(this.INTENT_INVENTORY);
            Inventar inventar = findInventarById(returnedObject.getId());

            if(inventar != null) {
                inventar.Copy(returnedObject);
                customListAdapter.notifyDataSetChanged();
            } else {

                // pre novy inventar vytvor objekt a napln ho roomcode a itembarcode TODO

            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //enable search within activity
//        handleIntent(getIntent());

        String myRoomcode = "";

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            myRoomcode = extras.getString("roomcode");
        }

        setContentView(R.layout.inventar_vmiestnosti);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        String kancel = dm.dajNazovMiestnosti(myRoomcode);

        toolbar.setTitle(kancel);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            zoznamHM = dm.dajNoveZaznamy(myRoomcode, "");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if (zoznamHM.size() != 0) {
            ListView lw = (ListView) findViewById(R.id.list_inventar_v_miestnosti);
            lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                //kliknutie na polozku zoznamu
                @SuppressLint("RestrictedApi")
                public void onItemClick(AdapterView<?> parent,
                                        View view, int position, long id) {
                    itembarcodeET = (TextView) view.findViewById(R.id.itembarcodeET);
                    itemdescriptionET = (TextView) view.findViewById(R.id.itemdescriptionET);

                    statusET = (TextView) view.findViewById(R.id.statusET);
                    datumET = (TextView) view.findViewById(R.id.datumET);

                    String sKnihaId = itembarcodeET.getText().toString();

                    int inventarID = (int) view.getTag();
                    Inventar inventar = findInventarById(inventarID);

                    Intent theIndent = new Intent(getApplication(),
                            ViewInventarDetail.class);
                    theIndent.putExtra("barcode", sKnihaId);
                    theIndent.putExtra(ListInventarVMiestnosti.INTENT_INVENTORY, inventar);

                    View imageView = view.findViewById(R.id.detailView_Image); // ma natvrdo v layoute devinovany src
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                            ListInventarVMiestnosti.this, imageView, "detailView_Image");

                    startActivityForResult(theIndent, 1,options.toBundle());
                }
            });
            customListAdapter = new CustomListInventoryAdapter(this, R.layout.inventar_vmiestnosti_riadok, zoznamHM);

            lw.setAdapter(customListAdapter);
        }
    }

    private Inventar findInventarById(Integer itemId){
        for (Inventar item : zoznamHM) {
            if(item.getId() == itemId)
                return item;
        }

        //toto by sa nikdy nemalo stat
        return null;
    }
}

