package it.unisa.microapp.store;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by martina on 10/06/2015.
 */
public class MHealth {
    SQLiteDatabase mDb;
    DatabaseHelper helper;
    Context context;

    // Database table
    public static final String TABLE_USER = "health";
    public static final String _ID = "_id";
    public static final String HEART_RATE = "pulse48";


    public MHealth(Context context) {
        this.context = context;
        helper = new DatabaseHelper(context);
    }

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table if not exists "
            + TABLE_USER
            + "("
            + _ID + " integer primary key autoincrement, "
            + HEART_RATE + " text "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {

        database.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(database);
    }

    static class MetaData {  // i metadati della tabella, accessibili ovunque
        static final String TABLE_USER = "health";
        static final String _ID = "_id";
        static final String HEART_RATE = "pulse48";
    }

    public void insertUser(String rate){ //metodo per inserire i dati
        ContentValues cv=new ContentValues();
        cv.put(MetaData.HEART_RATE, rate);
        mDb.insert(MetaData.TABLE_USER, null, cv);
    }

    public Cursor fetchUser(){ //metodo per fare la query di tutti i dati
        return mDb.query(MetaData.TABLE_USER, null,null,null,null,null,null);
    }

    public void open(){  //il database su cui agiamo e' leggibile/scrivibile
        mDb=helper.getWritableDatabase();
//        mDb=helper.getReadableDatabase();
    }

    public void close(){ //chiudiamo il database su cui agiamo
        mDb.close();
    }

    public SQLiteDatabase db(){
        return mDb;
    }



}
