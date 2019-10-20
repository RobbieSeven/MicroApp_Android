package it.unisa.microapp.project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteStatement;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import it.unisa.microapp.MicroAppGenerator;
import it.unisa.microapp.R;
import it.unisa.microapp.editor.CreateGestureActivity;
import it.unisa.microapp.library.Library;
import it.unisa.microapp.support.FileManagement;
import it.unisa.microapp.utils.Utils;

/**
 * Created by martina on 11/07/2015.
 */
public class SensorApp extends Activity {
    /** Called when the activity is first created. */

    String[] Icon;
    String[] Icons;
    String[] attlabel = { "ApplicationMenu", "SensorActivator"};
    String namefile =null;
    String desc = "";
    boolean isFileload = false;
    int scelta=0;
    //    String activator;
    boolean isActive = true;
    ArrayList<String> sensor;
    EditText testo;
    String icon;
    Spinner s;
    RadioGroup radioSensor;
    RadioButton sensorButton;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuresensor);

        isFileload = false;
        Icon = Library.getCategories(this);
        Icons = Library.getIcons(this);

		/* spinner 1 */
        s = (Spinner) findViewById(R.id.spinner);
        s.setAdapter(new MyCustomAdapter(this, R.layout.rigaspinner, Icon, Icons));

		/* spinner2 utilizzato per lanciare un app attraverso un gesto */
