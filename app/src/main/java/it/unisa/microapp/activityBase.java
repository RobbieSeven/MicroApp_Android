package it.unisa.microapp;

import android.content.*;
import it.unisa.microapp.activities.*;
import it.unisa.microapp.*;
import it.unisa.microapp.components.*;
import android.os.Bundle;
import android.view.View;

@SuppressWarnings("unused")
public class activityBase extends MAActivity
{
	private String message = "";
    MicroAppGenerator application;
    MAComponent mycomponent;
    Context context;
    MAActivity scaff;
    
    public void load(MicroAppGenerator app, MAComponent myc, Context con, MAActivity scaffolding) {
        application = app;
        mycomponent = myc;
        context = con;
        scaff = scaffolding;
    }
    
	@Override
	public void initInputs() {	
		
	}
    
	private void behaviour() {
		
	}
    
	@Override
	public void beforeNext() {
		
	}
    
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
		
	}	
}
