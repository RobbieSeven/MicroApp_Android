package it.unisa.microapp.editor;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import it.unisa.microapp.MicroAppGenerator;
import it.unisa.microapp.R;
import it.unisa.microapp.activities.DiscovererActivity;
import it.unisa.microapp.activities.NewComponentActivity;
import it.unisa.microapp.activities.NewRestfulActivity;
import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.exceptions.InvalidComponentException;
import it.unisa.microapp.store.AppCaricataActivity;
import it.unisa.microapp.store.ConnectDb;
import it.unisa.microapp.store.Parser;
import it.unisa.microapp.support.FileManagement;
import it.unisa.microapp.support.ScaffoldingManager;
import it.unisa.microapp.utils.Constants;
import it.unisa.microapp.utils.Utils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.Settings.Secure;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class EditorActivity extends Activity {
	MainPanel main;
	String namefile = null;
	String descrizione = null;
	String state = null;
	String icon = null;
	String activator = null;
	String nick = null;
	String xml = null;
	Bitmap screen;

	// private ScaffoldingManager SFM = new ScaffoldingManager();
	private ConnectDb connect = new ConnectDb();
	private JSONArray jArray = new JSONArray();
	private JSONArray jArray2 = new JSONArray();
	private String id;
	private ProgressDialog progDailog;
	private String encodeScreenshot;
	private static GestureLibrary sStore;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.mainpanel);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int phisicalH = metrics.heightPixels;
		int phisicalW = metrics.widthPixels;

		main = (MainPanel) findViewById(R.id.surface);
		main.setId(6432);
		main.resize(phisicalH, phisicalW);

		registerForContextMenu(main);

		Button button = (Button) this.findViewById(R.id.iconselect);
		button.setCompoundDrawablesWithIntrinsicBounds(0, android.R.drawable.ic_menu_add, 0, 0);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Activity act = EditorActivity.this;
				Utils.vibrate(act);
				Intent myIntent = new Intent(act.getApplicationContext(), ChoiceIconActivity.class);
				Bundle b = new Bundle();
				Utils.verbose("mainpanel:" + namefile);
				b.putString("file", namefile);
				myIntent.putExtras(b);
				act.startActivityForResult(myIntent, 2);
			}

		});

		Button buttonModify = (Button) this.findViewById(R.id.iconmodify);
		buttonModify.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				if (main.isReadyForInserting()) {
					Activity act = EditorActivity.this;
					Utils.vibrate(act);
					main.handleParameters();
				}
				return true;
			}

		});

		Button buttonOutline = (Button) this.findViewById(R.id.iconoutline);
		buttonOutline.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				Activity act = EditorActivity.this;
				Utils.vibrate(act);
				main.zoomZero();
				return true;
			}

		});

		buttonOutline.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Activity act = EditorActivity.this;
				Utils.vibrate(act);
				main.zoomIn();
			}
		});

		Bundle bu = getIntent().getExtras();
		namefile = bu.getString("namefile");
		descrizione = bu.getString("description");
		state = bu.getString("state"); 
		icon = bu.getString("icon");
		activator = bu.getString("activator");
		SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFERENCES, MODE_PRIVATE);
		nick = prefs.getString(Constants.TEXT_NICKNAME_KEY, "");

		updateTitle();
		updateIcon(icon);

		if (bu.getBoolean("load")) {
			main.load(namefile + Constants.extension);
			main.grid.setModified(false);
		}	

		Utils.extractAssets(this);
		ScaffoldingManager.createScaffoldingXml();

		//main.demo();
	}

	public void updateTitle() {
		if (namefile != null) {
			boolean mod = main.grid != null ? main.grid.isModified() : false;
			this.setTitle(getString(R.string.app_name) + " [" + namefile + (mod ? "*" : "") + "]");
		} else
			this.setTitle(getString(R.string.app_name));
		
		
	}

	public void updateIcon(String ref) {
		
		String uri = "@drawable/" + ref;
		int res = getResources().getIdentifier(uri, null, this.getPackageName());
		if (res != 0)
			this.getActionBar().setIcon(res);
		else
			this.getActionBar().setIcon(R.drawable.app_icon);				
	}
	
	private void fileSaved() {
		if (main.grid.isModified()) {
			AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);

			dlgAlert.setMessage("The file has been modified. Do you want lose the changes?");
			dlgAlert.setTitle("Resource modified");

			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						finish();
						break;

					case DialogInterface.BUTTON_NEGATIVE:
						break;
					}
				}
			};

			dlgAlert.setPositiveButton(android.R.string.yes, dialogClickListener);
			dlgAlert.setNegativeButton(android.R.string.no, dialogClickListener);
			dlgAlert.setCancelable(false);
			dlgAlert.create().show();
		} else {
			finish();
		}

	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	private void fileLoaded() {
		if (main.grid.isModified()) {
			AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);

			dlgAlert.setMessage("The file has been modified. Do you want lose the changes?");
			dlgAlert.setTitle("Resource modified");

			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						Intent i = new Intent(EditorActivity.this, ActivityLoad.class);
						EditorActivity.this.startActivityForResult(i, 10);
						break;

					case DialogInterface.BUTTON_NEGATIVE:
						break;
					}
				}
			};

			dlgAlert.setPositiveButton(android.R.string.yes, dialogClickListener);
			dlgAlert.setNegativeButton(android.R.string.no, dialogClickListener);
			dlgAlert.setCancelable(false);
			dlgAlert.create().show();
		} else {
			Intent i = new Intent(EditorActivity.this, ActivityLoad.class);
			EditorActivity.this.startActivityForResult(i, 10);
		}

	}

	private void fileValidated(int errorCode) {
		AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);

		switch (errorCode) {
		case Constants.SENTENCE_EMPTY:
			dlgAlert.setMessage("The application is empty.");
			dlgAlert.setTitle("Empty application");
			break;
		case Constants.SENTENCE_INPUT_MISSED:
			dlgAlert.setMessage("Some user inputs are missing.");
			dlgAlert.setTitle("Input missed");
			break;
		case Constants.SENTENCE_INPUT_UNUSED:
			dlgAlert.setMessage("Some inputs are not used.");
			dlgAlert.setTitle("Input missed");
			break;			
		default:
			return;
		}

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					break;
				}
			}
		};

		dlgAlert.setPositiveButton(android.R.string.ok, dialogClickListener);
		dlgAlert.setCancelable(true);
		dlgAlert.create().show();
	}

	@Override
	public void onBackPressed() {
		fileSaved();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, 2, 1, getString(R.string.load)).setIcon(android.R.drawable.ic_input_get);
		menu.add(Menu.NONE, 3, 2, getString(R.string.save)).setIcon(android.R.drawable.ic_menu_save);
		menu.add(Menu.NONE, 16, 3, getString(R.string.saveAs)).setIcon(android.R.drawable.ic_menu_share);
		menu.add(Menu.NONE, 4, 4, getString(R.string.upload)).setIcon(android.R.drawable.ic_menu_set_as);
		menu.add(Menu.NONE, 1, 5, getString(R.string.undo)).setIcon(android.R.drawable.ic_menu_revert);
		menu.add(Menu.NONE, 12, 6, getString(R.string.delete)).setIcon(android.R.drawable.ic_menu_delete);
		menu.add(Menu.NONE, 6, 7, getString(R.string.reset)).setIcon(android.R.drawable.ic_media_rew);
		menu.add(Menu.NONE, 7, 8, getString(R.string.back)).setIcon(android.R.drawable.ic_media_previous);
		menu.add(Menu.NONE, 10, 9, getString(R.string.shift)).setIcon(android.R.drawable.ic_menu_share);
		menu.add(Menu.NONE, 14, 10, getString(R.string.changeState)).setIcon(android.R.drawable.ic_menu_compass);
		menu.add(Menu.NONE, 11, 11, getString(R.string.setCondition)).setIcon(android.R.drawable.ic_menu_directions);
		menu.add(Menu.NONE, 5, 12, getString(R.string.ttry)).setIcon(android.R.drawable.ic_media_play);
		menu.add(Menu.NONE, 9, 13, getString(R.string.findService)).setIcon(android.R.drawable.ic_menu_search);
		menu.add(Menu.NONE, 15, 14, getString(R.string.findDomoService)).setIcon(android.R.drawable.ic_menu_search);
		menu.add(Menu.NONE, 13, 15, getString(R.string.newcomponent)).setIcon(android.R.drawable.ic_input_get);
		menu.add(Menu.NONE, 17, 16, getString(R.string.newrestful)).setIcon(android.R.drawable.ic_input_get);
		menu.add(Menu.NONE, 8, 17, getString(R.string.demo)).setIcon(android.R.drawable.ic_menu_manage);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.getItem(1).setEnabled(main.grid.isModified()); //save
		menu.getItem(2).setEnabled(main.grid.isModified()); //saveAs
		menu.getItem(3).setEnabled(!main.grid.isEmpty()); //upload
		menu.getItem(4).setEnabled(!main.grid.isEmpty()); //undo
		menu.getItem(5).setEnabled(main.grid.isSelected()); //delete
		menu.getItem(6).setEnabled(!main.grid.isEmpty()); //reset
		menu.getItem(7).setEnabled(!main.grid.isEmpty()); //back		
		menu.getItem(8).setEnabled(main.grid.isSelected()); //shift
		menu.getItem(9).setEnabled(main.grid.isSelected()); //change state
		menu.getItem(10).setEnabled(main.grid.isSelected()); //set condition
		menu.getItem(11).setEnabled(!main.grid.isEmpty()); //try
		menu.getItem(15).setEnabled(!main.grid.isModified()); //demo
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		int code;
		
		switch (item.getItemId()) {
		case 1: //undo
			main.undo();
			break;
		case 2: //load
			fileLoaded();
			break;
		case 3: //save
			code = main.validate(true);
			if (code == Constants.SENTENCE_CORRECT) {
				// in activator salva la stringa
				main.setInfos(icon, descrizione, activator);
				main.save(namefile, false, false);
				MicroAppGenerator app = (MicroAppGenerator) getApplication();

				if (activator.contains("GestureActivator")) {
					// potrebbe essere null nel caso in cui ho scelto di
					// modificare il programma
					// ma non ho apportato modifiche al launcher
					if (app.getGesture() != null) {
						sStore = GestureLibraries.fromFile(FileManagement.getGestureDir());
						sStore.load();
						sStore.addGesture(namefile + ".xml", app.getGesture());
						sStore.save();
					}

				}

			} else {
				fileValidated(code);
			}
			break;
		case 4: //upload
			int code1 = main.validate(true);
			if (code1 == Constants.SENTENCE_CORRECT) {
				main.setInfos(icon, descrizione, activator);
				main.save(namefile, false, false);

				if (nick.equals("") || nick.equals(null)) {
					Toast.makeText(this, "Nickname empty, set in Option!", Toast.LENGTH_SHORT).show();
				} else {
					getScreenshotsFile();
					parsingXML();
					try {
						progDailog = new ProgressDialog(EditorActivity.this);
						progDailog.setTitle("Upload application");
						progDailog.setMessage(getString(R.string.pleaseWait));
						progDailog.setCancelable(false);
						progDailog.show();
						new Thread() {
							public void run() {

								carica();
								progDailog.dismiss();
							}
						}.start();
					} catch (Exception e) {
						Utils.error("Initializa App", e);
					}
				}
			} else {
				fileValidated(code1);
			}

			break;
		case 5: //try
			code = main.validate(true);
			if (code == Constants.SENTENCE_CORRECT) {
				String path = main.save(namefile, false, true);
				MicroAppGenerator application = (MicroAppGenerator) getApplication();

				try {
					application.setDeployPath(path, false);

					try {
						application.initComponents();
					} catch (InvalidComponentException e) {
						Utils.errorDialog(this, getString(R.string.notRunnable), e.getMessage());
					}

					// beforeNext();

					MAComponent mycomponent = (MAComponent) application.getCurrentState();
					// initInputs();

					if (!mycomponent.isOutFilled()) {
						Toast.makeText(this, "Some outputs are missing " + mycomponent.getType() + " id:" + mycomponent.getId(),
								Toast.LENGTH_SHORT).show();

					}
					MAComponent comp = application.nextStep();

					if (comp == null) {
						setResult(Constants.ID_TERMINATED);
						// finish();
					}
					try {
						Intent runi = new Intent(this, comp.getActivityClass());

						Bundle b = new Bundle();
						b.putString("description", comp.getDescription());
						b.putString("state", comp.getNowState()); 
						runi.putExtras(b);

						startActivityForResult(runi, 1);
					} catch (ClassNotFoundException e) {
						Utils.errorDialog(this, e.getMessage());
					}

				} catch (Exception e) {
				}
			} else {
				fileValidated(code);
			}
			break;
		case 6: //reset
			main.news();
			break;
		case 7: //back
			fileSaved();
			break;
		case 8: //demo
			namefile = "Accident Assistant"; //Send to the Editor
			icon = "info48";
			descrizione = "A demo application of MicroApp";
			activator = "ApplicationMenu";

			main.setInfos(icon, descrizione, activator);
			main.demo();
			this.updateTitle();
			main.invalidate();
			break;
		case 9: //find service
			Intent intent = new Intent(this, DiscovererActivity.class);
			Bundle b = new Bundle();
			b.putString("file", namefile);
			intent.putExtras(b);
			this.startActivityForResult(intent, 1);
			break;
		case 10: //shift
			main.shift();
			break;
		case 11: //set condition
			if (main.grid.isSelected()) {
				Intent intentCondition = new Intent(this, SetConditionActivity.class);
				
				if (main.getCondition() == Constants.MANDATORY_CONDITION) {
					Bundle b1 = new Bundle();
					b1.putBoolean("mandatory", true);
					intentCondition.putExtras(b1);
				}
				Bundle b1 = new Bundle();
				b1.putParcelableArray("precondition", main.getPreCondition());
				intentCondition.putExtras(b1);

				this.startActivityForResult(intentCondition, 11);
			}
			break;
		case 12: //delete
			if (main.grid.isSelected()) {
				main.delete();
			}
			break;
		case 13: //new component
			if (!ScaffoldingManager.isScaffoldingFull()) {
				Intent intent1 = new Intent(this, NewComponentActivity.class);
				this.startActivity(intent1);
				break;
			} else {
				Toast.makeText(EditorActivity.this, "No more space for new components.", Toast.LENGTH_LONG).show();
			}
			break;
		case 14: //change state
			if (main.grid.isSelected()) {
				Piece pie = main.grid.getSelected();
				String[] all = pie.getArrayStates();

				if (all.length > 1) {
					Intent intentState = new Intent(this, SetStateActivity.class);

					Bundle b1 = new Bundle();
					b1.putStringArray("states", all);
					b1.putString("nowState", main.getNowState());
					intentState.putExtras(b1);

					this.startActivityForResult(intentState, 14);
				} else {
					Toast.makeText(EditorActivity.this, "This component can be only visible.", Toast.LENGTH_LONG).show();
				}
			}
			break; 
			case 15: //find domotic
							
						Intent i = new Intent(this, DomoticActivity.class);
						Bundle bdl = new Bundle();
						bdl.putString("file", namefile);
						i.putExtras(bdl);
						this.startActivityForResult(i, 15);
						break;
			case 16: //save as component
				code = main.validate(false);
				if (code == Constants.SENTENCE_CORRECT) {
					// in activator salva la stringa
					main.setInfos(icon, descrizione, activator);
					main.save(namefile, false, false);
					MicroAppGenerator app = (MicroAppGenerator) getApplication();

					if (activator.contains("GestureActivator")) {
						// potrebbe essere null nel caso in cui ho scelto di
						// modificare il programma
						// ma non ho apportato modifiche al launcher
						if (app.getGesture() != null) {
							sStore = GestureLibraries.fromFile(FileManagement.getGestureDir());
							sStore.load();
							sStore.addGesture(namefile + ".xml", app.getGesture());
							sStore.save();
						}

					}

				} else {
					fileValidated(code);
				}
				break;
			case 17: //new restful component	
				Intent intent1 = new Intent(this, NewRestfulActivity.class);
				this.startActivity(intent1);
				break;
						
		default:
			break;
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.ID_RESULT_CONTACT) {
			Pin lp = MainPanel.lastPin;
			if (lp != null && data != null) {
				Cursor cursor = this.getContentResolver().query(data.getData(), null, null, null, null);
				String name = "";
				while (cursor.moveToNext()) {
					name = cursor.getString(cursor.getColumnIndexOrThrow(Contacts.LOOKUP_KEY));
				}

				lp.setObject(new String(name));
				MainPanel.lastPin = null;
				main.invalidate();
			}
		} else if (requestCode == Constants.ID_SELECT_IMAGE) {
			Pin lp = MainPanel.lastPin;
			if (lp != null && data != null) {
				if (resultCode == Activity.RESULT_OK) {
					Uri picture = data.getData();
					lp.setObject(new String(picture.toString()));
					MainPanel.lastPin = null;
					main.invalidate();
				}
			}
		} else if (requestCode == Constants.ID_SELECT_VIDEO) {
			Pin lp = MainPanel.lastPin;
			if (lp != null && data != null) {
				if (resultCode == Activity.RESULT_OK) {
					Uri video = data.getData();
					lp.setObject(new String(video.toString()));
					MainPanel.lastPin = null;
					main.invalidate();
				}

			}
		} else if (requestCode == Constants.ID_SELECT_AUDIO) {
			Pin lp = MainPanel.lastPin;
			if (lp != null && data != null) {
				if (resultCode == Activity.RESULT_OK) {
					Uri audio = data.getData();
					lp.setObject(new String(audio.toString()));
					MainPanel.lastPin = null;
					main.invalidate();
				}

			}
		} else if (requestCode == 2) {
			main.invalidate();
		}

		if (resultCode == 1) {
			Intent in = data;
			Bundle b = in.getExtras();
			if (b != null) {
				boolean selezione = b.getBoolean("selezione");
				int inserimenti = b.getInt("inserimenti");
				boolean entra = b.getBoolean("entra");
				boolean StopinseriMulti = b.getBoolean("StopinseriMulti");
				boolean Vmulti = b.getBoolean("Vmulti");
				int multiconta = b.getInt("multiconta");
				int multico = b.getInt("multico");
				int userCont = b.getInt("userCont");
				boolean UserIns = b.getBoolean("UserIns");
				int pont = b.getInt("pont");
				int conta = b.getInt("conta");
				String padre = b.getString("padre");
				boolean discovering = b.getBoolean("discovering");

				Utils.verbose("discovering:" + discovering);

				Piece X = (Piece) b.getSerializable("X");
				Utils.debug(X.showInfo());
				main.Caricamento(selezione, inserimenti, entra, StopinseriMulti, Vmulti, multiconta, multico, userCont, UserIns, pont,
						conta, padre, X);
			}
		}
			else if (resultCode == 15) {
				Intent inn = data;
				Bundle bnd = inn.getExtras();
				if (bnd != null) {
					boolean selezione = bnd.getBoolean("selezione");
					int inserimenti = bnd.getInt("inserimenti");
					boolean entra = bnd.getBoolean("entra");
					boolean StopinseriMulti = bnd.getBoolean("StopinseriMulti");
					boolean Vmulti = bnd.getBoolean("Vmulti");
					int multiconta = bnd.getInt("multiconta");
					int multico = bnd.getInt("multico");
					int userCont = bnd.getInt("userCont");
					boolean UserIns = bnd.getBoolean("UserIns");
					int pont = bnd.getInt("pont");
					int conta = bnd.getInt("conta");
					String padre = bnd.getString("padre");
					boolean discovering = bnd.getBoolean("discovering");

					Utils.verbose("discovering:" + discovering);

					Piece X = (Piece) bnd.getSerializable("X");
					Utils.debug(X.showInfo());
					main.Caricamento(selezione, inserimenti, entra, StopinseriMulti, Vmulti, multiconta, multico, userCont, UserIns, pont,
							conta, padre, X);
				}

			
		} else if (resultCode == 9) {
			Intent io = data;
			Bundle bb = io.getExtras();
			main.news();
			main.load(bb.getString("nomefile"));
			main.grid.setModified(false);
		} else if (resultCode == 11) {
			Utils.debug("sto in 11");
			Intent io = data;
			Bundle bb = io.getExtras();
			if (bb != null) {
				int value = bb.getInt("condition");
				main.setCondition(value);
				Parcelable[] p = bb.getParcelableArray("precondition");

				PreCondition[] risp = null;
				if (p != null) {
					risp = new PreCondition[p.length];
					for (int i = 0; i < p.length; i++) {
						risp[i] = (PreCondition) p[i];
					}
				}
				main.setPreCondition(risp);

			}
		}
		else if (resultCode == 14) {
			if (resultCode != RESULT_CANCELED) {
				Intent io = data;
				Bundle bb = io.getExtras();
				if (bb != null) {
					String stat = bb.getString("state");
					state = stat;
					main.setNowState(stat);
					main.grid.unselectPiece();
					main.grid.setModified(true);

				}
			}
		}

		else if (resultCode == Constants.ID_TERMINATED) {
			try {
				MicroAppGenerator application = (MicroAppGenerator) getApplication();
				String tname = application.getDeployPath();

				if (tname.startsWith(Constants.tryFilePrefix)) {
					File f = new File(FileManagement.getDefaultPath(), tname);
					if (f.exists()) {
						f.delete();
					}

					application.setDeployPath(null, false);
				}
			} catch (IOException e) {
			} catch (SAXException e) {
			}
		}
	}

	public String getLookupUri(String nome) {
		Cursor cc = getContentResolver().query(Contacts.CONTENT_URI,
				new String[] { Contacts.DISPLAY_NAME, Contacts.LOOKUP_KEY },
				Contacts.DISPLAY_NAME + "= ?", new String[] { nome }, null);
		String lookID = "";
		while (cc.moveToNext()) {
			lookID = cc.getString(cc.getColumnIndex(Contacts.LOOKUP_KEY));
			Utils.debug(lookID);
		}
		return lookID;
	}

	public String getContactInfo(Intent intent) {
		Cursor cursor = this.getContentResolver().query(intent.getData(), null, null, null, null);
		String temp = "";
		while (cursor.moveToNext()) {
			String contactId = cursor.getString(cursor.getColumnIndex(Contacts._ID));
			String id = cursor.getString(cursor.getColumnIndex(Contacts.LOOKUP_KEY));

			temp += "Id: " + id + "\n";
			temp += "Id contact: " + contactId + "\n";
			String name = cursor.getString(cursor.getColumnIndexOrThrow(Contacts.DISPLAY_NAME));
			temp += "Name of contact: " + name + "\n";
			String hasPhone = cursor.getString(cursor.getColumnIndex(Contacts.HAS_PHONE_NUMBER));
			if (hasPhone.equalsIgnoreCase("1"))
				hasPhone = "true";
			else
				hasPhone = "false";
			if (Boolean.parseBoolean(hasPhone)) {
				Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
				while (phones.moveToNext()) {
					String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					temp += "Contacts phone number: " + phoneNumber + "\n";
				}
				phones.close();
			}
			// Find Email Addresses
			Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);
			while (emails.moveToNext()) {
				String emailAddress = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
				temp += "Contacts email address: " + emailAddress + "\n";
			}
			emails.close();
		}
		cursor.close();

		return temp;
	}

	// Screen
	private Bitmap screen() {
		LinearLayout view = (LinearLayout) findViewById(R.id.mainpanelscreen);
		// SurfaceView view =
		// (it.unisa.microapp.editor.MainPanel)findViewById(R.id.surface);
		// FrameLayout view = (FrameLayout)findViewById(R.id.framescreen);
		view.setDrawingCacheEnabled(true);
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache(true);
		Bitmap b = Bitmap.createBitmap(view.getDrawingCache());
		view.setDrawingCacheEnabled(false);
		return b;
	}

	// cartella screenshot
	private void getScreenshotsFile() {

		// screen = main.getScreenShot();
		screen = screen();
		encodeScreenshot = encode(screen);

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		screen.compress(Bitmap.CompressFormat.PNG, 90, bytes);

		File f = new File(FileManagement.getScreenshotPath() + namefile + ".png");
		try {
			f.createNewFile();

			FileOutputStream fo = new FileOutputStream(f);
			fo.write(bytes.toByteArray());

			fo.close();
		} catch (Exception e) {
			Utils.error("Error screenshot file");
		}
	}

	// codifica screenshot
	private String encode(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.PNG, 90, baos);
		byte[] b = baos.toByteArray();

		return Base64.encodeToString(b, 0);
	}

	private void carica() {

		id = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
		String nick1 = "";

		MyThreadUT mythreadut = new MyThreadUT();
		mythreadut.start();
		while (mythreadut.isAlive() && !connect.isError()) {
		}

		if (connect.isError()) {
			handler.sendEmptyMessage(0);
			return;
		}

		if (jArray.length() != 0) {
			try {
				JSONObject obj = jArray.getJSONObject(0);
				nick1 = obj.getString("Nickname");
			} catch (JSONException e) {
				Utils.error(e);
			}

			MyThreadApp mythreadapp = new MyThreadApp();
			mythreadapp.start();
			while (mythreadapp.isAlive() && !connect.isError()) {
			}

			if (connect.isError()) {

				handler.sendEmptyMessage(0);
				return;
			}

			if (jArray2.length() != 0) {
				try {
					JSONObject obj = jArray2.getJSONObject(0);
					String id_app = obj.getString("Id_app");
					String vers = obj.getString("Versione");
					double v = Double.parseDouble(vers);
					v++;
					MyThreadUpVersion mythreadupversion = new MyThreadUpVersion(v, id_app);
					mythreadupversion.start();
					while (mythreadupversion.isAlive()) {
					}

				} catch (JSONException e) {
					Utils.error(e);
				}
			}

			if (!nick.equals(nick1)) {
				MyThreadUpUtente mythreaduputente = new MyThreadUpUtente(id, nick);
				mythreaduputente.start();
				while (mythreaduputente.isAlive() && !connect.isError()) {
				}

				if (connect.isError()) {
					handler.sendEmptyMessage(0);
					return;
				}
			}

			ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(new BasicNameValuePair("id_app", namefile + id));
			list.add(new BasicNameValuePair("nome_app", namefile));
			list.add(new BasicNameValuePair("autore", id));
			list.add(new BasicNameValuePair("descrizione", descrizione));
			list.add(new BasicNameValuePair("xml", xml));

			list.add(new BasicNameValuePair("img", encodeScreenshot));
			MyThreadInsert insert = new MyThreadInsert(list);
			insert.start();
			while (insert.isAlive() && !connect.isError()) {
			}

			if (connect.isError()) {
				handler.sendEmptyMessage(0);
				return;
			}
		} else {
			ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(new BasicNameValuePair("id_app", namefile + id));
			list.add(new BasicNameValuePair("nome_app", namefile));
			list.add(new BasicNameValuePair("autore", id));
			list.add(new BasicNameValuePair("descrizione", descrizione));
			list.add(new BasicNameValuePair("xml", xml));
			list.add(new BasicNameValuePair("nickname", nick));
			list.add(new BasicNameValuePair("id_ut", id));

			list.add(new BasicNameValuePair("img", encodeScreenshot));
			MyThreadInsert2 insert2 = new MyThreadInsert2(list);
			insert2.start();
			while (insert2.isAlive() && !connect.isError()) {
			}

			if (connect.isError()) {
				handler.sendEmptyMessage(0);
				return;
			}
		}

		Intent intent = new Intent(this, AppCaricataActivity.class);
		intent.putExtra("nick", nick);
		intent.putExtra("nomeapp", namefile);
		intent.putExtra("descrizione", descrizione);
		intent.putExtra("state", state);
		intent.putExtra("img", encodeScreenshot);
		startActivity(intent);
	}

	private void parsingXML() {
		String path = FileManagement.getLocalAppPath() + namefile + ".xml";

		try {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(path));
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(in);
			Parser p = new Parser(doc);
			xml = p.getXMLString();

		} catch (Exception e) {
			Utils.error("Parsing XML error", e);
		}

	}

	private class MyThreadUT extends Thread {
		public void run() {
			jArray = connect.resultQuery("SELECT * FROM Utenti WHERE Id LIKE " + "'" + id + "'");
		}
	}

	private class MyThreadApp extends Thread {
		public void run() {
			jArray2 = connect
					.resultQuery("SELECT Id_app, Nome_app, Versione FROM App JOIN Versione ON Id_app LIKE Id_appv JOIN Utenti ON Id LIKE Autore WHERE Id LIKE "
							+ "'" + id + "'" + " AND Nome_app LIKE " + "'" + namefile + "'");
		}
	}

	private class MyThreadUpVersion extends Thread {
		private double v;
		private String id_app;

		public MyThreadUpVersion(double vc, String id_appc) {
			this.v = vc;
			this.id_app = id_appc;
		}

		public void run() {
			connect.updateVersion("" + v, id_app, xml, descrizione);
		}
	}

	private class MyThreadUpUtente extends Thread {
		private String id_u;
		private String nick_u;

		public MyThreadUpUtente(String id, String nick) {
			this.id_u = id;
			this.nick_u = nick;
		}

		public void run() {
			connect.updateNick(id_u, nick_u);
		}
	}

	private class MyThreadInsert extends Thread {
		ArrayList<NameValuePair> l = new ArrayList<NameValuePair>();

		public MyThreadInsert(ArrayList<NameValuePair> l1) {
			this.l = l1;
		}

		public void run() {
			connect.insertQuery(l);
		}
	}

	private class MyThreadInsert2 extends Thread {
		ArrayList<NameValuePair> li = new ArrayList<NameValuePair>();

		public MyThreadInsert2(ArrayList<NameValuePair> li1) {
			this.li = li1;
		}

		public void run() {
			connect.insertQuery2(li);
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			progDailog.dismiss();

			AlertDialog.Builder builder2 = new AlertDialog.Builder(EditorActivity.this);
			builder2.setMessage("Connection error").setCancelable(false)
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
						}
					});
			AlertDialog alert2 = builder2.create();
			alert2.show();
		}
	};
}
