package it.unisa.microapp.activities;

import java.util.ArrayList;

import it.unisa.microapp.R;
import it.unisa.microapp.data.*;
import it.unisa.microapp.support.GenericSaver;
import it.unisa.microapp.utils.Utils;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class CameraSaveActivity extends MAActivity {

	ArrayList<Bitmap> images;
	Iterable<GenericData<?>> itData;
	
	@Override
	protected void initialize(Bundle savedInstanceState) {
		images = new ArrayList<Bitmap>();

	}

	@Override
	protected void prepare() {
	}	
	
	@Override
	protected void prepareView(View v) {
		final LinearLayout ll = (LinearLayout) this.findViewById(R.id.layout);
		
		for(Bitmap bm : images) {
			final ImageView iv = new ImageView(getApplicationContext());
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(300, 300);
			iv.setLayoutParams(layoutParams);
			//iv.setImageBitmap(bm);
			iv.setImageBitmap(Bitmap.createScaledBitmap(bm, 300, 300, false));
			
			ll.addView(iv);						
		}
	}	
	
	@Override
	protected int onVisible() {
		return R.layout.save;
	}	
	
	@Override
	protected View onVisibleView() {
		
		return null;
	}	

	@Override
	protected void execute() {
		if (itData != null)
			for (GenericData<?> d : itData) {
				ImageData st = (ImageData) d;

				GenericSaver.saveBitmap(getContentResolver(), st);
				//application.putData(mycomponent, st);
				
				//images.add(st.getSingleData());
			}

	}
	
	@Override
	public void initInputs() {

		images.clear();
		itData = application.getData(mycomponent.getId(), DataType.IMAGE);
		if (itData != null)
			for (GenericData<?> d : itData) {
				ImageData st = (ImageData) d;

				//GenericSaver.saveBitmap(getContentResolver(), st);
				//application.putData(mycomponent, st);
				
				images.add(st.getSingleData());
			}

	}

	@Override
	public void beforeNext() {
		if (itData != null)
			for (GenericData<?> d : itData) {
				ImageData st = (ImageData) d;

				application.putData(mycomponent, st);
				
			}
	}

	
	@Override
	protected void onHidden() { 
		execute();
		next();
	}

	@Override
	protected String getProgressTitle() {
		return "Sto salvando";
	}
	@Override
	protected void onProgress(ProgressDialog d) { 
		if(d != null) {
			d.setIndeterminate(true);
		}
		Utils.delay(8000);
		if (itData != null)
			for (GenericData<?> dt : itData) {
				ImageData st = (ImageData) dt;

				GenericSaver.saveBitmap(getContentResolver(), st);
			}
		next();
	}
	
	@Override
	protected void resume(){
		
	}
}
