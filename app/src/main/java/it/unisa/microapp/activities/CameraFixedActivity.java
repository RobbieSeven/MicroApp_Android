package it.unisa.microapp.activities;

import java.io.IOException;

import it.unisa.microapp.data.ImageData;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

public class CameraFixedActivity extends MAActivity {

	private Bitmap bm;
	private ImageData image;

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
	public void initInputs() {
		String suri = this.mycomponent.getUserData("imageid").iterator().next();
		try {
			Uri ui = Uri.parse(suri);			
			bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), ui);
		} catch (IOException e) {
		}
	}

	@Override
	public void beforeNext() {
		if (bm != null) {
			image = new ImageData(mycomponent.getId(), bm);
			application.putData(mycomponent, image);
		}

	}
	
	@Override
	protected void resume(){
		
	}
}
