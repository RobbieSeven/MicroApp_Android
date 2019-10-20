package it.unisa.microapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import it.unisa.microapp.R;
import it.unisa.microapp.data.DataType;
import it.unisa.microapp.data.GenericData;

public class MediaPlaySoundActivity extends MAActivity {

	@Override
	protected void initialize(Bundle savedInstanceState) {
		

	}

	@Override
	protected void prepare() {
		

	}

	@Override
	protected int onVisible() {
		return R.layout.playsound;
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
		getSound();
	}	

	 public void doClick(View view) {
	    	int id = view.getId();
			if (id == R.id.button_list) {
				getSound();
			}
	    }
	
	private void getSound() {
		Intent intent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN,Intent.CATEGORY_APP_MUSIC);
		startActivity(intent);
	}


	public void initInputs() {

	}

	@Override
	public void beforeNext() {
		Iterable<GenericData<?>> it = application.getData(mycomponent.getId(), DataType.OBJECT);
		if(it != null)
		{
			for(GenericData<?> d : it)
				application.putData(mycomponent, d);
		}
	}
	
	@Override
	protected void resume(){
		 //metodi per speech Vincenzo Savarese
	}
}
