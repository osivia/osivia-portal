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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.command.mapper.DelegatingURLFactoryService;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowResourceCommand;
import org.jboss.portal.portlet.cache.CacheLevel;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerURL;
import org.osivia.portal.core.cms.CmsCommand;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.page.PermLinkCommand;
import org.osivia.portal.core.share.ShareCommand;
import org.osivia.portal.core.tasks.UpdateTaskCommand;
import org.osivia.portal.core.tracker.ITracker;
import org.osivia.portal.core.web.WebCommand;
import org.osivia.portal.core.web.WebURLFactory;


/**
 *
 * ajout d'un tag /pagemarker dans l'url pour associer à chaque page
 * l'état des portlets
 *
 * @author jeanseb
 *
 */

public class PortalDelegatingURLFactoryService extends DelegatingURLFactoryService {

    protected static final Log logger = LogFactory.getLog(PortalDelegatingURLFactoryService.class);

    private ITracker tracker;

    public ITracker getTracker() {
        return this.tracker;
    }

    public void setTracker(ITracker tracker) {
        this.tracker = tracker;
    }


    @Override
    public ServerURL doMapping(ControllerContext controllerContext, ServerInvocation invocation, ControllerCommand cmd) {


        ServerURL url = super.doMapping(controllerContext, invocation, cmd);


        /* Adapt URL to Web navigation */
        ServerURL webURL = WebURLFactory.doWebMapping(controllerContext, invocation, cmd, url);
        if (webURL != null) {
            return webURL;
        }

        // No page marker for web urls
        if (cmd instanceof WebCommand) {
            return url;
        }

        // Neither for share url
        if (cmd instanceof ShareCommand) {
            return url;
        }


        boolean pageMarkerInsertion = true;

        if ("1".equals(PageProperties.getProperties().getPagePropertiesMap().get("osivia.portal.disablePageMarker"))) {
            pageMarkerInsertion = false;
        }

        if ((cmd instanceof PermLinkCommand) || (cmd instanceof UpdateTaskCommand)) {
            pageMarkerInsertion = false;
        }

        if (cmd instanceof CmsCommand) {
            if (!((CmsCommand) cmd).isInsertPageMarker()) {
                pageMarkerInsertion = false;
            }
        }

        // Ressource with ID must preserve navigation context

        if (cmd instanceof InvokePortletWindowResourceCommand) {
            pageMarkerInsertion = false;

            if (((InvokePortletWindowResourceCommand) cmd).getCacheability() != CacheLevel.FULL) {
                pageMarkerInsertion = true;
            }
        }


        // if( ! (cmd instanceof InvokePortletWindowResourceCommand) && pageMarkerInsertion) {
        if (pageMarkerInsertion) {

            String pageMarker = PageMarkerUtils.getCurrentPageMarker(controllerContext);


            if ((url != null) && (pageMarker != null)) {
                url.setPortalRequestPath(PageMarkerUtils.PAGE_MARKER_PATH + pageMarker + url.getPortalRequestPath());
            }
        }


        return url;
    }

}
