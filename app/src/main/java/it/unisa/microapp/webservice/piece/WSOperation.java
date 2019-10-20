package it.unisa.microapp.webservice.piece;

import android.content.res.Resources;
import android.os.Parcel;

import it.unisa.microapp.editor.Grid;
import it.unisa.microapp.editor.Piece;
import it.unisa.microapp.utils.Constants;
import it.unisa.microapp.utils.Utils;

public class WSOperation extends Piece 
{
	private static final long serialVersionUID = 8042493841523776980L;
	private String wsdl;
	private String operation;
	private String service;
	private String port;
	private String tns;
	private String uri;
	private WSContext context;
	
	public static final Creator<WSOperation> CREATOR =
			new Creator<WSOperation>() {
			public WSOperation createFromParcel(Parcel in) {
			return new WSOperation(in);
			}
			public WSOperation[] newArray(int size) {
			return new WSOperation[size];
			}
			};
	
	public WSOperation(String idLogic, Resources act, String text, int x,
			int y, Grid grid) {
		super(idLogic, act, text, x, y, grid);
		context=new WSContext();
		wsdl="";
		operation="";
		service="";
		port="";
		tns="";
		uri="";
		allStates="visible/hidden/progress";
	}
	

	public WSOperation(Parcel in) 
	{
		super(in);
	}
	
	public void readFromParcel(Parcel in)
	{
		super.readFromParcel(in);
		wsdl=in.readString();
		operation=in.readString();
		service=in.readString();
		port=in.readString();
		tns=in.readString();
		uri=in.readString();
		context=new WSContext(in);
		Utils.debug(context.toString());
	}

	public WSContext getContext() {
		return context;
	}


	public void setContext(WSContext context) {
		this.context = context;
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
	
	public void writeToParcel(Parcel dest, int flags) 
	{
		super.writeToParcel(dest, flags);
		dest.writeString(wsdl);
		dest.writeString(operation);
		dest.writeString(service);
		dest.writeString(port);
		dest.writeString(tns);
		dest.writeString(uri);
		//dest.writeSerializable(context);
		context.writeToParcel(dest, flags);
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
