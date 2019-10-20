package it.unisa.microapp.activities;

import it.unisa.microapp.R;
import it.unisa.microapp.data.LocationData;
import it.unisa.microapp.editor.ControlLocation;
import it.unisa.microapp.utils.Utils;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class LocationActivity extends MAActivity {
	private TextView tvLatitudine;
	private TextView tvLongitudine;
	private TextView tvVelocita;
	private TextView tvQuota;
	private ProgressBar progress;
	private TextView tvAccuratezza;
	private Location loc;
	private ControlLocation controlLocation;
	
	
	@Override
	protected void initialize(Bundle savedInstanceState) {
		

	}

	@Override
	protected void prepare() {
		
	}

	@Override
	protected int onVisible() {
		return R.layout.locationgps;
	}

	@Override
	protected View onVisibleView() {
		
		return null;
	}
	
	@Override
	protected void prepareView(View v) {
		progress = (ProgressBar) this.findViewById(R.id.progressBar1);
		progress.setVisibility(ProgressBar.VISIBLE);
		
		tvLatitudine = (TextView) this.findViewById(R.id.tvLatitudine);
		tvLongitudine = (TextView) this.findViewById(R.id.tvLongitudine);
		tvVelocita = (TextView) this.findViewById(R.id.tvVelocita);
		tvQuota = (TextView) this.findViewById(R.id.tvQuota);
		tvAccuratezza = (TextView) this.findViewById(R.id.tvAccuratezza);

		controlLocation = new ControlLocation(this, locationListener);

	}

	@Override
	protected void execute() {
		update();
	}
	
	private void update() {
		RadioButton rb1 = (RadioButton)this.findViewById(R.id.radioGps);
		rb1.setChecked(controlLocation.isGps());
		RadioButton rb2 = (RadioButton)this.findViewById(R.id.radioNetwork);
		rb2.setChecked(controlLocation.isNetwork());		
	}
	
	
	LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			loc = location;

			tvLatitudine.setText(Double.toString(Utils.getRound(loc.getLatitude(), 5)));
			tvLongitudine.setText(Double.toString(Utils.getRound(loc.getLongitude(), 5)));
			tvVelocita.setText(Double.toString(Utils.getRound(loc.getSpeed() * 3.6, 1)) + " km/h");
			tvQuota.setText(Integer.toString((int) loc.getAltitude()) + " m");
			tvAccuratezza.setText(Integer.toString((int) loc.getAccuracy()) + " m");
		
			if(progress != null)
				progress.setVisibility(ProgressBar.INVISIBLE);
		}

		@Override
		public void onProviderDisabled(String provider) {
			update();
			Toast.makeText(LocationActivity.this, "GPS Provider Disabled " + provider, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onProviderEnabled(String provider) {
			update();
			Toast.makeText(LocationActivity.this, "GPS Provider Enabled " + provider, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Toast.makeText(LocationActivity.this, "GPS Status Changed " + provider + " status: " + status,
					Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	public void pause() {
		if (controlLocation != null)
			controlLocation.pause();

	}

	@Override
	public void resume() {
		if (controlLocation != null)
			controlLocation.resume();
		
		if(progress != null)
			progress.setVisibility(ProgressBar.VISIBLE);		
		 //metodi per speech Vincenzo Savarese
	}

	protected String getPreviousLabel() {
		return getString(R.string.back);
	}

	protected String getNextLabel() {
		return getString(R.string.get);
	}

	protected Drawable getNextDrawable() {
		return getResources().getDrawable(R.drawable.ic_menu_get);
	}


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

}
