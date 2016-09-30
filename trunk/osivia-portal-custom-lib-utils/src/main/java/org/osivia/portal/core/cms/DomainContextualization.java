package org.osivia.portal.core.cms;

import java.util.List;

import org.osivia.portal.api.context.PortalControllerContext;


/**
 * Domain contextualization.
 * 
 * @author Cédric Krommenhoek
 */
public interface DomainContextualization {

    /**
     * Check if domain must be contextualized.
     * 
     * @param portalControllerContext portal controller context
     * @param domainPath domain path
     * @return true if domain must be contextualized
     */
    boolean contextualize(PortalControllerContext portalControllerContext, String domainPath);


    /**
     * Get domain site names.
     * 
     * @param portalControllerContext portal controller context
     * @return site names
     */
    List<String> getSites(PortalControllerContext portalControllerContext);


    /**
     * Get domain default site name, may be null.
     * 
     * @param portalControllerContext portal controller context
     * @return default site name
     */
    String getDefaultSite(PortalControllerContext portalControllerContext);

}
