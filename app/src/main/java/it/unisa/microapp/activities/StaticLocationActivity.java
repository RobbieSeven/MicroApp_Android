package it.unisa.microapp.activities;

import it.unisa.microapp.data.LocationData;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;

public class StaticLocationActivity extends MAActivity {
	private String sloc;
	private Location loc;

	protected void initialize(Bundle savedInstanceState) {
		

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
		sloc=this.mycomponent.getUserData("location").iterator().next();
	}

	@Override
	public void beforeNext() {

		loc = new Location(LocationManager.GPS_PROVIDER);
	
		if (sloc == null)
		{
			loc.setLongitude(40.557154);
			loc.setLatitude(14.220294);				
		}
		else
		{
			String[] strs = sloc.split(",");
			loc.setLatitude(Double.parseDouble(strs[0]));						
			loc.setLongitude(Double.parseDouble(strs[1]));
		}
		
		LocationData ld = new LocationData(mycomponent.getId(), loc);
		application.putData(mycomponent, ld);

	}
	
	@Override
	protected void resume(){
		//metodi per speech Vincenzo Savarese
	}
}
