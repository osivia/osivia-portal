package org.osivia.portal.core.web;

import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.core.cms.CMSItem;

/**
 * Web URL service interface.
 *
 * @author Cédric Krommenhoek
 */
public interface IWebUrlService {

    /** Web URL segment webid prefix. */
    String WEBID_PREFIX = "id_";


    /**
     * Create web URL.
     *
     * @param portalControllerContext portal controller context
     * @param cmsItem CMS item
     * @return web URL
     */
    String create(PortalControllerContext portalControllerContext, CMSItem cmsItem);


    /**
     * Create web URL.
     *
     * @param portalControllerContext portal controller context
     * @param path cms path
     * @return web URL
     */
    String create(PortalControllerContext portalControllerContext, String path);


    /**
     * Resolve web URL to CMS item.
     *
     * @param portalControllerContext portal controller context
     * @param webUrl web URL
     * @return CMS item
     */
    CMSItem resolve(PortalControllerContext portalControllerContext, String webUrl);

}
