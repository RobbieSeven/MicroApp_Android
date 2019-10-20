package it.unisa.microapp.test;

import java.util.ArrayList;

import android.app.Application;
import android.content.SharedPreferences;

import it.unisa.microapp.utils.Constants;

public class OptionsTester {

	ArrayList<String> arr = new ArrayList<String>();
	Application _app;
	
	public OptionsTester(Application app) {
		_app = app;
	}

	public void run()
 	{		
		arr.clear();
		
		SharedPreferences prefs = _app.getSharedPreferences(Constants.MY_PREFERENCES, android.content.Context.MODE_PRIVATE);
		String nick = prefs.getString(Constants.TEXT_NICKNAME_KEY, "");

		if(nick.equals(""))
			arr.add("Nickname in Options is mandatory");
 	}


	public String runTest() {
		String ret ="";
		run();
		
		for(String s : arr)
		{
			ret = ret + s + "\n";
		}
		
		return ret;
	}

}
