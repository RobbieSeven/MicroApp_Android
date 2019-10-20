package it.unisa.microapp.editor;

import it.unisa.microapp.utils.Constants;
import it.unisa.microapp.utils.Utils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;


class InternalListener implements LocationListener {
	
	LocationListener external;
	ControlLocation control;
	
	public InternalListener(ControlLocation control, LocationListener external)
	{
		this.control = control;
		this.external = external;
	}
	
	@Override
	public void onLocationChanged(Location location) {
		if(external != null) external.onLocationChanged(location);
	}

	@Override
	public void onProviderDisabled(String provider) {
		if(control != null) control.update();		
		if(external != null) external.onProviderDisabled(provider);
	}

	@Override
	public void onProviderEnabled(String provider) {
		if(control != null) control.update();
		if(external != null) external.onProviderEnabled(provider);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		if(external != null) external.onStatusChanged(provider, status, extras);
	}
};

public class ControlLocation {

	LocationManager locationManager;
	boolean gps_enabled = false;
	boolean network_enabled = false;
	boolean first = true;
	int minTime = 1000;

	Activity context;

	InternalListener internal;

	public ControlLocation(Activity context, LocationListener listener) {
		
		internal = new InternalListener(this, listener);
		//this.listener = listener;
		this.context = context;

		// Otteniamo il riferimento al LocationManager
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		first = true;
		update();
		first = false;

	}

	public void setMinTime(int minimum)
	{
		if(minimum >= 0)
			this.minTime = minimum;
		
		update();
	}
	
	public void update()
	{
		gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		network_enabled = network_enabled && Utils.chkeckNetworkConnection(context.getApplication());

		if (!gps_enabled && !network_enabled) {
			if(first) 
				showGpsOptions();
			
			network_enabled = true;
			
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, 0, internal);
		} else {
			if (gps_enabled)
			{	
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, 0, internal);
				network_enabled = false;
			}			
			else if (network_enabled) {
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, 0, internal);
				gps_enabled = false;
			}
		}		
	}
	
	public boolean isGps() {
		return gps_enabled;
	}

	public boolean isNetwork() {
		return network_enabled;
	}

	private void showGpsOptions() {
		Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		context.startActivity(gpsOptionsIntent);
	}

	public void pause() {
		if (locationManager != null) {

			if (gps_enabled)
				locationManager.removeUpdates(internal);
			else if (network_enabled)
				locationManager.removeUpdates(internal);
		}

	}

	public void resume() {
		if (locationManager != null) {
			if (gps_enabled)
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, 0, internal);
			else if (network_enabled)
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, 0, internal);
		}
	}

	public Location getLocation() {
		Location loc = null;
		if (locationManager != null) {
			if (gps_enabled)
				loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			else if (network_enabled)
				loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}

		if (loc == null) {
			loc = new Location(LocationManager.GPS_PROVIDER);
			loc.setLatitude(Constants.unisaLocation.latitude);
			loc.setLongitude(Constants.unisaLocation.longitude);
		}
		return loc;
	}
	
	/*
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Intent intent = new Intent(Constants.ACTION_PROXIMITY_ALERT);
		intent.putExtra(Constants.INTENT_EXTRA_LOCATION, location); // custom payload
		PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);

		locationManager.addProximityAlert(location.getLatitude(),
		    location.getLongitude(), location.getRadius(), -1, pendingIntent)	
	
	 */

}
