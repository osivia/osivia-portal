package org.osivia.portal.core.web;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerContext;
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

    /** Log. */
    private final Log log;


    /** CMS service locator. */
    private ICMSServiceLocator cmsServiceLocator;


    /**
     * Constructor.
     */
    public WebUrlService() {
        super();
        this.validity = NumberUtils.toLong(System.getProperty(CACHE_VALIDITY_PROPERTY), CACHE_VALIDITY_DEFAULT_VALUE);
        this.cache = new WebUrlCache(this.validity);
        this.log = LogFactory.getLog(this.getClass());
    }


    /**
     * {@inheritDoc}
     */
    public String getBasePath(CMSServiceCtx cmsContext) {
        // Controller context
        ControllerContext controllerContext = cmsContext.getControllerContext();

        return WebURLFactory.getWebPortalBasePath(controllerContext);
    }


    /**
     * {@inheritDoc}
     */
    public String getWebPath(CMSServiceCtx cmsContext, String basePath, String webId) {
        String webPath;
        try {
            DocumentsMetadata metadata = this.getMetadata(cmsContext, basePath);
            webPath = metadata.getWebPath(webId);
        } catch (CMSException e) {
            webPath = null;
        }

        if (webPath == null) {
            // Default web path
            StringBuilder builder = new StringBuilder();
            builder.append("/");
            builder.append(WEB_ID_PREFIX);
            builder.append(webId);
            webPath = builder.toString();
        }

        return webPath;
    }


    /**
     * {@inheritDoc}
     */
    public String getWebId(CMSServiceCtx cmsContext, String basePath, String webPath) {
        String webId;
        try {
            DocumentsMetadata metadata = this.getMetadata(cmsContext, basePath);
            webId = metadata.getWebId(webPath);
        } catch (CMSException e) {
            webId = null;
        }

        if (webId == null) {
            // Segment
            String segment;
            if (StringUtils.contains(webPath, "/")) {
                segment = StringUtils.substringAfterLast(webPath, "/");
            } else {
                segment = webPath;
            }

            if (segment.startsWith(WEB_ID_PREFIX)) {
                // Default webId
                webId = StringUtils.removeStart(segment, WEB_ID_PREFIX);
            }
        }

        return webId;
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
        if (basePath == null) {
            throw new CMSException(CMSException.ERROR_NOTFOUND);
        }

        // Controller context
        ControllerContext controllerContext = cmsContext.getControllerContext();

        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();

        // Live version indicator
        boolean live = "1".equals(cmsContext.getDisplayLiveVersion());


        // Request cache
        WebUrlCache requestCache = (WebUrlCache) controllerContext.getAttribute(Scope.REQUEST_SCOPE, REQUEST_ATTRIBUTE);
        if (requestCache == null) {
            requestCache = new WebUrlCache(this.validity);
            controllerContext.setAttribute(Scope.REQUEST_SCOPE, REQUEST_ATTRIBUTE, requestCache);
        }

        // Metadata
        DocumentsMetadata metadata = requestCache.getMetadata(basePath, live);
        if (metadata == null) {
            // Global cache
            metadata = this.cache.getMetadata(basePath, live);
            if (metadata == null) {
                // Full refresh
                metadata = this.fullRefresh(cmsContext, basePath, live);
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
     * Documents metadata full refresh.
     *
     * @param cmsContext CMS context
     * @param basePath CMS base path
     * @param live live version indicator
     * @return documents metadata
     * @throws CMSException
     */
    private synchronized DocumentsMetadata fullRefresh(CMSServiceCtx cmsContext, String basePath, boolean live) throws CMSException {
        DocumentsMetadata metadata = this.cache.getMetadata(basePath, live);
        if (metadata == null) {
            // CMS service
            ICMSService cmsService = this.cmsServiceLocator.getCMSService();

            // Log
            if (this.log.isDebugEnabled()) {
                StringBuilder builder = new StringBuilder();
                builder.append("Full refresh [basePath='");
                builder.append(basePath);
                builder.append("' ; live=");
                builder.append(live);
                builder.append("]");
                this.log.debug(builder.toString());
            }

            metadata = cmsService.getDocumentsMetadata(cmsContext, basePath, null);
            this.cache.setMetadata(basePath, live, metadata);
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
