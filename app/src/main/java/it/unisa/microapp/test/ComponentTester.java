package it.unisa.microapp.test;

import java.util.ArrayList;


import android.app.Activity;

import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.exceptions.InvalidComponentException;
import it.unisa.microapp.exceptions.NoMoreSpaceException;
import it.unisa.microapp.library.Library;
import it.unisa.microapp.support.ConcreteComponentCreator;
import it.unisa.microapp.utils.Utils;

public class ComponentTester {
	Activity context;
	ArrayList<String> arr = new ArrayList<String>();
	
	public ComponentTester(Activity context) {
			this.context = context;	
	}

	public void run()
 	{
		String id = "";
		String description = "";
		MAComponent comp;
		ConcreteComponentCreator creator = new ConcreteComponentCreator();
				
		String[] functions = Library.getFunctions(context);		
		for(String function : functions)
		{
			try {
				Utils.verbose(function);
				comp = creator.createComponentService(function, id, description);
				try {
					comp.getActivityClass();
				} catch (ClassNotFoundException e) {
					String verb = "Invalid component-activity: "+comp.getClass().getCanonicalName();
					Utils.verbose(verb);
					arr.add(verb);
				}			
			} catch (InvalidComponentException e) {
				String verb = "Invalid component-activity for: "+function;
				Utils.verbose(verb);
				arr.add(verb);				
			} catch (NoMoreSpaceException e) {
				String verb = "No more space component-activity for: "+function;
				Utils.verbose(verb);
				arr.add(verb);
			}
		}				
 	}

	public String runTest() {
		String ret ="";
		arr.clear();
		
		run();
		
		for(String s : arr)
		{
			ret = ret + s + "\n";
		}
		
		return ret;
	}

}
