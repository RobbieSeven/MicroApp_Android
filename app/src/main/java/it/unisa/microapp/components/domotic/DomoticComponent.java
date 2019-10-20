package it.unisa.microapp.components.domotic;

import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.webservice.piece.WSSettings;

public class DomoticComponent extends MAComponent {
	private String dname;
	private String dstate;
	private String dlink;
	private String dtype;
	private String description;

	public DomoticComponent(String id, String name, String state,
			String link, String type, String description) {
		super(id, "");
		dname = name;
		dstate = state;
		dlink = link;
		dtype = type;
		
		this.description = description;
	}

	
	public String getDname() {
		return dname;
	}


	public void setDname(String dname) {
		this.dname = dname;
	}


	public String getDstate() {
		return dstate;
	}


	public void setDstate(String dstate) {
		this.dstate = dstate;
	}


	public String getDlink() {
		return dlink;
	}


	public void setDlink(String dlink) {
		this.dlink = dlink;
	}


	public String getDtype() {
		return dtype;
	}


	public void setDtype(String dtype) {
		this.dtype = dtype;
	}


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public WSSettings getSettings() {
		return null;
	}

	public void setSettings(WSSettings settings) {
	}


	@Override
	protected String getLocationQName() {
		return "it.unisa.microapp.activities.DomoticActivity";
	}

	@Override
	protected String getCompType(String id) {
		return "DOMOTIC";
	}

	@Override
	protected void updateSettings(boolean settingUpdate) {
	}	
	
	@Override
	public boolean isUpdate() {
		return false;
	}

}
