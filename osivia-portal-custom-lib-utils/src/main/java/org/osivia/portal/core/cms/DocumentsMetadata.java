package org.osivia.portal.core.cms;

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
