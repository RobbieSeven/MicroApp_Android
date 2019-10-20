package it.unisa.microapp.webservice.rpc;

public abstract class Request 
{
	/**
	 * metodo usato per inizializare le varie strutture dati
	 * necessarie alla comunicazione
	 */
	protected abstract void init();
	
	/**
	 * metodo usato per creare la connessione con la peer entity o il server
	 * Qualora la comunicazione non prevede connessione non sovrascrivere il metodo
	 */
	protected abstract void connect();
	
	/**
	 * metodo che si occupa della comunicazione con la peer entity o il server
	 * @return -1 in caso di errore 0 in caso di successo
	 */
	protected abstract int execute();
	
	/**
	 * metodo che si occupa del rilascio delle risorse. Qualora la comunicazione 
	 * non prevede connessione non sovrascrivere il metodo
	 */
	protected abstract void disconnect();
	
	public int sendRequest()
	{
		int ret;
		init();
		
		connect();
		
		ret=execute();
		
		disconnect();
		
		return ret;
	}
}
