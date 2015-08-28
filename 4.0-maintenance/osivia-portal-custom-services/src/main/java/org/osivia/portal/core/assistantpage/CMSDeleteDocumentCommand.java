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
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.core.cms.CMSObjectPath;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.CmsCommand;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.internationalization.InternationalizationUtils;
import org.osivia.portal.core.notifications.NotificationsUtils;
import org.osivia.portal.core.page.PageProperties;

/**
 * CMS command used to remove a document
 */
public class CMSDeleteDocumentCommand extends ControllerCommand {

    private static final String SUCCESS_MESSAGE_DELETE = "SUCCESS_MESSAGE_DELETE";

    ICMSService cmsService;

    private static INotificationsService notifService = NotificationsUtils.getNotificationsService();
    private static IInternationalizationService itlzService = InternationalizationUtils.getInternationalizationService();

    private static ICMSServiceLocator cmsServiceLocator;

    public static ICMSService getCMSService() throws Exception {

        if (cmsServiceLocator == null) {
            cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        }

        return cmsServiceLocator.getCMSService();

    }

    private static final CommandInfo info = new ActionCommandInfo(false);
    protected static final Log logger = LogFactory.getLog(CMSDeleteDocumentCommand.class);

    /** page ID used for reload after delete */
    private String pageId;

    /** page path used by the ECM */
    private String pagePath;

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
     * @return the pageId
     */
    public String getPageId() {
        return pageId;
    }

    public CMSDeleteDocumentCommand(String pageId, String pagePath) {
        this.pageId = pageId;
        this.pagePath = pagePath;
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


            PortalControllerContext pcc = new PortalControllerContext(getControllerContext());


//            getCMSService().deleteDocument(cmsCtx, pagePath);
            getCMSService().putDocumentInTrash(cmsCtx, pagePath);

            String success = itlzService.getString(SUCCESS_MESSAGE_DELETE, getControllerContext().getServerInvocation().getRequest().getLocale());
            notifService.addSimpleNotification(pcc, success, NotificationsType.SUCCESS);


            // relaod navigation tree
            PageProperties.getProperties().setRefreshingPage(true);


            // Redirect to the parent window
            CMSObjectPath parentPath = CMSObjectPath.parse(pagePath).getParent();
            String redirectPath = parentPath.toString();

            CmsCommand redirect = new CmsCommand(null, redirectPath, null, null, null, null, null, null, null, null, null);
            ControllerResponse execute = context.execute(redirect);

            return execute;


        } catch (Exception e) {
            if (!(e instanceof ControllerException))
                throw new ControllerException(e);
            else
                throw (ControllerException) e;
        }
    }

}
