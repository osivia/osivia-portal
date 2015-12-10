package org.osivia.portal.core.cms;

/**
 * Documents metadata interface.
 *
 * @author Cédric Krommenhoek
 */
public interface DocumentsMetadata {

    /**
     * Get web path from CMS path.
     *
     * @param path CMS path
     * @return web path
     */
    String getWebPath(String path);

}
