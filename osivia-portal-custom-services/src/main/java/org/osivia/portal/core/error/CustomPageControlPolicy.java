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

import java.net.URLEncoder;
import java.util.ArrayList;

import javax.portlet.PortletException;

import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.logging.Logger;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerRequestDispatcher;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.impl.model.content.portlet.PortletContent;
import org.jboss.portal.core.model.content.Content;
import org.jboss.portal.core.model.instance.InstanceDefinition;
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
import org.osivia.portal.api.PortalApplicationException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.log.LogContext;


public class CustomPageControlPolicy extends CustomControlPolicy implements PageControlPolicy {

	private ServerConfig serverConfig;
	private PortalObjectContainer portalObjectContainer;
	
	/** Log context. */
	private LogContext logContext;
	
	/** Portal log. */
	private final Logger portalLog;
    /** Default log. */
    private final Log defaultLog;
	

	public CustomPageControlPolicy() {
		super();
		this.portalLog = Logger.getLogger("PORTAL");
		this.defaultLog = LogFactory.getLog(this.getClass());
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
	
	public static String getPortletName ( ControllerContext controllerCtx, PortalObjectId poid){
        String portletName = null;
        try {
            Window window = (Window) controllerCtx.getController().getPortalObjectContainer().getObject(poid);
            Content content = window.getContent();
            String instanceId = ((PortletContent) content).getInstanceRef();
            InstanceDefinition instance = controllerCtx.getController().getInstanceContainer().getDefinition(instanceId);
            portletName = instance.getPortlet().getInfo().getName();
        } catch (Exception e) {
            //
        }

        return portletName;
	}
	

	public void doControl(PageControlContext controlContext) {
		WindowRendition rendition = controlContext.getRendition();
		ControllerResponse response = rendition.getControllerResponse();
		ControllerContext controllerContext = controlContext.getControllerContext();
		String userId = getUserId(controllerContext.getUser());
		
		
		String portletName=getPortletName(controllerContext, controlContext.getWindowId());
		

		ErrorDescriptor errDescriptor = getErrorDescriptor(response, userId, portletName, null);



        if (errDescriptor != null) {
            try {
                controllerContext.getRequestDispatcher(getPortalCharteCtx(controlContext), "/error/errorDiv.jsp");
            } catch (Exception e) {
                // Request is not operationnal (timeout)
                // Cant log
                errDescriptor = null;
                this.portalLog.warn("timeout error dump", e);
           }
        }
        
        
		
		if (errDescriptor != null) {
            // Portal controller context
            PortalControllerContext portalControllerContext = new PortalControllerContext(controllerContext);

            // Exception
            Throwable throwable = errDescriptor.getException();

            // Portlet exception
            PortletException portletException;
            if (throwable == null) {
                portletException = null;
            } else {
                try {
                    throw throwable;
                } catch (PortletException e) {
                    portletException = e;
                } catch (Throwable e) {
                    portletException = null;
                }
            }

            // Application exception
            PortalApplicationException applicationException;
            if ((portletException == null) || (portletException.getCause() == null)) {
                applicationException = null;
            } else {
                try {
                    throw portletException.getCause();
                } catch (PortalApplicationException e) {
                    applicationException = e;
                } catch (Throwable e) {
                    applicationException = null;
                }
            }

            // Token
            String token;

            if (applicationException == null) {
                token = this.logContext.createContext(portalControllerContext, "portal", null);
                this.defaultLog.error("Portlet error", throwable);
            } else {
                token = this.logContext.createContext(portalControllerContext, StringUtils.defaultIfEmpty(applicationException.getDomain(), "portal"),
                        applicationException.getCode());
                this.defaultLog.error(StringUtils.defaultIfEmpty(applicationException.getMessage(), "Portlet error"));
            }
		    
		    errDescriptor.setToken(token);
		    
			GlobalErrorHandler.getInstance().logError(errDescriptor);
			boolean affichage = false;

			
			try {
				ControllerRequestDispatcher rd = controllerContext.getRequestDispatcher(getPortalCharteCtx(controlContext),
						"/error/errorDiv.jsp?token=" + URLEncoder.encode(token, CharEncoding.UTF_8));

 				
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
			    this.portalLog.error("cannot obtain RequestDispatcher for '" + getPortalCharteCtx(controlContext) + "/error/errorDiv.jsp'");
			}

			if (!affichage)
				rendition.setControllerResponse(new MarkupResponse("Erreur technique", "An error occured", null));

		}
	}

    
    public void setLogContext(LogContext logContext) {
        this.logContext = logContext;
    }

}
