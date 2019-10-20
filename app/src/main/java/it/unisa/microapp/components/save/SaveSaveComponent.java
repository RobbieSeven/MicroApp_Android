package it.unisa.microapp.components.save;

import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.webservice.piece.WSSettings;

public class SaveSaveComponent extends MAComponent{

	public SaveSaveComponent(String id, String description) {
		super(id, description);
		
	}

	@Override
	protected String getLocationQName() {
		return "it.unisa.microapp.activities.SaveActivity";
	}

	@Override
	protected String getCompType(String id) {
		return "SAVE_SAVE";
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
