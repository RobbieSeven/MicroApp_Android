package it.unisa.microapp.store;

import it.unisa.microapp.R;
import it.unisa.microapp.support.FileManagement;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

public class MyDownloadedAppActivity extends Activity{

	private ListView listview;
	private ArrayList<Downloaded> list;
	private DBvoti db;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download);

		db = (DBvoti) SingletonParametersBridge.getInstance().getParameter("db");

		listview = (ListView) findViewById(R.id.downloadlist1);

		FileManagement fm = new FileManagement();
		String [] files = fm.getListOnLocalDiretory();
		list = new ArrayList<Downloaded>();

		for (int i=0; i<files.length; i++){
			Downloaded d = new Downloaded(files[i]);
			String [] tipo_app = files[i].split("_");
			if(tipo_app[tipo_app.length-1].equals("downloaded.xml")){
				d.setMyapp(false);
				Cursor rs = db.getVoto(files[i].split("\\.")[0]);
				int n = rs.getInt(0);
				d.setVotato(n);
				list.add(d);
			}

		}
		CustomListViewDownlodedApp adap = new CustomListViewDownlodedApp(this, R.layout.downloaditem, list);
		listview.setAdapter(adap);
	}

}