package it.unisa.microapp.store;

import it.unisa.microapp.R;
import it.unisa.microapp.utils.Utils;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class CustomListViewDownlodedApp extends ArrayAdapter<Downloaded> {

	private LayoutInflater inflater;
	private int resource;
	private String value;
	private String s;
	private int votoins;
	private ConnectDb conn = new ConnectDb();
	private JSONArray jArray;
	private boolean enable;
	private Context context;
	private ProgressDialog progDailog;
	private EditText input;
	private Button b;
	private int error = -1;

	public CustomListViewDownlodedApp(Context cont, int resourceId, List<Downloaded> items){
		super(cont,resourceId,items);
		resource = resourceId;
		inflater = LayoutInflater.from(cont);
		context=cont;
	}

	@SuppressWarnings("static-access")
	public View getView(int position, View convertView, ViewGroup parent){

		convertView = (RelativeLayout)inflater.inflate(resource, null);
		Downloaded down = getItem(position);
		TextView nome = (TextView)convertView.findViewById(R.id.txtnomeapp);
		Button voto = (Button)convertView.findViewById(R.id.btnvota);

		if(down.getVotato() == 1)
			voto.setEnabled(false);
		if(down.isMyapp())
			voto.setVisibility(convertView.INVISIBLE);

		String prova = down.getNomeapp();
		voto.setPrivateImeOptions(prova);

		nome.setText(prova);
		voto.setOnClickListener(new OnClickListener() {


			

			public void onClick(View v) {

				enable = false;
				String l = "" + ((Button)v).getPrivateImeOptions();
				setProva(l);
				MyAlertDialog alert = new MyAlertDialog(getContext());
				alert.setTitle("Vota");
				alert.setMessage("Dai un voto da 1 a 5");
				input = new EditText(getContext());
				alert.setView(input);
				alert.setB((Button)v);
				b = alert.getButton();
				alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						loading();
					}

				});

		//		if(!enable)
		//			b.setEnabled(false);

				alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						b.setEnabled(true);
					}
				});
				alert.show();
			}
		});

		return convertView;

	}

	private void launchToast(String s){
		Toast toast = Toast.makeText(getContext(), s, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP, 40, 40);
		toast.show();
	}

	private void setProva(String s){
		this.s = s;
	}

	private String getProva(){
		return s;
	}

	private boolean updateVoti(String nomeapp, int voto){


		double old_voto = 0;
		int n_voti = 0;
		double new_voto = 0;

		try{
			MyThreadSearchVoti mythread = new MyThreadSearchVoti(nomeapp, voto);
			mythread.start();
			while(mythread.isAlive() && !conn.isError()){
			}
			if(conn.isError()){
				error = 1;
				handler.sendEmptyMessage(0);
				return true;
			}

			for(int i=0; i<jArray.length(); i++){
				JSONObject obj = jArray.getJSONObject(i);
				old_voto = obj.getDouble("Voto");
				n_voti = obj.getInt("N_voti");

			}
		}catch(Exception e){
			Utils.error("log_tag Error converting result prova5 " + e.toString());
		}

		new_voto = ((old_voto * n_voti) + voto)/(n_voti + 1);

		MyThreadUpdateVoto thread = new MyThreadUpdateVoto(new_voto, n_voti + 1, nomeapp);
		thread.start();
		while(thread.isAlive() && !conn.isError()){
		}
		if(conn.isError()){
			error = 1;
			handler.sendEmptyMessage(0);
			return true;
		}
		return false;
	}

	private class MyThreadUpdateVoto extends Thread{
		private String nome_app;
		private int n_voti;
		private double newvoto;

		public MyThreadUpdateVoto(double new_voto, int n, String nomeapp){
			this.nome_app = nomeapp;
			this.newvoto = new_voto;
			this.n_voti = n;
		}
		public void run(){
			conn.updateVoti(newvoto, n_voti, nome_app);
		}
	}

	private class MyThreadSearchVoti extends Thread{
		private String nome_app;
		private int voto;

		public MyThreadSearchVoti(String nome, int v){
			this.nome_app = nome;
			this.voto = v;
		}
		public void run(){
			jArray = conn.resultSearchUpdateVoti(nome_app, voto);
		}
	}

	public void loading(){
		
		progDailog = new ProgressDialog(context);
		progDailog.setTitle("Voting");
		progDailog.setMessage(context.getString(R.string.pleaseWait));
		progDailog.setCancelable(false);
		progDailog.show();
		
		//progDailog = ProgressDialog.show(context, "Connessione in corso","attendere prego...");
		new Thread(){
			public void run(){
				value = input.getText().toString();
				try{
					votoins = Integer.parseInt(value);
					if(votoins > 5 || votoins < 1){
						launchToast("Insert a value in the range [1,5]");
					}
				}catch(Exception e){
					launchToast("Insert a value");
				}
				DBvoti db = (DBvoti) SingletonParametersBridge.getInstance().getParameter("db");

				String p = getProva();
				String [] stringa = p.split("_downloaded.xml");


				enable = updateVoti(stringa[0], votoins);
				if(!enable){
					db.update(stringa[0] + "_downloaded", 1);
					progDailog.dismiss();
					error = 0;
					handler.sendEmptyMessage(0);
				}
			}
		}.start();
	}

	@SuppressLint("HandlerLeak")	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			progDailog.dismiss();

			if(error ==1){
			AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
			builder2.setMessage("Connection error").setCancelable(false).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					b.setEnabled(true);
				}
			});
			AlertDialog alert2 = builder2.create();
			alert2.show();
			error = -1;
			}else if(error == 0){
				b.setEnabled(false);
				error = -1;
			}
		}
	};
}