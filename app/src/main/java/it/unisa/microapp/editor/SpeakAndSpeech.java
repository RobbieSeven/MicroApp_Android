package it.unisa.microapp.editor;

import it.unisa.microapp.R;
import it.unisa.microapp.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Stack;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

public class SpeakAndSpeech {
	private Context context;
	private ArrayList<Button> listaBottoni;
	private SpeechRecognizer sr;
	private Intent intent;
	private TextToSpeech tts;
	private Activity mainActivity;
	private String tipoListenerRecognition=null;
	private HashMap<String, String> params;
	public String richiestaBottoni="";
	public boolean speakableButton=false;
	public boolean modalitaComando=false;
	public int idViewSuccessiva=-1;
	private Stack<View> stackPrecedenti;
	private Stack<View> stackSuccessivi;
	
	public SpeakAndSpeech(Context context, Activity activity){
		this.mainActivity=activity;
		this.context=context;
		stackPrecedenti= new Stack<View>();
		stackSuccessivi= new Stack<View>();
		listaBottoni = new ArrayList<Button>();//
		sr = SpeechRecognizer.createSpeechRecognizer(context);//
		intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);//
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);//
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "it.unisa.microapp.activities");//
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);///
	}
	
	public void getStructureForMap(int resId) {//nuova modifica da verificare se tenere o meno
		
		int idButton[] = {R.id.buttnext,R.id.buttprev,R.id.normal,R.id.traffic,R.id.sat};
		for(int i=0;i<idButton.length;i++){
			Button but = (Button) mainActivity.findViewById(idButton[i]);
			stackSuccessivi.push(but);
		}
	}
	
	public void getStructureLayout(ViewGroup appView){ ////////////////////
		stackPrecedenti = getAllChildrenStack(appView);
		//stackSuccessivi=new Stack<View>();
		while(!stackPrecedenti.isEmpty()){
			View v = stackPrecedenti.peek();
			String nameClass = v.getClass().getSimpleName();
			if(!(nameClass.equalsIgnoreCase("linearlayout")||nameClass.equalsIgnoreCase("relativelayout")||nameClass.equalsIgnoreCase("framelayout"))){
				Utils.debug("VIEW stack" + v.getClass().getName());
				stackSuccessivi.push(stackPrecedenti.pop());
			}
			else
				Utils.debug("VIEW STACK Scartato -> "+stackPrecedenti.pop().getClass().getName().toString());
		}
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
				TextView txtview = (TextView) mainActivity.findViewById(currentView.getId());
				String testo = txtview.getText().toString();
				if(txtview.getLabelFor()!=0xffffffff){
					Utils.debug("VIEW TextView LabelFor "+testo);
					
					idViewSuccessiva = txtview.getLabelFor();
					EditText editView = (EditText)mainActivity.findViewById(idViewSuccessiva);
					Utils.debug("EDITSUCCESSVA"+ editView.getText()+" [testo]");
					if(!editView.getText().toString().equals(""))
						testo = testo + " ... Preimpostato: "+editView.getText()+" ... Pronuncia Valore";
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
				break;
			}
			else if(typeView.equalsIgnoreCase("EditText")){
				/*View vv = */ new View(context);
				EditText editxt = (EditText) mainActivity.findViewById(currentView.getId());
				String stateEdit = (String) editxt.getTag();
				if(stateEdit.equalsIgnoreCase("disabled")){
					tipoListenerRecognition=null;
					tts.speak(adapteToSpeak(editxt.getText().toString()), TextToSpeech.QUEUE_FLUSH, params);
				}
				else{
					tipoListenerRecognition="ListenerRecognitionEdit";
					tts.speak("inserisci contenuto edit", TextToSpeech.QUEUE_FLUSH, params);
				}
				break;
			}
			else if(typeView.equalsIgnoreCase("RadioGroup")){
				RadioGroup radioG = (RadioGroup) mainActivity.findViewById(currentView.getId());
				String testo= "Scegli tra: ";
				String checkedPred="";
				for(int i=0;i<radioG.getChildCount();i++){
					Utils.debug("VIEW RadioButton " + context.getResources().getResourceEntryName(currentView.getId())+" "+stackSuccessivi.size());
					RadioButton view = (RadioButton) radioG.getChildAt(i);
					testo = testo + view.getText().toString()+", ";
					if(view.isChecked())
						checkedPred = view.getText().toString();
				}
				testo= testo + ". Preimpostato "+checkedPred;
				tipoListenerRecognition="ListenerRecognitionRadioGroup";
				tts.speak(testo, TextToSpeech.QUEUE_FLUSH, params);
				break;
			}
			else if(typeView.equalsIgnoreCase("RadioButton")){
				/*controllo se il padre e' un radiogroup, se si non faccio nnt*/
				RadioButton radio = (RadioButton) mainActivity.findViewById(currentView.getId());
				
				if(radio.isChecked()){
					tipoListenerRecognition=null;
					tts.speak("Impostato: "+radio.getText().toString(), TextToSpeech.QUEUE_FLUSH, params);
				}
				else{
					speakSpeechLayout();
				}
				break;
			}
			else if(typeView.equalsIgnoreCase("Button")){
				//da rivedere. prende i bottoni anche quando stanno a meta' layout
				Button but = (Button) mainActivity.findViewById(currentView.getId());
				richiestaBottoni = richiestaBottoni + but.getText().toString()+", "; 
				listaBottoni.add(but);
				
			}
		
			else{
				Utils.debug("VIEW Altre :"+typeView);
			}
		}
		if(stackSuccessivi.isEmpty()&&/*!richiestaBottoni.equalsIgnoreCase("")*/listaBottoni.size()!=0){
			//speakableButton  = true;
			Utils.debug("VIEW Pronuncia Bottoni " +stackSuccessivi.size()+richiestaBottoni);
			tipoListenerRecognition="ListenerRecognitionButton";
			tts.speak("Pronuncia "+richiestaBottoni, TextToSpeech.QUEUE_FLUSH, params);
			//speakBottoni();//
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
				//e' un numero di telefono
				split[i]="";
				for(int j=0; j<testo.length();j++){
					split[i]=split[i]+String.valueOf(testo.charAt(j)+" ");
				}
			}
		}
		for(int i=0;i<split.length;i++){
			toReturn = toReturn + split[i]+" ";
		}
		Utils.debug("TESTO ADATTATO " + toReturn);
		return toReturn;
	}
		
	public void shutdown(){
		if(stackPrecedenti!=null){
    		while(!stackPrecedenti.isEmpty())
    			stackSuccessivi.push(stackPrecedenti.pop());
    	}
		speakableButton=false;
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
				boolean trovato=false;
				boolean enable=true;
				/*Controllare prima se ho detto comando applicazione*/
				for(int i = 0; i<listaBottoni.size(); i++){
				Button but = listaBottoni.get(i);
					if(matches.get(0).equalsIgnoreCase(but.getText().toString())){
						trovato=true;
						if(but.isEnabled()){
							but.performClick();
						}else{
							enable=false;
							tipoListenerRecognition="ListenerRecognitionButton";
							tts.speak("Button "+matches.get(0).toString()+" non abilitato. Pronuncia "+richiestaBottoni, TextToSpeech.QUEUE_FLUSH, params);
						}
						break;
					}
				}
				if(!trovato){
					tipoListenerRecognition="ListenerRecognitionButton";
					tts.speak(matches.get(0).toString()+" non presente. Pronuncia "+richiestaBottoni, TextToSpeech.QUEUE_FLUSH, params);
				}
				else{
					if(enable)
						speakSpeechLayout();
				}
			}

			@Override
			public void onRmsChanged(float rmsdB) {
			}

			//TTS
			@Override
			public void onInit(int status) {
				
				/*
				 *aggiungere la parte per far installare il tts se non è installato
				 */
				if(status==TextToSpeech.SUCCESS){
					int result = tts.setLanguage(Locale.getDefault());
					if(result==TextToSpeech.LANG_MISSING_DATA ||result==TextToSpeech.LANG_NOT_SUPPORTED  )
						Utils.debug("TTS non sopportato");
					Utils.debug("ACTIVITY  onInit");
				}
			}
			
		}

	  
	  class ListenerRecognitionEdit implements RecognitionListener{

			@Override
			public void onBeginningOfSpeech() {
				
				Utils.debug("onBeginning");
			}

			@Override
			public void onBufferReceived(byte[] buffer) {
				Utils.debug("onBuffer");
			}

			@Override
			public void onEndOfSpeech() {
				
				Utils.debug("onEnd");
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
						if(idViewSuccessiva!=-1)	
							stackSuccessivi.push(stackPrecedenti.pop());
						stackSuccessivi.push(stackPrecedenti.pop());
						speakSpeechLayout();
					}
				}
				else if(matches.get(0).equalsIgnoreCase("Avanti")&&modalitaComando){
					if(idViewSuccessiva!=-1)
						stackPrecedenti.push(stackSuccessivi.pop());
					modalitaComando=false;
					if(!stackSuccessivi.isEmpty()){
						speakSpeechLayout();
					}
					else{
						if(idViewSuccessiva!=-1)	
							stackSuccessivi.push(stackPrecedenti.pop());
						stackSuccessivi.push(stackPrecedenti.pop());
						tipoListenerRecognition=null;
						tts.speak("Avanti non possibile. ", TextToSpeech.QUEUE_FLUSH, params);
					}
					
					
				}
				else if(matches.get(0).equalsIgnoreCase("Esci")&&modalitaComando){
					stackSuccessivi.push(stackPrecedenti.pop());
					modalitaComando=false;
					speakSpeechLayout();
				}
				else{
					View vv;
					if(idViewSuccessiva!=-1){
						vv = mainActivity.findViewById(idViewSuccessiva);
					}else
						vv=stackPrecedenti.peek();
					if(vv instanceof EditText){
							EditText txt = (EditText) vv;
							for(int i=0;i<matches.size();i++)//
								Utils.debug("String "+ matches.get(i));//
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
									speakSpeechLayout();
								}
							}
							else{
								txt.setText(matches.get(0));
								if(idViewSuccessiva!=-1)
									stackPrecedenti.push(stackSuccessivi.pop());
								idViewSuccessiva=-1;
								speakSpeechLayout();
							}
					}	
				}

			}

			@Override
			public void onRmsChanged(float rmsdB) {
			}
	  }

	  class ListenerRecognitionRadioGroup implements RecognitionListener{ 

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
					RadioButton rdb = (RadioButton) mainActivity.findViewById(rdg.getChildAt(i).getId());
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
					speakSpeechLayout();
				}
			}

			@Override
			public void onRmsChanged(float rmsdB) {
			}
	  }
}

