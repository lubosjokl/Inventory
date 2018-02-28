package com.innovativeproposals.inventorypokus2.Komunikacia;

/**
 * Created by Lubos on 01.02.18.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.*;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import android.location.*;
import android.support.v7.app.AppCompatActivity;
import android.util.*;
import android.os.*;

import com.innovativeproposals.inventorypokus2.Constants;

import java.io.*;
//import java.net.HttpURLConnection;
//import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.util.*;


// export file
// https://stackoverflow.com/questions/11766878/sending-files-using-post-with-httpurlconnection


public class IO_Utilities extends Activity {
    private final boolean _useGpsToGetLocation = true;


   /* Context _context;
    public IO_Utilities(Context context) {
        _context = context;
    }*/


    static void copyFile(String src, String dst) throws IOException {
        //InputStream in = new FileInputStream(src);

        try {
            InputStream in = new FileInputStream(src); // osetri ked nepresiel import
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
                in.close();
            }
        } finally {
            // in.close();
        }
    }

    public boolean fileExist (String filePath)  {

        boolean jeTam = false;
        try {

            File file = new File(filePath);
            if(file.exists())
                jeTam = true;

        } finally {
            // in.close();
        }
        return jeTam;
    }

    public  String getDBPath() {

        PackageManager m = getPackageManager();


        String saveDir = getPackageName();
        PackageInfo p = null;
        try {
            p = m.getPackageInfo(saveDir,0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String sourceFileUri = p.applicationInfo.dataDir + "/databases/";
       // String sourceFileUri = p.applicationInfo.dataDir + "/databases/" + Constants.FILE_DATABASE;
       // String fileNameTo = p.applicationInfo.dataDir + "/databases/" + Constants.FILE_DATA_PDA2PC;

        return sourceFileUri;

    }


    // Append the location timestamp, lat/lng, and address to the specified file
    // NOTE: ** If running on an emulator, the emulator must be created to include an SD card
    public void save(Location location, String address, String fileName){
        try{
            // Retrieve the appropriate location on the SD card to great general-use,
            // publicly accessible files
            File targetDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            // adresar niekedy nie je vytvoreny a tato funkcia ho vytvori
            assureThatDirectoryExist(targetDir);

            // Open or create the file so that new content is appended to the existing and
            // wrap in a buffered writer (as opposed to doing direct physical writes on
            // each write operation)
            File outFile = new File(targetDir, fileName);
            FileWriter fileWriter =new FileWriter(outFile, true);
            BufferedWriter writer = new BufferedWriter(fileWriter);

            // Format the content and write it to the file
            String outLine = String.format("%s:%f/%f", DateFormat.getDateTimeInstance().format(location.getTime()), location.getLatitude(), location.getLongitude());
            writer.write(outLine);
            writer.write(address);

            // Flush the buffered writer's buffers and close the file
            writer.flush();
            writer.close();
            fileWriter.close();
        }
        catch (Exception ex){
            Log.e("Worker.save", ex.getMessage());
        }
    }

    private void assureThatDirectoryExist(File directory){
        if(!directory.exists())
            directory.mkdirs();
    }

/*
    private String reverseGeoCodeWithWebService(Location location){
        StringBuilder stringBuilder = new StringBuilder();
        String addressDescription = null;
        try {
            // Create the Google Web Service URL to use to retrieve the address for the lat/lng
            String serviceUrl =
                    String.format("http://maps.google.com/maps/api/geocode/xml?sensor=false&latlng=%f,%f",
                            location.getLatitude(), location.getLongitude());

            InputStream stream;
            String a1 = String.valueOf(location.getLatitude());
            String a2 = String.valueOf(location.getLongitude());
            String poloha = "http://maps.google.com/maps/api/geocode/xml?sensor=false&latlng="+a1+","+a2;
            URL url = new URL(poloha);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                stream = new BufferedInputStream(urlConnection.getInputStream());
                //readStream(in);
            } finally {
                urlConnection.disconnect();
            }


            HttpClient httpclient = new DefaultHttpClient();
            // Create the HTTP Client and make the call
            HttpGet httpGet = new HttpGet(serviceUrl);
            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(httpGet);
            // Retrieve the Web Service response and wrap in a Reader
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();*//*

            InputStreamReader reader = new InputStreamReader(stream);

            // Use the XML Parse to locate the formatted address in the response
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(reader);
            boolean isAddressNode = false;
            int eventType = xpp.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT){
                if(eventType == XmlPullParser.START_TAG){
                    String tagName = xpp.getName();
                    if(tagName.equalsIgnoreCase("formatted_address")){
                        // This is the formatted address element so set the flag indicating
                        //  that we need to read the address from the next text element
                        isAddressNode = true;
                    }
                }
                else if (isAddressNode && eventType == XmlPullParser.TEXT){
                    // This is the text element w/in the formatted_address so read it
                    //  then exit because we have what we came for
                    addressDescription = xpp.getText();
                    break;

                }
                eventType = xpp.next();
            }
        } catch (Exception ex) {
            Log.e("GeoCodeWithWebService", ex.getMessage());
        }

        return addressDescription;
    }
*/



    private URL ConvertToUrl(String urlStr) {
        try {
            URL url = new URL(urlStr);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(),
                    url.getHost(), url.getPort(), url.getPath(),
                    url.getQuery(), url.getRef());
            url = uri.toURL();
            return url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}


