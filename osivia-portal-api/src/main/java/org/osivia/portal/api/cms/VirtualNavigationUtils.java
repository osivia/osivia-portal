package org.osivia.portal.api.cms;

import org.apache.commons.lang.StringUtils;

/**
 * The Class VirtualNavigationSUtils.
 * <p>
 * Virtual Path are composed of /navigation_path/_vid_[WEBID]
 * The resulting virtual path is playable as a CMSCommand Path.
 * <p>
 * Manage virtual staple paths
 */
public class VirtualNavigationUtils {

    private static final String VS_PREFIX = "/_vid_";
    private static final String STAPLE_PREFIX = "vstaple_";


    /**
     * Adapt path.
     *
     * @param navigationPath the navigation path
     * @param webId          the web id
     * @return the string
     */

    public static String adaptPath(String navigationPath, String webId) {
        return navigationPath + VS_PREFIX + webId;
    }


    /**
     * Gets the content id.
     *
     * @param virtualPath the virtual path
     * @return the content id
     */
    public static String getWebId(String virtualPath) {
        return StringUtils.trimToNull(StringUtils.substringAfterLast(virtualPath, VS_PREFIX));
    }


    /**
     * Gets the virtual staple id if exists.
     *
     * @param virtualPath the virtual path
     * @return the staple id
     */
    public static String getStapleId(String virtualPath) {
        String contentId = getWebId(virtualPath);

        String result;
        if (StringUtils.startsWith(contentId, STAPLE_PREFIX)) {
            result = StringUtils.substringAfter(contentId, STAPLE_PREFIX);
        } else {
            result = null;
        }

        return result;
    }

}
