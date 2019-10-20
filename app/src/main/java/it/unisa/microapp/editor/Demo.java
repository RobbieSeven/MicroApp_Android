package it.unisa.microapp.editor;

import it.unisa.microapp.R;

public class Demo {

	int iloop = 0;
	MainPanel _panel;

	public Demo(MainPanel panel) {
		this._panel = panel;
	}

	public void reset() {
		iloop = 0;
	}

	public void set(int i) {
		if (_panel != null) {
			iloop = i % 4;
			sampleLoop(iloop);
		}
	}	
	public void next() {
		if (_panel != null) {
			iloop = (iloop + 1) % 4;
			sampleLoop(iloop);
		}
	}

	public void sample(int id) {
		if (_panel != null) {
			switch (id) {
			case 1:
				sample1();
				break;
			}
		}
	}

	private void sampleLoop(int screen) {
		if (screen == 0) {
			_panel.grid.clearPieces();

			Piece f = new Piece("CAMERA_TAKE", _panel.act.getResources(), "Take", 1, 1, _panel.grid);
			f.setIconin("R.drawable.camera48", R.drawable.camera48); // android.R.drawable
			f.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.IMAGE, "Image", "2", "CAMERA_PREVIEW"));
			f.setInputPin(_panel.annotatePins(f.getInputPin()));
			f.setId("1");
			f.setNowState("visible"); 
			_panel.grid.addNewPiece(f);

			Piece p;

