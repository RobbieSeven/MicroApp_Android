package it.unisa.microapp.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import it.unisa.microapp.components.*;
import it.unisa.microapp.components.information.ScaffoldingComponent;
import it.unisa.microapp.exceptions.InvalidComponentException;
import it.unisa.microapp.exceptions.NoMoreSpaceException;



import it.unisa.microapp.utils.Constants;
import it.unisa.microapp.webservice.piece.WSSettings;

public class ConcreteComponentCreator implements ComponentCreator {

	private String wsdl;
	private String operation;
	private String service;
	private String port;
	private String tns;
	private String uri;
	
	private boolean dotNet;
	private boolean implicitType;
	private int timeout;
	
	private String dname;
	private String dstate;
	private String dlink;
	private String dtype;	
	

	public void setWebServiceParam(String wsdl, String operation, String service, String port, String tns, String uri,boolean dotNet,boolean implicitType,int timeout) 
	{
		this.wsdl = wsdl;
		this.operation = operation;
		this.service = service;
		this.port = port;
		this.tns = tns;
		this.uri = uri;
		
		this.dotNet=dotNet;
		this.implicitType=implicitType;
		this.timeout=timeout;
	}
	
	public void setDomoticParam(String name, String state, String link, String type) 
	{
		this.dname = name;
		this.dstate = state;
		this.dlink = link;
		this.dtype = type;
	}	

