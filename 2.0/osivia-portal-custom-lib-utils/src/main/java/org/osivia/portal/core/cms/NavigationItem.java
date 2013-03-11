package org.osivia.portal.core.cms;

import java.util.ArrayList;
import java.util.List;

import org.osivia.portal.core.cms.CMSItem;




public class NavigationItem {
	
	private Object mainDoc;
	private List<Object> children;
	private CMSItem adaptedCMSItem;
	private boolean unfetchedChildren =  false;
	
	public boolean isUnfetchedChildren() {
		return unfetchedChildren;
	}


	public void setUnfetchedChildren(boolean unfetchedChildren) {
		this.unfetchedChildren = unfetchedChildren;
	}


	public Object getMainDoc() {
		return mainDoc;
	}

	
	public CMSItem getAdaptedCMSItem() {
		return adaptedCMSItem;
	}
	public void setAdaptedCMSItem(CMSItem adaptedCMSItem) {
		this.adaptedCMSItem = adaptedCMSItem;
	}
	public void setMainDoc(Object mainDoc) {
		this.mainDoc = mainDoc;
	}
	public List<Object> getChildren() {
		return children;
	}

	public void setChildren(List<Object> children) {
		this.children = children;
	}
	public NavigationItem() {
		super();
		children = new ArrayList<Object>();
	}
	
	

}
