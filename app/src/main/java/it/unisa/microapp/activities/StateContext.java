package it.unisa.microapp.activities;

public class StateContext {
	
	public StateContext() {}
	
	public int getMode(String state) {
		if(state.equals("hidden"))
			return 2;
		else if(state.equals("progress"))
			return 3;
		return 1; //visible
	}
}
