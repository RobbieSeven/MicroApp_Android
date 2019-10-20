package it.unisa.microapp.store;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import it.unisa.microapp.R;
import it.unisa.microapp.support.FileManagement;
import it.unisa.microapp.utils.Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class DownloadAppActivity extends Activity implements OnClickListener,OnTouchListener, Runnable{

	private Button download;
	private EditText desc;
	private String id;
	private String toSearch;
	private TextView nick;
	private TextView nomeapp;
	private RatingBar voto;
	private ImageView screen;
	private byte [] b;
	private String xml;
	private DBvoti db;
	private ConnectDb connect = new ConnectDb();
	private JSONArray jArray;
	//private String id_device;
	private ProgressDialog progDailog;
	private int statoDialog = -1;
	private String nickname;
	private String Descrizione;
	private int voto1;
	private String Nome_app;
	private Bitmap bmp;
	private int n_download;
	private String idapp;


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.downloadapp);

		nick = (TextView)findViewById(R.id.twd_nick);
		nomeapp = (TextView)findViewById(R.id.twd_title1);
		voto = (RatingBar)findViewById(R.id.ratingBar1download);

		download = (Button) findViewById(R.id.download2);
		download.setOnClickListener(this);

		screen = (ImageView) findViewById(R.id.imageView_download);
		screen.setOnTouchListener(this);

		desc = (EditText)findViewById(R.id.d_descrizione);

		Intent myIntent = getIntent();
		id = myIntent.getStringExtra("id");
		toSearch = myIntent.getStringExtra("toSearch");

		loading();
		
		
		
	}

	public void loading(){
		progDailog = ProgressDialog.show(this, "Connessione in corso","attendere prego...");
		Thread t = new Thread(this);
		t.start();
	}

	@Override
	public void run() {
		
		try{
			String query = "SELECT Id_app, Nickname, Nome_app, Descrizione, XML, Voto, Screenshot, Autore, N_download FROM App JOIN Versione ON Id_app=Id_appv JOIN Utenti ON Id=Autore WHERE Nome_app LIKE " + "'" +toSearch + "'" + "AND Id LIKE " + "'" + id + "'";

			MyThread mythread = new MyThread(query);
			mythread.start();

			while(mythread.isAlive() && !connect.isError()){
			}

			if(connect.isError()){
				statoDialog = 2;
				handler.sendEmptyMessage(0);
			}
			else{

				statoDialog = 0;
				for (int i=0; i<jArray.length(); i++){
					JSONObject obj = jArray.getJSONObject(i);
					nickname = obj.getString("Nickname");
					Descrizione = obj.getString("Descrizione");
					voto1 = obj.getInt("Voto");
					xml = obj.getString("XML");
					Nome_app = obj.getString("Nome_app");
					n_download = Integer.parseInt(obj.getString("N_download"));
 					idapp = obj.getString("Id_app");
					b = Base64.decode(obj.getString("Screenshot"), 0);
					ByteArrayInputStream ins = new ByteArrayInputStream(b);
					bmp = BitmapFactory.decodeStream(ins);

				}
				handler.sendEmptyMessage(0);
			}
		}catch(Exception e){
			Utils.error("log_tag Error converting result prova3 " + e.toString());
			statoDialog = 1;
			handler.sendEmptyMessage(0);
		}

	}

	public void onClick(View v) {

		try {
			download();
		
		} catch (IOException e) {
			Utils.error(e);
		}

	}

	public boolean onTouch(View v, MotionEvent event) {

		Intent intent = new Intent(this, ZoomActivity.class);
		intent.putExtra("img", b);
		startActivity(intent);

		return false;
	}

	private void download() throws IOException{

		String path = FileManagement.getLocalAppPath();

		FileOutputStream fos = new FileOutputStream(new File(path + Nome_app + "_downloaded.xml"));
		OutputStreamWriter ous = new OutputStreamWriter(fos);

		ous.write(xml);
		ous.flush();
		ous.close();


		db = (DBvoti) SingletonParametersBridge.getInstance().getParameter("db");
		try{
			db.insertCommand(Nome_app + "_downloaded", 0);
		}catch(SQLiteConstraintException e){

		}

		connect.updateDownload(n_download, idapp);

		Cursor rs = db.getAllL();
		while(rs.moveToNext())
			System.out.println(rs.getString(0) + " " + rs.getString(1));

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Download completed").setCancelable(false).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				finish();
			}
		});
		
		
		AlertDialog alert = builder.create();
		alert.show();
	}
	

	private class MyThread extends Thread{
		private String query;

		public MyThread(String q){
			this.query = q;
		}
		public void run(){
				jArray = connect.resultQuery(query);
		}

	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			progDailog.dismiss();

			if(statoDialog == 2){

				AlertDialog.Builder builder2 = new AlertDialog.Builder(DownloadAppActivity.this);
				builder2.setMessage("Connection Error").setCancelable(false).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						finish();
					}
				});
				AlertDialog alert2 = builder2.create();
				alert2.show();
				statoDialog=-1;
			}
			else if(statoDialog == 1){
				AlertDialog.Builder builder1 = new AlertDialog.Builder(DownloadAppActivity.this);
				builder1.setMessage("Error loading data").setCancelable(false).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						finish();
					}
				});
				AlertDialog alert2 = builder1.create();
				alert2.show();
				statoDialog=-1;
			}else if(statoDialog == 0){
				nick.setText(nickname);
				nomeapp.setText(Nome_app);
				voto.setRating(voto1);
				desc.setText(Descrizione);
				screen.setImageBitmap(bmp);
				statoDialog=-1;
			}


		}
	};
}
