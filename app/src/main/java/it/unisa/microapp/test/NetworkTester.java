package it.unisa.microapp.test;

import java.util.ArrayList;

import android.app.Application;

import it.unisa.microapp.utils.Utils;

public class NetworkTester {

	ArrayList<String> arr = new ArrayList<String>();
	Application _app;
	
	public NetworkTester(Application app) {
		_app = app;
	}

	public void run()
 	{		
		arr.clear();
		boolean isNetwork=Utils.chkeckNetworkConnection(_app);
		if(!isNetwork)
			arr.add("Network is not available");
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
