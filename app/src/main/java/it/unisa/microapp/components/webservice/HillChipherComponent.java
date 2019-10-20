package it.unisa.microapp.components.webservice;

import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.webservice.piece.WSSettings;

public class HillChipherComponent extends MAComponent 
{
	private String wsdlURI;
	private String operationName;
	private String tns;
	private String uri;
	private String description;
	private boolean update;
	
	private WSSettings settings;
	
	public HillChipherComponent(String id,String ws,String operation,String namespace,String endpoint, WSSettings s) 
	{
		super(id, "");
		wsdlURI=ws;
		operationName=operation;
		tns=namespace;
		uri=endpoint;
		
		settings=new WSSettings(s.isDotNet(),s.isImplicitType(),s.getTimeout());
	}

	public WSSettings getSettings() {
		return settings;
	}

	public void setSettings(WSSettings settings) {
		this.settings = settings;
	}

	public HillChipherComponent(String id, String wsdl, String operation, String tns2, String uri2, WSSettings wsSettings, String description) 
	{
		super(id,"");
		this.description=description;
	}
	
	

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	protected String getLocationQName() 
	{
		return "it.unisa.microapp.activities.HillChipherActivity";
	}

	@Override
	protected String getCompType(String id) 
	{
		return "WEBSERVICE_HILL_CHIPHER";
	}
	
	
	public String getWsdlURI() {
		return wsdlURI;
	}

	public void setWsdlURI(String wsdlURI) {
		this.wsdlURI = wsdlURI;
	}

	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	public String getTns() {
		return tns;
	}

	public void setTns(String tns) {
		this.tns = tns;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
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
