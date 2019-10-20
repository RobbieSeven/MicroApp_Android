package it.unisa.microapp.components.face;

import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.webservice.piece.WSSettings;

public class FaceDetectComponent extends MAComponent {

	public FaceDetectComponent(String id, String description) {
		super(id, description);
	}

	@Override
	protected String getLocationQName() {
		return "it.unisa.microapp.activities.FaceDetectActivity";
	}

	@Override
	protected String getCompType(String id) {
		return "FACE_DETECT";
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
