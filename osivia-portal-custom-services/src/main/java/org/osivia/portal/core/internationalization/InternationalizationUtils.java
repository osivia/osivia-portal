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
package org.osivia.portal.core.internationalization;

import java.util.Locale;

import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;

/**
 * Utility class with null-safe methods for internationalization.
 *
 * @author Cédric Krommenhoek
 */
public class InternationalizationUtils {

    /**
     * Private constructor : prevent instantiation.
     */
    private InternationalizationUtils() {
        throw new AssertionError();
    }


    /**
     * Get internationalization service.
     *
     * @return notifications service
     */
    public static final IInternationalizationService getInternationalizationService() {
        return Locator.findMBean(IInternationalizationService.class, IInternationalizationService.MBEAN_NAME);
    }


    /**
     * Get application name.
     * 
     * @param portalObject portal object
     * @param locale user locale
     * @return application name
     */
    public static final String getApplicationName(PortalObject portalObject, Locale locale) {
    	
    	if(System.getProperty("application.name") != null) {
    		return System.getProperty("application.name");
    	}
    	else {
    		
	        // Internationalization service
	        IInternationalizationService service = getInternationalizationService();
	        // Portal
	        Portal portal = PortalObjectUtils.getPortal(portalObject);
	        // Brand name internationalization key
	        String key = portal.getDeclaredProperty("osivia.brand.key");
	        if (key == null) {
	            key = "BRAND";
	        }
	        // Application name
	        return service.getString(key, locale);
    	}
    }

}
