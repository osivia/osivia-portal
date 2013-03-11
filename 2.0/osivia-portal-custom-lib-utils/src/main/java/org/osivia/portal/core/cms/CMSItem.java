package org.osivia.portal.core.cms;

import java.util.Map;

public class CMSItem {
	
	private Object nativeItem;
	private String path;	
	private Map<String, String> properties;	
	
	public Object getNativeItem() {
		return nativeItem;
	}


	public String getPath() {
		return path;
	}

		
	public Map<String, String> getProperties() {
		return properties;
	}

	public CMSItem(String path,  Map<String, String> properties, Object nativeItem) {
		super();

		this.path = path;
		this.properties = properties;
		this.nativeItem = nativeItem;
	}


}
