package it.unisa.microapp.components.calendar;

import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.webservice.piece.WSSettings;

public class CurrentDateComponent extends MAComponent 
{

	public CurrentDateComponent(String id, String description) {
		super(id, description);
		
	}
	
	@Override
	protected String getLocationQName() {
		return "it.unisa.microapp.activities.CurrentDateActivity";
	}

	@Override
	protected String getCompType(String id) 
	{
		return "CALENDAR_STATICDATE";
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
