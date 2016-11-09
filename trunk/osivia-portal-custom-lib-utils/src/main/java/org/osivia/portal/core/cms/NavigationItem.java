/*
 * (C) Copyright 2014 OSIVIA (http://www.osivia.com) 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
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
