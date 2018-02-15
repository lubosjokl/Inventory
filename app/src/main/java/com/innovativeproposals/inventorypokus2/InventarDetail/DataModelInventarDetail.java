package com.innovativeproposals.inventorypokus2.InventarDetail;

/**
 * Created by Lubos on 28.12.17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.innovativeproposals.inventorypokus2.Constants;
import com.innovativeproposals.inventorypokus2.Models.Inventar;
import java.net.URISyntaxException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class DataModelInventarDetail extends SQLiteOpenHelper {
    protected static final String DB_DATABAZA = Constants.FILE_DATABASE; //"inventory";
    protected static final int DB_VERZIA = 1;
    protected static final String DB_TABULKA = "majetok";

    // zaklad
    @Override
    public void onCreate(SQLiteDatabase db) {
      /*         db.execSQL("CREATE TABLE " + DB_TABULKA
                + " (" + ATR_ID     + " INTEGER PRIMARY KEY,"
                + ATR_DIVIZIA  + " TEXT"
                + ");");*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS " + DB_TABULKA;
        db.execSQL(query);
        onCreate(db);
    }

    public DataModelInventarDetail(Context ctx)
    {
        super(ctx, DB_DATABAZA, null, DB_VERZIA);
    }

    // doplnujuce funkcie
    public ArrayList<HashMap<String, String>> dajZaznamy(String myPoschodieKod) throws URISyntaxException {
        ArrayList<HashMap<String, String>> alVysledky;

        alVysledky = new ArrayList<HashMap<String, String>>();
        String sSQL = "SELECT Id, itembarcode, itemdescription, roomcodenew, status, datum, datumDispose,datumREAL, serialnr, zodpovednaosoba, datumzaradenia, extranotice, typmajetku " +
                " FROM " + DB_TABULKA + " WHERE itembarcode = '"+myPoschodieKod+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sSQL, null);

        // toto treba prerobit na class, ktory naplnim a vratim
        if (cursor.moveToFirst())
        {
            do
            {
                //id	,[itembarcode] ,		,[itemdescription]		,[roomcodenew]		,[status]		,[datum]		,[datumDispose]		,[datumREAL]
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("id", cursor.getString(0));
                hm.put("itembarcode", cursor.getString(1));
                hm.put("itemdescription", cursor.getString(2));
                hm.put("roomcodenew", cursor.getString(3));
                hm.put("status", cursor.getString(4));
                hm.put("datum", cursor.getString(5));
                hm.put("datumDispose", cursor.getString(6));
                hm.put("datumREAL", cursor.getString(7));
                hm.put("serialnr", cursor.getString(8));
                hm.put("zodpovednaosoba", cursor.getString(9));
                hm.put("datumzaradenia", cursor.getString(10));
                hm.put("extranotice", cursor.getString(11));
                hm.put("typmajetku", cursor.getString(12));
                alVysledky.add(hm);
            } while (cursor.moveToNext()); // kurzor na dalsi zaznam
        }
        return alVysledky;
    }
    public String dajNazovLokality(String myRoomCode) {

       // String result = null;
        String myDivision = null;
        String myOddelenie = null;
        String Zvysok = null;
        String myLocation = "miestnosť nie je určená";

        int kde = myRoomCode.indexOf("-");
        if(kde > 0) {
            myDivision = myRoomCode.substring(0,kde);
          //  myDivision = myDivision + "%";
            Zvysok = myRoomCode.substring(kde+1,myRoomCode.length());
        }

        kde = Zvysok.indexOf("-");
        if(kde > 0) {
          //  myOddelenie = myDivision;
            myOddelenie = myDivision + "-";
            myOddelenie = myOddelenie + Zvysok.substring(0,kde);
        }

        // budova
        String sSQL = "SELECT divizia FROM divizia WHERE KodDivizie =  '" +  myDivision +"'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sSQL, null);

        if (cursor.moveToFirst()) {         //kurzor na prvy zaznam
            do {
                myLocation = cursor.getString(0);
            } while (cursor.moveToNext());
        }

        // oddelenie
        sSQL = "SELECT oddelenie FROM oddelenie WHERE kododdelenia = '" + myOddelenie +  "'";

        cursor = db.rawQuery(sSQL, null);
        if (cursor.moveToFirst()) {
            do {
                myLocation = myLocation + "-" + cursor.getString(0);
            } while (cursor.moveToNext());
        }

        // kancelaria
        sSQL ="SELECT roomdescription FROM kancelaria WHERE roomcode = '" + myRoomCode +  "'";
        cursor = db.rawQuery(sSQL, null);
        if (cursor.moveToFirst()) {
            do {
                myLocation = myLocation + "-" + cursor.getString(0);
            } while (cursor.moveToNext());
        }
        return myLocation;
    }

    public ArrayList<String> dajTypyMajetku() throws URISyntaxException {

        ArrayList<String> results = new ArrayList<>();
        String sSQL = "SELECT popis  FROM typmajetku order by popis ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sSQL, null);

        //kurzor na prvy zaznam
        if (cursor.moveToFirst()) {
            do {
                results.add(cursor.getString(0));
            } while (cursor.moveToNext()); // kurzor na dalsi zaznam
        }
        return results;
    }

    public ArrayList<String> dajZodpovedneOsoby() throws URISyntaxException {

        ArrayList<String> results = new ArrayList<>();
        String sSQL = "SELECT meno  FROM osoba order by meno ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sSQL, null);

        //kurzor na prvy zaznam
        if (cursor.moveToFirst()) {
            do {
                results.add(cursor.getString(0));
            } while (cursor.moveToNext()); // kurzor na dalsi zaznam
        }
        return results;
    }

    public long ulozObrazok(Inventar myInventar) {

        boolean existuje = false;
        long vysledok = 0;
        String obrazokSql = "INSERT INTO MajetokObrazky ( obrazok, itembarcode) VALUES ( ? ,? )";

        String sSQL = "SELECT id  FROM MajetokObrazky where itembarcode = '" + myInventar.getItemBarcode()+ "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sSQL, null);

        //kurzor na prvy zaznam
        if (cursor.moveToFirst()) {
            do {
                existuje = true;
                obrazokSql = "UPDATE MajetokObrazky SET obrazok = ?  WHERE itembarcode = ?";
            } while (cursor.moveToNext()); // kurzor na dalsi zaznam
        }

        try
        {
            // https://stackoverflow.com/questions/7331310/how-to-store-image-as-blob-in-sqlite-how-to-retrieve-it
        SQLiteStatement insertStmt      =   db.compileStatement(obrazokSql);
        insertStmt.clearBindings();
       // insertStmt.bindString(1, Integer.toString(this.accId));
            insertStmt.bindBlob(1, myInventar.getImage());
            insertStmt.bindString(2,myInventar.getItemBarcode());

            Log.d("update","Query: "+insertStmt.toString());
        insertStmt.executeInsert();

        db.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return vysledok;
    }

    public int ulozInventar(Inventar myInventar, String newRoom)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        int aa = 0; // nepouzite
        String mySql = null;
        boolean isNew = false;
        String akaRoomcode = newRoom;
        if(newRoom.equalsIgnoreCase("info"))
            akaRoomcode = myInventar.getRommCode();

        if(myInventar.getId()==0)
            isNew = true;

        Date currentDate = new Date(System.currentTimeMillis());

        SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        String ts =  format1.format(currentDate);
        long millis = System.currentTimeMillis();
        long realDatum = millis/1000;

        try {

            // najprv uloz majetok
            if(isNew == true)
                mySql = "INSERT INTO majetok ( itemdescription, status, roomcodenew, serialnr, zodpovednaosoba, typmajetku, extranotice, datum, datumREAL, itembarcode) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
            else
                mySql = "UPDATE majetok set itemdescription=?, status=?, roomcodenew=?,serialnr=?,zodpovednaosoba=?,typmajetku=?,extranotice=?,datum=?, datumREAL=? where Id = ? ";

            SQLiteStatement insertStmt      =   db.compileStatement(mySql);
            insertStmt.clearBindings();
            // insertStmt.bindString(1, Integer.toString(this.accId));
            insertStmt.bindString(1,myInventar.getItemDescription());
            insertStmt.bindString(2,"10");
            insertStmt.bindString(3,akaRoomcode); // pri vstupe z miestnosti vzdy prepisuj miestnost, pri info ponechaj povodnu miestnost
            insertStmt.bindString(4,myInventar.getSerialNr());
            insertStmt.bindString(5,myInventar.getZodpovednaOsoba());
            insertStmt.bindString(6,myInventar.getTypeMajetku());
            insertStmt.bindString(7,myInventar.getPoznamka());
            insertStmt.bindString(8,ts);
            insertStmt.bindLong(9,realDatum);
            if(isNew) {
                // insert
                insertStmt.bindString(10, myInventar.getItemBarcode());
            }
            else {
                // update
                insertStmt.bindString(10, Integer.toString(myInventar.getId()));
            }

            Log.d("update", "Query: " + insertStmt.toString());
            insertStmt.executeInsert();

            // zapis do tabulky inventura **************************************** // pri info nezapisovat ******* !!!
            if(!myInventar.isInfo()) {
                // teraz uloz do tabulky inventura - tu je len insert
                mySql = "INSERT INTO inventura ( itemdescription, status, roomcode, insertedDatum, itembarcode) VALUES (?, ?, ?,  ?, ?)";

                SQLiteStatement insertStmt2 = db.compileStatement(mySql);
                insertStmt2.clearBindings();
                insertStmt2.bindString(1, myInventar.getItemDescription());
                insertStmt2.bindString(2, "10");
                insertStmt2.bindString(3, myInventar.getRommCode());
                //insertStmt2.bindLong(4,realDatum); // NOK
                insertStmt2.bindString(4, ts); // NOK
                insertStmt2.bindString(5, myInventar.getItemBarcode()); // NOK

                Log.d("insert", "Query: " + insertStmt2.toString());
                insertStmt2.executeInsert();

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }



        return aa;

    }



}



