package it.unisa.microapp.editor;

import it.unisa.microapp.R;
import it.unisa.microapp.library.Library;
import it.unisa.microapp.utils.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ChoiceIconActivity extends Activity
{

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.itemselection);

		String[] cols = Library.getCategories(this);
		String[] icone = Library.getIcons(this);

		if (cols == null)
			cols = new String[] { "Loading Error" };

		ListView list1 = (ListView) this.findViewById(R.id.listview1);

		list1.setAdapter(new MyCustomAdapter(this, R.layout.rigatextview, cols, icone));

		list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id)
			{

				TextView tv = (TextView) v.findViewById(R.id.iconanomeT);
				String st = (String) tv.getText();

				Intent i = new Intent(v.getContext(), FunzioniActivity.class);
				Bundle myBundle=ChoiceIconActivity.this.getIntent().getExtras();
				String file=myBundle.getString("file");
				Utils.verbose("sceltaicon:"+file);
				Bundle b = new Bundle();
				b.putString("Padre", st);
				b.putString("file", file);
				i.putExtras(b);
				startActivityForResult(i, 3);
			}
		});

	}
		
	@SuppressWarnings("unused")
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == 2)
		{

			Intent in = data;
			Bundle b = in.getExtras();
			if (b != null)
			{
				boolean selezione = b.getBoolean("selezione");
				int inserimenti = b.getInt("inserimenti");
				boolean entra = b.getBoolean("entra");
				boolean StopinseriMulti = b.getBoolean("StopinseriMulti");
				boolean Vmulti = b.getBoolean("Vmulti");
				int multiconta = b.getInt("multiconta");
				int multico = b.getInt("multico");
				int userCont = b.getInt("userCont");
				boolean UserIns = b.getBoolean("UserIns");
				int pont = b.getInt("pont");
				int conta = b.getInt("conta");
				String padre = b.getString("padre");
				setResult(1, in);
				finish();
			}
			// }
		}
	}

	public class MyCustomAdapter extends ArrayAdapter<String>
	{
		Object[] objects;
		String[] fixedIcon;

		public MyCustomAdapter(Context context, int textViewResourceId, String[] objects, String[] fixedIcon)
		{
			super(context, textViewResourceId, objects);
			this.objects = objects;// lista nomi
			this.fixedIcon = fixedIcon;
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent)
		{
			return getCustomView(position, convertView, parent);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			return getCustomView(position, convertView, parent);
		}

		public View getCustomView(int position, View convertView, ViewGroup parent)
		{

			String[] items = (String[]) this.objects;
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.rigatextview, parent, false);

			TextView label = (TextView) row.findViewById(R.id.iconanomeT);
			label.setText(items[position]);

			ImageView icon = (ImageView) row.findViewById(R.id.iconaT);
			String key = fixedIcon[position];
			key = key.substring(11);
			int id = getResources().getIdentifier(key, "drawable", getPackageName());
			Log.d("ICON", "icon key 129" + key);
			if (id > 0)
			{
				icon.setImageResource(id);
			} else
				icon.setImageResource(R.drawable.icon);

			return row;
		}
	}

}
