package it.unisa.microapp.activities;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class EndCallListener extends PhoneStateListener {
    
private MAActivity act;
private int counter=0;
	   
	   public EndCallListener(MAActivity act){
		   this.act=act;
	   }
	    
	    @Override
	    public void onCallStateChanged(int state, String incomingNumber) {
	    	if(TelephonyManager.CALL_STATE_RINGING == state) {
	            //wait for phone to go offhook (probably set a boolean flag) so you know your app initiated the call.

		      }
	       if(TelephonyManager.CALL_STATE_OFFHOOK == state) {
	            //wait for phone to go offhook (probably set a boolean flag) so you know your app initiated the call.
	    	 //  text+="\nHookoff";
	    	   //hook=true;
	       }
	        if(TelephonyManager.CALL_STATE_IDLE == state) {
	        		counter++;
	        		if (counter==0) act.next();
	        	}
	        
	    }
	}
