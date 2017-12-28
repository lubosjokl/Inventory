package com.innovativeproposals.inventorypokus2.InventarVMiestnosti;

/**
 * Created by Lubos on 27.12.17.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

/*
SELECT 	,[itembarcode]		,[itemdescription]	,[roomcodenew]		,[status]		,[datum]		,[datumDispose]		,[datumREAL]		serialnr
	FROM [majetok]

 */

public class DataModelInventarVMiestnosti extends SQLiteOpenHelper {
    protected static final String DB_DATABAZA = "inventory";
    protected static final int DB_VERZIA = 1;
    protected static final String DB_TABULKA = "majetok";

 //   public static final String ATR_ID = "_id";
 //   public static final String ATR_DIVIZIA = "divizia";
 //   public static final String ATR_ODDELENIE = "oddelenie";
 //   public static final String ATR_KODODDELENIE = "kododdelenia";

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

    public DataModelInventarVMiestnosti(Context ctx)
    {
        super(ctx, DB_DATABAZA, null, DB_VERZIA);
    }

    // doplnujuce funkcie
    public ArrayList<HashMap<String, String>> dajZaznamy(String myPoschodieKod) throws URISyntaxException {
        ArrayList<HashMap<String, String>> alVysledky;

        alVysledky = new ArrayList<HashMap<String, String>>();
        String sSQL = "SELECT Id, itembarcode, itemdescription, roomcodenew, status, datum, datumDispose,datumREAL, serialnr FROM " + DB_TABULKA + " WHERE roomcodenew = '"+myPoschodieKod+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sSQL, null);

        //kurzor na prvy zaznam
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
                alVysledky.add(hm);
            } while (cursor.moveToNext()); // kurzor na dalsi zaznam
        }
        return alVysledky;
    }

    public HashMap<String, String> dajZaznam(String id)
    {
        HashMap<String, String> hm = new HashMap<String, String>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sSQL = "SELECT * FROM " + DB_TABULKA + " WHERE _id='"+id+"'";
        Cursor cursor = db.rawQuery(sSQL, null);
        if (cursor.moveToFirst())
        {
            do
            {
                //     hm.put(ATR_DIVIZIA, cursor.getString(1));
               // hm.put(ATR_ODDELENIE, cursor.getString(2));
                //hm.put(ATR_KODODDELENIE, cursor.getString(3));

            } while (cursor.moveToNext());
        }
        return hm;
    }


}


