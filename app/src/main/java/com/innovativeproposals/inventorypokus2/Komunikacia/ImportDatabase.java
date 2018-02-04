package com.innovativeproposals.inventorypokus2.Komunikacia;
//package com.innovativeproposals.inventorypokus2.ImportDatabase;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.innovativeproposals.inventorypokus2.Constants;
import com.innovativeproposals.inventorypokus2.R;

import java.security.SecureRandom;
import java.util.concurrent.ExecutionException;

// komunikacia
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Lubos on 01.02.18.
 */

public class ImportDatabase extends Activity implements View.OnClickListener {

    private Button              mStartBtn;
    private Button              mStartBtnBR;
    private TextView            mProgressTxt;
    private ProgressBar         mProgress;
  //  private GenerateReceiver    mGenRec;
  //  private byte[]              dataBuf = new byte[TOTAL_DATA_POINTS];
  private byte[]              dataBuf;
    private GenAsyncTask        mGenAT;
    private static final int BUFFER_SIZE = 4096;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_bar);
        mStartBtn = (Button) findViewById(R.id.startButton);
        mProgressTxt = (TextView) findViewById(R.id.prog_txt);
        mProgress = (ProgressBar) findViewById(R.id.gen_progress);

        mStartBtn.setOnClickListener(this);
     //   mStartBtnBR.setOnClickListener(this);
    //    mGenRec = new GenerateReceiver();
    }

    @Override
    public void onClick(View v) {
        if (v == mStartBtn) {
            //  Start generating data, do it right here.
            doGenerate();
        }
    }

    private void doGenerate() {

        mProgress.setVisibility(View.VISIBLE);
        mProgress.setProgress(0);
        mProgressTxt.setVisibility(View.VISIBLE);
        //mProgressTxt.setText(R.string.filling_buf);
        mProgressTxt.setText("co tu je?");

        // ziskaj globalne SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName()+ Constants.PREF_FILE_NAME,MODE_PRIVATE);

        String address = sharedPreferences.getString(Constants.KEY_ADDRESS,"not defined"); // druhy parameter je defaultna hodnota
        String port = sharedPreferences.getString(Constants.KEY_PORT,"11235");
        int daysOfInventory = sharedPreferences.getInt(Constants.KEY_DAYS_OF_INVENTORY,5);

        //String fileURL = "http://192.168.1.119:11235";
        String fileURL = address + ":" + port;

        File saveDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        // adresar niekedy nie je vytvoreny a tato funkcia ho vytvori
        assureThatDirectoryExist(saveDir);

      //  String saveDir = "E:/Download";
        try {
            //HttpDownloadUtility.downloadFile(fileURL, saveDir);
            downloadFile(fileURL, saveDir.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
        }


        // teraz vytvorime AsyncTask
        if(mGenAT == null) {
            mGenAT = new GenAsyncTask();
            mGenAT.execute(dataBuf);
        }
    }


    /*    public class HttpDownloadUtility {
        private static final int BUFFER_SIZE = 4096;

        *//**
         * Downloads a file from a URL
         * @param fileURL HTTP URL of the file to be downloaded
         * @param saveDir path of the directory to save the file
         * @throws IOException
         *//*
    */
    public static void downloadFile(String fileURL, String saveDir) throws IOException {
            URL url = new URL(fileURL);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            int responseCode = httpConn.getResponseCode();


            // always check HTTP response code first
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String fileName = "";
                String disposition = httpConn.getHeaderField("Content-Disposition");
                String contentType = httpConn.getContentType();
                int contentLength = httpConn.getContentLength();

                if (disposition != null) {
                    // extracts file name from header field
                    int index = disposition.indexOf("filename=");
                    if (index > 0) {
                        fileName = disposition.substring(index + 10,
                                disposition.length() - 1);
                    }
                } else {
                    // extracts file name from URL
                    fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                            fileURL.length());
                }

                System.out.println("Content-Type = " + contentType);
                System.out.println("Content-Disposition = " + disposition);
                System.out.println("Content-Length = " + contentLength);
                System.out.println("fileName = " + fileName);

                // opens input stream from the HTTP connection
                InputStream inputStream = httpConn.getInputStream();
                String saveFilePath = saveDir + File.separator + fileName;

                // opens an output stream to save into file
                FileOutputStream outputStream = new FileOutputStream(saveFilePath);

                int bytesRead = -1;
                byte[] buffer = new byte[BUFFER_SIZE];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
                inputStream.close();

                System.out.println("File downloaded");
            } else {
                System.out.println("No file to download. Server replied HTTP code: " + responseCode);
            }
            httpConn.disconnect();
        }

        private void assureThatDirectoryExist(File directory){
            if(!directory.exists())
                directory.mkdirs();
        }

        // Samostatna synchronna trieda
    private class GenAsyncTask extends AsyncTask<byte[], Integer , Long> {

        // vstup do nasho spracovania pozadia
        @Override
        protected Long doInBackground(byte[]... arg0) {
            SecureRandom randGen = new SecureRandom();
            int progScale;
            long startTime;
            long endTime;

            startTime = SystemClock.elapsedRealtime();
            progScale = dataBuf.length / 100;

            for (int i = 0; i < dataBuf.length; i++) {
                // sem este test, ci sa medzitym proces nezrusil
                if (isCancelled()) {
                    // proces bol medzicasom ukonceny, odchod zo slucky
                    break;
                }

                dataBuf[i] = (byte) randGen.nextInt();

                if ((i % progScale) == 0) {
                    // zavolame internu metodu publikovania
                    publishProgress(i / progScale);
                }
            }
            endTime = SystemClock.elapsedRealtime();
            return (endTime - startTime);
        }

        // aktualizacia komponenty uzivatelskeho rozhrania
        @Override
        protected void onProgressUpdate(Integer... progress) {
            // progress je na zaklade vysledku onPostExecute?
            mProgress.setProgress(progress[0]);
        }

        // vysledok spracovanie, napr. pocet sekund trvania operacie
        @Override
        protected void onPostExecute(Long result) {
            //String doneStr = getString(R.string.done_fill);
            String doneStr = "hotovo";
            // hodnotu berieme z doInBackground.return (endTime - startTime);
            doneStr += result.toString() + " ms";
            mProgressTxt.setText(doneStr);
            mGenAT = null; // po ukonceni vycistit
        }
    }
}


