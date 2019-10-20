package it.unisa.microapp.components.admob;

import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.webservice.piece.WSSettings;


public class AdmobAdviewComponent extends MAComponent {


	public AdmobAdviewComponent(String id, String description) {
		super(id, description);
		
	}

	protected String getLocationQName() {
		return "it.unisa.microapp.activities.AdmobAdviewActivity";
	}

	protected String getCompType(String id) {
		return "ADMOB_ADVIEW";
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
