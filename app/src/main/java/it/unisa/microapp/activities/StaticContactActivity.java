package it.unisa.microapp.activities;

import it.unisa.microapp.data.Contact;
import it.unisa.microapp.data.ContactData;
import it.unisa.microapp.utils.Utils;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.view.View;

public class StaticContactActivity extends MAActivity {
	private Contact myContact=new Contact();
	
	@Override
	protected void initialize(Bundle savedInstanceState) {
		

	}

	@Override
	protected void prepare() {
		

	}

	@Override
	protected int onVisible() {
		return 0;
	}

	@Override
	protected View onVisibleView() {
		
		return null;
	}

	@Override
	protected void prepareView(View v) {
		

	}	
	
	@Override
	protected void execute() {
		next();
	}	
		
	@Override
	public void restart() {
		//super.onRestart();
		previous();
	}
	
	@Override
	public void initInputs() {
	}

	@Override
	public void beforeNext() {
		getContact();
		ContactData c=new ContactData(mycomponent.getId(),myContact);
		application.putData(mycomponent, c);
	}
	
	private void getContact() 
	{
		myContact=new Contact();
		for (String lookID : mycomponent.getUserData("contactid")){
		Uri lookupUri = Uri.withAppendedPath(Contacts.CONTENT_LOOKUP_URI, lookID);
		myContact.load(StaticContactActivity.this.getApplicationContext(), lookupUri);
		/*
		
		myContact.setLookUpURI(lookupUri);

		ContentResolver cr= getContentResolver();
    	Cursor cur = cr.query(lookupUri, new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME}, null, null, null);
        if (cur.getCount() > 0) {
        	while (cur.moveToNext()) {
        		String id = cur.getString(0);
        		Uri cntactPhotoUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, Long.parseLong(id));
        		InputStream photo=ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), cntactPhotoUri);
        		Bitmap b=BitmapFactory.decodeStream(photo);
        		myContact.setImg(b);
        		Cursor pCur = cr.query(
                    		ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    		 null, 
                    		 ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",   //Clausola where
                    		 new String[]{id},   //Parametro
                    		 null);
        		
        		 if (pCur.moveToNext()) { 
                	 String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String name=pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                	 myContact.setPhone(phone);
                	 myContact.setName(name);
                	 myContact.setLookUpURI(lookupUri);
                	 
        		 } 
 
      	        pCur.close();
      	    }
        	}
        
        lookupUri = Uri.withAppendedPath(Contacts.CONTENT_LOOKUP_URI, lookID);
		cr = getContentResolver();
		cur = cr.query(lookupUri, new String[] { ContactsContract.Contacts._ID,
				ContactsContract.Contacts.DISPLAY_NAME }, null, null, null);
		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				String id = cur.getString(0);
				Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, // email
																							// uri
						null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", // Clausola
																							// where
						new String[] { id }, // Parametro
						null);
				while (pCur.moveToNext()) {
					String email = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA1));
					myContact.setMail(email);
				}
			}
		}
	*/	
   	 Utils.verbose(myContact.toString());
        break; //VOGLIO FARE UN SOLO CICLO
		}

	}
	
	@Override
	protected void resume(){
		//metodi per speech Vincenzo Savarese
	}
}
