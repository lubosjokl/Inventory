package com.innovativeproposals.inventorypokus2;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.innovativeproposals.inventorypokus2.InventarDetail.ViewInventarDetail;
import com.innovativeproposals.inventorypokus2.InventarVMiestnosti.DataModelInventarVMiestnosti;
import com.innovativeproposals.inventorypokus2.Models.Inventar;

// add Zebra
import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKManager.EMDKListener;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.barcode.BarcodeManager;
import com.symbol.emdk.barcode.ScanDataCollection;
import com.symbol.emdk.barcode.Scanner;
import com.symbol.emdk.barcode.Scanner.DataListener;
import com.symbol.emdk.barcode.Scanner.StatusListener;
import com.symbol.emdk.barcode.ScannerConfig;
import com.symbol.emdk.barcode.ScannerException;
import com.symbol.emdk.barcode.ScannerInfo;
import com.symbol.emdk.barcode.ScannerResults;
import com.symbol.emdk.barcode.StatusData;

import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InfoActivity extends AppCompatActivity implements EMDKListener,
        StatusListener, DataListener, BarcodeManager.ScannerConnectionListener, OnCheckedChangeListener {

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
    private int dataLength = 0;
    // private TextView textViewData = null;
    private EditText scannET = null;
    private Button buttonStartScan = null;

    private TextView textViewStatus = null;
    String barcodeString = null;
    List<Inventar> zoznamHM = null;
    DataModelInventarVMiestnosti dm = new DataModelInventarVMiestnosti(this);

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        deviceList = new ArrayList<ScannerInfo>();

        EMDKResults results = EMDKManager.getEMDKManager(getApplicationContext(), this);
        if (results.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
            textViewStatus.setText("Status: " + "EMDKManager object request failed!");
        }

        setContentView(R.layout.info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        scannET = (EditText) findViewById(R.id.scannET);
        textViewStatus = (TextView) findViewById(R.id.textViewStatus);
         buttonStartScan = (Button) findViewById(R.id.buttonStartScan);

        addStartScanButtonListener();
        setupButtonHandlers(); // zavolam ovladanie mojich tlacitok

        // ukrytie klavesnice
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout_info);
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                hideKeyboard(view);
                return false;
            }
        });
    }

    protected void hideKeyboard(View view)
    {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

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

    private void setupButtonHandlers() {
        findViewById(R.id.btOK).setOnClickListener((View view) -> {
            try {
                doShowDetailCall();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });
    }

    private void doShowDetailCall() throws URISyntaxException {

        // TODO : skener sa aktivuje programovo v onOpened

        if (barcodeString == null) {
            barcodeString = scannET.getText().toString();
        }

        if (barcodeString.equals("")) {
            barcodeString = scannET.getText().toString();
        }

        if (barcodeString.equals("barcode")) {
            Toast.makeText(this, R.string.barcode_doesnt_read_properly, Toast.LENGTH_LONG).show();
            return;
        }

        if (barcodeString.equals("")) {
            Toast.makeText(this, R.string.scann_barcode_first, Toast.LENGTH_LONG).show();
            return;
        }

        zoznamHM = dm.dajNoveZaznamy("", barcodeString); //xx

        if (zoznamHM.size() == 0) {
            Toast.makeText(this, R.string.barcode_doesnt_exist, Toast.LENGTH_LONG).show();
            return;
        }

        Log.d("skenujem", "Show 4");
        Inventar inventar = zoznamHM.get(0);

        barcodeString = ""; // xx
        // scannET.setText(""); // tuna to padne, presunul som na onResume
        Intent theIndent = new Intent(this, ViewInventarDetail.class);
        theIndent.putExtra("roomcode",inventar.getRommCode());
        theIndent.putExtra("inventar_object", inventar);
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

        textViewStatus.setText("Status: " + getString(R.string.EMDK_OpenSuccess));

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
        textViewStatus.setText("Status: " + getString(R.string.EMDKclosedUnexpectedlz));
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
                Log.d("skenujem", "1");
                doShowDetailCall();  // nevolat to automaticky, robi mi to problem ??
                Log.d("skenujem", "2");
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
                textViewStatus.setText("Status: " + "Failed to get the list of supported scanner devices! Please close and restart the application.");
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

                textViewStatus.setText("Status: " + e.getMessage());
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
                    textViewStatus.setText(R.string.scannerIsNotEnabled);
                }

            } catch (ScannerException e) {

                textViewStatus.setText("Status: " + e.getMessage());
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

                textViewStatus.setText("Status: " + e.getMessage());
            }
        }
    }

    private void initScanner() {

        if (scanner == null) {

            if ((deviceList != null) && (deviceList.size() != 0)) {
                scanner = barcodeManager.getDevice(deviceList.get(scannerIndex));
            } else {
                textViewStatus.setText("Status: " + getString(R.string.FailedToGetScannerDevice));
                return;
            }

            if (scanner != null) {

                scanner.addDataListener(this);
                scanner.addStatusListener(this);

                try {
                    scanner.enable();
                } catch (ScannerException e) {

                    textViewStatus.setText("Status: " + e.getMessage());
                }
            } else {
                textViewStatus.setText("Status: " + getString(R.string.FailedToInitializeScannerDevice));
            }
        }
    }

    private void deInitScanner() {

        if (scanner != null) {

            try {

                scanner.cancelRead();
                scanner.disable();

            } catch (ScannerException e) {

                textViewStatus.setText("Status: " + e.getMessage());
            }
            scanner.removeDataListener(this);
            scanner.removeStatusListener(this);
            try {
                scanner.release();
            } catch (ScannerException e) {

                textViewStatus.setText("Status: " + e.getMessage());
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

        scannET.setText(""); // vycistit pri navrate z detailu

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
            scannET.setText(""); // vycistit pri navrate z detailu
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

           /* if (result != null) {
                if(dataLength ++ > 100) { //Clear the cache after 100 scans
                    textViewData.setText("");
                    dataLength = 0;
                }
                */

            //  textViewData.append(result+"\n");

            scannET.setText(result);
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

            textViewStatus.setText("Status: " + result);
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


