package it.unisa.microapp.components.contacts;

import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.webservice.piece.WSSettings;

public class ContactsPreviewComponent extends MAComponent {

	public ContactsPreviewComponent(String id, String description) {
		super(id, description);
		
	}

	@Override
	protected String getLocationQName() {
		return "it.unisa.microapp.activities.PreviewContactActivity";
	}

	@Override
	protected String getCompType(String id) {
		return "CONTACTS_PREVIEW";
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
