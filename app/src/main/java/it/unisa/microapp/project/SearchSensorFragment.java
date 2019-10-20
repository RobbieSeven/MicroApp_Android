package it.unisa.microapp.project;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

import it.unisa.microapp.R;

/**
 * Created by martina on 25/06/2015.
 */

public class SearchSensorFragment extends Fragment {

    String sensor;
    String filename;

    public SearchSensorFragment(String sensor, String filename) {
        this.sensor = sensor;
        this.filename = filename;
    }


    private ProjectTable db;
    private DateHandler dateHandler;
    private EditText fromData,toData;




    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {


        return inflater.inflate(R.layout.search_sensor_layout, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);


        db = new ProjectTable(getActivity());
        dateHandler = new DateHandler();

        fromData = (EditText) getView().findViewById(R.id.fromDateGli);
        fromData.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                changeFromDate();

            }
        });

        toData = (EditText) getView().findViewById(R.id.ToDateGli);
        toData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToDate();

            }
        });

        PressureSensorTable table = new PressureSensorTable(getActivity());
        table.open();

        AccelerometerSensorTable table2 = new AccelerometerSensorTable(getActivity());
        table2.open();

//        Date date1 = new Date(2011-1900, 3, 7);
//
//        Date date2 = new Date(2015-1900, 3, 7);

//        table.insertUser("boh","Temperature",String.valueOf(date1.getTime()), "85");
//
//        table.insertUser("boh","Temperature",String.valueOf(date2.getTime()), "45");

        Button search = (Button) getView().findViewById(R.id.searchOk);
        search.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!fromData.getText().toString().equals("")&& !toData.getText().toString().equals("")){
                    long from,upTo;
                    from=DateHandler.getDateInMillis(fromData.getText().toString());
                    Calendar c = GregorianCalendar.getInstance();
                    c.setTimeInMillis(from);
                    c.set(Calendar.HOUR_OF_DAY, 24);
                    from = c.getTimeInMillis();
                    upTo=DateHandler.getDateInMillis(toData.getText().toString());
                    c.setTimeInMillis(upTo);
                    c.set(Calendar.HOUR_OF_DAY, 24);
                    upTo = c.getTimeInMillis();
//                    ViewSensorFragment fragment= new ViewSensorFragment(from, upTo, sensor, filename);
//                    getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
                }
                else{
                    Toast.makeText(getActivity(), "Select date", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }


    private void changeFromDate()
    {
        int currentDay = dateHandler.getDay();
        int currentMonth = dateHandler.getMonth();
        int currentYear = dateHandler.getYear();

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int newYear, int newMonth, int newDay) {
                dateHandler.setDay(newDay);
                dateHandler.setMonth(newMonth);
                dateHandler.setYear(newYear);

                fromData.setText((dateHandler.getDate()));
            }
        }, currentYear, currentMonth, currentDay);

        dialog.setTitle("Change Data");

        dialog.show();
    }

    private void changeToDate()
    {
        int currentDay = dateHandler.getDay();
        int currentMonth = dateHandler.getMonth();
        int currentYear = dateHandler.getYear();

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int newYear, int newMonth, int newDay) {
                dateHandler.setDay(newDay);
                dateHandler.setMonth(newMonth);
                dateHandler.setYear(newYear);

                toData.setText((dateHandler.getDate()));
            }
        }, currentYear, currentMonth, currentDay);

        dialog.setTitle("Change Data");

        dialog.show();
    }



}
