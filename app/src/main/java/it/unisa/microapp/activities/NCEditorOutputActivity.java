package it.unisa.microapp.activities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import it.unisa.microapp.R;
//import it.unisa.microapp.editor.EditorActivity;
import it.unisa.microapp.support.FileManagement;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class NCEditorOutputActivity extends Activity {
	
	EditText beforenext;
	String n_Activity;
	Button bnt;
	String InputString, BehaviourString;
	
//	Intent init = getIntent();{
//	InputString = init.getStringExtra("InEditor");
//	BehaviourString = init.getStringExtra("BehaEditor");
//	}
	
	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		Bundle extras = getIntent().getExtras();
		n_Activity = extras.getString("n_Activity");
		InputString = extras.getString("InEditor");
		BehaviourString = extras.getString("BehaEditor");
		
		setContentView(R.layout.nceditoroutput);
		
		beforenext = (EditText)findViewById(R.id.edbeforenextet);
		
		bnt = (Button) findViewById(R.id.save_nceditor);
		bnt.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				
				deleteFolder(new File(FileManagement.getCompileSrc()));
				//new File(FileManagement.getCompileSrc()).mkdir();
				FileManagement.checkDirectory();
				File f = new File(FileManagement.getDefaultPath(),"activityBase.java");

				File newactivity = new File(FileManagement.getCompileSrc(), n_Activity+".java");

				try {
					newactivity.createNewFile();
					InputStream fstream = new FileInputStream(f);
					// prendo l'inputStream
					DataInputStream in = new DataInputStream(fstream);
					BufferedReader br = new BufferedReader(new InputStreamReader(in));
					String strLine;
					StringBuilder fileContent = new StringBuilder();

					// Leggo il file riga per riga
					while ((strLine = br.readLine()) != null) {

						//Controllo stringa
						if(strLine.contains("public class activityBase extends MAActivity")) {
							// se la riga � uguale a quella ricercata
							String nome_classe = "public class "+ n_Activity +" extends MAActivity";

							fileContent.append(System.getProperty("line.separator"));
							fileContent.append(nome_classe);
							fileContent.append(System.getProperty("line.separator"));

						} else if(strLine.contains("initInputs() {")) {
							fileContent.append(strLine);
							fileContent.append(System.getProperty("line.separator"));
							fileContent.append(InputString);
							fileContent.append(System.getProperty("line.separator"));
							fileContent.append("behaviour();");
							fileContent.append(System.getProperty("line.separator"));
						} else if(strLine.contains("behaviour() {")) {
							fileContent.append(strLine);
							fileContent.append(System.getProperty("line.separator"));
							fileContent.append(BehaviourString);
							fileContent.append(System.getProperty("line.separator"));
						} else if(strLine.contains("beforeNext() {")) {
							fileContent.append(strLine);
							fileContent.append(System.getProperty("line.separator"));
							fileContent.append(beforenext.getText().toString());
							fileContent.append(System.getProperty("line.separator"));
						} else {
							fileContent.append(strLine);
							fileContent.append(System.getProperty("line.separator"));
						}
					}
					// Sovrascrivo il file con il nuovo contenuto (aggiornato)
					FileWriter fstreamWrite;

					fstreamWrite = new FileWriter(newactivity);
					BufferedWriter out = new BufferedWriter(fstreamWrite);
					out.write(fileContent.toString());
					
					fstream.close();
					out.flush();
					out.close();
					in.close();
					
					//Intent intent = new Intent();
				   // intent.putExtra("DaEditor", "eccomi");
				    //intent.putExtra("filename", f.getAbsolutePath());
				    //setResult(RESULT_OK, intent);
				    
				    //TODO: chiamare direttamente NewComponentActivity e gestire il parametro filename
				    //finish();
					Intent intent=new Intent(NCEditorOutputActivity.this, NewComponentActivity.class);
				    intent.putExtra("filename", newactivity.getName());
					
				    NCEditorOutputActivity.this.startActivity(intent);
				} catch (IOException e) {
				}
			}

		});
	}

	public static void deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if(files!=null) { //some JVMs return null for empty dirs
			for(File f: files) {
				if(f.isDirectory()) {
					deleteFolder(f);
				} else {
					f.delete();
				}
			}
		}
		folder.delete();
	}

	@Override
	public void onBackPressed() {
		Intent intent=new Intent();
	    intent.putExtra("DaEditor", "backpressed");
	    setResult(RESULT_OK, intent);
	    finish();
	}
	
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		menu.add(3, 1, 1, getString(R.string.test)).setIcon(android.R.drawable.ic_media_play);
		menu.add(3, 2, 2, getString(R.string.save)).setIcon(android.R.drawable.ic_menu_save);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case 1:
			test();
			break;
		case 2:
			bnt.performClick();
			break;
		default:
			break;
		}
		return true;
	}
	
	public void test() {
		
		beforenext.setText("for (DataType dt : DataType.values()) {\n" +
				"Iterable<GenericData<?>> dit = application.getData(mycomponent.getId(), dt);\n" +
				"if(dit != null)\n" +
				"{\n" +
				"for(GenericData<?> d : dit)\n" +
				"application.putDataInObject(mycomponent, d);\n" +
				"}\n" +
				"}", TextView.BufferType.EDITABLE);
	}
	
}
