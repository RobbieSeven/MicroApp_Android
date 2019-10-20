package it.unisa.microapp.editor;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import it.unisa.microapp.service.domotics.Server;
import it.unisa.microapp.utils.Utils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class DomoticActivity extends Activity implements Runnable {
	private Button button;
	private EditText EdTxt0, EdTxt1;
	String filename;
	private String test;
	private ProgressDialog pDialog;
	long start;
	long end;
	Server server;
	String error;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(it.unisa.microapp.R.layout.domodiscoverer);
		Bundle b = getIntent().getExtras();
		filename = b.getString("file");

		button = (Button)findViewById(it.unisa.microapp.R.id.button_search);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loading();
			}

		});
		
		EdTxt0 = (EditText) findViewById(it.unisa.microapp.R.id.ip);
		EdTxt1 = (EditText) findViewById(it.unisa.microapp.R.id.port);
		
		loadConfig();
	}
	
	public void loading() {		
		
		String ip = EdTxt0.getText().toString();
		String port = EdTxt1.getText().toString();
		
		if(ip.equals("") || port.equals("")) {
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
			
			alertDialog.setTitle("IP/Port Error");
			alertDialog.setMessage("Insert correct IP and port number");
			
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			};			
			
			alertDialog.setPositiveButton(android.R.string.ok, dialogClickListener);
			alertDialog.setCancelable(true);
			alertDialog.create().show();			
		} else {		
		
			pDialog = new ProgressDialog(this);
			pDialog.setMessage("Discovering services");
			pDialog.setCancelable(true);
			pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialoginterface) {
					pDialog.dismiss();
					
				}
			});
	
			pDialog.show();
			Thread thread = new Thread(this);
			thread.start();
		}
	}	
	
	
	public void run() {

		String ip = EdTxt0.getText().toString();
		String port = EdTxt1.getText().toString();		
		
		boolean is3g=Utils.chkeckNetworkConnection3g(DomoticActivity.this.getApplication());
		Utils.verbose("3g network:"+is3g);
		
		server = new Server(ip,port, this, filename, test.equals("true"));
		
		start = System.currentTimeMillis();
		error = server.call();
		end = System.currentTimeMillis();

		float diff = end - start;
		float sec = (diff / 1000) % 60;
		Utils.verbose("elapsed time:" + sec + " sec.");
			
		handler.sendEmptyMessage(0);
	}
	
	@SuppressLint("HandlerLeak")	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pDialog.dismiss();
			
			if (error == null) {
				if(server != null)
					server.result();
			} else {
				Utils.errorDialog(DomoticActivity.this, error);
			}			
			
			
		}
	};		
		
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			this.setResult(15, data);
			this.finish();
		}
	}
	
	private void loadConfig() {
		try {
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document doc = docBuilder.parse(this.getAssets().open("domotic_disco_conf.xml"));

			Element root = (Element) doc.getElementsByTagName("configuration").item(0);
			Element disco = (Element) root.getElementsByTagName("discoverer").item(0);

			EdTxt0.setText(disco.getElementsByTagName("disco_ip").item(0).getTextContent());
			EdTxt1.setText(disco.getElementsByTagName("disco_port").item(0).getTextContent());
			
			test = disco.getElementsByTagName("test").item(0).getTextContent();
			
			
		} catch (Exception e) {
			Utils.error("Error while reading the configuration file", e);
			Utils.errorDialog(this, "Error while reading the configuration file");
		}
	}
	
} 
