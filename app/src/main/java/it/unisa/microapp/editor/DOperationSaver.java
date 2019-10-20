package it.unisa.microapp.editor;
	
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import android.util.Xml;

import it.unisa.microapp.service.domotics.DOperation;
import it.unisa.microapp.support.FileManagement;
import it.unisa.microapp.utils.Constants;
import it.unisa.microapp.utils.Utils;
	

	public class DOperationSaver 
	{
		public static void save(DOperation op,String category,String projFile,Context act)
		{
			String repPath=FileManagement.getRepositoryPath() + Constants.DRepository;
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
					for(int j=0; j<2; j++){
						Element newelem=doc.createElement("function");
					
					  //creo il nuovo elemento
					  makeNewElement(newelem,op,doc,j);
					
					  el.appendChild(newelem);
					}  
					
					File rf=new File(repPath);
					
					//salvo il tutto sul device
					saveOntoDevice(root,rf); 
				}
				else
				{
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

		private static void makeNewElement(Element elem, DOperation op, Document doc, int J) 
		{
			elem.setAttribute("name", op.getText());
			elem.setAttribute("id", op.getIdLogic()+J);			
			elem.setAttribute("states", op.getAllStates());
			
			
			//setto gli attributi
			elem.setAttribute("dname", op.getName());
			elem.setAttribute("dstate", op.getState());
			elem.setAttribute("dlink", op.getLink());
			elem.setAttribute("dtype", op.getType());
		
			switch (J) {
			
			case 0: 
				//aggiungo gli input
				Element ee=doc.createElement("inputs");
				makePins(ee,op.getInputPin(),doc,"input",false);
				elem.appendChild(ee);
				
				//aggiungo gli output
				ee=doc.createElement("outputs");
				makePins(ee,op.getOutputPin(),doc,"output",false);
				elem.appendChild(ee);
				break;
				
			case 1:
				//aggiungo gli input
				Element eee=doc.createElement("inputs");
				makePins(eee,op.getInputPin(),doc,"userinput",true);
				op.getInputPin().clear();
				op.addInputPin(new Pin(PinEnumerator.valueOf("NORMAL"), PinParamEnumerator.valueOf("OBJECT"), "OBJECT"));
				makePins(eee,op.getInputPin(),doc,"input",false);
				elem.appendChild(eee);
				
				//aggiungo gli output
				eee=doc.createElement("outputs");
				op.getOutputPin().clear();
				op.addOutputPin(new Pin(PinEnumerator.valueOf("NORMAL"), PinParamEnumerator.valueOf("OBJECT"), "OBJECT"));
				makePins(eee,op.getOutputPin(),doc,"output",false);
				elem.appendChild(eee);
				break;
				
			}		
		
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



