package it.unisa.microapp.activities;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import it.unisa.microapp.R;
import it.unisa.microapp.editor.FunzioniActivity;
import it.unisa.microapp.utils.Utils;
import it.unisa.microapp.webservice.entry.MAEntry;
import it.unisa.microapp.webservice.piece.WebService;
import it.unisa.microapp.webservice.rpc.KSoapRequest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class DiscovererActivity extends Activity implements Runnable {
	private LinearLayout linear;
	private KSoapRequest client;
	private String discovererLoc;
	private String discovererTns;
	private String discovererOperation;
	private String test;
	private View retview;
	private ListView l;
	private String filename;

	private ProgressDialog pDialog;
	SoapObject retSoap;
	long start;
	long end;
	int r;
	Button button;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.discoverer);

		Bundle b = getIntent().getExtras();
		filename = b.getString("file");

		linear = (LinearLayout) this.findViewById(R.id.discolin);

		LayoutInflater inflater = this.getLayoutInflater();
		retview = inflater.inflate(R.layout.webservicelist, null);

		loadConfig();

		linear.addView(retview);

		l = new ListView(this);

		l.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
				TextView name = (TextView) v.findViewById(R.id.ws_name);

				String url = (String) name.getTag();

				Toast t = Toast.makeText(DiscovererActivity.this, url, Toast.LENGTH_LONG);
				t.show();

				Intent intent = new Intent(DiscovererActivity.this, FunzioniActivity.class);
				Bundle b = new Bundle();
				b.putString("Padre", "wsdl:" + url);
				b.putString("file", filename);
				intent.putExtras(b);
				DiscovererActivity.this.startActivityForResult(intent, 3);

			}

		});
		linear.addView(l);

		button = (Button) this.findViewById(R.id.discobutt);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loading();
			}

		});
	}


	public void loading() {		
		pDialog = new ProgressDialog(this);
		//pDialog.setTitle("Discovering services"+ +getString(R.string.pleaseWait));
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

	public void run() {

		final EditText ed = (EditText) DiscovererActivity.this.findViewById(R.id.discosrc);

		@SuppressWarnings("unchecked")
		MAEntry<String, Object>[] request = new MAEntry[3];

		request[0] = new MAEntry<String, Object>("query", ed.getText().toString());

		Spinner sp = (Spinner) linear.findViewWithTag("spinner");

		request[1] = new MAEntry<String, Object>("UDDIRegistryURL", sp.getSelectedItem().toString());
		request[2] = new MAEntry<String, Object>("test", test);

		Utils.verbose(Arrays.toString(request));

		boolean is3g=Utils.chkeckNetworkConnection3g(DiscovererActivity.this.getApplication());

		Utils.verbose("3g network:"+is3g);

		client = new KSoapRequest("", discovererOperation, discovererTns, discovererLoc, request,is3g);
		client.setDotNet(false);

		start = System.currentTimeMillis();

		Utils.verbose(client.toString());
		
		r = client.sendRequest();
		end = System.currentTimeMillis();

		handler.sendEmptyMessage(0);
	}

	@SuppressLint("HandlerLeak")	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pDialog.dismiss();

			if (r == 0) {
				retSoap = (SoapObject) client.getResponse();
				Utils.verbose(retSoap.toString());
				showResponse(retSoap, start, end);
			} else {
				String s = (String) client.getResponse();
				Utils.errorDialog(DiscovererActivity.this, s);
			}

			//button.setEnabled(true);    		
		}
	};	

	protected void showResponse(SoapObject ret, long start, long end) {
		TextView numofWs = (TextView) retview.findViewById(R.id.wslist_tot);
		TextView time = (TextView) retview.findViewById(R.id.wslist_time);
		List<WebService> wsList = new LinkedList<WebService>();
		extractValues(wsList, ret);
		Utils.verbose(Arrays.toString(wsList.toArray()));

		numofWs.setText("# of web service(s):" + wsList.size());

		float diff = end - start;
		float sec = (diff / 1000) % 60;
		time.setText("elapsed time:" + sec + " sec.");
		l.setAdapter(new WebServiceAdapter(this, android.R.layout.simple_list_item_1, wsList));
		linear.invalidate();
	}

	private void extractValues(List<WebService> wsList, SoapObject ret) {
		for (int i = 0; i < ret.getPropertyCount(); i++) {
			SoapObject obj = (SoapObject) ret.getProperty(i);
			WebService ws = new WebService();
			for (int j = 0; j < obj.getPropertyCount(); j++) {
				PropertyInfo info = new PropertyInfo();
				obj.getPropertyInfo(j, info);

				if (info.getName().equals("name"))
					ws.setName(info.getValue().toString());
				else if (info.getName().equals("description"))
					ws.setDescription(info.getValue().toString());
				else if (info.getName().equals("wsdlURL"))
					ws.setWsdl(info.getValue().toString());
				else if (info.getName().equals("rating"))
					ws.setRating(Double.parseDouble(info.getValue().toString()));
			}

			wsList.add(ws);
		}
	}


	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			setResult(1, data);
			finish();
		}
	}

	private void loadConfig() {
		try {
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document doc = docBuilder.parse(this.getAssets().open("disco_conf.xml"));

			Element root = (Element) doc.getElementsByTagName("configuration").item(0);

			Element disco = (Element) root.getElementsByTagName("discoverer").item(0);

			discovererLoc = disco.getElementsByTagName("disco_ip").item(0).getTextContent();
			discovererTns = disco.getElementsByTagName("disco_namespace").item(0).getTextContent();
			discovererOperation = disco.getElementsByTagName("disco_operation").item(0).getTextContent();
			test = disco.getElementsByTagName("test").item(0).getTextContent();

			Element uddi = (Element) root.getElementsByTagName("uddi_nodes").item(0);

			NodeList l = uddi.getElementsByTagName("uddi_url");

			List<String> list = new LinkedList<String>();

			for (int i = 0; i < l.getLength(); i++) {
				Element e = (Element) l.item(i);
				list.add(e.getTextContent());
			}

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);

			Spinner spinner = new Spinner(this);
			spinner.setAdapter(adapter);
			spinner.setTag("spinner");

			TextView txt = new TextView(this);
			txt.setText("Server");
			linear.addView(txt);
			linear.addView(spinner);
		} catch (Exception e) {
			Utils.error("Error while reading the configuration file", e);
			Utils.errorDialog(this, "Error while reading the configuration file");
		}
	}

	private class WebServiceAdapter extends ArrayAdapter<WebService> {
		public WebServiceAdapter(Context context, int textViewResourceId, List<WebService> objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}

		private View getCustomView(int position, View convertView, ViewGroup parent) {
			WebService ws = this.getItem(position);

			LayoutInflater inflater = getLayoutInflater();

			View row = inflater.inflate(R.layout.sa, null);

			TextView name = (TextView) row.findViewById(R.id.ws_name);
			TextView desc = (TextView) row.findViewById(R.id.ws_description);
			RatingBar rating = (RatingBar) row.findViewById(R.id.ws_rating);

			name.setText(ws.getName());
			name.setTag(ws.getWsdl());
			desc.setText(ws.getDescription());
			float value = (float) ws.getRating() * 5;

			rating.setRating(value);

			return row;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}

	}
}
