package it.unisa.microapp.activities;

import java.util.ArrayList;

import it.unisa.microapp.R;
import it.unisa.microapp.editor.NewCompPossibleStates;
import it.unisa.microapp.library.Library;
import it.unisa.microapp.library.LibraryParser;
import it.unisa.microapp.utils.Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class NewComponentActivity extends Activity {
	EditText et, desc;
	String[] Icon;
	String[] Icons;
	String valoreSpinner, nameFile;
	boolean flag = false;
	ArrayList<String> inputList = new ArrayList<String>();
	ArrayList<String> outputList = new ArrayList<String>();
	String listates;

	public void onCreate(Bundle b) {
		super.onCreate(b);
		String filename = null;

		Bundle extras = getIntent().getExtras();
		if (extras != null)
			filename = extras.getString("filename");

		if (filename == null) {
			setContentView(R.layout.prova);

			// se troviamo il nomefile swith se diverso da null chiama next()
			Icon = Library.getCategories(this);
			Icons = Library.getIcons(this);

			et = (EditText) findViewById(R.id.name_nc);

			desc = (EditText) findViewById(R.id.description_nc);

			Spinner s = (Spinner) findViewById(R.id.spinner);
			s.setAdapter(new MyCustomAdapter(this, R.layout.rigaspinner, Icon,
					Icons));
			s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view,
						int pos, long id) {
					valoreSpinner = parent.getItemAtPosition(pos).toString();
				}

				public void onNothingSelected(AdapterView<?> parent) {
				}
			});

			Button add_nc = (Button) findViewById(R.id.add_newcategory);
			add_nc.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {

					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							NewComponentActivity.this);

					final EditText input = new EditText(
							NewComponentActivity.this);
					// set title
					alertDialogBuilder.setTitle("New Category");

					// set dialog message
					alertDialogBuilder
							.setCancelable(false)
							.setView(input)
							.setNegativeButton("Cancel",
									new DialogInterface.OnClickListener() {

										public void onClick(
												DialogInterface dialog,
												int which) {

										}
									})
							.setPositiveButton("Create",
									new DialogInterface.OnClickListener() {

										public void onClick(
												DialogInterface dialog,
												int which) {
											LibraryParser.addCategory(input
													.getText().toString());
											aggiorna();

											Toast.makeText(
													NewComponentActivity.this,
													"Category created: "
															+ input.getText()
																	.toString(),
													Toast.LENGTH_LONG).show();
										}

									});
					AlertDialog alert = alertDialogBuilder.create();
					alert.show();
				}
			});

			Button bnt = (Button) findViewById(R.id.save_nc);
			bnt.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					nextDef();
				}
			});

		} else { // compila
			next(filename);
		}

	}

	public void aggiorna() {
		Library.initialize(this);
		finish();
		startActivity(this.getIntent());
	}

	public void nextDef() {
		Intent i = new Intent(this, NewCompPossibleStates.class);
		this.startActivityForResult(i, 1);
	}

	public void nextToEditor() {
		Intent i = new Intent(this, NCEditorInputActivity.class);
		i.putExtra("nameActivity", et.getText().toString());
		final int result = 1;
		this.startActivityForResult(i, result);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 1) {
			super.onActivityResult(requestCode, resultCode, data);
			if (data.getSerializableExtra("DaEditor") == null) {

				@SuppressWarnings("unchecked")
				ArrayList<ArrayList<String>> res = (ArrayList<ArrayList<String>>) data
						.getSerializableExtra("ComingFrom");
				@SuppressWarnings("unchecked")
				ArrayList<CharSequence> pes = (ArrayList<CharSequence>) data
						.getSerializableExtra("ComingFromPS");

				inputList = res.get(0);
				outputList = res.get(1);

				// inizio modifica
				for (int i = 0; i < pes.size(); i++) {
					Utils.debug(pes.get(i).toString());
					if (i == pes.size() - 1) {
						listates += pes.get(i);
						break;
					}
					listates += pes.get(i) + "/";
				}
				// fine modifica

				nextToEditor();
			} else if (data.getSerializableExtra("DaEditor").equals(
					"backpressed")) {

			} else if (data.getSerializableExtra("filename") != null) {
				// next(filename);
			} else
				System.err.println("File vuoto!");
		}
	}

	public void comp(String javaName) {
		
	}	
	//TODO: attivare PINNA
