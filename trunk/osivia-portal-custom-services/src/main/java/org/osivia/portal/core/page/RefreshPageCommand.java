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
package org.osivia.portal.core.page;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.internationalization.InternationalizationUtils;
import org.osivia.portal.core.notifications.NotificationsUtils;

/**
 * Refresh page command.
 *
 * @see ControllerCommand
 */
public class RefreshPageCommand extends ControllerCommand {

    /** ECM action return. */
    private String ecmActionReturn;
    /** New document identifier. */
    private String newDocId;

    /** Page identifier. */
    private final String pageId;

    /** Command info. */
    private final CommandInfo info;

    /** Portal URL factory. */
    private final IPortalUrlFactory urlFactory;
    /** Notifications service. */
    private final INotificationsService notifService;
    /** Internationalization service. */
    private final IInternationalizationService itlzService;
    /** CMS service locator. */
    private final ICMSServiceLocator cmsServiceLocator;


    /**
     * Constructor.
     */
    public RefreshPageCommand() {
        this(null);
    }


    /**
     * Constructor.
     *
     * @param pageId page identifier
     */
    public RefreshPageCommand(String pageId) {
        super();
        this.pageId = pageId;
        this.info = new ActionCommandInfo(false);

        // Portal URL factory
        this.urlFactory = Locator.findMBean(IPortalUrlFactory.class, "osivia:service=UrlFactory");
        // Notifications service
        this.notifService = NotificationsUtils.getNotificationsService();
        // Internationalization service
        this.itlzService = InternationalizationUtils.getInternationalizationService();
        // CMS service locator
        this.cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, ICMSServiceLocator.MBEAN_NAME);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public CommandInfo getInfo() {
        return this.info;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerResponse execute() throws ControllerException {
        // Controller context
        ControllerContext controllerContext = this.getControllerContext();
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(controllerContext);

        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();
        // CMS context
        CMSServiceCtx cmsContext = new CMSServiceCtx();
        cmsContext.setControllerContext(controllerContext);

        // HTTP servlet request
        HttpServletRequest servletRequest = controllerContext.getServerInvocation().getServerContext().getClientRequest();
        // Locale
        Locale locale = servletRequest.getLocale();

        // Page identifier
        PortalObjectId poid = PortalObjectId.parse(this.pageId, PortalObjectPath.SAFEST_FORMAT);
        // Page
        PortalObject page = controllerContext.getController().getPortalObjectContainer().getObject(poid);


        // Set page refreshing indicator
        PageProperties.getProperties().setRefreshingPage(true);


        String ecmActionReturn = this.getEcmActionReturn();
        if (StringUtils.isNotBlank(ecmActionReturn)) {
            String newDocLiveUrl = null;
            if (this.getNewDocId() != null) {
                newDocLiveUrl = this.urlFactory.getCMSUrl(portalControllerContext, null, this.newDocId, null, null, InternalConstants.FANCYBOX_LIVE_CALLBACK,
                        null, null, null, null);
            }

            // Notification
            String notification = this.itlzService.getString(ecmActionReturn, locale, newDocLiveUrl);
            this.notifService.addSimpleNotification(portalControllerContext, notification, NotificationsType.SUCCESS);
        }


        // Reload CMS session
        try {
            cmsService.reloadSession(cmsContext);
        } catch (CMSException e) {
            throw new ControllerException(e);
        }

        return new UpdatePageResponse(page.getId());
    }


    /**
     * Getter for ecmActionReturn.
     *
     * @return the ecmActionReturn
     */
    public String getEcmActionReturn() {
        return this.ecmActionReturn;
    }

    /**
     * Setter for ecmActionReturn.
     *
     * @param ecmActionReturn the ecmActionReturn to set
     */
    public void setEcmActionReturn(String ecmActionReturn) {
        this.ecmActionReturn = ecmActionReturn;
    }

    /**
     * Getter for newDocId.
     *
     * @return the newDocId
     */
    public String getNewDocId() {
        return this.newDocId;
    }

    /**
     * Setter for newDocId.
     *
     * @param newDocId the newDocId to set
     */
    public void setNewDocId(String newDocId) {
        this.newDocId = newDocId;
    }

    /**
     * Getter for pageId.
     *
     * @return the pageId
     */
    public String getPageId() {
        return this.pageId;
    }

}
