package it.unisa.microapp.project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.unisa.microapp.MicroAppGenerator;
import it.unisa.microapp.R;
import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.exceptions.InvalidComponentException;
import it.unisa.microapp.utils.Constants;
import it.unisa.microapp.utils.Utils;

/**
 * Created by martina on 18/06/2015.
 */
public class SensorAppActivity extends Activity {

    String filename = "";
    ProjectTable table;
    String[] sensor;
    String[] microapp;
//    GridView gridview;

    Button stopService;
    ListView listSensor;
    ListView listMicroapp;

    @Override
    protected void onCreate(Bundle state) {

        super.onCreate(state);


        setContentView(R.layout.sensorapp);

        Bundle b = null;
        b = getIntent().getExtras();
        if(b.containsKey("namefile")){
            filename = b.getString("namefile");
        }
        table = new ProjectTable(getApplicationContext());
        table.open();

        Log.d("filename", filename);

        ProjectTable table = new ProjectTable(getApplicationContext());
        table.open();
        String query = " SELECT * " + " FROM " + ProjectTable.TABLE_PROJECT + " WHERE " + ProjectTable.NAME_PROJECT  + "=" + "'" + filename + "';";

        Cursor c;
        c = table.db().rawQuery(query, null);
        c.moveToNext();
        sensor  = c.getString(c.getColumnIndex(ProjectTable.NAME_SENSOR)).split(",");
        microapp  = c.getString(c.getColumnIndex(ProjectTable.NAME_MICROAPP)).split(",");

        final Intent serviceAccelerometer = new Intent(this, SensorAccelerometer.class);
        final Intent servicePressure = new Intent(this, SensorPressure.class);

        listSensor = (ListView) findViewById(R.id.list_sensor);
        listMicroapp = (ListView) findViewById(R.id.list_micropp);



        List<String> wordSensor = Arrays.asList(sensor);
        Log.d("wordsensor" , "sensor " +String.valueOf(wordSensor.size()) + wordSensor.toString());

        List<String> wordMicroapp = Arrays.asList(microapp);
        Log.d("wordsensor", "sensor " + String.valueOf(wordMicroapp.size()) + wordMicroapp.toString());


        listMicroapp.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, wordMicroapp));



        listSensor.setAdapter(new SensorAdapter(this, wordSensor));


        listMicroapp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s_i = (String) parent.getItemAtPosition(position);

            }
        });

        listMicroapp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String path = (String) parent.getItemAtPosition(position);
                MicroAppGenerator application = (MicroAppGenerator) getApplication();

                try {
                    application.setDeployPath(path, false);

                    try {
                        application.initComponents();
                    } catch (InvalidComponentException e) {
                        Utils.errorDialog(SensorAppActivity.this, getString(R.string.notRunnable), e.getMessage());
                    }

                    // beforeNext();

                    MAComponent mycomponent = application.getCurrentState();
                    // initInputs();

                    if (!mycomponent.isOutFilled()) {
//                        Toast.makeText(this, "Some outputs are missing " + mycomponent.getType() + " id:" + mycomponent.getId(),
//                                Toast.LENGTH_SHORT).show();

                    }
                    MAComponent comp = application.nextStep();

                    if (comp == null) {
                        setResult(Constants.ID_TERMINATED);
                        // finish();
                    }
                    try {
                        Intent runi = new Intent(SensorAppActivity.this, comp.getActivityClass());

                        Bundle b = new Bundle();
                        b.putString("description", comp.getDescription());
                        b.putString("state", comp.getNowState());
                        b.putString("project", filename);
                        runi.putExtras(b);

                        startActivityForResult(runi, 1);
                    } catch (ClassNotFoundException e) {
                        Utils.errorDialog(SensorAppActivity.this, e.getMessage());
                    }

                } catch (Exception e) {
            }

        }});

        String sensorName="";
        SensorTable t = new SensorTable(SensorAppActivity.this);
        t.open();
        for (int i=0;i<wordSensor.size();i++){
            String QUERY = "SELECT * FROM "+ SensorTable.TABLE_SENSOR
                    + " WHERE " + SensorTable.NAME_PROJECT + "=" + "'" + wordSensor.get(i) + "'";

            Cursor cName = t.db().rawQuery(QUERY, null);
//            cName.moveToFirst();
            Log.d("sensorName: ", "count " +cName.getCount());

            String name_sensor="";

            while(cName.moveToNext()){
                name_sensor =  cName.getString(cName.getColumnIndex(SensorTable.NAME_SENSOR));
            }
            sensorName  = sensorName+ " " + name_sensor;
            Log.d("sensorName: ", "sensorName: " +sensorName);
//            cName.close();
        }
