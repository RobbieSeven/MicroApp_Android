package it.unisa.microapp.store;

import it.unisa.microapp.R;

import java.io.ByteArrayInputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class ZoomActivity extends Activity{

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zoomimage);
		
		Intent intent = getIntent();
		
		byte [] b = intent.getByteArrayExtra("img");
		ByteArrayInputStream ins = new ByteArrayInputStream(b);
		Bitmap bmp = BitmapFactory.decodeStream(ins);
		
		ImageView image = (ImageView)findViewById(R.id.imagezoom);
		image.setImageBitmap(bmp);
	}

}
