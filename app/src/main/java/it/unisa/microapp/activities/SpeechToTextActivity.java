package it.unisa.microapp.activities;


import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import it.unisa.microapp.data.StringData;

public class SpeechToTextActivity extends MAActivity 
{
	private int REQUEST_CODE = 3;
	private String txt;
	
	@Override
	protected void initialize(Bundle savedInstanceState) {
		txt= "";
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
		startRecognition();

	}	
	
	@Override
	public void initInputs() {
		
	}

	private void startRecognition()
	{
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition");
		startActivityForResult(intent, REQUEST_CODE);			
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
	    if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
	    {
	        final ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	        if(matches.size() == 1)
	        {
	        	txt = matches.get(0);
	        	next();
	        }	
	        else if(matches.size() > 1)
	        {
	            AlertDialog.Builder builder = new AlertDialog.Builder(this);
	            builder.setTitle("Pick a value");
	            String[] smatches = matches.toArray(new String[matches.size()]);
	   
	            builder.setItems(smatches, new DialogInterface.OnClickListener() {
	                       public void onClick(DialogInterface dialog, int which) {
	                    	   txt = matches.get(which);
	                    	   next();
	                   }
	            });
	            
	            builder.setCancelable(false);
	            builder.create().show();	        	
	        }
	    }
	    else
	    {
	    	txt= "";
	    	next();
	    }	
	}	

	@Override
	public void beforeNext() 
	{
		StringData data = new StringData(this.mycomponent.getId(), txt);
		this.application.putData(this.mycomponent, data);		
	}
	
	@Override
	protected void resume(){
		//metodi per speech Vincenzo Savarese
	}
}
