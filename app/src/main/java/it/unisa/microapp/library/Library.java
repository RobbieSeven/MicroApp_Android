package it.unisa.microapp.library;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import it.unisa.microapp.R;

public class Library {

	private static Map<String, ArrayList<LibraryComponent>> components=new HashMap<String, ArrayList<LibraryComponent>>();
	private static String fileName="";

	public static void initialize(Context a){

		try {
			components = null;
			components=new HashMap<String, ArrayList<LibraryComponent>>();
			components=LibraryParser.parse(a.getResources().openRawResource(R.raw.component));
		} catch (NotFoundException e) {
		} catch (ParserConfigurationException e) {
		} catch (SAXException e) {
		} catch (IOException e) {
		}

	}


	public static String getFileName() {
		return fileName;
	}

	public static void setFileName(String f) {
		fileName = f;
	}

	public static boolean isParsed(){
		return components.size()!=0;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<LibraryComponent> getFunctionByCategory(Context cnx, String category) {
		if (!isParsed()) 
			initialize(cnx);
		ArrayList<LibraryComponent> list=(ArrayList<LibraryComponent>) components.get(category).clone();
		if(!fileName.isEmpty())
			LibraryParser.getFunctionFromRepository(category,fileName,list);
		return list;
	}

	public static String[] getCategories(Context cnx){
		if (!isParsed()) initialize(cnx);
		ArrayList <String> categories=LibraryParser.getCategoryList();
		return categories.toArray(new String[categories.size()]);
	}

	public static String[] getIcons(Context cnx){
		if (!isParsed()) initialize(cnx);
		ArrayList <String> icons=LibraryParser.getIconList();
		return icons.toArray(new String[icons.size()]);
	}

	public static String[] getFunctions(Context cnx){
		if (!isParsed()) initialize(cnx);
		ArrayList <String> functions=LibraryParser.getFunctionList();
		return functions.toArray(new String[functions.size()]);
	}	

	public static String[] getKeyWordForCategory(Context cnx,String category)
	{
		if(!isParsed())
			initialize(cnx);

		return LibraryParser.getKeywordsForCategory(category);
	}


}
