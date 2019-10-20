package it.unisa.microapp.store;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import it.unisa.microapp.project.AccelerometerSensorTable;
import it.unisa.microapp.project.ProjectTable;
import it.unisa.microapp.project.PressureSensorTable;
import it.unisa.microapp.project.SensorTable;

/**
 * Created by martina on 10/06/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, "DBVoti", null, 1);
    }


    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Voti (Id_app TEXT PRIMARY KEY, Votato INT not null);");
        MHealth.onCreate(db);
        ProjectTable.onCreate(db);
        PressureSensorTable.onCreate(db);
        AccelerometerSensorTable.onCreate(db);
        SensorTable.onCreate(db);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        MHealth.onUpgrade(db, oldVersion, newVersion);
        ProjectTable.onUpgrade(db, oldVersion, newVersion);
        PressureSensorTable.onUpgrade(db, oldVersion, newVersion);
        AccelerometerSensorTable.onUpgrade(db, oldVersion, newVersion);
        SensorTable.onUpgrade(db, oldVersion, newVersion);
    }
}
