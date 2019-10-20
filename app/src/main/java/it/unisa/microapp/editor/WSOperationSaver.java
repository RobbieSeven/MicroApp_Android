package it.unisa.microapp.editor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Xml;

import it.unisa.microapp.support.FileManagement;
import it.unisa.microapp.utils.Constants;
import it.unisa.microapp.utils.Utils;
import it.unisa.microapp.webservice.piece.WSOperation;

public class WSOperationSaver 
{
	public static void save(WSOperation op,String category,String projFile,Context act)
	{
		String repPath=FileManagement.getRepositoryPath() + Constants.WSRepository;
		File f=new File(repPath);
		
		try {
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
	        DocumentBuilder docBuilder;
			docBuilder = dbfac.newDocumentBuilder();
			Document doc=docBuilder.parse(f);
			
			Element root=(Element) doc.getFirstChild();
			
			NodeList list=root.getElementsByTagName("category");
			Element el=null;
			
			for(int i=0;i<list.getLength();i++)
			{
				el=(Element) list.item(i);
				String name=el.getAttribute("name");
				
				if(name.equals(category))
					break;
			}
			
			if(!exist(op.getIdLogic(),el))
			{
				Element newelem=doc.createElement("function");
				
				//creo il nuovo elemento
				makeNewElement(newelem,op,doc);
				
				el.appendChild(newelem);
				
				File rf=new File(repPath);
				
				//salvo il tutto sul device
				saveOntoDevice(root,rf);
			}
			else
			{
				Utils.verbose("update context of "+op.getOperation());
				updateContext(op.getIdLogic(),el,act);
				File rf=new File(repPath);
						
				//salvo il tutto sul device
				saveOntoDevice(root,rf);
			}
			
		} catch (ParserConfigurationException e) {
			Utils.error(e.getMessage(), e);
		} catch (SAXException e) {
			Utils.error(e.getMessage(), e);
		} catch (IOException e) {
			Utils.error(e.getMessage(), e);
		}
		
	}

	protected static void updateContext(String id, Element el, Context act) 
	{
		NodeList list=el.getElementsByTagName("function");
		Element func=null;
		
		for(int i=0;i<list.getLength();i++)
		{
			func=(Element) list.item(i);
			String ids=func.getAttribute("id");
			
			if(ids.equals(id))
				break;
		}
		
		if(func != null)
		{
			//aggiorno tempo
			Element context=(Element) func.getElementsByTagName("context").item(0);
			
			Element time=(Element) context.getElementsByTagName("time").item(0);
			
			Calendar c=Calendar.getInstance();
			
			Utils.verbose("old time:"+time.getTextContent());
			
			time.setTextContent(""+c.getTime().getTime());
			
			Utils.verbose("new time:"+time.getTextContent());
			
			//aggiorno location
			Element location=(Element) context.getElementsByTagName("location").item(0);
			
			LocationManager locationManager;
			String con = Context.LOCATION_SERVICE;
			locationManager = (LocationManager)act.getSystemService(con);
			String provider = LocationManager.GPS_PROVIDER;
			Location loc = locationManager.getLastKnownLocation(provider);
			
			Utils.verbose("old Location\nlat:"+location.getAttribute("latitude")+
					  "\nlon:"+location.getAttribute("longitude")+
				      "\nalt:"+location.getAttribute("altitude"));
			
			if(loc != null)
			{
				location.setAttribute("latitude", ""+loc.getLatitude());
				location.setAttribute("longitude", ""+loc.getLongitude());
				location.setAttribute("altitude", ""+loc.getAltitude());
				
				Utils.verbose("new Location\nlat:"+loc.getLatitude()+
							  "\nlon:"+loc.getLongitude()+
						      "\nalt:"+loc.getAltitude());
			}
		}
	}

	private static boolean exist(String id, Element el) 
	{
		NodeList list=el.getElementsByTagName("function");
		
		for(int i=0;i<list.getLength();i++)
		{
			Element e=(Element) list.item(i);
			String ids=e.getAttribute("id");
			
			if(ids.equals(id))
				return true;
		}
		
		return false;
	}

