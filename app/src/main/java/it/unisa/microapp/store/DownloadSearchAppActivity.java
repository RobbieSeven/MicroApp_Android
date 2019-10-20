package it.unisa.microapp.store;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import it.unisa.microapp.R;
import it.unisa.microapp.activities.ListFileActivity;
import it.unisa.microapp.utils.Utils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class DownloadSearchAppActivity extends Activity implements OnClickListener,OnItemClickListener,Runnable{

	private Button search;
	private EditText daCercare;
	private ListView list;
	private String toSearch;
	private ArrayList<App> app;
	private JSONArray jArray = new JSONArray();
	private String query;
	private ConnectDb connect = new ConnectDb();
	private ProgressDialog progDailog;
	private int statoDialog=-1;
	//private long start;
	//private long end;
	private String id;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchapp);

		daCercare = (EditText) findViewById(R.id.searchapp1);
		search = (Button) findViewById(R.id.searchdownload);
		search.setOnClickListener(this);

		list = (ListView) findViewById(R.id.list1);
		list.setOnItemClickListener(this);

	}

	public void onClick(View v) {
		toSearch = daCercare.getText().toString().trim();
		if(!toSearch.equals(""))
			loading();
	}

	
	
	public void loading(){
		progDailog = new ProgressDialog(this);
		//progDailog.setTitle("Discovering application"+getString(R.string.pleaseWait));
		progDailog.setMessage("Discovering applications");
		progDailog.setCancelable(true);
		progDailog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialoginterface) {
				progDailog.dismiss();
				
			}
		});		
		
		progDailog.show();
		Thread t = new Thread(DownloadSearchAppActivity.this);
		t.start();
	}

	@Override
	public void run() {
		toSearch = daCercare.getText().toString().trim();
		String nome = "";
		String desc = "";
		String img = "";
		id = "";
		double voto;
		Bitmap bmp = null;

		if(toSearch.equals("")){
			System.out.println("is empty");
			statoDialog = 1;
			handler.sendEmptyMessage(0);
		}
		else{

			query = "SELECT Nome_app, Descrizione, Voto, Screenshot, Autore FROM App JOIN Versione ON Id_app=Id_appv WHERE Nome_app LIKE " + "'" +toSearch + "%'";
			app = new ArrayList<App>();

			MyThread mythread = new MyThread();
			mythread.start();
			//start = SystemClock.currentThreadTimeMillis();
			//end = SystemClock.currentThreadTimeMillis();

			
			while(mythread.isAlive() && !connect.isError()){// && (end - start < 10000)){
				//end = System.currentTimeMillis();
			}

			/*if(end - start >= 10000){
				connect.setError(true);
				mythread.interrupt();
			}*/

			//connect.setError(false);
			
			if(connect.isError()){
				statoDialog = 2;
				handler.sendEmptyMessage(0);
			}
			else{
				try{
					//put data
					/*
					  JSONObject obja=new JSONObject();
					  obja.put("Nome_app","Take And Send");
					  obja.put("Voto",Integer.valueOf(5));
					  obja.put("Descrizione","This application takes an image and sends it by a mail");
					  obja.put("Screenshot","send48");
					  obja.put("Autore","mrisi");					
					
					jArray.put(obja);

					
					  obja=new JSONObject();
					  obja.put("Nome_app","Fixed Contact SMS");
					  obja.put("Voto",Integer.valueOf(2));
					  obja.put("Descrizione","It sends an SMS to a fixed contact");
					  obja.put("Screenshot","contact48");
					  obja.put("Autore","mrisi");					
										
					  jArray.put(obja);	
					 
					  obja=new JSONObject();
					  obja.put("Nome_app","Speeched SMS");
					  obja.put("Voto",Integer.valueOf(2));
					  obja.put("Descrizione","It sends an SMS to a contact with a speeched text");
					  obja.put("Screenshot","speech48");
					  obja.put("Autore","mrisi");					
					
					  jArray.put(obja);						  
					  */
					if(jArray.length() > 0){
						statoDialog = 0;
						for(int i=0; i<jArray.length(); i++){
							JSONObject obj = jArray.getJSONObject(i);
							nome = obj.getString("Nome_app");
							voto = obj.getInt("Voto");
							desc = obj.getString("Descrizione");
							img = obj.getString("Screenshot");
							id = obj.getString("Autore");

							
							String uri = "@drawable/" + img;
							int res = getResources().getIdentifier(uri, null, this.getPackageName());
							if (res != 0) {

								android.content.res.Resources r = this.getResources();
								bmp = BitmapFactory.decodeResource(r, res);
							}	
							else {
								byte [] b = Base64.decode(img, 0);
								ByteArrayInputStream ins = new ByteArrayInputStream(b);
								bmp = BitmapFactory.decodeStream(ins);	
							}
							
							app.add(new App(nome,desc,voto,bmp,id));
							
						}
						handler.sendEmptyMessage(0);
					}
					else{
						statoDialog = 3;
						handler.sendEmptyMessage(0);
					}

				}catch(Exception e){
					Utils.error("log_tag Error converting result prova2 " + e.toString());
				}
			}
		}

	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			progDailog.dismiss();

			if (statoDialog == 1) {
				CustomListViewAdapter adapter = new CustomListViewAdapter(DownloadSearchAppActivity.this, R.layout.rowitem, app);
				list.setAdapter(adapter);
				list.setOnItemClickListener(DownloadSearchAppActivity.this);

				AlertDialog.Builder builder1 = new AlertDialog.Builder(DownloadSearchAppActivity.this);
				builder1.setMessage("Empty search").setCancelable(false).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					}
				});
				AlertDialog alert1 = builder1.create();
				alert1.show();
				statoDialog=-1;
			}
			else if(statoDialog == 2){
				CustomListViewAdapter adapter = new CustomListViewAdapter(DownloadSearchAppActivity.this, R.layout.rowitem, app);
				list.setAdapter(adapter);
				list.setOnItemClickListener(DownloadSearchAppActivity.this);

				AlertDialog.Builder builder2 = new AlertDialog.Builder(DownloadSearchAppActivity.this);
				builder2.setMessage("Connection error").setCancelable(false).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						daCercare.setText("");
					}
				});
				AlertDialog alert2 = builder2.create();
				alert2.show();
				statoDialog=-1;
			}
			else if(statoDialog == 0){
				CustomListViewAdapter adapter = new CustomListViewAdapter(DownloadSearchAppActivity.this, R.layout.rowitem, app);
				list.setAdapter(adapter);
				list.setOnItemClickListener(DownloadSearchAppActivity.this);
				statoDialog=-1;
			}
			else if(statoDialog == 3){
				CustomListViewAdapter adapter = new CustomListViewAdapter(DownloadSearchAppActivity.this, R.layout.rowitem, app);
				list.setAdapter(adapter);
				list.setOnItemClickListener(DownloadSearchAppActivity.this);

				AlertDialog.Builder builder3 = new AlertDialog.Builder(DownloadSearchAppActivity.this);
				builder3.setMessage("App does not exist!").setCancelable(false).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						daCercare.setText("");
					}
				});
				AlertDialog alert3 = builder3.create();
				alert3.show();
				statoDialog=-1;
			}
		}
	};

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		Intent intent = new Intent(DownloadSearchAppActivity.this, DownloadAppActivity.class);
		intent.putExtra("id", app.get(arg2).getAutore());
		intent.putExtra("toSearch", app.get(arg2).getName());
		startActivity(intent);

	}

	public void onBackPressed(){
		finish();
		Intent intent = new Intent(this, ListFileActivity.class);
		startActivity(intent);
	}


	private class MyThread extends Thread{
		public void run(){
			jArray = connect.resultQuery(query);
		}

	}
}
