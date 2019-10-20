package it.unisa.microapp.test;

import java.util.ArrayList;

import android.app.Activity;

import it.unisa.microapp.components.BlankComponent;
import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.exceptions.InvalidComponentException;
import it.unisa.microapp.exceptions.NoMoreSpaceException;
import it.unisa.microapp.library.Library;
import it.unisa.microapp.support.ConcreteComponentCreator;

public class UnimplementedTester {
	Activity context;
	ArrayList<String> arr = new ArrayList<String>();
	
	public UnimplementedTester(Activity context) {
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
				comp = creator.createComponentService(function, id, description);
				if(comp instanceof BlankComponent)
				{		
					String verb = function;
					arr.add(verb);
				}			
			} catch (InvalidComponentException e) {
			} catch (NoMoreSpaceException e) {
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
