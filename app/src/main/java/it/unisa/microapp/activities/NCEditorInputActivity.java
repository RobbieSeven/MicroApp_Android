package it.unisa.microapp.activities;

import it.unisa.microapp.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class NCEditorInputActivity extends Activity{
	EditText initinput;
	String nameActivity, theText_I;
	Button bnt;
	
	public void onCreate(Bundle b) {
		super.onCreate(b);
		Bundle extras = getIntent().getExtras();
		nameActivity = extras.getString("nameActivity");
		
		setContentView(R.layout.nceditorinput);
		
		initinput = (EditText)findViewById(R.id.edinitinputet);
		
		theText_I = initinput.getText().toString();
		
		bnt = (Button) findViewById(R.id.next_editor_input);
		bnt.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

		Intent Intent_inp = new Intent(NCEditorInputActivity.this,
				NCEditorBehaviourActivity.class);
		Intent_inp.putExtra("InputEditor", theText_I); //Optional parameters
		Intent_inp.putExtra("nActivity", nameActivity );
		NCEditorInputActivity.this.startActivity(Intent_inp);
				}
		});
	}
	
	
	@Override
	public void onBackPressed() {
		Intent intent=new Intent();
	    intent.putExtra("DaEditor", "backpressed");
	    setResult(RESULT_OK, intent);
	    finish();
	}
	
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		menu.add(3, 1, 1, getString(R.string.test)).setIcon(android.R.drawable.ic_media_play);
		menu.add(3, 2, 2, getString(R.string.save)).setIcon(android.R.drawable.ic_menu_save);
		return true;
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case 1:
			test();
			break;
		case 2:
			bnt.performClick();
			break;
		default:
			break;
		}
		return true;
	}
	
	public void test() {
		initinput.setText("Iterable<GenericData<?>> it = application.getData(mycomponent.getId(), DataType.STRING);\n" +
				"if (it != null)\n" +
				"for (GenericData<?> d : it) {\n" +
				"StringData st = (StringData) d;\n" +
				"for (String s : st.getData()) {\n" +
				"message = message + \"\\n\" + s;\n" +
				"}\n" +
				"}", TextView.BufferType.EDITABLE);
	}
		

}
