package it.unisa.microapp.support;

import it.unisa.microapp.utils.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

public class ScaffoldingManager {

	private static int num_scaffolding = Constants.MAX_SCAFFOLDING;
	private static int indice = 1;

	public static boolean increaseIndex() {
		if(indice <= num_scaffolding) {
			indice++;
			return true;
		}
		return false;
	}
	
	public static void resetScaffoldingIndex() {
		
		try 
		{
			DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			db = df.newDocumentBuilder();
			File f_scaff = new File(FileManagement.getRepositoryPath(), Constants.ScaffoldingRepository);
			Document d = db.parse(new FileInputStream(f_scaff));
			NodeList l = d.getElementsByTagName("component");
			
			// LISTA DEGLI SCAFFOLDING 
			for (int i = 0; i < l.getLength(); i++)
			{
				Element el = (Element) l.item(i);
				el.setAttribute("used", "null");
			}
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			StreamResult result = new StreamResult(new FileOutputStream(f_scaff));
			DOMSource source = new DOMSource(d);
			transformer.transform(source, result);
			
		} catch (ParserConfigurationException e) {
		} catch (FileNotFoundException e) {
		} catch (SAXException e) {
		} catch (IOException e) {
		} catch (TransformerConfigurationException e) {
		} catch (TransformerFactoryConfigurationError e) {
		} catch (TransformerException e) {
		}
		
		indice = 1;
	}
	
	public static boolean isScaffoldingFull() {
		if(indice <= num_scaffolding) {
			return false;
		}
		return true;
	}
	
	public static int getScaffoldingNumber() {
		return num_scaffolding;
	}
	public static int getScaffoldingIndex() {
		return indice;
	}
	
	public static String getScaffolding(String nomeclasse) {
		
		try 
		{
			DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			db = df.newDocumentBuilder();
			File f_scaff = new File(FileManagement.getRepositoryPath(), Constants.ScaffoldingRepository);
			Document d = db.parse(new FileInputStream(f_scaff));
			NodeList l = d.getElementsByTagName("component");
			
			// LISTA DEGLI SCAFFOLDING 
			for (int i = 0; i < l.getLength(); i++)
			{
				Element el = (Element) l.item(i);
				String scaffolding_id = el.getAttribute("id");
				if(scaffolding_id.equalsIgnoreCase(nomeclasse)) {
					el.setAttribute("used", "Scaffolding"+indice);
					break;
				}
			}
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			StreamResult result = new StreamResult(new FileOutputStream(f_scaff));
			DOMSource source = new DOMSource(d);
			transformer.transform(source, result);
			
		} catch (ParserConfigurationException e) {
		} catch (FileNotFoundException e) {
		} catch (SAXException e) {
		} catch (IOException e) {
		} catch (TransformerConfigurationException e) {
		} catch (TransformerFactoryConfigurationError e) {
		} catch (TransformerException e) {
		}
		
		return "Scaffolding"+indice;
	}
	
	public static void createScaffoldingXml() {
		try {
			File f_scaff = new File(FileManagement.getRepositoryPath(), Constants.ScaffoldingRepository);
			if (!f_scaff.exists()) {
				f_scaff.createNewFile();
				FileOutputStream fileos_scaff = new FileOutputStream(f_scaff);
				XmlSerializer serializer_scaff = Xml.newSerializer();
				serializer_scaff.setOutput(fileos_scaff, "UTF-8");
				serializer_scaff.startDocument(null, null);
				serializer_scaff.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
				serializer_scaff.startTag(null, "document");
				serializer_scaff.text(" ");
				serializer_scaff.endTag(null, "document");
				serializer_scaff.endDocument();
				serializer_scaff.flush();
				fileos_scaff.close();
			}
		
		} catch (IOException e) {
		}
	}

	public static void setIdNewComponent(String id) {
		try 
		{
			File f_scaff = new File(FileManagement.getRepositoryPath(), Constants.ScaffoldingRepository);
			InputStream is = new FileInputStream(f_scaff);
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(is);

			NodeList document =  doc.getElementsByTagName("document");
			Element el = (Element) document.item(0);

			Element nuovoelemento = doc.createElement("component");
			nuovoelemento.setAttribute("class", id+".zip");
			nuovoelemento.setAttribute("id", id);
			nuovoelemento.setAttribute("used", "null");

			el.appendChild(nuovoelemento);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new FileOutputStream(f_scaff));
			transformer.transform(source, result);
			
		} catch (ParserConfigurationException e) {
		} catch (FileNotFoundException e) {
		} catch (SAXException e) {
		} catch (IOException e) {
		} catch (TransformerConfigurationException e) {
		} catch (TransformerFactoryConfigurationError e) {
		} catch (TransformerException e) {
		}
	}
}
