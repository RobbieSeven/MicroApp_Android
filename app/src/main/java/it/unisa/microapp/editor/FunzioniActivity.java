package it.unisa.microapp.editor;

import it.unisa.microapp.utils.Utils;

import java.util.ArrayList;

import android.app.Activity;

import android.os.Bundle;

import android.util.DisplayMetrics;

public class FunzioniActivity extends Activity
{
	IconsPanel main;
	String padre;
	private String file;
	ManagementXML g = null;
	ArrayList<Piece> lista = new ArrayList<Piece>();

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int h = metrics.heightPixels;
		int w = metrics.widthPixels;
		
		// ********************************************************************

		Bundle b = getIntent().getExtras();
		padre = b.getString("Padre");
		// *******************************************************
		file=b.getString("file");
		Utils.verbose("funzioni:"+file);
		main = new IconsPanel(this, h - 75, w, padre,file);
		
		//main = (it.unisa.microapp.editor.MainPanel)findViewById(R.id.surface);
		//main.setFile(file);
		main.setId(6430);
		registerForContextMenu(main);
		setContentView(main);

	}

}
