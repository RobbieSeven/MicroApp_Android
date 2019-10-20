package it.unisa.microapp.library;

import it.unisa.microapp.webservice.piece.WSContext;

import java.util.Arrays;


public class WSLibraryComponent extends LibraryComponent 
{
	private static final long serialVersionUID = -865835905042081469L;
	private String wsdl;
	private String operation;
	private String service;
	private String port;
	private String tns;
	private String uri;
	private WSContext context;
	
	
	public WSLibraryComponent(String idlog, String n, String cat, String icon) {
		super(idlog, n, cat, icon);
		
	}
	
	public String getWsdl() {
		return wsdl;
	}

	public void setWsdl(String wsdl) {
		this.wsdl = wsdl;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
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
	
	
	
	public WSContext getContext() {
		return context;
	}

	public void setContext(WSContext context) {
		this.context = context;
	}

	public String toString()
	{
		return "WSLibraryComponent [name=" + this.getName() + ", category=" + this.getCategory() +
				" ,wsdl="+wsdl+
				" ,operation="+operation +
				" ,service="+service
				+" ,port="+port 
				+" ,tns="+tns 
				+" ,uri="+uri 
				+",context="+context+
				", inputs=" + Arrays.toString(inputs) + ", outputs="
				+ Arrays.toString(outputs) + ", userInputs=" + Arrays.toString(userInputs) + "]";
	}

}
