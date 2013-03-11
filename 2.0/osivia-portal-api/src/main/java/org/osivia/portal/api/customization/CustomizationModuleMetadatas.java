package org.osivia.portal.api.customization;

import java.util.List;

public class CustomizationModuleMetadatas {
	
	private String name;
	private int order=0;
	public ICustomizationModule module;
	
	public ICustomizationModule getModule() {
		return module;
	}
	public void setModule(ICustomizationModule module) {
		this.module = module;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	
	public List<String> customizationIDs;

	public List<String> getCustomizationIDs() {
		return customizationIDs;
	}
	public void setCustomizationIDs(List<String> customizationIDs) {
		this.customizationIDs = customizationIDs;
	}

}
