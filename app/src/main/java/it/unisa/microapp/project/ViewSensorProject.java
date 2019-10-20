package it.unisa.microapp.project;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import it.unisa.microapp.R;

/**
 * Created by martina on 25/06/2015.
 */
public class ViewSensorProject extends Activity {

    String sensor;
    String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        Bundle b = getIntent().getExtras();
        if(b!=null){
            sensor = b.getString("sensor");
            filename = b.getString("filename");
        }

        FragmentManager fM = getFragmentManager();
        Fragment f = fM.findFragmentById(R.id.container);
        fM.beginTransaction().replace(R.id.container, new SearchSensorFragment(sensor, filename)).commit();

    }
}
