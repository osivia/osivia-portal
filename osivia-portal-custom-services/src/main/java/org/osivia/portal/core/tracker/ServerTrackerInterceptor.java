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

import java.util.Iterator;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.server.ServerInterceptor;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerInvocationContext;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.portalobjects.DynamicPortalObjectContainer;


public class ServerTrackerInterceptor extends ServerInterceptor {

	protected static final Log logger = LogFactory.getLog(ServerTrackerInterceptor.class);

	private ITracker tracker;
	public PortalObjectContainer portalObjectContainer;

	public ITracker getTracker() {
		return this.tracker;
	}

	public void setTracker(ITracker tracker) {
		this.tracker = tracker;
	}


	public PortalObjectContainer getPortalObjectContainer() {

		if (this.portalObjectContainer == null) {
            this.portalObjectContainer = Locator.findMBean(PortalObjectContainer.class, "portal:container=PortalObject");
        }

		return this.portalObjectContainer;
	}

	protected void invoke(ServerInvocation invocation) throws Exception, InvocationException {


		// réinitialisation des propriétes des windows
		PageProperties.getProperties().init();


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





		try {
				// Continue invocation
			invocation.invokeNext();
		}

		finally {
			this.getTracker().popState();

		}

	}


}
