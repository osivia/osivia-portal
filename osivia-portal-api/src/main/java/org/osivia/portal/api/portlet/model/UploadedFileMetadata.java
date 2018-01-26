package org.osivia.portal.api.portlet.model;

import javax.activation.MimeType;

/**
 * Uploaded file metadata interface.
 * 
 * @author Cédric Krommenhoek
 */
public interface UploadedFileMetadata {

    /**
     * Get file name.
     * 
     * @return file name
     */
    String getFileName();


    /**
     * Get file MIME type.
     * 
     * @return MIME type
     */
    MimeType getMimeType();


    /**
     * Get file type icon.
     * 
     * @return icon
     */
    String getIcon();

}