//        t.close();

           if(sensorName.contains(getResources().getString(R.string.accelerometer))){
                Log.d("START", "accelerometer");
//                serviceAccelerometer.putExtra("namefile", filename);
                startService(serviceAccelerometer);
            }
            if(sensorName.contains(getResources().getString(R.string.pressure))){
                Log.d("START", "pressure");
//                servicePressure.putExtra("namefile",filename);
                startService(servicePressure);
            }




//        gridview = (GridView) findViewById(R.id.gridviewAppSensor);
//        gridview.setAdapter(adapter);
//        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String sensor = (String) adapter.getItem(position);
//                Intent intent = new Intent(SensorAppActivity.this, ViewSensorProject.class);
//                intent.putExtra("sensor", sensor);
//                intent.putExtra("filename", filename);
//                startActivity(intent);
//            }
//        });

//        stopService = (Button) findViewById(R.id.stop_service);
//        stopService.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                stopService(serviceAccelerometer);
//                stopService(servicePressure);
//            }
//        });


        listSensor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String path = (String) parent.getItemAtPosition(position);
                SensorTable t = new SensorTable(SensorAppActivity.this);
                t.open();

                String QUERY = "SELECT * FROM "+ SensorTable.TABLE_SENSOR
                    + " WHERE " + SensorTable.NAME_PROJECT + "=" + "'" + path + "'";

                Cursor c = t.db().rawQuery(QUERY, null);
                Log.d("info", "size db " + c.getCount());
//                c.moveToFirst();
                String name_sensor="";

                while(c.moveToNext()){
                    name_sensor =  c.getString(c.getColumnIndex(SensorTable.NAME_SENSOR));
                }
                if(name_sensor.equals(getResources().getString(R.string.accelerometer))){
                    stopService(serviceAccelerometer);
                    Log.d("STOP", "acc");
                }
                else if(name_sensor.equals(getResources().getString(R.string.pressure))){
                    stopService(servicePressure);
                    Log.d("STOP", "press");
                }
                else
                    Log.d("STOP" , "nessun stop service");
            }});
    }


    private static class SensorAdapter extends BaseAdapter {
        private Context context;
        List<String> sensor;
        private LayoutInflater li;

        public SensorAdapter(SensorAppActivity sensorAppActivity, List<String> sensor) {
            this.context = sensorAppActivity;
            this.sensor = sensor;
            Log.d("INFO" , "sensori: " + sensor);
        }


        @Override
        public int getCount() {
            Log.d("sensori size: " , "size: " +sensor.size());
            return sensor.size();
        }

        @Override
        public Object getItem(int position) {
            return sensor.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

//        static class ViewHolder {
//            public ImageView imageView;
//            public TextView textView;
//
//        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            this.li = ((Activity) context).getLayoutInflater();
//            ViewHolder holder;
//            if (convertView == null) {
//                holder = new ViewHolder();
//                // if it's not recycled, initialize some attributes
//                convertView = li.inflate(R.layout.sensor_gridview_item, parent, false);
//                holder.imageView = (ImageView) convertView.findViewById(R.id.icon_image);
//                holder.textView = (TextView) convertView.findViewById(R.id.icon_text);
//                holder.imageView.setPadding(8, 8, 8, 8);
//                convertView.setTag(holder);
//            } else {
//                holder = (ViewHolder) convertView.getTag();
//            }
//
//            int drawable = R.drawable.ic_launcher;
//
//            if(sensor.get(position).equals(context.getString(R.string.accelerometer)))
//                drawable = R.drawable.accelerometer48;
//            if(sensor.get(position).equals(context.getString(R.string.pressure)))
//                drawable = R.drawable.barometer48;
//
//            holder.imageView.setImageResource(drawable);
//            holder.textView.setText(sensor.get(position));

            convertView = li.inflate(android.R.layout.simple_list_item_1, parent, false);
            TextView t = (TextView) convertView.findViewById(android.R.id.text1);
            t.setText(context.getResources().getString(R.string.stop_serv) + " " + sensor.get(position));
            return convertView;
        }

    }
}
