package com.innovativeproposals.inventorypokus2.InventarVMiestnosti;

/**
 * Created by Lubos on 27.12.17.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.innovativeproposals.inventorypokus2.Constants;
import com.innovativeproposals.inventorypokus2.InventarDetail.ViewInventarDetail;
import com.innovativeproposals.inventorypokus2.Models.Inventar;
import com.innovativeproposals.inventorypokus2.R;
import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.barcode.BarcodeManager;
import com.symbol.emdk.barcode.ScanDataCollection;
import com.symbol.emdk.barcode.Scanner;
import com.symbol.emdk.barcode.ScannerConfig;
import com.symbol.emdk.barcode.ScannerException;
import com.symbol.emdk.barcode.ScannerInfo;
import com.symbol.emdk.barcode.ScannerResults;
import com.symbol.emdk.barcode.StatusData;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ListInventarVMiestnosti extends AppCompatActivity implements EMDKManager.EMDKListener,
        Scanner.StatusListener, Scanner.DataListener, BarcodeManager.ScannerConnectionListener, CompoundButton.OnCheckedChangeListener {

    private String myRoomcode;
    TextView itembarcodeET;
    TextView itemdescriptionET;

    TextView statusET;
    TextView datumET;
    List<Inventar> zoznamHM = null;

    DataModelInventarVMiestnosti dm = new DataModelInventarVMiestnosti(this); // pri kopirovani do inej triedy zmen
    CustomListInventoryAdapter customListAdapter;

    //Zebra
    private EMDKManager emdkManager = null;
    private BarcodeManager barcodeManager = null;
    private Scanner scanner = null;
    private String statusString = "";
    private boolean bContinuousMode = false;
    private boolean mScannerEnabled = false;
    private List<ScannerInfo> deviceList = null;
    private String[] triggerStrings = {"HARD", "SOFT"};
    private Spinner spinnerTriggers = null;
    private int triggerIndex = 0;
    private int defaultIndex = 0; // Keep the default scanner
    private int scannerIndex = 0; // Keep the selected scanner

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem myActionMenuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Toast like print
                if (!searchView.isIconified()) {
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

        //   Associate searchable configuration with the SearchView
        //        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        //        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        //        searchView.setSearchableInfo(
        //        searchManager.getSearchableInfo(getComponentName()));

        return true;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // toto refreshuje list
        if (requestCode != 1) return; // sem ide po navrate z detailu. Aj niekedy inokedy?

        if (resultCode == 0) { // bolo Activity.RESULT_OK

            //aktualizacia udajov po navrate z Detailu
            try {
                zoznamHM = null;
                zoznamHM = dm.dajNoveZaznamy(myRoomcode, "");
                if(zoznamHM.size()>0) {

                     customListAdapter.original_data = zoznamHM;
                     customListAdapter.filtered_list = zoznamHM;
                     customListAdapter.notifyDataSetChanged();
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    // bolo protected
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //enable search within activity

        myRoomcode = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            myRoomcode = extras.getString("roomcode");
        }

        // scanner
        deviceList = new ArrayList<ScannerInfo>();
        EMDKResults results = EMDKManager.getEMDKManager(getApplicationContext(), this);
        if (results.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
            Log.i("warning", "EMDK failed: ");
        }

        setContentView(R.layout.inventar_vmiestnosti);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        String kancel = dm.dajNazovMiestnosti(myRoomcode);

        toolbar.setTitle(kancel);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            // zoznam inventarov v miestnosti
            zoznamHM = dm.dajNoveZaznamy(myRoomcode, "");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

     //   if (zoznamHM.size() != 0) {
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

                    int inventarID = (int) view.getTag();
                    Inventar inventar = findInventarById(inventarID);
                    inventar.setInfo(false);

                    Intent theIndent = new Intent(getApplication(),
                            ViewInventarDetail.class);

                    theIndent.putExtra("roomcode",myRoomcode); // musim posielat aj roomcode, pri nasnimani inventara v inej miestnosti sa musi zapisat jej kod
                    theIndent.putExtra(Constants.INTENT_INVENTORY, inventar);

                    View imageView = view.findViewById(R.id.detailView_Image);
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                            ListInventarVMiestnosti.this, imageView, "detailView_Image");

                    startActivityForResult(theIndent, 1, options.toBundle()); //
                }
            });
            customListAdapter = new CustomListInventoryAdapter(this, R.layout.inventar_vmiestnosti_riadok, zoznamHM);
            lw.setAdapter(customListAdapter);

      //  }
    }

    private Inventar findInventarById(Integer itemId) {
        for (Inventar item : zoznamHM) {
            if (item.getId() == itemId)
                return item;
        }

        //toto by sa nikdy nemalo stat
        return null;
    }

    private Inventar findInventarByBarcode(String barcode) {
        for (Inventar item : zoznamHM) {
            if (item.getItemBarcode() == barcode)
                return item;
        }

        //toto by sa nikdy nemalo stat
        return null;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setDecoders();
    }

    @Override
    public void onOpened(EMDKManager emdkManager) {
//        textViewStatus.setText("Status: " + "EMDK open success!");

        this.emdkManager = emdkManager;

        // Acquire the barcode manager resources
        barcodeManager = (BarcodeManager) emdkManager.getInstance(EMDKManager.FEATURE_TYPE.BARCODE);

        // Add connection listener
        if (barcodeManager != null) {
            barcodeManager.addConnectionListener(this);
        }

        // Enumerate scanner devices
        enumerateScannerDevices();

        // Set default scanner
        //  spinnerScannerDevices.setSelection(defaultIndex);

        // Initialize scanner
        initScanner();
        setTrigger();
        setDecoders();

        startScan();
    }

    @Override
    public void onClosed() {
        if (emdkManager != null) {

            // Remove connection listener
            if (barcodeManager != null) {
                barcodeManager.removeConnectionListener(this);
                barcodeManager = null;
            }

            // Release all the resources
            emdkManager.release();
            emdkManager = null;
        }
    }

    @Override
    public void onConnectionChange(ScannerInfo scannerInfo, BarcodeManager.ConnectionState connectionState) {
        String status;
        String scannerName = "";

        String statusExtScanner = connectionState.toString();
        String scannerNameExtScanner = scannerInfo.getFriendlyName();

        if (deviceList.size() != 0) {
            scannerName = deviceList.get(scannerIndex).getFriendlyName();
        }

        if (scannerName.equalsIgnoreCase(scannerNameExtScanner)) {

            switch (connectionState) {
                case CONNECTED:
                    deInitScanner();
                    initScanner();
                    setTrigger();
                    setDecoders();
                    break;
                case DISCONNECTED:
                    deInitScanner();
                    new AsyncUiControlUpdate().execute(true);
                    break;
            }

            status = scannerNameExtScanner + ":" + statusExtScanner;
            new AsyncStatusUpdate().execute(status);
        } else {
            status = statusString + " " + scannerNameExtScanner + ":" + statusExtScanner;
            new AsyncStatusUpdate().execute(status);
        }
    }

    @Override
    public void onData(ScanDataCollection scanDataCollection) {

        if ((scanDataCollection != null) && (scanDataCollection.getResult() == ScannerResults.SUCCESS)) {
            ArrayList<ScanDataCollection.ScanData> scanData = scanDataCollection.getScanData();
            for (ScanDataCollection.ScanData data : scanData) {

                String dataString = data.getData();
                Inventar inventar = new Inventar();

                Log.i("Scanned value:", dataString);
                new AsyncDataUpdate().execute(dataString);

                try {
                    // daj inventar s kodom
                    zoznamHM = dm.dajNoveZaznamy("", dataString); //xx
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

                Log.d("skenujem", "Show 4");
                if (zoznamHM.size() > 0)
                    inventar = zoznamHM.get(0);

                else {
                    // je to novy inventar
                    inventar.setRommCode(myRoomcode);
                    inventar.setItemBarcode(dataString);
                    inventar.setInfo(false );
                }

                Intent theIndent = new Intent(this, ViewInventarDetail.class);
                theIndent.putExtra(Constants.INTENT_INVENTORY, inventar);
                theIndent.putExtra("roomcode",myRoomcode); // musim posielat aj roomcode, pri nasnimani inventara v inej miestnosti sa musi zapisat jej kod

                startActivityForResult(theIndent, 1);
            }
            return;
        }
    }

    @Override
    public void onStatus(StatusData statusData) {
        StatusData.ScannerStates state = statusData.getState();
        switch (state) {
            case IDLE:
                statusString = statusData.getFriendlyName() + " is enabled and idle...";
                new AsyncStatusUpdate().execute(statusString);
                if (bContinuousMode) {
                    try {
                        // An attempt to use the scanner continuously and rapidly (with a delay < 100 ms between scans)
                        // may cause the scanner to pause momentarily before resuming the scanning.
                        // Hence add some delay (>= 100ms) before submitting the next read.
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        scanner.read();
                    } catch (ScannerException e) {
                        statusString = e.getMessage();
                        new AsyncStatusUpdate().execute(statusString);
                    }
                }
                new AsyncUiControlUpdate().execute(true);
                break;
            case WAITING:
                statusString = "Scanner is waiting for trigger press...";
                new AsyncStatusUpdate().execute(statusString);
                new AsyncUiControlUpdate().execute(false);
                break;
            case SCANNING:
                statusString = "Scanning...";
                new AsyncStatusUpdate().execute(statusString);
                new AsyncUiControlUpdate().execute(false);
                break;
            case DISABLED:
                statusString = statusData.getFriendlyName() + " is disabled.";
                new AsyncStatusUpdate().execute(statusString);
                new AsyncUiControlUpdate().execute(true);
//                enableScanner(); // viedensky priklad
                break;
            case ERROR:
                statusString = "An error has occurred.";
                new AsyncStatusUpdate().execute(statusString);
                new AsyncUiControlUpdate().execute(true);
                break;
            default:
                break;
        }
    }

    private void deInitScanner() {

        if (scanner != null) {

            try {

                scanner.cancelRead();
                scanner.disable();

            } catch (ScannerException e) {

//                textViewStatus.setText("Status: " + e.getMessage());
            }
            scanner.removeDataListener(this);
            scanner.removeStatusListener(this);
            try {
                scanner.release();
            } catch (ScannerException e) {

//                textViewStatus.setText("Status: " + e.getMessage());
            }

            scanner = null;
        }
    }

    private void startScan() {

        if (scanner == null) {
            initScanner();
        }

        if (scanner != null) {
            try {

                if (scanner.isEnabled()) {
                    // Submit a new read.
                    scanner.read();
                    bContinuousMode = true; // false = skenujem len raz

                 /*   if (checkBoxContinuous.isChecked())
                        bContinuousMode = true;
                    else
                        bContinuousMode = false;*/

                    new AsyncUiControlUpdate().execute(false);
                } else {
//                    textViewStatus.setText("Status: Scanner is not enabled");
                }

            } catch (ScannerException e) {

//                textViewStatus.setText("Status: " + e.getMessage());
            }
        }
    }

    private void enumerateScannerDevices() {

        if (barcodeManager != null) {

            List<String> friendlyNameList = new ArrayList<String>();
            int spinnerIndex = 0;

            deviceList = barcodeManager.getSupportedDevicesInfo();

            if ((deviceList != null) && (deviceList.size() != 0)) {

                Iterator<ScannerInfo> it = deviceList.iterator();
                while (it.hasNext()) {
                    ScannerInfo scnInfo = it.next();
                    friendlyNameList.add(scnInfo.getFriendlyName());
                    if (scnInfo.isDefaultScanner()) {
                        defaultIndex = spinnerIndex;
                    }
                    ++spinnerIndex;
                }
            } else {
//                textViewStatus.setText("Status: " + "Failed to get the list of supported scanner devices! Please close and restart the application.");
            }

            //   ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(InfoActivity.this, android.R.layout.simple_spinner_item, friendlyNameList);
            //   spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            //   spinnerScannerDevices.setAdapter(spinnerAdapter);
        }
    }

    private void setTrigger() {

        if (scanner == null) {
            initScanner();
        }

        if (scanner != null) {
            switch (triggerIndex) {
                case 0: // Selected "HARD"
                    scanner.triggerType = Scanner.TriggerType.HARD;
                    break;
                case 1: // Selected "SOFT"
                    scanner.triggerType = Scanner.TriggerType.SOFT_ALWAYS;
                    break;
            }
        }
    }

    private void setDecoders() {

        if (scanner == null) {
            initScanner();
        }

        if ((scanner != null) && (scanner.isEnabled())) {
            try {

                ScannerConfig config = scanner.getConfig();

                // config.decoderParams.ean8.enabled = true;
                config.decoderParams.ean13.enabled = true;
                //  config.decoderParams.code39.enabled = true;
                config.decoderParams.code128.enabled = true;

                /*
                // Set EAN8
                if(checkBoxEAN8.isChecked())
                    config.decoderParams.ean8.enabled = true;
                else
                    config.decoderParams.ean8.enabled = false;

                // Set EAN13
                if(checkBoxEAN13.isChecked())
                    config.decoderParams.ean13.enabled = true;
                else
                    config.decoderParams.ean13.enabled = false;

                // Set Code39
                if(checkBoxCode39.isChecked())
                    config.decoderParams.code39.enabled = true;
                else
                    config.decoderParams.code39.enabled = false;

                //Set Code128
                if(checkBoxCode128.isChecked())
                    config.decoderParams.code128.enabled = true;
                else
                    config.decoderParams.code128.enabled = false;
                    */

                scanner.setConfig(config);

            } catch (ScannerException e) {

//                textViewStatus.setText("Status: " + e.getMessage());
            }
        }
    }

    private void initScanner() {

        if (scanner == null) {

            if ((deviceList != null) && (deviceList.size() != 0)) {
                scanner = barcodeManager.getDevice(deviceList.get(scannerIndex));
            } else {
//                textViewStatus.setText("Status: " + "Failed to get the specified scanner device! Please close and restart the application.");
                return;
            }

            if (scanner != null) {

                scanner.addDataListener(this);
                scanner.addStatusListener(this);

                try {
                    scanner.enable();
                } catch (ScannerException e) {

//                    textViewStatus.setText("Status: " + e.getMessage());
                }
            } else {
//                textViewStatus.setText("Status: " + "Failed to initialize the scanner device.");
            }
        }
    }

    private class AsyncDataUpdate extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            return params[0];
        }

        protected void onPostExecute(String result) {

           /* if (result != null) {
                if(dataLength ++ > 100) { //Clear the cache after 100 scans
                    textViewData.setText("");
                    dataLength = 0;
                }
                */

            //  textViewData.append(result+"\n");

//            scannET.setText(result);
            //     textViewData.setText(result);
            return;


            // scrolovanie v textView
          /*      ((View) findViewById(R.id.scrollView1)).post(new Runnable()
                {
                    public void run()
                    {
                        ((ScrollView) findViewById(R.id.scrollView1)).fullScroll(View.FOCUS_DOWN);
                    }
                });*/
        }
    }

    private class AsyncStatusUpdate extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            return params[0];
        }

        @Override
        protected void onPostExecute(String result) {

//            textViewStatus.setText("Status: " + result);
        }
    }

    private class AsyncUiControlUpdate extends AsyncTask<Boolean, Void, Boolean> {


        @Override
        protected void onPostExecute(Boolean bEnable) {

            //    checkBoxEAN8.setEnabled(bEnable);
            //    checkBoxEAN13.setEnabled(bEnable);
            //    checkBoxCode39.setEnabled(bEnable);
            //    checkBoxCode128.setEnabled(bEnable);
            //   spinnerScannerDevices.setEnabled(bEnable);
            //   spinnerTriggers.setEnabled(bEnable);
        }

        @Override
        protected Boolean doInBackground(Boolean... arg0) {

            return arg0[0];
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // De-initialize scanner
        deInitScanner();

        // Remove connection listener
        if (barcodeManager != null) {
            barcodeManager.removeConnectionListener(this);
            barcodeManager = null;
        }

        // Release all the resources
        if (emdkManager != null) {
            emdkManager.release();
            emdkManager = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // The application is in background

        // De-initialize scanner
        deInitScanner();

        // Remove connection listener
        if (barcodeManager != null) {
            barcodeManager.removeConnectionListener(this);
            barcodeManager = null;
            deviceList = null;
        }

        // Release the barcode manager resources
        if (emdkManager != null) {
            emdkManager.release(EMDKManager.FEATURE_TYPE.BARCODE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Toto tu musi byt, inak nam po navrate z detailu nefunguje spravne skener

        if (emdkManager != null) {
            barcodeManager = (BarcodeManager) emdkManager.getInstance(EMDKManager.FEATURE_TYPE.BARCODE);

            // Add connection listener
            if (barcodeManager != null) {
                barcodeManager.addConnectionListener(this);
            }

            // Enumerate scanner devices
            enumerateScannerDevices();

            // Set selected scanner
            // spinnerScannerDevices.setSelection(scannerIndex);

            // Initialize scanner
            initScanner();
            setTrigger();
            setDecoders();
            startScan();
        }

        // a este nacitaj original_data ?


    }

    @Override
    protected void onStart() {
        super.onStart();
        // Called when the activity is becoming visible to the user.
        //startScan(); // aktivuj hw tlacidlo skeneru
    }


}

