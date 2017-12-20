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

import java.util.List;

import org.osivia.portal.api.customization.CustomizationContext;

public interface ICustomizationService {

    /** MBean name. */
    String MBEAN_NAME = "osivia:service=CustomizationService";

    /**
     * Calls the customizers for the specified id
     *
     * @param customizationID the customization id
     * @param ctx the ctx
     */
    public void customize ( String customizationID, CustomizationContext ctx);



    /**
     * Set The CMS Observer.
     *
     * @param observer the observer
     */
    public void setCMSObserver(ICMSCustomizationObserver observer);


    /**
     * Checks if a plugin with the provided name is registered
     *
     * @param pluginName
     */
    public boolean isPluginRegistered(String pluginName);

    /**
     * lists the names of registered plugins
     *
     */
    public List<String> getRegisteredPluginNames();

}
