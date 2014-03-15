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
package org.osivia.portal.api.cache.services;

import java.io.Serializable;

import org.osivia.portal.api.PortalException;


/**
 * The Interface ICacheService.
 */
public interface ICacheService extends Serializable{

	/**
	 * Gets the cache.
	 *
	 * @param infos the infos
	 * @return the cache
	 * @throws PortalException the portal exception
	 */
	public Object getCache( CacheInfo infos) throws PortalException;
	
	/**
	 * Gets the cache initialisation ts.
	 *
	 * @return the cache initialisation ts
	 */
	public long getCacheInitialisationTs() ;
	
	/**
	 * Inits the cache.
	 *
	 * @throws Exception the exception
	 */
	public void initCache() throws PortalException;
	
	/**
	 * Inits the portal parameters.
	 */
	public void initPortalParameters();


	
}
