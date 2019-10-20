package it.unisa.microapp.service.domotics;

import it.unisa.microapp.editor.FunzioniActivity;
import it.unisa.microapp.support.FileManagement;
import it.unisa.microapp.utils.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class Server
{
	protected String ip;
	protected String port;
	protected HttpGet httpget;
	protected HttpClient client;
	protected Context cnx;
	protected String doc;
	Activity act;
	File prova;

	int i;
	HashMap<String, ArrayList<Item>> lista = new HashMap<String, ArrayList<Item>>();
	String filename;
	boolean _test = false;

	public Server(String IP, String Port, Context context, String file,
			boolean test) {
		ip = IP;
		port = Port;
		httpget = new HttpGet(ip + ":" + port + "/rest/items");
		client = new DefaultHttpClient();
		cnx = context;
		act = (Activity) context;
		i = 0;
		filename = file;
		_test = test;
	}

	public String call()
	{
		String response = null;
		doc = null;
		if (_test) {
			doc = "<items>"
					+ "<item><type>SwitchItem</type><name>Light_GF_Kitchen_Ceiling</name><state>OFF</state>"
					+ "<link>http://localhost:8080/rest/items/Light_GF_Kitchen_Ceiling</link></item>"
					+ "<item><type>SwitchItem</type><name>Light_GF_Kitchen_Table</name><state>OFF</state>"
					+ "<link>http://localhost:8080/rest/items/Light_GF_Kitchen_Table</link></item></items>";
		} else {
			try {
				HttpResponse risposta = client.execute(httpget);
				HttpEntity boh = risposta.getEntity();
				doc = EntityUtils.toString(boh);
				Log.i("Ricevuto:", doc);
			} catch (ClientProtocolException e) {
				response = e.getMessage();
			} catch (IOException e) {
				response = e.getMessage();
			}
		}

		return response;
	}


	public void result() {
		try {
			discovery(cnx);

			ArrayList<String> listp = new ArrayList<String>();
			for (ArrayList<Item> items : lista.values()) {
				for (Item current : items) {
					listp.add(current.getName());
				}
			}

			String[] rows = new String[listp.size()];
			for (int i = 0; i < listp.size(); i++) {
				rows[i] = listp.get(i);

				ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(act, it.unisa.microapp.R.layout.rowdomotic, rows);
				ListView mainListView = (ListView) act.findViewById(it.unisa.microapp.R.id.listViewDemo);
				mainListView.setAdapter(listAdapter);
				mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
								String inName = ((TextView) parent.findViewById(v.getId())).getText().toString();
								Log.e("ITEM", inName);
								
								Item selected = findItembyName(inName, lista);
								Toast t = Toast.makeText(cnx, inName,
										Toast.LENGTH_LONG);
								t.show();

								Intent intent = new Intent(cnx,
										FunzioniActivity.class);
								Bundle b = new Bundle();
								try {
									b.putString("Padre", "domotic:" + inName);
									b.putString("file", selected.getName()
											+ ";" + selected.getStatus() + ";"
											+ selected.getLink() + ";"
											+ selected.getType() + ";"
											+ getDomainName(ip));
								} catch (URISyntaxException e) {
									e.printStackTrace();
								}
								intent.putExtras(b);
								act.startActivityForResult(intent, 3);

							}
						});
			};

		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			act.setResult(15, data);
			act.finish();
		}
	}

	private void discovery(Context context) throws FileNotFoundException,
			SAXException, IOException, ParserConfigurationException {

		cnx = context;
		lista.put("SwitchItem", new ArrayList<Item>());
		lista.put("ColorItem", new ArrayList<Item>());
		lista.put("ContactItem", new ArrayList<Item>());
		lista.put("DateTimeItem", new ArrayList<Item>());
		lista.put("DimmerItem", new ArrayList<Item>());
		lista.put("GroupItem", new ArrayList<Item>());
		lista.put("NumberItem", new ArrayList<Item>());
		lista.put("RollershutterItem", new ArrayList<Item>());
		lista.put("StringItem", new ArrayList<Item>());

		DocumentBuilderFactory documentFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder builder = documentFactory.newDocumentBuilder();

		FileOutputStream fOut = null;

		File directory = new File(FileManagement.getDomoticArea());
		prova = new File(directory, Constants.DDiscRepository);
		fOut = new FileOutputStream(prova);

		OutputStreamWriter osw = new OutputStreamWriter(fOut);
		osw.write(doc);
		osw.close();

		Document document = builder.parse(prova);

		NodeList items = document.getElementsByTagName("item");
		System.out.println("Totale item: " + items.getLength());
		for (int i = 0; i < items.getLength(); i++) {
			Node nodo = items.item(i);
			if (nodo.getNodeType() == Node.ELEMENT_NODE) {
				Element item = (Element) nodo;
				String type = item.getElementsByTagName("type").item(0).getFirstChild().getNodeValue();
				String name = item.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();
				String state = item.getElementsByTagName("state").item(0).getFirstChild().getNodeValue();
				String link = item.getElementsByTagName("link").item(0).getFirstChild().getNodeValue();

				if (type.equals("SwitchItem")) {
					SwitchItem element = new SwitchItem(type, name, state, link);
					lista.get("SwitchItem").add(element);
				}
				/*
				else if (type.equals("ColorItem")) {
					SwitchItem element = new SwitchItem(type, name, state, link);
					lista.get("ColorItem").add(element);
				}
				else if (type.equals("ContactItem")) {
					SwitchItem element = new SwitchItem(type, name, state, link);
					lista.get("ContactItem").add(element);
				}
				else if (type.equals("DateTimeItem")) {
					SwitchItem element = new SwitchItem(type, name, state, link);
					lista.get("DateTimeItem").add(element);
				}
				else if (type.equals("DimmerItem")) {
					SwitchItem element = new SwitchItem(type, name, state, link);
					lista.get("DimmerItem").add(element);
				}
				else if (type.equals("GroupItem")) {
					SwitchItem element = new SwitchItem(type, name, state, link);
					lista.get("GroupItem").add(element);
				}
				else if (type.equals("NumberItem")) {
					SwitchItem element = new SwitchItem(type, name, state, link);
					lista.get("NumberItem").add(element);
				}*/
				else if (type.equals("RollershutterItem")) {
					SwitchItem element = new SwitchItem(type, name, state, link);
					lista.get("RollershutterItem").add(element);
				}/*
				else if (type.equals("StringItem")) {
					SwitchItem element = new SwitchItem(type, name, state, link);
					lista.get("StringItem").add(element);
				}*/

			}

		}

		/*
		 * for(ArrayList<Item> items2 : lista .values()) { for(Item current :
		 * items2){ Button item_button = new Button(cnx);
		 * item_button.setText(current.getName()); ll.addView(item_button); } }
		 */
		// return lista;

	}

	public HashMap<String, ArrayList<Item>> getItem() {
		return lista;
	}

	private Item findItembyName(String name,
			HashMap<String, ArrayList<Item>> lista) {
		for (ArrayList<Item> items : lista.values()) {
			for (Item current : items) {
				if (name.equals(current.getName()))
					return current;
			}
		}

		return null;
	};

	private static String getDomainName(String url) throws URISyntaxException {
		URI uri = new URI(url);
		String domain = uri.getHost();
		return domain.startsWith("http://www.") ? domain.substring(11) : domain;
	}

}
