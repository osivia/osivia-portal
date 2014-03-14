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
package org.osivia.portal.core.error;

import java.util.ArrayList;

import org.jboss.logging.Logger;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerRequestDispatcher;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.response.MarkupResponse;
import org.jboss.portal.core.model.portal.content.WindowRendition;
import org.jboss.portal.core.model.portal.control.page.PageControlContext;
import org.jboss.portal.core.model.portal.control.page.PageControlPolicy;
import org.jboss.portal.server.config.ServerConfig;
import org.jboss.portal.theme.PageService;
import org.jboss.portal.theme.PortalTheme;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.ThemeService;
import org.osivia.portal.core.error.ErrorDescriptor;
import org.osivia.portal.core.error.GlobalErrorHandler;


public class CustomPageControlPolicy extends CustomControlPolicy implements PageControlPolicy {

	private static final Logger log = Logger.getLogger(CustomPageControlPolicy.class);

	private ServerConfig serverConfig;
	private PortalObjectContainer portalObjectContainer;

	public CustomPageControlPolicy() {
		super();
	}

	public ServerConfig getServerConfig() {
		return serverConfig;
	}

	public void setServerConfig(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}

	public PortalObjectContainer getPortalObjectContainer() {
		return portalObjectContainer;
	}

	public void setPortalObjectContainer(PortalObjectContainer portalObjectContainer) {
		this.portalObjectContainer = portalObjectContainer;
	}

	protected String getPortalCharteCtx(PageControlContext controlContext) {

  	    String themeId = getPortalObjectContainer().getContext().getDefaultPortal().getProperty(ThemeConstants.PORTAL_PROP_THEME);
       PageService pageService = controlContext.getControllerContext().getController().getPageService();
        ThemeService themeService = pageService.getThemeService();
        PortalTheme theme = themeService.getThemeById(themeId);
        return theme.getThemeInfo().getContextPath();

	}

	public void doControl(PageControlContext controlContext) {
		

		WindowRendition rendition = controlContext.getRendition();
		ControllerResponse response = rendition.getControllerResponse();
		ControllerContext controllerCtx = controlContext.getControllerContext();
		String userId = getUserId(controllerCtx.getUser());
		ErrorDescriptor errDescriptor = getErrorDescriptor(response, userId);

		if (errDescriptor != null) {
			long errId = GlobalErrorHandler.getInstance().logError(errDescriptor);
			boolean affichage = false;

			try {

				ControllerRequestDispatcher rd = controllerCtx.getRequestDispatcher(getPortalCharteCtx(controlContext),
						"/error/errorDiv.jsp?err=" + errId);

				if (rd != null) {
					rd.include();
					String markup = rd.getMarkup();
					
					//initialiser les supported MODE si ce n'est pas déjà fait
					// Plante dans le cas d'un 404
					
					if( rendition.getSupportedModes()== null)
						rendition.setSupportedModes(new ArrayList());
					
					if( rendition.getSupportedWindowStates()== null)
						rendition.setSupportedWindowStates(new ArrayList());
				

					rendition.setControllerResponse(new MarkupResponse("An error occured", markup, null));
					affichage = true;
				}
			}

			catch (Exception e) {

				log.error("cannot obtain RequestDispatcher for '" + getPortalCharteCtx(controlContext) + "/error/errorDiv.jsp'");
			}

			if (!affichage)
				rendition.setControllerResponse(new MarkupResponse("Erreur technique", "An error occured", null));

		}
	}

}
