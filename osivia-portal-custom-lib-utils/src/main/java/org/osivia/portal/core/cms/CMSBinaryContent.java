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
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;

import org.osivia.portal.api.cache.services.ICacheDataListener;



// TODO: Auto-generated Javadoc
/**
 * The Class CMSBinaryContent.
 */
public class CMSBinaryContent implements ICacheDataListener {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3209402949942533453L;

	

    // 2.0.22 : to stream big files
    /** The Constant largeFile. */
    public static final Map<String, CMSBinaryContent> largeFile = new Hashtable<String, CMSBinaryContent>();
    

	/** The name. */
	private String name;
	
	   
    /** The long live session. */
    public Object longLiveSession;

    
    
    /**
     * Gets the long live session.
     *
     * @return the long live session
     */
    public Object getLongLiveSession() {
        return longLiveSession;
    }

    /**
     * Sets the long live session.
     *
     * @param longLiveSession the new long live session
     */
    public void setLongLiveSession(Object longLiveSession) {
        this.longLiveSession = longLiveSession;
    }

    
    /** The stream. */
    private InputStream stream;

    
    /**
     * Gets the stream.
     *
     * @return the stream
     */
    public InputStream getStream() {
        return stream;
    }

    
    /**
     * Sets the stream.
     *
     * @param stream the new stream
     */
    public void setStream(InputStream stream) {
        this.stream = stream;
    }
    

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the mime type.
	 *
	 * @return the mime type
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * Sets the mime type.
	 *
	 * @param mimeType the new mime type
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}


	/** The mime type. */
	private String mimeType;
	
	/** The file. */
	private File file;

	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Sets the file.
	 *
	 * @param file the new file
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/* Explicitly removed from cache : new cache has replaced old value */
	/* (non-Javadoc)
	 * @see org.osivia.portal.api.cache.services.ICacheDataListener#remove()
	 */
	public void remove() {
		if( file != null)	{
			file.delete();
			file = null;
		}
		
	}
	
	/* Derefrenced files : ie session closed */
	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() throws Throwable	{
		if( file != null)	{
			file.delete();
			file = null;
		}
		
	}
	

}
