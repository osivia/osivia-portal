package org.osivia.portal.api.customization;

import java.util.Map;

public class CustomizationContext {
	
	public CustomizationContext(Map<String, Object> attributes) {
		super();
		this.attributes = attributes;
	}

	private Map<String, Object> attributes;

	public Map<String, Object> getAttributes() {
		return attributes;
	}
	

}