//        final Spinner s2 = (Spinner) findViewById(R.id.spinner2);

        final EditText descrizione = (EditText) findViewById(R.id.description);

        final Button bnt = (Button) findViewById(R.id.btnsave);

        radioSensor= (RadioGroup) findViewById(R.id.radioSensor);
        bnt.setEnabled(false);
        radioSensor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                                   @Override
                                                   public void onCheckedChanged(RadioGroup group, int checkedId) {
                                                       bnt.setEnabled(true);
                                                   }
                                               });


                SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor_accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor sensor_pressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        Log.d("sensor", "" + sensor_accelerometer + sensor_pressure);

        Bundle bu = getIntent().getExtras();
        if (bu != null)
        {
            Log.d("qui","qui");
            namefile = bu.getString("namefile");

            if (namefile != null)
            {
                Log.d("qui","qui");
                try
                {
                    File xmlUrl = new File(FileManagement.getLocalAppPath(), namefile);

                    if (xmlUrl.exists())
                    {
                        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                        Document doc = db.parse(xmlUrl);
                        doc.getDocumentElement().normalize();
                        Element root = doc.getDocumentElement();

                        NodeList description = root.getElementsByTagName("description");
                        if (description.getLength() > 0)
                        {
                            descrizione.setText(description.item(0).getTextContent());
                        }

                        NodeList icon = root.getElementsByTagName("icon");
                        if (icon.getLength() > 0)
                        {
                            for (int i = 0; i < Icons.length; i++)
                            {
                                if (Icons[i].endsWith("." + icon.item(0).getTextContent()))
                                {
                                    s.setSelection(i);
                                    break;
                                }
                            }
                        }


                    }
                    isFileload = true;
                }
                catch (SAXException e)
                {
                }
                catch (IOException e)
                {
                }
                catch (ParserConfigurationException e)
                {
                }
                catch (FactoryConfigurationError e)
                {
                }

                int pos = namefile.indexOf(".");
                if (pos > 0)
                {
                    namefile = namefile.substring(0, pos);
                }


                final EditText testo = (EditText) findViewById(R.id.nomeapp);
                testo.setText(namefile);

                testo.addTextChangedListener(new TextWatcher(){

                    @Override
                    public void afterTextChanged(Editable se) {
                        attlabel[1]="GestureActivator";
                        scelta=0;
//                        s2.setSelection(0);

                        isActive = !testo.getText().toString().equals("");
//                        s2.setEnabled(isActive);
                        s.setEnabled(isActive);
                        descrizione.setEnabled(isActive);
                        bnt.setEnabled(isActive);
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                    }

                });

            }
        }

        else//sto editando un nuovo file
        {
//            s2.setAdapter(new MyCustomAdapter(this, R.layout.rigaspinner, attlabel, R.drawable.gesture48));

            testo = (EditText) findViewById(R.id.nomeproject);
            testo.setText(testo.getText() + "_" + Utils.linearizeData());

            testo.addTextChangedListener(new TextWatcher(){

                @Override
                public void afterTextChanged(Editable se) {
                    isActive = !testo.getText().toString().equals("");
//                    s2.setEnabled(isActive);
                    s.setEnabled(isActive);
                    descrizione.setEnabled(isActive);
                    bnt.setEnabled(isActive);

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                }

            });
        }



        bnt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                next(view);
            }
        });
    }


    public void next(View view)
    {
        next();
        save();
    }

    private void save() {
        //salvare il progetto
        SensorTable table = new SensorTable(this);
        table.open();
        SQLiteStatement ifExist = table.db().compileStatement(" SELECT COUNT(*) " + " FROM "
                + SensorTable.TABLE_SENSOR + " WHERE " + SensorTable.NAME_PROJECT  + "=" + "'" + testo.getText().toString() + "';");
        long countIfExist = ifExist.simpleQueryForLong();
        if(countIfExist>0){
            SQLiteStatement s9 = table.db().compileStatement(" DELETE FROM "
                    + SensorTable.TABLE_SENSOR + " WHERE " + SensorTable.NAME_PROJECT  + "=" + "'" + testo.getText().toString() + "';");

            s9.execute();
        }

        String sensor = "";
        int selectedId = radioSensor.getCheckedRadioButtonId();

        // find the radiobutton by returned id
        sensorButton = (RadioButton) findViewById(selectedId);

        sensor = sensorButton.getText().toString();




        Log.d("INFO", "Sensori: " +sensor + "Icon " + icon + "desc: " +desc);

        table.insertUser(testo.getText().toString(), sensor, icon, desc);
//        SQLiteStatement s = table.db().compileStatement(" SELECT COUNT(*) " + " FROM "
//                + SensorTable.TABLE_SENSOR + ";");
//
//        long count = s.simpleQueryForLong();
//        Log.d("Size table:", String.valueOf(count));
//        table.close();


        finish();
    }

    public void next() {
        // avvio la seconda activity
        EditText testo = (EditText) findViewById(R.id.nomeproject);
        EditText descrizione = (EditText) findViewById(R.id.description);
        MicroAppGenerator app = (MicroAppGenerator) getApplication();
        Spinner s = (Spinner) findViewById(R.id.spinner);

        namefile = testo.getText().toString();
        desc = descrizione.getText().toString();

        int pos = s.getSelectedItemPosition();


        String key = Icons[pos].substring(11);

        icon = key;


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, 1, 1, getString(R.string.back)).setIcon(android.R.drawable.ic_media_previous);
        menu.add(1, 2, 2, getString(R.string.save)).setIcon(android.R.drawable.ic_media_next);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        menu.getItem(1).setEnabled(isActive);
        return true;
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                this.finish();
                break;
            case 2:
                this.next();
                this.save();
                break;
            default:
                break;
        }
        return false;
    }

    public class MyCustomAdapter extends ArrayAdapter<String> {
        Object[] objects;
        Object[] icons;
        int fixedIcon = 0;

        public MyCustomAdapter(Context context, int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);
            this.objects = objects;
            fixedIcon = 0;
        }

        public MyCustomAdapter(Context context, int textViewResourceId, String[] objects, int fixedIcon) {
            super(context, textViewResourceId, objects);
            this.objects = objects;
            this.fixedIcon = fixedIcon;
        }

        public MyCustomAdapter(Context context, int textViewResourceId, String[] objects, String[] objectIcon) {
            super(context, textViewResourceId, objects);
            this.objects = objects;
            this.icons = objectIcon;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            String[] items = (String[]) this.objects;
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.rigaspinner, parent, false);

            if (this.fixedIcon == 0) {
                TextView label = (TextView) row.findViewById(R.id.iconanome);
                label.setText(Icon[position]);

                ImageView icon = (ImageView) row.findViewById(R.id.icona);

                String key = "";
                if (Icons.length == 0) {
                    key = items[position].toLowerCase(Locale.getDefault()) + "48";

                } else {
                    key = Icons[position];
                    key = key.substring(11);
                }

                int id = getResources().getIdentifier(key, "drawable", getPackageName());
                if (id > 0) {
                    icon.setImageResource(id);
                } else
                    icon.setImageResource(R.drawable.icon);
            } else {
                TextView label = (TextView) row.findViewById(R.id.iconanome);
                label.setText(attlabel[position]);
                ImageView icon = (ImageView) row.findViewById(R.id.icona);
                if(position == 0)
                    icon.setImageResource(R.drawable.menu48);
                else
                    icon.setImageResource(this.fixedIcon);

            }

            return row;
        }
    }


}
