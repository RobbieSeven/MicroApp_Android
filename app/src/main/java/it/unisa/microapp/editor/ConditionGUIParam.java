package it.unisa.microapp.editor;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import it.unisa.microapp.R;
import it.unisa.microapp.activities.ConditionActivity;
import it.unisa.microapp.data.Condition;
import it.unisa.microapp.utils.Constants;
import it.unisa.microapp.utils.Utils;

/**
 * Created by Vincenzo on 05/12/2015.
 */
public class ConditionGUIParam extends FragmentActivity{
    Pin pin;
    EditorActivity act;
    AlertDialog.Builder alert;
    EditorGrid grid;
    View dialoglayout;
    Spinner spinnerTrue;
    Spinner spinnerFalse;

    public ConditionGUIParam(Pin pin, EditorActivity act, AlertDialog.Builder alert,EditorGrid grid,View dialoglayout){
        this.pin=pin;
        this.act=act;
        this.alert=alert;
        this.grid=grid;
        this.dialoglayout=dialoglayout;

    }

    public void selectTrueFalse(){
        spinnerTrue= (Spinner)dialoglayout.findViewById(R.id.spinnerTrue);
        ArrayAdapter<String> adapterTrue = new ArrayAdapter<String>(act,android.R.layout.simple_spinner_item,new String[]{"pin 1","pin 2"}
        );
        spinnerTrue.setAdapter(adapterTrue);


        spinnerFalse = (Spinner)dialoglayout.findViewById(R.id.spinnerFalse);
        ArrayAdapter<String> adapterFalse = new ArrayAdapter<String>(act,android.R.layout.simple_spinner_item,new String[]{"pin 1","pin 2"}
        );
        spinnerFalse.setAdapter(adapterFalse);
    }

