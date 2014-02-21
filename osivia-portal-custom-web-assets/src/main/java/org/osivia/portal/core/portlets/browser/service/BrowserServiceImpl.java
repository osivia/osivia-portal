package org.osivia.portal.core.portlets.browser.service;

import javax.portlet.PortletException;

import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.path.ICMSPathService;
import org.springframework.stereotype.Service;

/**
 * Live content browser service implementation.
 * 
 * @author Cédric Krommenhoek
 * @see IBrowserService
 */
@Service
public class BrowserServiceImpl implements IBrowserService {

    /** CMS path service. */
    private final ICMSPathService cmsPathService;


    /**
     * Constructor.
     */
    public BrowserServiceImpl() {
        super();
        this.cmsPathService = Locator.findMBean(ICMSPathService.class, ICMSPathService.MBEAN_NAME);
    }


    /**
     * {@inheritDoc}
     */
    public String browse(PortalControllerContext portalControllerContext, String path) throws PortletException {
        return this.cmsPathService.browseContentLazyLoading(portalControllerContext, path, true, false);
    }

}
