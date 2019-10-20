package it.unisa.microapp.components.information;

import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.webservice.piece.WSSettings;

public class ScaffoldingComponent extends MAComponent 
{	
	public ScaffoldingComponent(String id, String description, String sc_name, String cmp_t) {
		super(id, description,sc_name, cmp_t);
	}
	
	@Override
	protected String getLocationQName() { 
		return "it.unisa.microapp.activities."+scaffolding_name;
	}
	
	public String getProvaName() {
		return getLocationQName();
	}

	@Override
	protected String getCompType(String id) {
		return comp_type;
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
