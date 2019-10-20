package it.unisa.microapp.activities;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.bouncycastle.openssl.PEMParser;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import com.android.sdklib.build.ApkBuilder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import it.unisa.microapp.MicroAppGenerator;
import it.unisa.microapp.R;
import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.editor.BlankMapActivity;
import it.unisa.microapp.editor.GestureBuilderActivity;
import it.unisa.microapp.editor.MicroApp;
import it.unisa.microapp.editor.OptionsActivity;
import it.unisa.microapp.exceptions.InvalidComponentException;
import it.unisa.microapp.exceptions.NoMoreSpaceException;
import it.unisa.microapp.library.Library;
import it.unisa.microapp.project.ListProjectActivity;
import it.unisa.microapp.project.ProjectApp;
import it.unisa.microapp.project.ProjectTable;
import it.unisa.microapp.project.SensorApp;
import it.unisa.microapp.project.SensorAppActivity;
import it.unisa.microapp.store.DBvoti;
import it.unisa.microapp.store.SingletonParametersBridge;
import it.unisa.microapp.support.FileManagement;
import it.unisa.microapp.support.ScaffoldingManager;
import it.unisa.microapp.test.ComponentTester;
import it.unisa.microapp.test.NetworkTester;
import it.unisa.microapp.test.OptionsTester;
import it.unisa.microapp.test.UnimplementedTester;
import it.unisa.microapp.utils.Constants;
import it.unisa.microapp.utils.Utils;
import it.unisa.microapp.webservice.piece.WSSettings;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteStatement;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.provider.Settings.Secure;

public class First extends MAActivity {
	private boolean runtime = false;

	private Button b_avvia;
	private Button b_modifica;
	private Button b_cancella;
	private Button b_deploy;
//	private Button b_project;
	private Button b_run_project;
	private String textPath;
	private String description;
	private String android_id;
	private static DBvoti db = null;

	private boolean doubleBackToExitPressedOnce = false;

	public First() {
		activateEditingSpeech = false;
	}

	private void initialize() {
		new ScaffoldingManager();
		ScaffoldingManager.createScaffoldingXml();
		ScaffoldingManager.resetScaffoldingIndex();
		Utils.extractAssetsCompilation(this);

		String uri = "";
		if (application.getDeployPath() == null) {
			textPath = "";
			// description = "";
			b_avvia.setEnabled(false);
			b_modifica.setEnabled(false);
			b_cancella.setEnabled(false);
			b_deploy.setEnabled(false);
			b_run_project.setEnabled(false);
			uri = "@drawable/microapp";
		} else {
			ProjectTable table = new ProjectTable(this);
			table.open();
			SQLiteStatement ifExist = table.db().compileStatement(" SELECT COUNT(*) " + " FROM "
					+ ProjectTable.TABLE_PROJECT + " WHERE " + ProjectTable.NAME_PROJECT  + "=" + "'" + application.getDeployPath() + "';");
			long countIfExist = ifExist.simpleQueryForLong();
			if(countIfExist>0){
				textPath = application.getDeployPath();
				description = application.getDescription();
				b_avvia.setEnabled(false);
				b_modifica.setEnabled(false);
				b_cancella.setEnabled(false);
				b_deploy.setEnabled(false);
				uri = "@drawable/" + application.getIconPath();
				b_run_project.setEnabled(true);
			}
			else{
				textPath = application.getDeployPath();
				boolean runnable = !textPath.startsWith(Constants.tryFilePrefix);
				description = application.getDescription();
				b_avvia.setEnabled(runnable);
				b_modifica.setEnabled(runnable);
				b_cancella.setEnabled(true);
				b_deploy.setEnabled(runnable);
				uri = "@drawable/" + application.getIconPath();

				if (application.isDownloaded() != 0) {
					b_avvia.setEnabled(false);
					b_deploy.setEnabled(false);
				}

				b_run_project.setEnabled(false);
			}

		}
		TextView v = (TextView) findViewById(R.id.namefile);
		v.setText(textPath);
		TextView d = (TextView) findViewById(R.id.descfile);
		d.setText(description);

		ImageView image = (ImageView) findViewById(R.id.logoapp);
		int res = getResources().getIdentifier(uri, null, this.getPackageName());
		image.setImageResource(res);
		image.setPadding(0, 100, 0, 0);

		if (db == null) {
			db = new DBvoti(this);
			db.open();
			SingletonParametersBridge.getInstance().addParameter("db", db);
		}

		Constants.setScreenOffset(getBarHeight());
	}

