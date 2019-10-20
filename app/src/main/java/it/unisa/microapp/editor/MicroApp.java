package it.unisa.microapp.editor;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import it.unisa.microapp.MicroAppGenerator;
import it.unisa.microapp.R;
import it.unisa.microapp.library.Library;
import it.unisa.microapp.support.FileManagement;
import it.unisa.microapp.utils.Utils;
import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;

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

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MicroApp extends Activity {
	/** Called when the activity is first created. */

	String[] Icon;
	String[] Icons;
	String[] attlabel = { "ApplicationMenu", "GestureActivator"};
	String namefile =null;
	String desc = null;
	boolean isFileload = false;
	int scelta=0;
	String activator;
	boolean isActive = true;

	//libreria gesti
	//private GestureLibrary mLibrary;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configureeditor);

		isFileload = false;
		Icon = Library.getCategories(this);
		Icons = Library.getIcons(this);

		/* spinner 1 */
		final Spinner s = (Spinner) findViewById(R.id.spinner);
		s.setAdapter(new MyCustomAdapter(this, R.layout.rigaspinner, Icon, Icons));

		/* spinner2 utilizzato per lanciare un app attraverso un gesto */
		final Spinner s2 = (Spinner) findViewById(R.id.spinner2);
		
		final EditText descrizione = (EditText) findViewById(R.id.description);

		final Button bnt = (Button) findViewById(R.id.btnnext);
		
		
		Bundle bu = getIntent().getExtras();
		if (bu != null)
		{
			namefile = bu.getString("namefile");
			
			if (namefile != null) 
			{	
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

						NodeList activator = root.getElementsByTagName("activator");
						
						//se l'attivazione conteneva gia' un gesto lo riporto nello spinner
						//e setto il nome che l'utente gli aveva dato in precedenza
						
						if(activator.item(0).getTextContent().contains("GestureActivator"))
						{
							attlabel[1]=activator.item(0).getTextContent();
							scelta=1;
						}
						s2.setAdapter(new MyCustomAdapter(this, R.layout.rigaspinner, attlabel, R.drawable.gesture48));
						//se non lo conteneva setto la scelta di default
						s2.setSelection(scelta);
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
						s2.setSelection(0);

						isActive = !testo.getText().toString().equals("");
						s2.setEnabled(isActive);
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
			s2.setAdapter(new MyCustomAdapter(this, R.layout.rigaspinner, attlabel, R.drawable.gesture48));
			
			final EditText testo = (EditText) findViewById(R.id.nomeapp);
			testo.setText(testo.getText() + "_" + Utils.linearizeData());
			
			testo.addTextChangedListener(new TextWatcher(){

				@Override
				public void afterTextChanged(Editable se) {
					isActive = !testo.getText().toString().equals("");
					s2.setEnabled(isActive);
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
				
		s2.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				//se la mia scelta è quella di eseguire il gesto lancio l'intent
				if(arg2==1)
					creaGesto();
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		bnt.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				next(view);
			}
		});
	}

	
	public void next(View view)
	{
		next();
	}
	
	
	protected void creaGesto() {
		
		//creo intent per eseguire il gesto
		final Intent intent=new Intent(this.getApplicationContext(), CreateGestureActivity.class);
		
		MicroAppGenerator app = (MicroAppGenerator) getApplication();
		//non era già impostato un gesto per l'applicazione
		if(scelta==0 && !app.getFlagGesture())
		{	
			final EditText testo = (EditText) findViewById(R.id.nomeapp);

			Bundle b = new Bundle();
			b.putString("namefile", testo.getText().toString());
			intent.putExtras(b);
			startActivity(intent);
		}
	}
	
	public void next() {
		// avvio la seconda activity
		EditText testo = (EditText) findViewById(R.id.nomeapp);
		EditText descrizione = (EditText) findViewById(R.id.description);
		MicroAppGenerator app = (MicroAppGenerator) getApplication();
		Spinner s = (Spinner) findViewById(R.id.spinner);		
		Spinner s2 = (Spinner) findViewById(R.id.spinner2);
		if(namefile!=null)
			if(namefile.equals(testo.getText().toString())==false && s2.getSelectedItemPosition()==1)
			{
			s2.setSelection(0);
			}
		namefile = testo.getText().toString();
		desc = descrizione.getText().toString();

		int pos = s.getSelectedItemPosition();

		if (namefile.trim().compareTo("") == 0)
			Toast.makeText(this, "Name is mandatory", Toast.LENGTH_LONG).show();
		else
		{
			
			Intent i = new Intent(this, EditorActivity.class);
			Bundle b = new Bundle();
			b.putString("namefile", namefile);
			b.putString("description", desc);
			String key = Icons[pos].substring(11);
			b.putString("icon", key);
			
			
			if(s2.getSelectedItemPosition()==0 && scelta==1)
			{
				boolean flag=false;
				GestureLibrary store = GestureLibraries.fromFile(FileManagement.getGestureDir());	
				if (store.load()) 
					
	                for (String name : store.getGestureEntries()) 
	                	{
	                		
	                		for (Gesture gesture : store.getGestures(name)) 
	                		{
	                        
	                			if((namefile+".xml").equals(name))
	                			{
	                				store.removeGesture(namefile+".xml", gesture);
	                				store.save();
	                				flag=true;
	                				break;
	                			}
	                		
	                			if((namefile+".xml").equals(name))
	                			{
	                				store.removeGesture(namefile+".xml", gesture);
	                				store.save();
	                				flag=true;
	                				break;
	                			}
	                		}
	                		
	                		if(flag)
	                			break;
	                	}
			}
				
				//ho scelto un gesto e questo non era presente già nella mia app
			if(s2.getSelectedItemPosition()==1 && scelta==0)
					b.putString("activator", "GestureActivator"+"("+app.getGestureName()+")");
				else
				{
					//scelta senza cambiamenti
					b.putString("activator",s2.getSelectedItem().toString());
					GestureLibraries.fromFile(FileManagement.getGestureDir());
				}
					

			b.putBoolean("load", isFileload);
			i.putExtras(b);
			this.startActivity(i);

		}
	}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(1, 1, 1, getString(R.string.back)).setIcon(android.R.drawable.ic_media_previous);
		menu.add(1, 2, 2, getString(R.string.next)).setIcon(android.R.drawable.ic_media_next);
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
				Log.d("ICON", "icon uri" + id);

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