	public MAComponent createComponentService(String type, String id, String description) throws InvalidComponentException , NoMoreSpaceException{
		
		if (type.equals("ADMOB_ADVIEW"))
			return new it.unisa.microapp.components.admob.AdmobAdviewComponent(id,description);
		
		if (type.equals("ACCELEROMETER_INTERCEPTOR"))
			return new BlankComponent(type);
		
		if (type.equals("CAMERA_TAKE"))
			return new it.unisa.microapp.components.camera.CameraTakeComponent(id,description);
		if (type.equals("CAMERA_PREVIEW"))
			return new it.unisa.microapp.components.camera.CameraPreviewComponent(id,description);
		if (type.equals("CAMERA_FIXED"))
			return new it.unisa.microapp.components.camera.CameraFixedComponent(id,description);
		if (type.equals("CAMERA_SAVE"))
			return new it.unisa.microapp.components.camera.CameraSaveComponent(id, description);

		if (type.equals("CONTACTS_STATIC"))
			return new it.unisa.microapp.components.contacts.ContactsStaticComponent(id,description);
		if (type.equals("CONTACTS_SELECT"))
			return new it.unisa.microapp.components.contacts.ContactsSelectComponent(id,description);
		if (type.equals("CONTACTS_PREVIEW"))
			return new it.unisa.microapp.components.contacts.ContactsPreviewComponent(id,description);
		if (type.equals("CONTACTS_JOIN"))
			return new BlankComponent(type);
		if (type.equals("CONTACTS_UPDATE"))
			return new it.unisa.microapp.components.contacts.ContactsUpdateComponent(id,description);
		if (type.equals("CONTACTS_CREATE"))
			return new it.unisa.microapp.components.contacts.ContactsCreateComponent(id,description);
		
		if (type.equals("CALENDAR_DATE"))
			return new it.unisa.microapp.components.calendar.DateComponent(id,description);
		if (type.equals("CALENDAR_STATICDATE"))
			return new it.unisa.microapp.components.calendar.StaticDateComponent(id,description);
		if (type.equals("CALENDAR_CURRENTDATE"))
			return new it.unisa.microapp.components.calendar.CurrentDateComponent(id,description);
		
		if (type.equals("FACE_DETECT"))
			return new it.unisa.microapp.components.face.FaceDetectComponent(id,description);
		
		if (type.equals("FACEBOOK_UPLOAD"))
			return new BlankComponent(type);
		if (type.equals("FACEBOOK_FRIENDS"))
			return new BlankComponent(type);
		if (type.equals("FACEBOOK_GROUPS"))
			return new BlankComponent(type);
		
		if (type.equals("GPS_LOCATION"))
			return new it.unisa.microapp.components.gps.GpsLocationComponent(id,description);
		if (type.equals("GPS_STATICLOCATION"))
			return new it.unisa.microapp.components.gps.GpsStaticLocationComponent(id,description);
		if (type.equals("LOCATION_INTERCEPTOR"))
			return new it.unisa.microapp.components.gps.GpsLocationInterceptorComponent(id,description);
		if (type.equals("PROXIMITY_INTERCEPTOR"))
			return new BlankComponent(type);;		
		 
		if (type.equals("IMAGE_SCALE"))
			return new it.unisa.microapp.components.image.ImageScaleComponent(id,description);
		if (type.equals("IMAGE_ROTATE"))
			return new it.unisa.microapp.components.image.ImageRotateComponent(id,description);
		if (type.equals("IMAGE_FLIP"))
			return new it.unisa.microapp.components.image.ImageFlipComponent(id,description);
		if (type.equals("IMAGE_PAINT"))
			return new it.unisa.microapp.components.image.ImagePaintComponent(id,description);

		if (type.equals("INFORMATION_PROMPT"))
			return new it.unisa.microapp.components.information.InformationPromptComponent(id,description);
		if (type.equals("INFORMATION_VIBRATE"))
			return new it.unisa.microapp.components.information.InformationVibrateComponent(id,description);
		if (type.equals("INFORMATION_BEEP"))
			return new it.unisa.microapp.components.information.InformationBeepComponent(id,description);
		if (type.equals("INFORMATION_INFO"))
			return new it.unisa.microapp.components.information.InformationInfoComponent(id,description);
		if (type.equals("INFORMATION_ERROR"))
			return new it.unisa.microapp.components.information.InformationErrorComponent(id,description);
		if (type.equals("INFORMATION_PRINT"))
			return new it.unisa.microapp.components.information.InformationPrintComponent(id,description);
		if (type.equals("INFORMATION_PREVIEW"))
			return new it.unisa.microapp.components.information.InformationPreviewComponent(id,description);

		if (type.equals("MAPS_MAP"))
			return new it.unisa.microapp.components.maps.MapsMapComponent(id,description);
		if (type.equals("MAPS_GEOLOCATION"))
			return new it.unisa.microapp.components.maps.MapsGeolocationComponent(id,description);
		if (type.equals("MAPS_GEOADDRESS"))
			return new it.unisa.microapp.components.maps.MapsGeoaddressComponent(id,description);
		if (type.equals("MAPS_LOCATION"))
			return new it.unisa.microapp.components.maps.MapsLocationComponent(id,description);

		if (type.equals("MEDIA_SELECTAUDIO"))
			return new it.unisa.microapp.components.media.MediaSelectAudioComponent(id,description);
		if (type.equals("MEDIA_RECAUDIO"))
			return new it.unisa.microapp.components.media.MediaRecAudioComponent(id,description);
		if (type.equals("MEDIA_PLAYAUDIO"))
			return new it.unisa.microapp.components.media.MediaPlayAudioComponent(id,description);
		if (type.equals("MEDIA_STATICAUDIO"))
			return new it.unisa.microapp.components.media.MediaStaticAudioComponent(id,description);
		if (type.equals("MEDIA_SELECTVIDEO"))
			return new it.unisa.microapp.components.media.MediaSelectVideoComponent(id,description);
		if (type.equals("MEDIA_RECVIDEO"))
			return new it.unisa.microapp.components.media.MediaRecVideoComponent(id,description);
		if (type.equals("MEDIA_PLAYVIDEO"))
			return new it.unisa.microapp.components.media.MediaPlayVideoComponent(id,description);
		if (type.equals("MEDIA_STATICVIDEO"))
			return new it.unisa.microapp.components.media.MediaStaticVideoComponent(id,description);
		if (type.equals("MEDIA_PLAYSOUND"))
			return new it.unisa.microapp.components.media.MediaPlaySoundComponent(id,description);

		if (type.equals("NET_INTERCEPTOR"))
			return new BlankComponent(type);		
		
		if (type.equals("CALL_INTERCEPTOR"))
			return new it.unisa.microapp.components.call.CallInterceptorComponent(id,description);
		if (type.equals("PHONE_CALL"))
			return new it.unisa.microapp.components.call.PhoneCallComponent(id,description);

		if (type.equals("SAVE_SAVE"))
			return new it.unisa.microapp.components.save.SaveSaveComponent(id,description);

		if (type.equals("SEND_MAIL"))
			return new  it.unisa.microapp.components.send.SendMailComponent(id,description);
		if (type.equals("SEND_STATICMAIL"))
			return new  it.unisa.microapp.components.send.SendStaticMailComponent(id,description);
		if (type.equals("SEND_TEXTSMS"))
			return new it.unisa.microapp.components.send.SendSMSComponent(id,description);
		if (type.equals("SEND_STATICTEXTSMS"))
			return new BlankComponent(type);		
		
		if (type.equals("SPEECH_SPEECHTOTEXT"))
			return new it.unisa.microapp.components.speech.SpeechSpeechToTextComponent(id,description);
		if (type.equals("SPEECH_TEXTTOSPEECH"))
			return new it.unisa.microapp.components.speech.SpeechTextToSpeechComponent(id,description);
		if (type.equals("SPEECH_STATICTEXTTOSPEECH"))
			return new BlankComponent(type);		
		
		if (type.equals("TEXT_TEXT"))
			return new it.unisa.microapp.components.text.TextComponent(id,description);
		if (type.equals("TEXT_STATIC"))
			return new it.unisa.microapp.components.text.StaticTextComponent(id,description);
		if (type.equals("TEXT_PASSWORD"))
			return new it.unisa.microapp.components.text.PasswordTextComponent(id,description);

		if (type.equals("NUMBER_NUMBER"))
			return new it.unisa.microapp.components.number.NumberComponent(id,description);
		if (type.equals("NUMBER_STATIC"))
			return new it.unisa.microapp.components.number.StaticNumberComponent(id,description);
		if (type.equals("NUMBER_BETWEEN"))
			return new it.unisa.microapp.components.number.BetweenNumberComponent(id,description);
		
		if (type.startsWith("WEBSERVICE")) {
			if (type.equals("WEBSERVICE_HILL_CHIPHER"))
				return new it.unisa.microapp.components.webservice.HillChipherComponent(id, wsdl, operation, tns, uri,new WSSettings(dotNet,implicitType,timeout),description);
			else if (type.equals("WEBSERVICE_STATIC"))
				return new it.unisa.microapp.components.webservice.WebServiceStaticComponent(id,new WSSettings(dotNet,implicitType,timeout),description);
			else if (type.equals("WEBSERVICE_AIRCONDITIONER"))
				return new BlankComponent(type);
			else if (type.equals("WEBSERVICE_YELLOWPAGES"))
				return new BlankComponent(type);			
			else
				return new it.unisa.microapp.components.webservice.WebServiceComponent(id, wsdl, service, port, operation, tns, uri,new WSSettings(dotNet,implicitType,timeout),description);
		}
		
		
		if (type.startsWith("DOMOTIC")) {
			return new it.unisa.microapp.components.domotic.DomoticComponent(id, dname, dstate,  dlink, dtype, description);
			
		}

		if(type.startsWith("HEART_RATE")){
			return new it.unisa.microapp.components.heartrate.HeartRateComponent(id,description);
		}
		if(type.startsWith("VIEWER")){
			return new it.unisa.microapp.components.viewer.ViewerComponent(id,description);
		}

		if (type.equals("CONDITION"))
			return new it.unisa.microapp.components.condition.ConditionComponent(id,description);

		if (type.equals("CONDITIONDYN"))
			return new it.unisa.microapp.components.condition.ConditionDynamicComponent(id,description);
		//controlla se l'id è nel file scaffolding xml e restituisce l'activity corrispondente.
		try 
		{
			DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			db = df.newDocumentBuilder();
			File f_scaff = new File( FileManagement.getRepositoryPath(), Constants.ScaffoldingRepository);
			Document d = db.parse(new FileInputStream(f_scaff));
			NodeList l = d.getElementsByTagName("component");
			
			// LISTA DEGLI SCAFFOLDING
			for (int i = 0; i < l.getLength(); i++)
			{
				Element el = (Element) l.item(i);
				String scaffolding_name = el.getAttribute("class");
				String scaffolding_id = el.getAttribute("id");
				if(type.equals(scaffolding_id)) {
					System.err.println(scaffolding_name+ " __ " +scaffolding_id);

					if(!ScaffoldingManager.isScaffoldingFull()) { 
						String scaffoldingName = ScaffoldingManager.getScaffolding(type);
						ScaffoldingManager.increaseIndex();
						System.err.println(ScaffoldingManager.getScaffoldingIndex());
						ScaffoldingComponent p = new ScaffoldingComponent(id,description,scaffoldingName,scaffolding_id);
						System.err.println(p.getProvaName());
						return p;
					}
					else {
						throw new NoMoreSpaceException(ScaffoldingManager.getScaffoldingNumber());
					}
					
				}
			}
		} catch (ParserConfigurationException e) {
		} catch (FileNotFoundException e) {
		} catch (SAXException e) {
		} catch (IOException e) {
		}
		
		throw new InvalidComponentException(type);
	}
}

/*
       
         
            
 */