package org.osivia.portal.api.status;


public interface IStatusService {

	public void notifyError(String url, UnavailableServer e);
	public boolean isReady(String url);


}
