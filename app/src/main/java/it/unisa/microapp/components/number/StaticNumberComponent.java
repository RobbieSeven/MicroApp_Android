package it.unisa.microapp.components.number;

import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.webservice.piece.WSSettings;

public class StaticNumberComponent extends MAComponent 
{
	public StaticNumberComponent(String id, String description) {
		super(id, description);
		
	}

	@Override
	protected String getLocationQName() 
	{
		return "it.unisa.microapp.activities.StaticNumberActivity";
	}

	@Override
	protected String getCompType(String id) 
	{
		return "NUMBER_STATIC";
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