/*	
	public void comp(String javaName) {

		// COMPILAZIONE
		System.err.println("Sto compilando!");

		// org.eclipse.jdt.core.compiler.CompilationProgress progress; //
		// instantiate your subclass
		// org.eclipse.jdt.core.compiler.CompilationProgress progress = new
		// CompilerProgress();

		boolean completed = org.eclipse.jdt.core.compiler.batch.BatchCompiler
				.compile(
						"-source 1.5 -warn:suppress -classpath" + " "
								+ FileManagement.getDefaultPath()
								+ "android.jar:"
								+ FileManagement.getDefaultPath()
								+ "/ma_classes.jar:"
								+ FileManagement.getDefaultPath()
								+ "/jgrapht-jdk1.6.jar" + " "
								+ FileManagement.getCompileSrc()
								+ "it/unisa/microapp/"+javaName,
						new PrintWriter(System.out),
						new PrintWriter(System.err),
						new org.eclipse.jdt.core.compiler.CompilationProgress() {
							private long startTime = -1;
							private long finishTime = -1;

							@Override
							public void worked(int workIncrement,
									int remainingWork) {
								// TODO Auto-generated method stub
								System.out.println("WORKED: " + workIncrement
										+ " - " + remainingWork);
							}

							@Override
							public void setTaskName(String name) {
								// TODO Auto-generated method stub
								System.out.println("SETTASKNAME: " + name);
							}

							@Override
							public boolean isCanceled() {
								return false;
							}

							@Override
							public void done() {
								this.finishTime = System.currentTimeMillis();
								long duration = this.getDuration();
								System.out
										.println("Finita compilazione.. Durata: "
												+ duration);
								// TODO Auto-generated method stub
								// System.out.println("DONE");
							}

							@Override
							public void begin(int remainingWork) {
								this.startTime = System.currentTimeMillis();
								// TODO Auto-generated method stub
								System.out.println("BEGIN: " + remainingWork);
							}

							public long getDuration() {
								return this.finishTime - this.startTime;
							}

						});

		if (completed) {
			System.err.println("Compilato.");

			System.err.println("calling DEX and dexifying the test class");
			com.android.dx.command.Main.main(new String[] {
					"--dex",
					"--output=" + FileManagement.getAddedComponent()
							+ "run2.zip", FileManagement.getCompileSrc() });
			// FINE COMPILAZIONE

			// setta il primo spazio libero nello scaffolding con l'id del nuovo
			// componente
			ScaffoldingManager.setIdNewComponent(et.getText().toString());

			Toast.makeText(NewComponentActivity.this, et.getText().toString(),
					Toast.LENGTH_LONG).show();

			File f = new File(FileManagement.getDefaultPath(),
					"componentiAggiunti.xml");
			String valorespinner_tolower = valoreSpinner.toLowerCase();
			String icona = "R.drawable." + valorespinner_tolower + "48";

			String[] keywords = Library.getKeyWordForCategory(this,
					valoreSpinner);
			String chiavi = "";
			for (int c = 0; c < keywords.length; c++) {
				chiavi = chiavi + " " + keywords[c];
			}

			if (f.exists()) {
				try {
					InputStream is = new FileInputStream(f);
					DocumentBuilderFactory docFactory = DocumentBuilderFactory
							.newInstance();
					DocumentBuilder docBuilder = docFactory
							.newDocumentBuilder();
					Document doc = docBuilder.parse(is);

					NodeList document = doc.getElementsByTagName("document");
					Element el = (Element) document.item(0);

					Element nuovoelemento = doc.createElement("category");
					nuovoelemento.setAttribute("name", valoreSpinner);
					nuovoelemento.setAttribute("icon", icona);
					nuovoelemento.setAttribute("keywords", chiavi);

					Element function = doc.createElement("function");
					function.setAttribute("name", et.getText().toString());
					function.setAttribute("id", et.getText().toString());

					Element descrizione = doc.createElement("description");
					descrizione.setTextContent(desc.getText().toString());

					function.appendChild(descrizione);

					Element inputs = doc.createElement("inputs");
					// LISTA DEGLI INPUT e USERINPUT
					for (int a = 0; a < inputList.size(); a++) {
						String[] stringa_divisa = inputList.get(a).split(" - ");
						if (stringa_divisa[2].equalsIgnoreCase("input")) {
							Element input = doc.createElement("input");
							if (stringa_divisa[3].equals("multi")) {
								input.setAttribute("multi", "true");
							}
							input.setAttribute("type", stringa_divisa[0]);
							input.setAttribute("name", stringa_divisa[1]);
							inputs.appendChild(input);

						} else if (stringa_divisa[2]
								.equalsIgnoreCase("userinput")) {
							Element userinput = doc.createElement("userinput");
							if (!stringa_divisa[4].equals("Seleziona")) {
								userinput.setAttribute("type",
										stringa_divisa[3]);
							}
							userinput.setAttribute("type", stringa_divisa[0]);
							userinput.setAttribute("name", stringa_divisa[1]);
							inputs.appendChild(userinput);
						}
					}

					Element outputs = doc.createElement("outputs");
					// LISTA DEGLI OUTPUT
					for (int b = 0; b < outputList.size(); b++) {
						Element output = doc.createElement("output");
						String[] stringa_divisa = outputList.get(b)
								.split(" - ");
						output.setAttribute("type", stringa_divisa[0]);
						output.setAttribute("name", stringa_divisa[1]);
						outputs.appendChild(output);
					}

					function.appendChild(inputs);
					function.appendChild(outputs);

					nuovoelemento.appendChild(function);

					el.appendChild(nuovoelemento);

					TransformerFactory transformerFactory = TransformerFactory
							.newInstance();
					Transformer transformer = transformerFactory
							.newTransformer();
					DOMSource source = new DOMSource(doc);
					StreamResult result = new StreamResult(
							new FileOutputStream(f));
					transformer.transform(source, result);

					System.err.println("Done");

				} catch (IOException e) {
					Utils.error("Repository creation error", e);
					Utils.errorDialog(this, "Repository creation error");
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TransformerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					f.createNewFile();
					FileOutputStream fileos = new FileOutputStream(f);
					XmlSerializer serializer = Xml.newSerializer();
					serializer.setOutput(fileos, "UTF-8");
					serializer.startDocument(null, null);
					serializer
							.setFeature(
									"http://xmlpull.org/v1/doc/features.html#indent-output",
									true);

					serializer.startTag(null, "document");

					String cat = valoreSpinner;
					serializer.startTag(null, "category");
					serializer.attribute(null, "name", cat);
					serializer.attribute(null, "icon", icona);

					serializer.startTag(null, "function");
					serializer.attribute(null, "name", et.getText().toString());
					serializer.attribute(null, "id", et.getText().toString());

					serializer.startTag(null, "description");
					serializer.text(desc.getText().toString());
					serializer.endTag(null, "description");

					serializer.startTag(null, "inputs");

					// LISTA DEGLI INPUT e USERINPUT
					for (int k = 0; k < inputList.size(); k++) {
						String[] stringa_divisa = inputList.get(k).split(" - ");
						if (stringa_divisa[2].equalsIgnoreCase("input")) {
							serializer.startTag(null, "input");
							serializer.attribute(null, "type",
									stringa_divisa[0]);
							serializer.attribute(null, "name",
									stringa_divisa[1]);
							serializer.endTag(null, "input");

						} else if (stringa_divisa[2]
								.equalsIgnoreCase("userinput")) {
							serializer.startTag(null, "userinput");
							serializer.attribute(null, "type",
									stringa_divisa[0]);
							serializer.attribute(null, "name",
									stringa_divisa[1]);
							serializer.endTag(null, "userinput");
						}
					}
					serializer.endTag(null, "inputs");
					serializer.startTag(null, "outputs");
					// LISTA DEGLI OUTPUT
					for (int b = 0; b < outputList.size(); b++) {
						String[] stringa_divisa = outputList.get(b)
								.split(" - ");
						serializer.startTag(null, "output");
						serializer.attribute(null, "type", stringa_divisa[0]);
						serializer.attribute(null, "name", stringa_divisa[1]);
						serializer.endTag(null, "output");
					}
					serializer.endTag(null, "outputs");

					serializer.endTag(null, "function");

					serializer.endTag(null, "category");

					serializer.endTag(null, "document");

					serializer.endDocument();

					serializer.flush();

				} catch (IOException e) {
					Utils.error("Repository creation error", e);
					Utils.errorDialog(NewComponentActivity.this,
							"Repository creation error");
				}
			}

			Library.initialize(this);
			finish();
		} else {

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					NewComponentActivity.this);
			// set title
			alertDialogBuilder.setTitle("ERROR");

			// set dialog message
			alertDialogBuilder
					.setMessage("Compilation error")
					.setCancelable(false)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
								}

							});
			AlertDialog alert = alertDialogBuilder.create();
			alert.show();
		}

	}
*/
	public void next(String filename) {
		
	}
	//TODO: attivare PINNA
