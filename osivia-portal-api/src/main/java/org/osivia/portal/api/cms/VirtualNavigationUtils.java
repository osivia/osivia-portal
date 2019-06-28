package org.osivia.portal.api.cms;

import org.apache.commons.lang.StringUtils;

/**
 * The Class VirtualNavigationSUtils.
 * 
 * Virtual Path are composed of /navigation_path/_vid_[WEBID]
 * The resulting virtual path is playable as a CMSCommand Path.
 * 
 * Manage virtual staple paths
 * 
 */
public class VirtualNavigationUtils {

    private static final String _VS_PREFIX = "/_vid_";
    private static final String _STAPPLE_PREFIX = "vstapple_";    

    /**
     * Adapt path.
     * 
     *
     * @param navigationPath the navigation path
     * @param webId the web id
     * @return the string
     */

    public static String adaptPath(String navigationPath, String webId) {
        return navigationPath + _VS_PREFIX + webId;
    }

    /**
     * Gets the content id.
     *
     * @param virtualPath the virtual path
     * @return the content id
     */
    public static String getWebId(String virtualPath) {
       int ilastSegment = virtualPath.lastIndexOf(_VS_PREFIX);
        if (ilastSegment != -1) {
            return virtualPath.substring(ilastSegment + _VS_PREFIX.length());

        } 
        return null;
    }
    
    
    /**
     * Gets the virtual stapple id if exists.
     *
     * @param virtualPath the virtual path
     * @return the stapple id
     */
    
    public static String getStappleId(String virtualPath) {
        String contentId = getWebId(virtualPath);
        
        if( StringUtils.isNotEmpty(contentId))  {
            if( contentId.startsWith(_STAPPLE_PREFIX))  {
                return contentId.substring(_STAPPLE_PREFIX.length());
            } 
        }

        return null;
    }
}
