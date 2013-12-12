package org.osivia.portal.core.tracker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.server.ServerException;
import org.jboss.portal.server.ServerInterceptor;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerInvocationContext;
import org.osivia.portal.core.assistantpage.SecurePageCommand;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.portalobjects.DynamicPortalObjectContainer;
import org.osivia.portal.core.tracker.ITracker;


public class ServerTrackerInterceptor extends ServerInterceptor {

	protected static final Log logger = LogFactory.getLog(ServerTrackerInterceptor.class);

	private ITracker tracker;

	public ITracker getTracker() {
		return tracker;
	}

	public void setTracker(ITracker tracker) {
		this.tracker = tracker;
	}

	protected void invoke(ServerInvocation invocation) throws Exception, InvocationException {
		
		
		// TODO : déplacer dans un intercepteur
		// TODO : paramétrer les googlebot dans environnement_portail.properties
		
		// prevent google unwanted request (pagemarker)

		HttpServletRequest request = invocation.getServerContext().getClientRequest();

		String header = request.getHeader("User-Agent");
		String uri = request.getRequestURI();

		// logger.info("header:" + header + "uri:"+ uri);

		if (request.getRemoteUser() == null && header != null && uri != null && header.toLowerCase().indexOf("googlebot/2.1") != -1 && uri.contains("/pagemarker/")) {
			invocation.getServerContext().getClientResponse().setStatus(403);
			return;
		}


		
		//TODO : déplacer dans intercepteur spécifique
		
		// v1.0.10 : réinitialisation des propriétes des windows
		PageProperties.getProperties().init();

		
		
		DynamicPortalObjectContainer.clearCache();

		
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
