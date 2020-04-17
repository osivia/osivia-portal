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
package org.osivia.portal.core.myworkspace;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.controller.command.response.ErrorResponse;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.controller.command.response.SecurityErrorResponse;
import org.jboss.portal.core.impl.api.node.PageURL;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowResourceCommand;
import org.jboss.portal.portlet.cache.CacheLevel;
import org.jboss.portal.server.ServerURL;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.assistantpage.AssistantCommand;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.profils.IProfilManager;
import org.osivia.portal.core.profils.ProfilBean;
import org.osivia.portal.core.web.WebCommand;
import org.osivia.portal.core.web.WebURLFactory;


public class MyWorkspaceCommand extends ControllerCommand {

    /** . */
    private static final CommandInfo info = new ActionCommandInfo(false);
    protected static final Log logger = LogFactory.getLog(MyWorkspaceCommand.class);
    
    /** CMS service locator. */
    private static ICMSServiceLocator cmsServiceLocator;


    public MyWorkspaceCommand() {
    }


    @Override
    public CommandInfo getInfo() {
        return info;
    }
    


    /**
     * Static access to CMS service.
     *
     * @return CMS service
     * @throws Exception
     */
    public static ICMSService getCMSService() throws Exception {
        if (cmsServiceLocator == null) {
            cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        }
        return cmsServiceLocator.getCMSService();
    }




    @Override
    public ControllerResponse execute() throws ControllerException {
        try {
            
            if (getControllerContext().getServerInvocation().getServerContext().getClientRequest().getUserPrincipal() == null) {
                return new SecurityErrorResponse("Vous devez être connecté", SecurityErrorResponse.NOT_AUTHORIZED,
                        false);
            }

              
            PortalControllerContext ctx = new PortalControllerContext(getControllerContext());
            CMSServiceCtx cmsContext = new CMSServiceCtx();
            cmsContext.setControllerContext(getControllerContext());

            CMSItem userWorkspace;
            try {
                userWorkspace = getCMSService().getUserWorkspace(cmsContext);
            } catch (CMSException e) {
                userWorkspace = null;
                this.log.error("Unable to get user workspaces.", e.fillInStackTrace());
            }



            // User workspace URL

            if ((userWorkspace != null) && StringUtils.isNotEmpty(userWorkspace.getCmsPath())) {

                // Portal URL factory
                IPortalUrlFactory urlFactory = Locator.findMBean(IPortalUrlFactory.class, "osivia:service=UrlFactory");                    
                String cmsUrl = urlFactory.getCMSUrl(new PortalControllerContext(getControllerContext()), null, userWorkspace.getCmsPath(), null, null, null, null, null, null, null);

                return  new RedirectionResponse(cmsUrl);

            } 
            
            
            return new ErrorResponse("No user workspace", true);

        } catch (Exception e) {
            if (!(e instanceof ControllerException)) {
                throw new ControllerException(e);
            } else {
                throw (ControllerException) e;
            }
        }

    }
}
