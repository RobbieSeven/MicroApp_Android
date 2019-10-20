package it.unisa.microapp.store;


import it.unisa.microapp.utils.Utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;

public class ConnectDb{

	private String result;
	private InputStream is;
	private static String directory = "172.16.52.60/JavaBridge/MicroAppStore/";
	private boolean error = false;


	public ConnectDb(){
		result = "";
		is = null;
		error = false;
	}

	public JSONArray resultQuery(String query){


		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("query", query));

		JSONArray jArray = new JSONArray();

		try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httpost = new HttpPost("http://" + directory + "connect.php");
			httpost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httpost);
			HttpEntity entity = response.getEntity();

			is = entity.getContent();
		}catch(Exception e){
			Utils.error("log_tag Error in http connection "+e.toString());
			setError(true);
		}

		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,HTTP.UTF_8),8);
			StringBuilder sb = new StringBuilder();
			String line = null;

			while((line = reader.readLine()) != null){
				sb.append(line);
			}
			is.close();

			result = sb.toString();

			jArray = new JSONArray(result);

		}catch(Exception e){
			Utils.error("log_tag Error converting result prova1 " + e.toString());
		}
		return jArray;
	}

	public void insertQuery(ArrayList<NameValuePair> list){

		try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://" + directory + "insert.php");
			httppost.setEntity(new UrlEncodedFormEntity(list));
			HttpResponse response = httpclient.execute(httppost);
			Utils.debug("postData"+ response.getStatusLine().toString());
			Utils.debug("parameters"+ list.toString());

		}catch(Exception e){
			Utils.error("log_tag Insert error " + e.toString());
			setError(true);
		}
	}

	public void insertQuery2(ArrayList<NameValuePair> list){

		try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://" + directory + "insert2.php");
			httppost.setEntity(new UrlEncodedFormEntity(list));
			HttpResponse response = httpclient.execute(httppost);
			Utils.debug("parameters "+ list.toString());
			Utils.debug("postData"+ response.getStatusLine().toString());

		}catch(Exception e){
			Utils.error("log_tag Insert error " + e.toString());
			setError(true);
		}
	}

	public JSONArray resultSearchUpdateVoti(String nomeapp, int voto){
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("query", "SELECT Voto,N_voti FROM App WHERE Nome_app LIKE " + "'" + nomeapp + "'"));

		JSONArray jArray = new JSONArray();

		try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httpost = new HttpPost("http://" + directory + "updateVoti.php");
			httpost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httpost);
			HttpEntity entity = response.getEntity();

			is = entity.getContent();
		}catch(Exception e){
			Utils.error("log_tag Error in http connection "+e.toString());
			setError(true);
		}

		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,HTTP.UTF_8),8);
			StringBuilder sb = new StringBuilder();
			String line = null;

			while((line = reader.readLine()) != null){
				sb.append(line);
			}
			is.close();

			result = sb.toString();

			jArray = new JSONArray(result);

		}catch(Exception e){
			Utils.error("log_tag Error converting result prova5 " + e.toString());
			//setError(true);
		}
		return jArray;
	}

	public void updateVoti(double newvoto, int n_voti, String nomeapp){
		ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("voto", "" + newvoto));
		list.add(new BasicNameValuePair("n_voti", "" + n_voti));
		list.add(new BasicNameValuePair("nome", nomeapp));

		try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://" + directory + "insertUpdateVoti.php");
			httppost.setEntity(new UrlEncodedFormEntity(list));
			HttpResponse response = httpclient.execute(httppost);
			Utils.debug("parameters"+ list.toString());
			Utils.debug("postData"+ response.getStatusLine().toString());

		}catch(Exception e){
			Utils.error("log_tag Update error " + e.toString());
			//setError(true);
		}

	}

	public void updateDownload(int n_download, String id_app){
		MyThread t = new MyThread(n_download,id_app);
		
		t.start();

	}

	public class MyThread extends Thread{
		private int n_download;
		private String id_app;
		
		public MyThread(int n_download,String id_app){
			this.n_download = n_download;
			this.id_app = id_app;
		}
		
		public void run(){
			ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(new BasicNameValuePair("id_app", id_app));
			n_download++;
			list.add(new BasicNameValuePair("n_download", "" + n_download));
			try{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost("http://" + directory + "insertUpdateNDownload.php");
				httppost.setEntity(new UrlEncodedFormEntity(list));
				HttpResponse response = httpclient.execute(httppost);
				Utils.debug("parameters"+ list.toString());
				Utils.debug("postData"+ response.getStatusLine().toString());

			}catch(Exception e){
				Utils.error("log_tag Update error " + e.toString());
				//setError(true);
			}
		}
	}

	public void updateVersion(String version, String id_app, String xml, String desc){
		ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();

		list.add(new BasicNameValuePair("versione", version));
		list.add(new BasicNameValuePair("id_app", id_app));
		list.add(new BasicNameValuePair("xml", xml));
		list.add(new BasicNameValuePair("descrizione", desc));	

		try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://" + directory + "updateVersion.php");
			httppost.setEntity(new UrlEncodedFormEntity(list));
			HttpResponse response = httpclient.execute(httppost);
			Utils.debug("parameters"+ list.toString());
			Utils.debug("postData"+ response.getStatusLine().toString());

		}catch(Exception e){
			Utils.error("log_tag Update error " + e.toString());
			setError(true);
		}
	}

	public void updateNick(String id, String nickname){
		ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();

		list.add(new BasicNameValuePair("id", id));
		list.add(new BasicNameValuePair("new_nick", nickname));

		try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://" + directory + "updateNick.php");
			httppost.setEntity(new UrlEncodedFormEntity(list));
			HttpResponse response = httpclient.execute(httppost);
			Utils.debug("parameters"+ list.toString());
			Utils.debug("postData"+ response.getStatusLine().toString());

		}catch(Exception e){
			Utils.error("log_tag Update error " + e.toString());
			setError(true);
		}
	}

	public void insertUt(ArrayList<NameValuePair> list){

		try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://" + directory + "insertUtente.php");
			httppost.setEntity(new UrlEncodedFormEntity(list));
			HttpResponse response = httpclient.execute(httppost);
			Utils.debug("postData"+ response.getStatusLine().toString());
			Utils.debug("parameters"+ list.toString());

		}catch(Exception e){
			Utils.error("log_tag Insert error " + e.toString());
			setError(true);
		}
	}

	public boolean isError() {
		return error;
	}



	public void setError(boolean error) {
		this.error = error;
	}
}
