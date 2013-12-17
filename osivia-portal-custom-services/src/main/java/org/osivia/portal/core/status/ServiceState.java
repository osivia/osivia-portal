package org.osivia.portal.core.status;


public class ServiceState {
	
	private long lastCheckTimestamp = 0L;
	private boolean serviceUp = true;
	
	private String url = "";
	

    private boolean mustBeChecked = false;
    
    public boolean isMustBeChecked() {
        return mustBeChecked;
    }

    public void setMustBeChecked(boolean mustBeChecked) {
        this.mustBeChecked = mustBeChecked;
    }	
	

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
