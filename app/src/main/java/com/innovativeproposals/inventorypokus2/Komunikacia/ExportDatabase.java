package com.innovativeproposals.inventorypokus2.Komunikacia;

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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.innovativeproposals.inventorypokus2.Constants;
import com.innovativeproposals.inventorypokus2.R;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Lubos on 12.02.18.
 */

// Uploading Sqlite database to a remote server


public class ExportDatabase extends AppCompatActivity implements View.OnClickListener {

    private Button mStartBtn;
    private TextView mProgressTxt;
    private ProgressBar mProgress;


    private static final int    SIZE_KB = 1024;
    private static final int    SIZE_MB = (SIZE_KB * 1024);
    private static final int    TOTAL_DATA_POINTS = (4 * SIZE_MB);
    //public String fileNameFrom;
    //public String fileNameTo;
    String TAG  = "Export database";

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
               // Toast.makeText(getBaseContext(), "Network is not Available", Toast.LENGTH_SHORT).show();
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
        mProgressTxt.setText("Exportujem databazu ...");

        // teraz vytvorime AsyncTask
        if (mGenAT == null) {
            mGenAT = new GenAsyncTask();
            mGenAT.execute(dataBuf);
        }
    }


    // Samostatna asynchronna trieda
    // byte[] je vstup do nasho spracovania pozadia
    // Integer je typ ktory sa pouziva na aktualizaciu komponentu uzivatelskeho rozhrania
    // Long je vysledok spracovanie
    private class GenAsyncTask extends AsyncTask<byte[], Integer, String> {

        @Override
        protected void onPostExecute(String  result) {
            String doneStr; // = "hotovo";

            if(result.equals("nofile")) {
                doneStr = getString(R.string.NeexportovalSaZiadnySuborPreverteFirewall);
                mProgressTxt.setBackgroundResource(R.color.colorNO );
                publishProgress(0);
            } else {
                doneStr = getString(R.string.ImportDone) + result + " ms";
                mProgressTxt.setBackgroundResource(R.color.colorZelena  );
            }


            mProgress.setVisibility(View.INVISIBLE); // VISIBLE
            mProgressTxt.setText(doneStr);
            mGenAT = null; // po ukonceni vycistit

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {

            mProgress.setProgress(progress[0]);
        }

        @Override
        protected String doInBackground(byte[]... bytes) {


            long startTime;
            long endTime;
            startTime = SystemClock.elapsedRealtime();

            PackageManager m = getPackageManager();
            String saveDir = getPackageName();
            PackageInfo p = null;
            try {
                p = m.getPackageInfo(saveDir,0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            //saveDir = p.applicationInfo.dataDir + "/files"; // /data/user/0/com.innovativeproposals.inventorypokus2
            //fileNameTo = p.applicationInfo.dataDir + "/databases";
            String sourceFileUri = p.applicationInfo.dataDir + "/databases/" + Constants.FILE_DATABASE;
            String fileNameTo = p.applicationInfo.dataDir + "/databases/" + Constants.FILE_DATA_PDA2PC;


            SharedPreferences sharedPreferences = getSharedPreferences(getPackageName() + Constants.PREF_FILE_NAME, MODE_PRIVATE);
            String address = sharedPreferences.getString(Constants.KEY_ADDRESS, "not defined"); // druhy parameter je defaultna hodnota
            String port = sharedPreferences.getString(Constants.KEY_PORT, "11235");
            String upLoadServerUri = address + ":" + port; //String fileURL = "http://192.168.1.119:11235";

            // https://stackoverflow.com/questions/40966093/which-content-type-to-use-when-uploading-sqlite-db-file-to-a-server

            try {
                IO_Utilities.copyFile(sourceFileUri,fileNameTo);
            } catch (IOException e) {
                e.printStackTrace();
            }

            sourceFileUri = fileNameTo;

            String fileName = sourceFileUri; // the path to transferDataiPOD2PC.db
            int serverResponseCode = 0;

            HttpURLConnection connection = null;
            DataOutputStream dataOutputStream = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            File sourceFile = new File(sourceFileUri);

            if (!sourceFile.isFile()) {

                Log.e("uploadFile", "Source File not exist ");
                // TODO  return 0;
            } else {
                try {


                    long progScale = sourceFile.length();

                    // open a URL connection to the Servlet
                    FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    URL url = new URL(upLoadServerUri);

                    // Open a HTTP  connection to  the URL
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true); // Allow Inputs
                    connection.setDoOutput(true); //Triggers  http POST method.
                    connection.setConnectTimeout(2000); // 2 Sekundy

                    connection.setUseCaches(false); // Don't use a Cached Copy
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                    connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                    //conn.setRequestProperty("Content-Type", "application/x-sqlite3; boundary=" + boundary);// when I tried this it didn't work, so you can delete this line
                    connection.setRequestProperty("uploadedfile", fileName);

                    dataOutputStream = new DataOutputStream(connection.getOutputStream());

                   // dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd); // treba?
                    //dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=" + fileName + "" + lineEnd);
                   // dataOutputStream.writeBytes( fileName ); treba?
                   // dataOutputStream.writeBytes(lineEnd); // treba?

                    // create a buffer of  maximum size
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    int kolo = 1;
                    int aa = 1;

                    while (bytesRead > 0) {

                        dataOutputStream.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                        // zverejnenovanie vysledku, musim posielat integer

                        aa = (Math.round(100*kolo*bufferSize / progScale));
                        if(aa>100) kolo = 0;
                        kolo++;
                        publishProgress(aa);

                    }

                    // send multipart form data necesssary after file data...

                    publishProgress(100);

                    dataOutputStream.writeBytes(lineEnd);
                    dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    // Responses from the server (code and message)
                    serverResponseCode = connection.getResponseCode();
                    String serverResponseMessage = connection.getResponseMessage();

                    Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);

                    if (serverResponseCode == 200) {

                        String msg = "server respose code ";
                        Log.i(TAG, msg + serverResponseCode);
                        StringBuilder result = new StringBuilder();

                        InputStream in = new BufferedInputStream(connection.getInputStream());

                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }

                        //JSONObject mResponseJSONObject = new JSONObject(String.valueOf(result)); //convert the respons in json
                        Log.i(TAG, msg + result);
                    }

                    //close the streams //
                    fileInputStream.close();
                    dataOutputStream.flush();
                    dataOutputStream.close();

                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                    Log.e(TAG, "MalformedURLException Exception : check script url.");
                    Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
                    return "nofile";
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Upload file to server Exception : " + e.getMessage(), e);
                    return "nofile";
                }

               // TODO return serverResponseCode;

            } // End else block

            endTime = SystemClock.elapsedRealtime();
            return String.valueOf(endTime - startTime);

        }
    }


}
