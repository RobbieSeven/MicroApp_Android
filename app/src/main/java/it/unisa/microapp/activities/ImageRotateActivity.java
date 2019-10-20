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

public class ImageRotateActivity extends MAActivity implements OnTouchListener
{
	//private static final int SOGLIA=5;//5 pixel
	private Bitmap image;
	private Bitmap retImage;
	private ImageView img;
	
	@Override
	protected void initialize(Bundle savedInstanceState) {
		

	}

	@Override
	protected void prepare() {
		

	}

	@Override
	protected int onVisible() {
		return R.layout.rotateimage;
	}

	@Override
	protected View onVisibleView() {
		
		return null;
	}

	@Override
	protected void prepareView(View v) {
		img=(ImageView) this.findViewById(R.id.rotateimage1);
		img.setOnTouchListener(this);
		img.setDrawingCacheEnabled(true);
		img.setImageBitmap(image);
	}	

	@Override
	protected void execute() {
		

	}
	
	public boolean onTouch(View view, MotionEvent event)
	{
		
	    if(image == null)
	    	image=img.getDrawingCache();
	    
	    switch (event.getAction()) {
	        case MotionEvent.ACTION_DOWN:
	            break;
	        case MotionEvent.ACTION_MOVE:
	        	double r=Math.atan2(event.getX()-(img.getWidth()/2), (img.getHeight()/2)-event.getY());
	    	    int rotation=(int)Math.toDegrees(r);
	            //x=event.getX();
	            //y=event.getY();
	            updateRotation(rotation);
	            break;
	        case MotionEvent.ACTION_UP:
	            break;
	    }//switch       

	    return true;

	}
	
	private void updateRotation(double rot){
	    float newRot= Float.valueOf((float)rot);
	    Matrix matrix=new Matrix();
	    matrix.postRotate(newRot,image.getWidth()/2,image.getHeight()/2);
	    Bitmap reDrawnBitmap=Bitmap.createBitmap(image,0,0,image.getWidth(),image.getHeight(),matrix,true);
	    img.setImageBitmap(reDrawnBitmap);
	    retImage=reDrawnBitmap;
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
	protected void resume(){
		 //metodi per speech Vincenzo Savarese
	}
}
