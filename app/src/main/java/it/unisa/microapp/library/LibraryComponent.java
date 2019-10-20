package it.unisa.microapp.library;

import it.unisa.microapp.editor.StateManagement;

import java.io.Serializable;
import java.util.Arrays;

public class LibraryComponent extends StateManagement implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2864178394609282237L;

	private String idLogic;
	private String name;
	private String category;
	private String icon;
	private String description;

	DataTypeMulti[] inputs, outputs;
	DataUserType[] userInputs;

	public LibraryComponent(String idlog, String n, String cat, String icon)
	{
		this.idLogic = idlog;
		this.name = n;
		this.category = cat;
		this.icon = icon;
		this.description = "";
		this.nowState = "visible"; 
		this.allStates = "";
	}

	public String getIdLogic()
	{
		return idLogic;
	}	
	
	public String getName()
	{
		return name;
	}

	public String getCategory()
	{
		return category;
	}

	public String getIcon()
	{
		return icon;
	}	
		
	public DataTypeMulti[] getInputList()
	{
		return inputs;
	}

	public DataTypeMulti[] getOutputList()
	{
		return outputs;
	}

	public DataUserType[] getUserInputList()
	{
		return userInputs;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}	
	
	@Override
	public String toString()
	{
		return "LibraryComponent [name=" + name + ", category=" + category + ", inputs=" + Arrays.toString(inputs) + ", outputs="
				+ Arrays.toString(outputs) + ", userInputs=" + Arrays.toString(userInputs) + "]";
	}

}
