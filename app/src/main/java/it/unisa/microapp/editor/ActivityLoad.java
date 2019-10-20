package it.unisa.microapp.editor;

import it.unisa.microapp.R;
import it.unisa.microapp.support.FileManagement;
import it.unisa.microapp.utils.Constants;

import java.io.File;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityLoad extends Activity
{
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.itemselection);

		File[] cols = FileManagement.getListOnLocalDirectory();

		if (cols.length == 0)
			Toast.makeText(this, getString(R.string.noXml), Toast.LENGTH_LONG).show();
		else 
		{
			String[] string = new String[cols.length];
			for(int i = 0; i < string.length; i++)
			{
				string[i] = cols[i].getName();
			}

			ListView list1 = (ListView) this.findViewById(R.id.listview1);

			list1.setAdapter(new MyCustomAdapter(this, R.layout.rigatextview2, string));

			list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View v, int position, long id)
				{

					TextView tv = (TextView) v.findViewById(R.id.iconanomeT);
					String st = (String) tv.getText();

					Intent i = new Intent(v.getContext(), EditorActivity.class);
					Bundle b = new Bundle();
					b.putString("nomefile", st);
					i.putExtras(b);
					setResult(Constants.ID_SELECT_ICON, i);
					finish();
				}
			});
		}

	}

	public class MyCustomAdapter extends ArrayAdapter<String>
	{
		Object[] objects;

		public MyCustomAdapter(Context context, int textViewResourceId, String[] objects)
		{

			super(context, textViewResourceId, objects);
			this.objects = objects;// lista nomi file

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
			View row = inflater.inflate(R.layout.rigatextview2, parent, false);

			TextView label = (TextView) row.findViewById(R.id.iconanomeT);
			label.setText(items[position]);

			return row;
		}
	}

}
