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
package org.osivia.portal.core.cms;

import java.io.File;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;

import org.osivia.portal.api.cache.services.ICacheDataListener;

/**
 * CMS binary content.
 *
 * @see ICacheDataListener
 */
public class CMSBinaryContent implements ICacheDataListener, IContentStreamingSupport {

    /** Default generated id. */
    private static final long serialVersionUID = 1L;

    // 2.0.22 : to stream big files
    /** The Constant largeFile. */
    public static final Map<String, CMSBinaryContent> largeFile = new Hashtable<String, CMSBinaryContent>();


    /** Name. */
    private String name;
    /** Long live session. */
    private Object longLiveSession;
    /** Stream. */
    private InputStream stream;
    /** Mime type. */
    private String mimeType;
    /** File. */
    private File file;
    /** File size. */
    private Long fileSize;


    /**
     * Constructor.
     */
    public CMSBinaryContent() {
        super();
    }


    /* Explicitly removed from cache : new cache has replaced old value */
    /*
     * (non-Javadoc)
     * 
     * @see org.osivia.portal.api.cache.services.ICacheDataListener#remove()
     */
    public void remove() {
        if (this.file != null) {
            this.file.delete();
            this.file = null;
        }

    }

    /* Derefrenced files : ie session closed */
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable {
        if (this.file != null) {
            this.file.delete();
            this.file = null;
        }
    }


    /**
     * Getter for name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setter for name.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for longLiveSession.
     *
     * @return the longLiveSession
     */
    public Object getLongLiveSession() {
        return this.longLiveSession;
    }

    /**
     * Setter for longLiveSession.
     *
     * @param longLiveSession the longLiveSession to set
     */
    public void setLongLiveSession(Object longLiveSession) {
        this.longLiveSession = longLiveSession;
    }

    /**
     * Getter for stream.
     *
     * @return the stream
     */
    public InputStream getStream() {
        return this.stream;
    }

    /**
     * Setter for stream.
     *
     * @param stream the stream to set
     */
    public void setStream(InputStream stream) {
        this.stream = stream;
    }

    /**
     * Getter for mimeType.
     *
     * @return the mimeType
     */
    public String getMimeType() {
        return this.mimeType;
    }

    /**
     * Setter for mimeType.
     *
     * @param mimeType the mimeType to set
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * Getter for file.
     *
     * @return the file
     */
    public File getFile() {
        return this.file;
    }

    /**
     * Setter for file.
     *
     * @param file the file to set
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Getter for fileSize.
     *
     * @return the fileSize
     */
    public Long getFileSize() {
        return this.fileSize;
    }

    /**
     * Setter for fileSize.
     *
     * @param fileSize the fileSize to set
     */
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

}
