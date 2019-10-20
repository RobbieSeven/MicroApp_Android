package it.unisa.microapp.store;

public class Downloaded {
	
	private String nomeapp;
	private int votato;
	private boolean myapp;
	
	public boolean isMyapp() {
		return myapp;
	}

	public void setMyapp(boolean myapp) {
		this.myapp = myapp;
	}

	public int getVotato() {
		return votato;
	}

	public void setVotato(int votato) {
		this.votato = votato;
	}

	public Downloaded(String nomeapp){
		this.nomeapp = nomeapp;
	}

	public String getNomeapp() {
		return nomeapp;
	}

	public void setNomeapp(String nomeapp) {
		this.nomeapp = nomeapp;
	}

}
