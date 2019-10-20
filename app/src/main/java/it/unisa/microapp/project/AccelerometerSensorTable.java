package it.unisa.microapp.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import it.unisa.microapp.store.DatabaseHelper;

/**
 * Created by martina on 30/06/2015.
 */
public class AccelerometerSensorTable {

    SQLiteDatabase mDb;
    static DatabaseHelper helper;
    Context context;

    // Database table
    public static final String TABLE_SENSOR = "accelerometer_sensor";
    public static final String _ID = "_id";
//    public static final String NAME_PROJECT = "name_project";
    public static final String DATA = "data";
    public static final String VALUE_X = "value_x";
    public static final String VALUE_Y = "value_y";
    public static final String VALUE_Z = "value_z";


    public AccelerometerSensorTable(Context context) {
        this.context = context;
        helper = new DatabaseHelper(context);
    }

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table if not exists "
            + TABLE_SENSOR
            + "("
            + _ID + " integer primary key autoincrement, "
//            + NAME_PROJECT + " text, "
            + DATA + " text, "
            + VALUE_X + " text, "
            + VALUE_Y + " text, "
            + VALUE_Z + " text "
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
        static final String TABLE_SENSOR = "accelerometer_sensor";
        static final String _ID = "_id";
//        static final String NAME_PROJECT = "name_project";
        static final String DATA = "data";
        static final String VALUE_X = "value_x";
        static final String VALUE_Y = "value_y";
        static final String VALUE_Z = "value_z";
    }

    public void insertUser(String data, String valueX, String valueY, String valueZ){ //metodo per inserire i dati
        ContentValues cv=new ContentValues();
//        cv.put(MetaData.NAME_PROJECT, name_project);
        cv.put(MetaData.DATA, data);
        cv.put(MetaData.VALUE_X, valueX);
        cv.put(MetaData.VALUE_Y, valueY);
        cv.put(MetaData.VALUE_Z, valueZ);
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

    public static ArrayList<AccelerometerSensorObject> getValues(Context context, long from, long to, String sensor){
        SQLiteDatabase db = helper.getReadableDatabase();

        String QUERY = "SELECT * FROM "+ AccelerometerSensorTable.TABLE_SENSOR ;
//        + " WHERE " + AccelerometerSensorTable.NAME_PROJECT + "=" + "'" + filename + "'";
        Cursor c = db.rawQuery(QUERY, null);
        Log.d("info", "size db " + c.getCount());
        c.moveToFirst();
        ArrayList<AccelerometerSensorObject> toReturn = new ArrayList<AccelerometerSensorObject>();

        while(c.isAfterLast()==false){

            String valueX, valueY, valueZ, date;

            valueX = c.getString(c.getColumnIndex(AccelerometerSensorTable.VALUE_X));
            valueY = c.getString(c.getColumnIndex(AccelerometerSensorTable.VALUE_Y));
            valueZ = c.getString(c.getColumnIndex(AccelerometerSensorTable.VALUE_Z));
            date = c.getString(c.getColumnIndex(AccelerometerSensorTable.DATA));

            AccelerometerSensorObject toAdd = new AccelerometerSensorObject(valueX, valueY, valueZ, date);


            long dataConvert = Long.valueOf(date).longValue();
            Date d = new Date(dataConvert);
            Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MMM/dd hh:mm:ss z");
            sdf.setCalendar(cal);
            cal.setTime(d);

            Date date_from = new Date(from);
            sdf.setCalendar(cal);
            cal.setTime(date_from);

            Date date_to = new Date(to);
            sdf.setCalendar(cal);
            cal.setTime(date_to);


            Log.d("Info" , "data0 "  + d + " dataform " + date_from + " datato " + date_to);


            if(d.after(date_from) && d.before(date_to)  || d.getDay()==date_from.getDay() || d.getDay()==date_to.getDay()){

                toReturn.add(toAdd);
            }

            c.moveToNext();
        }

        Log.d("INFO", "size "+toReturn.size());

        return toReturn;

    }
}
