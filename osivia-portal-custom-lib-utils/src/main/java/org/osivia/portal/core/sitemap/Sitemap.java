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


    private String liveId;
	
	private boolean liveVersion;

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
     * @return the liveVersion
     */
    public boolean isLiveVersion() {
        return liveVersion;
    }

    /**
     * @param liveVersion the liveVersion to set
     */
    public void setLiveVersion(boolean liveVersion) {
        this.liveVersion = liveVersion;
    }

    /**
     * @return the liveId
     */
    public String getLiveId() {
        return liveId;
    }

    /**
     * @param liveId the liveId to set
     */
    public void setLiveId(String liveId) {
        this.liveId = liveId;
    }

    public String toString() {
		return super.toString() + " {" + title + ": " + url + "}";
    }


}
