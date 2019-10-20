package it.unisa.microapp.activities;

import android.os.Bundle;
import android.view.View;
import it.unisa.microapp.data.StringData;

public class StaticDateActivity extends MAActivity {
	protected String date;
	
	@Override
	protected void initialize(Bundle savedInstanceState) {
		
	}	
	
	@Override
	public void initInputs() {
		date=this.mycomponent.getUserData("date").iterator().next();
		
	}
	
	@Override
	protected void prepare() {
	}	
		
	@Override
	public void beforeNext() {
		StringData data = new StringData(this.mycomponent.getId(), date);
		this.application.putData(this.mycomponent, data);
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
	protected void restart() {
		

	}

	@Override
	protected void resume() {
		
		//metodi per speech Vincenzo Savarese
	}

	@Override
	protected void pause() {
		

	}
	
	@Override
	protected void onCondition(boolean state) {
		

	}	

}
