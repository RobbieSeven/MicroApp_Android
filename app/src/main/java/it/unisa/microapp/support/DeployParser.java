package it.unisa.microapp.support;

import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.data.DataType;
import it.unisa.microapp.editor.PreCondition;
import it.unisa.microapp.exceptions.InvalidComponentException;
import it.unisa.microapp.exceptions.NoMoreSpaceException;
import it.unisa.microapp.utils.Utils;
import it.unisa.microapp.webservice.WSDLParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.wsdl.Port;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.xml.XMLConstants;
import javax.xml.parsers.*;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import android.app.Application;
import android.os.StrictMode;
import android.util.Log;

public class DeployParser {

	private DocumentBuilder db;
	private Document doc;
	private ComponentCreator creator;
	private MAComponent startComp = null;
	private DirectedGraph<MAComponent, DirEdge> grafo;
	private Map<String, MAComponent> idComponents;
	private String icon;
	private String desc;	
	private Application app;

	public DeployParser(ComponentCreator crea, Application a) throws ParserConfigurationException {

		DocumentBuilderFactory bf = DocumentBuilderFactory.newInstance();
		bf.setNamespaceAware(true);
		db = bf.newDocumentBuilder();
		creator = crea;
		app = a;
	}

	public MAComponent getStartComp() {
		return startComp;
	}

	public String getIcon() {
		return icon;
	}

	public String getDescription() {
		return desc;
	}
	
	public void setDeployFile(File f, InputStream schema) throws SAXException, IOException {
		doc = db.parse(f);
		// validate(schema);
		try {
			icon = doc.getElementsByTagName("icon").item(0).getTextContent();
		} catch (Exception e) {
			icon = "app_icon";
		}
		Log.d("icon", "icon o " +icon);
		try {
			desc = doc.getElementsByTagName("description").item(0).getTextContent();
		} catch (Exception e) {
			desc = "";
		}
	}

	@SuppressWarnings("unused")
	private void validate(InputStream schemas) {

		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);

