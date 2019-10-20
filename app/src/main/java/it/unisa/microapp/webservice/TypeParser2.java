package it.unisa.microapp.webservice;
import it.unisa.microapp.utils.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.wsdl.Definition;
import javax.wsdl.Types;
import javax.wsdl.extensions.schema.Schema;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import com.ibm.wsdl.extensions.schema.SchemaImportImpl;

public class TypeParser2 
{
	private static Definition def;
	private static Map<String,Node> typeMap;//mappa degli elementi definiti nelo schema
	private static Map<String,Node> idNodeMap;//mappa degli elementi con id associato
	private static List<QName> typeList;
	
	public TypeParser2(Definition d)
	{
		def=d;
	}
	
	public static void parse(String uri) 
	{
		
		idNodeMap=new HashMap<String,Node>();
		
	    Types T=def.getTypes();
	    
	    if(T == null)
	    {
	    	//throw new NoTypesException("il documento non ha tipi dati");
	    	return;
	    }
	    else
	    {
	    	typeList=new LinkedList<QName>();
	    	typeMap=new HashMap<String,Node>();
	    }
	    
	    List<?> l=T.getExtensibilityElements();
	    
	    for(Object o : l)
	    {
	    	if(o instanceof Schema)
	    	{
	    		Schema s=(Schema)o;
	    		
	    		Element schema=s.getElement();
	    		
	    		//estraggo tipi da eventuali elementi import
	    		extractTypeFromImport(s);
	    		
	    		
	    		String tns=schema.getAttribute("targetNameSpace");
	    		
	    		if(tns.isEmpty())
	    			tns=def.getTargetNamespace();
	    		
	    		//estraggo i tipi dallo schema
	    		extractTypes(schema,tns);
	    	}
	    	
	    }

	}
	
	private static void extractTypeFromImport(Schema s) 
	{
		Map<?,?> imports=s.getImports();
		Set<?> keyset=(Set<?>) imports.keySet();
	    
	    Iterator<?> it= (Iterator<?>) keyset.iterator();
	    
	    while(it.hasNext())
	    {
	    	Vector<?> v=(Vector<?>) imports.get(it.next());
	    	for(int j=0;j<v.size();j++)
	    	{
	    		SchemaImportImpl i=(SchemaImportImpl)v.get(j);
		    	DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
		    	try {
		    				
		    		DocumentBuilder db = df.newDocumentBuilder();
		    		
		    		if(i.getSchemaLocationURI() != null)
		    		{	
		    			Document doc=db.parse(i.getSchemaLocationURI());
		    				
		    			Element e=(Element) doc.getDocumentElement();
		    				
		   				extractTypes(e,i.getNamespaceURI());
		    		}
		    		
		    	} catch (ParserConfigurationException e) {
					Utils.error(e.getMessage(), e);
		   		} catch (SAXException e) {
					Utils.error(e.getMessage(), e);
		   		} catch (IOException e) {
					Utils.error(e.getMessage(), e);
		   		}
	    	}
	    	
	    }
	}

	public Map<String,Node> getTypeMap()
	{
		return typeMap;
	}
	
	public Map<String,Node> getIdNodeMap()
	{
		return idNodeMap;
	}
	
	public List<QName> getTypeList()
	{
		return typeList;
	}


	private static void extractTypes(Element schema, String tns) 
	{
		NodeList list=schema.getChildNodes();
		
		for(int i=0;i<list.getLength();i++)
		{
			Node n=list.item(i);
			
			if(n instanceof Element)
			{
				Element e=(Element)n;
				
				String name=e.getAttribute("name");
				
				if(name != null)
				{
					//controllo se l'elemento si riferisce ad un'altro
					String type=e.getAttribute("type");
					
					
					
					if(typeMap.get(name) == null)
					{
						if(!type.contains(name))
						{
							typeList.add(new QName(tns,name));
							typeMap.put(name, e);
						}
					}
				}
					
				
				if(e.getAttribute("id") != null)
					if(idNodeMap.get(e.getAttribute("id")) == null)
						idNodeMap.put(e.getAttribute("id"), e);
			}
		}
	}
}
