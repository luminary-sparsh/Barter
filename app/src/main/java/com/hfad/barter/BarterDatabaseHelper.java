package com.hfad.barter;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import static android.content.ContentValues.TAG;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

class BarterDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "barter"; // the name of our database
    private static final int DB_VERSION = 1; // the version of the database

    BarterDatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        //creates the database with following attributes
        db.execSQL("CREATE TABLE TRANSACTIONS (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "LEND TEXT, ITEMNAME TEXT, LBNAME TEXT, DESCRIPTION TEXT, DATETIME TEXT, FAVORITE TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        //will be used when the upgrade and downgrade functionality will be enabled.
    }


    public static void insertTransaction(SQLiteDatabase db,String lend,String itemName,String LBname,
                                         String description,String date, String fav) {
        ContentValues transactionValues = new ContentValues();
        transactionValues.put("LEND",lend);
        transactionValues.put("ITEMNAME", itemName);
        transactionValues.put("LBNAME", LBname);
        transactionValues.put("DESCRIPTION", description);
        transactionValues.put("DATETIME", date);
        transactionValues.put("FAVORITE",fav);
        db.insert("TRANSACTIONS", null, transactionValues);

    }

    public static  void printDB(SQLiteDatabase db){
        Log.d(TAG, "getTableAsString called");
        String tableString = String.format("Table %s:\n", "TRANSACTIONS");
        Cursor allRows  = db.rawQuery("SELECT * FROM " + "TRANSACTIONS", null);
        if (allRows.moveToFirst() ){
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String columnname: columnNames) {
                    tableString += String.format("%s: %s\n", columnname,
                            allRows.getString(allRows.getColumnIndex(columnname)));
                }
                tableString += "\n";

            } while (allRows.moveToNext());
        }
        Log.d(TAG, ""+ tableString);
    }

    public static Cursor getInformation(SQLiteDatabase db,int frag){
        Cursor cursor;
        String[] projection = {"_id","LEND","ITEMNAME","LBNAME","DESCRIPTION","DATETIME","FAVORITE"};
        if (frag == 1){
        cursor = db.query("TRANSACTIONS",projection,"LEND=?",new String[]{"0"},null,null,null);
        } else if (frag == 2){
            cursor = db.query("TRANSACTIONS",projection,"LEND=?",new String[]{"1"},null,null,null);
        }else if (frag == 3){
            cursor = db.query("TRANSACTIONS", projection, null, null, null,null ,null );
        }
        else {
            cursor = db.query("TRANSACTIONS",projection,"FAVORITE=?",new String[]{"1"},null,null,null);
        }
        return cursor;
    }

    public static Cursor getInformation(SQLiteDatabase db,String id){
        Cursor cursor;
        String[] projection = {"_id","LEND","ITEMNAME","LBNAME","DESCRIPTION","DATETIME","FAVORITE"};
        Log.d(TAG, "getInformation: id value" + id);
        cursor=db.query("TRANSACTIONS",projection,"_id=?",new String[]{id},null,null,null);
        return cursor;
    }

}