			p = new Piece("CAMERA_PREVIEW", _panel.act.getResources(), "Preview", 1, 2, _panel.grid);
			p.addInputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.IMAGE, "Image"));
			p.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.IMAGE, "Image", "3", "WEBSERVICE_ENCRYPTH"));
			p.setIconin("R.drawable.camera48", R.drawable.camera48);
			p.setInputPin(_panel.annotatePins(p.getInputPin()));
			p.setId("2");
			p.setAllStates("visible/hidden");
			//p.setNowState("hidden"); 
			p.setNowState("visible");
			_panel.grid.addNewPiece(p);

			p = new Piece("WEBSERVICE_ENCRYPT", _panel.act.getResources(), "Encrypt", 1, 3, _panel.grid);
			p.addInputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.OBJECT, "Image"));
			Pin ppass = new Pin(PinEnumerator.USER, PinParamEnumerator.STRING, "Password");
			ppass.setInput("password");
			p.addInputPin(ppass);
			p.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.OBJECT, "Image", "8", "SEND_SEND"));
			p.setIconin("R.drawable.image48", R.drawable.webservice48);
			p.setInputPin(_panel.annotatePins(p.getInputPin()));
			p.setId("3");
			p.setNowState("visible"); 
			_panel.grid.addNewPiece(p);

		} else if (screen == 2) {
			_panel.grid.clearPieces();

			Piece f = new Piece("CAMERA_TAKE", _panel.act.getResources(), "Take", 1, 1, _panel.grid);
			f.setIconin("R.drawable.camera48", R.drawable.camera48); // android.R.drawable
			f.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.IMAGE, "Image", "2", "CAMERA_PREVIEW"));
			f.setInputPin(_panel.annotatePins(f.getInputPin()));
			f.setId("1");
			f.setNowState("visible"); 
			_panel.grid.addNewPiece(f);

			Piece p;

			p = new Piece("CAMERA_PREVIEW", _panel.act.getResources(), "Preview", 1, 2, _panel.grid);
			p.addInputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.IMAGE, "Image"));
			p.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.IMAGE, "Image", "3", "WEBSERVICE_ENCRYPTH"));
			p.setIconin("R.drawable.camera48", R.drawable.camera48);
			p.setInputPin(_panel.annotatePins(p.getInputPin()));
			p.setId("2");
			p.setAllStates("visible/hidden");
			p.setNowState("visible");
			_panel.grid.addNewPiece(p);

			p = new Piece("WEBSERVICE_ENCRYPT", _panel.act.getResources(), "Encrypt", 1, 3, _panel.grid);
			p.addInputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.OBJECT, "Image"));
			Pin ppass = new Pin(PinEnumerator.USER, PinParamEnumerator.STRING, "Password");
			ppass.setInput("password");
			p.addInputPin(ppass);
			p.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.OBJECT, "Image", "8", "SEND_SEND"));
			p.setIconin("R.drawable.image48", R.drawable.webservice48);
			p.setInputPin(_panel.annotatePins(p.getInputPin()));
			p.setId("3");
			p.setNowState("visible"); 
			_panel.grid.addNewPiece(p);

			p = new Piece("CONTACTS_CONTACT", _panel.act.getResources(), "Contact", 2, 1, _panel.grid);
			p.addInputPin(new Pin(PinEnumerator.USER, PinParamEnumerator.CONTACT, "Contact"));
			p.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.CONTACT, "Contact", "8", "SEND_SEND"));
			p.setIconin("R.drawable.contact48", R.drawable.contact48);
			p.setInputPin(_panel.annotatePins(p.getInputPin()));
			p.setId("5");
			p.setNowState("visible"); 
			_panel.grid.addNewPiece(p);
		} else if (screen == 3) {
			_panel.grid.clearPieces();

			Piece f = new Piece("CAMERA_TAKE", _panel.act.getResources(), "Take", 1, 1, _panel.grid);
			f.setIconin("R.drawable.camera48", R.drawable.camera48); // android.R.drawable
			f.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.IMAGE, "Image", "2", "CAMERA_PREVIEW"));
			f.setInputPin(_panel.annotatePins(f.getInputPin()));
			f.setId("1");
			f.setNowState("visible"); 
			_panel.grid.addNewPiece(f);

			Piece p;

			p = new Piece("CAMERA_PREVIEW", _panel.act.getResources(), "Preview", 1, 2, _panel.grid);
			p.addInputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.IMAGE, "Image"));
			p.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.IMAGE, "Image", "3", "WEBSERVICE_ENCRYPTH"));
			p.setIconin("R.drawable.camera48", R.drawable.camera48);
			p.setInputPin(_panel.annotatePins(p.getInputPin()));
			p.setId("2");
			p.setAllStates("visible/hidden");
			p.setNowState("visible");
			_panel.grid.addNewPiece(p);

			p = new Piece("WEBSERVICE_ENCRYPT", _panel.act.getResources(), "Encrypt", 1, 3, _panel.grid);
			p.addInputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.OBJECT, "Image"));
			Pin ppass = new Pin(PinEnumerator.USER, PinParamEnumerator.STRING, "Password");
			ppass.setInput("password");
			p.addInputPin(ppass);
			p.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.OBJECT, "Image", "8", "SEND_SEND"));
			p.setIconin("R.drawable.image48", R.drawable.webservice48);
			p.setInputPin(_panel.annotatePins(p.getInputPin()));
			p.setId("3");
			p.setNowState("visible"); 
			_panel.grid.addNewPiece(p);

			p = new Piece("CONTACTS_CONTACT", _panel.act.getResources(), "Contact", 2, 1, _panel.grid);
			p.addInputPin(new Pin(PinEnumerator.USER, PinParamEnumerator.CONTACT, "Contact"));
			p.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.CONTACT, "Contact", "8", "SEND_SEND"));
			p.setIconin("R.drawable.contact48", R.drawable.contact48);
			p.setInputPin(_panel.annotatePins(p.getInputPin()));
			p.setId("5");
			p.setNowState("visible"); 
			_panel.grid.addNewPiece(p);

			p = new Piece("GPS_LOCATION", _panel.act.getResources(), "Location", 3, 1, _panel.grid);
			p.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.LOCATION, "Location", "7", "MAPS_MAPS"));
			p.setIconin("R.drawable.gps48", R.drawable.gps48);
			p.setInputPin(_panel.annotatePins(p.getInputPin()));
			p.setId("6");
			p.setAllStates("visible/hidden/progress");
			p.setNowState("progress"); 
			_panel.grid.addNewPiece(p);

			p = new Piece("MAPS_MAPS", _panel.act.getResources(), "Maps", 3, 2, _panel.grid);
			p.addInputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.LOCATION, "Location"));
			p.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.IMAGE, "Image"));
			p.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.STRING, "PlaceName", "8", "SEND_SEND"));
			p.setIconin("R.drawable.maps48", R.drawable.maps48);
			p.setInputPin(_panel.annotatePins(p.getInputPin()));
			p.setId("7");
			p.setNowState("visible"); 
			_panel.grid.addNewPiece(p);

		} else if (screen == 4) {
			_panel.grid.clearPieces();

			Piece f = new Piece("CAMERA_TAKE", _panel.act.getResources(), "Take", 1, 1, _panel.grid);
			f.setIconin("R.drawable.camera48", R.drawable.camera48); // android.R.drawable
			f.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.IMAGE, "Image", "2", "CAMERA_PREVIEW"));
			f.setInputPin(_panel.annotatePins(f.getInputPin()));
			f.setId("1");
			f.setNowState("visible"); 
			_panel.grid.addNewPiece(f);

			Piece p;

			p = new Piece("CAMERA_PREVIEW", _panel.act.getResources(), "Preview", 1, 2, _panel.grid);
			p.addInputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.IMAGE, "Image"));
			p.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.IMAGE, "Image", "3", "WEBSERVICE_ENCRYPTH"));
			p.setIconin("R.drawable.camera48", R.drawable.camera48);
			p.setInputPin(_panel.annotatePins(p.getInputPin()));
			p.setId("2");
			p.setAllStates("visible/hidden");
			p.setNowState("visible");
			_panel.grid.addNewPiece(p);

			p = new Piece("WEBSERVICE_ENCRYPT", _panel.act.getResources(), "Encrypt", 1, 3, _panel.grid);
			p.addInputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.OBJECT, "Image"));
			Pin ppass = new Pin(PinEnumerator.USER, PinParamEnumerator.STRING, "Password");
			ppass.setInput("password");
			p.addInputPin(ppass);
			p.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.OBJECT, "Image", "8", "SEND_SEND"));
			p.setIconin("R.drawable.image48", R.drawable.webservice48);
			p.setInputPin(_panel.annotatePins(p.getInputPin()));
			p.setId("3");
			p.setNowState("visible"); 
			_panel.grid.addNewPiece(p);

			p = new Piece("CONTACTS_CONTACT", _panel.act.getResources(), "Contact", 2, 1, _panel.grid);
			p.setHeightScale(3);
			p.addInputPin(new Pin(PinEnumerator.USER, PinParamEnumerator.CONTACT, "Contact"));
			p.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.CONTACT, "Contact", "8", "SEND_SEND"));
			p.setIconin("R.drawable.contact48", R.drawable.contact48);
			p.setInputPin(_panel.annotatePins(p.getInputPin()));
			p.setId("5");
			p.setNowState("visible"); 
			_panel.grid.addNewPiece(p);

			p = new Piece("GPS_LOCATION", _panel.act.getResources(), "Location", 3, 1, _panel.grid);
			p.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.LOCATION, "Location", "7", "MAPS_MAPS"));
			p.setIconin("R.drawable.gps48", R.drawable.gps48);
			p.setInputPin(_panel.annotatePins(p.getInputPin()));
			p.setId("6");
			p.setAllStates("visible/hidden/progress");
			p.setNowState("progress"); 
			_panel.grid.addNewPiece(p);

			p = new Piece("MAPS_MAPS", _panel.act.getResources(), "Maps", 3, 2, _panel.grid);
			p.setHeightScale(2);
			p.addInputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.LOCATION, "Location"));
			p.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.IMAGE, "Image"));
			p.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.STRING, "PlaceName", "8", "SEND_SEND"));
			p.setIconin("R.drawable.maps48", R.drawable.maps48);
			p.setInputPin(_panel.annotatePins(p.getInputPin()));
			p.setId("7");
			p.setNowState("visible"); 
			_panel.grid.addNewPiece(p);

			p = new Piece("SEND_SEND", _panel.act.getResources(), "Send", 1, 4, _panel.grid);
			p.setWidthScale(3); // p.setHeightScale(2);
			p.setIconin("R.drawable.mail48", R.drawable.mail48);
			p.addInputPin(new Pin(PinEnumerator.MULTI, PinParamEnumerator.OBJECT, "Attach"));
			p.addInputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.CONTACT, "Contact"));
			p.addInputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.STRING, "Subject"));
			p.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.MAIL, "Email", "9", "INFO_INFO"));
			p.addHole(3);
			p.setInputPin(_panel.annotatePins(p.getInputPin()));
			p.setId("8");
			p.setNowState("visible"); 
			_panel.grid.addNewPiece(p);

		} else if (screen == 1) //Accident Assistance
		{
		_panel.grid.clearPieces();
		
		Piece f = new Piece("CONTACTS_EMERGENCY_STATIC2", _panel.act.getResources(), "Emergency", 1, 1, _panel.grid);
		Pin ppass = new Pin(PinEnumerator.USER, PinParamEnumerator.STRING, "Number");
		ppass.setInput("number");
		f.addInputPin(ppass);		
		f.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.CONTACT, "contact", "3", "PHONE_CALLS"));
		f.setIconin("R.drawable.emergency48", R.drawable.emergency48); // android.R.drawable
		f.setInputPin(_panel.annotatePins(f.getInputPin()));
		f.setId("1");
		f.setNowState("hidden"); 
		_panel.grid.addNewPiece(f);		
		
		f = new Piece("CONTACTS_EMERGENCY_STATIC2", _panel.act.getResources(), "Emergency", 2, 1, _panel.grid);
		ppass = new Pin(PinEnumerator.USER, PinParamEnumerator.STRING, "Number");
		ppass.setInput("number");
		f.addInputPin(ppass);
		f.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.CONTACT, "contact", "3", "PHONE_CALLS"));
		f.setIconin("R.drawable.emergency48", R.drawable.emergency48); // android.R.drawable
		f.setInputPin(_panel.annotatePins(f.getInputPin()));
		f.setId("2");
		f.setNowState("hidden"); 
		_panel.grid.addNewPiece(f);			
		
		f = new Piece("PHONE_CALLS", _panel.act.getResources(), "Call", 1, 2, _panel.grid);
		f.setIconin("R.drawable.phone48", R.drawable.phone48); // android.R.drawable
		f.setWidthScale(2);
		f.addInputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.CONTACT, "contact"));
		f.addInputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.CONTACT, "contact"));
		f.addOutputPin(new Pin(PinEnumerator.MULTI, PinParamEnumerator.CONTACT, "contact", "4", "GPS_LOCATION"));
		f.setId("3");
		f.setNowState("visible"); 
		_panel.grid.addNewPiece(f);		
		
		f = new Piece("GPS_LOCATION", _panel.act.getResources(), "Location", 3, 1, _panel.grid);
		f.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.LOCATION, "location", "5", "MAPS_MAPS"));
		f.setIconin("R.drawable.gps48", R.drawable.gps48);
		f.setInputPin(_panel.annotatePins(f.getInputPin()));
		f.setId("4");
		f.setNowState("visible"); 
		_panel.grid.addNewPiece(f);		
		
		f = new Piece("MAPS_GEOLOCATION", _panel.act.getResources(), "Address Geolocation", 3, 2, _panel.grid);
		f.setIconin("R.drawable.maps48", R.drawable.maps48);
		f.addInputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.LOCATION, "location"));
		f.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.STRING, "address", "6", "RESTFUL_AR"));
		f.setId("5");
		f.setAllStates("visible/hidden");
		f.setNowState("hidden"); 
		_panel.grid.addNewPiece(f);			
		
		f = new Piece("RESTFUL_AR", _panel.act.getResources(), "Accident Report", 3, 3, _panel.grid);
		f.setIconin("R.drawable.restful48", R.drawable.restful48);
		f.setWidthScale(2);
		f.addInputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.STRING, "address"));
		f.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.OBJECT, "info", "7", "INFORMATION_PREVIEW"));
		f.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.OBJECT, "address", "9", "TAKE_AND_SEND2"));
		f.setId("6");
		f.setNowState("visible"); 
		_panel.grid.addNewPiece(f);			
	
		f = new Piece("INFORMATION_PREVIEW", _panel.act.getResources(), "Preview Result", 3, 4, _panel.grid);
		f.setIconin("R.drawable.info48", R.drawable.info48);
		f.addInputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.OBJECT, "info"));
		f.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.OBJECT, "info", "8", "SAVE_EVENT"));
		f.setId("7");
		f.setNowState("visible"); 
		_panel.grid.addNewPiece(f);			
		
		f = new Piece("SAVE_EVENT", _panel.act.getResources(), "Store Event", 3, 5, _panel.grid);
		f.setIconin("R.drawable.notebook48", R.drawable.notebook48);
		f.addInputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.OBJECT, "info"));
		f.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.OBJECT, "info", "10", "SAVE_EVENT"));
		f.setId("8");
		f.setNowState("visible"); 
		_panel.grid.addNewPiece(f);			
		
		f = new Piece("TAKE_AND_SEND2", _panel.act.getResources(), "Take And Send", 4, 4, _panel.grid);
		f.setIconin("R.drawable.send48", R.drawable.send48);
		f.addInputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.OBJECT, "address"));
		f.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.MAIL, "mail", "11", "SAVE_EVENT"));
		ppass = new Pin(PinEnumerator.USER, PinParamEnumerator.CONTACT, "Contact");
		ppass.setInput("contact");
		f.addInputPin(ppass);	
		ppass = new Pin(PinEnumerator.USER, PinParamEnumerator.STRING, "Body");
		ppass.setInput("string");
		f.addInputPin(ppass);	
		ppass = new Pin(PinEnumerator.USER, PinParamEnumerator.STRING, "Subject");
		ppass.setInput("string");
		f.addInputPin(ppass);	
		f.setInputPin(_panel.annotatePins(f.getInputPin()));		
		f.setId("9");
		f.setNowState("visible"); 
		_panel.grid.addNewPiece(f);					
		}
	}

	private void sample1() {
		_panel.grid.clearPieces();

		Piece f = new Piece("CAMERA_TAKE", _panel.act.getResources(), "Take", 1, 1, _panel.grid);
		f.setIconin("R.drawable.camera48", R.drawable.camera48); // android.R.drawable
		f.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.IMAGE, "Image", "2", "CAMERA_PREVIEW"));
		f.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.IMAGE, "Image", "4", "FACEBOOK_UPLOAD"));
		f.setInputPin(_panel.annotatePins(f.getInputPin()));
		f.setId("1");
		f.setNowState("visible"); 
		_panel.grid.addNewPiece(f);

		Piece p;

		p = new Piece("CAMERA_PREVIEW", _panel.act.getResources(), "Preview", 1, 2, _panel.grid);
		p.addInputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.IMAGE, "Image"));
		p.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.IMAGE, "Image", "3", "SAVE_SAVE"));
		p.setIconin("R.drawable.camera48", R.drawable.camera48);
		p.setInputPin(_panel.annotatePins(p.getInputPin()));
		p.setId("2");
		p.setNowState("visible"); 
		_panel.grid.addNewPiece(p);

		p = new Piece("SAVE_SAVE", _panel.act.getResources(), "Save", 1, 3, _panel.grid);
		p.addInputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.IMAGE, "Image"));
		p.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.IMAGE, "Image", "8", "SEND_SEND"));
		p.setIconin("R.drawable.image48", R.drawable.image48);
		p.setInputPin(_panel.annotatePins(p.getInputPin()));
		p.setId("3");
		p.setNowState("visible"); 
		_panel.grid.addNewPiece(p);

		p = new Piece("FACEBOOK_UPLOAD", _panel.act.getResources(), "Upload", 2, 2, _panel.grid);
		p.setHeightScale(3);
		p.addInputPin(new Pin(PinEnumerator.USER, PinParamEnumerator.STRING, "Password"));
		p.addInputPin(new Pin(PinEnumerator.USER, PinParamEnumerator.STRING, "Login"));
		p.addInputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.IMAGE, "Image"));
		p.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.IMAGE, "Image", "9", "INFO_INFO"));
		p.setIconin("R.drawable.facebook48", R.drawable.facebook48);
		p.setInputPin(_panel.annotatePins(p.getInputPin()));
		p.setId("4");
		p.setNowState("visible"); 
		_panel.grid.addNewPiece(p);

		p = new Piece("CONTACTS_CONTACT", _panel.act.getResources(), "Contact", 3, 1, _panel.grid);
		p.setHeightScale(3);
		p.addInputPin(new Pin(PinEnumerator.USER, PinParamEnumerator.CONTACT, "Contact"));
		p.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.CONTACT, "Contact", "8", "SEND_SEND"));
		p.setIconin("R.drawable.contact48", R.drawable.contact48);
		p.setInputPin(_panel.annotatePins(p.getInputPin()));
		p.setId("5");
		p.setNowState("visible"); 
		_panel.grid.addNewPiece(p);

		p = new Piece("GPS_LOCATION", _panel.act.getResources(), "Location", 4, 1, _panel.grid);
		p.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.LOCATION, "Location", "7", "MAPS_MAPS"));
		p.setIconin("R.drawable.gps48", R.drawable.gps48);
		p.setInputPin(_panel.annotatePins(p.getInputPin()));
		p.setId("6");
		p.setNowState("visible"); 
		_panel.grid.addNewPiece(p);

		p = new Piece("MAPS_MAPS", _panel.act.getResources(), "Maps", 4, 2, _panel.grid);
		p.setHeightScale(2);
		p.addInputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.LOCATION, "Location"));
		p.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.IMAGE, "Image"));
		p.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.STRING, "PlaceName", "8", "SEND_SEND"));
		p.setIconin("R.drawable.maps48", R.drawable.maps48);
		p.setInputPin(_panel.annotatePins(p.getInputPin()));
		p.setId("7");
		p.setNowState("visible"); 
		_panel.grid.addNewPiece(p);

		p = new Piece("SEND_SEND", _panel.act.getResources(), "Send", 1, 4, _panel.grid);
		p.setWidthScale(3); // p.setHeightScale(2);
		p.setIconin("R.drawable.mail48", R.drawable.mail48);
		p.addInputPin(new Pin(PinEnumerator.MULTI, PinParamEnumerator.OBJECT, "Attach"));
		p.addInputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.CONTACT, "Contact"));
		p.addInputPin(new Pin(PinEnumerator.MULTI, PinParamEnumerator.OBJECT, "Attach"));
		p.addInputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.STRING, "Subject")); // ,
																							// //
																							// false));
		p.addOutputPin(new Pin(PinEnumerator.NORMAL, PinParamEnumerator.MAIL, "Email", "9", "INFO_INFO"));
		p.addHole(2); //
		p.addHole(4);
		p.setInputPin(_panel.annotatePins(p.getInputPin()));
		p.setId("8");
		p.setNowState("visible"); 
		_panel.grid.addNewPiece(p);

		p = new Piece("INFO_INFO", _panel.act.getResources(), "Info", 1, 5, _panel.grid);
		p.addInputPin(new Pin(PinEnumerator.MULTI, PinParamEnumerator.OBJECT, "Signal"));
		p.addInputPin(new Pin(PinEnumerator.MULTI, PinParamEnumerator.OBJECT, "Signal"));
		p.addInputPin(new Pin(PinEnumerator.USER, PinParamEnumerator.STRING, "Text"));
		p.addOutputPin(new Pin(PinEnumerator.MULTI, PinParamEnumerator.OBJECT, "Signal"));
		p.setIconin("R.drawable.info48", R.drawable.info48);
		p.setInputPin(_panel.annotatePins(p.getInputPin()));
		p.setId("9");
		p.setNowState("visible"); 
		_panel.grid.addNewPiece(p);
	}

}
