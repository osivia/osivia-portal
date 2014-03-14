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
package org.osivia.portal.core.contribution;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.controller.command.response.SecurityErrorResponse;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.contribution.IContributionService;
import org.osivia.portal.api.contribution.IContributionService.EditionState;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.internationalization.InternationalizationUtils;
import org.osivia.portal.core.notifications.NotificationsUtils;
import org.osivia.portal.core.page.PageProperties;

/**
 * CMS command used when a document is published
 */
public class PublishContributionCommand extends ControllerCommand {



    private static final String SUCCESS_MESSAGE_PUBLISH = "SUCCESS_MESSAGE_PUBLISH";
    private static final String SUCCESS_MESSAGE_UNPUBLISH = "SUCCESS_MESSAGE_UNPUBLISH";

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
    protected static final Log logger = LogFactory.getLog(PublishContributionCommand.class);

    /** page ID used for reload after delete */
    private String windowId;

    /** page path used by the ECM */
    private String docPath;

    /** action (publish / unpublish) */
    private String actionCms;

    /**
     * @return the pagePath
     */
    public String getDocPath() {
        return docPath;
    }


    /**
     * @param pagePath the pagePath to set
     */
    public void setDocPath(String docPath) {
        this.docPath = docPath;
    }


    /**
     * @return the pageId
     */
    public String getWindowId() {
        return windowId;
    }

    /**
     * @return the actionCms
     */
    public String getActionCms() {
        return actionCms;
    }

    /**
     * @param actionCms the actionCms to set
     */
    public void setActionCms(String actionCms) {
        this.actionCms = actionCms;
    }


    public PublishContributionCommand(String windowId, String docPath, String actionCms) {
        this.windowId = windowId;
        this.docPath = docPath;
        this.actionCms = actionCms;

    }

    public CommandInfo getInfo() {
        return info;
    }


    public ControllerResponse execute() throws ControllerException {


        try {
            // Contrôle droits

            PortalObjectId poid = PortalObjectId.parse(windowId, PortalObjectPath.SAFEST_FORMAT);
            Window window = (Window) getControllerContext().getController().getPortalObjectContainer().getObject(poid);

            CMSServiceCtx cmsCtx = new CMSServiceCtx();
            cmsCtx.setControllerContext(getControllerContext());


  
            PortalControllerContext pcc = new PortalControllerContext(getControllerContext());

            if (actionCms.equals(IContributionService.PUBLISH)) {
                getCMSService().publishDocument(cmsCtx, docPath);

                String success = itlzService.getString(SUCCESS_MESSAGE_PUBLISH, getControllerContext().getServerInvocation().getRequest().getLocale());
                notifService.addSimpleNotification(pcc, success, NotificationsType.SUCCESS);
            } else if (actionCms.equals(IContributionService.UNPUBLISH)) {
                getCMSService().unpublishDocument(cmsCtx, docPath);

                String success = itlzService.getString(SUCCESS_MESSAGE_UNPUBLISH, getControllerContext().getServerInvocation().getRequest().getLocale());
                notifService.addSimpleNotification(pcc, success, NotificationsType.SUCCESS);
            }
            
            
            ContributionService.setWindowEditionState(getControllerContext(), window.getId(), new EditionState(EditionState.CONTRIBUTION_MODE_ONLINE,docPath));

            PageProperties.getProperties().setRefreshingPage(true);
            
            return new UpdatePageResponse(window.getPage().getId());


        } catch (Exception e) {
            if (!(e instanceof ControllerException))
                throw new ControllerException(e);
            else
                throw (ControllerException) e;
        }
    }

}
