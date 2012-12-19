package fr.toutatice.portail.api.urls;

public class Link {
	
	private boolean external = false;
	
	private String url;

	public boolean isExternal() {
		return external;
	}

	public void setExternal(boolean external) {
		this.external = external;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Link( String url, boolean external) {
		super();
		this.external = external;
		this.url = url;
	}
	
	
	

}
