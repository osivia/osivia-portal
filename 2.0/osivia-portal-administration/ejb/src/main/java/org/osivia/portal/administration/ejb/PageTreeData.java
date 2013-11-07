package org.osivia.portal.administration.ejb;

import org.jboss.portal.core.model.portal.PortalObjectId;


public class PageTreeData {

	private String pageId;
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getPageId() {
		return pageId;
	}

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	

	public PageTreeData(String pageId, String name) {
		super();
		this.pageId = pageId;
		this.name = name;
	}

	@Override
	public String toString() {
		return getName();
	}

}
