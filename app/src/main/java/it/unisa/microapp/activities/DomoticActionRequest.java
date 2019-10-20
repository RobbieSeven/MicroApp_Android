package it.unisa.microapp.activities;

import it.unisa.microapp.utils.Utils;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.app.Activity;
import android.os.AsyncTask;

public class DomoticActionRequest extends AsyncTask<String, String, String> {
	String name, state, link, type, response, statoattuale;
	protected HttpGet httpget;
	protected HttpClient client = new DefaultHttpClient();
	Activity _act;
	String ip;
	String port;
	String test;
	
	public String getStatoattuale(){
		if(statoattuale == null || statoattuale.equals("")){
			statoattuale="unknown";
		}		
		return statoattuale;
	}
	
	public DomoticActionRequest(Activity act, String dname, String dstate, String dlink,
			String dtype) {
		_act = act;
		name= dname;
		state= dstate;
		link= dlink;
		type= dtype;
		
		loadConfig();
	}

	@Override
	protected String doInBackground(String... params) {
			if(type.equals("SwitchItem")){
			try {
				httpget=new HttpGet(ip+":"+port+"/CMD?"+name+"=TOGGLE");
				HttpResponse risposta = client.execute(httpget);
				HttpEntity boh = risposta.getEntity();
				httpget=new HttpGet(ip+":"+port+"/rest/items/"+name+"/state");
				HttpResponse risp = client.execute(httpget);
				HttpEntity info = risp.getEntity();
				String stato = EntityUtils.toString(info);
				return stato;
				
			} catch (ClientProtocolException e) {
			} catch (IOException e) {
				e.printStackTrace();
			}}
			else if(type.equals("RollershutterItem")){
				try {
					httpget=new HttpGet(ip+":"+port+"/rest/items/"+name+"/state");
					HttpResponse risposta = client.execute(httpget);
					HttpEntity boh = risposta.getEntity();
					response = EntityUtils.toString(boh);
				} catch (ClientProtocolException e) {
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(response.equals("0")){
					try {
						httpget=new HttpGet(ip+":"+port+"/CMD?"+name+"=DOWN");
						HttpResponse risposta = client.execute(httpget);
						HttpEntity boh = risposta.getEntity();
					} catch (ClientProtocolException e) {
					} catch (IOException e) {
						e.printStackTrace();
					}
					return response;
				}
				else {
					try {
						httpget=new HttpGet(ip+":"+port+"/CMD?"+name+"=UP");
						HttpResponse risposta = client.execute(httpget);
						HttpEntity boh = risposta.getEntity();
					} catch (ClientProtocolException e) {
					} catch (IOException e) {
						e.printStackTrace();
					}
				return response;}
			}
			
				return null; 
	}

	protected void onPostExecute(String result){
		statoattuale = result;		
	}
	
	private void loadConfig() {
		try {
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document doc = docBuilder.parse(_act.getAssets().open("domotic_disco_conf.xml"));

			Element root = (Element) doc.getElementsByTagName("configuration").item(0);
			Element disco = (Element) root.getElementsByTagName("discoverer").item(0);

			ip = disco.getElementsByTagName("disco_ip").item(0).getTextContent();
			port = disco.getElementsByTagName("disco_port").item(0).getTextContent();
			
			test = disco.getElementsByTagName("test").item(0).getTextContent();
				
		} catch (Exception e) {
			Utils.error("Error while reading the configuration file", e);
			Utils.errorDialog(_act, "Error while reading the configuration file");
		}
	}
	
}
