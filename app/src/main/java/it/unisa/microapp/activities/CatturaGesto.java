package it.unisa.microapp.activities;

import it.unisa.microapp.R;
import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.exceptions.InvalidComponentException;
import it.unisa.microapp.support.FileManagement;
import it.unisa.microapp.utils.Constants;
import it.unisa.microapp.utils.Utils;
import it.unisa.microapp.webservice.piece.WSSettings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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

import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class CatturaGesto extends MAActivity implements OnGesturePerformedListener {
    
	private GestureLibrary mLibrary;
	String textPath="";

	@Override
	protected void initialize(Bundle savedInstanceState) {
		
        requestWindowFeature(Window.FEATURE_NO_TITLE);

	}	
	
	@Override
	protected void prepare() {
	 
	}

	@Override
	protected int onVisible() {
		return R.layout.catturagesto;
	}	
	
	@Override
	protected View onVisibleView() {
		
		return null;
	}	

	@Override
	protected void execute() {
		

	}
	
	@Override
	protected void prepareView(View v) {
	    mLibrary = GestureLibraries.fromFile(FileManagement.getGestureDir());
	    if (!mLibrary.load())
	    {
	    	Toast.makeText(this,"No gestures active!", Toast.LENGTH_SHORT).show();
	    	finish();
	    }
	    else
	    	Toast.makeText(this,"Draw the gesture\nClick back to exit", Toast.LENGTH_SHORT).show();

        GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestures);
        gestures.addOnGesturePerformedListener(this);	    
	}	

	@Override
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture)
	{
		ArrayList<Prediction> predictions = mLibrary.recognize(gesture);
		String msg="";
		//boolean runtime=false;
		boolean flag=false;
		
		// We want at least one prediction
		if (predictions.size() > 0) 
		{
			Prediction prediction = predictions.get(0);
			// We want at least some confidence in the result
			if (prediction.score > 1) 
			{
				    // Show the spell
					textPath=prediction.name;
					
					if(textPath.contains("è"))
					{
						flag=true;
						textPath=textPath.substring(0,textPath.length()-2);
					}
			}
		}
		
		if(!flag)
			Toast.makeText(this,"No match", Toast.LENGTH_SHORT).show();
		else
		{	
			Toast.makeText(this,"Loading "+ textPath, Toast.LENGTH_SHORT).show();
		
			try 
			{
				application.setDeployPath(textPath, false);
				application.parsingDownload(textPath);
			} 
			catch (NullPointerException e) 
			{
				msg="File opening error";
				Toast.makeText(this,msg, Toast.LENGTH_SHORT).show();
				return;
			} 
			catch (SAXException e) 
			{
				msg="Reading file is corrupted";		
				Toast.makeText(this,msg, Toast.LENGTH_SHORT).show();
				return;
			} 
			catch (IOException e)
			{
				msg="File opening error";
				Toast.makeText(this,msg, Toast.LENGTH_SHORT).show();
				return;
			}
		
			try 
			{
				//runtime = true;
				//application.setDeployPath(textPath, false);

				try
				{
					application.initComponents();
				} 
				catch (InvalidComponentException e)
				{
					Toast.makeText(this,getString(R.string.notRunnable), Toast.LENGTH_SHORT).show();
					return;
				}
			
				MAComponent mycomponent = (MAComponent) application.getCurrentState();
			
				if (!mycomponent.isOutFilled()) 
				{
					Toast.makeText(this,"Some outputs are missing " + mycomponent.getType() + " id:" + mycomponent.getId(),Toast.LENGTH_SHORT).show();
					return;
				}
			
				MAComponent comp = application.nextStep();
				if (comp == null) 
				{
					setResult(Constants.ID_TERMINATED);
					checkSettingUpdate();
					finish();
				}
			
				try 
				{
					Intent runi = new Intent(this, comp.getActivityClass());

					Bundle bnd = new Bundle();
					bnd.putString("description", comp.getDescription());
					bnd.putString("state", comp.getNowState());
					runi.putExtras(bnd);						
					startActivityForResult(runi, 0);
				} 
				catch (ClassNotFoundException e)
				{
					Utils.errorDialog(this, e.getMessage());
				}
			} 
			catch (Exception e)
			{
				Utils.error(e.getMessage(), e);
			}
		}
	}

	
	protected void checkSettingUpdate() 
	{
		boolean update=false;

		Document doc=openFile(FileManagement.getLocalAppPath() + textPath);

		if(doc == null)
			finish();

		List<MAComponent> components=application.getComponentList();

		for(MAComponent comp : components)
		{
			if(comp.getType().startsWith("WEBSERVICE"))
			{
				if(comp.isUpdate())
				{
					updateFile(doc,comp.getId(),comp.getSettings());
					update=true;
				}
			}
		}

		if(update)
			saveOntoDevice((Element)doc.getFirstChild(),new File(FileManagement.getLocalAppPath() + textPath));
	}
	
	protected Document openFile(String path) 
	{
		File f=new File(path);
		Document doc=null;
		try {
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;
			docBuilder = dbfac.newDocumentBuilder();
			doc=docBuilder.parse(f);
		} catch (SAXException e) {
			Utils.error(e);
		} catch (IOException e) {
			Utils.error(e);
		} catch (ParserConfigurationException e) {
			Utils.error(e);
		}
		return doc;
	}
	
	protected void updateFile(Document doc, String id, WSSettings set) 
	{
		Element root=(Element) doc.getFirstChild();

		NodeList list=root.getElementsByTagName("component");

		for(int i=0;i<list.getLength();i++)
		{
			Element elem=(Element) list.item(i);
			String ids=elem.getAttribute("id");

			if(ids.equals(id))
			{
				Element setting=(Element) elem.getElementsByTagName("settings").item(0);

				if(setting == null)
				{
					setting=doc.createElement("settings");
					setting.setAttribute("dotNet",""+set.isDotNet());
					setting.setAttribute("implicitType",""+set.isImplicitType());
					setting.setAttribute("timeout",""+set.getTimeout());

					elem.appendChild(setting);
				}
				else
				{
					setting.setAttribute("dotNet",""+set.isDotNet());
					setting.setAttribute("implicitType",""+set.isImplicitType());
					setting.setAttribute("timeout",""+set.getTimeout());
				}
			}
		}

	}
	
	protected void saveOntoDevice(Element root, File f) 
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
	
	private void preorder(XmlSerializer serializer, Element root) 
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
		} 
		
		catch (IllegalArgumentException e) 
		{
			Utils.error(e.getMessage(), e);
		} 
		catch (IllegalStateException e) 
		{
			Utils.error(e.getMessage(), e);
		} 
		catch (IOException e) 
		{
			Utils.error(e.getMessage(), e);
		}
	}
	
	public void initInputs() {
	}
	
	public void beforeNext() {
	}
	
	@Override
	public void backPressed() {
	    finish();
	}
	
	protected void resume(){
		
	}

}


