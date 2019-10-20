package it.unisa.microapp.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import it.unisa.microapp.store.DatabaseHelper;

/**
 * Created by martina on 11/07/2015.
 */
public class SensorTable {

    SQLiteDatabase mDb;
    DatabaseHelper helper;
    Context context;

    // Database table
    public static final String TABLE_SENSOR = "sensor";
    public static final String _ID = "_id";
    public static final String NAME_PROJECT = "name";
    public static final String NAME_SENSOR = "sensor";
    public static final String ICON_PROJECT = "icon";
    public static final String DESC_PROJECT = "description";



    public SensorTable(Context context) {
        this.context = context;
        helper = new DatabaseHelper(context);
    }

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table if not exists "
            + TABLE_SENSOR
            + "("
            + _ID + " integer primary key autoincrement, "
            + NAME_PROJECT + " text, "
            + NAME_SENSOR + " text, "
            + ICON_PROJECT + " text, "
            + DESC_PROJECT+ " text "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {

        database.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSOR);
        onCreate(database);
    }

    static class MetaData {  // i metadati della tabella, accessibili ovunque
        static final String TABLE_SENSOR = "sensor";
        static final String _ID = "_id";
        static final String NAME_PROJECT = "name";
        static final String NAME_SENSOR = "sensor";
        static final String ICON_PROJECT = "icon";
        static final String DESC_PROJECT = "description";
    }

    public void insertUser(String name_project, String name_sensor, String icon_project, String desc_project){ //metodo per inserire i dati
        ContentValues cv=new ContentValues();
        Log.d("TABELLA", name_project + " " + name_sensor);
        cv.put(MetaData.NAME_PROJECT, name_project);
        cv.put(MetaData.NAME_SENSOR, name_sensor);
        cv.put(MetaData.ICON_PROJECT, icon_project);
        cv.put(MetaData.DESC_PROJECT, desc_project);
        mDb.insert(MetaData.TABLE_SENSOR, null, cv);
    }

    public Cursor fetchUser(){ //metodo per fare la query di tutti i dati
        return mDb.query(MetaData.TABLE_SENSOR, null,null,null,null,null,null);
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
