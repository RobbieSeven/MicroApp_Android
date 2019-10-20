package it.unisa.microapp.activities;

import it.unisa.microapp.data.UriData;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class MediaStaticVideoActivity extends MAActivity {

    private Uri aUri;
	private boolean first = false;
		
	@Override
	protected void initialize(Bundle savedInstanceState) {
		

	}

	@Override
	protected void prepare() {
		first=true;
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
		

	}	

	
	@Override
	public void restart() {
		//super.onRestart();
		first=false;
	}
	
	@Override
	public void resume() {
		//super.onResume();
		 //metodi per speech Vincenzo Savarese
		if(verificationCondition){
			if(first){
				next();
			}else{
				previous();
			}
		}
	}
	
	private void getVideo() {
		for(String s : mycomponent.getUserData("video")){
			aUri= Uri.parse(s);
			break;
		}		
	}
		
		@Override
		public void initInputs() {
		}

		@Override
		public void beforeNext() {
			getVideo();
			UriData c = new UriData(mycomponent.getId(), aUri);
			application.putData(mycomponent, c);
		}
}
