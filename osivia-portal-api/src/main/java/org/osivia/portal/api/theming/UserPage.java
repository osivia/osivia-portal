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



public class UserPage implements Serializable {
	

	private static final long serialVersionUID = 2616620851302460065L;
	private String name;
	private String url;
	private Object id;
	private String closePageUrl;

	
	public String getClosePageUrl() {
		return closePageUrl;
	}
	public void setClosePageUrl(String closePageUrl) {
		this.closePageUrl = closePageUrl;
	}
	public Object getId() {
		return id;
	}
	public void setId(Object id) {
		this.id = id;
	}
	private List<UserPage> children;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<UserPage> getChildren() {
		return children;
	}
	public void setChildren(List<UserPage> children) {
		this.children = children;
	}
	
	
	
	

}
