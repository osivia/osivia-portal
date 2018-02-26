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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.control.portal.PortalControlContext;
import org.jboss.portal.core.model.portal.control.portal.PortalControlPolicy;
import org.jboss.portal.theme.PageService;
import org.jboss.portal.theme.PortalTheme;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.ThemeService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.log.LogContext;



public class CustomPortalControlPolicy extends CustomControlPolicy implements PortalControlPolicy {

	private PortalObjectContainer portalObjectContainer;
	
    /** Log context. */
    private LogContext logContext;

    /** Default log. */
    private final Log defaultLog;


	public CustomPortalControlPolicy() {
		super();
        this.defaultLog = LogFactory.getLog(this.getClass());
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
        ControllerContext controllerContext = controlContext.getControllerContext();
        String userId = getUserId(controllerContext.getUser());
		
		String portalId = controlContext.getPortalId().toString(PortalObjectPath.CANONICAL_FORMAT);
		
		ErrorDescriptor errDescriptor = getErrorDescriptor(response, userId, null, portalId);
		
        if (errDescriptor != null) {
            PortalControllerContext portalControllerContext = new PortalControllerContext(controllerContext);

            String token = this.logContext.createContext(portalControllerContext, "portal", null);
            this.defaultLog.error("Portlet error", errDescriptor.getException());

            errDescriptor.setToken(token);

			GlobalErrorHandler.getInstance().logError(errDescriptor);

            // URL encoded token
            String encodedToken;
            try {
                encodedToken = URLEncoder.encode(token, CharEncoding.UTF_8);
            } catch (UnsupportedEncodingException e) {
                this.defaultLog.error("Token URL encoding error", e);
                encodedToken = StringUtils.EMPTY;
            }


            controlContext.setResponse(new RedirectionResponse(
                    getPortalCharteCtx(controlContext) + "/error/errorPage.jsp?httpCode=" + errDescriptor.getHttpErrCode() + "&token=" + encodedToken));
		}
	}


    /**
     * Setter for logContext.
     * 
     * @param logContext the logContext to set
     */
    public void setLogContext(LogContext logContext) {
        this.logContext = logContext;
    }
	
}
