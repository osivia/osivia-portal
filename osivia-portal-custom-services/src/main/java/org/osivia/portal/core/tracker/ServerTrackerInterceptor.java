/*
 * (C) Copyright 2014 OSIVIA (http://www.osivia.com)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package org.osivia.portal.core.tracker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.server.ServerInterceptor;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerInvocationContext;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.directory.IDirectoryService;
import org.osivia.portal.api.directory.IDirectoryServiceLocator;
import org.osivia.portal.api.directory.v2.IDirProvider;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cache.global.ICacheService;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.error.UserAction;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.portalobjects.DynamicPortalObjectContainer;


public class ServerTrackerInterceptor extends ServerInterceptor {

	protected static final Log logger = LogFactory.getLog(ServerTrackerInterceptor.class);

    private static ICMSServiceLocator cmsServiceLocator;

	private ITracker tracker;
	
	private IDirectoryServiceLocator directoryServiceLocator;
	
	public PortalObjectContainer portalObjectContainer;
	
	
    private ICacheService cacheService;
    
    
    
    
    private long portalParametersCount = 0;
    
    

	public ITracker getTracker() {
		return this.tracker;
	}

	public void setTracker(ITracker tracker) {
		this.tracker = tracker;
	}
	

    public IDirectoryServiceLocator getDirectoryServiceLocator() {
		return directoryServiceLocator;
	}

	public void setDirectoryServiceLocator(
			IDirectoryServiceLocator directoryServiceLocator) {
		this.directoryServiceLocator = directoryServiceLocator;
	}
	
	
	

	public ICacheService getCacheService() {
		return cacheService;
	}

	public void setCacheService(ICacheService cacheService) {
		this.cacheService = cacheService;
	}

	/**
     * Static access to CMS service.
     *
     * @return CMS service
     */
    private static ICMSService getCMSService() {
        if (cmsServiceLocator == null) {
            cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        }
        return cmsServiceLocator.getCMSService();
    }

	public PortalObjectContainer getPortalObjectContainer() {

		if (this.portalObjectContainer == null) {
            this.portalObjectContainer = Locator.findMBean(PortalObjectContainer.class, "portal:container=PortalObject");
        }

		return this.portalObjectContainer;
	}

	@Override
    protected void invoke(ServerInvocation invocation) throws Exception, InvocationException {
	    
	    
    /* For trace and profiling */
        
        String request = invocation.getServerContext().getClientRequest().getPathInfo();
        String qs = invocation.getServerContext().getClientRequest().getQueryString();
        if (qs != null)
            request += "?" + qs;

        try {
            invocation.getServerContext().getClientRequest().setAttribute("osivia.trace.url", request);

            HttpSession session = invocation.getServerContext().getClientRequest().getSession();
            List<UserAction> historic = (List<UserAction>) session.getAttribute("osivia.trace.historic");
            if (historic == null) {
                historic = new ArrayList();
                session.setAttribute("osivia.trace.historic", historic);
            }

            if (historic.size() > 50) {
                historic.remove(49);
            }


            historic.add(0, new UserAction(System.currentTimeMillis(), request));

        } catch (Exception e) {
            // No log during error management
        }


	    
	    


//	    if(true)
//	        throw new Exception("eeee");
	    
		// réinitialisation des propriétes des windows
        PageProperties.init();


	    DynamicPortalObjectContainer.clearCache();








		invocation.setAttribute(Scope.REQUEST_SCOPE, "osivia.portalObjectContainer", this.getPortalObjectContainer());

		this.getTracker().init();

		this.getTracker().pushState(invocation);

	    ServerInvocationContext context = invocation.getServerContext();

	    this.getTracker().setHttpRequest( context.getClientRequest());

	    HttpSession session = context.getClientRequest().getSession( true);

		this.getTracker().setHttpSession(session);




		  // TODO : il faut reussir à préinitialiser le host avant le LoginInterceptor
        // car le prechargement des pages doit tenir compte de la policy du host courant
        // actuellement, génération d'une exception

        // Par défaut, le portail est calculé en fonction de l'url
        // Ensuite, il sera ajusté en fonction de la commande Jboss Portal (PageMarkerInterceptor)
        // Ce traitement sert pour le calcul des pages au login
        String reqHost = invocation.getServerContext().getClientRequest().getServerName();
        String portalName = null;
        Iterator<PortalObject> portals = this.getPortalObjectContainer().getContext("").getChildren().iterator();
        while(portals.hasNext())    {
            PortalObject portal =  portals.next();
            String host = portal.getDeclaredProperty("osivia.site.hostName");
            if( reqHost.equals(host)) {
                portalName = portal.getName();
            }
        }
        if( portalName == null) {
            portalName = this.getPortalObjectContainer().getContext().getDefaultPortal().getName();
        }

        PageProperties.getProperties().getPagePropertiesMap().put(Constants.PORTAL_NAME, portalName);

        /* Récupération du domainID */

        String domainComputed = PageProperties.getProperties().getPagePropertiesMap().get("osivia.cms.domainId.computed");

        if (domainComputed == null) {

            PageProperties.getProperties().getPagePropertiesMap().put("osivia.cms.domainId.computed", "1");


            if (portalName != null) {

                PortalObjectId portalId = PortalObjectId.parse("/" + portalName, PortalObjectPath.CANONICAL_FORMAT);

                PortalObject po = this.portalObjectContainer.getObject(portalId);

                // Pas de page par defaut pour le portail util (NPE) !!!!
                Page defPage = ((Portal) po).getDefaultPage();

                if (defPage != null) {


                    String basePath = ((Portal) po).getDefaultPage().getDeclaredProperty("osivia.cms.basePath");


                    if (basePath != null) {
                        CMSServiceCtx cmsReadItemContext = new CMSServiceCtx();
                        cmsReadItemContext.setServerInvocation(invocation);
                        cmsReadItemContext.setScope("superuser_context");

                        CMSItem spaceConfig = null;
                        try {
                            spaceConfig = getCMSService().getSpaceConfig(cmsReadItemContext, basePath);
                        } catch (CMSException e) {
                            logger.error("Space config unavailable.", e);
                        }

                        if (spaceConfig != null) {
                            String domainId = spaceConfig.getDomainId();

                            if (!StringUtils.isEmpty(domainId)) {
                                PageProperties.getProperties().getPagePropertiesMap().put("osivia.cms.domainId", domainId);
                            }
                        }
                    }
                }
            }
        }


        // Check if LDAP cache should be evicted
        long newCacheCount = cacheService.getGlobalParametersCount();
        
        if (portalParametersCount < newCacheCount)  {

            IDirectoryService directoryService = directoryServiceLocator.getDirectoryService();
            if(directoryService != null) {
            	directoryService.clearCaches();
            }
            
            // V4.4 directory
            IDirProvider dirProvider = Locator.findMBean(IDirProvider.class, IDirProvider.MBEAN_NAME);
            dirProvider.clearCaches();
        	
            portalParametersCount = newCacheCount;
        }
        

		try {
				// Continue invocation
			invocation.invokeNext();
		}

		finally {
			this.getTracker().popState();

		}

	}


}