		Source schemaFile = new StreamSource(schemas);
		try {
			Schema schema = factory.newSchema(schemaFile);
			Validator validator = schema.newValidator();
			validator.validate(new DOMSource(doc));
		} catch (SAXException e) {
		} catch (IOException e) {
		}
	}

	public void getAppStructure() {

	}

	public DirectedGraph<MAComponent, DirEdge> createGraph() throws InvalidComponentException, NoMoreSpaceException {
		grafo = new DefaultDirectedGraph<MAComponent, DirEdge>(DirEdge.class);

		NodeList components = doc.getElementsByTagName("component");
		int l = components.getLength();
		idComponents = new HashMap<String, MAComponent>(l);
		for (int i = 0; i < l; i++) {
			Element n = (Element) components.item(i);
			String idComp = n.getAttribute("id");
			String type = n.getAttribute("type");
			MAComponent newComp = null;

			String description = "";
			NodeList nDesc = n.getElementsByTagName("description");
			if (nDesc.getLength() > 0) {
				Element desc = (Element) nDesc.item(0);
				description = desc.getTextContent();
			}
			Log.d("INFO", "sono qui 127 " + idComp + " "+ type);
			creator = new ConcreteComponentCreator();
			if (type.contains("WEBSERVICE")) {
				String wsdl = "";
				String port = "";
				String url = "";

				if (n.getElementsByTagName("userdata").getLength() > 0) {
					NodeList list = n.getElementsByTagName("userdata");

					for (int k = 0; k < list.getLength(); k++) {
						String name = ((Element) list.item(k)).getAttribute("name");

						if (name.equals("wsdlUri"))
							wsdl = ((Element) list.item(k)).getTextContent();
						else if (name.equals("port"))
							port = ((Element) list.item(k)).getTextContent();
					}
				} else {
					wsdl = n.getAttribute("wsdl");
					port = n.getAttribute("port");
					url = n.getAttribute("uri");
				}
				if (checkURL(wsdl, port, url)) {
					Element setting = (Element) n.getElementsByTagName("settings").item(0);

					if (setting == null)
						((ConcreteComponentCreator) creator).setWebServiceParam(n.getAttribute("wsdl"),
								n.getAttribute("operation"), n.getAttribute("service"), n.getAttribute("port"),
								n.getAttribute("tns"), n.getAttribute("uri"), false, true, 60000);
					else {
						boolean dotNet = Boolean.parseBoolean(setting.getAttribute("dotNet"));
						boolean implicitType = Boolean.parseBoolean(setting.getAttribute("implicitType"));
						int timeout = Integer.parseInt(setting.getAttribute("timeout"));

						((ConcreteComponentCreator) creator).setWebServiceParam(n.getAttribute("wsdl"),
								n.getAttribute("operation"), n.getAttribute("service"), n.getAttribute("port"),
								n.getAttribute("tns"), n.getAttribute("uri"), dotNet, implicitType, timeout);

					}

					newComp = creator.createComponentService(type, idComp, description);
				} else
					throw new InvalidComponentException("web service " + n.getAttribute("operation") + " not available");

			} 
			else if (type.contains("DOMOTIC")) {	
				String name = n.getAttribute("dname");
				String state = n.getAttribute("dstate");
				String link = n.getAttribute("dlink");
				String dtype = n.getAttribute("dtype");
				
				((ConcreteComponentCreator) creator).setDomoticParam(name,state,link,dtype);
				newComp = creator.createComponentService(type, idComp, description);
			}
			else {
				newComp = creator.createComponentService(type, idComp, description);
			}

			
			String condition = n.getAttribute("condition");
			newComp.setCondition(condition);
			if(!(condition.equals("false"))){
				Utils.debug("DeployParser "+  condition);
				Element list = (Element) n.getElementsByTagName("condition").item(0);
				int length =  list.getElementsByTagName("precondition").getLength();
				PreCondition[] preCondition = new PreCondition[length];
				newComp.setPreCondition(preCondition);
				for(int k=0 ; k < length ;k++){
					preCondition[k] = new PreCondition();
					Element pre = (Element) list.getElementsByTagName("precondition").item(k);
					String check = pre.getAttribute("check");
					String cond = pre.getAttribute("condition");
					String oper = pre.getAttribute("operator");
					String value = pre.getAttribute("value");
				
					if(check.equals("true"))
						preCondition[k].setCheck(true);
					else
						preCondition[k].setCheck(false);
					preCondition[k].setCondition(Integer.parseInt(cond));
					preCondition[k].setOperator(Integer.parseInt(oper));
					preCondition[k].setValue(value);	
				}
			}

			String stat = n.getAttribute("state");
			newComp.setNowState(stat);

			NodeList userdata = n.getElementsByTagName("userdata");
			int ln = userdata.getLength();
			for (int j = 0; j < ln; j++) {
				Element out = (Element) userdata.item(j);

				newComp.addUserData(out.getAttribute("name"), out.getTextContent());

			}
			grafo.addVertex(newComp);
			if (i == 0)
				startComp = newComp;
			idComponents.put(idComp, newComp);
		}
		
			
		for (int i = 0; i < l; i++) {
				
			Element n = (Element) components.item(i);
			NodeList outputs = n.getElementsByTagName("output");
			int k = outputs.getLength();
			
			for (int j = 0; j < k; j++) {
				Element out = (Element) outputs.item(j);

				if (!out.getAttribute("id").isEmpty()) {
					MAComponent scomp = idComponents.get(out.getAttribute("id"));
					MAComponent fcomp = idComponents.get(n.getAttribute("id"));
					grafo.addEdge(fcomp, scomp);
					fcomp.addOutput(DataType.valueOf(out.getAttribute("datatype")), scomp.getId(),
							out.getAttribute("binding"));
					scomp.addInput(DataType.valueOf(out.getAttribute("datatype")), fcomp.getId(),
							out.getAttribute("binding"));
				}
			}
		}
		return grafo;
	}


	public String createGraphCode() throws InvalidComponentException, NoMoreSpaceException {
		String grafoCode = "";
		
		grafoCode +="MAComponent newComp = null;\n";
		grafoCode +="MAComponent startComp = null;\n";
		
		grafoCode += "DefaultDirectedGraph<MAComponent, DirEdge> grafo = new DefaultDirectedGraph<MAComponent, DirEdge>(DirEdge.class);\n";
		
		NodeList components = doc.getElementsByTagName("component");
		int l = components.getLength();
		
		grafoCode += "HashMap<String, MAComponent> idComponents = new HashMap<String, MAComponent>(" + l + ");\n";
		
		grafoCode += "ConcreteComponentCreator creator = new ConcreteComponentCreator();\n";
		grafoCode += "try {\n";
		for (int i = 0; i < l; i++) {
			Element n = (Element) components.item(i);
			String idComp = n.getAttribute("id");
			String type = n.getAttribute("type");
			MAComponent newComp = null;

			String description = "";
			NodeList nDesc = n.getElementsByTagName("description");
			if (nDesc.getLength() > 0) {
				Element desc = (Element) nDesc.item(0);
				description = desc.getTextContent();
			}
			
			if (type.contains("WEBSERVICE")) {
				String wsdl = "";
				String port = "";
				String url = "";

				if (n.getElementsByTagName("userdata").getLength() > 0) {
					NodeList list = n.getElementsByTagName("userdata");

					for (int k = 0; k < list.getLength(); k++) {
						String name = ((Element) list.item(k)).getAttribute("name");

						if (name.equals("wsdlUri"))
							wsdl = ((Element) list.item(k)).getTextContent();
						else if (name.equals("port"))
							port = ((Element) list.item(k)).getTextContent();
					}
				} else {
					wsdl = n.getAttribute("wsdl");
					port = n.getAttribute("port");
					url = n.getAttribute("uri");
				}
				if (checkURL(wsdl, port, url)) {
					Element setting = (Element) n.getElementsByTagName("settings").item(0);

					if (setting == null) {
						grafoCode += "((ConcreteComponentCreator) creator).setWebServiceParam(\"" + n.getAttribute("wsdl") + "\",\""+
								n.getAttribute("operation") + "\", \"" + n.getAttribute("service") + "\", \"" + n.getAttribute("port") + "\",\""+
								n.getAttribute("tns") + "\", \"" + n.getAttribute("uri") + "\", false, true, 60000);\n";
					}
						
					else {
						boolean dotNet = Boolean.parseBoolean(setting.getAttribute("dotNet"));
						boolean implicitType = Boolean.parseBoolean(setting.getAttribute("implicitType"));
						int timeout = Integer.parseInt(setting.getAttribute("timeout"));
						
						grafoCode += "((ConcreteComponentCreator) creator).setWebServiceParam(\"" + n.getAttribute("wsdl") + "\",\""+
								n.getAttribute("operation") + "\", \"" + n.getAttribute("service") + "\", \"" + n.getAttribute("port") + "\",\""+
								n.getAttribute("tns") + "\", \"" + n.getAttribute("uri") + "\", " + dotNet + ", " + implicitType + ", " + timeout + ");\n";

					}

					grafoCode += "newComp = creator.createComponent(\"" + type + "\", \"" + idComp + "\", \"" + description + "\");\n";
				} else
					throw new InvalidComponentException("web service " + n.getAttribute("operation") + " not available");

			} else {
				grafoCode += "newComp = creator.createComponent(\"" + type + "\", \"" + idComp + "\", \"" + description + "\");\n";
			}
		
			String condition = n.getAttribute("condition");
			grafoCode += "newComp.setCondition(\"" + condition + "\");\n";
			
			if(!(condition.equals("false"))){
				Utils.debug("DeployParser "+  condition);
				Element list = (Element) n.getElementsByTagName("condition").item(0);
				int length =  list.getElementsByTagName("precondition").getLength();
				
				grafoCode += "PreCondition[] preCondition = new PreCondition["+length+ "];\n";
				grafoCode += "newComp.setPreCondition(preCondition);\n";
				for(int k=0 ; k < length ;k++){
					grafoCode += "preCondition[k] = new PreCondition();\n";
					Element pre = (Element) list.getElementsByTagName("precondition").item(k);
					String check = pre.getAttribute("check");
					String cond = pre.getAttribute("condition");
					String oper = pre.getAttribute("operator");
					String value = pre.getAttribute("value");
				
					if(check.equals("true"))
						grafoCode += "preCondition["+k+"].setCheck(true);\n";
					else
						grafoCode += "preCondition["+k+"].setCheck(false);\n";
					
					grafoCode += "preCondition[" + k + "].setCondition(" + Integer.parseInt(cond) + ");\n";
					grafoCode += "preCondition[" + k + "].setOperator(" + Integer.parseInt(oper) + ");\n";
					grafoCode += "preCondition[" + k + "].setValue(\"" + value + "\");\n";	
				}
			}
			

			NodeList userdata = n.getElementsByTagName("userdata");
			int ln = userdata.getLength();
			for (int j = 0; j < ln; j++) {
				Element out = (Element) userdata.item(j);

				grafoCode += "newComp.addUserData(\"" + out.getAttribute("name") + "\", \"" + out.getTextContent() + "\");\n";
			}
			
			grafoCode += "grafo.addVertex(newComp);\n";
			if (i == 0)
				grafoCode += "startComp = newComp;\n";
			
			grafoCode += "idComponents.put(\"" + idComp + "\", newComp);\n";
		}
		grafoCode += "MAComponent scomp = null;\n";
		grafoCode += "MAComponent fcomp = null;\n";
		
			
		for (int i = 0; i < l; i++) {
				
			Element n = (Element) components.item(i);
			NodeList outputs = n.getElementsByTagName("output");
			int k = outputs.getLength();
			
			for (int j = 0; j < k; j++) {
				Element out = (Element) outputs.item(j);

				if (!out.getAttribute("id").isEmpty()) {
					
					grafoCode += "scomp = idComponents.get(\"" + out.getAttribute("id") + "\");\n";
					grafoCode += "fcomp = idComponents.get(\"" + n.getAttribute("id") + "\");\n";
					
					grafoCode += "grafo.addEdge(fcomp, scomp);\n";
					
					grafoCode += "fcomp.addOutput(DataType.valueOf(\"" + out.getAttribute("datatype") + "\"), scomp.getId(), \""+ out.getAttribute("binding") + "\");\n";
					grafoCode += "scomp.addInput(DataType.valueOf(\"" + out.getAttribute("datatype") + "\"), fcomp.getId(), \"" + out.getAttribute("binding") + "\");\n";
				}
			}
		}
		grafoCode += "}catch(Exception e){}\n";

		return grafoCode;
	}

	
	
	@SuppressWarnings("static-access")
	private boolean checkURL(String wsdl, String port, String url) {


		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

		StrictMode.setThreadPolicy(policy);

		// controllo se e attiva una connessione
		boolean connected = false;

		connected = Utils.chkeckNetworkConnection(app);
		if (connected) {
			// TODO:caso 1 url non vuoto
			if (!url.isEmpty()) {
				// check URL
				try {
					URL Url = new URL(url);
					HttpURLConnection http = (HttpURLConnection) Url.openConnection();

					http.setRequestMethod("GET");

					http.connect();

					int code = http.getResponseCode();

					http.disconnect();

					switch (code) {
					case 404:// NOT FOUND
					case 410:// GONE
					case 503:// SERVICE UNAVAILABLE
						return false;
					default:
						return true;
					}

				} catch (MalformedURLException e) {
					return false;
				} catch (IOException e) {
					return false;
				}
			}
			// TODO:caso 2 mi ricavo url dal documento wsdl
			else {
				WSDLParser parser = new WSDLParser();

				String endpoint = "";

				parser.setportinfo(true);
				parser.setservinfo(false);

				if (!wsdl.startsWith("http://"))
					wsdl = "http://" + wsdl;

				
				if (parser.parse(wsdl) == 0) {
					Port p = parser.getPort(port);

					if (p != null) {
						List<?> list = p.getExtensibilityElements();

						for (Object obj : list) {
							if (obj instanceof SOAPAddress) {
								SOAPAddress addr = (SOAPAddress) obj;
								endpoint = addr.getLocationURI();
								break;
							}
						}

						// check URL
						try {
							URL Url = new URL(endpoint);
							HttpURLConnection http = (HttpURLConnection) Url.openConnection();

							http.setRequestMethod("GET");

							http.connect();

							int code = http.getResponseCode();

							http.disconnect();

							switch (code) {
							case 404:// NOT FOUND
							case 410:// GONE
							case 503:// SERVICE UNAVAILABLE
								return false;
							default:
								return true;
							}
						} catch (MalformedURLException e) {
							return false;
						} catch (IOException e) {
							return false;
						}
					} else
						return false;
				} else
					return false;
			}
		} else
			return false;
	}

}

/*
 * ABBOZZO DELL'ALGORTIMO
 * 
 * NodeList components=document.getElementsByTagName("component"); creazione del
 * grafo dalla lista di component visita DfS con controllo per ogni nodo
 */
