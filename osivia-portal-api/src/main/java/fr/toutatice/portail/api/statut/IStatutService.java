package fr.toutatice.portail.api.statut;


public interface IStatutService {

	public void notifyError(String url, ServeurIndisponible e);
	public boolean isReady(String url);


}
