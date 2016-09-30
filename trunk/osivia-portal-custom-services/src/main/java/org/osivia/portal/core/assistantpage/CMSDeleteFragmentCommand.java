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
package org.osivia.portal.core.assistantpage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.controller.command.response.SecurityErrorResponse;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;

/**
 * CMS command used when a fragment is removed in the page
 */
public class CMSDeleteFragmentCommand extends ControllerCommand {

    ICMSService cmsService;

    private static ICMSServiceLocator cmsServiceLocator;

    public static ICMSService getCMSService() throws Exception {

        if (cmsServiceLocator == null) {
            cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        }

        return cmsServiceLocator.getCMSService();

    }

    private static final CommandInfo info = new ActionCommandInfo(false);
    protected static final Log logger = LogFactory.getLog(CMSDeleteFragmentCommand.class);

    /** page ID used for reload after delete */
    private String pageId;

    /** page path used by the ECM */
    private String pagePath;

    /** fragment identifier */
    private String refURI;


    /**
     * @return the pagePath
     */
    public String getPagePath() {
        return pagePath;
    }


    /**
     * @param pagePath the pagePath to set
     */
    public void setPagePath(String pagePath) {
        this.pagePath = pagePath;
    }


    /**
     * @return the refURI
     */
    public String getRefURI() {
        return refURI;
    }


    /**
     * @param refURI the refURI to set
     */
    public void setRefURI(String refURI) {
        this.refURI = refURI;
    }

    /**
     * @return the pageId
     */
    public String getPageId() {
        return pageId;
    }

    public CMSDeleteFragmentCommand(String pageId, String pagePath, String refURI) {
        this.pageId = pageId;
        this.pagePath = pagePath;
        this.refURI = refURI;

    }

    public CommandInfo getInfo() {
        return info;
    }


    public ControllerResponse execute() throws ControllerException {


        try {
            // Contrôle droits

            PortalObjectId poid = PortalObjectId.parse(pageId, PortalObjectPath.SAFEST_FORMAT);
            PortalObject page = getControllerContext().getController().getPortalObjectContainer().getObject(poid);

            CMSServiceCtx cmsCtx = new CMSServiceCtx();
            cmsCtx.setControllerContext(getControllerContext());


            if (!CMSEditionPageCustomizerInterceptor.checkWritePermission(context, (Page) page))
                return new SecurityErrorResponse(SecurityErrorResponse.NOT_AUTHORIZED, false);


            getCMSService().deleteFragment(cmsCtx, pagePath, refURI);

            // Force la mise des editables windows
            getControllerContext().getServerInvocation().setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.editableWindows." + "preview" + "." + pagePath,
                    null);


            return new UpdatePageResponse(poid);


        } catch (Exception e) {
            if (!(e instanceof ControllerException))
                throw new ControllerException(e);
            else
                throw (ControllerException) e;
        }
    }

}
