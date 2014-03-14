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
package org.osivia.portal.core.cms;

import java.io.File;

import org.osivia.portal.api.cache.services.ICacheDataListener;



public class CMSBinaryContent implements ICacheDataListener {

	private static final long serialVersionUID = -3209402949942533453L;

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}


	private String mimeType;
	
	private File file;

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	/* Explicitly removed from cache : new cache has replaced old value */
	public void remove() {
		if( file != null)	{
			file.delete();
			file = null;
		}
		
	}
	
	/* Derefrenced files : ie session closed */
	protected void finalize() throws Throwable	{
		if( file != null)	{
			file.delete();
			file = null;
		}
		
	}
	

}
