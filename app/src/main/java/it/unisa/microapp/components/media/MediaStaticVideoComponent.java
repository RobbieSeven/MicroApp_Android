package it.unisa.microapp.components.media;

import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.webservice.piece.WSSettings;


public class MediaStaticVideoComponent extends MAComponent {


	public MediaStaticVideoComponent(String id, String description) {
		super(id, description);
		
	}

	protected String getLocationQName() {
		return "it.unisa.microapp.activities.MediaStaticVideoActivity";
	}

	protected String getCompType(String id) {
		return "MEDIA_STATICVIDEO";
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
