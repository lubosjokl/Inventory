package com.innovativeproposals.inventorypokus2.InventarVMiestnosti;

/**
 * Created by Lubos on 27.12.17.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.util.Log;

import com.innovativeproposals.inventorypokus2.Models.Inventar;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/*
SELECT 	,[itembarcode]		,[itemdescription]	,[roomcodenew]		,[status]		,[datum]		,[datumDispose]		,[datumREAL]		serialnr
	FROM [majetok]

 */

public class DataModelInventarVMiestnosti extends SQLiteOpenHelper {
    protected static final String DB_DATABAZA = "inventory";
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

    public DataModelInventarVMiestnosti(Context ctx) {
        super(ctx, DB_DATABAZA, null, DB_VERZIA);
    }

    public String dajNazovMiestnosti(String myRoomCode) {

        String result = null;
        String sSQL = "SELECT roomdescription  FROM kancelaria  " +
                    "WHERE roomcode = '" + myRoomCode +"'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sSQL, null);

        //kurzor na prvy zaznam
        if (cursor.moveToFirst()) {
            do {

                result = cursor.getString(0);

            } while (cursor.moveToNext()); // kurzor na dalsi zaznam
        }

        return result;
    }

    // doplnujuce funkcie
    public List<Inventar> dajNoveZaznamy(String myKancelariaKod, String myBarCode) throws URISyntaxException {

        ArrayList<Inventar> results = new ArrayList<>();
        String sSQL = null;
        Log.d("skenujem","Datovy Model 1");

        if(myKancelariaKod != "" && myBarCode==""){
            sSQL = "SELECT aa.Id,aa.itembarcode, aa.itemdescription, aa.roomcodenew,aa.status, aa.datum,aa.datumzaradenia, aa.serialnr, bb.obrazok, aa.datumvyradenia, aa.zodpovednaosoba," +
                    "aa.typmajetku, aa.obstaravaciacena, aa.extranotice, aa.datumReal FROM majetok aa " +
                    "left join  MajetokObrazky bb on bb.itembarcode = aa.itembarcode " + "WHERE aa.roomcodenew = '" + myKancelariaKod + "' order by aa.datumREAL asc";
        }

        if(myBarCode !="" && myKancelariaKod == ""){
            sSQL = "SELECT aa.Id,aa.itembarcode, aa.itemdescription, aa.roomcodenew,aa.status, aa.datum,aa.datumzaradenia, aa.serialnr, bb.obrazok, aa.datumvyradenia, aa.zodpovednaosoba, " +
                    "aa.typmajetku, aa.obstaravaciacena, aa.extranotice, aa.datumReal FROM majetok aa " +
                    "left join  MajetokObrazky bb on bb.itembarcode = aa.itembarcode " + "WHERE aa.itembarcode = '" + myBarCode + "' order by aa.datumREAL asc";
        }


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sSQL, null);

        //kurzor na prvy zaznam
        if (cursor.moveToFirst()) {
            do {

                Inventar newItem = new Inventar();
                newItem.setId(cursor.getInt(0));
                newItem.setItemBarcode(cursor.getString(1));
                newItem.setItemDescription(cursor.getString(2));
                newItem.setRommCode(cursor.getString(3));
                newItem.setStatus(cursor.getString(4));
                newItem.setDatum(cursor.getString(5));
                newItem.setDatum_added(cursor.getString(6));
                newItem.setSerialNr(cursor.getString(7));
                newItem.setImage(cursor.getBlob(8));
                newItem.setDatum_discarded(cursor.getString(9));
                newItem.setZodpovednaOsoba(cursor.getString(10));
                newItem.setTypeMajetku(cursor.getString(11));
                newItem.setPrice(cursor.getString(12));
                newItem.setPoznamka(cursor.getString(13));
                newItem.setDatumReal((cursor.getLong(14)));

                results.add(newItem);

            } while (cursor.moveToNext()); // kurzor na dalsi zaznam
        }

        return results;
    }

}