	private int getBarHeight() {
		int StatusBarHeight = 0;
		int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			StatusBarHeight = getResources().getDimensionPixelSize(resourceId);
		}

		int TitleBarHeight = (int) Utils.convertDpToPixel(48, this);
		return StatusBarHeight + TitleBarHeight;
	}

	@Override
	protected void execute() {

	}

	@Override
	public void resume() {
		initialize();
		this.doubleBackToExitPressedOnce = false;
	}

	private void addShortcut(String filename, int resourceIcon, String name) {

		Intent shortcutIntent = new Intent(getApplicationContext(), First.class);

		shortcutIntent.setAction(Intent.ACTION_MAIN);

		Intent addIntent = new Intent();
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
				Intent.ShortcutIconResource.fromContext(getApplicationContext(), resourceIcon));
		addIntent.putExtra("filename", filename);

		// addIntent.putExtra("duplicate", false);

		addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		getApplicationContext().sendBroadcast(addIntent);

	}

	@SuppressWarnings("unused")
	private void delShortcut(String name) {
		Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");

		// Shortcut name
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);

		String appClass = this.getPackageName() + "." + this.getLocalClassName();
		ComponentName comp = new ComponentName(this.getPackageName(), appClass);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_MAIN).setComponent(comp));

		sendBroadcast(shortcut);
	}

	private void checkDeviceKeys(String id) {
		Utils.verbose("Device id: " + id);

		String[] keys = getResources().getStringArray(R.array.key);

		boolean found = true;
		for (String key : keys) {
			if (key.equals(id)) {
				found = true;
				break;
			}
		}

		//TODO: remove the statement
		found = true;
		
		if (!found) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setCancelable(false);
			builder.setIcon(android.R.drawable.stat_notify_error);
			builder.setTitle("Error");
			builder.setMessage(R.string.notRegistered);
			builder.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});

			AlertDialog dial = builder.create();
			dial.show();

		}
	}

	@Override
	protected void initialize(Bundle savedInstanceState) {

	}

	@Override
	protected void prepare() {

	}

	@Override
	protected int onVisible() {
		setContentView(R.layout.main);

		return 0;
	}

	@Override
	protected View onVisibleView() {

		return null;
	}

	@Override
	protected void prepareView(View v) {

	}

	public void onCreate(Bundle b) {
		super.onCreate(b);
		android_id = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
		checkDeviceKeys(android_id);

		TextView tv = (TextView) findViewById(R.id.benvv);
		if (tv != null) {
			tv.setText(Utils.buildVersion(this.getString(R.string.app_version), this.getString(R.string.buildnum)));
		}

		if (OptionsActivity.isFirstRun(this)) {
			description = "Thank you in advance for using "
					+ this.getString(R.string.app_fullname)
					+ ".\n\n"
					+ "This is the first time you run the application. Please, select and set important information available in the Options menu.";
		
			TextView d = (TextView) findViewById(R.id.descfile);
			d.setTextColor(Color.YELLOW);
		} else
			description = "";

		final Intent intent = getIntent();
		String extra = intent.getStringExtra("filename");

		if (extra != null) {
			Utils.debug("Running " + extra);

			try {
				runtime = true;
				application.setDeployPath(extra, true);

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
					checkSettingUpdate();
					finish();
				}
				try {
					Intent runi = new Intent(this, comp.getActivityClass());

					Bundle bnd = new Bundle();
					bnd.putString("description", comp.getDescription());
					bnd.putString("state", comp.getNowState());
					runi.putExtras(bnd);

					startActivityForResult(runi, 0);
				} catch (ClassNotFoundException e) {
					Utils.errorDialog(this, e.getMessage());
				}

			} catch (Exception e) {
				Utils.error(e.getMessage(), e);
			}
		}

		b_avvia = (Button) findViewById(R.id.avvia);
		b_modifica = (Button) findViewById(R.id.modifica_butt);
		b_cancella = (Button) findViewById(R.id.delete_butt);
		b_deploy = (Button) findViewById(R.id.butt_deploy);
		Button bott = (Button) findViewById(R.id.sfoglia);
		Button gesture = (Button) findViewById(R.id.editGestures);
		Button rgesture = (Button) findViewById(R.id.runGestures);
