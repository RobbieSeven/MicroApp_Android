package it.unisa.microapp.activities;

import it.unisa.microapp.R;
import it.unisa.microapp.data.DataType;
import it.unisa.microapp.data.ImageData;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class ImageFlipActivity extends MAActivity 
{
	private Bitmap image;
	private ImageView imgView;

	@Override
	protected void initialize(Bundle savedInstanceState) {
		

	}

	@Override
	protected void prepare() {
		

	}

	@Override
	protected int onVisible() {
		return R.layout.flipimage;
	}

	@Override
	protected View onVisibleView() {
		
		return null;
	}

	@Override
	protected void prepareView(View v) {
		Button b=(Button) this.findViewById(R.id.flip_vertical);
		b.setOnClickListener(new listener());
		b=(Button) this.findViewById(R.id.flip_horizontal);
		b.setOnClickListener(new listener());
		imgView=(ImageView) this.findViewById(R.id.flipimage);
		imgView.setImageBitmap(image);
	}
		
	@Override
	protected void execute() {
		

	}
	
	private class listener implements OnClickListener
	{

		@Override
		public void onClick(View v) 
		{
			Matrix matrix=new Matrix();
			if(v.getId() == R.id.flip_vertical)
			{
				matrix.setScale(1, -1);
				matrix.postTranslate(0, image.getHeight());

			}
			else
			{
				matrix.setScale(-1, 1);
				matrix.postTranslate(image.getWidth(),0);
				
			}
			
			Bitmap bit=Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, false);
			
			imgView.setImageBitmap(bit);
			imgView.invalidate();
			
			image=bit;
		}
		
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
		ImageData img=new ImageData(mycomponent.getId(), image);
		application.putData(mycomponent, img);
	}
	
	@Override
	protected void resume(){
		 //metodi per speech Vincenzo Savarese
	}
}
