package it.unisa.microapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import it.unisa.microapp.data.UriData;
import it.unisa.microapp.utils.Constants;

public class MediaSelectAudioActivity extends MAActivity {

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
	public void resume() {
		//super.onResume();
		 //metodi per speech Vincenzo Savarese
		if(verificationCondition){
			if(first){
				getAudio();
			}
		}
	}
	@Override
	public void stop() {
		//super.onStop();
		first=true;
	}
	
	private void getAudio() {
		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
		this.startActivityForResult(intent, Constants.ID_SELECT_AUDIO);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == Constants.ID_SELECT_AUDIO) 
		{
			if(resultCode == 0)
			{
				previous();
			}else{
				aUri = data.getData();
				first=false;
				next();
			}
		}
		
	}

	public void initInputs() {

	}

	@Override
	public void beforeNext() {
		UriData c = new UriData(mycomponent.getId(), aUri);
		application.putData(mycomponent, c);
	}
}
