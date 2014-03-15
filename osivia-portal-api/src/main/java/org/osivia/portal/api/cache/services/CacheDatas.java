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



/**
 * The Class CacheDatas.
 * 
 * contains informations about each record that is stored in cache
 * 
 * @author Jean-Sébastien Steux
 */
public class CacheDatas {

	/** The ts saving. */
	private long tsSaving;
	
	/** The cache content. */
	private Object cacheContent;
	
	/** The ts ask for reloading. */
	private long tsAskForReloading;

	/**
	 * Gets the ts enregistrement.
	 *
	 * @return the ts enregistrement
	 */
	public long getTsEnregistrement() {
		return tsSaving;
	}

	/**
	 * Sets the ts saving.
	 *
	 * @param tsEnregistrement the new ts saving
	 */
	public void setTsSaving(long tsEnregistrement) {
		this.tsSaving = tsEnregistrement;
	}

	/**
	 * Instantiates a new cache datas.
	 *
	 * @param infos the infos
	 * @param contenuCache the contenu cache
	 */
	public CacheDatas(CacheInfo infos, Object contenuCache) {
		super();
		this.tsSaving = System.currentTimeMillis();
		this.cacheContent = contenuCache;
	}

	/**
	 * Gets the content.
	 *
	 * @return the content
	 */
	public Object getContent() {
		return cacheContent;
	}

	/**
	 * Sets the content.
	 *
	 * @param contenuCache the new content
	 */
	public void setContent(Object contenuCache) {
		this.cacheContent = contenuCache;
	}

	/**
	 * Gets the ts ask for reloading.
	 *
	 * @return the ts ask for reloading
	 */
	public long getTsAskForReloading() {
		return tsAskForReloading;
	}

	/**
	 * Sets the ts ask for reloading.
	 *
	 * @param tsAskForReloading the new ts ask for reloading
	 */
	public void setTsAskForReloading(long tsAskForReloading) {
		this.tsAskForReloading = tsAskForReloading;
	}
	
}
