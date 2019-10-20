package it.unisa.microapp.library;

import it.unisa.microapp.data.DataType;

public class DataUserType{
	private DataType types;
	private String name;
	private String input;
	
	public DataUserType(DataType types,String name, String input){
		this.types=types;
		this.name=name;
		this.input=input;
	}
	public DataType getType() {
		return types;
	}
	
	public String getName() {
		return name;
	}
		
	public String getInput() {
		return input;
	}

	@Override
	public String toString() {
		return "DataUserType [types=" + types + ", name=" + name + ", input=" + input + "]";
	}

}
