package it.unisa.microapp.activities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.unisa.microapp.R;
import it.unisa.microapp.data.ComplexDataType;
import it.unisa.microapp.data.Contact;
import it.unisa.microapp.data.DataType;
import it.unisa.microapp.data.GenericData;
import it.unisa.microapp.data.ImageData;
import it.unisa.microapp.editor.OptionsActivity;
import it.unisa.microapp.support.GenericSaver;
import it.unisa.microapp.utils.Constants;
import it.unisa.microapp.utils.GMailSender;
import android.app.AlertDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SendStaticMailActivity extends MAActivity {
	private ArrayList<Uri> uris = new ArrayList<Uri>();
	private ArrayList<String> sendby = new ArrayList<String>();
	private String _subject = "";
	private String _body = "";
	private String _header = "";
	private boolean plain = true;

	@Override
	protected void initialize(Bundle savedInstanceState) {
		

	}

	@Override
	protected void prepare() {
		

	}

	@Override
	protected int onVisible() {
		return R.layout.mailpreview;
	}

	@Override
	protected View onVisibleView() {
		
		return null;
	}

	@Override
	protected void prepareView(View v) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();	
		StrictMode.setThreadPolicy(policy); 
		
		
		TextView tx = (TextView) findViewById(R.id.emailaddress);
		TextView att = (TextView) findViewById(R.id.txallegati);
		Button btn = (Button) findViewById(R.id.SendButton);

		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				boolean typeMail = OptionsActivity.getBoolean(Constants.CB_EMAILPROVIDER_KEY, SendStaticMailActivity.this.getApplication());
				
				if(typeMail)
					sendMailWithoutIntent();
				else 
					sendMail();
			}
		});

		EditText subject = (EditText) this.findViewById(R.id.mailsubject);
		subject.setText(_subject);


		for (String s : sendby) {
			tx.append(s + ";\n");
		}
		for (Uri u : uris) {
			att.append(u + "\n");
		}

		EditText body = (EditText) this.findViewById(R.id.mailbody);		
		body.setText(_body);
	}	
	
	@Override
	protected void execute() {
		

	}	

	private void sendMail() {

		Intent emailIntent = new Intent(Intent.ACTION_SEND);

		if(sendby.size() == 0)
		{
			 AlertDialog.Builder builder = new AlertDialog.Builder(this);
			 builder.setMessage("Invalid mail address")
			 .setTitle(R.string.error)
			 .setCancelable(false) 
			 .setPositiveButton(android.R.string.ok, null);
			 AlertDialog alert = builder.create(); 
			 alert.show();
			 return;
		}		
		
		String[] stockArr = new String[sendby.size()];
		stockArr = sendby.toArray(stockArr);

		emailIntent.putExtra(Intent.EXTRA_EMAIL, stockArr);
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, _subject);
		emailIntent.putExtra(Intent.EXTRA_TEXT, _body);

		if (plain)
			emailIntent.setType("text/plain");
		else
			emailIntent.setType("text/html");

		for (Uri u : uris) {
			emailIntent.putExtra(Intent.EXTRA_STREAM, u);
		}
		startActivityForResult(Intent.createChooser(emailIntent, "Send email..."), 1);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		  if (requestCode == 1) {

		     if(resultCode == RESULT_OK){      
		     }
		     if (resultCode == RESULT_CANCELED) {    
		     }
		     
		     next();
		  }
		}//onActivityResult	
	
	
	private void sendMailWithoutIntent() {
		
		String mail = OptionsActivity.getString(Constants.TEXT_EMAILPROVIDER_KEY, this);
		String password = OptionsActivity.getString(Constants.TEXT_EMAILPASSWORD_KEY, this);
		GMailSender m = new GMailSender(mail, password);
		
		
		String[] stockArr = new String[sendby.size()];
		stockArr = sendby.toArray(stockArr);
		
		m.setTo(stockArr);
		m.setFrom("michele.risi@gmail.com");
		m.setSubject(_subject);
		m.setBody(_body);

		try {
			
		    for(Uri u: uris) {
		    	String path = u.getPath();
				if(path != null) 
					m.addAttachment(path);	
		    }
			
			if (m.send()) {
				Toast.makeText(this, "Email was sent successfully.", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, "Email was not sent.", Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			Toast.makeText(this, "Email was not sent.", Toast.LENGTH_LONG).show();
		}
		
		next();
	}
	
	@Override
	public void initInputs() {

		// Gestisco piu' input da diverse componenti ma questi input non sono multipli

		// ricavo il subject e il body
		_subject = this.mycomponent.getUserData("subject").iterator().next();
		_body = this.mycomponent.getUserData("body").iterator().next();
		
		Iterable<GenericData<?>> i = application.getData(mycomponent.getId(), DataType.LOCATION);
		if (i != null) {
			for (GenericData<?> d : i) {
				 Location loc = (Location) d.getSingleData();
				  _header += localityByLocation(loc.getLatitude(), loc.getLongitude()) + "\n";
			}
		}
		
		i = application.getData(mycomponent.getId(), DataType.STRING);
		if (i != null) {
			for (GenericData<?> d : i) {
				 String loc = (String) d.getSingleData();
				  _header += loc + "\n";
			}
		}		

		// ricavo la mail dal contatto
		i = application.getData(mycomponent.getId(), DataType.CONTACT);

		if (i != null) {
			for (GenericData<?> d : i) {
				Contact con = (Contact) d.getSingleData();
				List<String> mails = con.getMails();

				for (String s : mails)
					sendby.add(s);
			}
		}
		
		// Allegati
		gestisciAllegati();
		
		if(!_header.equals(""))
			_body = _header + "\n\n"+ _body; 

	}

	private String localityByLocation(double geoLat, double geoLng) {
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		List<Address> addresses = null;
		try {
			addresses = geocoder.getFromLocation(geoLat, geoLng, 1);
		} catch (IOException e) {
		}

		return addresses.get(0).getLocality();
	}	
	
	private void gestisciAllegati() {
		// TODO:gestisco eventuali Immagini
		Iterable<GenericData<?>> i = application.getData(mycomponent.getId(), DataType.IMAGE);
		addAttach(i);

		// TODO:gestisco objects:i complexData vengono salvati su un file xml o
		// html "da decidere quale formato" tramite la classe GenericSaver
		
		i = application.getData(mycomponent.getId(), DataType.OBJECT);
		addAttach(i);
	}

	private void addAttach(Iterable<GenericData<?>> i) {
		if (i != null) {
			for (GenericData<?> img : i) {
				File f = img.getFile();

				if (img instanceof ComplexDataType) {
					if (f == null) {
						// salvo come documento Html valore false
						// se si vuole salvare come xml valore true
						GenericSaver.saveObject(img, false);
						f = img.getFile();
					}
				}
				if (img instanceof ImageData) {
					if (f == null) {
						GenericSaver.saveBitmap(getContentResolver(), (ImageData)img);
						GenericSaver.saveObject(img, false);
						f = img.getFile();
					}
				}
				try {
					Uri v = Uri.fromFile(f);
					uris.add(v);
				} catch (Exception e) {
				}
			}
		}
	}

	public void beforeNext() {

	}
	
	@Override
	protected void resume(){
		//metodi per speech Vincenzo Savarese
	}
}
