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

public class NCEditorBehaviourActivity extends Activity{

	EditText behaviour;
	String nActivity;
	Button bnt;
	String InputString, theText_B;
	
	public void onCreate(Bundle b) {
		super.onCreate(b);
		Bundle extras = getIntent().getExtras();
		nActivity = extras.getString("nActivity");
		Intent initinput = getIntent();
		InputString = initinput.getStringExtra("InputEditor");
		
		setContentView(R.layout.nceditorbehavoiur);
		
		behaviour = (EditText)findViewById(R.id.edbehaviouret);
		
		theText_B = behaviour.getText().toString();
		
		bnt = (Button) findViewById(R.id.next_editor_behaviour);
		bnt.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent Intent_b = new Intent( NCEditorBehaviourActivity.this, NCEditorOutputActivity.class);
				Intent_b.putExtra("BehaEditor", theText_B); //Optional parameters
				Intent_b.putExtra("InEditor", InputString); //Optional parameters
				Intent_b.putExtra("n_Activity", nActivity );
				 NCEditorBehaviourActivity.this.startActivity(Intent_b);
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
		behaviour.setText("AlertDialog.Builder builder=new AlertDialog.Builder(conte);\n" +
				"builder.setTitle(\"Prompt\");\n" +
				"builder.setMessage(message);\n" +
				"builder.setPositiveButton(android.R.string.ok, new OnClickListener(){\n" +
				"public void onClick(DialogInterface dialog, int which)\n" +
				"{\n" +
				"scaff.next();\n" +
				"}\n" +
				"});\n" +
				"AlertDialog dial=builder.create();\n" +
				"dial.show();", TextView.BufferType.EDITABLE);
	}
		
}
