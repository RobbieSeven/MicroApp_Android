package it.unisa.microapp.editor;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import it.unisa.microapp.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ConditionMapActivity extends Activity implements OnMapClickListener {

	com.google.android.gms.maps.GoogleMap mMap;
	boolean isMapActive = false;
	Circle myCircle;
	Marker center;
	Marker position;
	LatLng radius;
	float distance;
	
	final int RQS_GooglePlayServices = 1;

	private ControlLocation controlLocation;
	boolean gps_enabled = false;
	boolean network_enabled = false;

	Button bdone;
	Button bclean;
	Button bcenter;
	TextView emeters;

	public void onCreate(Bundle save) {
		super.onCreate(save);

		setContentView(R.layout.conditionmap);

		bdone = (Button) findViewById(R.id.done);
		bdone.setEnabled(false);
		bcenter = (Button) findViewById(R.id.center);
		bcenter.setEnabled(false);
		bclean = (Button) findViewById(R.id.clean);
		bclean.setEnabled(false);

		emeters = (TextView) findViewById(R.id.meters);
		emeters.setText("");

		setUpMap();

		center();
		controlLocation = new ControlLocation(this, locationListener);
	}

	LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {

			if (position != null)
				position.remove();

			LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
			position = mMap.addMarker(new MarkerOptions().position(newLatLng).icon(
					BitmapDescriptorFactory.defaultMarker()));

			bcenter.setEnabled(true);

		}

		@Override
		public void onProviderDisabled(String provider) {
			Toast.makeText(ConditionMapActivity.this, "GPS Provider Disabled " + provider, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onProviderEnabled(String provider) {
			Toast.makeText(ConditionMapActivity.this, "GPS Provider Enabled " + provider, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	private void center() {
		if (this.isMapActive) {
			if (controlLocation != null) {
				Location loc = controlLocation.getLocation();

				LatLng pos = new LatLng(loc.getLatitude(), loc.getLongitude());
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 18));
				mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
			}
		}
	}

	private void clean() {
		if (this.isMapActive) {
			if (myCircle != null) {
				myCircle.remove();
				myCircle = null;
				center.remove();
				center = null;
			}

			bclean.setEnabled(false);
			bdone.setEnabled(false);
			emeters.setText("");
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		if (controlLocation != null)
			controlLocation.pause();

	}

	@Override
	public void onResume() {
		super.onResume();

		if (controlLocation != null)
			controlLocation.resume();
	}

	private void setUpMap() {
		isMapActive = false;

		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

		if (resultCode != ConnectionResult.SUCCESS) {
			GooglePlayServicesUtil.getErrorDialog(resultCode, this, RQS_GooglePlayServices);
		}

		if (mMap == null) {
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			if (mMap != null) {
				isMapActive = true;
				mMap.setMapType(com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL);

				mMap.setOnMapClickListener(this);

			}
		}
	}

	@Override
	public void onMapClick(LatLng point) {
		if (myCircle != null) {
			myCircle.remove();
			myCircle = null;
			center.remove();
			center = null;
		}

		if (center == null) {
			BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.home);
			center = mMap.addMarker(new MarkerOptions().position(point).icon(bitmapDescriptor));
			bclean.setEnabled(false);
			bdone.setEnabled(false);
			emeters.setText("");
		} else {
			LatLng pos = center.getPosition();
			float[] results = new float[5];
			Location.distanceBetween(pos.latitude, pos.longitude, point.latitude, point.longitude, results);

			distance = results[0];

			CircleOptions circleOptions = new CircleOptions().center(pos).radius(distance).fillColor(0x40ff0000)
					.strokeColor(Color.BLUE).strokeWidth(5);

			radius = point;
			myCircle = mMap.addCircle(circleOptions);
			emeters.setText(distance + " meters");
			bclean.setEnabled(true);
			bdone.setEnabled(true);
		}

	}

	public void myClickHandler(View target) {
		int id = target.getId();
		if (id == R.id.center) {
			center();
		} else if (id == R.id.traffic) {
			if (this.isMapActive)
				mMap.setMapType(com.google.android.gms.maps.GoogleMap.MAP_TYPE_TERRAIN);
		} else if (id == R.id.normal) {
			if (this.isMapActive)
				mMap.setMapType(com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL);
		} else if (id == R.id.clean) {
			clean();
		} else if (id == R.id.done) {
			// recupera i dati
		    Intent data = new Intent();
			data.putExtra("center", center.getPosition());
			data.putExtra("limit", radius);
			data.putExtra("radius", Float.valueOf(distance));
			setResult(RESULT_OK, data);
			finish();
		}
	}

}
