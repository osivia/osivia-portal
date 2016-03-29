package org.osivia.portal.core.cms;

import java.util.List;

import org.osivia.portal.api.context.PortalControllerContext;


/**
 * Domain contextualization.
 * 
 * @author Cédric Krommenhoek
 */
public interface DomainContextualization {


    boolean contextualize(PortalControllerContext portalControllerContext, String domainPath);


    List<String> getSites(PortalControllerContext portalControllerContext);


    String getDefaultSite(PortalControllerContext portalControllerContext);

}
