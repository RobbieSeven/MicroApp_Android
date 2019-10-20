package it.unisa.microapp.components.text;

import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.webservice.piece.WSSettings;

public class StaticTextComponent extends MAComponent 
{

	public StaticTextComponent(String id, String description) {
		super(id, description);
		
	}

	@Override
	protected String getLocationQName() 
	{
		return "it.unisa.microapp.activities.StaticTextActivity";
	}

	@Override
	protected String getCompType(String id) 
	{
		
		return "TEXT_STATIC";
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
