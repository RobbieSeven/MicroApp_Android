package it.unisa.microapp.activities;

import it.unisa.microapp.R;
import it.unisa.microapp.data.Contact;
import it.unisa.microapp.data.ContactData;
import it.unisa.microapp.data.DataType;
import it.unisa.microapp.data.GenericData;
import it.unisa.microapp.data.StringData;
import it.unisa.microapp.editor.OptionsActivity;
import it.unisa.microapp.utils.Constants;

import java.util.Iterator;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SendSmsMessageActivity extends MAActivity {
	private Contact con;
	private String message ="";

	@Override
	protected void initialize(Bundle savedInstanceState) {
	}

	@Override
	protected void prepare() {
		

	}

	@Override
	protected int onVisible() {
		return R.layout.sendsms;
	}

	@Override
	protected View onVisibleView() {
		
		return null;
	}

	@Override
	protected void prepareView(View v) {
		TextView tx1 = (TextView) findViewById(R.id.tx_label_cont);
		TextView tx2 = (TextView) findViewById(R.id.tx_label_cont2);
		ImageView iv = (ImageView) findViewById(R.id.picture);
		
		EditText et = (EditText) findViewById(R.id.tx_container);
		et.setKeyListener(null);

		Bitmap bm = con.getImg();
		if(bm != null)
		{
			iv.setImageBitmap(bm);
		}
		
		String s = con.getName();
		if (s != null) {
			tx1.setText(s);
		} else
			tx1.setText("");

		s = con.getPhone();
		if (s != null) {
			tx2.setText(s);
		} else
			tx2.setText("");

		et.setText(message);
	}	
	
	@Override
	protected void execute() {
		

	}	


	@Override
	public void initInputs() {
		Iterator<GenericData<?>> i = application.getData(mycomponent.getId(), DataType.CONTACT).iterator();
		if (i.hasNext())
			con = (Contact) i.next().getSingleData();

		//Iterator<GenericData<?>> im = application.getData(mycomponent.getId(), DataType.STRING).iterator();
		//while (im.hasNext())
		//	message = message.concat(" " + (String) im.next().getSingleData());
		// message= mycomponent.getUserData("message").iterator().next();
		// recupero messaggio da inviare da XML <userinput name="Message"/>
		
		Iterable<GenericData<?>> it = application.getData(mycomponent.getId(), DataType.STRING);
		if (it != null)
			for (GenericData<?> d : it) {
				StringData st = (StringData) d;

				for (String s : st.getData()) {
					message = message.concat(" " + s);
				}				
			}		
		
	}

	protected String getNextLabel() {
		return getString(R.string.send);
	}

	private void sendSMS() {
		String phoneNo = con.getPhone();

		if (phoneNo != null) {
			try {
				SmsManager smsManager = SmsManager.getDefault();
				if(!OptionsActivity.getBoolean(Constants.CB_SMS_KEY, this))
					smsManager.sendTextMessage(phoneNo, null, message, null,null);
				
				Toast.makeText(getApplicationContext(), "SMS Sent!", Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				
				Toast.makeText(getApplicationContext(), "SMS failed!", Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void beforeNext() {

		sendSMS();

		ContactData c = new ContactData(mycomponent.getId(), con);
		application.putData(mycomponent, c);

	}
	
	@Override
	protected void resume(){
		//metodi per speech Vincenzo Savarese
	}
	
}
