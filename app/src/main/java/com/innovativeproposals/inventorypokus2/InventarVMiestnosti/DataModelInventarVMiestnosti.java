package com.innovativeproposals.inventorypokus2.InventarVMiestnosti;

/**
 * Created by Lubos on 27.12.17.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;

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

    // doplnujuce funkcie
    public List<Inventar> dajNoveZaznamy(String myPoschodieKod) throws URISyntaxException {
        ArrayList<Inventar> results = new ArrayList<>();


        String sSQL = "SELECT aa.Id,aa.itembarcode, aa.itemdescription, aa.roomcodenew,aa.status, aa.datum,aa.datumDispose, aa.serialnr, bb.obrazok FROM majetok aa " +
                "left join  MajetokObrazky bb on bb.itembarcode = aa.itembarcode " + "WHERE aa.roomcodenew = '" + myPoschodieKod + "' order by aa.datumREAL asc";


//        String sSQL = "SELECT Id, itembarcode, itemdescription, roomcodenew, status, datum, datumDispose,datumREAL, serialnr FROM " + DB_TABULKA + " WHERE roomcodenew = '"+myPoschodieKod+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sSQL, null);

        //kurzor na prvy zaznam
        if (cursor.moveToFirst()) {
            do {
                //id	,[itembarcode] ,		,[itemdescription]		,[roomcodenew]		,[status]		,[datum]		,[datumDispose]		,[datumREAL]
                Inventar newItem = new Inventar();
                newItem.setId(cursor.getInt(0));
                newItem.setItemBarcode(cursor.getString(1));
                newItem.setItemDescription(cursor.getString(2));
                newItem.setRommCode(cursor.getString(3));
                newItem.setStatus(cursor.getString(4));
                newItem.setDatum(cursor.getString(5));
                newItem.setDatum_discarded(cursor.getString(6));
                newItem.setSerialNr(cursor.getString(7));
                newItem.setImage(cursor.getBlob(8));
                ;

                results.add(newItem);

            } while (cursor.moveToNext()); // kurzor na dalsi zaznam
        }

        return results;
    }

    public ArrayList<HashMap<String, String>> dajZaznamy(String myPoschodieKod) throws URISyntaxException {
        ArrayList<HashMap<String, String>> alVysledky;

        alVysledky = new ArrayList<HashMap<String, String>>();

        /*

        toto treba volat aj s obrazkom a naplnit class, kde obrazok bude blob, zvysok su string
        Ale neviem ako to naplnit do maphash, kedze ta vyzaduje string - string, pripadne zmen na string-list?

        String sSQL = "SELECT aa.Id,aa.itembarcode, aa.itemdescription,aa.status,aa.datum,bb.obrazok,aa.datumREAL FROM majetok aa left join  MajetokObrazky bb on bb.itembarcode = aa.itembarcode " +
                "WHERE aa.roomcodenew = '"+myPoschodieKod+"' order by aa.datumREAL asc";

        */
        String sSQL = "SELECT Id, itembarcode, itemdescription, roomcodenew, status, datum, datumDispose,datumREAL, serialnr FROM " + DB_TABULKA + " WHERE roomcodenew = '" + myPoschodieKod + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sSQL, null);

        //kurzor na prvy zaznam
        if (cursor.moveToFirst()) {
            do {
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
                alVysledky.add(hm);
            } while (cursor.moveToNext()); // kurzor na dalsi zaznam
        }
        return alVysledky;
    }
}


