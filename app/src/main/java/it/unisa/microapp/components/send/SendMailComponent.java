package it.unisa.microapp.components.send;

import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.webservice.piece.WSSettings;

public class SendMailComponent extends MAComponent {

	public SendMailComponent(String id, String description) {
		super(id, description);
		
	}
	
	@Override
	protected String getLocationQName() {
		return "it.unisa.microapp.activities.SendMailActivity";
	}

	@Override
	protected String getCompType(String id) {
		return "SEND_MAIL";
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
