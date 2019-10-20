package it.unisa.microapp.activities;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import it.unisa.microapp.R;
import it.unisa.microapp.data.LocationData;
import it.unisa.microapp.utils.Constants;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;

public class MapsLocationActivity extends MAActivity implements OnMapClickListener {

	com.google.android.gms.maps.GoogleMap mMap;
	private Location loc;
	boolean isMapActive = false;
	boolean isGet = false;
	Marker marker;

	@Override
	protected void initialize(Bundle savedInstanceState) {
		
		isGet = false;

	}

	@Override
	protected void prepare() {
		loc= new Location(LocationManager.PASSIVE_PROVIDER);
		loc.setLatitude(Constants.unisaLocation.latitude);
		loc.setLongitude(Constants.unisaLocation.longitude);

	}

	@Override
	protected int onVisible() {
		return R.layout.map;
	}

	@Override
	protected View onVisibleView() {
		
		return null;
	}

	@Override
	protected void prepareView(View v) {
		
		setUpMap();
	}	
	
	@Override
	protected void execute() {
		

	}	

	private void setUpMap() {
		isMapActive = false;

		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

		if (resultCode != ConnectionResult.SUCCESS) {
			GooglePlayServicesUtil.getErrorDialog(resultCode, this, Constants.RQS_GooglePlayServices);
		}

		if (mMap == null) {
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			if (mMap != null) {
				isMapActive = true;
				mMap.setMapType(com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL);

				mMap.setOnMapClickListener(this);	
				
				mMap.addMarker(new MarkerOptions().position(Constants.unisaLocation).icon(BitmapDescriptorFactory.defaultMarker()));
				centerAndZoom(Constants.unisaLocation);
			}
		}
	}

	private void centerAndZoom(LatLng center) {
		CameraUpdate cameracenter = CameraUpdateFactory.newLatLng(center);
		mMap.moveCamera(cameracenter);
		mMap.animateCamera(CameraUpdateFactory.zoomTo(9), 2000, null);
	}
	
	@Override
	public void onMapClick(LatLng point) {
		
		if(marker != null) {
			marker.remove();
		}
		marker = mMap.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));		
		
		loc.setLatitude(point.latitude);
		loc.setLongitude(point.longitude);
		isGet = true;
		
		inflateButtons();
	}
	
	@Override
	protected boolean enableNext() {
		return isGet;
	}
	
	@Override
	protected String getNextLabel() {
		return getString(R.string.get);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void myClickHandler(View target) {
		int id = target.getId();
		if (id == R.id.sat) {
			mMap.setMapType(com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE);
		} else if (id == R.id.traffic) {
			mMap.setMapType(com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID);
		} else if (id == R.id.normal) {
			mMap.setMapType(com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL);
		}
	}

	@Override
	public void initInputs() {
	}


	@Override
	public void beforeNext() {
		LocationData ld = new LocationData(mycomponent.getId(), loc);
		application.putData(mycomponent, ld);		
	}

	@Override
	protected void resume(){
		 //metodi per speech Vincenzo Savarese
	}

}