    public void createContactGUI(){
        final TextView tex=(TextView)dialoglayout.findViewById(R.id.textViewcond2);
        tex.setVisibility(View.GONE);
        final EditText inputbis=(EditText)dialoglayout.findViewById(R.id.editTextbis);
        inputbis.setVisibility(View.GONE);
        final TextView t=(TextView)dialoglayout.findViewById(R.id.textView);
        t.setText("Phone number");
        final TextView t2=(TextView)dialoglayout.findViewById(R.id.textViewcond1);
        t2.setText("");
        final Spinner spinner = (Spinner)dialoglayout.findViewById(R.id.spinner3);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(act,android.R.layout.simple_spinner_item,new String[]{"equal to","contains","starts with"}
        );
        spinner.setAdapter(adapter);
        final EditText input=(EditText)dialoglayout.findViewById(R.id.editText);
        Object obj = pin.getObject();
        if (obj instanceof String) {
            String lobj = (String) obj;
            String[] array=lobj.split(".;.");
            input.setText(array[0]);
        }
        Spinner spinnerT= (Spinner)dialoglayout.findViewById(R.id.spinnerTrue);
        spinnerT.setVisibility(View.GONE);
        Spinner spinnerF = (Spinner)dialoglayout.findViewById(R.id.spinnerFalse);
        spinnerF.setVisibility(View.GONE);
        TextView TTrue= (TextView)dialoglayout.findViewById(R.id.textViewtrue);
        TTrue.setVisibility(View.GONE);
        TextView TFalse= (TextView)dialoglayout.findViewById(R.id.textViewfalse);
        TFalse.setVisibility(View.GONE);
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String item = spinner.getSelectedItem().toString();
                String value = input.getText().toString();
                pin.setObject(value + ".;." + item);
                grid.setModified(true);
            }
        });

    }

    public void createDynamicContactGUI(){
        final EditText inputbis=(EditText)dialoglayout.findViewById(R.id.editTextbis);
        inputbis.setVisibility(View.GONE);
        TextView t=(TextView)dialoglayout.findViewById(R.id.textView);
        t.setText("First phone number");
        TextView t2=(TextView)dialoglayout.findViewById(R.id.textViewcond1);
        t2.setVisibility(View.GONE);
        final Spinner spinner = (Spinner)dialoglayout.findViewById(R.id.spinner3);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(act,android.R.layout.simple_spinner_item,new String[]{"equal to","not equal to","starts with"}
        );
        spinner.setAdapter(adapter);
        final TextView tex=(TextView)dialoglayout.findViewById(R.id.textViewcond2);
        tex.setText("Second phone number");
        final EditText input=(EditText)dialoglayout.findViewById(R.id.editText);
        input.setVisibility(View.GONE);

        this.selectTrueFalse();

        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String item = spinner.getSelectedItem().toString();
                String itemTrue= spinnerTrue.getSelectedItem().toString();
                String itemFalse= spinnerFalse.getSelectedItem().toString();
                grid.getSelected().truePin=itemTrue;
                grid.getSelected().falsePin=itemFalse;
                grid.getSelected().linee=true;
                pin.setObject(item+".;."+itemTrue+".;."+itemFalse);
                grid.setModified(true);

            }
        });

    }

    public void createStringGUI(){
        final TextView tex=(TextView)dialoglayout.findViewById(R.id.textViewcond2);
        tex.setVisibility(View.GONE);
        final EditText inputbis=(EditText)dialoglayout.findViewById(R.id.editTextbis);
        inputbis.setVisibility(View.GONE);
        TextView t=(TextView)dialoglayout.findViewById(R.id.textView);
        t.setText("String");
        TextView t2=(TextView)dialoglayout.findViewById(R.id.textViewcond1);
        t2.setText("");
        final Spinner spinner = (Spinner)dialoglayout.findViewById(R.id.spinner3);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(act,android.R.layout.simple_spinner_item,new String[]{"equal to","contains","starts with"}
        );
        spinner.setAdapter(adapter);
        final EditText input=(EditText)dialoglayout.findViewById(R.id.editText);
        Object obj = pin.getObject();
        if (obj instanceof String) {
            String lobj = (String) obj;
            String[] array=lobj.split(".;.");
            input.setText(array[0]);
        }

        Spinner spinnerT= (Spinner)dialoglayout.findViewById(R.id.spinnerTrue);
        spinnerT.setVisibility(View.GONE);
        Spinner spinnerF = (Spinner)dialoglayout.findViewById(R.id.spinnerFalse);
        spinnerF.setVisibility(View.GONE);
        TextView TTrue= (TextView)dialoglayout.findViewById(R.id.textViewtrue);
        TTrue.setVisibility(View.GONE);
        TextView TFalse= (TextView)dialoglayout.findViewById(R.id.textViewfalse);
        TFalse.setVisibility(View.GONE);

        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String item = spinner.getSelectedItem().toString();
                String value = input.getText().toString();
                pin.setObject(value+".;."+item);
                grid.setModified(true);
            }
        });

    }

    public void createDynamicStringGUI(){
        final EditText inputbis=(EditText)dialoglayout.findViewById(R.id.editTextbis);
        inputbis.setVisibility(View.GONE);
        TextView t=(TextView)dialoglayout.findViewById(R.id.textView);
        t.setText("First String");
        TextView t2=(TextView)dialoglayout.findViewById(R.id.textViewcond1);
        t2.setVisibility(View.GONE);
        final Spinner spinner = (Spinner)dialoglayout.findViewById(R.id.spinner3);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(act,android.R.layout.simple_spinner_item,new String[]{"equal to","not equal to","starts with","contains"}
        );
        spinner.setAdapter(adapter);
        final TextView tex=(TextView)dialoglayout.findViewById(R.id.textViewcond2);
        tex.setText("Second String");
        final EditText input=(EditText)dialoglayout.findViewById(R.id.editText);
        input.setVisibility(View.GONE);

        this.selectTrueFalse();

        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String item = spinner.getSelectedItem().toString();
                String itemTrue= spinnerTrue.getSelectedItem().toString();
                String itemFalse= spinnerFalse.getSelectedItem().toString();
                grid.getSelected().truePin=itemTrue;
                grid.getSelected().falsePin=itemFalse;
                grid.getSelected().linee=true;
                pin.setObject(item+".;."+itemTrue+".;."+itemFalse);
                grid.setModified(true);
            }
        });

    }

    public void createImageGUI(){
        final EditText input1=(EditText)dialoglayout.findViewById(R.id.editText);
        input1.setEnabled(false);
        Object obj = pin.getObject();
        if (obj instanceof String) {
            String lobj = (String) obj;
            String[] array=lobj.split(".;.");
            input1.setText(array[0]);
        }

        final EditText inputbis=(EditText)dialoglayout.findViewById(R.id.editTextbis);
        inputbis.setEnabled(false);
        if (obj instanceof String) {
            String lobj = (String) obj;
            String[] array=lobj.split(".;.");
            inputbis.setText(array[1]);
        }

        TextView t1=(TextView)dialoglayout.findViewById(R.id.textViewcond1);
        t1.setText("Width");
        TextView t2=(TextView)dialoglayout.findViewById(R.id.textViewcond2);
        t2.setText("Height");
        TextView t=(TextView)dialoglayout.findViewById(R.id.textView);
        t.setText("Image");
        final Spinner spinner = (Spinner)dialoglayout.findViewById(R.id.spinner3);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(act,android.R.layout.simple_spinner_item,new String[]{"width less","width longer","height less","height longer"}
        );
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                int item = arg0.getSelectedItemPosition();
                switch (item) {
                    case 0:
                        input1.setEnabled(true);
                        inputbis.setEnabled(false);
                        break;
                    case 1:
                        input1.setEnabled(true);
                        inputbis.setEnabled(false);
                        break;
                    case 2:
                        input1.setEnabled(false);
                        inputbis.setEnabled(true);
                        break;
                    case 3:
                        input1.setEnabled(false);
                        inputbis.setEnabled(true);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        spinner.setAdapter(adapter);


        Spinner spinnerT= (Spinner)dialoglayout.findViewById(R.id.spinnerTrue);
        spinnerT.setVisibility(View.GONE);
        Spinner spinnerF = (Spinner)dialoglayout.findViewById(R.id.spinnerFalse);
        spinnerF.setVisibility(View.GONE);
        TextView TTrue= (TextView)dialoglayout.findViewById(R.id.textViewtrue);
        TTrue.setVisibility(View.GONE);
        TextView TFalse= (TextView)dialoglayout.findViewById(R.id.textViewfalse);
        TFalse.setVisibility(View.GONE);

            alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()

            {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String item = spinner.getSelectedItem().toString();
                    String value1 = input1.getText().toString();
                    String value2= inputbis.getText().toString();
                    pin.setObject(value1+".;."+value2+".;."+item);
                    grid.setModified(true);
                }
            });
    }

    public void createEmailGUI(){
        final TextView tex=(TextView)dialoglayout.findViewById(R.id.textViewcond2);
        tex.setVisibility(View.GONE);
        final EditText inputbis=(EditText)dialoglayout.findViewById(R.id.editTextbis);
        inputbis.setVisibility(View.GONE);
        TextView t=(TextView)dialoglayout.findViewById(R.id.textView);
        t.setText("Email");
        TextView t2=(TextView)dialoglayout.findViewById(R.id.textViewcond1);
        t2.setText("");
        final Spinner spinner = (Spinner)dialoglayout.findViewById(R.id.spinner3);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(act,android.R.layout.simple_spinner_item,new String[]{"equal to","contains","starts with"}
        );
        spinner.setAdapter(adapter);
        final EditText input=(EditText)dialoglayout.findViewById(R.id.editText);
        Object obj = pin.getObject();
        if (obj instanceof String) {
            String lobj = (String) obj;
            String[] array=lobj.split(".;.");
            input.setText(array[0]);
        }

        Spinner spinnerT= (Spinner)dialoglayout.findViewById(R.id.spinnerTrue);
        spinnerT.setVisibility(View.GONE);
        Spinner spinnerF = (Spinner)dialoglayout.findViewById(R.id.spinnerFalse);
        spinnerF.setVisibility(View.GONE);
        TextView TTrue= (TextView)dialoglayout.findViewById(R.id.textViewtrue);
        TTrue.setVisibility(View.GONE);
        TextView TFalse= (TextView)dialoglayout.findViewById(R.id.textViewfalse);
        TFalse.setVisibility(View.GONE);

        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String item = spinner.getSelectedItem().toString();
                String value = input.getText().toString();
                pin.setObject(value+".;."+item);
                grid.setModified(true);
            }
        });
    }

    public void createDynamicImageGUI(){
        final EditText inputbis=(EditText)dialoglayout.findViewById(R.id.editTextbis);
        inputbis.setVisibility(View.GONE);
        TextView t=(TextView)dialoglayout.findViewById(R.id.textView);
        t.setText("First image");
        TextView t2=(TextView)dialoglayout.findViewById(R.id.textViewcond1);
        t2.setVisibility(View.GONE);
        final Spinner spinner = (Spinner)dialoglayout.findViewById(R.id.spinner3);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(act,android.R.layout.simple_spinner_item,new String[]{"width less","width longer","height less","height longer"}
        );
        spinner.setAdapter(adapter);
        final TextView tex=(TextView)dialoglayout.findViewById(R.id.textViewcond2);
        tex.setText("Second image");
        //prendere la selezione e concatenarlo al valore passato in pin.setObject
        final EditText input=(EditText)dialoglayout.findViewById(R.id.editText);
        input.setVisibility(View.GONE);

        this.selectTrueFalse();

        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String item = spinner.getSelectedItem().toString();
                String itemTrue= spinnerTrue.getSelectedItem().toString();
                String itemFalse= spinnerFalse.getSelectedItem().toString();
                grid.getSelected().truePin=itemTrue;
                grid.getSelected().falsePin=itemFalse;
                grid.getSelected().linee=true;
                pin.setObject(item+".;."+itemTrue+".;."+itemFalse);
                grid.setModified(true);
            }
        });
    }

    public void createLocationGUI() {
        final EditText input1=(EditText)dialoglayout.findViewById(R.id.editText);
        Object obj = pin.getObject();
        if (obj instanceof String) {
            String lobj = (String) obj;
            String[] array=lobj.split(".;.");
            input1.setText(array[0]);
        }

        final EditText inputbis=(EditText)dialoglayout.findViewById(R.id.editTextbis);
        if (obj instanceof String) {
            String lobj = (String) obj;
            String[] array=lobj.split(".;.");
            inputbis.setText(array[1]);
        }

        TextView t1=(TextView)dialoglayout.findViewById(R.id.textViewcond1);
        t1.setText("latitude");
        TextView t2=(TextView)dialoglayout.findViewById(R.id.textViewcond2);
        t2.setText("longitude");
        TextView t=(TextView)dialoglayout.findViewById(R.id.textView);
        t.setText("Location");
        final Spinner spinner = (Spinner)dialoglayout.findViewById(R.id.spinner3);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(act,android.R.layout.simple_spinner_item,new String[]{"is equal","is not equal"});
        spinner.setAdapter(adapter);

        Spinner spinnerT= (Spinner)dialoglayout.findViewById(R.id.spinnerTrue);
        spinnerT.setVisibility(View.GONE);
        Spinner spinnerF = (Spinner)dialoglayout.findViewById(R.id.spinnerFalse);
        spinnerF.setVisibility(View.GONE);
        TextView TTrue= (TextView)dialoglayout.findViewById(R.id.textViewtrue);
        TTrue.setVisibility(View.GONE);
        TextView TFalse= (TextView)dialoglayout.findViewById(R.id.textViewfalse);
        TFalse.setVisibility(View.GONE);
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String item = spinner.getSelectedItem().toString();
                        String value1 = input1.getText().toString();
                        String value2 = inputbis.getText().toString();
                        pin.setObject(value1 + ".;." + value2 + ".;." + item);
                        grid.setModified(true);
                    }
                }

        );
    }

    public void createDynamicLocationGUI(){
        final EditText inputbis=(EditText)dialoglayout.findViewById(R.id.editTextbis);
        inputbis.setVisibility(View.GONE);
        TextView t=(TextView)dialoglayout.findViewById(R.id.textView);
        t.setText("First location");
        TextView t2=(TextView)dialoglayout.findViewById(R.id.textViewcond1);
        t2.setVisibility(View.GONE);
        final Spinner spinner = (Spinner)dialoglayout.findViewById(R.id.spinner3);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(act,android.R.layout.simple_spinner_item,new String[]{"is equal","is not equal"}
        );
        spinner.setAdapter(adapter);
        final TextView tex=(TextView)dialoglayout.findViewById(R.id.textViewcond2);
        tex.setText("Second location");
        final EditText input=(EditText)dialoglayout.findViewById(R.id.editText);
        input.setVisibility(View.GONE);

        this.selectTrueFalse();

        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String item = spinner.getSelectedItem().toString();
                String itemTrue= spinnerTrue.getSelectedItem().toString();
                String itemFalse= spinnerFalse.getSelectedItem().toString();
                grid.getSelected().truePin=itemTrue;
                grid.getSelected().falsePin=itemFalse;
                grid.getSelected().linee=true;
                pin.setObject(item+".;."+itemTrue+".;."+itemFalse);
                grid.setModified(true);
            }
        });
    }


}