package org.osivia.portal.core.dynamic;

import java.io.Serializable;
import java.util.Map;

import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;

public class DynamicPageBean implements Serializable {
	

	
	private static final long serialVersionUID = 3801681183296702167L;
	
	protected PortalObjectId pageId  = null;
	protected PortalObjectId parentId  = null;	
	protected PortalObjectId templateId = null;
	protected int order= -1;
	public String uniqueID = null;


	protected Map displayNames;
	   
	protected boolean closable = true;
	
	public boolean isClosable() {
		return closable;
	}

	public void setClosable(boolean closable) {
		this.closable = closable;
	}

	public Map getDisplayNames() {
		return displayNames;
	}

	public void setDisplayNames(Map displayNames) {
		this.displayNames = displayNames;
	}

	public String getUniqueID() {
		return uniqueID;
	}

	public static final int DYNAMIC_PRELOADEDPAGES_FIRST_ORDER = 500;
	
	public static final int DYNAMIC_PAGES_FIRST_ORDER = 1000;
	
	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public PortalObjectId getTemplateId() {
		return templateId;
	}

	public void setTemplateId(PortalObjectId templateId) {
		this.templateId = templateId;
	}


	// v1.1
	Map<String, String> pageProperties;


	public Map<String, String> getPageProperties() {
		return pageProperties;
	}

	public void setPageProperties(Map<String, String> pageProperties) {
		this.pageProperties = pageProperties;
	}

	public PortalObjectId getPage() {
		return pageId;
	}

	public PortalObjectId getParentId() {
		return parentId;
	}


	protected String name = null;


	public DynamicPageBean(PortalObject parent, String name, Map displayName, PortalObjectId templateId, Map<String, String> pageProperties) {
		super();
		
		pageId = new PortalObjectId("", new PortalObjectPath(parent.getId().getPath().toString()
				.concat("/").concat(name), PortalObjectPath.CANONICAL_FORMAT));


		this.parentId = parent.getId();
		this.name = name;
		this.displayNames = displayName;
		this.templateId = templateId;
		this.pageProperties = pageProperties;
		this.uniqueID = "p_" + System.currentTimeMillis();


	}
		
	public String getName() {
		return name;
	}


	public PortalObjectId getPageId() {
		return pageId;
	}



}