	private static void saveOntoDevice(Element root, File f) 
	{
		FileOutputStream fileos;
		try {
			fileos = new FileOutputStream(f);
			XmlSerializer serializer = Xml.newSerializer();
			serializer.setOutput(fileos, "UTF-8");
			serializer.startDocument(null, null);
			serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
			
			preorder(serializer,root);
			
			serializer.endDocument();
			serializer.flush();
			
		} catch (FileNotFoundException e) {
			Utils.error(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			Utils.error(e.getMessage(), e);
		} catch (IllegalStateException e) {
			Utils.error(e.getMessage(), e);
		} catch (IOException e) {
			Utils.error(e.getMessage(), e);
		}
		
	}

	private static void preorder(XmlSerializer serializer, Element root) 
	{
		try {
			serializer.startTag(null, root.getNodeName());
			
			if(root.hasAttributes())
			{
				NamedNodeMap attr=root.getAttributes();
				for(int i=0;i<attr.getLength();i++)
				{
					Attr at=(Attr) attr.item(i);
					serializer.attribute(null, at.getName(), at.getNodeValue());
				}
			}
			
			NodeList list=root.getChildNodes();
			
			for(int i=0;i<list.getLength();i++)
			{
				if(list.item(i) instanceof Element)
					preorder(serializer,(Element) list.item(i));
				else
				{
					Text txt=(Text) list.item(i);
					serializer.text(txt.getTextContent());
				}
			}
			
			serializer.endTag(null, root.getNodeName());
		} catch (IllegalArgumentException e) {
			Utils.error(e.getMessage(), e);
		} catch (IllegalStateException e) {
			Utils.error(e.getMessage(), e);
		} catch (IOException e) {
			Utils.error(e.getMessage(), e);
		}
	}

	private static void makeNewElement(Element elem, WSOperation op, Document doc) 
	{
		//setto gli attributi
		elem.setAttribute("name", op.getText());
		elem.setAttribute("id", op.getIdLogic());
		elem.setAttribute("wsdl", op.getWsdl());
		elem.setAttribute("operation", op.getOperation());
		elem.setAttribute("port", op.getPort());
		elem.setAttribute("service", op.getService());
		elem.setAttribute("tns", op.getTns());
		elem.setAttribute("uri", op.getUri());
		elem.setAttribute("states", op.getAllStates());
		
		//aggiungo contesto
		Element ctx=doc.createElement("context");
		Element time=doc.createElement("time");
		Element loc=doc.createElement("location");
		
		time.setTextContent(""+op.getContext().getDate().getTime());
		ctx.appendChild(time);
		
		loc.setAttribute("latitude", ""+op.getContext().getLocation().getLatitude());
		loc.setAttribute("longitude", ""+op.getContext().getLocation().getLongitude());
		loc.setAttribute("altitude", ""+op.getContext().getLocation().getAltitude());
		
		ctx.appendChild(loc);
		
		elem.appendChild(ctx);
		
		//aggiungo descrizione
		ctx=doc.createElement("description");
		ctx.setTextContent(op.getDescription());
		
		elem.appendChild(ctx);
		
		//aggiungo gli input
		Element ee=doc.createElement("inputs");
		makePins(ee,op.getInputPin(),doc,"input",false);
		//makePins(ee,op.getInputPin(),doc,"userinput",true);
		elem.appendChild(ee);
		
		//aggiungo gli output
		ee=doc.createElement("outputs");
		makePins(ee,op.getOutputPin(),doc,"output",false);
		elem.appendChild(ee);
	}

	private static void makePins(Element ee, List<Pin> list, Document doc, String type, boolean user) 
	{
		for(Pin p : list)
		{
			if(!user)
			{
				if(p.getType() != PinEnumerator.USER)
				{
					Element pin=doc.createElement(type);
					pin.setAttribute("name", p.getText());
					pin.setAttribute("type", p.getParam().toString());
					if(p.getType() == PinEnumerator.MULTI)
						pin.setAttribute("multi", "true");
					ee.appendChild(pin);
				}
			}
			else
			{
				Element pin=doc.createElement(type);
				pin.setAttribute("name", p.getText());
				pin.setAttribute("type", p.getParam().toString());
				ee.appendChild(pin);
			}
		}
	}
}