/*	
	public void next(String filename) {

		filename = filename.substring(0, filename.length() - 5);
		// COMPILAZIONE
		Utils.verbose("Sto compilando:" + filename);

		boolean completed = org.eclipse.jdt.core.compiler.batch.BatchCompiler
				.compile(
						"-source 1.5 -warn:suppress -classpath" + " "
								+ FileManagement.getDefaultPath()
								+ "android.jar:"
								+ FileManagement.getDefaultPath()
								+ "ma_classes.jar" + " "
								+ FileManagement.getCompileSrc() + filename
								+ ".java",
						new PrintWriter(System.out),
						new PrintWriter(System.err),
						new org.eclipse.jdt.core.compiler.CompilationProgress() {
							private long startTime = -1;
							private long finishTime = -1;

							@Override
							public void worked(int workIncrement,
									int remainingWork) {
								Utils.verbose("WORKED: " + workIncrement
										+ " - " + remainingWork);
							}

							@Override
							public void setTaskName(String name) {
								Utils.verbose("SETTASKNAME: " + name);
							}

							@Override
							public boolean isCanceled() {
								return false;
							}

							@Override
							public void done() {
								this.finishTime = System.currentTimeMillis();
								long duration = this.getDuration();
								Utils.verbose("Finita compilazione.. Durata: "
										+ duration);
							}

							@Override
							public void begin(int remainingWork) {
								this.startTime = System.currentTimeMillis();
								Utils.verbose("BEGIN: " + remainingWork);
							}

							public long getDuration() {
								return this.finishTime - this.startTime;
							}

						});

		if (completed) {
			Utils.verbose("Compilato.");

			System.err.println("calling DEX and dexifying the test class");
			com.android.dx.command.Main.main(new String[] {
					"--dex",
					"--output=" + FileManagement.getAddedComponent() + filename
							+ ".zip", FileManagement.getCompileSrc() });
			// FINE COMPILAZIONE

			// setta il primo spazio libero nello scaffolding con l'id del nuovo
			// componente
			ScaffoldingManager.setIdNewComponent(et.getText().toString());

			Toast.makeText(NewComponentActivity.this, et.getText().toString(),
					Toast.LENGTH_LONG).show();

			File f = new File(FileManagement.getDefaultPath(),
					"componentiAggiunti.xml");
			String valorespinner_tolower = valoreSpinner.toLowerCase();
			String icona = "R.drawable." + valorespinner_tolower + "48";

			String[] keywords = Library.getKeyWordForCategory(this,
					valoreSpinner);
			String chiavi = "";
			for (int c = 0; c < keywords.length; c++) {
				chiavi = chiavi + " " + keywords[c];
			}

			if (f.exists()) {
				try {
					InputStream is = new FileInputStream(f);
					DocumentBuilderFactory docFactory = DocumentBuilderFactory
							.newInstance();
					DocumentBuilder docBuilder = docFactory
							.newDocumentBuilder();
					Document doc = docBuilder.parse(is);

					NodeList document = doc.getElementsByTagName("document");
					Element el = (Element) document.item(0);

					Element nuovoelemento = doc.createElement("category");
					nuovoelemento.setAttribute("name", valoreSpinner);
					nuovoelemento.setAttribute("icon", icona);
					nuovoelemento.setAttribute("keywords", chiavi);

					Element function = doc.createElement("function");
					function.setAttribute("name", et.getText().toString());
					function.setAttribute("id", et.getText().toString());
					function.setAttribute("states", listates);

					Element descrizione = doc.createElement("description");
					descrizione.setTextContent(desc.getText().toString());

					function.appendChild(descrizione);

					Element inputs = doc.createElement("inputs");
					// LISTA DEGLI INPUT e USERINPUT
					for (int a = 0; a < inputList.size(); a++) {
						String[] stringa_divisa = inputList.get(a).split(" - ");
						if (stringa_divisa[2].equalsIgnoreCase("input")) {
							Element input = doc.createElement("input");
							if (stringa_divisa[3].equals("multi")) {
								input.setAttribute("multi", "true");
							}
							input.setAttribute("type", stringa_divisa[0]);
							input.setAttribute("name", stringa_divisa[1]);
							inputs.appendChild(input);

						} else if (stringa_divisa[2]
								.equalsIgnoreCase("userinput")) {
							Element userinput = doc.createElement("userinput");
							if (!stringa_divisa[4].equals("Seleziona")) {
								userinput.setAttribute("type",
										stringa_divisa[3]);
							}
							userinput.setAttribute("type", stringa_divisa[0]);
							userinput.setAttribute("name", stringa_divisa[1]);
							inputs.appendChild(userinput);
						}
					}

					Element outputs = doc.createElement("outputs");
					// LISTA DEGLI OUTPUT
					for (int b = 0; b < outputList.size(); b++) {
						Element output = doc.createElement("output");
						String[] stringa_divisa = outputList.get(b)
								.split(" - ");
						output.setAttribute("type", stringa_divisa[0]);
						output.setAttribute("name", stringa_divisa[1]);
						outputs.appendChild(output);
					}

					function.appendChild(inputs);
					function.appendChild(outputs);

					nuovoelemento.appendChild(function);

					el.appendChild(nuovoelemento);

					TransformerFactory transformerFactory = TransformerFactory
							.newInstance();
					Transformer transformer = transformerFactory
							.newTransformer();
					DOMSource source = new DOMSource(doc);
					StreamResult result = new StreamResult(
							new FileOutputStream(f));
					transformer.transform(source, result);

					System.err.println("Done");

				} catch (IOException e) {
					Utils.error("Repository creation error", e);
					Utils.errorDialog(this, "Repository creation error");
				} catch (SAXException e) {
				} catch (ParserConfigurationException e) {
				} catch (TransformerException e) {
				}
			} else {
				try {
					f.createNewFile();
					FileOutputStream fileos = new FileOutputStream(f);
					XmlSerializer serializer = Xml.newSerializer();
					serializer.setOutput(fileos, "UTF-8");
					serializer.startDocument(null, null);
					serializer
							.setFeature(
									"http://xmlpull.org/v1/doc/features.html#indent-output",
									true);

					serializer.startTag(null, "document");

					String cat = valoreSpinner;
					serializer.startTag(null, "category");
					serializer.attribute(null, "name", cat);
					serializer.attribute(null, "icon", icona);

					serializer.startTag(null, "function");
					serializer.attribute(null, "name", et.getText().toString());
					serializer.attribute(null, "id", et.getText().toString());
					serializer.attribute(null, "states", listates);

					serializer.startTag(null, "description");
					serializer.text(desc.getText().toString());
					serializer.endTag(null, "description");

					serializer.startTag(null, "inputs");

					// LISTA DEGLI INPUT e USERINPUT
					for (int k = 0; k < inputList.size(); k++) {
						String[] stringa_divisa = inputList.get(k).split(" - ");
						if (stringa_divisa[2].equalsIgnoreCase("input")) {
							serializer.startTag(null, "input");
							serializer.attribute(null, "type",
									stringa_divisa[0]);
							serializer.attribute(null, "name",
									stringa_divisa[1]);
							serializer.endTag(null, "input");

						} else if (stringa_divisa[2]
								.equalsIgnoreCase("userinput")) {
							serializer.startTag(null, "userinput");
							serializer.attribute(null, "type",
									stringa_divisa[0]);
							serializer.attribute(null, "name",
									stringa_divisa[1]);
							serializer.endTag(null, "userinput");
						}
					}
					serializer.endTag(null, "inputs");
					serializer.startTag(null, "outputs");
					// LISTA DEGLI OUTPUT
					for (int b = 0; b < outputList.size(); b++) {
						String[] stringa_divisa = outputList.get(b)
								.split(" - ");
						serializer.startTag(null, "output");
						serializer.attribute(null, "type", stringa_divisa[0]);
						serializer.attribute(null, "name", stringa_divisa[1]);
						serializer.endTag(null, "output");
					}
					serializer.endTag(null, "outputs");

					serializer.endTag(null, "function");

					serializer.endTag(null, "category");

					serializer.endTag(null, "document");

					serializer.endDocument();

					serializer.flush();

				} catch (IOException e) {
					Utils.error("Repository creation error", e);
					Utils.errorDialog(NewComponentActivity.this,
							"Repository creation error");
				}
			}

			Library.initialize(this);
			finish();
		} else {

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					NewComponentActivity.this);
			// set title
			alertDialogBuilder.setTitle("ERROR");

			// set dialog message
			alertDialogBuilder
					.setMessage("Compilation error")
					.setCancelable(false)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
								}

							});
			AlertDialog alert = alertDialogBuilder.create();
			alert.show();
		}
	}
*/
	public void onBackPressed() {
		finish();
	}

	public class MyCustomAdapter extends ArrayAdapter<String> {
		Object[] objects;
		String[] fixedIcon;

		public MyCustomAdapter(Context context, int textViewResourceId,
				String[] objects, String[] fixedIcon) {
			super(context, textViewResourceId, objects);
			this.objects = objects;// lista nomi
			this.fixedIcon = fixedIcon;
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}

		public View getCustomView(int position, View convertView,
				ViewGroup parent) {

			String[] items = (String[]) this.objects;
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.rigatextview, parent, false);

			TextView label = (TextView) row.findViewById(R.id.iconanomeT);
			label.setText(items[position]);

			ImageView icon = (ImageView) row.findViewById(R.id.iconaT);
			String key = fixedIcon[position];
			Log.d("ICON", "icon key 820" + key);
			key = key.substring(11);
			int id = getResources().getIdentifier(key, "drawable",
					getPackageName());

			if (id > 0) {
				icon.setImageResource(id);
			} else
				icon.setImageResource(R.drawable.icon);

			return row;
		}
	}
}
