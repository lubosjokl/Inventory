package com.innovativeproposals.inventorypokus2.Budova;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.content.ContentValues;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Lubos on 26.12.17.
 */
/*
SELECT [Id]
        ,[divizia]
        ,[KodDivizie]
        FROM [divizia]
        ORDER BY [Id] DESC LIMIT 500*/

public class DataModelBudova extends SQLiteOpenHelper {
    protected static final String DB_DATABAZA = "inventory";
    protected static final int DB_VERZIA = 1;
    protected static final String DB_TABULKA = "divizia";

    public static final String ATR_ID = "_id";
    public static final String ATR_DIVIZIA = "divizia";
    public static final String ATR_KODDIVIZIE = "KodDivizie";

    // zaklad
    @Override
    public void onCreate(SQLiteDatabase db) {
         /*     db.execSQL("CREATE TABLE " + DB_TABULKA
                + " (" + ATR_ID     + " INTEGER PRIMARY KEY,"
                + ATR_DIVIZIA  + " TEXT"

                + ");");
                */
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS " + DB_TABULKA;
        db.execSQL(query);
        onCreate(db);
    }


    public DataModelBudova(Context ctx)
    {
        super(ctx, DB_DATABAZA, null, DB_VERZIA);
    }

    // doplnujuce funkcie
    public ArrayList<HashMap<String, String>> dajZaznamy()
    {
        ArrayList<HashMap<String, String>> alVysledky;
        alVysledky = new ArrayList<HashMap<String, String>>();
        String sSQL = "SELECT Id, divizia, KodDivizie FROM " + DB_TABULKA;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sSQL, null);

        //kurzor na prvy zaznam
        if (cursor.moveToFirst())
        {
            do
            {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put(ATR_ID, cursor.getString(0));
                hm.put(ATR_DIVIZIA, cursor.getString(1));
                hm.put(ATR_KODDIVIZIE, cursor.getString(2));

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
                hm.put(ATR_DIVIZIA, cursor.getString(1));

            } while (cursor.moveToNext());
        }
        return hm;
    }


}
