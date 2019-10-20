package it.unisa.microapp.components.camera;

import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.webservice.piece.WSSettings;

public class CameraPreviewComponent extends MAComponent {

	public CameraPreviewComponent(String id, String description) {
		super(id, description);
		
	}


	@Override
	protected String getLocationQName() {
		return "it.unisa.microapp.activities.PreviewActivity";
	}

	@Override
	protected String getCompType(String id) {
		return "CAMERA_PREVIEW";
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
