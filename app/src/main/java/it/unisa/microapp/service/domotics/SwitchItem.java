package it.unisa.microapp.service.domotics;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class SwitchItem implements Item {
	protected String type, name, state, link;
	
	public SwitchItem (String Type, String Name, String State, String Link){
		type=Type;
		name=Name;
		state=State;
		link=Link;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getStatus() {
		return state;
	}

	@Override
	public String getLink() {
		return link;
	}

	public void toggle(String IP) throws IOException {

		URL url = new URL(IP+"/CMD?"+this.getName()+"=TOGGLE");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		int code = connection.getResponseCode();
		
	}
	
}