//		Button b_s_project  = (Button) findViewById(R.id.searchProject);
		b_run_project = (Button) findViewById(R.id.runProject);
		final Intent inten = new Intent(getApplicationContext(), GestureBuilderActivity.class);
		final Intent i = new Intent(getApplicationContext(), ListFileActivity.class);
		final Intent lunges = new Intent(getApplicationContext(), CatturaGesto.class);
		final Intent s_proj = new Intent(getApplicationContext(), ListProjectActivity.class);

//		b_s_project.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				startActivityForResult(s_proj, 0);
//			}
//		});

		bott.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivityForResult(i, 0);
			}
		});

		gesture.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(inten);
			}
		});

		rgesture.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(lunges);
			}
		});

		b_deploy.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View arg0) {
				String generated = "";
				generated += "package it.unisa.microapp;\n\n" +
						"import it.unisa.microapp.activities.MAActivity; \n" +
						"import it.unisa.microapp.components.MAComponent; \n" +
						"import it.unisa.microapp.data.DataType;\n" +
						"import it.unisa.microapp.support.ConcreteComponentCreator;\n" +
						"import it.unisa.microapp.support.DirEdge;\n" +
						"import java.util.HashMap;\n" +
						"import org.jgrapht.graph.DefaultDirectedGraph;\n" +
						"\n" +
						"\n" +
						"public class run2 extends MAActivity {\n" +
						"\n" +
						"	public void onResume() {\n" +
						"		super.onResume();\n" +
						"		init();\n" +
						"}\n" +
						"\n" +
						"	public void init() {;\n";

				try {
					generated += (application.getParser().createGraphCode());
				} catch (Exception e) {
				}

				generated += (("\napplication.initComponents(grafo,startComp);"));
				generated += ("\nnext();");
				generated += "\n" +
						"}\n" +
						"public void initInputs() {}\n" +
						"public void beforeNext() {} \n" +
						"};";

				String baseDir = FileManagement.getCompileSrc() + "/it/unisa/microapp/";
				File dir = new File(baseDir);
				dir.mkdirs();

				File outFile = new File(baseDir, "run2.java");

				try {

					FileOutputStream out = new FileOutputStream(outFile);
					out.write(generated.getBytes());
					out.flush();
					out.close();

					Toast.makeText(First.this, "Export effettuato con successo.", Toast.LENGTH_SHORT).show();

					//TODO: da decommentare qualora si volesse usare il compilatore di Ferraiolo-Cavalieri
					NewComponentActivity com = new NewComponentActivity();
					com.comp("run2.java");

					File fileApk = new File(baseDir + "/run.apk");
					File fileDex = new File(baseDir + "/classes.dex");
					//File fileRes = new File(baseDir + "/resources.ap_");
					File fileResZip = new File(baseDir + "/resources.zip");

					if (fileResZip.exists()) {
						Log.d("test", "esiste");
					} else {
						Log.d("test", "non esiste");
					}
					// add the resources

					FileInputStream fis = null;

					ByteArrayInputStream bais = null;
					X509Certificate cert = null;
					PrivateKey key = null;
					try {
						// use FileInputStream to read the file
						fis = new FileInputStream(FileManagement.getCompileSrc() + "/certificato.crt");
						File indKey = new File(baseDir + "/pkcs8_key");

						// read the bytes
						byte value[] = new byte[fis.available()];
						fis.read(value);
						bais = new ByteArrayInputStream(value);

						// get X509 certificate factory
						CertificateFactory certFactory = CertificateFactory.getInstance("X.509");

						// certificate factory can now create the certificate 
						cert = (X509Certificate) certFactory.generateCertificate(bais);

						BufferedReader br = new BufferedReader(new FileReader(indKey));


						//trovare modo per prendere la chiave
						PEMParser parser = new PEMParser(br);
						Object obj = parser.readObject();
						KeyPair kp = (KeyPair) obj;

						key = kp.getPrivate();
						parser.close();

					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						fis.close();
						bais.close();
					}

					ApkBuilder apkB = new ApkBuilder(fileApk, fileResZip, fileDex, key, cert, System.err);

					//apkB.addResourcesFromJar(new File(baseDir + "/microapp.jar"));
					//addZipFile(fileResZip);
					apkB.addSourceFolder(new File(baseDir + "/res"));
					apkB.setDebugMode(false);
					apkB.sealApk();

				} catch (Exception e) {
					e.printStackTrace();
				}

				return true;
			}

		});	
		
		b_avvia.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					application.initComponents();
				} catch (InvalidComponentException e) {
					Utils.errorDialog(First.this, getString(R.string.notRunnable), e.getMessage());

					if (runtime) {
						finish();
					}
					return;
				} catch (NoMoreSpaceException e) {
					Utils.errorDialog(First.this, getString(R.string.notRunnable), e.getMessage());

					if (runtime) {
						finish();
					}
					return;
				}
				next();
			}

		});

		b_run_project.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),SensorAppActivity.class);
                Bundle b = new Bundle();
                b.putString("namefile", textPath);
                i.putExtras(b);
                startActivity(i);
			}

		});

		final First cl = this;
		b_cancella.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (application.getDeployPath() != null) {
					final String filename = application.getDeployPath();
					AlertDialog.Builder dlgAlert = new AlertDialog.Builder(cl);

					dlgAlert.setMessage("Delete '" + filename + "' file?");
					dlgAlert.setTitle("Delete file");

					DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case DialogInterface.BUTTON_POSITIVE:

								File f = new File(FileManagement.getLocalAppPath(), filename);
								if (f.exists()) {
									f.delete();
									try {
										application.setDeployPath(null, false);
										boolean flag = false;
										GestureLibrary store = GestureLibraries.fromFile(FileManagement.getGestureDir());

										if (store.load())

											for (String name : store.getGestureEntries()) {

												for (Gesture gesture : store.getGestures(name)) {

													if ((filename).equals(name) || (filename + "�^").equals(name)) {
														store.removeGesture(name, gesture);
														store.save();
														flag = true;

													}

													if (flag)
														break;

												}
											}

									} catch (Exception e) {
									}
									initialize();
								}
								String[] screen = filename.split(".xml");
								File f1 = new File(FileManagement.getScreenshotPath(), screen[0] + ".png");
								if (f1.exists()) {
									f1.delete();
								}

								break;

							case DialogInterface.BUTTON_NEGATIVE:
								break;
							}
						}
					};

					dlgAlert.setPositiveButton(android.R.string.ok, dialogClickListener);
					dlgAlert.setNegativeButton(R.string.cancel, dialogClickListener);
					dlgAlert.setCancelable(true);
					dlgAlert.create().show();

				}
			}
		});

		if (!runtime) {

			// addGestureShortcut();

			// creo il repository per i web service
			FileManagement.checkDirectory();

			File f = new File(FileManagement.getRepositoryPath(), Constants.WSRepository);

			if (!f.exists()) {
				try {
					f.createNewFile();
					FileOutputStream fileos = new FileOutputStream(f);
					XmlSerializer serializer = Xml.newSerializer();
					serializer.setOutput(fileos, "UTF-8");
					serializer.startDocument(null, null);
					serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

					String[] categories = Library.getCategories(this);
					String[] icon = Library.getIcons(this);

					serializer.startTag(null, "document");

					for (int j = 0; j < categories.length; j++) {
						String cat = categories[j];
						serializer.startTag(null, "category");
						serializer.attribute(null, "name", cat);
						serializer.attribute(null, "icon", icon[j]);
						serializer.endTag(null, "category");
					}

					serializer.endTag(null, "document");

					serializer.endDocument();
					serializer.flush();
				} catch (IOException e) {
					Utils.error("Repository creation error", e);
					Utils.errorDialog(this, "Repository creation error");
				}
				
			}
				f = new File(FileManagement.getRepositoryPath(), Constants.DRepository);

				if (!f.exists()) {
					try {
						f.createNewFile();
						FileOutputStream fileos = new FileOutputStream(f);
						XmlSerializer serializer = Xml.newSerializer();
						serializer.setOutput(fileos, "UTF-8");
						serializer.startDocument(null, null);
						serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

						serializer.startTag(null, "document");
						serializer.startTag(null, "category");
						serializer.attribute(null, "name", "Domotic");
						serializer.attribute(null, "icon", "R.drawable.domotic48");
						serializer.endTag(null, "category");

						serializer.endTag(null, "document");

						serializer.endDocument();
						serializer.flush();
					} catch (IOException e) {
						Utils.error("Repository creation error", e);
						Utils.errorDialog(this, "Repository creation error");
					}				
				
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, getString(R.string.about)).setIcon(android.R.drawable.ic_menu_info_details);
		menu.add(0, 1, 1, getString(R.string.setting)).setIcon(android.R.drawable.ic_menu_manage);
		menu.add(0, 2, 2, getString(R.string.previewMap)).setIcon(android.R.drawable.ic_menu_mapmode);
		menu.add(0, 3, 3, getString(R.string.test)).setIcon(android.R.drawable.ic_menu_sort_by_size);
		menu.add(0, 4, 4, getString(R.string.update)).setIcon(android.R.drawable.ic_menu_rotate);
		menu.add(0, 5, 5, getString(R.string.restore)).setIcon(android.R.drawable.ic_menu_delete);
		menu.add(0, 6, 6, getString(R.string.legal)).setIcon(android.R.drawable.ic_menu_view);
		menu.add(0, 7, 7, getString(R.string.exit)).setIcon(android.R.drawable.ic_lock_power_off);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			this.about();
			break;
		case 1:
			this.options();
			break;
		case 2:
			this.testMap();
			break;
		case 3:
			this.test();
			break;
		case 4:
			this.update();
			break;
		case 5:
			this.restore();
			break;
		case 6:
			this.legal();
			break;
		case 7:
			this.exit();
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	public void initInputs() {
		return;
	}

	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			super.onBackPressed();
			finish();
			return;
		}
		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, "Please back once more to exit", Toast.LENGTH_SHORT).show();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				doubleBackToExitPressedOnce = false;

			}
		}, 2000);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Constants.ID_EXIT) {
			application.reset();
			checkSettingUpdate();
			finish();
		}

		if (resultCode == Constants.ID_TERMINATED) {
			application.reset();

			checkSettingUpdate();

			if (runtime) {

				try {
					application.setDeployPath(null, false);
				} catch (Exception e) {
				}
				finish();
			}
		}

		if (runtime) {
			checkSettingUpdate();
			try {
				application.setDeployPath(null, false);
			} catch (Exception e) {
			}
			finish();
		}
	}

	@Override
	public void beforeNext() {
	}

	public void newDeploy(View v) throws ParserConfigurationException, SAXException, IOException, Exception {
		boolean flag = false;
		if (application.getDeployPath() != null) {
			String path = application.getDeployPath();
			try {
				File xmlUrl = new File(FileManagement.getLocalAppPath(), path);
				DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document doc = db.parse(xmlUrl);
				doc.getDocumentElement().normalize();
				Element root = doc.getDocumentElement();

				NodeList activator = root.getElementsByTagName("activator");

				GestureLibrary store = GestureLibraries.fromFile(FileManagement.getGestureDir());
				if (activator.item(0).getTextContent().contains("GestureActivator")) {
					boolean flag2 = false;
					if (store.load())
						flag = true;

					for (String name : store.getGestureEntries()) {

						for (Gesture gesture : store.getGestures(name)) {

							if ((path).equals(name)) {
								store.removeGesture(name, gesture);
								store.addGesture(name + "�^", gesture);
								store.save();
								flag2 = true;
								break;
							}

							if (flag2)
								break;

						}
					}

				}
			}

			catch (SAXException e) {

			} catch (IOException e) {

			} catch (ParserConfigurationException e) {

			} catch (FactoryConfigurationError e) {
			}

			catch (Exception e) {

			}
			if (!flag) {

				int resIcon = getResources().getIdentifier(application.getIconPath(), "drawable", getPackageName());
				addShortcut(FileManagement.getLocalAppPath() + path, resIcon, path.substring(0, path.lastIndexOf('.')));
			}

		}
	}

	public void newEditing(View v) {
		MicroAppGenerator app = (MicroAppGenerator) getApplication();

		app.setGesture(null);
		app.setFlagGesture(false);
		app.setGestureName("");
		Intent i = new Intent(getApplicationContext(), MicroApp.class);
		startActivity(i);
	}

	public void newProject(View v) {
		MicroAppGenerator app = (MicroAppGenerator) getApplication();

		app.setGesture(null);
		app.setFlagGesture(false);
		app.setGestureName("");
		Intent i = new Intent(getApplicationContext(), ProjectApp.class);
		startActivity(i);
	}

	public void newSensor(View v) {
		MicroAppGenerator app = (MicroAppGenerator) getApplication();

		app.setGesture(null);
		app.setFlagGesture(false);
		app.setGestureName("");
		Intent i = new Intent(getApplicationContext(), SensorApp.class);
		startActivity(i);
	}


	public void modifica(View v) {
		if (application.getDeployPath() != null) {
			MicroAppGenerator app = (MicroAppGenerator) getApplication();
			// imposto il gesto a null
			app.setGesture(null);
			// imposto il flag gesto a null
			app.setFlagGesture(false);
			// imposto la stringa del gesto a ""
			app.setGestureName("");
			Intent i = new Intent(getApplicationContext(), MicroApp.class);
			Bundle b = new Bundle();
			b.putString("namefile", textPath);
			i.putExtras(b);
			startActivity(i);

		}
	}

	private void about() {

		List<String> strings = new ArrayList<String>();

		String nick = OptionsActivity.getString(Constants.TEXT_NICKNAME_KEY, this);
		if (!nick.equals("")) {
			nick = "\n\n" + this.getString(R.string.nickname) + ": " + nick;
		}
		strings.add(this.getString(R.string.app_fullname) + "\n" + this.getString(R.string.app_location) + "\n"
				+ this.getString(R.string.version) + ": "
				+ Utils.buildVersion(this.getString(R.string.app_version), this.getString(R.string.buildnum)) + "\n"
				+ this.getString(R.string.builddate) + nick);

		String[] team = getResources().getStringArray(R.array.team);
		strings.addAll(Arrays.asList(team));
		strings.add(Utils.version() + "Device id: " + android_id + "\n" + this.getString(R.string.label_language) + ": "
				+ this.getString(R.string.language));

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(this.getString(R.string.about) + " " + this.getString(R.string.app_name));

		ViewFlipper flip = new ViewFlipper(this);

		for (int i = 0; i < strings.size(); i++) {
			TextView txt = new TextView(this);
			txt.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
			txt.setLines(7);
			txt.setText(strings.get(i));
			txt.setTextSize(TypedValue.COMPLEX_UNIT_PT, 9);
			flip.addView(txt);
		}

		Animation anim = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -1.0f);

		int duration = 220;
		anim.setDuration(duration);
		anim.setInterpolator(new AccelerateInterpolator());

		flip.setOutAnimation(anim);

		anim = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
				1.0f, Animation.RELATIVE_TO_PARENT, 0.0f);

		anim.setDuration(duration);
		anim.setInterpolator(new AccelerateInterpolator());
		flip.setInAnimation(anim);
		builder.setView(flip);
		flip.startFlipping();
		builder.setNeutralButton(android.R.string.ok, null).setIcon(R.drawable.app_icon);
		builder.create().show();
	}

	private void legal() {
		String notes = GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(this);

		final ScrollView sview = new ScrollView(getApplicationContext());
		final TextView tview = new TextView(getApplicationContext());
		tview.setText(notes);
		tview.setTextSize(9);
		sview.addView(tview);

		AlertDialog.Builder LicenseDialog = new AlertDialog.Builder(this);
		LicenseDialog.setTitle(getString(R.string.legal)).setView(sview).setIcon(R.drawable.app_icon);
		LicenseDialog.setNeutralButton(android.R.string.ok, null);
		LicenseDialog.show();
	}

	private void options() {
		Intent in = new Intent(this, OptionsActivity.class);
		this.startActivity(in);
	}

	private void restore() {
		final MAActivity f = this;
		AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);

		dlgAlert.setMessage("All data including microapps, components, web services, images, gestures and preferences will be removed.\nDo you want continue?");
		dlgAlert.setTitle(this.getString(R.string.restore));

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					// remove all data
					FileManagement.remove();
					OptionsActivity.removePreferences(f);
					f.exit();
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					break;
				}
			}
		};

		dlgAlert.setPositiveButton(this.getString(R.string.yes), dialogClickListener);
		dlgAlert.setNegativeButton(this.getString(R.string.no), dialogClickListener);
		dlgAlert.setCancelable(false);
		dlgAlert.create().show();
	}

	private void update() {
		// TODO: call to check the updated APK
		//Intent in = new Intent(this, AutoUpdateApkActivity.class);
		//this.startActivity(in);
	}

	private void testMap() {
		if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext()) == ConnectionResult.SUCCESS) {
			Intent in = new Intent(this, BlankMapActivity.class);
			this.startActivity(in);
		} else {
			Toast.makeText(this, "GooglePlay Services are not available", Toast.LENGTH_LONG).show();
		}
	}

	private void test() {
		boolean hasError = false;
		ComponentTester ct = new ComponentTester(this);
		String ret = ct.runTest();
		if (!ret.equals("")) {
			hasError = true;
			Toast.makeText(this, ret, Toast.LENGTH_LONG).show();
		}

		UnimplementedTester ut = new UnimplementedTester(this);
		ret = ut.runTest();
		if (!ret.equals("")) {
			Toast.makeText(this, ret, Toast.LENGTH_LONG).show();
		}

		OptionsTester ot = new OptionsTester(this.getApplication());
		ret = ot.runTest();
		if (!ret.equals("")) {
			hasError = true;
			Toast.makeText(this, ret, Toast.LENGTH_LONG).show();
		}

		NetworkTester nt = new NetworkTester(this.getApplication());
		ret = nt.runTest();
		if (!ret.equals("")) {
			hasError = true;
			Toast.makeText(this, ret, Toast.LENGTH_LONG).show();
		}

		if (!hasError)
			Toast.makeText(this, "Tests end correctly!", Toast.LENGTH_LONG).show();
	}

	protected void checkSettingUpdate() {
		boolean update = false;

		Document doc = openFile(FileManagement.getLocalAppPath() + textPath);

		if (doc == null)
			finish();

		List<MAComponent> components = application.getComponentList();

		for (MAComponent comp : components) {
			if (comp.getType().startsWith("WEBSERVICE")) {
				if (comp.isUpdate()) {
					updateFile(doc, comp.getId(), comp.getSettings());
					update = true;
				}
			}
		}

		if (update)
			saveOntoDevice((Element) doc.getFirstChild(), new File(FileManagement.getLocalAppPath() + textPath));
	}

	protected void saveOntoDevice(Element root, File f) {
		FileOutputStream fileos;
		try {
			fileos = new FileOutputStream(f);
			XmlSerializer serializer = Xml.newSerializer();
			serializer.setOutput(fileos, "UTF-8");
			serializer.startDocument(null, null);
			serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

			preorder(serializer, root);

			serializer.endDocument();
			serializer.flush();

		} catch (FileNotFoundException e) {
			Utils.error(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			Utils.error(e.getMessage(), e);
		} catch (IllegalStateException e) {
			Utils.error(e.getMessage(), e);
		} catch (IOException e) {
			Utils.error(e.getMessage(), e);
		}
	}

	private void preorder(XmlSerializer serializer, Element root) {
		try {
			serializer.startTag(null, root.getNodeName());

			if (root.hasAttributes()) {
				NamedNodeMap attr = root.getAttributes();
				for (int i = 0; i < attr.getLength(); i++) {
					Attr at = (Attr) attr.item(i);
					serializer.attribute(null, at.getName(), at.getNodeValue());
				}
			}

			NodeList list = root.getChildNodes();

			for (int i = 0; i < list.getLength(); i++) {
				if (list.item(i) instanceof Element)
					preorder(serializer, (Element) list.item(i));
				else {
					Text txt = (Text) list.item(i);
					serializer.text(txt.getTextContent());
				}
			}

			serializer.endTag(null, root.getNodeName());
		} catch (IllegalArgumentException e) {
			Utils.error(e.getMessage(), e);
		} catch (IllegalStateException e) {
			Utils.error(e.getMessage(), e);
		} catch (IOException e) {
			Utils.error(e.getMessage(), e);
		}
	}

	protected void updateFile(Document doc, String id, WSSettings set) {
		Element root = (Element) doc.getFirstChild();

		NodeList list = root.getElementsByTagName("component");

		for (int i = 0; i < list.getLength(); i++) {
			Element elem = (Element) list.item(i);
			String ids = elem.getAttribute("id");

			if (ids.equals(id)) {
				Element setting = (Element) elem.getElementsByTagName("settings").item(0);

				if (setting == null) {
					setting = doc.createElement("settings");
					setting.setAttribute("dotNet", "" + set.isDotNet());
					setting.setAttribute("implicitType", "" + set.isImplicitType());
					setting.setAttribute("timeout", "" + set.getTimeout());

					elem.appendChild(setting);
				} else {
					setting.setAttribute("dotNet", "" + set.isDotNet());
					setting.setAttribute("implicitType", "" + set.isImplicitType());
					setting.setAttribute("timeout", "" + set.getTimeout());
				}
			}
		}

	}

	protected Document openFile(String path) {
		File f = new File(path);
		Document doc = null;
		try {
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;
			docBuilder = dbfac.newDocumentBuilder();
			doc = docBuilder.parse(f);
		} catch (SAXException e) {
			Utils.error(e);
		} catch (IOException e) {
			Utils.error(e);
		} catch (ParserConfigurationException e) {
			Utils.error(e);
		}
		return doc;
	}

}
