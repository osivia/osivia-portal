package org.osivia.portal.core.web;

import java.util.concurrent.TimeUnit;

import org.osivia.portal.core.cms.CMSServiceCtx;

/**
 * Web URL service interface.
 *
 * @author Cédric Krommenhoek
 */
public interface IWebUrlService {

    /** MBean name. */
    String MBEAN_NAME = "osivia:service=webUrlService";

    /** Web URL segment webId prefix. */
    String WEB_ID_PREFIX = "id_";

    /** Cache validity property name. */
    String CACHE_VALIDITY_PROPERTY = "url.cache.validity";
    /** Cache validity default value. */
    long CACHE_VALIDITY_DEFAULT_VALUE = TimeUnit.HOURS.toMillis(1);
    /** Update validity property name. */
    String UPDATE_VALIDITY_PROPERTY = "url.update.validity";
    /** Update validity default value. */
    long UPDATE_VALIDITY_DEFAULT_VALUE = TimeUnit.MINUTES.toMillis(1);


    /**
     * Get current CMS base path.
     *
     * @param cmsContext CMS context
     * @return CMS base path
     */
    String getBasePath(CMSServiceCtx cmsContext);


    /**
     * Get web path from webId.
     *
     * @param cmsContext CMS context
     * @param basePath CMS base path
     * @param webId webId
     * @return web path
     */
    String getWebPath(CMSServiceCtx cmsContext, String basePath, String webId);


    /**
     * Get webId from web path.
     *
     * @param cmsContext CMS context
     * @param basePath CMS base path
     * @param webPath web path
     * @return webId
     */
    String getWebId(CMSServiceCtx cmsContext, String basePath, String webPath);

}
