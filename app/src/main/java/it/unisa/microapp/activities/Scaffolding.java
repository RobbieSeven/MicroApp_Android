package it.unisa.microapp.activities;

import it.unisa.microapp.support.FileManagement;
import it.unisa.microapp.utils.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import dalvik.system.DexClassLoader;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

public abstract class Scaffolding extends MAActivity {
	Object ist;
	boolean flag = false;
	String cl = "";

	@Override
	protected void initialize(Bundle savedInstanceState) {
		ist = null;
		caricaClasse();

		if (ist != null) {
			try {
				Method[] mtd = ist.getClass().getDeclaredMethods();
				for (int i = 0; i < mtd.length; i++) {
					if (mtd[i].getName().equalsIgnoreCase("load")) {
						mtd[i].invoke(ist, application, mycomponent, Scaffolding.this, Scaffolding.this);
					}
				}
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
		}
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

	private void caricaClasse() {
		String classedacaricare = "";
		try {
			DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;

			db = df.newDocumentBuilder();

			File f_scaff = new File(FileManagement.getRepositoryPath(), Constants.ScaffoldingRepository);
			Document d = db.parse(new FileInputStream(f_scaff));
			NodeList l = d.getElementsByTagName("component");

			// LISTA DEGLI SCAFFOLDING
			for (int i = 0; i < l.getLength(); i++) {
				Element el = (Element) l.item(i);
				String scaffolding_id = el.getAttribute("id");
				String scaffolding_used = el.getAttribute("used");
				if (scaffolding_used.equalsIgnoreCase(this.getClass().getSimpleName())) {
					classedacaricare = scaffolding_id;
				}
			}

		} catch (ParserConfigurationException e1) {
		} catch (FileNotFoundException e) {
		} catch (SAXException e) {
		} catch (IOException e) {
		}

		flag = true;
		File storage = getDir("all41", Context.MODE_PRIVATE);
		System.err.println("instantiating DexClassLoader, loading class");
		DexClassLoader cl = new DexClassLoader(FileManagement.getAddedComponent() + "/" + classedacaricare + ".zip",
				storage.getAbsolutePath(), null, getClassLoader());
		System.err.println(classedacaricare);
		try {
			Class<?> libProviderClazz = cl.loadClass(classedacaricare);

			Object instance = libProviderClazz.newInstance();
			ist = (MAActivity) instance;
		} catch (Exception e) {
			System.err.println("Error while instanciating object: " + e.getMessage());
		}

	}

	@Override
	protected void initInputs() {
		if (ist != null) {
			try {
				Method[] mtd = ist.getClass().getDeclaredMethods();
				for (int i = 0; i < mtd.length; i++) {
					if (mtd[i].getName().equalsIgnoreCase("initInputs")) {
						mtd[i].invoke(ist);
					}
				}
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
		}
	}

	@Override
	protected void execute() {
		if (ist != null) {
			try {
				Method[] mtd = ist.getClass().getDeclaredMethods();
				for (int i = 0; i < mtd.length; i++) {
					if (mtd[i].getName().equalsIgnoreCase("behaviour")) {
						mtd[i].invoke(ist);
					}
				}
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
		}
	}

	@Override
	protected void beforeNext() {
		if (ist != null) {
			try {
				Method[] mtd = ist.getClass().getDeclaredMethods();
				for (int i = 0; i < mtd.length; i++) {
					if (mtd[i].getName().equalsIgnoreCase("beforeNext")) {
						mtd[i].invoke(ist);
					}
				}

			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
		}

	}
	/*
	 * private boolean isEmpty() { if(flag) { return false; } else { return
	 * true; } }
	 */

}
