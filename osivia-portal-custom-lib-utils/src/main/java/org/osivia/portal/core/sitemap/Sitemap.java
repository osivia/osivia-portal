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
package org.osivia.portal.core.sitemap;

import java.util.ArrayList;
import java.util.List;

public class Sitemap {
    
    /** Children (pages and other elements inside the current page) */
    private List<Sitemap> children = new ArrayList<Sitemap>();


    /** The display title of the  link. */
    private String title;
    
    /** The absolute URL of the  link. */
    private String url;

    /** true if the document has been published */
    private Boolean published;

    /** true if the document has changed since the last publication */
    private Boolean editedInLiveVersion;
	

	/** Canonical constructor.*/
	public Sitemap(String aTitle, String anUrl) {
		title = aTitle;
		url = anUrl;
	}
	
    public List<Sitemap> getChildren() {
        return children;
	}

	public String getTitle() {
		return title;
	}

	public String getUrl() {
		return url;
	}
	
    public void setChildren(List<Sitemap> children) {
        this.children = children;
	}


	public void setTitle(String aTitle) {
		this.title = aTitle;
	}

	public void setUrl(String anUrl) {
		this.url = anUrl;
	}


    /**
     * @return the published
     */
    public Boolean getPublished() {
        return published;
    }


    /**
     * @param published the published to set
     */
    public void setPublished(Boolean published) {
        this.published = published;
    }


    /**
     * @return the editedInLiveVersion
     */
    public Boolean getEditedInLiveVersion() {
        return editedInLiveVersion;
    }


    /**
     * @param editedInLiveVersion the editedInLiveVersion to set
     */
    public void setEditedInLiveVersion(Boolean editedInLiveVersion) {
        this.editedInLiveVersion = editedInLiveVersion;
    }

    public String toString() {
		return super.toString() + " {" + title + ": " + url + "}";
    }


}
