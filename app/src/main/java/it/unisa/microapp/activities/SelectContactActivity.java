package it.unisa.microapp.activities;

import it.unisa.microapp.data.Contact;
import it.unisa.microapp.data.ContactData;
import it.unisa.microapp.utils.Constants;
import it.unisa.microapp.utils.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;

public class SelectContactActivity extends MAActivity {
	private Contact myContact;
	private boolean first = false;

	@Override
	protected void initialize(Bundle savedInstanceState) {
		myContact = new Contact();
	}

	@Override
	protected void prepare() {
		first = true;

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
		// super.onStart();
		if (verificationCondition) {
			if (first) {
				getContact();
			}
		}
	}

	@Override
	public void stop() {
		// super.onStop();
		first = true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == Constants.ID_RESULT_CONTACT) {
			if (resultCode == 0) {
				previous();
			} else {

				myContact.load(SelectContactActivity.this.getApplicationContext(), data.getData());
				Utils.verbose(myContact.toString());
				first = false;
				next();
			}
		}

	}

	@Override
	public void initInputs() {

	}

	private void getContact() {
		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		this.startActivityForResult(intent, Constants.ID_RESULT_CONTACT);
	}

	@Override
	public void beforeNext() {

		ContactData c = new ContactData(mycomponent.getId(), myContact);
		application.putData(mycomponent, c);
	}

	@Override
	protected void resume() {
		// metodi per speech Vincenzo Savarese
	}
}
