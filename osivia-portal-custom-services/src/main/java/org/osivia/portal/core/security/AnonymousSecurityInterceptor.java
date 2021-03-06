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
package org.osivia.portal.core.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerInterceptor;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.controller.command.response.SecurityErrorResponse;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.PageCommand;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowCommand;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.model.portal.command.view.ViewPageCommand;
import org.jboss.portal.server.request.URLContext;
import org.jboss.portal.server.request.URLFormat;
import org.osivia.portal.core.page.PermLinkCommand;


/**
 * Pas d'affichage 403 si une page est interdite à un utilisteur non connecté
 * 
 * A la place, on redirige vers l'authentification
 * 
 * @author jeanseb
 *
 */
public class AnonymousSecurityInterceptor extends ControllerInterceptor {

	
	/** . */
	protected static final Log logger = LogFactory.getLog(AnonymousSecurityInterceptor.class);

	/** . */


	public ControllerResponse invoke(ControllerCommand cmd) throws Exception {
		
		
		ControllerResponse resp;

		 resp = (ControllerResponse) cmd.invokeNext();
		
			// Uniquement sur les view pages
			if (cmd instanceof ViewPageCommand && resp instanceof SecurityErrorResponse) {
				
				if( ( (SecurityErrorResponse) resp).getStatus() == SecurityErrorResponse.NOT_AUTHORIZED)	{
					
					// Et en mode anonyme
					if( cmd.getControllerContext().getUser() == null)	{
						
						URLContext urlContext = cmd.getControllerContext().getServerInvocation().getServerContext().getURLContext();
						
						// On force l'authentification
						urlContext = urlContext.withAuthenticated(true);
						
						// Et on redirige sur l'authentification
						String url = cmd.getControllerContext().renderURL(cmd, urlContext, URLFormat.newInstance(false, true));

						return new RedirectionResponse(url);
					}
				
				}

			}
			
		
		//
		return resp;
	}


}
