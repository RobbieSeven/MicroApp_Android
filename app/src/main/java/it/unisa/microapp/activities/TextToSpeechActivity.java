package it.unisa.microapp.activities;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import it.unisa.microapp.data.DataType;
import it.unisa.microapp.data.GenericData;
import it.unisa.microapp.data.StringData;
import it.unisa.microapp.utils.Utils;

public class TextToSpeechActivity extends MAActivity implements OnInitListener {
	private String message = "";
	private TextToSpeech tts;
	private ProgressDialog pDialog;

	protected void initialize(Bundle savedInstanceState) {
		tts = new TextToSpeech(this, this);
		//tts.setPitch(1.2f);
		tts.setSpeechRate(0.8f);
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
		

	}	


	@Override
	public void initInputs() {
		
		Iterator<GenericData<?>> im = application.getData(mycomponent.getId(), DataType.STRING).iterator();
		if (im.hasNext())
			message = (String) im.next().getSingleData();		
	}

	public void cleanUp() {
		// Don't forget to shutdown tts!
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
	}

	@Override
    public void destroy()
    {
		//super.onDestroy();
        if(tts != null){
            tts.shutdown();
        }       
    }	
	
	public void speaking() {		
		pDialog = new ProgressDialog(this);
		//pDialog.setTitle("Speaking"+getString(R.string.pleaseWait));
		pDialog.setMessage("Speaking");
		pDialog.setCancelable(false);
		pDialog.show();
	}	
	
	@Override
	public void onInit(int status) {
		
		if (status == TextToSpeech.SUCCESS) {

			int result = tts.setLanguage(Locale.getDefault());

			if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Utils.errorDialog(this, "This Language is not supported in TTS");
			} else {
				
		        tts.setOnUtteranceProgressListener(new UtteranceProgressListener()
		        {
		            @Override
		            public void onDone(String utteranceId)
		            {
						pDialog.dismiss();
		                next();
		                
		            }

		            @Override
		            public void onError(String utteranceId)
		            {
						pDialog.dismiss();
						next();
		            }

		            @Override
		            public void onStart(String utteranceId)
		            {
		            }
		        });				
				
				speakOut();
			}

		} else {
			// Utils.error("TTS Initilization Failed!");
		}
	}
	   
	private void speakOut() {
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utid");
		
		speaking();
		tts.speak(message, TextToSpeech.QUEUE_FLUSH, params);
	}

	@Override
	public void beforeNext() {
		
		cleanUp();
		
		StringData data = new StringData(this.mycomponent.getId(), message);
		this.application.putData(this.mycomponent, data);	
	}

	@Override
	protected void resume(){
		//metodi per speech Vincenzo Savarese
	}

}
