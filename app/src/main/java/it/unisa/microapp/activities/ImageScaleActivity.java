package it.unisa.microapp.activities;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import it.unisa.microapp.R;
import it.unisa.microapp.data.DataType;
import it.unisa.microapp.data.ImageData;

public class ImageScaleActivity extends MAActivity implements OnTouchListener
{
	//private static final float ZOOMJUMP = 5f;

	private Bitmap image;
	private Bitmap retImage;
	private ImageView img;
	private float mOrigSeparation;
	//private boolean ignoreLastFinger = false;
	private float zoom;
	//private boolean zoomIn=false;
	
	@Override
	protected void initialize(Bundle savedInstanceState) {
		

	}

	@Override
	protected void prepare() {
		

	}

	@Override
	protected int onVisible() {
		return R.layout.imagescale;
	}

	@Override
	protected View onVisibleView() {
		
		return null;
	}
	
	@Override
	protected void prepareView(View v) {
		img=(ImageView) this.findViewById(R.id.scaleview);
		img.setImageBitmap(image);
		mOrigSeparation=100.0f;
		img.setOnTouchListener(this);
	}	
	
	@Override
	protected void execute() {
		

	}	
	
	@Override
	public void initInputs() 
	{
		ImageData img=(ImageData) application.getData(mycomponent.getId(), DataType.IMAGE).iterator().next();
		image=img.getSingleData();
	}

	@Override
	public void beforeNext()
	{
		ImageData img=new ImageData(mycomponent.getId(), retImage);
		application.putData(mycomponent, img);
	}


	@Override
	public boolean onTouch(View v, MotionEvent e) 
	{
		
		int action = e.getAction() & MotionEvent.ACTION_MASK;
		
		switch(action) 
		{
		case MotionEvent.ACTION_POINTER_DOWN:
		
			// We may be starting a new pinch so get ready
			mOrigSeparation = calculateSeparation(e);
		break;
		case MotionEvent.ACTION_POINTER_UP:
		
			// We're ending a pinch so prepare to
			// ignore the last finger while it's the
			// only one still down.
			
		break;
		case MotionEvent.ACTION_MOVE:
		// We're in a pinch so decide if we need to change
		// the zoom level.
		float newSeparation = calculateSeparation(e);
		/*
		if(newSeparation - mOrigSeparation > 0) 
		{
			// we got wider, zoom in
			mOrigSeparation = newSeparation;
		}
		else if (mOrigSeparation - newSeparation > 0) 
		{
			// we got narrower, zoom out
			
			mOrigSeparation = newSeparation;
		}
		*/
		zoom=newSeparation/mOrigSeparation;
		//mOrigSeparation = newSeparation;
		scaleImage(e);
		
		break;
		}

		return true;
	}
	
	private void scaleImage(MotionEvent event) 
	{
		Matrix matrix=new Matrix();
		
		float x = (event.getX(0) + event.getX(1))/2;
	    float y = (event.getY(0) + event.getY(1))/2;

	    matrix.postScale(zoom, zoom,x,y);
		
		Bitmap reDrawnBitmap=Bitmap.createBitmap(image,0,0,image.getWidth(),image.getHeight(),matrix,true);
	    img.setImageBitmap(reDrawnBitmap);
	    retImage=reDrawnBitmap;
	}


	private float calculateSeparation(MotionEvent e) 
	{
		float x = e.getX(0) - e.getX(1);
		float y = e.getY(0) - e.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}
	
	@Override
	protected void resume(){
		 //metodi per speech Vincenzo Savarese
	}
}
