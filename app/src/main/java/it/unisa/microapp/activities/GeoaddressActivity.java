package it.unisa.microapp.activities;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import it.unisa.microapp.data.DataType;
import it.unisa.microapp.data.GenericData;
import it.unisa.microapp.data.StringData;
import it.unisa.microapp.utils.Utils;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

public class GeoaddressActivity extends MAActivity {

	private Location loc;
	private String address;
	
	@Override
	protected void initialize(Bundle savedInstanceState) {
		

	}

	@Override
	protected void prepare() {
		getGeolocation();				
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
	
	private void getGeolocation()
	{
		address = "";
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		List<Address> addresses = null;
		try {
			addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
			Address add = addresses.get(0);
			address = Utils.addressStringFromAddress(add);
		} catch (IOException e) {
			Utils.errorDialog(this, e.getMessage());
		}
		
		Utils.verbose("location:"+address);
	}
	

	public void initInputs() 
	{
		Iterator<GenericData<?>> i = application.getData(mycomponent.getId(), DataType.LOCATION).iterator();
		if (i.hasNext())
			loc = (Location) i.next().getSingleData();
	}
	
	@Override
	public void beforeNext() {
		StringData ld = new StringData(mycomponent.getId(), address);
		application.putData(mycomponent, ld);	
	}
	
	@Override
	protected void resume(){
		 //metodi per speech Vincenzo Savarese
	}
}
