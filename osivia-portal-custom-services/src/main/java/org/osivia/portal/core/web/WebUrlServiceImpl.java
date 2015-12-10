package org.osivia.portal.core.web;

import org.apache.commons.lang.StringUtils;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.ICMSServiceLocator;


/**
 * Web URL service implementation.
 *
 * @author Cédric Krommenhoek
 * @see IWebUrlService
 */
public class WebUrlServiceImpl implements IWebUrlService {

    /** Portal URL factory. */
    private IPortalUrlFactory portalUrlFactory;
    /** CMS service locator. */
    private ICMSServiceLocator cmsServiceLocator;


    /**
     * Constructor.
     */
    public WebUrlServiceImpl() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public String create(PortalControllerContext portalControllerContext, CMSItem cmsItem) {
        return this.create(portalControllerContext, cmsItem.getPath());
    }


    /**
     * {@inheritDoc}
     */
    public String create(PortalControllerContext portalControllerContext, String path) {

        String[] splittedPaths = StringUtils.split(path, "/");


        // TODO Auto-generated method stub
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public CMSItem resolve(PortalControllerContext portalControllerContext, String webUrl) {
        // TODO Auto-generated method stub
        return null;
    }


    /**
     * Setter for portalUrlFactory.
     *
     * @param portalUrlFactory the portalUrlFactory to set
     */
    public void setPortalUrlFactory(IPortalUrlFactory portalUrlFactory) {
        this.portalUrlFactory = portalUrlFactory;
    }

    /**
     * Setter for cmsServiceLocator.
     *
     * @param cmsServiceLocator the cmsServiceLocator to set
     */
    public void setCmsServiceLocator(ICMSServiceLocator cmsServiceLocator) {
        this.cmsServiceLocator = cmsServiceLocator;
    }

}
