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

package org.osivia.portal.core.pagemarker;

import java.util.Map;

import org.osivia.portal.core.mt.CacheEntry;



/**
 * The Class PageBackCacheInfo.
 */
public class PageBackCacheInfo {
    
    /** The page marker. */
    private final String pageMarker;
    
    /** The back page marker. */
    private final String backPageMarker;
    
    /** The back cache. */
    private final  Map<String, CacheEntry> backCache;
    
    /**
     * Instantiates a new page back cache info.
     *
     * @param backPageMarker the back page marker
     * @param pageMarker the page marker
     * @param backCache the back cache
     */
    public PageBackCacheInfo(String backPageMarker,String pageMarker, Map<String, CacheEntry> backCache) {
        super();
        this.pageMarker = pageMarker;
        this.backCache =  backCache;
        this.backPageMarker = backPageMarker;
    }
   
    
    /**
     * Gets the back cache.
     *
     * @return the back cache
     */
    public Map<String, CacheEntry> getBackCache() {
        return backCache;
    }

    
    /**
     * Gets the page marker.
     *
     * @return the page marker
     */
    public String getPageMarker() {
        return pageMarker;
    }
    
    /**
     * Gets the back page marker.
     *
     * @return the back page marker
     */
    public String getBackPageMarker() {
        return backPageMarker;
    }

}
