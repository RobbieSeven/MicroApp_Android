package it.unisa.microapp.components.contacts;

import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.webservice.piece.WSSettings;

public class ContactsSelectComponent extends MAComponent {

	public ContactsSelectComponent(String id, String description) {
		super(id, description);
		
	}

	@Override
	protected String getLocationQName() {
		return "it.unisa.microapp.activities.SelectContactActivity";
	}

	@Override
	protected String getCompType(String id) {
		return "CONTACTS_SELECT";
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
