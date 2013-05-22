package org.osivia.portal.core.tracker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.server.ServerException;
import org.jboss.portal.server.ServerInterceptor;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerInvocationContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.assistantpage.SecurePageCommand;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.portalobjects.DynamicPortalObjectContainer;
import org.osivia.portal.core.tracker.ITracker;


public class ServerTrackerInterceptor extends ServerInterceptor {

	protected static final Log logger = LogFactory.getLog(ServerTrackerInterceptor.class);

	private ITracker tracker;
	public PortalObjectContainer portalObjectContainer;

	public ITracker getTracker() {
		return tracker;
	}

	public void setTracker(ITracker tracker) {
		this.tracker = tracker;
	}

	
	public PortalObjectContainer getPortalObjectContainer() {

		if (portalObjectContainer == null)
			portalObjectContainer = Locator.findMBean(PortalObjectContainer.class, "portal:container=PortalObject");

		return portalObjectContainer;
	}
	
	protected void invoke(ServerInvocation invocation) throws Exception, InvocationException {
		
		
		//TODO : déplacer dans intercepteur spécifique
		
		// v1.0.10 : réinitialisation des propriétes des windows
		PageProperties.getProperties().init();
		
	
		// TODO : il faut reussir à préinitialiser le host avant le LoginInterceptor
		// car le prechargement des pages doit tenir compte de la policy du host courant
		// actuellement, génération d'une exception
/*		
		// Par défaut, le portail est calculé en fonction de l'url
		// Ensuite, il sera ajusté en fonction de la commande Jboss Portal
		// Ce traitement sert pour le calcul des pages au login
		String reqHost = invocation.getServerContext().getClientRequest().getServerName();
		String portalName = null;
		Iterator<PortalObject> portals = getPortalObjectContainer().getContext("").getChildren().iterator();
		while(portals.hasNext())	{
			PortalObject portal =  portals.next();
			String host = portal.getDeclaredProperty("osivia.site.hostName");
			if( reqHost.equals(host))
				portalName = portal.getName();
		}
		if( portalName == null)
			portalName = getPortalObjectContainer().getContext().getDefaultPortal().getName();
		
		PageProperties.getProperties().getPagePropertiesMap().put("portalName", portalName);
*/		
		
		
		
		
		DynamicPortalObjectContainer.clearCache();
		
		invocation.setAttribute(Scope.REQUEST_SCOPE, "osivia.portalObjectContainer", getPortalObjectContainer());
		
		getTracker().init();
			
		getTracker().pushState(invocation);
		
	    ServerInvocationContext context = invocation.getServerContext();
	    
	    getTracker().setHttpRequest( context.getClientRequest());
	    
	    HttpSession session = context.getClientRequest().getSession();
	 
		getTracker().setHttpSession(session);
		
		try {
				// Continue invocation
			invocation.invokeNext();
		}
		
		finally {
			getTracker().popState();

		}

	}

	
}
