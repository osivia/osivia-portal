/*
 * (C) Copyright 2015 OSIVIA (http://www.osivia.com) 
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

import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.CmsCommand;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.internationalization.InternationalizationUtils;
import org.osivia.portal.core.notifications.NotificationsUtils;
import org.osivia.portal.core.page.PageProperties;

/**
 * Actions for subscribe to all notifications on a document
 * 
 * @author lbillon
 * 
 */
public class SubscriptionCommand extends ControllerCommand {

	private static ICMSServiceLocator cmsServiceLocator;

	public static ICMSService getCMSService() throws Exception {

		if (cmsServiceLocator == null) {
			cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
		}

		return cmsServiceLocator.getCMSService();

	}

	private static INotificationsService notifService = NotificationsUtils.getNotificationsService();
	private static IInternationalizationService itlzService = InternationalizationUtils.getInternationalizationService();

	/** cms path used by the ECM */
	private String cmsPath;

	/** for unsubscribe, default is "subscribe" */
	private boolean unsubscribe;

	public SubscriptionCommand(String cmsPath, boolean unsubscribe) {
		super();
		this.cmsPath = cmsPath;
		this.unsubscribe = unsubscribe;
	}

	@Override
	public CommandInfo getInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ControllerResponse execute() throws ControllerException {
		CMSServiceCtx cmsCtx = new CMSServiceCtx();
		cmsCtx.setControllerContext(getControllerContext());

		PortalControllerContext pcc = new PortalControllerContext(getControllerContext());
		
		try {
			getCMSService().subscribe(cmsCtx, cmsPath, unsubscribe);
		} catch (CMSException e) {
			throw new ControllerException(e);
		} catch (Exception e) {
			throw new ControllerException(e);
		}

		if (unsubscribe) {
            String success = itlzService.getString("SUCCESS_MESSAGE_UNSUBSCRIBE", getControllerContext().getServerInvocation().getRequest().getLocale());
            notifService.addSimpleNotification(pcc, success, NotificationsType.SUCCESS);
        } else {
            String success = itlzService.getString("SUCCESS_MESSAGE_SUBSCRIBE", getControllerContext().getServerInvocation().getRequest().getLocale());
            notifService.addSimpleNotification(pcc, success, NotificationsType.SUCCESS);
        }


        // relaod navigation tree
        PageProperties.getProperties().setRefreshingPage(true);


        CmsCommand redirect = new CmsCommand(null, cmsPath, null, null, null, null, null, null, null, null, null);
        ControllerResponse execute = context.execute(redirect);

        return execute;
	}

	/**
	 * @return the cmsPath
	 */
	public String getCmsPath() {
		return cmsPath;
	}

	/**
	 * @param cmsPath
	 *            the cmsPath to set
	 */
	public void setCmsPath(String cmsPath) {
		this.cmsPath = cmsPath;
	}

	/**
	 * @return the unsubscribe
	 */
	public boolean isUnsubscribe() {
		return unsubscribe;
	}

	/**
	 * @param unsubscribe
	 *            the unsubscribe to set
	 */
	public void setUnsubscribe(boolean unsubscribe) {
		this.unsubscribe = unsubscribe;
	}

}
