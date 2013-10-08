package org.osivia.portal.core.cms;

import java.util.Map;
import java.util.TreeMap;

public class CMSItem {
	
	private Object nativeItem;
	private String path;	
    private Map<String, String> properties;

    /** Used for SEO */
    private Map<String, String> metaProperties = new TreeMap<String, String>();
	
    private Boolean published;

	public Object getNativeItem() {
		return nativeItem;
	}


	public String getPath() {
		return path;
	}

		
	public Map<String, String> getProperties() {
		return properties;
	}

	public CMSItem(String path,  Map<String, String> properties, Object nativeItem) {
		super();

		this.path = path;
		this.properties = properties;
		this.nativeItem = nativeItem;
	}

    /**
     * @return the metaProperties
     */
    public Map<String, String> getMetaProperties() {
        return metaProperties;
    }


    /**
     * @param metaProperties the metaProperties to set
     */
    public void setMetaProperties(Map<String, String> metaProperties) {
        this.metaProperties = metaProperties;
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


}
