package it.unisa.microapp.activities;

import java.text.NumberFormat;
import java.text.ParsePosition;

import android.os.Bundle;
import android.view.View;

import it.unisa.microapp.data.StringData;

public class StaticNumberActivity extends MAActivity {
	private String txt;

	protected void initialize(Bundle savedInstanceState) {
		txt = "";
	}

	@Override
	protected void prepare() {
		

	}

	@Override
	protected int onVisible() {
		return 0;
	}

	@Override
	protected View onVisibleView() {
		
		return null;
	}

	@Override
	protected void prepareView(View v) {
		

	}	
	
	@Override
	protected void execute() {
		next();
	}	
	
	@Override
	public void initInputs() {
		if (this.mycomponent.getUserData("number") != null) {
			txt = this.mycomponent.getUserData("number").iterator().next();
		} 

		if(!check(txt))
		{
			txt="0";
		}
		
	}

	
	private boolean check(String s)
	{
		NumberFormat formatter = NumberFormat.getInstance();
		ParsePosition pos = new ParsePosition(0);
		formatter.parse(s, pos);
		return s.length() == pos.getIndex();		
	}	
	
	@Override
	public void beforeNext() {
		StringData data = new StringData(this.mycomponent.getId(), txt);
		this.application.putData(this.mycomponent, data);
	}

	@Override
	protected void resume(){
		//metodi per speech Vincenzo Savarese
	}
}
