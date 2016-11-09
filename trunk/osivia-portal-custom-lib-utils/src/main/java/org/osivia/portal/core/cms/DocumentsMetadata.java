package org.osivia.portal.core.cms;

import java.util.List;

import org.osivia.portal.api.cms.EcmDocument;

/**
 * Documents metadata interface.
 *
 * @author Cédric Krommenhoek
 */
public interface DocumentsMetadata {

    /**
     * Get web path from webId.
     *
     * @param webId webId
     * @return web path
     */
    String getWebPath(String webId);


    /**
     * Get webId from web path.
     *
     * @param webPath web path
     * @return webId
     */
    String getWebId(String webPath);


    /**
     * Get documents.
     * 
     * @return documents
     */
    List<EcmDocument> getDocuments();


    /**
     * Get last modification timestamp.
     *
     * @return timestamp
     */
    long getTimestamp();


    /**
     * Update documents metadata.
     *
     * @param updates update values
     */
    void update(DocumentsMetadata updates);

}
