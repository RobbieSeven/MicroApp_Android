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
import android.widget.EditText;
import android.widget.Spinner;


public class IO_info_out extends Activity {
	
	EditText name;
	String valore_stiu, toResult;
	
	@Override
    public void onCreate(Bundle b) {
    	super.onCreate(b);
		setContentView(R.layout.io_info_out);
        
        Spinner s_tiu = (Spinner) findViewById(R.id.type_iu_out);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
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
        s_tiu.setAdapter(adapter);
        
        name = (EditText)findViewById(R.id.name_iu_out);
        
        Button btn = (Button)findViewById(R.id.save_ioinfo_out);
        btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				toResult = "null"+"/"+valore_stiu+"/"+name.getText().toString();
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
