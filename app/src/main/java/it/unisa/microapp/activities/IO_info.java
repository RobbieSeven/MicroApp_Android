package it.unisa.microapp.activities;

import it.unisa.microapp.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;


public class IO_info extends Activity {
	
	EditText name;
	CheckBox checkBox;
	String valore_stiu,valore_siu, toResult,valore_spuserinput = "null";
	boolean multi = false;
	Spinner sp_userinput;
	
	@Override
    public void onCreate(Bundle b) {
    	super.onCreate(b);
		setContentView(R.layout.io_info);
		
		checkBox = (CheckBox) findViewById(R.id.checkBox1);
		checkBox.setOnClickListener(new OnClickListener() {
			 
			  @Override
			  public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					multi = true;
				}
			  }
			});
        
		Spinner s_iu = (Spinner) findViewById(R.id.input_userinput);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(
        		this,
        		android.R.layout.simple_spinner_item,
        		new String[]{"Input","Userinput"}
        		);
        s_iu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				valore_siu = parent.getItemAtPosition(pos).toString();
				if(valore_siu.equalsIgnoreCase("Userinput")){
					 sp_userinput.setEnabled(true);
				}
			}
		    public void onNothingSelected(AdapterView<?> parent) {
		    }
		});
        s_iu.setAdapter(adapter1);
        
        Spinner s_tiu = (Spinner) findViewById(R.id.type_iu);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
        		this,
        		android.R.layout.simple_spinner_item,
        		new String[]{"OBJECT","STRING","IMAGE","CONTACT","LOCATION","URI","MAIL",}
        		);
        s_tiu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				valore_stiu = parent.getItemAtPosition(pos).toString();
			}
		    public void onNothingSelected(AdapterView<?> parent) {
		    }
		});
        s_tiu.setAdapter(adapter2);
        
        sp_userinput = (Spinner) findViewById(R.id.spinner_userinput);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(
        		this,
        		android.R.layout.simple_spinner_item,
        		new String[]{"Seleziona","string","date","image","contact","password","location","number","audio","video"}
        		);
        sp_userinput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				valore_spuserinput = parent.getItemAtPosition(pos).toString();
			}
		    public void onNothingSelected(AdapterView<?> parent) {
		    }
		});
        sp_userinput.setAdapter(adapter3);
        sp_userinput.setEnabled(false);
        
        name = (EditText)findViewById(R.id.name_iu);
        
        Button btn = (Button)findViewById(R.id.save_ioinfo);
        btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				toResult = valore_siu+"/"+valore_stiu+"/" +
						""+name.getText().toString()+"/"+multi+"/"+
						valore_spuserinput;
				Intent intent=new Intent();
			    intent.putExtra("ComingFrom", toResult);
			    setResult(RESULT_OK, intent);
			    finish();
			}
        	
        });
	}

	@Override
	public void onBackPressed() {
		Intent intent=new Intent();
	    intent.putExtra("ComingFrom", "backpressed");
	    setResult(RESULT_OK, intent);
	    finish();
	}
}
