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
package org.osivia.portal.core.dynamic;

// import org.apache.commons.logging.Log;
// import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.api.PortalURL;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.view.ViewPageCommand;
import org.osivia.portal.api.theming.Breadcrumb;
import org.osivia.portal.api.theming.BreadcrumbItem;
import org.osivia.portal.core.page.PortalURLImpl;


public class StopDynamicWindowCommand extends DynamicCommand {

    private static final CommandInfo info = new ActionCommandInfo(false);
    protected static final Log logger = LogFactory.getLog(StopDynamicWindowCommand.class);

    public CommandInfo getInfo() {
        return info;
    }

    private String windowId;
    private String pageId;


    public StopDynamicWindowCommand() {
    }

    public StopDynamicWindowCommand(String pageId, String windowId) {
        this.pageId = pageId;
        this.windowId = windowId;
    }

    public ControllerResponse execute() throws ControllerException {

        try {
            PortalObjectId pageid = PortalObjectId.parse(pageId, PortalObjectPath.SAFEST_FORMAT);


            Window window = (Window) getControllerContext().getController().getPortalObjectContainer()
                    .getObject(PortalObjectId.parse(windowId, PortalObjectPath.SAFEST_FORMAT));


            // 2.0.22 : fixes NPE if session has expired
            if (window != null) {

                String closeUrl = window.getProperty("osivia.dynamic.close_url");

                if (closeUrl != null) {
                    return new RedirectionResponse(closeUrl);
                }


                // Lecture du breadcrumb
                Breadcrumb breadcrumb = (Breadcrumb) getControllerContext().getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "breadcrumb");


                // Si plusieurs items, renvoi vers l'item précédent (enchainement d'actions)

                if (breadcrumb.getChildren().size() > 1) {
                    // Renvoi vers l'item precedent
                    BreadcrumbItem lastItem = breadcrumb.getChildren().get(breadcrumb.getChildren().size() - 2);
                    return new RedirectionResponse(lastItem.getUrl());
                }
            }


            /*
             * 
             * // Si contenu contextualisé, renvoi sur le cms
             * 
             * NavigationalStateContext nsContext = (NavigationalStateContext) context
             * .getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
             * 
             * String pagePath = pageid.toString(PortalObjectPath.CANONICAL_FORMAT);
             * 
             * PageNavigationalState pns = nsContext.getPageNavigationalState( pageid.toString(PortalObjectPath.CANONICAL_FORMAT));
             * 
             * if( pns != null ) {
             * 
             * 
             * // Par défaut, renvoi sur la rubrique
             * 
             * 
             * String cmsNav[] = pns.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.path") );
             * 
             * if( cmsNav != null && cmsNav.length > 0) {
             * CmsCommand cmsCommand = new CmsCommand(pagePath, cmsNav[0], null, "0");
             * 
             * return context.execute(cmsCommand);
             * }
             * 
             * 
             * 
             * }
             */


            // Sinon renvoi sur la page


            ViewPageCommand pageCmd = new ViewPageCommand(pageid);
            PortalURL url = new PortalURLImpl(pageCmd, getControllerContext(), null, null);


            String redirectUrl = url.toString() + "?init-state=true";


            return new RedirectionResponse(redirectUrl);

        } catch (Exception e) {
            throw new ControllerException(e);
        }

    }

}
