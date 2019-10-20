package it.unisa.microapp.activities;

import it.unisa.microapp.data.LocationData;
import it.unisa.microapp.editor.ControlLocation;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;

public class GpsLocationInterceptActivity extends MAActivity {

	private Location loc;
	private ControlLocation controlLocation;
	
	@Override
	protected void initialize(Bundle savedInstanceState) {
		controlLocation = new ControlLocation(this, locationListener);
		controlLocation.setMinTime(0);
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
		

	}	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	
	}

	LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			loc = location;
			
			controlLocation.pause();
			next();
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};


	@Override
	public void initInputs() {
	}

	@Override
	public void beforeNext() {
		
		if (controlLocation != null) {
			controlLocation.pause();
			loc = controlLocation.getLocation();
		}

		LocationData ld = new LocationData(mycomponent.getId(), loc);
		application.putData(mycomponent, ld);		
	}
	
	protected void resume(){
		 //metodi per speech Vincenzo Savarese
	}
}
