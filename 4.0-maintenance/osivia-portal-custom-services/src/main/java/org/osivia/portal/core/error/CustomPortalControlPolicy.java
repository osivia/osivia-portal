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

import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.command.response.MarkupResponse;
import org.jboss.portal.core.model.portal.control.page.PageControlContext;
import org.jboss.portal.core.model.portal.control.portal.PortalControlContext;
import org.jboss.portal.core.model.portal.control.portal.PortalControlPolicy;
import org.jboss.portal.theme.PageService;
import org.jboss.portal.theme.PortalTheme;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.ThemeService;
import org.osivia.portal.core.error.ErrorDescriptor;
import org.osivia.portal.core.error.GlobalErrorHandler;
import org.w3c.dom.Element;



public class CustomPortalControlPolicy extends CustomControlPolicy implements PortalControlPolicy {

	private PortalObjectContainer portalObjectContainer;
	
	public CustomPortalControlPolicy() {
		super();
	}

	public PortalObjectContainer getPortalObjectContainer() {
		return portalObjectContainer;
	}

	public void setPortalObjectContainer(PortalObjectContainer portalObjectContainer) {
		this.portalObjectContainer = portalObjectContainer;
	}

	
	protected String getPortalCharteCtx(PortalControlContext controlContext) {
 
 	    String themeId = getPortalObjectContainer().getContext().getDefaultPortal().getProperty(ThemeConstants.PORTAL_PROP_THEME);
 	    PageService pageService = controlContext.getControllerContext().getController().getPageService();
        ThemeService themeService = pageService.getThemeService();
        PortalTheme theme = themeService.getThemeById(themeId);
        return theme.getThemeInfo().getContextPath();
	}

	public void doControl(PortalControlContext controlContext) {
		ControllerResponse response = controlContext.getResponse();
		String userId = getUserId(controlContext.getControllerContext().getUser());
		ErrorDescriptor errDescriptor = getErrorDescriptor(response, userId);
		
		if( errDescriptor != null) {			
			long errId = GlobalErrorHandler.getInstance().logError(errDescriptor);

			controlContext.setResponse(new RedirectionResponse(getPortalCharteCtx(controlContext) + "/error/errorPage.jsp?err=" + errId+ "&httpCode=" + errDescriptor.getHttpErrCode()));
		}
	}
	
}