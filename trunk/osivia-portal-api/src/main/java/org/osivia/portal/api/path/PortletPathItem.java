package org.osivia.portal.api.path;

import java.util.Map;

public class PortletPathItem {
	
	Map<String, String> renderParams;
	public String url;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public PortletPathItem(Map<String, String> renderParams, String label) {
		super();
		this.renderParams = renderParams;
		this.label = label;
	}
	public Map<String, String> getRenderParams() {
		return renderParams;
	}
	public void setRenderParams(Map<String, String> renderParams) {
		this.renderParams = renderParams;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	String label;

}
