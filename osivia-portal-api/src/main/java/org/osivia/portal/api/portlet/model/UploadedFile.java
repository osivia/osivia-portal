package org.osivia.portal.api.portlet.model;

import java.io.File;

/**
 * Uploaded file interface.
 * 
 * @author Cédric Krommenhoek
 */
public interface UploadedFile {

    /**
     * Get original file URL.
     * 
     * @return URL
     */
    String getUrl();


    /**
     * Get original file index.
     * 
     * @return index
     */
    Integer getIndex();


    /**
     * Get original file metadata.
     * 
     * @return file metadata
     */
    UploadedFileMetadata getOriginalMetadata();


    /**
     * Get temporary file.
     * 
     * @return temporary file
     */
    File getTemporaryFile();


    /**
     * Get temporary file metadata.
     * 
     * @return metadata
     */
    UploadedFileMetadata getTemporaryMetadata();


    /**
     * Deleted file indicator.
     * 
     * @return true if the file has been deleted
     */
    boolean isDeleted();

}
