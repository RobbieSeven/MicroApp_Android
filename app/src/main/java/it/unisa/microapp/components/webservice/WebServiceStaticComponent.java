package it.unisa.microapp.components.webservice;

import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.webservice.piece.WSSettings;


public class WebServiceStaticComponent extends MAComponent 
{
	private WSSettings settings;
	private String description;
	private boolean update;

	public WebServiceStaticComponent(String id, WSSettings s, String description) 
	{
		super(id,"");
		settings=new WSSettings(s.isDotNet(),s.isImplicitType(),s.getTimeout());
		this.description=description;
	}
	
	

	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}



	public WSSettings getSettings() {
		return settings;
	}

	public void setSettings(WSSettings settings) {
		this.settings = settings;
	}

	@Override
	protected String getLocationQName() {
		return "it.unisa.microapp.activities.WebServiceStaticActivity";
	}

	@Override
	protected String getCompType(String id) 
	{
		return "WEBSERVICE_STATIC";
	}

	public void setUpdate(boolean update) 
	{
		this.update=update;
	}

	@Override
	protected void updateSettings(boolean settingUpdate) {
	}

	@Override
	public boolean isUpdate() 
	{
		return update;
	}

}
