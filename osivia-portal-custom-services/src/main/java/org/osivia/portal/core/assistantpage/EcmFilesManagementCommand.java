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
 */
package org.osivia.portal.core.assistantpage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.api.urls.EcmFilesCommand;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.CmsCommand;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.internationalization.InternationalizationUtils;
import org.osivia.portal.core.notifications.NotificationsUtils;
import org.osivia.portal.core.page.PageProperties;

/**
 * portal command to send ECM commands like synchronizing folder or check out documents 
 * 
 * @author lbi
 * 
 */
public class EcmFilesManagementCommand extends ControllerCommand {


    private static final String SUCCESS_MESSAGE_SYNCHRO = "SUCCESS_MESSAGE_SYNCHRO";
    private static final String SUCCESS_MESSAGE_UNSYNCHRO = "SUCCESS_MESSAGE_UNSYNCHRO";

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
    protected static final Log logger = LogFactory.getLog(EcmFilesManagementCommand.class);

    /** cms path used by the ECM */
    private String cmsPath;

    /** ECM command */
    private EcmFilesCommand command;


    /**
     * @return the cmsPath
     */
    public String getCmsPath() {
        return cmsPath;
    }

    

    public EcmFilesCommand getCommand() {
		return command;
	}



	public void setCommand(EcmFilesCommand command) {
		this.command = command;
	}



	/**
     * @param cmsPath the cmsPath to set
     */
    public void setCmsPath(String cmsPath) {
        this.cmsPath = cmsPath;
    }



    public EcmFilesManagementCommand(String cmsPath, EcmFilesCommand command) {
        this.cmsPath = cmsPath;
        this.command = command;
    }

    public CommandInfo getInfo() {
        return info;
    }

    /**
     * {@inheritDoc}
     */
    public ControllerResponse execute() throws ControllerException {


        try {

            CMSServiceCtx cmsCtx = new CMSServiceCtx();
            cmsCtx.setControllerContext(getControllerContext());

            PortalControllerContext pcc = new PortalControllerContext(getControllerContext());


            getCMSService().getEcmFilesUrl(cmsCtx, cmsPath, command);

            if (command.equals(EcmFilesCommand.synchronizeFolder)) {
                String success = itlzService.getString(SUCCESS_MESSAGE_SYNCHRO, getControllerContext().getServerInvocation().getRequest().getLocale());
                notifService.addSimpleNotification(pcc, success, NotificationsType.SUCCESS);
            } else if (command.equals(EcmFilesCommand.unsynchronizeFolder)) {
                String success = itlzService.getString(SUCCESS_MESSAGE_UNSYNCHRO, getControllerContext().getServerInvocation().getRequest().getLocale());
                notifService.addSimpleNotification(pcc, success, NotificationsType.SUCCESS);
            }


            // relaod navigation tree
            PageProperties.getProperties().setRefreshingPage(true);


            CmsCommand redirect = new CmsCommand(null, cmsPath, null, null, null, null, null, null, null, null, null);
            ControllerResponse execute = context.execute(redirect);

            return execute;


        } catch (CMSException e) {

            throw new ControllerException(e);
        } catch (Exception e) {
            throw new ControllerException(e);
        }
    }


}
