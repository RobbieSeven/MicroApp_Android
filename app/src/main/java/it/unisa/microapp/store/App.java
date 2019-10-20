package it.unisa.microapp.store;

import android.graphics.Bitmap;

public class App {

	private String name;
	private String desc;
	private double voto;
	private Bitmap image;
	private String autore;

	public App(String name, String desc, double voto, Bitmap image, String autore) {
		this.name = name;
		this.desc = desc;
		this.voto = voto;
		this.image = image;
		this.autore = autore;
	}

	public String getAutore() {
		return autore;
	}

	public void setAutore(String autore) {
		this.autore = autore;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public double getVoto() {
		return voto;
	}

	public void setVoto(double voto) {
		this.voto = voto;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}
	
}
