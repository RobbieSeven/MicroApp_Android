package it.unisa.microapp.components;

import it.unisa.microapp.webservice.piece.WSSettings;

public class MAFirstComponent extends MAComponent
{

	public MAFirstComponent(String id, String description) {
		super(id, description);
	}


	@Override
	protected String getLocationQName()
	{
		return "it.unisa.microapp.activities.First";
	}

	@Override
	protected String getCompType(String id)
	{
		return "FIRST";
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
