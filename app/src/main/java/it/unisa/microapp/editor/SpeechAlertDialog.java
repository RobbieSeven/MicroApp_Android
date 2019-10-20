package it.unisa.microapp.editor;

import it.unisa.microapp.utils.Constants;
import it.unisa.microapp.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Stack;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class SpeechAlertDialog extends AlertDialog{


	private Context context;
	private TextToSpeech tts;
	private ViewGroup layoutView=null;
	private SpeechRecognizer sr;
	private Intent intent;
	private ArrayList<Button> listaBottoni;
	private Stack<View> stackSuccessivi;
	private Stack<View> stackPrecedenti;
	private String richiestaBottoni="";
	public String tipoListenerRecognition;
	public int idViewSuccessiva=-1;
	public boolean modalitaComando;
	private HashMap<String, String> params;
	private CharSequence message="";
	private CharSequence title="";
	private Activity mainActivity;
	
	public static final String TESTO_ERRORE = "è avvenuto un errore. Inserisci ";
	public static final String TESTO_INSERISCI = "Inserisci ";
	
	public SpeechAlertDialog(Context context,Activity activity) {
		super(context);
		this.context=context;
		this.mainActivity=activity;
		listaBottoni = new ArrayList<Button>();
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
	
	@Override
	public void setView (View view){
		super.setView(view);
		layoutView = (ViewGroup)view;
		Utils.debug("NUMERO COMPONENTI"+ layoutView.getChildCount()+"");
	}
	
	public int numView(){
		return layoutView.getChildCount();
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
	
	@Override
	public void setTitle(int idTitle){
		super.setTitle(title);
		this.title=context.getResources().getString(idTitle);
	}
	
	@Override
	public void show(){
		super.show();
	}
	
	@Override
	public void onStart(){
		super.onStart();
		this.getStructureLayout(layoutView);
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
		if(OptionsActivity.getBoolean(Constants.CB_SPEECH_KEY, context)){//per disattivare o attivare speech
			Handler handler = new Handler(); 
			handler.postDelayed(new Runnable() { 
				public void run() { 
					speakSpeechLayout();
				} 
			}, 1000); 
		}
	}
	
	@Override
	public void onDetachedFromWindow(){
		super.onDetachedFromWindow();
	}
	
	@Override
	public void onWindowFocusChanged (boolean hasFocus){
		super.onWindowFocusChanged(hasFocus);
	}
	
	public void getStructureLayout(ViewGroup appView){ ////////////////////
		stackPrecedenti = new Stack<View>();
		stackSuccessivi = new Stack<View>();
		if(appView!=null){
			stackPrecedenti = getAllChildrenStack(appView);
			while(!stackPrecedenti.isEmpty()){
				View v = stackPrecedenti.peek();
				String nameClass = v.getClass().getSimpleName();
				if(!(nameClass.equalsIgnoreCase("linearlayout")||nameClass.equalsIgnoreCase("relativelayout")||nameClass.equalsIgnoreCase("framelayout")||nameClass.equalsIgnoreCase("scrollview"))){
					stackSuccessivi.push(stackPrecedenti.pop());
				}
				else
					Utils.debug("VIEW STACK Scartato -> "+stackPrecedenti.pop().getClass().getName().toString());
			}
		}
		if(!message.equals("")){
			TextView viewMessage = new TextView(context);
			viewMessage.setText(message);
			stackSuccessivi.push(viewMessage);
		}
		TextView viewTitle= new TextView(context);
		viewTitle.setText(title);
		stackSuccessivi.push(viewTitle);
	}
	
	private Stack<View> getAllChildrenStack(View v) {
	    if (!(v instanceof ViewGroup)) {
	        Stack<View> viewStack = new Stack<View>();
	        viewStack.add(v);
	        return viewStack;
	    }
	    Stack<View> result = new Stack<View>();
	    ViewGroup vg = (ViewGroup) v;
	    for (int i = 0; i < vg.getChildCount(); i++) {
	        View child = vg.getChildAt(i);
	        Stack<View> viewStack = new Stack<View>();
	        viewStack.add(v);
	        viewStack.addAll(getAllChildrenStack(child));
	        result.addAll(viewStack);
	    }
	    return result;
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
				if(txtview.getLabelFor()!=0xffffffff){
					
					idViewSuccessiva = txtview.getLabelFor();
					EditText editView = (EditText)findViewById(idViewSuccessiva);
					if(!editView.getText().toString().equals(""))
						testo = testo + "Preimpostato: "+editView.getText()+"... Pronuncia Valore";
					else
						testo= testo + "... Pronuncia valore";
					tipoListenerRecognition="ListenerRecognitionEdit";
					tts.speak(testo, TextToSpeech.QUEUE_FLUSH, params);
				}
				else{
					testo = adapteToSpeak(testo); //
					tipoListenerRecognition=null; //
					tts.speak(testo, TextToSpeech.QUEUE_FLUSH, params); //				
				}  
				return;
			}
			else if(typeView.equalsIgnoreCase("EditText")){
				EditText editxt = (EditText) /*findViewById(currentView.getId())*/currentView;
				String stateEdit = (String) editxt.getTag();
				if(stateEdit.equalsIgnoreCase("disabled")){
					tipoListenerRecognition=null;
					tts.speak(adapteToSpeak(editxt.getText().toString()), TextToSpeech.QUEUE_FLUSH, params);
				}
				else{
					tipoListenerRecognition="ListenerRecognitionEdit";
					tts.speak("inserisci contenuto edit", TextToSpeech.QUEUE_FLUSH, params);
				}
				return;
			}
			else if(typeView.equalsIgnoreCase("RadioGroup")){
				RadioGroup radioG = (RadioGroup) /*findViewById(currentView.getId())*/currentView;
				String testo= "Scegli tra: ";
				String checkedPred="";
				for(int i=0;i<radioG.getChildCount();i++){
					RadioButton view = (RadioButton) radioG.getChildAt(i);
					testo = testo + view.getText().toString()+", ";
					if(view.isChecked())
						checkedPred = view.getText().toString();
				}
				testo= testo + ". Preimpostato "+checkedPred;
				tipoListenerRecognition="ListenerRecognitionRadioGroup";
				tts.speak(testo, TextToSpeech.QUEUE_FLUSH, params);
				return;
			}
			else if(typeView.equalsIgnoreCase("RadioButton")){
				/*controllo se il padre è un radiogroup, se si non faccio nnt*/
				RadioButton radio = (RadioButton) findViewById(currentView.getId());
				
				if(radio.isChecked()){
					tipoListenerRecognition=null;
					tts.speak("Impostato: "+radio.getText().toString(), TextToSpeech.QUEUE_FLUSH, params);
				}
				else{
				}
				return;
			}
			else if(typeView.equalsIgnoreCase("Button")){
				//da rivedere. prende i bottoni anche quando stanno a metà layout
				Button but = (Button) /*findViewById(currentView.getId())*/currentView;
				richiestaBottoni = richiestaBottoni + but.getText().toString()+", "; 
				listaBottoni.add(but);
				
			}
		
			else{
				Utils.debug("VIEW Altre : "+typeView);
			}
		}
		if(stackSuccessivi.isEmpty()/*&&listaBottoni.size()!=0*/){
			//speakableButton  = true;
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
	
	private String adapteToSpeak(String text){
		String testo="";
		String toReturn="";
		String split[] = text.split("\n");
		text="";
		for(int i=0; i<split.length;i++)
			text=text + split[i] + " ... ";
		split = text.split(" ");
		for(int i=0;i<split.length;i++){
			boolean isLong = false;
			testo=split[i];
			try{
				Long.parseLong(testo);
				isLong=true;
			}catch(Exception e){}
			
			if(isLong&&testo.length()==10){
				//è un numero di telefono
				split[i]="";
				for(int j=0; j<testo.length();j++){
					split[i]=split[i]+String.valueOf(testo.charAt(j)+" ");
				}
			}
		}
		for(int i=0;i<split.length;i++){
			toReturn = toReturn + split[i]+" ";
		}
		return toReturn;
	}
	
	public void shutdown(){
		if(stackPrecedenti!=null){
    		while(!stackPrecedenti.isEmpty())
    			stackSuccessivi.push(stackPrecedenti.pop());
    	}
    	richiestaBottoni="";
    	listaBottoni.clear();
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
	                	else if(tipoListenerRecognition.equalsIgnoreCase("ListenerRecognitionEdit")){
	                		sr.setRecognitionListener(new ListenerRecognitionEdit());
	    					sr.startListening(intent);
	                	}
	                	else if(tipoListenerRecognition.equalsIgnoreCase("ListenerRecognitionRadioGroup")){
	                		sr.setRecognitionListener(new ListenerRecognitionRadioGroup());
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
				Utils.debug("MATCHES "+matches.get(0));
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
					Utils.debug("DIALOG onInit");
				}
			}
			
		}

	  
	  class ListenerRecognitionEdit implements RecognitionListener{ /////////

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
					Utils.debug("COMANDO Modalita' comando");
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
						if(idViewSuccessiva!=-1)	
							stackSuccessivi.push(stackPrecedenti.pop());
						stackSuccessivi.push(stackPrecedenti.pop());
					}
				}
				else if(matches.get(0).equalsIgnoreCase("Avanti")&&modalitaComando){
					if(idViewSuccessiva!=-1)
						stackPrecedenti.push(stackSuccessivi.pop());
					modalitaComando=false;
					if(!stackSuccessivi.isEmpty()){
					}
					else{
						if(idViewSuccessiva!=-1)	
							stackSuccessivi.push(stackPrecedenti.pop());
						stackSuccessivi.push(stackPrecedenti.pop());
						tipoListenerRecognition=null;
						tts.speak("Avanti non possibile.", TextToSpeech.QUEUE_FLUSH, params);
					}
					
					
				}
				else if(matches.get(0).equalsIgnoreCase("Esci")&&modalitaComando){
					stackSuccessivi.push(stackPrecedenti.pop());
					modalitaComando=false;
					
				}
				else{
					View vv;
					if(idViewSuccessiva!=-1){
						vv = findViewById(idViewSuccessiva);
					}else
						vv=stackPrecedenti.peek();
					EditText txt = (EditText) vv;
					for(int i=0;i<matches.size();i++)//
						Utils.debug("String "+matches.get(i));//
					if(txt.getInputType()==InputType.TYPE_CLASS_NUMBER||txt.getInputType()==InputType.TYPE_CLASS_PHONE){
						String number = getNumberFromString(matches.get(0));
						if(number==null){
							tipoListenerRecognition="ListenerRecognitionEdit";
							tts.speak("Inserire un valore numerico. Riprova", TextToSpeech.QUEUE_FLUSH, params);
						}
						else{
							txt.setText(number);
							if(idViewSuccessiva!=-1)
								stackPrecedenti.push(stackSuccessivi.pop());
							idViewSuccessiva=-1;
							
						}
					}
					else{
						txt.setText(matches.get(0));
						if(idViewSuccessiva!=-1)
							stackPrecedenti.push(stackSuccessivi.pop());
						idViewSuccessiva=-1;
						
					}
						/*
							idViewSuccessiva=-1;
							*/
							//INDICE++;
				}	

			}

			@Override
			public void onRmsChanged(float rmsdB) {
			}
	  }

	  class ListenerRecognitionRadioGroup implements RecognitionListener{ /////////

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
				String richiestaRadio="";
				String checkedPrec="";
				ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
				boolean trovato=false;
				RadioGroup rdg = (RadioGroup) stackPrecedenti.peek();
				/*Controllare prima se ho detto comando applicazione*/
				for(int i = 0; i<rdg.getChildCount(); i++){
					RadioButton rdb = (RadioButton) findViewById(rdg.getChildAt(i).getId());
					richiestaRadio= richiestaRadio + rdb.getText().toString()+", ";
					if(rdb.isChecked())
						checkedPrec=rdb.getText().toString();
					if(matches.get(0).equalsIgnoreCase(rdb.getText().toString())){
						trovato=true;
					
						rdb.callOnClick();
						rdb.performClick();
					}
					
				}
				if(!trovato){
					tipoListenerRecognition="ListenerRecognitionRadioGroup";
					tts.speak(matches.get(0).toString()+" non presente. Preimpostato "+ checkedPrec +". Scegli tra "+richiestaRadio, TextToSpeech.QUEUE_FLUSH, params);
				}
				else{
					for(int i = 0; i<rdg.getChildCount(); i++){
						stackPrecedenti.push(stackSuccessivi.pop());
						stackPrecedenti.push(stackSuccessivi.pop());
					}
					stackSuccessivi.push(stackPrecedenti.pop());
					
				}
			}

			@Override
			public void onRmsChanged(float rmsdB) {
			}
	  }


}
