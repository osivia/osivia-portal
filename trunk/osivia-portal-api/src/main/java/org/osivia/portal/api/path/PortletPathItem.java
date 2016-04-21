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
package org.osivia.portal.api.path;

import java.util.Map;


/**
 * The Class PortletPathItem.
 */
public class PortletPathItem {
	
	/** The render params. */
	Map<String, String> renderParams;
	
	/** The url. */
	public String url;
	
	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * Sets the url.
	 *
	 * @param url the new url
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * Instantiates a new portlet path item.
	 *
	 * @param renderParams the render params
	 * @param label the label
	 */
	public PortletPathItem(Map<String, String> renderParams, String label) {
		super();
		this.renderParams = renderParams;
		this.label = label;
	}
	
	/**
	 * Gets the render params.
	 *
	 * @return the render params
	 */
	public Map<String, String> getRenderParams() {
		return renderParams;
	}
	
	/**
	 * Sets the render params.
	 *
	 * @param renderParams the render params
	 */
	public void setRenderParams(Map<String, String> renderParams) {
		this.renderParams = renderParams;
	}
	
	/**
	 * Gets the label.
	 *
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * Sets the label.
	 *
	 * @param label the new label
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	/** The label. */
	String label;

}
