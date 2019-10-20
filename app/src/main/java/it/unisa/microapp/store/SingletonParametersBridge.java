package it.unisa.microapp.store;

import java.util.HashMap;


public class SingletonParametersBridge {
	
	private static SingletonParametersBridge instance = null;
	private HashMap<String, Object> map;

	public static SingletonParametersBridge getInstance() {
		if (instance == null)
			instance = new SingletonParametersBridge();
		return instance;
	}
	private SingletonParametersBridge() {
		map = new HashMap<String, Object>();
	}
	public void addParameter(String key, Object value) {
		map.put(key, value);
	}
	public Object getParameter(String key) {
		return map.get(key);
	}
	public void removeParameter(String key) {
		map.remove(key);
	}
}