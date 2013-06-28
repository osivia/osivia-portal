package org.osivia.portal.core.customization;

import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.api.customization.CustomizationContext;
import org.osivia.portal.api.customization.CustomizationModuleMetadatas;
import org.osivia.portal.api.customization.ICustomizationModulesRepository;

import org.osivia.portal.core.login.LoginInterceptor;




public class CustomizationService implements ICustomizationService, ICustomizationModulesRepository	{

	
	protected static final Log logger = LogFactory.getLog(LoginInterceptor.class);

	Map<String, CustomizationModuleMetadatas> customModules = new Hashtable<String, CustomizationModuleMetadatas>();
	SortedSet<CustomizationModuleMetadatas> sortedModules = new TreeSet<CustomizationModuleMetadatas>(moduleComparator);

	
	public static final Comparator<CustomizationModuleMetadatas> moduleComparator = new Comparator<CustomizationModuleMetadatas>() {

		public int compare(CustomizationModuleMetadatas m1, CustomizationModuleMetadatas m2) {
			return m1.getOrder() - m2.getOrder();

		}
	};


	private void synchronizeSortedModules() {

		sortedModules = new TreeSet<CustomizationModuleMetadatas>(moduleComparator);
		
		for (CustomizationModuleMetadatas module : customModules.values()) {
			sortedModules.add(module);
		}

	}
	
	public void customize(String customizationID, CustomizationContext ctx) {
		for (CustomizationModuleMetadatas module : sortedModules) {
			
			if( module.getCustomizationIDs().contains(customizationID))
				module.getModule().customize(customizationID, ctx);
		}
		
	}

	public void register(CustomizationModuleMetadatas moduleMetadatas) {
		customModules.put(moduleMetadatas.getName(), moduleMetadatas);
		synchronizeSortedModules();
	}

	public void unregister(CustomizationModuleMetadatas moduleMetadatas) {
		customModules.remove(moduleMetadatas.getName());
		synchronizeSortedModules();
	}


	
	
}
