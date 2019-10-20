package it.unisa.microapp.activities;

import java.util.Iterator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import android.view.View;
import android.widget.Toast;

import it.unisa.microapp.data.Contact;
import it.unisa.microapp.data.ContactData;
import it.unisa.microapp.data.DataType;
import it.unisa.microapp.data.GenericData;
import it.unisa.microapp.utils.Utils;

public class CallActivity extends MAActivity {
	private Contact con;
	private String callUri;
	private String text = "";
	private boolean ended, controlpause = false;
	
	@SuppressWarnings("unused")
	private class EndCallListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			if (TelephonyManager.CALL_STATE_RINGING == state) {
				// wait for phone to go offhook (probably set a boolean flag) so
				// you know your app initiated the call.
				text += "\nRing";
			}
			if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
				// wait for phone to go offhook (probably set a boolean flag) so
				// you know your app initiated the call.
				// text+="\nHookoff";
				// hook=true;
			}
			if (TelephonyManager.CALL_STATE_IDLE == state) {
				// text+="\nIdle";

				// if (hook)
				// ended=true;
			}

		}
	}

	
	@Override
	public void initialize(Bundle savedInstanceState) {
		

	}	
	
	@Override
	protected void prepare() {
		
	}	
	
	@Override
	protected void prepareView(View v) {
		

	}		
	
	@Override
	protected void execute() {
		if (callUri != null) {
			Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(callUri));
			startActivity(callIntent);
			controlpause = true;
		}

	}	
	
	@Override	
	public void resume() {
		if (ended && controlpause) {
			next();
			return;
		}
	}

	@Override
	public void initInputs() {
		Iterator<GenericData<?>> i=application.getData(mycomponent.getId(), DataType.CONTACT).iterator();
		if (i.hasNext())
			con=(Contact) i.next().getSingleData();
		Utils.debug("preview: " + con.getPhone());
		callUri="tel:" + con.getPhone();

		/*ContactData c = null;
		Iterator<GenericData<?>> i = application.getData(mycomponent.getId(), DataType.CONTACT).iterator();
		if (i.hasNext())
			c = (ContactData) i.next();

		callUri = "tel:" + c.getSingleData().getPhone();*/
		
	}

	@Override
	public void beforeNext() {
		ContactData c=new ContactData(mycomponent.getId(),con);
		application.putData(mycomponent, c);
	}

	@Override	
	public void pause() {
		ended = true;
	}
		
	@Override
	protected int onVisible() {
		
		return 0;
		
	}

	@Override
	protected View onVisibleView() {
		
		return null;
	}	
}
