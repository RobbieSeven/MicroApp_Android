package it.unisa.microapp.activities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.os.Bundle;
import android.view.View;

import it.unisa.microapp.data.StringData;
import it.unisa.microapp.utils.Constants;

public class CurrentDateActivity extends MAActivity {
	protected String date;

	@Override
	protected void initialize(Bundle savedInstanceState) {
		Calendar c = Calendar.getInstance();
		int mYear = c.get(Calendar.YEAR);
		int mMonth = c.get(Calendar.MONTH);
		int mDay = c.get(Calendar.DAY_OF_MONTH);
		
		SimpleDateFormat format = new SimpleDateFormat(Constants.dateFormat, Locale.getDefault());
		
		c.set(Calendar.YEAR, mYear);
		c.set(Calendar.MONTH, mMonth);
		c.set(Calendar.DAY_OF_MONTH, mDay);
		date = format.format(c.getTime());

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
	}
		
	@Override
	public void beforeNext() {
		StringData data = new StringData(this.mycomponent.getId(), date);
		this.application.putData(this.mycomponent, data);
	}
	
	@Override
	protected void resume(){
		
	}
}
