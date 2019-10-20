package it.unisa.microapp.webservice.piece;

import android.content.res.Resources;
import it.unisa.microapp.editor.Grid;
import it.unisa.microapp.editor.IconPiece;
import it.unisa.microapp.utils.Constants;

public class WSPiece extends IconPiece {
	private static final long serialVersionUID = 7001346415006521573L;
	private String wsdl;
	private String operation;
	private String service;
	private String port;
	private String tns;
	private String uri;
	private WSContext context;

	public WSPiece(String idlogic, Resources act, String text, int x, int y, Grid grid) {
		super(idlogic, act, text, x, y, grid);
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

	public String showInfo()
	{
		String info = super.showInfo();
		if(Constants.__DEBUG)
			{ 
			info = info+"\n"
					+ "wsdl:"+wsdl+","
					+ "operation:"+operation+","
					+ "service:"+service+","
					+ "port:"+port+","
					+ "tns:"+tns+","
					+ "uri:"+uri+","
					+"context:"+context;
			}	
		return info;
	}
}
