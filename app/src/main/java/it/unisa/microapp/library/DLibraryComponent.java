package it.unisa.microapp.library;

import java.util.Arrays;

public class DLibraryComponent extends LibraryComponent {
	private static final long serialVersionUID = -865835905042081469L;
	private String state;
	private String link;
	private String name;
	private String type;

	public DLibraryComponent(String idlog, String n, String cat, String icon) {
		super(idlog, n, cat, icon);

	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String toString() {
		return "DLibraryComponent [name=" + this.getName() + ", category=" + this.getCategory() + ", name:" + name + ", state:" + state
				+ ", link:" + link + ", type:" + type + ", inputs=" + Arrays.toString(inputs) + ", outputs=" + Arrays.toString(outputs)
				+ ", userInputs=" + Arrays.toString(userInputs) + "]";
	}

}
