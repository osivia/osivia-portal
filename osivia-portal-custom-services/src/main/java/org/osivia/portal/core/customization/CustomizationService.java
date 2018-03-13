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
 */
package org.osivia.portal.core.customization;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.api.customization.CustomizationContext;
import org.osivia.portal.api.customization.CustomizationModuleMetadatas;
import org.osivia.portal.api.customization.ICustomizationModule;
import org.osivia.portal.api.customization.ICustomizationModulesRepository;
import org.osivia.portal.core.login.LoginInterceptor;



/**
 * The Class CustomizationService.
 */
public class CustomizationService implements ICustomizationService, ICustomizationModulesRepository {


    /** The Constant logger. */
    protected static final Log logger = LogFactory.getLog(LoginInterceptor.class);

    /** The custom modules. */
    Map<String, CustomizationModuleMetadatas> customModules = new Hashtable<>();

    /** The sorted modules. */
    SortedSet<CustomizationModuleMetadatas> sortedModules = new TreeSet<>(moduleComparator);

    /** The first ts. */
    Hashtable<MultiKey, Long> firstTS = new Hashtable<>();

    /** The cms observer. */
    private ICMSCustomizationObserver cmsObserver;


    /** The Constant moduleComparator. */
    public static final Comparator<CustomizationModuleMetadatas> moduleComparator = new Comparator<CustomizationModuleMetadatas>() {

        @Override
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


    /**
     * Synchronize sorted modules.
     */
    private void synchronizeSortedModules() {

        sortedModules = new TreeSet<>(moduleComparator);

        for (CustomizationModuleMetadatas module : customModules.values()) {
            sortedModules.add(module);
        }

    }

    /* (non-Javadoc)
     * @see org.osivia.portal.core.customization.ICustomizationService#customize(java.lang.String, org.osivia.portal.api.customization.CustomizationContext)
     */
    @Override
    public void customize(String customizationID, CustomizationContext ctx) {

        for (CustomizationModuleMetadatas module : sortedModules) {

            if (module.getCustomizationIDs().contains(customizationID)) {
                module.getModule().customize(ctx);
            }
        }

        MultiKey updateKey = new MultiKey(customizationID, ctx.getLocale());
        if (firstTS.get(updateKey) == null) {
            firstTS.put(updateKey, System.currentTimeMillis());
        }
    }


    /**
     * Removes the ts.
     *
     * @param moduleMetadatas the module metadatas
     */
    private void removeTS(CustomizationModuleMetadatas moduleMetadatas) {
        List<MultiKey> itemsToRemove = new ArrayList<>();

        for (MultiKey key : firstTS.keySet()) {
            if (moduleMetadatas.getCustomizationIDs().contains(key.getKey(0))) {
                itemsToRemove.add(key);
            }
        }

        for (MultiKey key : itemsToRemove) {
            firstTS.remove(key);
        }
    }

    /* (non-Javadoc)
     * @see org.osivia.portal.api.customization.ICustomizationModulesRepository#register(org.osivia.portal.api.customization.CustomizationModuleMetadatas)
     */
    @Override
    public void register(CustomizationModuleMetadatas moduleMetadatas) {
        customModules.put(moduleMetadatas.getName(), moduleMetadatas);
        removeTS(moduleMetadatas);
        synchronizeSortedModules();

        if (cmsObserver != null) {
            if (moduleMetadatas.getCustomizationIDs().contains(ICustomizationModule.PLUGIN_ID)) {
                cmsObserver.notifyDeployment();
            }
        }
    }

    /* (non-Javadoc)
     * @see org.osivia.portal.api.customization.ICustomizationModulesRepository#unregister(org.osivia.portal.api.customization.CustomizationModuleMetadatas)
     */
    @Override
    public void unregister(CustomizationModuleMetadatas moduleMetadatas) {
        customModules.remove(moduleMetadatas.getName());
        removeTS(moduleMetadatas);
        synchronizeSortedModules();

        if( cmsObserver != null) {
            if( moduleMetadatas.getCustomizationIDs().contains(ICustomizationModule.PLUGIN_ID)) {
                ;
            }
        }
        cmsObserver.notifyDeployment();

    }


    /* (non-Javadoc)
     * @see org.osivia.portal.core.customization.ICustomizationService#setCMSObserver(org.osivia.portal.core.customization.ICMSCustomizationObserver)
     */
    @Override
    public void setCMSObserver(ICMSCustomizationObserver observer) {
        cmsObserver = observer;
    }

    @Override
    public boolean isPluginRegistered(String pluginName) {
        if (pluginName != null) {
            return customModules.get(pluginName) != null;
        }
        return false;
    }

    @Override
    public List<String> getRegisteredPluginNames() {
        List<String> registeredPluginNames = new ArrayList<>();
        for (CustomizationModuleMetadatas customizationModuleMetadatas : sortedModules) {
            registeredPluginNames.add(customizationModuleMetadatas.getName());
        }
        return registeredPluginNames;
    }


}
