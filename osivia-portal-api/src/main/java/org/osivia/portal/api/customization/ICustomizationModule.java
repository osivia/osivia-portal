package org.osivia.portal.api.customization;


/**
 * Allow to customize hooks defined by portal
 *
 */
public interface ICustomizationModule {
	
	public void customize ( String customizationID, CustomizationContext ctx);

}
