package com.innovativeproposals.inventorypokus2.Models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.database.Cursor;

import com.innovativeproposals.inventorypokus2.Constants;

import java.util.ArrayList;


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

    public Integer dajCelkovyPocetInventara(String myRoomCode) { //xx

        Integer results = 0;
        String sSQL = null;
        if(myRoomCode=="")
            sSQL = "SELECT count(*)  FROM majetok";
        else
            sSQL = "SELECT count(*)  FROM majetok where roomcodenew = '"+myRoomCode+"'";

        SQLiteDatabase db = this.getWritableDatabase(); // mName = inventory.db
        Cursor cursor = db.rawQuery(sSQL, null);

        //kurzor na prvy zaznam
        if (cursor.moveToFirst()) {
            do {
                results = cursor.getInt(0);
            } while (cursor.moveToNext()); // kurzor na dalsi zaznam
        }
        cursor.close();
        db.close();
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
        cursor.close();
        db.close();
        return results;
    }

    public ArrayList<String> dajZodpovedneOsoby()  {

        ArrayList<String> results = new ArrayList<>();
        String sSQL = "SELECT meno  FROM osoba order by meno COLLATE NOCASE"; // zohladnuje radenie podla slovenciny

        // vraj mozny aj zapis
        // SELECT name, assignment_id FROM GrammarAssignments ORDER BY name COLLATE LOCALIZED ASC

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sSQL, null);

        //kurzor na prvy zaznam
        if (cursor.moveToFirst()) {
            do {
                results.add(cursor.getString(0));
            } while (cursor.moveToNext()); // kurzor na dalsi zaznam
        }
        cursor.close();
        db.close();
        return results;
    }

    public ArrayList<String> dajTypyMajetku()  {

        ArrayList<String> results = new ArrayList<>();
        String sSQL = "SELECT popis  FROM typmajetku order by popis COLLATE NOCASE"; // lower(popis) ASC

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sSQL, null);

        //kurzor na prvy zaznam
        if (cursor.moveToFirst()) {
            do {
                results.add(cursor.getString(0));
            } while (cursor.moveToNext()); // kurzor na dalsi zaznam
        }
        cursor.close();
        db.close();
        return results;
    }

    public Boolean  GetSQLResultStringNoParam(String sqltxt)
    {
        Cursor cursor;
        boolean myResult = false;
        ArrayList<String> results = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        try
        {
            cursor = db.rawQuery(sqltxt, null); // musi byt v try bloku

            //kurzor na prvy zaznam
            if (cursor.moveToFirst()) {
                myResult = true;
                do {
                    results.add(cursor.getString(0));
                } while (cursor.moveToNext()); // kurzor na dalsi zaznam
            }
        }

        catch(Exception exp) // exp
        {
           // hodnota = "-1";
            //MessageBox.Show(exp.ToString());
            String aa = "a";
        }
        finally {
           // if(cursor != null)
            //    cursor.close();
            db.close();
        }

        return myResult;

    }

    public void checkDatabaseIndex()
    {

        String query = "CREATE INDEX IF NOT EXISTS index_kancelaria_roomcode ON kancelaria (roomcode)";
        SQLiteDatabase db = this.getWritableDatabase(); // mName = inventory.db

        //onCreate(db);


        //        1. CREATE INDEX IF NOT EXISTS index_kancelaria_roomcode ON kancelaria (roomcode);
        //        2. CREATE INDEX IF NOT EXISTS index_majetok_itembarcode_datumREAL ON majetok (itembarcode,datumREAL,serialnr, itemdescription, extranotice);

        try
        {
            db.execSQL(query);
            query = "CREATE INDEX IF NOT EXISTS index_majetok_itembarcode_datumREAL ON majetok (itembarcode,datumREAL,serialnr, itemdescription, extranotice)";
            db.execSQL(query);
        }

        catch(Exception exp) // exp
        {
            // hodnota = "-1";
            String aa = exp.toString();
        }
        finally {
            // if(cursor != null)
            //    cursor.close();
            db.close();
        }

        return;

    }


}
