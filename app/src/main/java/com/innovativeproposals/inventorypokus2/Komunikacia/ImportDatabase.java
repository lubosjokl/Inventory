package com.innovativeproposals.inventorypokus2.Komunikacia;
//package com.innovativeproposals.inventorypokus2.ImportDatabase;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.innovativeproposals.inventorypokus2.Constants;
import com.innovativeproposals.inventorypokus2.R;

import java.net.MalformedURLException;

// komunikacia
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


// https://code.google.com/archive/p/android-query/wikis/AsyncAPI.wiki
// https://eventuallyconsistent.net/2011/08/02/working-with-urlconnection-and-timeouts/
// http://wptrafficanalyzer.in/blog/android-http-access-with-httpurlconnection-to-download-image-example/

// download sqlite from server
// https://stackoverflow.com/questions/28810064/how-to-download-sqlite-database-file-from-the-server-in-android


public class ImportDatabase extends AppCompatActivity implements View.OnClickListener {

    private Button mStartBtn;
    private TextView mProgressTxt;
    private ProgressBar mProgress;
    private static final int    SIZE_KB = 1024;
    private static final int    SIZE_MB = (SIZE_KB * 1024);
    private static final int    TOTAL_DATA_POINTS = (4 * SIZE_MB);
    public String fileNameFrom;
    public String fileNameTo;

    private byte[] dataBuf = new byte[TOTAL_DATA_POINTS];
    private GenAsyncTask mGenAT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_bar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mStartBtn = (Button) findViewById(R.id.startButton);
        mProgressTxt = (TextView) findViewById(R.id.prog_txt);
        mProgress = (ProgressBar) findViewById(R.id.gen_progress);

        mProgressTxt.setText(R.string.textBeforeImport_Export);

        mStartBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mStartBtn) {
            //  Start generating data, do it right here.
            if(isNetworkAvailable()) {
                doGenerate();
            } else
              //  Toast.makeText(getBaseContext(), "Network is not Available", Toast.LENGTH_SHORT).show();
                mProgressTxt.setText(R.string.NetworkisNotAvailable);
                mProgressTxt.setBackgroundResource(R.color.colorNO );
        }
    }

    private boolean isNetworkAvailable(){
        boolean available = false;
        /** Getting the system's connectivity service */
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        /** Getting active network interface  to get the network's status */
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo !=null && networkInfo.isAvailable())
            available = true;

        /** Returning the status of the network */
        return available;
    }

    private void doGenerate() {

        mProgress.setVisibility(View.VISIBLE);
        mProgress.setProgress(0);
        mProgressTxt.setVisibility(View.VISIBLE);
        mProgressTxt.setText(R.string.loadDatabase);

        // teraz vytvorime AsyncTask
        if (mGenAT == null) {
            mGenAT = new GenAsyncTask();
            mGenAT.execute(dataBuf);
        }
    }

    public void assureThatDirectoryExist(String directory) {

        File dir = new File(directory);

        if (!dir.exists()&& dir.isDirectory())
            dir.mkdirs();
    }

    // Samostatna asynchronna trieda
    // byte[] je vstup do nasho spracovania pozadia
    // Integer je typ ktory sa pouziva na aktualizaciu komponentu uzivatelskeho rozhrania
    // Long je vysledok spracovanie
    private class GenAsyncTask extends AsyncTask<byte[], Integer, String> {

        // vstup do nasho spracovania pozadia

        @Override
        protected String doInBackground(byte[]... arg0) {

            int progScale;
            long startTime;
            long endTime;

            startTime = SystemClock.elapsedRealtime();

            // ziskaj globalne SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences(getPackageName() + Constants.PREF_FILE_NAME, MODE_PRIVATE);
            String address = sharedPreferences.getString(Constants.KEY_ADDRESS, "not defined"); // druhy parameter je defaultna hodnota
            String port = sharedPreferences.getString(Constants.KEY_PORT, "11235");
            String fileURL = address + ":" + port; //String fileURL = "http://192.168.1.119:11235";

            PackageManager m = getPackageManager();
            String saveDir = getPackageName();
            PackageInfo p = null;
            try {
                p = m.getPackageInfo(saveDir,0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            saveDir = p.applicationInfo.dataDir + "/files"; // /data/user/0/com.innovativeproposals.inventorypokus2
            assureThatDirectoryExist(saveDir); // vytvor adresar ak neexistuje
            fileNameTo = p.applicationInfo.dataDir + "/databases";
            assureThatDirectoryExist(fileNameTo); //
            fileNameTo = p.applicationInfo.dataDir + "/databases/" + Constants.FILE_DATABASE;



            //int BUFFER_SIZE = 1024;
            HttpURLConnection httpConn = null;
            try {
                URL url = new URL(fileURL);
                 httpConn = (HttpURLConnection) url.openConnection();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                httpConn.setConnectTimeout(2000); // 2 Sekundy
                int responseCode = httpConn.getResponseCode();

                // always check HTTP response code first
                if (responseCode == HttpURLConnection.HTTP_OK) {

                    httpConn.connect();

                    String fileName = Constants.FILE_DATA_PC2PDA;
                    String disposition = httpConn.getHeaderField("Content-Disposition");
                    String contentType = httpConn.getContentType();
                    int contentLength = httpConn.getContentLength();

                  //  progScale = contentLength / 100;

                    if (disposition != null) {
                        // extracts file name from header field
                        int index = disposition.indexOf("filename=");
                        if (index > 0) {
                            fileName = disposition.substring(index + 10,
                                    disposition.length() - 1);
                        }
                    } else {
                        // extracts file name from URL
                        fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());
                        fileNameFrom = saveDir + "/" + Constants.FILE_DATA_PC2PDA;

                    }

                    System.out.println("Content-Type = " + contentType);
                    System.out.println("Content-Disposition = " + disposition);
                    System.out.println("Content-Length = " + contentLength);
                    System.out.println("fileName = " + fileName);

                    // opens input stream from the HTTP connection toto velkost suboru
                    InputStream inputStream = httpConn.getInputStream();
                    progScale = inputStream.available();
                    if(progScale == 0)
                        progScale = 500000;

                   // String saveFilePath = saveDir + "/" + File.separator + fileName;

                    // opens an output stream to save into file
                    FileOutputStream outputStream = new FileOutputStream(fileNameFrom);

                    int kolo = 1;
                    byte[] buffer = new byte[1024];
                    int bufferLength = 0;

                    int aa=0;

                    while ((bufferLength = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, bufferLength);
                        kolo++;

                        aa = (Math.round(30*kolo*bufferLength / progScale));
                        if(aa>100)
                            kolo = 1;
                        publishProgress(aa); // je to tu spravne
                    }

                    outputStream.close();
                    inputStream.close();

                    System.out.println("File downloaded");
                } else {
                    System.out.println("No file to download. Server replied HTTP code: " + responseCode);
                }


            } catch (IOException ex) {
                ex.printStackTrace();
                return "nofile";
            }
            httpConn.disconnect();

            endTime = SystemClock.elapsedRealtime();
            return String.valueOf(endTime - startTime);
        }

        // aktualizacia komponenty uzivatelskeho rozhrania
        @Override
        protected void onProgressUpdate(Integer... progress) {
            // progress je na zaklade vysledku onPostExecute?
            mProgress.setProgress(progress[0]);
        }

        // vysledok spracovanie, napr. pocet sekund trvania operacie
        @Override
        protected void onPostExecute(String result) {
            //String doneStr = getString(R.string.done_fill);
            String doneStr; // = "hotovo";

            if(result.equals("nofile")) {
                doneStr = getString(R.string.NenaimportovalSaZiadnySuborPreverteFirewall);
                publishProgress(0);
                mProgressTxt.setBackgroundResource(R.color.colorNO );
            } else {

                // hodnotu berieme z doInBackground.return (endTime - startTime);
                doneStr = getString(R.string.ImportDone) + result + " ms";
                publishProgress(100);
                mProgressTxt.setBackgroundResource(R.color.colorZelena  );
                // volaj to len pri uspesnom importe
                try {
                    //copyFile(fileNameFrom,fileNameTo);
                    IO_Utilities.copyFile(fileNameFrom, fileNameTo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            mProgressTxt.setText(doneStr);
            mProgress.setVisibility(View.INVISIBLE); // VISIBLE
            mGenAT = null; // po ukonceni vycistit

        }


    }
}



