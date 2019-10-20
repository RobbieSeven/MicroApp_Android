package it.unisa.microapp.editor;

import it.unisa.microapp.utils.Constants;
import it.unisa.microapp.utils.Utils;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Stack;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

public class SpeechDatePickerDialog extends DatePickerDialog{

	private Context context;
	private SpeechRecognizer sr;
	private Intent intent;
	private Activity mainActivity;
	private TextToSpeech tts;
	private String tipoListenerRecognition=null;
	private HashMap<String, String> params;
	private CharSequence title;
	private Stack<View> stackPrecedenti;
	private Stack<View> stackSuccessivi;
	private String richiestaBottoni="";
	public boolean modalitaComando=false;
	
	public SpeechDatePickerDialog(Context context,Activity activity, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
		super(context, callBack, year, monthOfYear, dayOfMonth);
		this.context=context;
		this.mainActivity=activity;
		sr = SpeechRecognizer.createSpeechRecognizer(context);
		intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "it.unisa.microapp.activities");
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		/*
		 * forse non far partire neanche l'istanceTTS nel caso in cui lo speech è disattivo
		 */
		this.istanceTTS();
	}


	public void istanceTTS(){
		tts = new TextToSpeech(context,new ListenerRecognitionButton());//
		tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {

	        @Override
	        public void onStart(String utteranceId) {
	        }

	        @Override
	        public void onError(String utteranceId) {
	        }
	        

	        @Override
	        public void onDone(String utteranceId) {
	            mainActivity.runOnUiThread(new Runnable() {
					@Override
	                public void run() {
	                	if(tipoListenerRecognition==null){
		                	speakSpeechLayout();
	                	}
	                	else if(tipoListenerRecognition.equalsIgnoreCase("ListenerRecognitionPicker")){
	                		sr.setRecognitionListener(new ListenerRecognitionPicker());
	    					sr.startListening(intent);
	                	}
	                	else if(tipoListenerRecognition.equalsIgnoreCase("ListenerRecognitionButton")){
	                		sr.setRecognitionListener(new ListenerRecognitionButton());
	        				sr.startListening(intent);
	                	}else{
	                	}
	                }    	 
	           });
	            
	        }
	    });//
		params = new HashMap<String, String>();
	    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"utterance");

	}
	
	@Override
	public void setTitle(CharSequence title){
		super.setTitle(title);
		this.title=title;
	}
	
	@Override
	public void setTitle(int idTitle){
		super.setTitle(title);
		this.title=context.getResources().getString(idTitle);
	}
	
	@Override
	public void onStart(){
		super.onStart();
		this.getStructureLayout();
	}
	
	@Override
	public void onStop(){
		this.shutdown();
		super.onStop();
		super.dismiss();
		
	}
	
	@Override
	public void onAttachedToWindow(){
		super.onAttachedToWindow();
		if(OptionsActivity.getBoolean(Constants.CB_SPEECH_KEY, context)){//per disattivare o attivare speech{
			Handler handler = new Handler(); 
			handler.postDelayed(new Runnable() { 
				public void run() { 
					speakSpeechLayout();
				}	 
			}, 1000); 
		}
	}

	public void getStructureLayout(){ ////////////////////
		stackPrecedenti = new Stack<View>();
		stackSuccessivi = new Stack<View>();
		String[] field = {"Year", "Month","Day"};
		for(int i=0;i<3;i++){
			TextView view = new TextView(context);
			view.setText(field[i]);
			stackSuccessivi.push(view);
		}
		if(!title.equals("")){
			TextView viewMessage = new TextView(context);
			viewMessage.setText(title);
			stackSuccessivi.push(viewMessage);
		}
	}
	
	public void speakSpeechLayout(){
		View currentView=null;
		String typeView="";
		while(!stackSuccessivi.isEmpty()){
			currentView=stackSuccessivi.peek();
			stackPrecedenti.push(stackSuccessivi.pop());
			typeView = currentView.getClass().getSimpleName();
			
			if(typeView.equalsIgnoreCase("TextView")){
				TextView txtview = (TextView) currentView;/*findViewById(currentView.getId());*/
				String testo = txtview.getText().toString();
				if(testo.equals("Year")||testo.equals("Month")||testo.equals("Day")){
					tipoListenerRecognition="ListenerRecognitionPicker";
					tts.speak("Pronunciare "+testo, TextToSpeech.QUEUE_FLUSH, params);
				}
				else {
					tipoListenerRecognition=null; //
					tts.speak(testo, TextToSpeech.QUEUE_FLUSH, params); //
				
				}  
				return;
			}
			else{
			}
		}
		if(stackSuccessivi.isEmpty()){
			int[] typeButton = {AlertDialog.BUTTON_POSITIVE,AlertDialog.BUTTON_NEGATIVE,AlertDialog.BUTTON_NEUTRAL};
			for(int i=0;i<typeButton.length;i++){
				Button but = getButton(typeButton[i]);
				if(but!=null)
					richiestaBottoni = richiestaBottoni + but.getText().toString()+" ... ";
			}
			tipoListenerRecognition="ListenerRecognitionButton";
			tts.speak("Pronuncia "+richiestaBottoni, TextToSpeech.QUEUE_FLUSH, params);
		}
	}	
	
	public void shutdown(){
		if(stackPrecedenti!=null){
    		while(!stackPrecedenti.isEmpty())
    			stackSuccessivi.push(stackPrecedenti.pop());
    	}
    	richiestaBottoni="";
    	tts.stop();
    	tts.shutdown();
		sr.destroy();
	}
	
	public void restartTTS(){
		sr.destroy();
		tts.stop();
		tts.shutdown();
		istanceTTS();
	}
	
	private String getNumberFromString(String text){
		String[] splitted = text.split(" ");
		if(splitted.length==1){
			boolean isInteger = false;
			try{
				Integer.parseInt(splitted[0]);
				isInteger=true;
			}catch(Exception e){}
			
			if(isInteger)	
				return splitted[0];
		}
		else if(splitted.length>1&&splitted.length<4){
			if(splitted[1].equalsIgnoreCase("punto")||splitted[1].equalsIgnoreCase("virgola")){
				boolean isInteger = false;
				try{
					Integer.parseInt(splitted[0]);
					Integer.parseInt(splitted[2]);
					isInteger=true;
				}catch(Exception e){}
				if(isInteger)
					return splitted[0]+"."+splitted[2];
			}
		}		
		return null;
	}


	  class ListenerRecognitionButton implements RecognitionListener, OnInitListener{ /////////

			@Override
			public void onBeginningOfSpeech() {
			}

			@Override
			public void onBufferReceived(byte[] buffer) {
			}

			@Override
			public void onEndOfSpeech() {
			}

			@Override
			public void onError(int error) {
				Toast.makeText(context, "errore "+error, Toast.LENGTH_SHORT).show();
				tipoListenerRecognition="ListenerRecognitionButton";
				tts.speak("Errore. Pronuncia "+richiestaBottoni, TextToSpeech.QUEUE_FLUSH, params);
			}

			@Override
			public void onEvent(int eventType, Bundle params) {
			}

			@Override
			public void onPartialResults(Bundle partialResults) {
			}

			@Override
			public void onReadyForSpeech(Bundle params) {
				tipoListenerRecognition=null;
				Toast.makeText(context, "Start", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onResults(Bundle results) {
				
				ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
				boolean trovato=false;
				int[] typeButton = {AlertDialog.BUTTON_POSITIVE,AlertDialog.BUTTON_NEGATIVE,AlertDialog.BUTTON_NEUTRAL};
				for(int i=0;i<typeButton.length;i++){
					Button but = getButton(typeButton[i]);
					if(but!=null)
						if(matches.get(0).equalsIgnoreCase(but.getText().toString())){
							trovato=true;
							but.performClick();
							break;
						}//richiestaBottoni = richiestaBottoni + but.getText().toString()+" ... ";
				}
				if(!trovato){
					tipoListenerRecognition="ListenerRecognitionButton";
					tts.speak(matches.get(0).toString()+" non presente. Pronuncia "+richiestaBottoni, TextToSpeech.QUEUE_FLUSH, params);
				}
			}

			@Override
			public void onRmsChanged(float rmsdB) {
			}

			//TTS
			@Override
			public void onInit(int status) {
				
				if(status==TextToSpeech.SUCCESS){
					int result = tts.setLanguage(Locale.getDefault());
					if(result==TextToSpeech.LANG_MISSING_DATA ||result==TextToSpeech.LANG_NOT_SUPPORTED  )
						Utils.debug("TTS non sopportato");
				}
			}
			
		}

	  
	  class ListenerRecognitionPicker implements RecognitionListener{ /////////

			@Override
			public void onBeginningOfSpeech() {
			}

			@Override
			public void onBufferReceived(byte[] buffer) {
			}

			@Override
			public void onEndOfSpeech() {
			}

			@Override
			public void onError(int error) {
				Toast.makeText(context, "errore "+error, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onEvent(int eventType, Bundle params) {
			}

			@Override
			public void onPartialResults(Bundle partialResults) {
			}

			@Override
			public void onReadyForSpeech(Bundle params) {
				tipoListenerRecognition=null;
				Toast.makeText(context, "Start", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onResults(Bundle results) {
				ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
				Toast.makeText(context, matches.get(0)+ "", Toast.LENGTH_LONG).show();
				
				/*dare un occhiata alla modalità comando*/
				if(matches.get(0).equalsIgnoreCase("Comando Applicazione")){
					Toast.makeText(context, matches.get(0)+ "", Toast.LENGTH_LONG).show();
					modalitaComando=true;
					tipoListenerRecognition="ListenerRecognitionEdit";
					tts.speak("Modalità Comando. Pronuncia Avanti, Indietro, Ok, Cancella, Esci", TextToSpeech.QUEUE_FLUSH, params);
				}
				else if(matches.get(0).equalsIgnoreCase("Indietro")&&modalitaComando){
					stackSuccessivi.push(stackPrecedenti.pop());
					modalitaComando=false;
					if(stackPrecedenti.isEmpty()){
						tipoListenerRecognition=null;
						tts.speak("Indietro non possibile. ", TextToSpeech.QUEUE_FLUSH, params);
					}
					else{
						stackSuccessivi.push(stackPrecedenti.pop());						
					}
				}
				else if(matches.get(0).equalsIgnoreCase("Avanti")&&modalitaComando){
					modalitaComando=false;
					if(!stackSuccessivi.isEmpty()){
					}
					else{
						stackSuccessivi.push(stackPrecedenti.pop());
						tipoListenerRecognition=null;
						tts.speak("Avanti non possibile. ", TextToSpeech.QUEUE_FLUSH, params);
					}
					
				
				}
				else if(matches.get(0).equalsIgnoreCase("Esci")&&modalitaComando){
					stackSuccessivi.push(stackPrecedenti.pop());
					modalitaComando=false;
					
				}
				else{
					TextView txt = (TextView) stackPrecedenti.peek();	
					DatePicker picker = getDatePicker();
					GregorianCalendar calendar = new GregorianCalendar();
					for(int i=0;i<matches.size();i++)//
						Utils.debug("String "+ matches.get(i));//
					String number="";
					if(txt.getText().equals("Month")){
						for(int i=0;i<12;i++){
							calendar.set(GregorianCalendar.MONTH, i);
							String shortName = calendar.getDisplayName(GregorianCalendar.MONTH, GregorianCalendar.SHORT, Locale.getDefault());
							if(matches.get(0).substring(0,3).equalsIgnoreCase(shortName)){
								number=i+"";
								break;
							}
						}
					}
					else{
						number = getNumberFromString(matches.get(0));
					}
					if(number==null){
						tipoListenerRecognition="ListenerRecognitionPicker";
						tts.speak("Inserire un valore numerico. Riprova", TextToSpeech.QUEUE_FLUSH, params);
					}
					else if(number.equals("")){
						tipoListenerRecognition="ListenerRecognitionPicker";
						tts.speak("Mese inserito non esistente. Riprova", TextToSpeech.QUEUE_FLUSH, params);
					}
					else{
						if(txt.getText().equals("Day")){
							picker.updateDate(picker.getYear(), picker.getMonth(), Integer.parseInt(number));
							calendar.setTimeInMillis(picker.getCalendarView().getDate());
						}
						else if(txt.getText().equals("Month")){
							picker.updateDate(picker.getYear(), Integer.parseInt(number), picker.getDayOfMonth());
							calendar.setTimeInMillis(picker.getCalendarView().getDate());
						}
						else if(txt.getText().equals("Year")){
							picker.updateDate(Integer.parseInt(number), picker.getMonth(), picker.getDayOfMonth());
							calendar.setTimeInMillis(picker.getCalendarView().getDate());
						}
						
					}
				}
			}

			@Override
			public void onRmsChanged(float rmsdB) {
			}
	  }


	


}
