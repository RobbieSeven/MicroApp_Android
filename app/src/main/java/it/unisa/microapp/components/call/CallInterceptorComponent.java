package it.unisa.microapp.components.call;

import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.webservice.piece.WSSettings;

public class CallInterceptorComponent extends MAComponent {

	public CallInterceptorComponent(String id, String description) {
		super(id, description);	
	}

	@Override
	protected String getLocationQName() {
		return "it.unisa.microapp.activities.CallInterceptActivity";
	}

	@Override
	protected String getCompType(String id) {
		return "CALL_INTERCEPTOR";
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
