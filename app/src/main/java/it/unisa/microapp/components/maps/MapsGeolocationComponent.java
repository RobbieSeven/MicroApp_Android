package it.unisa.microapp.components.maps;

import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.webservice.piece.WSSettings;

public class MapsGeolocationComponent extends MAComponent{

	public MapsGeolocationComponent(String id, String description) {
		super(id, description);
		
	}


	@Override
	protected String getLocationQName() {
		return "it.unisa.microapp.activities.GeolocationActivity";
	}

	@Override
	protected String getCompType(String id) {
		return "MAPS_GEOLOCATION";
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
