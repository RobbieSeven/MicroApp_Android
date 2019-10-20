package it.unisa.microapp.editor;

import java.util.ArrayList;

import it.unisa.microapp.R;
import it.unisa.microapp.activities.IO_NcActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class NewCompPossibleStates extends Activity{
	private ArrayList<CharSequence> checks = new ArrayList<CharSequence>();
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			
		setContentView(R.layout.possiblestates);
		
		TextView text1 = (TextView) findViewById(R.id.textState);
		text1.setText("visible");
		text1.setEnabled(false);
		CheckBox cb = (CheckBox) findViewById(R.id.checkBoxState);
		cb.setChecked(true);
		cb.setEnabled(false);
		checks.add(text1.getText());
		
		final TextView text2 = (TextView) findViewById(R.id.textState2);
		text2.setText("hidden");
		text2.setEnabled(false);	
		
		final CheckBox check2 = (CheckBox) findViewById(R.id.checkBoxState2);
		check2.setEnabled(true);
		check2.setChecked(false);
		text2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
            	if(!check2.isChecked()) {
            		check2.setChecked(true);
        			checks.add(text2.getText());
            	}
            	else if(check2.isChecked()) {
            		check2.setChecked(false);
            		checks.remove(text2.getText());
            	}
            }
        });
		
		final TextView text3 = (TextView) findViewById(R.id.textState3);
		text3.setText("progress");
		text3.setEnabled(false);
		
		final CheckBox check3 = (CheckBox) findViewById(R.id.checkBoxState3);
		check3.setEnabled(true);
		check3.setChecked(false);
		text2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
            	if(!check3.isChecked()) {
            		check3.setChecked(true);
        			checks.add(text3.getText());
            	}
            	else if(check3.isChecked()) {
            		check3.setChecked(false);
            		checks.remove(text3.getText());
            	}
            }
        });
		
		Button set = (Button) this.findViewById(R.id.buttonSave);
		
		set.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(NewCompPossibleStates.this, IO_NcActivity.class);
				final int result=1;
				NewCompPossibleStates.this.startActivityForResult(i,result);
			}

		});	
		
		Button back = (Button) this.findViewById(R.id.buttonBack);

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent returnIntent = new Intent();
				setResult(RESULT_CANCELED, returnIntent);        
				finish();
			}

		});
		
	}
	
	@Override
	public void onBackPressed() {
		this.finish();
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

			  @SuppressWarnings("unchecked")
			  ArrayList<ArrayList<String>> res = (ArrayList<ArrayList<String>>) data.getSerializableExtra("ComingFrom");
			  Intent intent=new Intent();
			  intent.putExtra("ComingFrom", res);
			  intent.putExtra("ComingFromPS", checks);
			  setResult(1, intent);
			  finish();
	}
}