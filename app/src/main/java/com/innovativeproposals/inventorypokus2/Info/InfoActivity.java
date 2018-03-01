package com.innovativeproposals.inventorypokus2.Info;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.innovativeproposals.inventorypokus2.Constants;
import com.innovativeproposals.inventorypokus2.InventarDetail.ViewInventarDetail;
import com.innovativeproposals.inventorypokus2.InventarVMiestnosti.CustomListInventoryAdapter;
import com.innovativeproposals.inventorypokus2.InventarVMiestnosti.DataModelInventarVMiestnosti;
import com.innovativeproposals.inventorypokus2.Models.Inventar;

// add Zebra
import com.innovativeproposals.inventorypokus2.MyAlertDialogFragmentOK;
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

import android.widget.Toast;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InfoActivity extends AppCompatActivity  implements EMDKManager.EMDKListener,
        Scanner.StatusListener, Scanner.DataListener, BarcodeManager.ScannerConnectionListener, CompoundButton.OnCheckedChangeListener {

        //implements EMDKListener,
       // StatusListener, DataListener, BarcodeManager.ScannerConnectionListener, OnCheckedChangeListener {

    // pridanie skenovania
    // http://techdocs.zebra.com/emdk-for-android/6-6/tutorial/tutAdvancedScanningAPI/

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

    TextView itembarcodeET;
    TextView itemdescriptionET;

    TextView statusET;
    TextView datumET;
    CustomListInventoryAdapter customListAdapter;
    DataModelInventarVMiestnosti dm2 = new DataModelInventarVMiestnosti(this); // pri kopirovani do inej triedy zmen

    String barcodeString = null;
    List<Inventar> zoznamHM = null;
    List<Inventar> zoznamHM2 = null;


    DataModelInfo dm = new DataModelInfo(this);

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
            public boolean onQueryTextChange(String searchedValue) {

                //tu sa odohrava event, kde sa zmenil search text
                //  customListAdapter.filter(s);

                //aktualizacia udajov po navrate z Detailu
                     try {
                zoznamHM = null;
                zoznamHM = dm.getInventarList(searchedValue);
                    if(zoznamHM.size()>0) {

                        customListAdapter.original_data = zoznamHM;
                        customListAdapter.filtered_list = zoznamHM;
                        customListAdapter.notifyDataSetChanged();
                    }
                    //     Log.i("QUERY CHANGED", "Search Text: " + searchedValue);

                   }
                   catch (URISyntaxException e) {
                        e.printStackTrace();
                    //return false;
                   }
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
        if (requestCode != 1) return; // sem ide po navrate z detailu. Aj niekedy inokedy?

        if (resultCode == 0) { // bolo Activity.RESULT_OK

            //aktualizacia udajov po navrate z Detailu
            try {
                zoznamHM = null;
                zoznamHM = dm.getInventarList("");
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

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // scanner
        deviceList = new ArrayList<ScannerInfo>();
        EMDKResults results = EMDKManager.getEMDKManager(getApplicationContext(), this);
        if (results.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
          //  textViewStatus.setText("Status: " + "EMDKManager object request failed!");
            ShowMyAlert("Status: " + "EMDKManager object request failed!");
        }

        setContentView(R.layout.inventar_vmiestnosti);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            // zoznam inventarov
            zoznamHM = dm.getInventarList("");
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

                theIndent.putExtra("roomcode","info"); // v Detaile sa vyzaduje pri nasnimani inventara v inej miestnosti sa musi zapisat jej kod, tu poslem len "info"
                theIndent.putExtra(Constants.INTENT_INVENTORY, inventar);

                View imageView = view.findViewById(R.id.detailView_Image);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(InfoActivity.this  , imageView, "detailView_Image");

                startActivityForResult(theIndent, 1, options.toBundle()); //
            }
        });
        customListAdapter = new CustomListInventoryAdapter(this, R.layout.inventar_vmiestnosti_riadok, zoznamHM);
        lw.setAdapter(customListAdapter);

       //x addStartScanButtonListener();
       //x  setupButtonHandlers(); // zavolam ovladanie mojich tlacitok

        // ukrytie klavesnice
        /*
        CoordinatorLayout layout = (CoordinatorLayout) findViewById(R.id.layout_info);
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                hideKeyboard(view);
                return false;
            }
        });*/

    }

    public void ShowMyAlert(String msg) {

        // pouzi DialogFragment

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(msg);
        builder1.setCancelable(true);
        builder1.setIcon(android.R.drawable.ic_dialog_alert);
        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

  /*          builder1.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    }); */

        AlertDialog alert11 = builder1.create();
        alert11.show(); // sem to nepride
      //  finish();

    }


    private Inventar findInventarById(Integer itemId) {
        for (Inventar item : zoznamHM) {
            if (item.getId() == itemId)
                return item;
        }

        //toto by sa nikdy nemalo stat
        return null;
    }

    protected void hideKeyboard(View view)
    {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /*
    private void addStartScanButtonListener() {
        Button btnStartScan = (Button) findViewById(R.id.buttonStartScan);
        btnStartScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startScan();
            }
        });
    }

    private void addStopScanButtonListener() {
        Button btnStopScan = (Button) findViewById(R.id.buttonStopScan);
        btnStopScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopScan();
            }
        });
    }
*/
    /*
    private void setupButtonHandlers() {
        findViewById(R.id.btOK).setOnClickListener((View view) -> {
            try {

                barcodeString = scannET.getText().toString();

                doShowDetailCall();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });
    }

*/
    private void doShowDetailCall() throws URISyntaxException {

        if (barcodeString.equals("")) {
            Toast.makeText(this, R.string.scann_barcode_first, Toast.LENGTH_LONG).show();
            return;
        }

        zoznamHM2 = dm2.dajNoveZaznamy("", barcodeString); // List<Inventar> zoznamHM

        if (zoznamHM2.size() == 0) {
            // TODO sken neexistujuceho kodu zobrazi Toast prilis kratko, resp. ho premaze Status skeneru
            // sem to prejde na button, ale nie na sken
            // Toast.makeText(this, R.string.barcode_doesnt_exist, Toast.LENGTH_LONG).show(); // xx

           // ShowMyAlert(getResources().getString(R.string.barcode_doesnt_exist));

          //  DialogFragment newFragment = MyAlertDialogFragmentOK.newInstance(1);
            //   newFragment.show(getFragmentManager(), "dialog");
          // nejde  !!!  MyAlertDialogFragmentOK.showAlert("title", "message",null);


            return;
        }


        Log.d("skenujem", "Show 4");
        Inventar inventar = zoznamHM2.get(0);
        barcodeString = "";
        inventar.setInfo(true);

        Intent theIndent = new Intent(this, ViewInventarDetail.class);
        theIndent.putExtra("roomcode","info"); // v Detaile sa vyzaduje pri nasnimani inventara v inej miestnosti sa musi zapisat jej kod, tu poslem len "info"

        theIndent.putExtra(Constants.INTENT_INVENTORY, inventar);
        startActivity(theIndent);
    }

    private void setDefaultOrientation() {

        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int rotation = windowManager.getDefaultDisplay().getRotation();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = 0;
        int height = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
            case Surface.ROTATION_180:
                width = dm.widthPixels;
                height = dm.heightPixels;
                break;
            case Surface.ROTATION_90:
            case Surface.ROTATION_270:
                width = dm.heightPixels;
                height = dm.widthPixels;
                break;
            default:
                break;
        }

        if (width > height) {
            setContentView(R.layout.info);
        } else {
            setContentView(R.layout.info);
        }
    }

    @Override
    public void onOpened(EMDKManager emdkManager) {

       // textViewStatus.setText("Status: " + getString(R.string.EMDK_OpenSuccess));

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
     //   textViewStatus.setText("Status: " + getString(R.string.EMDKclosedUnexpectedlz));
        ShowMyAlert("Status: " + getString(R.string.EMDKclosedUnexpectedlz));
    }

    @Override
    public void onData(ScanDataCollection scanDataCollection) {

        if ((scanDataCollection != null) && (scanDataCollection.getResult() == ScannerResults.SUCCESS)) {
            ArrayList<ScanDataCollection.ScanData> scanData = scanDataCollection.getScanData();
            for (ScanDataCollection.ScanData data : scanData) {

                String dataString = data.getData();
                barcodeString = dataString;

                new AsyncDataUpdate().execute(dataString);
            }

            try {
                //Log.d("skenujem", String.format("MyHandler[running on thread %d] - recevied:%s", threadId,messageText));
                doShowDetailCall();  // nevolat to automaticky, robi mi to problem ??
              //  Log.d("skenujem", "2");
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStatus(StatusData statusData) {

        StatusData.ScannerStates state = statusData.getState();
        switch (state) {
            case IDLE:
                statusString = statusData.getFriendlyName() + getString(R.string.isEnabledAndIdle);
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
                statusString = getString(R.string.scannerIsWaitingForTrigger);
                new AsyncStatusUpdate().execute(statusString);
                new AsyncUiControlUpdate().execute(false);
                break;
            case SCANNING:
                statusString = getString(R.string.scanning);
                new AsyncStatusUpdate().execute(statusString);
                new AsyncUiControlUpdate().execute(false);
                break;
            case DISABLED:
                statusString = statusData.getFriendlyName() + getString(R.string.isDisabled);
                new AsyncStatusUpdate().execute(statusString);
                new AsyncUiControlUpdate().execute(true);
             //   enableScanner(); // viedensky priklad
                break;
            case ERROR:
                statusString = getString(R.string.AnErrorISOccured);
                new AsyncStatusUpdate().execute(statusString);
                new AsyncUiControlUpdate().execute(true);
                break;
            default:
                break;
        }
    }

    public void enableScanner() {
        if (!mScannerEnabled) {
            mScannerEnabled = true;
            //initScanner();

            if (scanner != null) {
                try {
                    scanner.enable();
                } catch (ScannerException e) {
                    // mELogListener.onLogE(TAG, "enableScanner; Status: " + e.getMessage(), e);
                    //textViewStatus.setText("Status: " + e.getMessage());
                    ShowMyAlert("Status: " + e.getMessage());
                }
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
               // textViewStatus.setText("Status: " + "Failed to get the list of supported scanner devices! Please close and restart the application.");
                ShowMyAlert("Status: " +"Failed to get the list of supported scanner devices! Please close and restart the application.");
            }

            //   ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(InfoActivity.this, android.R.layout.simple_spinner_item, friendlyNameList);
            //   spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //   spinnerScannerDevices.setAdapter(spinnerAdapter);
        }
    }

    private void populateTriggers() {

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(InfoActivity.this, android.R.layout.simple_spinner_item, triggerStrings);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerTriggers.setAdapter(spinnerAdapter);
        spinnerTriggers.setSelection(triggerIndex);
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

              //  textViewStatus.setText("Status: " + e.getMessage());
                ShowMyAlert("Status: " + e.getMessage());
            }
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
                 //   textViewStatus.setText(R.string.scannerIsNotEnabled);
                    ShowMyAlert("Status: " + R.string.scannerIsNotEnabled);
                }

            } catch (ScannerException e) {

             //   textViewStatus.setText("Status: " + e.getMessage());
                ShowMyAlert("Status: " +  e.getMessage());
            }
        }
    }

    private void stopScan() {

        if (scanner != null) {

            try {

                // Reset continuous flag
                bContinuousMode = false;

                // Cancel the pending read.
                scanner.cancelRead();

                new AsyncUiControlUpdate().execute(true);

            } catch (ScannerException e) {

              //  textViewStatus.setText("Status: " + e.getMessage());
                ShowMyAlert("Status: " +  e.getMessage());
            }
        }
    }

    private void initScanner() {

        if (scanner == null) {

            if ((deviceList != null) && (deviceList.size() != 0)) {
                scanner = barcodeManager.getDevice(deviceList.get(scannerIndex));
            } else {
              //  textViewStatus.setText("Status: " + getString(R.string.FailedToGetScannerDevice));
                ShowMyAlert("Status: " +   getString(R.string.FailedToGetScannerDevice));
                return;
            }

            if (scanner != null) {

                scanner.addDataListener(this);
                scanner.addStatusListener(this);

                try {
                    scanner.enable();
                } catch (ScannerException e) {

                //    textViewStatus.setText("Status: " + e.getMessage());
                }
            } else {
             //   textViewStatus.setText("Status: " + getString(R.string.FailedToInitializeScannerDevice));
                ShowMyAlert("Status: " +  getString(R.string.FailedToInitializeScannerDevice));
            }
        }
    }

    private void deInitScanner() {

        if (scanner != null) {

            try {
                scanner.cancelRead();
                scanner.disable();

            } catch (ScannerException e) {

             //   textViewStatus.setText("Status: " + e.getMessage());
                ShowMyAlert("Status: " + e.getMessage());
            }
            scanner.removeDataListener(this);
            scanner.removeStatusListener(this);
            try {
                scanner.release();
            } catch (ScannerException e) {

              //  textViewStatus.setText("Status: " + e.getMessage());
                ShowMyAlert("Status: " + e.getMessage());
            }
            scanner = null;
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

     //   scannET.setText(""); // vycistit pri navrate z detailu

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
       //     scannET.setText(""); // vycistit pri navrate z detailu
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Called when the activity is becoming visible to the user.
        //startScan(); // aktivuj hw tlacidlo skeneru
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setDecoders();
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

    private class AsyncDataUpdate extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            return params[0];
        }

        protected void onPostExecute(String result) {
        //    scannET.setText(result);

            // xx sem daj hladanie

            return;
        }
    }

    private class AsyncStatusUpdate extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            return params[0];
        }

        @Override
        protected void onPostExecute(String result) {

          //  textViewStatus.setText("Status: " + result);
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
}

