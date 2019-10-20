package it.unisa.microapp.activities;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import it.unisa.microapp.R;
import it.unisa.microapp.components.domotic.DomoticComponent;
import it.unisa.microapp.data.StringData;

public class DomoticActivity extends MAActivity {
	private DomoticComponent component;
	private String dname;
	private String dstate;
	private String dlink;
	private String dtype;
	private String description;
	private String statoattuale;

	private int time = 60000;
	private boolean settingUpdate = false;

	protected void initialize(Bundle savedInstanceState) {

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		component = (DomoticComponent) this.mycomponent;	
	}

	@Override
	protected void prepare() {
	
	}

	@Override
	protected int onVisible() {
		return R.layout.domotic;
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
		
		if (dtype.equalsIgnoreCase("SwitchItem")){
			DomoticActionRequest request = new DomoticActionRequest(this, dname, dstate, dlink, dtype);
			request.execute("");
			statoattuale = request.getStatoattuale();
		}
		
		if (dtype.equalsIgnoreCase("RollershutterItem")){
			DomoticActionRequest request = new DomoticActionRequest(this, dname, dstate, dlink, dtype);
			request.execute("");
			statoattuale = request.getStatoattuale();
		}
		
	}

	@Override
	public void initInputs() {

		dname = component.getDname();
		dstate = component.getDstate();
		dlink = component.getDlink();
		dtype = component.getDtype();
		
		description = component.getDescription();
		
		//else leggere input dal triangolo rosso userinput)
	}

	@Override
	public void beforeNext() {

		StringData data=new StringData(this.mycomponent.getId(),statoattuale);
		this.application.putData(this.mycomponent, data);
		
	}

	@Override
	protected void resume(){
		//metodi per speech Vincenzo Savarese
	}
}
