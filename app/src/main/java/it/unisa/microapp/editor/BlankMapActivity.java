package it.unisa.microapp.editor;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import it.unisa.microapp.R;
import it.unisa.microapp.utils.Constants;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class BlankMapActivity extends Activity implements OnMapClickListener, OnMapLongClickListener {

	com.google.android.gms.maps.GoogleMap mMap;
	boolean isMapActive = false;
	Marker center;

	public void onCreate(Bundle save) {
		super.onCreate(save);

		setContentView(R.layout.blankmap);
		setUpMap();
	}

	private void setUpMap() {
		isMapActive = false;

		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

		if (resultCode == ConnectionResult.SUCCESS) {
			//Toast.makeText(getApplicationContext(), "isGooglePlayServicesAvailable SUCCESS", Toast.LENGTH_LONG).show();
		} else {
			GooglePlayServicesUtil.getErrorDialog(resultCode, this, Constants.RQS_GooglePlayServices);
		}

		if (mMap == null) {
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			if (mMap != null) {
				isMapActive = true;
				mMap.setMapType(com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID);

				mMap.addMarker(new MarkerOptions().position(Constants.unisaLocation).icon(BitmapDescriptorFactory.defaultMarker()));

				mMap.setOnMapClickListener(this);
				mMap.setOnMapLongClickListener(this);
			}
		}
	}


	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onMapClick(LatLng point) {
	}
			
		
	@Override
	public void onMapLongClick(LatLng point) {
		zoomCamera();
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

	public void zoomCamera() {
		// Move the camera instantly to hamburg with a zoom of 15.
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Constants.unisaLocation, 15));

		// Zoom in, animating the camera.
		mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
	}

}
