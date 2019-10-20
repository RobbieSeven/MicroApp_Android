package it.unisa.microapp.activities;

import java.util.ArrayList;

import it.unisa.microapp.data.Contact;
import it.unisa.microapp.data.ContactData;
import it.unisa.microapp.data.DataType;
import it.unisa.microapp.data.GenericData;
import it.unisa.microapp.data.ImageData;
import it.unisa.microapp.data.LocationData;
import it.unisa.microapp.data.StringData;
import it.unisa.microapp.editor.SpeechAlertDialog;
import it.unisa.microapp.utils.Utils;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class InformationInfoActivity extends MAActivity 
{
	private String message = "";
	ArrayList<Bitmap> images;
	
	@Override
	protected void initialize(Bundle savedInstanceState) {
		images = new ArrayList<Bitmap>();
	}

	@Override
	protected void prepare() {
		showMessage();

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
	public void initInputs() 
	{
		message=this.mycomponent.getUserData("text").iterator().next();

		images.clear();		
		Iterable<GenericData<?>> it = application.getData(mycomponent.getId(), DataType.STRING);
		if (it != null)
			for (GenericData<?> d : it) {
				StringData st = (StringData) d;

				for (String s : st.getData()) {
					message = message + "\n" + s;
				}				
			}
		
		it = application.getData(mycomponent.getId(), DataType.LOCATION);
		if (it != null)
			for (GenericData<?> d : it) {
				LocationData st = (LocationData) d;

				for (Location s : st.getData()) {
					message = message + "\n" + Utils.locationStringFromLocation(s).toString();
				}				
			}		
		
		it = application.getData(mycomponent.getId(), DataType.CONTACT);
		if (it != null)
			for (GenericData<?> d : it) {
				ContactData st = (ContactData) d;

				for (Contact s : st.getData()) {
					message = message + "\n" + s.toString();
				}				
			}			
		
		it = application.getData(mycomponent.getId(), DataType.IMAGE);
		if (it != null)
			for (GenericData<?> d : it) {
				ImageData st = (ImageData) d;

				for (Bitmap s : st.getData()) {
					images.add(s);
				}				
			}		
		
		
	}	
	

	private void showMessage() 
	{
		final ScrollView sview = new ScrollView(getApplicationContext());
		final TextView tview = new TextView(getApplicationContext());
		tview.setTextSize(18);
		tview.setText(message);

		final LinearLayout ll = new LinearLayout(getApplicationContext());
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.setGravity(Gravity.CENTER_HORIZONTAL);
		
		for(Bitmap image : images) {
						
			final ImageView iv = new ImageView(getApplicationContext());
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(300, 300);
			iv.setLayoutParams(layoutParams);			
			//iv.setImageBitmap(image);
			iv.setImageBitmap(Bitmap.createScaledBitmap(image, 300, 300, false));
			ll.addView(iv);		
		}  	

		ll.addView(tview);		
		sview.addView(ll);
		
		SpeechAlertDialog builder=new SpeechAlertDialog(this,InformationInfoActivity.this);
		builder.setTitle("Information");
		builder.setView(sview);
		
		builder.setButton(SpeechAlertDialog.BUTTON_POSITIVE,getResources().getString(android.R.string.ok), new OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				next();
			}
			
		});
		
		builder.show();
	}


	@Override
	public void beforeNext() 
	{
		for (DataType dt : DataType.values()) {
			Iterable<GenericData<?>> dit = application.getData(mycomponent.getId(), dt);
			
			if(dit != null)
			{
				for(GenericData<?> d : dit)
					application.putDataInObject(mycomponent, d);
			}
		}
	}
	
	@Override
	protected void resume(){
		 //metodi per speech Vincenzo Savarese
	}
}
