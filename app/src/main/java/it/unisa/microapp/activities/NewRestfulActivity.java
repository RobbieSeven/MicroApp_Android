package it.unisa.microapp.activities;

import java.util.ArrayList;

import it.unisa.microapp.R;
import it.unisa.microapp.editor.NewCompPossibleStates;
import it.unisa.microapp.library.Library;
import it.unisa.microapp.library.LibraryParser;
import it.unisa.microapp.utils.Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class NewRestfulActivity extends Activity {
	EditText et, desc;
	String[] Icon;
	String[] Icons;
	String valoreSpinner, nameFile;
	boolean flag = false;
	ArrayList<String> inputList = new ArrayList<String>();
	ArrayList<String> outputList = new ArrayList<String>();
	String listates;

	public void onCreate(Bundle b) {
		super.onCreate(b);

		setContentView(R.layout.restful);

		Icon = Library.getCategories(this);
		Icons = Library.getIcons(this);

		et = (EditText) findViewById(R.id.name_nc);
		desc = (EditText) findViewById(R.id.description_nc);

		Spinner s = (Spinner) findViewById(R.id.spinner);
		s.setAdapter(new MyCustomAdapter(this, R.layout.rigaspinner, Icon,
				Icons));
		s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				valoreSpinner = parent.getItemAtPosition(pos).toString();
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		Button add_nc = (Button) findViewById(R.id.add_newcategory);
		add_nc.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						NewRestfulActivity.this);

				final EditText input = new EditText(NewRestfulActivity.this);
				// set title
				alertDialogBuilder.setTitle("New Category");

				// set dialog message
				alertDialogBuilder
						.setCancelable(false)
						.setView(input)
						.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {

									}
								})
						.setPositiveButton("Create",
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										LibraryParser.addCategory(input
												.getText().toString());
										aggiorna();

										Toast.makeText(
												NewRestfulActivity.this,
												"Category created: "
														+ input.getText()
																.toString(),
												Toast.LENGTH_LONG).show();
									}

								});
				AlertDialog alert = alertDialogBuilder.create();
				alert.show();
			}
		});

		Button bnt = (Button) findViewById(R.id.save_nc);
		bnt.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				nextDef();
			}
		});
	}

	public void aggiorna() {
		Library.initialize(this);
		finish();
		startActivity(this.getIntent());
	}

	public void nextDef() {
		Intent i = new Intent(this, NewCompPossibleStates.class);
		this.startActivityForResult(i, 1);
	}

	public void nextToEditor() {
		Intent i = new Intent(this, NCEditorInputActivity.class);
		i.putExtra("nameActivity", et.getText().toString());
		final int result = 1;
		this.startActivityForResult(i, result);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 1) {
			super.onActivityResult(requestCode, resultCode, data);
			if (data.getSerializableExtra("DaEditor") == null) {

				@SuppressWarnings("unchecked")
				ArrayList<ArrayList<String>> res = (ArrayList<ArrayList<String>>) data
						.getSerializableExtra("ComingFrom");
				@SuppressWarnings("unchecked")
				ArrayList<CharSequence> pes = (ArrayList<CharSequence>) data
						.getSerializableExtra("ComingFromPS");

				inputList = res.get(0);
				outputList = res.get(1);

				// inizio modifica
				for (int i = 0; i < pes.size(); i++) {
					Utils.debug(pes.get(i).toString());
					if (i == pes.size() - 1) {
						listates += pes.get(i);
						break;
					}
					listates += pes.get(i) + "/";
				}
				// fine modifica

				nextToEditor();
			} else if (data.getSerializableExtra("DaEditor").equals(
					"backpressed")) {

			} else if (data.getSerializableExtra("filename") != null) {
				// next(filename);
			} else
				System.err.println("File vuoto!");
		}
	}

	public void onBackPressed() {
		finish();
	}

	public class MyCustomAdapter extends ArrayAdapter<String> {
		Object[] objects;
		String[] fixedIcon;

		public MyCustomAdapter(Context context, int textViewResourceId,
				String[] objects, String[] fixedIcon) {
			super(context, textViewResourceId, objects);
			this.objects = objects;// lista nomi
			this.fixedIcon = fixedIcon;
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}

		public View getCustomView(int position, View convertView,
				ViewGroup parent) {

			String[] items = (String[]) this.objects;
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.rigatextview, parent, false);

			TextView label = (TextView) row.findViewById(R.id.iconanomeT);
			label.setText(items[position]);

			ImageView icon = (ImageView) row.findViewById(R.id.iconaT);
			String key = fixedIcon[position];
			key = key.substring(11);
			int id = getResources().getIdentifier(key, "drawable",
					getPackageName());
			Log.d("ICON", "icon key 214" + key);

			if (id > 0) {
				icon.setImageResource(id);
			} else
				icon.setImageResource(R.drawable.icon);

			return row;
		}
	}
}
