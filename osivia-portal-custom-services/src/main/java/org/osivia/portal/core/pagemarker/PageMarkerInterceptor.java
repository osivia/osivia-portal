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
package org.osivia.portal.core.pagemarker;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerInterceptor;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.PageCommand;
import org.jboss.portal.core.model.portal.command.PortalCommand;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowActionCommand;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowCommand;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowResourceCommand;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.osivia.portal.api.Constants;
import org.osivia.portal.core.dynamic.StopDynamicPageCommand;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.web.WebCommand;


/**
 * Gestion des états de la page
 * 
 * (permettent de gérér le back et le multi-onglets)
 * 
 * @author jeanseb
 * 
 */
public class PageMarkerInterceptor extends ControllerInterceptor {


    /** . */
    protected static final Log logger = LogFactory.getLog(PageMarkerInterceptor.class);

    private PortalObjectContainer portalObjectContainer;

    /** . */

    public PortalObjectContainer getPortalObjectContainer() {
        return this.portalObjectContainer;
    }

    public void setPortalObjectContainer(PortalObjectContainer portalObjectContainer) {
        this.portalObjectContainer = portalObjectContainer;
    }


    public ControllerResponse invoke(ControllerCommand cmd) throws Exception {


        ControllerResponse resp;

        /* Initialisation page marker */

        {
            ControllerContext controllerCtx = cmd.getControllerContext();

            if (cmd instanceof InvokePortletWindowResourceCommand) {
                // on reprend le page marker pour les ressources sans l'incrémenter
                String currentPageMarker = (String) controllerCtx.getAttribute(Scope.REQUEST_SCOPE, "controlledPageMarker");
                controllerCtx.setAttribute(Scope.REQUEST_SCOPE, "currentPageMarker", StringUtils.defaultIfEmpty(currentPageMarker, "0"));
            } else {
                // Initialisation standard
                PageMarkerUtils.getCurrentPageMarker(controllerCtx);
            }
        }


        // Calcul du nom de portail

        boolean computePortal = true;
        if ( cmd instanceof WebCommand)	{
        	// On peut sortir d'une modale par echap ou Fermer
        	// Mais la command Web, ne porte pas de pagemarker
        	// On se contente du traitement du ServerTrackerInterceptor
            PortalObjectId currentPageId = (PortalObjectId) cmd.getControllerContext().getAttribute(ControllerCommand.PRINCIPAL_SCOPE, Constants.ATTR_PAGE_ID);
            if(currentPageId.getPath().toString().equals("/osivia-util/modal"))
            	computePortal = false;
            		
        }
        
        if( computePortal)	{
	        String portalName = null;
	        if (cmd instanceof PortalCommand) {
	            portalName = ((PortalCommand) cmd).getPortal().getName();
	        } else {
	            PortalObjectId currentPageId = (PortalObjectId) cmd.getControllerContext().getAttribute(ControllerCommand.PRINCIPAL_SCOPE, Constants.ATTR_PAGE_ID);
	
	            if (currentPageId == null) {
	                portalName = this.getPortalObjectContainer().getContext().getDefaultPortal().getName();
	            } else {
	                portalName = currentPageId.getPath().getName(0);
	            }
	
	        }
	        PageProperties.getProperties().getPagePropertiesMap().put(Constants.PORTAL_NAME, portalName);
        }

        resp = (ControllerResponse) cmd.invokeNext();

        boolean alreadySaved = false;
        
        if (cmd instanceof InvokePortletWindowCommand && (ControllerContext.AJAX_TYPE == cmd.getControllerContext().getType())) {
            
            ControllerContext controllerCtx = cmd.getControllerContext();
            
            Window window = ((InvokePortletWindowCommand)cmd).getWindow();
            
            //  sauvegarde des infos associées au markeur de page
            
            PageMarkerUtils.savePageState(controllerCtx, window.getPage());
            
            alreadySaved = true;
        }
        
        if (!alreadySaved &&   cmd instanceof InvokePortletWindowActionCommand) {

            ControllerContext controllerCtx = cmd.getControllerContext();

            Window window = ((InvokePortletWindowActionCommand) cmd).getWindow();

            PageMarkerUtils.savePageState(controllerCtx, window.getPage());

        }

        if (!alreadySaved &&  cmd instanceof RenderPageCommand) {

            ControllerContext controllerCtx = cmd.getControllerContext();
            Page page = ((PageCommand) cmd).getPage();

            // sauvegarde des infos associées au markeur de page

            PageMarkerUtils.savePageState(controllerCtx, page);
        }

        
        if (!alreadySaved &&  cmd instanceof StopDynamicPageCommand) {

            ControllerContext controllerCtx = cmd.getControllerContext();

            // sauvegarde des infos associées au markeur de page

            PageMarkerUtils.savePageState(controllerCtx, null);
        }

        //
        return resp;
    }


}
