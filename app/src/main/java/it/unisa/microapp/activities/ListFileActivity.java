package it.unisa.microapp.activities;

import java.io.IOException;

import org.xml.sax.SAXException;

import it.unisa.microapp.MicroAppGenerator;
import it.unisa.microapp.R;
import it.unisa.microapp.store.DownloadSearchAppActivity;
import it.unisa.microapp.store.MyDownloadedAppActivity;
import it.unisa.microapp.support.FileManagement;
import it.unisa.microapp.utils.Utils;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ListFileActivity extends ListActivity {

	private Button download;
	private Button vota;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listfile);

		vota = (Button)findViewById(R.id.button_vota);
		vota.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ListFileActivity.this, MyDownloadedAppActivity.class);
				startActivity(intent);
			}
		});

		download = (Button)findViewById(R.id.button_download);
		download.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ListFileActivity.this, DownloadSearchAppActivity.class);
				startActivity(intent);
				finish();
			}
		});

		FileManagement fm=new FileManagement();
		String[] files=fm.getListOnLocalDiretory();

		if (files==null){
			Utils.errorDialog(this,getString(R.string.notExists)); 
			return;	  
		}


		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, files));
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

				MicroAppGenerator ap=(MicroAppGenerator) getApplication();
				//String msg="";
				//Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
				try {
					ap.setDeployPath(((TextView) view).getText().toString(), false);
					ap.parsingDownload(((TextView) view).getText().toString());

				} catch (NullPointerException e) {
				} catch (SAXException e) {
					String msg="Reading file is corrupted";
					Utils.errorDialog(ListFileActivity.this, msg, e.getMessage());
				} catch (IOException e) {
					String msg="File opening error";
					Utils.errorDialog(ListFileActivity.this, msg, e.getMessage());
				}

				finish();
			}
		});


	}
	
	public void onBackPressed(){
		finish();
	}

}
