package it.unisa.microapp.editor;


import it.unisa.microapp.utils.Constants;
import it.unisa.microapp.utils.Utils;

import java.util.Locale;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

public class SpeechProgressDialog extends ProgressDialog{

	private TextToSpeech tts;
	private Context context;
	private CharSequence message;
	private CharSequence title;

	public SpeechProgressDialog(Context context) {
		super(context);
		this.context=context;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		/*
		 * forse non instanziare il tts nel caso in cui lo speech è disattivo
		 */
		tts = new TextToSpeech(context,new ListenerTTS());//
	}
	
	@Override
	public void onAttachedToWindow(){
		super.onAttachedToWindow();
		if(OptionsActivity.getBoolean(Constants.CB_SPEECH_KEY, context)){//per disattivare o attivare speech
			Handler handler = new Handler(); 
			handler.postDelayed(new Runnable() { 
				public void run() { 
					tts.speak(title.toString()+" ... "+message.toString()+" ... ", TextToSpeech.QUEUE_FLUSH, null);
				} 
			}, 1000); 
		}
	}
	
	@Override
	public void onStop(){
		tts.stop();
		tts.shutdown();
		super.onStop();
		super.dismiss();	
	}
	
	@Override
	public void setMessage(CharSequence message){
		super.setMessage(message);
		this.message=message;
	}
	
	@Override
	public void setTitle(CharSequence title){
		super.setTitle(title);
		this.title=title;
	}
	
	
	class ListenerTTS implements OnInitListener{ /////////	@Override
		public void onInit(int status) {
			
			if(status==TextToSpeech.SUCCESS){
				int result = tts.setLanguage(Locale.getDefault());
				if(result==TextToSpeech.LANG_MISSING_DATA ||result==TextToSpeech.LANG_NOT_SUPPORTED  )
					Utils.debug("TTS non sopportato");
			}
		}
			
	}

	
}
