package com.innovativeproposals.inventorypokus2.Poschodie;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;

import com.innovativeproposals.inventorypokus2.Constants;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Lubos on 26.12.17.
 */

/*
SELECT [Id]        ,[divizia]        ,[oddelenie]        ,[kododdelenia]        FROM [oddelenie]*/

public class DataModelPoschodie  extends SQLiteOpenHelper {
    private static final String DB_DATABAZA = Constants.FILE_DATABASE; //"inventory";
    private static final int DB_VERZIA = 1;
    //private static final String DB_TABULKA = "oddelenie";

    public static final String ATR_ID = "_id";
    public static final String ATR_DIVIZIA = "divizia";
    public static final String ATR_ODDELENIE = "oddelenie";
    public static final String ATR_KODODDELENIE = "kododdelenia";

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
        String query = "DROP TABLE IF EXISTS oddelenie";
        db.execSQL(query);
        onCreate(db);
    }


    public DataModelPoschodie(Context ctx)
    {
        super(ctx, DB_DATABAZA, null, DB_VERZIA);
    }

    // doplnujuce funkcie
    public ArrayList<HashMap<String, String>> dajZaznamy(String myDivizia) throws URISyntaxException {
        ArrayList<HashMap<String, String>> alVysledky;

        alVysledky = new ArrayList<HashMap<String, String>>();

        String sSQL = "SELECT Id, divizia, oddelenie, kododdelenia FROM oddelenie WHERE divizia='"+myDivizia+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sSQL, null);

        //kurzor na prvy zaznam
        if (cursor.moveToFirst())
        {
            do
            {
                HashMap<String, String> hm = new HashMap<String, String>();
              //  hm.put(ATR_ID, cursor.getString(0));
              //  hm.put(ATR_DIVIZIA, cursor.getString(1));
                hm.put(ATR_ODDELENIE, cursor.getString(2));
                hm.put(ATR_KODODDELENIE, cursor.getString(3));
                alVysledky.add(hm);
            } while (cursor.moveToNext()); // kurzor na dalsi zaznam
        }
        cursor.close();
        return alVysledky;
    }

    public HashMap<String, String> dajZaznam(String id)
    {
        HashMap<String, String> hm = new HashMap<String, String>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sSQL = "SELECT * FROM oddelenie WHERE _id='"+id+"'";
        Cursor cursor = db.rawQuery(sSQL, null);
        if (cursor.moveToFirst())
        {
            do
            {
           //     hm.put(ATR_DIVIZIA, cursor.getString(1));
                hm.put(ATR_ODDELENIE, cursor.getString(2));
                hm.put(ATR_KODODDELENIE, cursor.getString(3));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return hm;
    }


}

