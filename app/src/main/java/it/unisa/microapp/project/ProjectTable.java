package it.unisa.microapp.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import it.unisa.microapp.store.DatabaseHelper;

/**
 * Created by martina on 12/06/2015.
 */
public class ProjectTable {

    SQLiteDatabase mDb;
    DatabaseHelper helper;
    Context context;

    // Database table
    public static final String TABLE_PROJECT = "project";
    public static final String _ID = "_id";
    public static final String NAME_PROJECT = "name";
    public static final String NAME_SENSOR = "sensor";
    public static final String ICON_PROJECT = "icon";
    public static final String DESC_PROJECT = "description";
    public static final String NAME_MICROAPP = "microapp";


    public ProjectTable(Context context) {
        this.context = context;
        helper = new DatabaseHelper(context);
    }

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table if not exists "
            + TABLE_PROJECT
            + "("
            + _ID + " integer primary key autoincrement, "
            + NAME_PROJECT + " text, "
            + NAME_SENSOR + " text, "
            + ICON_PROJECT + " text, "
            + DESC_PROJECT + " text, "
            + NAME_MICROAPP + " text "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {

        database.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECT);
        onCreate(database);
    }

    static class MetaData {  // i metadati della tabella, accessibili ovunque
        static final String TABLE_PROJECT = "project";
        static final String _ID = "_id";
        static final String NAME_PROJECT = "name";
        static final String NAME_SENSOR = "sensor";
        static final String ICON_PROJECT = "icon";
        static final String DESC_PROJECT = "description";
        static final String NAME_MICROAPP = "microapp";

    }

    public void insertUser(String name_project, String name_sensor, String icon_project, String desc_project, String microapp){ //metodo per inserire i dati
        ContentValues cv=new ContentValues();
        Log.d("TABELLA", name_project + " " + name_sensor);
        cv.put(MetaData.NAME_PROJECT, name_project);
        cv.put(MetaData.NAME_SENSOR, name_sensor);
        cv.put(MetaData.ICON_PROJECT, icon_project);
        cv.put(MetaData.DESC_PROJECT, desc_project);
        cv.put(MetaData.NAME_MICROAPP, microapp);
        mDb.insert(MetaData.TABLE_PROJECT, null, cv);
    }

    public Cursor fetchUser(){ //metodo per fare la query di tutti i dati
        return mDb.query(MetaData.TABLE_PROJECT, null,null,null,null,null,null);
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
