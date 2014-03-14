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
			int order1 = m1.getOrder();
			int order2 = m2.getOrder();
			if (order1 != order2) {
			    return order1 - order2;
			} else {
			    String name1 = m1.getName();
			    String name2 = m2.getName();
			    return name1.compareTo(name2);
			}
		}

	};


	private void synchronizeSortedModules() {

		this.sortedModules = new TreeSet<CustomizationModuleMetadatas>(moduleComparator);

		for (CustomizationModuleMetadatas module : this.customModules.values()) {
			this.sortedModules.add(module);
		}

	}

	public void customize(String customizationID, CustomizationContext ctx) {
		for (CustomizationModuleMetadatas module : this.sortedModules) {

			if( module.getCustomizationIDs().contains(customizationID)) {
                module.getModule().customize(customizationID, ctx);
            }
		}

	}

	public void register(CustomizationModuleMetadatas moduleMetadatas) {
		this.customModules.put(moduleMetadatas.getName(), moduleMetadatas);
		this.synchronizeSortedModules();
	}

	public void unregister(CustomizationModuleMetadatas moduleMetadatas) {
		this.customModules.remove(moduleMetadatas.getName());
		this.synchronizeSortedModules();
	}

}
