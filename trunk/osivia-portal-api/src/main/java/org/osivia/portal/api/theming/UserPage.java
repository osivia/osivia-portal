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
package org.osivia.portal.api.theming;

import java.io.Serializable;
import java.util.List;




/**
 * The Class UserPage.
 */
public class UserPage implements Serializable {
	

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2616620851302460065L;
	
	/** The name. */
	private String name;
	
	/** The url. */
	private String url;
	
	/** The id. */
	private Object id;
	
	/** The close page url. */
	private String closePageUrl;

	
	/**
	 * Gets the close page url.
	 *
	 * @return the close page url
	 */
	public String getClosePageUrl() {
		return closePageUrl;
	}
	
	/**
	 * Sets the close page url.
	 *
	 * @param closePageUrl the new close page url
	 */
	public void setClosePageUrl(String closePageUrl) {
		this.closePageUrl = closePageUrl;
	}
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Object getId() {
		return id;
	}
	
	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(Object id) {
		this.id = id;
	}
	
	/** The children. */
	private List<UserPage> children;
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
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
	 * Gets the children.
	 *
	 * @return the children
	 */
	public List<UserPage> getChildren() {
		return children;
	}
	
	/**
	 * Sets the children.
	 *
	 * @param children the new children
	 */
	public void setChildren(List<UserPage> children) {
		this.children = children;
	}
	
	
	
	

}
