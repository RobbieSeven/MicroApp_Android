package it.unisa.microapp.activities;

import android.os.Bundle;
import android.view.View;
import it.unisa.microapp.data.DataType;
import it.unisa.microapp.data.GenericData;
import it.unisa.microapp.utils.Utils;

public class InformationVibrateActivity extends MAActivity 
{
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
		Utils.vibrate(this);
		next();
	}	
	
	@Override
	public void initInputs() {

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
