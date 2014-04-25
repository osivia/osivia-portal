package org.osivia.portal.core.statut;


public class ServiceState {
	
	private long lastCheckTimestamp = 0L;
	private boolean serviceUp = true;
	
	// v2.0.21
	private boolean mustBeChecked = false;
	
	public boolean isMustBeChecked() {
		return mustBeChecked;
	}

	public void setMustBeChecked(boolean mustBeChecked) {
		this.mustBeChecked = mustBeChecked;
	}

	private String url = "";
	

	public long getLastCheckTimestamp() {
		return lastCheckTimestamp;
	}

	public void setLastCheckTimestamp(long lastCheckTimestamp) {
		this.lastCheckTimestamp = lastCheckTimestamp;
	}
	
	public boolean isServiceUp() {
		return serviceUp;
	}
	


	public void setServiceUp(boolean serviceUp) {
		this.serviceUp = serviceUp;
	}

	public ServiceState( String url) {
		super();
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	
}