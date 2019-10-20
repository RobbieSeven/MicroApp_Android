package it.unisa.microapp.activities;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import it.unisa.microapp.R;
import it.unisa.microapp.data.DataType;
import it.unisa.microapp.data.GenericData;
import it.unisa.microapp.data.ImageData;
import it.unisa.microapp.data.StringData;
import it.unisa.microapp.utils.Constants;
import it.unisa.microapp.utils.Utils;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

public class GeolocationActivity extends MAActivity implements OnMapClickListener {

	com.google.android.gms.maps.GoogleMap mMap;
	private Location loc;
	private Bitmap bm;
	boolean isMapActive = false;
	private String address;
	private View vMap;
	
	@Override
	protected void initialize(Bundle savedInstanceState) {
		

	}

	@Override
	protected void prepare() {
		

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
	protected void execute() {
		

	}	
	
	@Override
	protected void prepareView(View v) {
		vMap = (View)this.findViewById(R.id.map);
		setUpMap();
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

				LatLng newLatLng = new LatLng(loc.getLatitude(), loc.getLongitude());
				mMap.addMarker(new MarkerOptions().position(newLatLng).icon(BitmapDescriptorFactory.defaultMarker()));

				mMap.setOnMapClickListener(this);

				centerAndZoom(newLatLng);
			}
		}
	}

	private void centerAndZoom(LatLng center) {
		CameraUpdate cameracenter = CameraUpdateFactory.newLatLng(center);
		mMap.moveCamera(cameracenter);
		mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
	}


	@Override
	public void onMapClick(LatLng point) {
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

	private void getGeolocation() {
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

		Utils.verbose("location:" + address);
	}

	public void initInputs() {
		Iterator<GenericData<?>> i = application.getData(mycomponent.getId(), DataType.LOCATION).iterator();
		if (i.hasNext())
			loc = (Location) i.next().getSingleData();

		getGeolocation();
	}

	private Bitmap getBitmap() {

		// TODO: map fragment bitmap
		int w = vMap.getWidth();
		int h = vMap.getHeight();

		String url = Utils.getStaticMap(mMap, loc, w, h);
		Bitmap bitmap = Utils.getBitmapFromURL(url);

		if (bitmap == null) {
			View v1 = (View) this.findViewById(R.id.map);
			v1.setDrawingCacheEnabled(true);
			bitmap = Bitmap.createBitmap(v1.getDrawingCache());
			v1.setDrawingCacheEnabled(false);
		}
		return bitmap;
	}	
	
	@Override
	public void beforeNext() {
		
		bm = getBitmap();
		
		ImageData img = new ImageData(mycomponent.getId(), bm);
		application.putData(mycomponent, img);

		StringData ld = new StringData(mycomponent.getId(), address);
		application.putData(mycomponent, ld);

	}
	
	@Override
	protected void resume(){
		 //metodi per speech Vincenzo Savarese
	}
}
