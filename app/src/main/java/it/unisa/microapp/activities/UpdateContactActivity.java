package it.unisa.microapp.activities;

import java.util.Iterator;

import it.unisa.microapp.data.Contact;
import it.unisa.microapp.data.ContactData;
import it.unisa.microapp.data.DataType;
import it.unisa.microapp.data.GenericData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.view.View;

public class UpdateContactActivity extends MAActivity {
	private Contact con;
	private boolean first = false;	

	protected void initialize(Bundle savedInstanceState) {
		

	}

	@Override
	protected void prepare() {
		first=true;

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
		

	}	


	@Override
	public void start() {
		//super.onStart();
		if(verificationCondition){
			if(first){
				update();
				first=false;
			} 
		}
	}
	
	@Override
	public void stop() {
		//super.onStop();
		//first=true;
	}
	
	@Override
	public void initInputs() {
		Iterator<GenericData<?>> i = application.getData(mycomponent.getId(), DataType.CONTACT).iterator();
		if (i.hasNext())
			con = (Contact) i.next().getSingleData();
	}

	private void update() {
		Intent editIntent = new Intent(Intent.ACTION_EDIT);
		Uri uri = con.getLookUpURI();
		editIntent.setDataAndType(uri, Contacts.CONTENT_ITEM_TYPE);

		editIntent.putExtra("finishActivityOnSaveCompleted", true);

		// Sends the Intent
		startActivityForResult(editIntent, 1);
		//startActivity(editIntent);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		  if (requestCode == 1) {

		     if(resultCode == RESULT_OK){    
						con.load(UpdateContactActivity.this.getApplicationContext(), data.getData());
					     first=false;
					     next();

		     }
		     if (resultCode == RESULT_CANCELED) { 
		    	 previous();
		     }
		  } 
		}//onActivityResult	
	
	@Override
	public void beforeNext() {

		ContactData c = new ContactData(mycomponent.getId(), con);
		application.putData(mycomponent, c);
	}
	
	@Override
	protected void resume(){
		//metodi per speech Vincenzo Savarese
	}

}
