package it.unisa.microapp.filters;

import it.unisa.microapp.utils.Constants;

import java.io.File;
import java.io.FilenameFilter;

public class PamFilter implements FilenameFilter {

	public boolean accept(File dir, String name) {
		return (name.endsWith(Constants.extension) && (!name.startsWith("._")));
	}

}
