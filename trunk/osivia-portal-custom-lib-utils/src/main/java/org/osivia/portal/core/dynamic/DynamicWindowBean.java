package org.osivia.portal.core.dynamic;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;

public class DynamicWindowBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1448856007420313534L;
	
	protected PortalObjectId windowId  = null;
	public PortalObjectId getWindowId() {
		return windowId;
	}

	protected PortalObjectId pageId = null;
	protected String name = null;
	protected String uri = null;
	public String uniqueID = null;
	
	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}

	private String initialPageMarker = null;

	public String getInitialPageMarker() {
		return initialPageMarker;
	}


	public String getUniqueID() {
		return uniqueID;
	}


	public void setInitialPageMarker(String initialPageMarker) {
		this.initialPageMarker = initialPageMarker;
	}

	Map<String, String> properties;
	
	public DynamicWindowBean(PortalObjectId pageId, String name, String uri, Map<String, String> properties, String pageMarker) {
		super();
		
		windowId = new PortalObjectId("", new PortalObjectPath(pageId.getPath().toString()
				.concat("/").concat(name), PortalObjectPath.CANONICAL_FORMAT));


		this.pageId = pageId;
		this.name = name;
		this.uri = uri;
		this.properties = properties;
		this.initialPageMarker = pageMarker;
		this.uniqueID = "w_" + System.currentTimeMillis();
	}

		
	public String getName() {
		return name;
	}



	public String getUri() {
		return uri;
	}


	
	public Map<String, String> getProperties() {
		return properties;
	}

	public PortalObjectId getPageId() {
		return pageId;
	}




}
