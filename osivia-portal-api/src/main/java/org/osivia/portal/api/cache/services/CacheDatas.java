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



public class CacheDatas {

	private long tsSaving;
	private Object cacheContent;
	private long tsAskForReloading;

	public long getTsEnregistrement() {
		return tsSaving;
	}

	public void setTsSaving(long tsEnregistrement) {
		this.tsSaving = tsEnregistrement;
	}

	public CacheDatas(CacheInfo infos, Object contenuCache) {
		super();
		this.tsSaving = System.currentTimeMillis();
		this.cacheContent = contenuCache;
	}

	public Object getContent() {
		return cacheContent;
	}

	public void setContent(Object contenuCache) {
		this.cacheContent = contenuCache;
	}

	public long getTsAskForReloading() {
		return tsAskForReloading;
	}

	public void setTsAskForReloading(long tsAskForReloading) {
		this.tsAskForReloading = tsAskForReloading;
	}
	
}
