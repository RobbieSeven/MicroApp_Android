package it.unisa.microapp.support;

import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.exceptions.InvalidComponentException;
import it.unisa.microapp.exceptions.NoMoreSpaceException;

public interface ComponentCreator {

	public MAComponent createComponentService(String type, String id, String description) throws InvalidComponentException, NoMoreSpaceException;

}
