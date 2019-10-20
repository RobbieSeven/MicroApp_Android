package it.unisa.microapp.components.gps;

import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.webservice.piece.WSSettings;

public class GpsLocationComponent extends MAComponent {

	public GpsLocationComponent(String id, String description) {
		super(id, description);
	}

	@Override
	protected String getLocationQName() {
		
		return "it.unisa.microapp.activities.LocationActivity";
	}

	@Override
	protected String getCompType(String id) {
		
		return "GPS_LOCATION";
	}

	@Override
	public WSSettings getSettings() {
		return null;
	}

	@Override
	protected void setSettings(WSSettings settings) {
	}

	@Override
	protected void updateSettings(boolean settingUpdate) {
	}

	@Override
	public boolean isUpdate() {
		return false;
	}

}
