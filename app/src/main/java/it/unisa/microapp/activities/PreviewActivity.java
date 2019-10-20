package it.unisa.microapp.activities;

import java.util.Iterator;

import it.unisa.microapp.R;
import it.unisa.microapp.data.DataType;
import it.unisa.microapp.data.GenericData;
import it.unisa.microapp.data.ImageData;
import it.unisa.microapp.utils.Utils;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PreviewActivity extends MAActivity {

	private Bitmap bm;

	
	@Override
	public void initialize(Bundle savedInstanceState) {
		bm = null;
	}
	
	@Override
	public void prepare() {		
	}	
	
	@Override
	protected int onVisible() {
		return R.layout.prewiew;
	}	
	
	@Override
	protected View onVisibleView() {
		
		return null;
	}
	
	@Override
	public void prepareView(View v) {
		if(bm != null) {
			ImageView im=(ImageView)this.findViewById(R.id.pre_img);
			//im.setImageBitmap(bm);
			im.setImageBitmap(Bitmap.createScaledBitmap(bm, 300, 350, false));
		}
		TextView tw=(TextView) this.findViewById(R.id.pre_text);
		tw.setTextSize(18);
	}
	
	@Override
	protected void execute() {
		
	}	
	
	public void initInputs() {
		Iterator<GenericData<?>> i=application.getData(mycomponent.getId(), DataType.IMAGE).iterator();

		if (i!=null && i.hasNext())
			bm=(Bitmap) i.next().getSingleData(); //Voglio recuperare un elemento da un solo input
	}


	@Override
	public void beforeNext() {
		ImageData image=new ImageData(mycomponent.getId(),bm);
		application.putData(mycomponent, image);		
	}

	
	public void onHidden() { 
		next();
	}

	@Override
	protected void resume(){
		//metodi per speech
	}
}
