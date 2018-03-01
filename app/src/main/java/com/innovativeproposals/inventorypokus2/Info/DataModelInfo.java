package com.innovativeproposals.inventorypokus2.Info;

/**
 * Created by Lubos on 01.03.18.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.innovativeproposals.inventorypokus2.Constants;
import com.innovativeproposals.inventorypokus2.Models.Inventar;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class DataModelInfo extends SQLiteOpenHelper {

    protected static final String DB_DATABAZA =  Constants.FILE_DATABASE; //"inventory";
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
     /*   String query = "DROP TABLE IF EXISTS " + DB_TABULKA;
        db.execSQL(query);
        onCreate(db);*/
    }

    public DataModelInfo(Context ctx) {
       // super(context, name, factory, version);
        super(ctx, DB_DATABAZA, null, DB_VERZIA);
    }

    public List<Inventar> getInventarList(String myFilter) throws URISyntaxException {

        ArrayList<Inventar> results = new ArrayList<>();
        String sSQL = null;

//        String myNazov2 = "'"+ "%"+myFilter+"%"+"'";
  //      String myKod2 = "'"+myFilter+"%"+"'";

      //  String myNazov2 = "'%"+myFilter+"%'";
        String myNazov2 = "%"+myFilter+"%";
        String myKod2 =  myFilter + "%";



        //myNazov2 = [myNazov2 stringByAppendingString:myFilter];
        //myNazov2 = [myNazov2 stringByAppendingString:@"%"];
        //myKod2 = [myKod2 stringByAppendingString:@"%"];

/*        if(myFilter == "N"){ // nevyradeny
            sSQL = "SELECT aa.Id,aa.itemdescription,aa.itembarcode,aa.status,aa.datum,bb.obrazok FROM majetok aa left join  MajetokObrazky bb on bb.itembarcode = aa.itembarcode " +
                    "WHERE (aa.datumDispose =\\\"\\\" or aa.datumDispose IS NULL) and aa.itembarcode like ? or aa.serialnr like ? or aa.itemdescription like ? " +
                    "or aa.extranotice like ?  order by aa.datumREAL asc limit 100";
        } else */

        sSQL = "SELECT aa.Id,aa.itemdescription,aa.itembarcode,aa.status,aa.datum,bb.obrazok, aa.datumzaradenia, aa.serialnr, aa.datumvyradenia, aa.zodpovednaosoba, aa.typmajetku," +
                "aa.obstaravaciacena, aa.extranotice, aa.datumReal " +
                "FROM majetok aa left join  MajetokObrazky bb on bb.itembarcode = aa.itembarcode " +
                "WHERE aa.itembarcode like '" + myKod2 +"' or aa.serialnr like '" + myKod2 +"' or aa.itemdescription like '" + myNazov2 +
                "' or aa.extranotice like '" + myNazov2 + "' order by aa.datumREAL asc limit 100";


        //order by aa.datumREAL asc
        // order by status, datum desc

        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement selectStmt  =   db.compileStatement(sSQL);

        Cursor cursor = db.rawQuery(sSQL, null);

        //kurzor na prvy zaznam
        if (cursor.moveToFirst()) {
            do {

                Inventar newItem = new Inventar();
                newItem.setId(cursor.getInt(0));
                newItem.setItemDescription(cursor.getString(1));
                newItem.setItemBarcode(cursor.getString(2));
                newItem.setStatus(cursor.getString(3));
                newItem.setDatum(cursor.getString(4));
                newItem.setImage(cursor.getBlob(5));
                newItem.setDatum_added(cursor.getString(6)) ;
                newItem.setSerialNr(cursor.getString(7));
                newItem.setDatum_discarded(cursor.getString(8));
                newItem.setZodpovednaOsoba(cursor.getString(9));
                newItem.setTypeMajetku(cursor.getString(10));
                newItem.setPrice(cursor.getString(11));
                newItem.setPoznamka(cursor.getString(12));
                newItem.setDatumReal((cursor.getLong(13)));

                results.add(newItem);

            } while (cursor.moveToNext()); // kurzor na dalsi zaznam
        }

        return results;
    }
}
