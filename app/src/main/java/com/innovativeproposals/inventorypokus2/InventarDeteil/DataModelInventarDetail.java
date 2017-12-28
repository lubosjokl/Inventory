package com.innovativeproposals.inventorypokus2.InventarDeteil;

/**
 * Created by Lubos on 28.12.17.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;


public class DataModelInventarDetail extends SQLiteOpenHelper {
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

    public DataModelInventarDetail(Context ctx)
    {
        super(ctx, DB_DATABAZA, null, DB_VERZIA);
    }

    // doplnujuce funkcie
    public ArrayList<HashMap<String, String>> dajZaznamy(String myPoschodieKod) throws URISyntaxException {
        ArrayList<HashMap<String, String>> alVysledky;

        alVysledky = new ArrayList<HashMap<String, String>>();

        /*


        toto treba volat aj s obrazkom a naplnit class, kde obrazok bude blob, zvysok su string
        Ale neviem ako to naplnit do maphash, kedze ta vyzaduje string - string, pripadne zmen na string-list?

        String sSQL = "SELECT aa.Id,aa.itembarcode, aa.itemdescription,aa.status,aa.datum,bb.obrazok,aa.datumREAL FROM majetok aa left join  MajetokObrazky bb on bb.itembarcode = aa.itembarcode " +
                "WHERE aa.roomcodenew = '"+myPoschodieKod+"' order by aa.datumREAL asc";

        kuk sem
        https://junjunguo.com/blog/android-sqlite-image-view-b/

        */
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
}



