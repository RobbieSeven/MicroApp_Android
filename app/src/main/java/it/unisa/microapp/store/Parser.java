package it.unisa.microapp.store;



import java.io.IOException;
import java.io.StringWriter;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;


public class Parser extends Activity{

	private Document doc;
	private String xml;

	public Parser(Document doc){
		this.doc = doc;
	}

	public String getXMLString() throws TransformerException, IOException{
		NodeList list = doc.getElementsByTagName("userdata");

		for(int i=0; i<list.getLength(); i++){
			Element e = (Element) list.item(i);
			if(e.getAttribute("datatype").equals("CONTACT"))
				e.setTextContent(/*Constants.SENTENCE_EMPTY + */"");
			if(e.getAttribute("datatype").equals("IMAGE"))
				e.setTextContent("");
			if(e.getAttribute("datatype").equals("URI"))
				e.setTextContent("");
		}
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		StringWriter writer = new StringWriter();
		Result result = new StreamResult(writer);
		Source source = new DOMSource(doc);
		transformer.transform(source, result);
		writer.close();

		xml = writer.toString();

		return xml;
	}

}
