package it.unisa.microapp.activities;

import it.unisa.microapp.editor.SpeechAlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;

public class InformationErrorActivity extends MAActivity 
{
	private String message;

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
		showMessage();

	}	
	
	@Override
	public void initInputs() 
	{
		message=this.mycomponent.getUserData("text").iterator().next();	
	}

	private void showMessage() 
	{
		SpeechAlertDialog builder=new SpeechAlertDialog(this,InformationErrorActivity.this);
		builder.setTitle("Error");
		builder.setMessage(message);
		builder.setButton(SpeechAlertDialog.BUTTON_POSITIVE,getResources().getString(android.R.string.ok), new OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				InformationErrorActivity.this.next();
			}
			
		});
		builder.show();
	}

	@Override
	public void beforeNext() 
	{

	}
	
	@Override
	protected void resume(){
		 //metodi per speech Vincenzo Savarese
	}
}
