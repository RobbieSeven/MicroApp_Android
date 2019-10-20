package it.unisa.microapp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import it.unisa.microapp.R;
import it.unisa.microapp.project.AccelerometerSensorObject;
import it.unisa.microapp.project.AccelerometerSensorTable;
import it.unisa.microapp.project.PressureSensorObject;
import it.unisa.microapp.project.PressureSensorTable;
import it.unisa.microapp.project.ProjectTable;
import it.unisa.microapp.project.SensorTable;
import it.unisa.microapp.project.ViewSensorFragment;
import it.unisa.microapp.utils.Constants;
import it.unisa.microapp.utils.Utils;

/**
 * Created by martina on 11/07/2015.
 */
public class ViewerActivity extends MAActivity implements View.OnClickListener {


    private XYPlot plot;
    private String sensor;
    private String filename;
    private Button h12,day1,day3,day5;
    String name_sensor="";
    long data;
    long mseconds;


    @Override
    protected void initialize(Bundle savedInstanceState) {

    }

    @Override
    protected void prepare() {
    }


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.xyplot_layout);
//
//        prepareView();
//    }

        @Override
    protected void prepareView(View v) {
            ViewSensorFragment fragment= new ViewSensorFragment(adUnitId, filename);
                    getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
//        Bundle b = null;
//        b = getIntent().getExtras();
//        if(b.containsKey("filename")){
//            filename = b.getString("filename");
//            Log.d("filename view", filename);
//        }
//        plot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
//        h12 = (Button) findViewById(R.id.twelve);
//        day1 = (Button) findViewById(R.id.oneday);
//        day3 = (Button) findViewById(R.id.threeday);
//        day5 = (Button) findViewById(R.id.fiveday);
//
//        Date now = new Date();
//
//        mseconds = now.getTime();
//        //default data a 12 ore
//        Log.d("time","Time: " +mseconds);
//        data = mseconds - (1000 * 60 * 60 * 12);
//        Log.d("time","h12: " + data);
//        h12.setOnClickListener(this);
//        day1.setOnClickListener(this);
//        day3.setOnClickListener(this);
//        day5.setOnClickListener(this);
//
//        ProjectTable table = new ProjectTable(this);
//        table.open();
//        String query = " SELECT * " + " FROM " + ProjectTable.TABLE_PROJECT+ ";";
//         //+ " WHERE " + ProjectTable.NAME_PROJECT  + "=" + "'" + filename + "';";
//
//        Cursor c;
//        c = table.db().rawQuery(query, null);
//        Log.d("info", "size db " + c.getCount());
//
//        c.moveToNext();
//        String sensorName  = c.getString(c.getColumnIndex(ProjectTable.NAME_SENSOR));
//        if(sensorName.contains("Sensor_a")){
//
//            SensorTable t = new SensorTable(this);
//            t.open();
//            String QUERY = "SELECT * FROM "+ SensorTable.TABLE_SENSOR
//                    + " WHERE " + SensorTable.NAME_PROJECT + "=" + "'" + "Sensor_a" + "'";
//
//            Cursor cName = t.db().rawQuery(QUERY, null);
////            cName.moveToFirst();
//            Log.d("sensorName: ", "count " +cName.getCount());
//
//
//            while(cName.moveToNext()){
//                name_sensor =  cName.getString(cName.getColumnIndex(SensorTable.NAME_SENSOR));
//            }
//
//            setPlotXY(data);
//
//        }
//        else {
//            AlertDialog.Builder alert = new AlertDialog.Builder(this);
//
//            alert.setTitle("Error");
//            alert.setMessage("The senson in the project does not match the sensor's view");
//
//
//
//            alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//
//                }
//            });
//
//
//            alert.show();
//        }
    }




        public void setPlotXY(long date2){
            Log.d("INFO" , "setPlotXY");
            if(name_sensor.equals(getResources().getString(R.string.pressure))){
                Log.d("INFO" , "sensor PRESSURE");
                // initialize our XYPlot reference:
                ArrayList<PressureSensorObject> rangedGl = PressureSensorTable.getValues(this, date2, mseconds , sensor);

                if(rangedGl.size()>0){
//                    Number[] series1Numbers = {1, 8, 5, 2, 7, 4};
                    Number[] series1Numbers = new Number[rangedGl.size()];
//                    Number[] series2Numbers = new Number[rangedGl.size()];

                    for(int i = 0; i<rangedGl.size(); i++){
                        series1Numbers[i] = Double.valueOf(rangedGl.get(i).getValue());
//                        series2Numbers[i] = Long.valueOf(rangedGl.get(i).getData());
//                        Log.d("INFO value", ""+series1Numbers[i] + series2Numbers[i]);
                    }



                    Log.d("INFO v", ""+series1Numbers.length + series1Numbers.toString());

                    // Turn the above arrays into XYSeries':
                    XYSeries series1 = new SimpleXYSeries(
                            Arrays.asList(series1Numbers),          // SimpleXYSeries takes a List so turn our array into a List
                            SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
                            "Value");                             // Set the display title of the series
                    Log.d("INFO v", ""+series1.toString() + series1.size());

                    // same as above
//            XYSeries series2 = new SimpleXYSeries(Arrays.asList(series2Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, null);

                    // Create a formatter to use for drawing a series using LineAndPointRenderer
                    // and configure it from xml:
                    LineAndPointFormatter series1Format = new LineAndPointFormatter(Color.RED, Color.GREEN, null, null);

                    // add a new series' to the xyplot:
                    boolean fla = plot.addSeries(series1, series1Format);

                    Log.d("flag" , "flag" + fla);

//                    Log.d("INFO v", "" + plot.getCalculatedMaxY());


                    // same as above:
//                    LineAndPointFormatter series2Format = new LineAndPointFormatter(Color.RED, Color.GREEN, null, null);

                    // reduce the number of range labels
                    plot.setTicksPerRangeLabel(3);
                    plot.getGraphWidget().setDomainLabelOrientation(-45);

//                    plot.setDomainStep(XYStepMode.SUBDIVIDE, series2Numbers.length);
//
//                    MyFormat mf = new MyFormat();
//                    mf.xAxis = series2Numbers;
//                    plot.setDomainValueFormat(mf);
//                    plot.redraw();
                }

                else {
                    Log.d("INFO" , "La size e' 0");
                }
            }
            else if(name_sensor.equals(getResources().getString(R.string.accelerometer))){
                Log.d("INFO" , "sensor ACCELEROMETER");
                // initialize our XYPlot reference:

                ArrayList<AccelerometerSensorObject> rangedGl = AccelerometerSensorTable.getValues(this, date2, mseconds, sensor);

                if(rangedGl.size()>0){

                    Number[] series1Numbers = new Number[rangedGl.size()];
                    Number[] series2Numbers = new Number[rangedGl.size()];
                    Number[] series3Numbers = new Number[rangedGl.size()];
//                    Number[] series4Numbers = new Number[rangedGl.size()];

                    for(int i = 0; i<rangedGl.size(); i++){
                        series1Numbers[i] = Double.valueOf(rangedGl.get(i).getValueX());
                        series2Numbers[i] = Double.valueOf(rangedGl.get(i).getValueY());
                        series3Numbers[i] = Double.valueOf(rangedGl.get(i).getValueZ());
//                        series4Numbers[i] = Long.valueOf(rangedGl.get(i).getData());
                    }



                    Log.d("INFO", ""+series1Numbers.length + series2Numbers.length);

                    // Turn the above arrays into XYSeries':
                    XYSeries series1 = new SimpleXYSeries(
                            Arrays.asList(series1Numbers),          // SimpleXYSeries takes a List so turn our array into a List
                            SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
                            "ValueX");                             // Set the display title of the series

                    // same as above
                    XYSeries series2 = new SimpleXYSeries(Arrays.asList(series2Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "ValueY");

                    // same as above
                    XYSeries series3 = new SimpleXYSeries(Arrays.asList(series3Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "ValueZ");

                    // Create a formatter to use for drawing a series using LineAndPointRenderer
                    // and configure it from xml:
                    LineAndPointFormatter series1Format = new LineAndPointFormatter(Color.RED, Color.GREEN, null, null);

                    // add a new series' to the xyplot:
                    plot.addSeries(series1, series1Format);

                    // same as above:
                    LineAndPointFormatter series2Format = new LineAndPointFormatter(Color.BLUE, Color.GREEN, null, null);

                    plot.addSeries(series2, series2Format);

                    // same as above:
                    LineAndPointFormatter series3Format = new LineAndPointFormatter(Color.GREEN, Color.GREEN, null, null);

                    plot.addSeries(series3, series3Format);
//plot.setRangeBoundaries(-03, 12, BoundaryMode.FIXED);

                    // reduce the number of range labels
                    plot.setTicksPerRangeLabel(3);
                    plot.getGraphWidget().setDomainLabelOrientation(-45);
//                    plot.redraw();
//                    plot.setDomainStep(XYStepMode.SUBDIVIDE, series4Numbers.length);

//                    MyFormat mf = new MyFormat();
//                    mf.xAxis = series4Numbers;
//                    plot.setDomainValueFormat(mf);

                }

                else {
                    Log.d("INFO" , "La size e' 0");
                }
            }
            }

    @Override
    protected void execute() {
    }

    @Override
    public void restart() {

    }

    @Override
    public void resume() {


    }
//
    @Override
    public void initInputs() {
        for(String s : mycomponent.getUserData("viewer")){
            adUnitId = s;

            Utils.verbose("viewer id:"+ adUnitId);
            break;
        }

    }

    @Override
    public void beforeNext() {
    }

    @Override
    protected int onVisible() {
        return R.layout.activity_sensor;
    }

    @Override
    protected View onVisibleView() {
        return null;
    }

    @Override
    public void onClick(View v) {
    Log.d("onClick", "onClick");
            switch(v.getId())
            {
                case R.id.twelve:
                {
                    data = mseconds - (1000 * 60 * 60 * 12);
                    break;
                }
                case R.id.oneday:
                {
                    data = mseconds - (1000 * 60 * 60 * 24);
                    break;
                }
                case R.id.threeday:
                {
                    data = mseconds - (1000 * 60 * 60 * 24 * 3);
                    break;
                }
                case R.id.fiveday:
                {
                    data = mseconds - (1000 * 60 * 60 * 24 * 5);
                    break;
                }

            }
        setPlotXY(data);
        plot.redraw();
    }


    class MyFormat extends Format {

        /**
         *
         */
        private static final long serialVersionUID = 1L;
        private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM hh:mm");
        public Number[] xAxis = null;

        @Override
        public Object parseObject(String string, ParsePosition position) {
            // TODO Auto-generated method stub
            return null;
        }

        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {

            // because our timestamps are in seconds and SimpleDateFormat expects milliseconds
            // we multiply our timestamp by 1000:
            long fl = ((Number) obj).longValue();
            int index = Math.round(fl);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(xAxis[index].longValue());
            Date date = new Date(xAxis[index].longValue());
            return dateFormat.format(date, toAppendTo, pos);
        }

    }

}
