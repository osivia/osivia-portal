package org.osivia.portal.core.web;

import org.apache.commons.lang.math.NumberUtils;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerContext;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.DocumentsMetadata;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;


/**
 * Web URL service implementation.
 *
 * @author Cédric Krommenhoek
 * @see IWebUrlService
 */
public class WebUrlService implements IWebUrlService {

    /** Request attribute name. */
    public static final String REQUEST_ATTRIBUTE = "osivia.url.cache";


    /** Cache. */
    private final WebUrlCache cache;
    /** Cache validity (in milliseconds). */
    private final long validity;


    /** CMS service locator. */
    private ICMSServiceLocator cmsServiceLocator;


    /**
     * Constructor.
     */
    public WebUrlService() {
        super();
        this.cache = new WebUrlCache();
        this.validity = NumberUtils.toLong(System.getProperty(CACHE_VALIDITY_PROPERTY), CACHE_VALIDITY_DEFAULT_VALUE);
    }


    /**
     * {@inheritDoc}
     */
    public String getWebPath(CMSServiceCtx cmsContext, String basePath, String webId) throws PortalException {
        DocumentsMetadata metadata;
        try {
            metadata = this.getMetadata(cmsContext, basePath);
        } catch (CMSException e) {
            throw new PortalException(e);
        }
        return metadata.getWebPath(webId);
    }


    /**
     * {@inheritDoc}
     */
    public String getWebId(CMSServiceCtx cmsContext, String basePath, String webPath) throws PortalException {
        DocumentsMetadata metadata;
        try {
            metadata = this.getMetadata(cmsContext, basePath);
        } catch (CMSException e) {
            throw new PortalException(e);
        }
        return metadata.getWebId(webPath);
    }


    /**
     * Get documents metadata.
     *
     * @param cmsContext CMS context
     * @param basePath CMS base path
     * @return documents metadata
     * @throws CMSException
     */
    private DocumentsMetadata getMetadata(CMSServiceCtx cmsContext, String basePath) throws CMSException {
        // Controller context
        ControllerContext controllerContext = cmsContext.getControllerContext();

        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();

        // Live version indicator
        boolean live = "1".equals(cmsContext.getDisplayLiveVersion());


        // Request cache
        WebUrlCache requestCache = (WebUrlCache) controllerContext.getAttribute(Scope.REQUEST_SCOPE, REQUEST_ATTRIBUTE);
        if (requestCache == null) {
            requestCache = new WebUrlCache();
            controllerContext.setAttribute(Scope.REQUEST_SCOPE, REQUEST_ATTRIBUTE, requestCache);
        }

        // Metadata
        DocumentsMetadata metadata = requestCache.getMetadata(basePath, live);
        if (metadata == null) {
            // Global cache
            metadata = this.cache.getMetadata(basePath, live);
            if ((metadata == null) || ((metadata.getTimestamp() - this.validity) > System.currentTimeMillis())) {
                // Full refresh
                metadata = cmsService.getDocumentsMetadata(cmsContext, basePath, null);
                this.cache.setMetadata(basePath, live, metadata);
            } else {
                // Updates
                long timestamp = metadata.getTimestamp();
                DocumentsMetadata updates = cmsService.getDocumentsMetadata(cmsContext, basePath, timestamp);
                metadata.update(updates);
            }
            requestCache.setMetadata(basePath, live, metadata);
        }

        return metadata;
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
