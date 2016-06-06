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
 */
package org.osivia.portal.api.urls;

import java.io.Serializable;


/**
 * Link java-bean.
 */
public class Link implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7595217766599298663L;
	
	
	/** Link URL. */
    private final String url;
    /** External link indicator. */
    private final boolean external;

    /** Downloadable link indicator. */
    private boolean downloadable;


    /**
     * Constructor.
     *
     * @param url link URL
     * @param external external link indicator
     */
    public Link(String url, boolean external) {
        super();
        this.url = url;
        this.external = external;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Link [url=" + this.url + "]";
    }


    /**
     * Getter for downloadable.
     *
     * @return the downloadable
     */
    public boolean isDownloadable() {
        return this.downloadable;
    }

    /**
     * Setter for downloadable.
     *
     * @param downloadable the downloadable to set
     */
    public void setDownloadable(boolean downloadable) {
        this.downloadable = downloadable;
    }

    /**
     * Getter for url.
     *
     * @return the url
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * Getter for external.
     *
     * @return the external
     */
    public boolean isExternal() {
        return this.external;
    }

}
