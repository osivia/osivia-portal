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

// TODO: Auto-generated Javadoc
/**
 * The Class UserPortal.
 */
public class UserPortal implements Serializable {
	

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4680072181726328231L;
	
	/** The name. */
	private String name;
	
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

	/** The pages. */
	private List<UserPage> pages;

	/**
	 * Gets the user pages.
	 *
	 * @return the user pages
	 */
	public List<UserPage> getUserPages() {
		return pages;
	
	}

	/**
	 * Sets the user pages.
	 *
	 * @param pages the new user pages
	 */
	public void setUserPages(List<UserPage> pages ) {
		this.pages = pages;
	}

}
