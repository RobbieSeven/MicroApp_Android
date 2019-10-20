package it.unisa.microapp.exceptions;

public class NoMoreSpaceException extends Exception {

	private static final long serialVersionUID = 1L;

	public NoMoreSpaceException(int ind){
		super("No more space for new component. You can only use " + ind + " new component");
	}
	
}
