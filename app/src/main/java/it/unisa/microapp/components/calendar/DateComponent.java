package it.unisa.microapp.components.calendar;

import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.webservice.piece.WSSettings;

public class DateComponent extends MAComponent {

	public DateComponent(String id, String description) {
		super(id, description);
		
	}

	@Override
	protected String getLocationQName() {
		return "it.unisa.microapp.activities.DateActivity";
	}

	@Override
	protected String getCompType(String id) {
		return "CALENDAR_DATE";
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
