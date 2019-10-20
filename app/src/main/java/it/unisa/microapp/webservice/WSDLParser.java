package it.unisa.microapp.webservice;
import it.unisa.microapp.utils.Utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;

import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;


import com.ibm.wsdl.factory.WSDLFactoryImpl;

public class WSDLParser 
{
	private static Definition def;
	private static QName serviceName=null;
	private static String targetNameSpace=null;
	private static String documentation=null;
	private static Map<String,List<Operation>> portlist=null;
	private static Map<String,Port> _ports=null;
	private static TypeParser2 typeParser=null;
	
	private static boolean getservinfo;
	private static boolean getportinfo;
	private static boolean getopinfo;
	private static boolean gettypeinfo;
	
	public WSDLParser()
	{
		getservinfo=true;
		getportinfo=false;
		getopinfo=false;
		gettypeinfo=false;
	}
	
	@SuppressWarnings("static-access")
	public static int parse(String Uri)
	{
		WSDLFactory factory = new WSDLFactoryImpl();
	    WSDLReader reader = factory.newWSDLReader();
	    try {
			def = reader.readWSDL(Uri);
		} catch (WSDLException e) {
			Utils.error(e.getMessage(), e);
			return -1;
		} catch (Exception e)
		{
			Utils.error(e.getMessage(), e);
			return -1;			
		}
	    	
	    if(getopinfo)
	    	portlist=new HashMap<String,List<Operation>>();
	    if(getportinfo)
	    	_ports=new HashMap<String,Port>();
	    if(gettypeinfo)
	    	typeParser=new TypeParser2(def);
	    	    
	    Map<?,?> m=def.getServices();
	    
	    Set<?> key=m.keySet();
	    
	    Iterator<?> i=key.iterator();
	    
	    while(i.hasNext())
	    {
	    	Service s=(Service)m.get(i.next());
	    	
	    	if(getservinfo)
	    	{
	    		targetNameSpace=def.getTargetNamespace();
	    		serviceName=s.getQName();
	    	
	    		if(def.getDocumentationElement() != null)
	    			documentation=def.getDocumentationElement().getFirstChild().getNodeValue();
	    		else if(s.getDocumentationElement() != null)
	    			documentation=s.getDocumentationElement().getFirstChild().getNodeValue();
	    		else
	    			documentation=serviceName.getLocalPart();
	    	}
	    	
	    	if(getopinfo || getportinfo)
	    	{
	    		Map<?,?> ports=s.getPorts();
	    		Set<?> kport=ports.keySet();
	    		Iterator<?> ip=kport.iterator();
	    	
	    		while(ip.hasNext())
	    		{
	    			String opname;
	    		
	    			Port p=(Port)ports.get(ip.next());
	    		
	    			opname=p.getName();
	    			
	    			if(getopinfo)
	    			{
		    			Binding b=p.getBinding();
		    			PortType pp=b.getPortType();
		    			
		    			@SuppressWarnings("unchecked")
	    				List<Operation> list = pp.getOperations();
	    				portlist.put(opname, list);
	    			}
	    			if(getportinfo)
	    				_ports.put(opname, p);
	    		}
	    	}
	    	
	    	if(gettypeinfo)
	    		typeParser.parse(Uri);
	    }
		return 0; 
	}
	
	public TypeParser2 getTypesInformation()
	{
		return typeParser;
	}
	
	public void setallInfos()
	{
		getservinfo=true;
		getportinfo=true;
		getopinfo=true;
		gettypeinfo=true;
	}

	public  boolean isservinfo() {
		return getservinfo;
	}

	public void setservinfo(boolean getservinfo) {
		WSDLParser.getservinfo = getservinfo;
	}

	public  boolean isportinfo() {
		return getportinfo;
	}

	public  void setportinfo(boolean getportinfo) {
		WSDLParser.getportinfo = getportinfo;
	}

	public  boolean isopinfo() {
		return getopinfo;
	}

	public  void setopinfo(boolean getopinfo) {
		WSDLParser.getopinfo = getopinfo;
	}

	public  boolean istypeinfo() {
		return gettypeinfo;
	}

	public  void settypeinfo(boolean gettypeinfo) {
		WSDLParser.gettypeinfo = gettypeinfo;
	}

	public Port getPort(String portName)
	{
		return _ports.get(portName);
	}
	
	public String getServiceName()
	{
		return serviceName.getLocalPart();
	}
	
	public QName getServiceQName()
	{
		return serviceName;
	}
	
	public String getTargerNameSpace()
	{
		return targetNameSpace;
	}
	
	public String getDocumentation()
	{
		return documentation;
	}
	
	public Set<String> getPortList()
	{
		return portlist.keySet();
	}
	
	public List<Operation> getOperationList(String port)
	{
		return portlist.get(port);
	}
}
