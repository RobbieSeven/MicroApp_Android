package it.unisa.microapp.components.image;

import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.webservice.piece.WSSettings;

public class ImageFlipComponent extends MAComponent {

	public ImageFlipComponent(String id, String description) {
		super(id, description);
	}

	@Override
	protected String getLocationQName() {

		return "it.unisa.microapp.activities.ImageFlipActivity";
	}

	@Override
	protected String getCompType(String id) {

		return "IMAGE_FLIP";
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
