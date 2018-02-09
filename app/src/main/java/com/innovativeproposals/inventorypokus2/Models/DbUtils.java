package com.innovativeproposals.inventorypokus2.Models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.database.Cursor;

import com.innovativeproposals.inventorypokus2.Constants;


/**
 * Created by Lubos on 02.01.18.
 */


public class DbUtils extends SQLiteOpenHelper {
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

    public DbUtils(Context ctx) {
        super(ctx, DB_DATABAZA, null, DB_VERZIA);
    }

// doplnujuce funkcie

    public Integer dajCelkovyPocetInventara(String myRoomCode) {

        Integer results = 0;
        String sSQL = null;
        if(myRoomCode=="")
            sSQL = "SELECT count(*)  FROM majetok";
        else
            sSQL = "SELECT count(*)  FROM majetok where roomcodenew = '"+myRoomCode+"'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sSQL, null);

        //kurzor na prvy zaznam
        if (cursor.moveToFirst()) {
            do {
                results = cursor.getInt(0);
            } while (cursor.moveToNext()); // kurzor na dalsi zaznam
        }
        return results;
    }

    public Integer dajPocetSpracovanehoInventara() {

        Integer results = 0;
        String sSQL = "SELECT count(*) FROM majetok where status = 10 ";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sSQL, null);

        //kurzor na prvy zaznam
        if (cursor.moveToFirst()) {
            do {
                results = cursor.getInt(0);
            } while (cursor.moveToNext()); // kurzor na dalsi zaznam
        }
        return results;
    }


}
