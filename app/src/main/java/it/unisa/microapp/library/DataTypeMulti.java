package it.unisa.microapp.library;

import it.unisa.microapp.data.DataType;

public class DataTypeMulti {
	private DataType types;
	private boolean multi;
	private String name;

	public DataTypeMulti(DataType types, boolean multi, String name) {
		this.types = types;
		this.multi = multi;
		this.name = name;
	}

	public DataType getType() {
		return types;
	}

	public String getMulti() {
		if (multi)
			return "MULTI";
		else
			return "NORMAL";
	}

	public boolean isMulti() {
		return multi;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "DataTypeMulti [types=" + types + ", multi=" + multi + ", name="+name+"]";
	}

}
