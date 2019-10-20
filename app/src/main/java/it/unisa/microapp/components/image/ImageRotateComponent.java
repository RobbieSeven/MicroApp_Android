package it.unisa.microapp.components.image;

import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.webservice.piece.WSSettings;

public class ImageRotateComponent extends MAComponent 
{

	public ImageRotateComponent(String id, String description) {
		super(id, description);
		
	}

	@Override
	protected String getLocationQName() 
	{

		return "it.unisa.microapp.activities.ImageRotateActivity";
	}

	@Override
	protected String getCompType(String id) {

		return "IMAGE_ROTATE";
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
