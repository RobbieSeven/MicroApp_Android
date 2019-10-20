package it.unisa.microapp.activities;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


import it.unisa.microapp.MicroAppGenerator;
import it.unisa.microapp.R;
import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.data.DataType;
import it.unisa.microapp.data.GenericData;
import it.unisa.microapp.editor.ControllerConditionActivity;
import it.unisa.microapp.editor.OptionsActivity;
import it.unisa.microapp.editor.PreCondition;
import it.unisa.microapp.editor.SpeakAndSpeech;
import it.unisa.microapp.editor.SpeechProgressDialog;
import it.unisa.microapp.utils.Constants;
import it.unisa.microapp.utils.Utils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public abstract class MAActivity extends Activity implements State, Runnable {
	private String description = "";
	private String stateView = "";
	private StateContext contextstat = new StateContext(); 

	protected MAComponent mycomponent;
	protected MicroAppGenerator application;
	protected Menu contextmenu;

	protected static boolean banner = false; 
	protected static String adUnitId;
	protected AdView adView;

	// private ControllerCondition controller = new ControllerCondition(this);
	private PreCondition[] listCondition = null;
	protected boolean verificationCondition = false;
	protected boolean createCondition = false;
	protected boolean restartCondition = false;

	private SpeechProgressDialog pDialog;

	private ViewGroup appView; // ***

	protected boolean activateEditingSpeech = false;
	private SpeakAndSpeech sas;

	// private View generateLayout=null;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		application = (MicroAppGenerator) getApplication();

		Bundle bu = getIntent().getExtras();
		if (bu != null) {
			setDescription(bu.getString("description"));
			setViewState(bu.getString("state")); 
			updateTitle(Utils.stripExtension(application.getDeployPath()), bu.getString("name"));

			// this.requestWindowFeature(Window.FEATURE_LEFT_ICON);
			// this.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
			// application.getIconId());
		}

		if (application.getCurrentState() == null)
			return;

		mycomponent = (MAComponent) application.getCurrentState();
		initialize(savedInstanceState);
		verificationConditionActivityFalse();
		initInputs();
		prepare();

		if (activateEditingSpeech) {
			sas = new SpeakAndSpeech(this, MAActivity.this);
		}

		onView(); 

		verificationCondition();
	}

	protected void onView() {
		View v = null;
		switch (contextstat.getMode(stateView)) {
		case 1:
			Utils.debug("onView -> VISIBLE");
			v = onVisibleView();
			if (v != null) {
				prepareView(v);
				setAppView(v);
			} else {
				int res = onVisible();
				if (res <= 0) {
					// dialog
				} else {
					View layout = null;
					if (res != R.layout.map) {
						LayoutInflater inflater = LayoutInflater.from(this);
						layout = inflater.inflate(res, null);
					}
					setAppView(res);// nuova modifica la normale e' setAppView2
					prepareView(layout);
				}
			}

			execute();

			break;
		case 2:
			Utils.debug("onView -> HIDDEN");
			onHidden();
			break;
		case 3:
			Utils.debug("onView -> PROGRESS");
			onProgressDialog();
			break;
		default:
			break;
		}
	}

	protected void verificationConditionActivityFalse(){
		if (application.getConditionActivity()==0){
			application.setConditionActivity(2);
			mycomponent.setJumpComponent(true);
			MAComponent comp;
			do {
				comp = application.nextStep();
				/*for (DataType dt : DataType.values()) {
					Iterable<GenericData<?>> dit = application.getData(mycomponent.getId(), dt);
					if(dit != null)
					{
						for(GenericData<?> d : dit)
							application.putDataInCondition(mycomponent, d);
					}
				}*/
				if (comp == null) {
					banner = false;
					setResult(Constants.ID_TERMINATED);
					finish();
					return;
				}
			} while (!comp.getFirstFalseComponentCondition());

			try {
				Intent i = new Intent(this, comp.getActivityClass());

				Bundle b = new Bundle();
				b.putString("description", comp.getDescription());
				b.putString("state", comp.getNowState());
				b.putString("name", Utils.typeToString(comp.getType()));
				i.putExtras(b);
				startActivityForResult(i, 0);
			} catch (ClassNotFoundException e) {
				Utils.errorDialog(this, e.getMessage());
			}
		}
	}

	protected abstract void initialize(Bundle savedInstanceState);

	protected abstract void initInputs();

	protected abstract void prepare();

	protected abstract int onVisible();

	protected abstract View onVisibleView();

	protected void onHidden() {
	};

	protected void onProgress(ProgressDialog d) {
		int i = 0;
		while (i <= 100) {
			Utils.delay(250);
			setProgressDialogValue(i);
			i += 10;
		}
	};

	protected abstract void prepareView(View v);

	protected abstract void execute();

	protected void start() {
	};

	protected void restart() {
	};

	protected void resume() {
	};

	protected void pause() {
	};

	protected void stop() {

	};

	protected void backPressed() {
	};

	protected void onCondition(boolean state) {
	};

	protected abstract void beforeNext();

	protected void destroy() {
	};

	// fine modifica

	@Override
	public void onStart() {
		super.onStart();
		start();
	}

	@Override
	public void onStop() {
		super.onStop();
		stop();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		// reVerificationCondition();
		Utils.debug("MAActivity -> onRestart " + verificationCondition + " msg");

		if (mycomponent.getCondition() != null) {
			if (!(mycomponent.getCondition().equals("false"))) {
				listCondition = mycomponent.getPreCondition();
				Intent intentCondition = new Intent(this, ControllerConditionActivity.class);
				Bundle b1 = new Bundle();
				b1.putParcelableArray("listcondition", listCondition);
				intentCondition.putExtras(b1);
				this.startActivityForResult(intentCondition, Constants.RESTART_LIST_CONDITION);
			}
		}

		restart();
	}

	@Override
	protected void onResume() {
		super.onResume();

		Utils.debug("MAActivity -> onresume " + verificationCondition + " msg");
		if (createCondition) {
			createCondition = false;
			if (mycomponent.getCondition().equals("mandatory")) {
				if (!verificationCondition) {
					setResult(Constants.ID_TERMINATED);
					finish();
					return;
				}
			} else if (mycomponent.getCondition().equals("optional")) {
				if (!verificationCondition) {
					mycomponent.setJumpComponent(true);
					MAComponent comp;
					do {
						comp = application.nextStep();
						if (comp == null) {
							banner = false;
							setResult(Constants.ID_TERMINATED);
							finish();
							return;
						}
					} while (!comp.getFirstComponentGraph());

					try {
						Intent i = new Intent(this, comp.getActivityClass());

						Bundle b = new Bundle();
						b.putString("description", comp.getDescription());
						b.putString("state", comp.getNowState()); 
						b.putString("name", Utils.typeToString(comp.getType()));
						i.putExtras(b);

						startActivityForResult(i, 0);
					} catch (ClassNotFoundException e) {
						Utils.errorDialog(this, e.getMessage());
					}
				}
			}
		} else if (restartCondition) {
			restartCondition = false;
			if (mycomponent.getCondition().equals("mandatory")) {
				if (!verificationCondition) {
					setResult(Constants.ID_TERMINATED);
					finish();
					return;
				}
			} else if (mycomponent.getCondition().equals("optional")) {
				if (mycomponent.getJumpComponent()) {
					MAComponent comp;
					comp = application.nextStep();
					while (!mycomponent.equals(comp)) {
						comp = application.prevStep();
					}
					mycomponent.setJumpComponent(false);
				}

				if (!verificationCondition) {
					application.prevStep();
					finish();
				}
			}
		}
		/*
		 * forse non far partire neanche l'istanceTTS nel caso in cui lo speech
		 * � disattivo
		 */

		if (activateEditingSpeech) {
			sas.istanceTTS();// Vincenzo Savarese
		}
		resume();

		if (activateEditingSpeech) {
			speakSpeechLayout();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		Utils.debug("MAActivity -> onPause " + verificationCondition + " msg");
		if (activateEditingSpeech) {
			sas.shutdown();// Vincenzo Savarese
		}
		pause();

	}

	private void verificationCondition() {

		Utils.debug("ULTIMO oncreate " + mycomponent.getType());

		if (mycomponent.getCondition() != null) {

			if (!(mycomponent.getCondition().equals("false"))) {
				listCondition = mycomponent.getPreCondition();
				Intent intentCondition = new Intent(this, ControllerConditionActivity.class);
				Bundle b1 = new Bundle();
				b1.putParcelableArray("listcondition", listCondition);
				intentCondition.putExtras(b1);
				this.startActivityForResult(intentCondition, Constants.CREATE_LIST_CONDITION);

				/*
				 * verificationCondition=controller.verification(listCondition);
				 * if( mycomponent.getCondition().equals("mandatory")){
				 * if(!verificationCondition){
				 * setResult(Constants.ID_TERMINATED); finish(); return; } }else
				 * if(mycomponent.getCondition().equals("optional")){
				 * if(!verificationCondition){
				 * mycomponent.setJumpComponent(true); MAComponent comp; do{
				 * comp = application.nextStep(); if (comp == null) {
				 * banner=false; setResult(Constants.ID_TERMINATED); finish();
				 * return; } }while(!comp.getFirstComponentGraph());
				 * 
				 * try { Intent i = new Intent(this, comp.getActivityClass());
				 * 
				 * Bundle b = new Bundle(); b.putString("description",
				 * comp.getDescription()); b.putString("name",
				 * Utils.typeToString(comp.getType())); i.putExtras(b);
				 * 
				 * startActivityForResult(i, 0); } catch (ClassNotFoundException
				 * e) { Utils.errorDialog(this, e.getMessage()); } } }
				 */
			} else {
				verificationCondition = true;
			}
		}
		onCondition(verificationCondition);
	}

	/*
	 * private void reVerificationCondition(){ Utils.debug("ULTIMO onRestart"+
	 * ""); Utils.debug("ULTIMO onRestart "+ mycomponent.getType()); 
	 * if( mycomponent.getCondition()!=null){
	 * if(!(mycomponent.getCondition().equals("false"))){ //ok no comment
	 * verificationCondition=controller.verification(listCondition); if(
	 * mycomponent.getCondition().equals("mandatory")){
	 * if(!verificationCondition){ setResult(Constants.ID_TERMINATED); finish();
	 * return; } }else if(mycomponent.getCondition().equals("optional")){
	 * if(mycomponent.getJumpComponent()){ MAComponent comp; comp =
	 * application.nextStep(); while(!mycomponent.equals(comp)){ comp =
	 * application.prevStep(); } mycomponent.setJumpComponent(false); }
	 * if(!verificationCondition){ application.prevStep(); finish(); } } } }
	 * 
	 * 
	 * }
	 */

	private void updateTitle(String title, String subtitle) {
		if (title != null && !title.equals("")) {
			this.setTitle(title + (subtitle != null ? (" [" + subtitle + "]") : ""));
		} else
			this.setTitle(getString(R.string.app_name));
	}

	public void gotoPrevious(View v) {
		this.previous();
	}

	public void gotoNext(View v) {
		this.next();
	}

	protected String getPreviousLabel() {
		return getString(R.string.previous);
	}

	protected String getNextLabel() {
		return getString(R.string.next);
	}

	protected String getProgressTitle() {
		return null;
	}

	protected Drawable getPreviousDrawable() { 
		return getResources().getDrawable(R.drawable.ic_menu_back);
	}

	protected Drawable getNextDrawable() { 
		return getResources().getDrawable(R.drawable.ic_menu_forward);
	}

	protected Drawable getVoidDrawable() { 
		return getResources().getDrawable(R.drawable.pixel);
	}

	protected boolean enablePrevious() {
		return true;
	}

	protected boolean enableNext() {
		return true;
	}

	protected boolean enableInfo() {
		return (onCreateDescription() != null && !onCreateDescription().equals(""));
	}

	protected boolean enableExit() {
		return true;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		int order = Menu.FIRST;
		int gruppo = 0;

		menu.add(gruppo, 0, order++, getPreviousLabel()).setIcon(android.R.drawable.ic_media_previous);
		menu.add(gruppo, 1, order++, getNextLabel()).setIcon(android.R.drawable.ic_media_next);
		gruppo++;
		gruppo++;
		menu.add(gruppo, 3, order++, getString(R.string.info)).setIcon(android.R.drawable.ic_dialog_info);
		menu.add(gruppo, 4, order++, getString(R.string.exit)).setIcon(android.R.drawable.ic_lock_power_off);

		contextmenu = menu;
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.getItem(0).setEnabled(enablePrevious());
		menu.getItem(1).setEnabled(enableNext());
		menu.getItem(2).setEnabled(enableInfo());
		menu.getItem(3).setEnabled(enableExit());
		return true;
	}

	/*
	 * public void setGenerateLayout(View layout){//Vincenzo Savarese
	 * generateLayout = layout; }
	 */

	protected void setAppView(int resId) { // Vincenzo Savarese nuova modifica
											// da verificare se tenere o meno
											// (originale setAppView2)
		setContentView(resId);

		if (activateEditingSpeech) {
			if (resId == R.layout.map) {
				Utils.verbose("LayoutMap" + R.layout.map);
				sas.getStructureForMap(resId);
			} else {
				LayoutInflater inflater = LayoutInflater.from(this);
				View layout = inflater.inflate(resId, null);
				sas.getStructureLayout((ViewGroup) layout);
			}
		}

		insertBanner();
		inflateButtons();
	}

	/*
	 * protected void setAppView2(int resId){ //Vincenzo Savarese
	 * setContentView(resId); LayoutInflater inflater =
	 * LayoutInflater.from(this); View layout = inflater.inflate(resId, null);
	 * sas.getStructureLayout((ViewGroup) layout); insertBanner();
	 * inflateButtons(); }
	 */

	protected void setAppView(View layout) {
		setContentView(layout);
		if (activateEditingSpeech) {
			sas.getStructureLayout((ViewGroup) layout);
		}
		insertBanner();
		inflateButtons();
	}

	protected void getStructureLayout(View layout) {// Vincenzo Savarese
		if (activateEditingSpeech) {
			appView = (ViewGroup) layout;
			sas.getStructureLayout(appView);
		}
	}

	protected void speakSpeechLayout() {// Vincenzo Savarese
		if (OptionsActivity.getBoolean(Constants.CB_SPEECH_KEY, this)) {// per
																		// disattivare
																		// o
																		// attivare
																		// speech
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {

					sas.speakSpeechLayout();
				}
			}, 1500);
		}
	}

	protected void inflateButtons() {
		try {
			Button b = (Button) findViewById(R.id.buttprev);

			if (b != null) {
				b.setText(getPreviousLabel());
				b.setEnabled(enablePrevious());
				b.setCompoundDrawablesWithIntrinsicBounds(getPreviousDrawable(), null, null, null);
			}
		} catch (ClassCastException e) {
		}

		try {
			Button b = (Button) findViewById(R.id.buttnext);

			if (b != null) {
				b.setText(getNextLabel());
				b.setEnabled(enableNext());
				b.setCompoundDrawablesWithIntrinsicBounds(null, null, getNextDrawable(), null);
			}
		} catch (ClassCastException e) {
		}
	}

	public void setPreviousMenuItem(boolean state) {
		contextmenu.getItem(0).setEnabled(state);
	}

	public void setNextMenuItem(boolean state) {
		contextmenu.getItem(1).setEnabled(state);
	}

	public void setInfoMenuItem(boolean state) {
		contextmenu.getItem(2).setEnabled(state);
	}

	public void setExitMenuItem(boolean state) {
		contextmenu.getItem(3).setEnabled(state);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			this.previous();
			break;
		case 1:
			this.next();
			break;
		case 3:
			this.getInfo();
			break;
		case 4:
			this.exit();
			break;
		}
		return true;
	}

	protected void exit() {
		Utils.verbose("Closing " + this.getString(R.string.app_name) + "!");
		setResult(Constants.ID_EXIT);
		finish();
	}

	public void getInfo() {
		new AlertDialog.Builder(this).setTitle("Info...").setMessage(this.onCreateDescription())
				.setNeutralButton("Close", null).setIcon(android.R.drawable.ic_dialog_info).show();
	}

	public void verificationConditionActivityTrue(){
		if (application.getConditionActivity()==1){
			Utils.debug("mycomponent: "+mycomponent.getDescription());
			Utils.debug("getlasttrue: "+mycomponent.getLastTrue());
			if (mycomponent.getLastTrue()) {
				application.setConditionActivity(2);
				mycomponent.setJumpComponent(true);
				MAComponent comp;
				for (DataType dt : DataType.values()) {
					Iterable<GenericData<?>> dit = application.getData(mycomponent.getId(), dt);
					if(dit != null)
					{
						for(GenericData<?> d : dit)
							application.putDataInCondition(mycomponent, d);
					}
				}
				do {
					comp = application.nextStep();
					if (comp == null) {
						banner = false;
						setResult(Constants.ID_TERMINATED);
						finish();
						return;
					}
				} while (!comp.getFirstComponentGraph());
				try {
					Intent i = new Intent(this, comp.getActivityClass());
					Bundle b = new Bundle();
					b.putString("description", comp.getDescription());
					b.putString("state", comp.getNowState());
					b.putString("name", Utils.typeToString(comp.getType()));
					i.putExtras(b);
					startActivityForResult(i, 0);
				} catch (ClassNotFoundException e) {
					Utils.errorDialog(this, e.getMessage());
				}
			}
		}
	}

	public void next() {
		beforeNext();
		verificationConditionActivityTrue();
		removeBanner();
		if (!mycomponent.isOutFilled()) {
			Toast.makeText(this, "Some outputs are missing " + mycomponent.getType() + " id:" + mycomponent.getId(),
					Toast.LENGTH_SHORT).show();
			return;
		}
		MAComponent comp = application.nextStep();
		if (comp == null) {
			banner = false;
			setResult(Constants.ID_TERMINATED);
			finish();
			return;
		}

		try {
			Intent i = new Intent(this, comp.getActivityClass());

			Bundle b = new Bundle();
			b.putString("description", comp.getDescription());
			b.putString("state", comp.getNowState()); 
			b.putString("name", Utils.typeToString(comp.getType()));
			i.putExtras(b);

			startActivityForResult(i, 0);
		} catch (ClassNotFoundException e) {
			Utils.errorDialog(this, e.getMessage());
		}
	}

	public void previous() {
		application.prevStep();
		finish();
	}

	public void setDescription(String s) {
		description = s;
	}

	protected void setViewState(String s) {
		stateView = s;
	}

	protected String getViewState() {
		return stateView;
	}


	@Override
	public String onCreateDescription() {
		return description;
	}

	@Override
	public void onBackPressed() {
		backPressed();
		previous();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Constants.ID_EXIT || resultCode == Constants.ID_TERMINATED) {
			setResult(resultCode);
			finish();
			return;
		} else if (requestCode == Constants.CREATE_LIST_CONDITION && resultCode == Constants.LIST_CONDITION) {
			createCondition = true;
			verificationCondition = data.getBooleanExtra("verification", false);
			Utils.debug("MAActivity -> onactivityresult " + verificationCondition + " CREATE_LIST_CONDITION");
		} else if (requestCode == Constants.RESTART_LIST_CONDITION && resultCode == Constants.LIST_CONDITION) {
			restartCondition = true;
			verificationCondition = data.getBooleanExtra("verification", false);
			Utils.debug("MAActivity -> onactivityresult " + verificationCondition + " RESTART_LIST_CONDITION");
		}
	}

	private void insertBanner() {
		if (banner) {
			
		    adView = new AdView(this);
		    adView.setAdUnitId(adUnitId);
		    adView.setAdSize(AdSize.BANNER);			
			
			LinearLayout layout = (LinearLayout) findViewById(R.id.bunnerLayout);
			if (layout != null) {
				layout.addView(adView, 0);
				AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("TEST_DEVICE_ID").build();
				adView.loadAd(adRequest);
			}
		}
	}

	private void removeBanner() {
		if (adView != null) {
			adView.destroy();
		}
	}

	private void onProgressDialog() {
		pDialog = new SpeechProgressDialog(this);/* SpeechProgressDialog(this); */

		String title = this.getProgressTitle();
		if (title != null)
			pDialog.setTitle(title);

		pDialog.setMessage(getString(R.string.pleaseWait));
		pDialog.setCancelable(false);
		pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pDialog.setMax(100);
		pDialog.setProgress(0);
		pDialog.show();
		Thread thread = new Thread(this);
		thread.start();
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pDialog.dismiss();
		}
	};

	protected void setProgressDialogValue(int i) {
		if (pDialog != null) {
			if (i >= 0 && i <= 100)
				pDialog.setProgress(i);
		}
	}

	@Override
	public void run() {
		onProgress(pDialog);
		handler.sendEmptyMessage(0);
	}

	@Override
	public void onDestroy() {
		removeBanner();
		destroy();
		super.onDestroy();

	}

	protected void activateSpeech(boolean active) {
		if (OptionsActivity.getBoolean(Constants.CB_SPEECH_KEY, this)) {
			if (activateEditingSpeech) {
			if (active) {
				sas.istanceTTS();
				speakSpeechLayout();
			} else
				sas.shutdown();
			}
		}
	}
}
