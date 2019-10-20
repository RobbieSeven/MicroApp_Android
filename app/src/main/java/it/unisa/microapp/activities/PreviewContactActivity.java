package it.unisa.microapp.activities;

import java.util.Iterator;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import it.unisa.microapp.R;
import it.unisa.microapp.data.Contact;
import it.unisa.microapp.data.ContactData;
import it.unisa.microapp.data.DataType;
import it.unisa.microapp.data.GenericData;
import it.unisa.microapp.utils.Utils;

public class PreviewContactActivity extends MAActivity {

	private Contact con;
	
	@Override
	protected void initialize(Bundle savedInstanceState) {
		

	}

	@Override
	protected void prepare() {
		

	}

	@Override
	protected int onVisible() {
		return R.layout.contactpreview;
	}

	@Override
	protected View onVisibleView() {
		
		return null;
	}

	@Override
	protected void prepareView(View v) {
		ImageView im=(ImageView) findViewById(R.id.CallButton);
		TextView txnome=(TextView) findViewById(R.id.txNome);
		TextView txnumero=(TextView) findViewById(R.id.txNumero);
		TextView txmail=(TextView) findViewById(R.id.txMail);
		
		Bitmap bm = con.getImg();
		if(bm != null && (bm.getHeight() > 1 && bm.getWidth() > 1))
		    im.setImageBitmap(bm);
		
		txnome.setText(con.getName());
		String phones ="";
		for(String s : con.getPhones()) {
			phones = phones + s + "\n";
		}		
		txnumero.setText(phones);
		
		String mails ="";
		for(String s : con.getMails()) {
			mails = mails + s + "\n";
		}
		txmail.setText(mails);
		
		im.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				next();
				
			}
		});
	}	
	
	@Override
	protected void execute() {
		Utils.debug("si sta in execute");
	}	
	
	
	@Override
	public void initInputs() {
		Iterator<GenericData<?>> i=application.getData(mycomponent.getId(), DataType.CONTACT).iterator();
		 if (i.hasNext())
			con=(Contact) i.next().getSingleData();
		Utils.debug("preview: "+con.getPhone());
	}

	@Override
	public void beforeNext() {
		ContactData c=new ContactData(mycomponent.getId(),con);
		application.putData(mycomponent, c);

	}
	
	@Override
	protected void resume(){
		//metodi per speech Vincenzo Savarese
	}
}
