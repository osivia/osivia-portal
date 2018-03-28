package org.osivia.portal.api.cms;

/**
 * Symlink interface.
 *
 * @author Cédric Krommenhoek
 */
public interface Symlink {

    /**
     * Get parent path.
     * 
     * @return path
     */
    String getParentPath();


    /**
     * Get segment.
     * 
     * @return segment
     */
    String getSegment();


    /**
     * Get target path.
     * 
     * @return path
     */
    String getTargetPath();


    /**
     * Get target webId.
     * 
     * @return webId
     */
    String getTargetWebId();


    /**
     * Get virtual path.
     * 
     * @return path
     */
    String getVirtualPath();


    /**
     * Get navigation path.
     * 
     * @return path
     */
    String getNavigationPath();

}
