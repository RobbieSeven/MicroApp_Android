package it.unisa.microapp.activities;

import it.unisa.microapp.utils.Constants;
import it.unisa.microapp.utils.Utils;
import android.os.Bundle;
import android.view.View;

public class AdmobAdviewActivity extends MAActivity {
	private boolean first = false;
	private boolean prevBanner = false;
	
	@Override
	protected void initialize(Bundle savedInstanceState) {		
		first=true;		
	}

	@Override
	protected void prepare() {
	}	
	
	@Override
	protected void prepareView(View v) {

	}	
	
	@Override
	protected void execute() {
	}	
	
	@Override
	public void restart() {
		first=false;		
	}
	
	@Override
	public void resume() {
		if(verificationCondition){
			if(first){
				prevBanner=banner;
				banner = true;
				next();
			}else{
				banner=prevBanner;
				previous();
			}
		}		
	}
				
	@Override
	public void initInputs() {
		for(String s : mycomponent.getUserData("adunitid")){
			adUnitId = s;
			
			Utils.verbose("Admob id:"+ adUnitId);
			break;
		}
		
		if(adUnitId.equals("default"))
		{
			adUnitId = Constants.admobId;
		}		
	}

	@Override
	public void beforeNext() {
	}

	@Override
	protected int onVisible() {
		return 0;
	}

	@Override
	protected View onVisibleView() {
		return null;
	}	
}
