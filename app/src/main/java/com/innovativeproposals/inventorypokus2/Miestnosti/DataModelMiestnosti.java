package com.innovativeproposals.inventorypokus2.Miestnosti;

/**
 * Created by Lubos on 26.12.17.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;

import com.innovativeproposals.inventorypokus2.Constants;
import com.innovativeproposals.inventorypokus2.Models.DbUtils;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/*
SELECT [Id]
		,[roomcode]
		,[roomdescription]
	FROM [kancelaria]
 */

public class DataModelMiestnosti  extends SQLiteOpenHelper {
    private  static final String DB_DATABAZA = Constants.FILE_DATABASE; //"inventory";
    private static final int DB_VERZIA = 1;
    private static final String DB_TABULKA = "kancelaria";

    private static final String ATR_ID = "_id";
    private static final String ATR_ROOMCODE = "roomcode";
    private static final String ATR_ROOMDESCRIPTION = "roomdescription";
    private static final String ATR_KODMIESTNOSTI = "kodmiestnosti";


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


    public DataModelMiestnosti(Context ctx)
    {
        super(ctx, DB_DATABAZA, null, DB_VERZIA);
    }

    // doplnujuce funkcie
    public ArrayList<HashMap<String, String>> dajZaznamy(String myPoschodieKod) throws URISyntaxException {
        ArrayList<HashMap<String, String>> alVysledky;

        alVysledky = new ArrayList<HashMap<String, String>>();
        String sSQL = "SELECT id,roomcode, roomdescription FROM " + DB_TABULKA + " WHERE roomcode like '"+myPoschodieKod+"%' ORDER BY roomdescription";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sSQL, null);

        String retazec;

        //kurzor na prvy zaznam
        if (cursor.moveToFirst())
        {
            do
            {
                HashMap<String, String> hm = new HashMap<String, String>();
                //  hm.put(ATR_ID, cursor.getString(0));
                //  hm.put(ATR_DIVIZIA, cursor.getString(1));
                hm.put(ATR_ROOMCODE, cursor.getString(1));
                hm.put(ATR_ROOMDESCRIPTION, cursor.getString(2));


                int celkomInventarov = dajCelkovyPocetInventara(cursor.getString(1));
                int spracovanychInventarov = dajPocetSpracovanehoInventara(cursor.getString(1));

                retazec = Integer.toString(spracovanychInventarov)+"/"+Integer.toString(celkomInventarov);
                hm.put(ATR_KODMIESTNOSTI , retazec);


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
        String sSQL = "SELECT * FROM " + DB_TABULKA + " WHERE _id='"+id+"'";
        Cursor cursor = db.rawQuery(sSQL, null);
        if (cursor.moveToFirst())
        {
            do
            {
                //     hm.put(ATR_DIVIZIA, cursor.getString(1));
                hm.put(ATR_ROOMCODE, cursor.getString(2));
                hm.put(ATR_ROOMDESCRIPTION, cursor.getString(3));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return hm;
    }

    private Integer dajCelkovyPocetInventara(String myRoomCode) {

        Integer results = 0;
        String sSQL;
        if(Objects.equals(myRoomCode, ""))
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
        cursor.close();
        return results;
    }

    private Integer dajPocetSpracovanehoInventara(String myRoomCode) {

        Integer results = 0;
        String sSQL;
        if(Objects.equals(myRoomCode, ""))
            sSQL = "SELECT count(*) FROM majetok where status = 10 ";
        else
            sSQL = "SELECT count(*) FROM majetok where status = 10 and roomcodenew = '"+myRoomCode+"'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sSQL, null);

        //kurzor na prvy zaznam
        if (cursor.moveToFirst()) {
            do {
                results = cursor.getInt(0);
            } while (cursor.moveToNext()); // kurzor na dalsi zaznam
        }
        cursor.close();
        return results;
    }



}