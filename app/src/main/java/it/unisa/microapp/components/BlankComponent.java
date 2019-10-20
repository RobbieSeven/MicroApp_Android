package it.unisa.microapp.components;

import it.unisa.microapp.webservice.piece.WSSettings;

public class BlankComponent extends MAComponent {

	// Solo per questo componente fittizio
	String _id;

	public BlankComponent(String id) {
		super(id, "");
		this._id = id;
	}

	@Override
	protected String getLocationQName() {
		return "it.unisa.microapp.activities.BlankActivity";
	}

	@Override
	protected String getCompType(String id) {
		if(this._id == null) return id;
		return this._id;
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
