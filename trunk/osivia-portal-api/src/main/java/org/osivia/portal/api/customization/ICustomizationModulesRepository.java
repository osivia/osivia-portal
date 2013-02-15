package org.osivia.portal.api.customization;

public interface ICustomizationModulesRepository {
	
	public void register (CustomizationModuleMetadatas moduleMetadatas);
	public void unregister (CustomizationModuleMetadatas moduleMetadatas);
	
}
