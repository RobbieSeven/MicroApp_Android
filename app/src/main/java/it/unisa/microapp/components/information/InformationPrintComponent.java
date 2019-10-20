package it.unisa.microapp.components.information;

import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.webservice.piece.WSSettings;

public class InformationPrintComponent extends MAComponent {

	public InformationPrintComponent(String id, String description) {
		super(id, description);
		
	}

	@Override
	protected String getLocationQName() 
	{
		return "it.unisa.microapp.activities.InformationPrintActivity";
	}

	@Override
	protected String getCompType(String id) 
	{
		return "INFORMATION_PRINT";
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
