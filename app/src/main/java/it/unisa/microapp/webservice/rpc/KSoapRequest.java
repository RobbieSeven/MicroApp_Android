package it.unisa.microapp.webservice.rpc;

import java.io.IOException;

import it.unisa.microapp.utils.Utils;
import it.unisa.microapp.webservice.entry.MAEntry;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.Transport;
import org.xmlpull.v1.XmlPullParserException;

/**
 * classe responsabile dell'invocazione di un web service
 * tramite l'uso della libreria KSoap 2.6.2
 *
 */
public class KSoapRequest extends Request 
{
	private String wsdl;
	private String operation;
	private String tns;
	private String uri;
	private Object[] params;
	private SoapObject request;
	private SoapSerializationEnvelope envelope;
	private Transport call;
	private boolean dotNet;
	private boolean implicitTypes;
	private int timeout;
	private String SOAPAction;
	private boolean is3GNetwork;
	
	private Object response;
	
	public KSoapRequest(String wsdl,String operation,String tns,String uri,Object[] params,boolean _3G)
	{
		this.wsdl=wsdl;
		this.operation=operation;
		this.tns=tns;
		this.uri=uri;
		this.params=params;
		dotNet=false;
		implicitTypes=true;
		SOAPAction="";
		timeout=60000;
		is3GNetwork=_3G;
	}

	public boolean isDotNet() {
		return dotNet;
	}

	public void setDotNet(boolean dotNet) {
		this.dotNet = dotNet;
	}
	
	public String getWsdl()
	{
		return wsdl;
	}
	
	public boolean isImplicitTypes() {
		return implicitTypes;
	}

	public void setImplicitTypes(boolean implicitTypes) {
		this.implicitTypes = implicitTypes;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	@Override
	protected void init() 
	{
		request=new SoapObject(tns,operation);
		
		parseElements(request,params);
		
		Utils.verbose(request.toString());
		envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.bodyOut = request;
        envelope.dotNet = dotNet;
        envelope.setOutputSoapObject(request);
        envelope.setAddAdornments(false);
        envelope.implicitTypes= implicitTypes;
		
        //controllo se la rete in uso e' 3g
        if(is3GNetwork)
        	call=new Http3GTransport(uri);
        else
        	call=new HttpTransportSE(uri,timeout);
		
		if(!tns.endsWith("/"))
			tns=tns+"/";
	}


	@SuppressWarnings("unchecked")
	private void parseElements(SoapObject request, Object[] params) 
	{
		int n=params.length;
		
		for(int i=0;i<n;i++)
		{
			
			MAEntry<String,Object> entry= (MAEntry<String, Object>) params[i];
			
			if(entry.getValue() instanceof Object[])
			{
				SoapObject r=new SoapObject(tns,entry.getKey());
				parseElements(r,(Object[]) entry.getValue());
				request.addProperty(""+entry.getKey(), r);
			}
			else
				request.addProperty(""+entry.getKey(), ""+entry.getValue().toString());
			
			/*
			if(entry.getValue() instanceof String)
			{
				request.addProperty(entry.getKey(), entry.getValue().toString());
			}
			else
			{
				SoapObject r=new SoapObject(tns,entry.getKey());
				parseElements(r,(Object[]) entry.getValue());
				request.addProperty(""+entry.getKey(), r);
			}
			*/
		}
	}
	
	@Override
	protected void connect() {
	}

	@Override
	protected int execute() 
	{
		
		try {
			//call.call(tns+operation, envelope);
			call.call(SOAPAction, envelope);
			response=envelope.bodyIn;
			//TODO:gestire i casi in cui si riceve un soapfalt o una risposta positiva
			if(response instanceof SoapFault)
			{
				String faultString=((SoapFault) response).faultstring;
				String ret="fault:"+faultString+"\nHELP: change the setting properties or adjust the parameters of request";
				response=ret;
				
				return -1;
			}
		} catch (IOException e) 
		{
			if(envelope.bodyIn != null)
			{
				response=envelope.bodyIn;
			}
			else
				response=new String("Fault:client was unable to read response\nHELP: change the setting properties or adjust the parameters of request");
			return -1;
		} catch (XmlPullParserException e) 
		{
			Utils.error(e.getMessage(), e);
			if(envelope.bodyIn != null)
				response=envelope.bodyIn;
			else
				response=new String("Fault:client was unable to read response\nHELP: change the setting properties or adjust the parameters of request");
			return -1;
		}
		
		return 0;
	}

	@Override
	protected void disconnect() 
	{
	}
	
	public Object getResponse()
	{
		return response;
	}

	public void setSOAPAction(String SOAPAction) 
	{
		this.SOAPAction=SOAPAction;
	}

}
