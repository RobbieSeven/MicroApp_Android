package it.unisa.microapp.test;

import it.unisa.microapp.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;

public class Agenda extends Activity{
	
public void displayContacts() {
		Uri lookupUri = Uri.withAppendedPath(Contacts.CONTENT_LOOKUP_URI, "1");
		ContentResolver cr = getContentResolver();
    	Cursor cur = getContentResolver().query(lookupUri, null, null, null, null);
        if (cur.getCount() > 0) {
        	while (cur.moveToNext()) {
        		String id = cur.getString(cur.getColumnIndex(Contacts._ID));
        		//String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        		if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(Contacts.HAS_PHONE_NUMBER))) > 0) {
                     Cursor pCur = cr.query(
                    		 ContactsContract.CommonDataKinds.Email.CONTENT_URI,  //email uri
                    		 null, 
                    		 ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",   //Clausola where
                    		 new String[]{id},   //Parametro
                    		 null);
                     while (pCur.moveToNext()) {
                    	 String phoneNo = pCur.getString(pCur.getColumnIndex(Contacts._ID));
         				new AlertDialog.Builder(this).setTitle("Contact Info").setMessage(phoneNo).setNeutralButton(android.R.string.ok, null).setIcon(R.drawable.contact48).show();  

                    	// String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.));
                    	// Toast.makeText(this, "Name: " + name + ", Phone No: " + phoneNo, Toast.LENGTH_SHORT).show();
                     } 
      	        pCur.close();
      	    }
        	}
        }
    }
}
