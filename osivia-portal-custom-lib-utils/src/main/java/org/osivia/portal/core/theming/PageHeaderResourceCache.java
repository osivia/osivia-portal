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

package org.osivia.portal.core.theming;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;



/**
 * Page header resource cache.
 * Maintains the version for each deployed web-app even if custom-services is reloaded.
 *
 * @author Jean-Sébastien Steux
 * @author Cédric Krommenhoek
 */
public final class PageHeaderResourceCache {

    /** Singleton instance. */
    private static PageHeaderResourceCache instance = new PageHeaderResourceCache();


    /** Web-apps versions. */
    private final Map<String, String> versions;
    /** Adapted elements. */
    private final Map<String, String> adaptedElements;


    /**
     * Private constructor.
     */
    private PageHeaderResourceCache() {
        this.versions = Collections.synchronizedMap(new HashMap<String, String>());
        this.adaptedElements = Collections.synchronizedMap(new HashMap<String, String>());
    }


    /**
     * Get singleton instance.
     *
     * @return singleton instance
     */
    public static PageHeaderResourceCache getInstance() {
        return instance;
    }


    /**
     * Add web-app version in cache.
     *
     * @param contextPath web-app context path
     * @param version web-app version
     */
    public void addVersion(String contextPath, String version) {
        this.versions.put(contextPath, version);
    }


    /**
     * Get web-app version in cache.
     *
     * @param contextPath web-app context path
     * @return version, or null if not in cache
     */
    public String getVersion(String contextPath) {
        return this.versions.get(contextPath);
    }


    /**
     * Remove web-app version in cache.
     *
     * @param contextPath web-app context path
     */
    public void removeVersion(String contextPath) {
        this.versions.remove(contextPath);
    }


    /**
     * Add adapted element in cache.
     *
     * @param originalElement original element
     * @param adaptedElement adapted element
     */
    public void addAdaptedElement(String originalElement, String adaptedElement) {
        this.adaptedElements.put(originalElement, adaptedElement);
    }


    /**
     * Get adapted element in cache.
     *
     * @param originalElement original element
     * @return adapted element, or null if not in cache
     */
    public String getAdaptedElement(String originalElement) {
        return this.adaptedElements.get(originalElement);
    }


    /**
     * Clear adapted elements cache.
     */
    public void clearAdaptedElements() {
        this.adaptedElements.clear();
    }

}
