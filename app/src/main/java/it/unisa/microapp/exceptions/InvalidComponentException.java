package it.unisa.microapp.exceptions;

public class InvalidComponentException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidComponentException(String type){
		super("The component type '"+type+"' is not expected by the application");
	}
	
}
