package org.osivia.portal.core.cms;

public class ContentItem {
	
	private String type;
	private String id;
	public ContentItem(String type, String id) {
		super();
		this.type = type;
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

}
