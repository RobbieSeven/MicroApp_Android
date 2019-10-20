package it.unisa.microapp.project;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by martina on 30/06/2015.
 */
public class SensorAccelerometer extends Service implements SensorEventListener {

    SensorManager sensorManager;
    Sensor sensor;
    public static final long NOTIFY_INTERVAL = 5 * 60 * 1000; // 10 seconds
//    String filename;
    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;
    boolean flag = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Bundle b = null;
        b = intent.getExtras();
//        if(b.containsKey("namefile")){
//            filename = b.getString("namefile");
//        };

//        Log.d("filename sensor", filename);
        sensorManager=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        Log.d("INFO52", sensor.toString());

        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        return START_STICKY;
    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    flag = true;
                    sensorManager.registerListener(SensorAccelerometer.this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

                }

            });
        }


    }

    @Override
    public void onCreate() {
        super.onCreate();

        sensorManager=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // cancel if already existed
        if(mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);

    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener((SensorEventListener) this, sensor);
        super.onDestroy();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (flag) {
            Log.d("INFO", "onSensorChanged" + event.values[0] + event.values[1] + event.values[2]);
//            Log.d("filename sensor", "" +filename);
            Date date = new Date();

            AccelerometerSensorTable table = new AccelerometerSensorTable(getApplicationContext());
            table.open();
            table.insertUser(String.valueOf(date.getTime()) , String.valueOf(event.values[0]), String.valueOf(event.values[1]), String.valueOf(event.values[2]));

            table.close();
            flag = false;
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//        Log.d("INFO", "onAccuracyChanged" + sensor.toString() + " " + accuracy);
    }
}
