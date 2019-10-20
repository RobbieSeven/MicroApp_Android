package it.unisa.microapp.project;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

import it.unisa.microapp.R;

/**
 * Created by martina on 26/06/2015.
 */
public class ViewSensorFragment extends Fragment implements View.OnClickListener {

    private XYPlot plot;
    private String sensor;
    private String filename;
    private Button h12,day1,day3,day5;
    String name_sensor="";
    long data;
    long mseconds;


    public ViewSensorFragment(String sensor, String filename) {
        this.sensor = sensor;
        this.filename = filename;
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.xyplot_layout, container, false);

        plot = (XYPlot) rootview.findViewById(R.id.mySimpleXYPlot);
        h12 = (Button) rootview.findViewById(R.id.twelve);
        day1 = (Button) rootview.findViewById(R.id.oneday);
        day3 = (Button) rootview.findViewById(R.id.threeday);
        day5 = (Button) rootview.findViewById(R.id.fiveday);

        Date now = new Date();

        mseconds = now.getTime();
        //default data a 12 ore
        Log.d("time","Time: " +mseconds);
        data = mseconds - (1000 * 60 * 60 * 12);
        Log.d("time","h12: " + data);
        h12.setOnClickListener(this);
        day1.setOnClickListener(this);
        day3.setOnClickListener(this);
        day5.setOnClickListener(this);

        ProjectTable table = new ProjectTable(getActivity());
        table.open();
        String query = " SELECT * " + " FROM " + ProjectTable.TABLE_PROJECT+ ";";
        //+ " WHERE " + ProjectTable.NAME_PROJECT  + "=" + "'" + filename + "';";

        Cursor c;
        c = table.db().rawQuery(query, null);
        Log.d("info", "size db " + c.getCount());

        c.moveToNext();
        String sensorName  = c.getString(c.getColumnIndex(ProjectTable.NAME_SENSOR));
        if(sensorName.contains(sensor)){

            SensorTable t = new SensorTable(getActivity());
            t.open();
            String QUERY = "SELECT * FROM "+ SensorTable.TABLE_SENSOR
                    + " WHERE " + SensorTable.NAME_PROJECT + "=" + "'" + sensor + "'";

            Cursor cName = t.db().rawQuery(QUERY, null);
//            cName.moveToFirst();
            Log.d("sensorName: ", "count " +cName.getCount());


            while(cName.moveToNext()){
                name_sensor =  cName.getString(cName.getColumnIndex(SensorTable.NAME_SENSOR));
            }

            setPlotXY(data);

        }
        else {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

            alert.setTitle("Error");
            alert.setMessage("The sensor in the project does not match the sensor's view");



            alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                }
            });


            alert.show();
        }

        return rootview;
    }

    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);


    }


    public void setPlotXY(long date2){
        plot.clear();
        Log.d("INFO" , "setPlotXY");
        if(name_sensor.equals(getResources().getString(R.string.pressure))){
            Log.d("INFO" , "sensor PRESSURE");
            // initialize our XYPlot reference:
            ArrayList<PressureSensorObject> rangedGl = PressureSensorTable.getValues(getActivity(), date2, mseconds , sensor);

            if(rangedGl.size()>0){
//                    Number[] series1Numbers = {1, 8, 5, 2, 7, 4};
                Number[] series1Numbers = new Number[rangedGl.size()];
                    Number[] series2Numbers = new Number[rangedGl.size()];

                for(int i = 0; i<rangedGl.size(); i++){
                    series1Numbers[i] = Double.valueOf(rangedGl.get(i).getValue());
                        series2Numbers[i] = Long.valueOf(rangedGl.get(i).getData());
//                        Log.d("INFO value", ""+series1Numbers[i] + series2Numbers[i]);
                }



                Log.d("INFO v", ""+series1Numbers.length + series2Numbers.length);

                // Turn the above arrays into XYSeries':
                XYSeries series1 = new SimpleXYSeries(
                        Arrays.asList(series1Numbers),          // SimpleXYSeries takes a List so turn our array into a List
                        SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
                        null);                             // Set the display title of the series
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

                    plot.setDomainStep(XYStepMode.SUBDIVIDE, series2Numbers.length);
//
                    MyFormat mf = new MyFormat();
                    mf.xAxis = series2Numbers;
                    plot.setDomainValueFormat(mf);

                    plot.redraw();
            }

            else {
                Log.d("INFO" , "La size e' 0");
            }
        }
        else if(name_sensor.equals(getResources().getString(R.string.accelerometer))){
            Log.d("INFO" , "sensor ACCELEROMETER");
            // initialize our XYPlot reference:

            ArrayList<AccelerometerSensorObject> rangedGl = AccelerometerSensorTable.getValues(getActivity(), date2, mseconds, sensor);

            if(rangedGl.size()>0){

                Number[] series1Numbers = new Number[rangedGl.size()];
                Number[] series2Numbers = new Number[rangedGl.size()];
                Number[] series3Numbers = new Number[rangedGl.size()];
                    Number[] series4Numbers = new Number[rangedGl.size()];

                for(int i = 0; i<rangedGl.size(); i++){
                    series1Numbers[i] = Double.valueOf(rangedGl.get(i).getValueX());
                    series2Numbers[i] = Double.valueOf(rangedGl.get(i).getValueY());
                    series3Numbers[i] = Double.valueOf(rangedGl.get(i).getValueZ());
                        series4Numbers[i] = Long.valueOf(rangedGl.get(i).getData());
                }



                Log.d("INFO", ""+series1Numbers.length + series2Numbers.length);

                // Turn the above arrays into XYSeries':
                XYSeries series1 = new SimpleXYSeries(
                        Arrays.asList(series1Numbers),          // SimpleXYSeries takes a List so turn our array into a List
                        SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
                        null);                             // Set the display title of the series

                // same as above
                XYSeries series2 = new SimpleXYSeries(Arrays.asList(series2Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, null);

                // same as above
                XYSeries series3 = new SimpleXYSeries(Arrays.asList(series3Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, null);

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

                    MyFormat mf = new MyFormat();
                    mf.xAxis = series4Numbers;
                    plot.setDomainValueFormat(mf);

                    plot.redraw();
            }

            else {
                Log.d("INFO" , "La size e' 0");
            }
        }
    }

    public class MyFormat extends Format {

        /**
         *
         */
        private static final long serialVersionUID = 1L;
        private SimpleDateFormat dateFormat = new SimpleDateFormat("");
        public Number[] xAxis = null;

        @Override
        public Object parseObject(String string, ParsePosition position) {
            // TODO Auto-generated method stub
            return null;
        }

        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {

            // because our timestamps are in seconds and SimpleDateFormat expects milliseconds
            // we multiply our timestamp by 1000:
            long fl = xAxis.length-1;
            Log.d("TIME" , "t: " + fl);
            int index = Math.round(fl);
            Log.d("TIME" , "i: " + index);
//            Calendar c = Calendar.getInstance();
//            Log.d("TIME" , "x: " + xAxis[index].longValue());
//            c.setTimeInMillis(xAxis[index].longValue());
            Date date = new Date(xAxis[index].longValue());

            return dateFormat.format(date, toAppendTo, pos);

        }

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

}
