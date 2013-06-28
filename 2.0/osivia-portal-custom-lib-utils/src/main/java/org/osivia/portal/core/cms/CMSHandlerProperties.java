package org.osivia.portal.core.cms;

import java.util.Map;

public class CMSHandlerProperties {
	

	private String externalUrl;
	private String portletInstance;
	Map<String, String> windowProperties;	
	
	public String getPortletInstance() {
		return portletInstance;
	}

	public void setPortletInstance(String portletInstance) {
		this.portletInstance = portletInstance;
	}

	public String getExternalUrl() {
		return externalUrl;
	}

	public void setExternalUrl(String externalUrl) {
		this.externalUrl = externalUrl;
	}

	
	public Map<String, String> getWindowProperties() {
		return windowProperties;
	}

	public void setWindowProperties(Map<String, String> windowProperties) {
		this.windowProperties = windowProperties;
	}

	public String getUrl() {
		return externalUrl;
	}

	public void setUrl(String url) {
		this.externalUrl = url;
	}




}
