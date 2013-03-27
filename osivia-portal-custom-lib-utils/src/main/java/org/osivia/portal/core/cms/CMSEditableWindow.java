package org.osivia.portal.core.cms;

import java.util.Map;

public class CMSEditableWindow {

	String name;
	String applicationID;
	Map<String,String> applicationProperties;
	
	public CMSEditableWindow(String name, String applicationID, Map<String, String> applicationProperties) {
		super();
		this.name = name;
		this.applicationID = applicationID;
		this.applicationProperties = applicationProperties;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getApplicationID() {
		return applicationID;
	}
	public void setApplicationID(String applicationID) {
		this.applicationID = applicationID;
	}
	public Map<String, String> getApplicationProperties() {
		return applicationProperties;
	}
	public void setApplicationProperties(Map<String, String> applicationProperties) {
		this.applicationProperties = applicationProperties;
	}
	
	

}
