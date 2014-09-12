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
package org.osivia.portal.api.urls;


/**
 * The Class Link.
 */
public class Link {
	
	/** The external. */
	private boolean external = false;
	
	/** The downloadable. */
	private boolean downloadable = false;
	
	/** The url. */
	private String url;
	
	/**
	 * Checks if is downloadable.
	 *
	 * @return true, if is downloadable
	 */
	public boolean isDownloadable() {
		return downloadable;
	}

	/**
	 * Sets the downloadable.
	 *
	 * @param downloadable the new downloadable
	 */
	public void setDownloadable(boolean downloadable) {
		this.downloadable = downloadable;
	}



	/**
	 * Checks if is external.
	 *
	 * @return true, if is external
	 */
	public boolean isExternal() {
		return external;
	}

	/**
	 * Sets the external.
	 *
	 * @param external the new external
	 */
	public void setExternal(boolean external) {
		this.external = external;
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
	 * Instantiates a new link.
	 *
	 * @param url the url
	 * @param external the external
	 */
	public Link( String url, boolean external) {
		super();
		this.external = external;
		this.url = url;
	}
	
	
	

}
