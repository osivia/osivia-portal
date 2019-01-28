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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.portal.api.PortalURL;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.core.model.portal.command.view.ViewPageCommand;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateContext;
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
import org.osivia.portal.core.cms.Satellite;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.internationalization.InternationalizationUtils;
import org.osivia.portal.core.notifications.NotificationsUtils;
import org.osivia.portal.core.portalobjects.DynamicPersistentPage;

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

		
		
		
		// En cas de perte de session, les pages statiques CMS ne sont pas correctement rafraichies
		// (liées à la perte des paramètres de la page)
		// On reprovoque un init-state
		
		if( ( page instanceof DynamicPersistentPage) && (page.getDeclaredProperty("osivia.cms.basePath") != null))    {
            NavigationalStateContext stateContext = (NavigationalStateContext) getControllerContext().getAttributeResolver(
                    ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
            // Current page state
            PageNavigationalState pageState = stateContext.getPageNavigationalState(page.getId().toString());

            String[] sPath = null;
            if (pageState != null) {
                sPath = pageState.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.path"));
            }

            if (sPath == null) {
                ViewPageCommand pageCmd = new ViewPageCommand(page.getId());
                PortalURL url = new PortalURLImpl(pageCmd, getControllerContext(), null, null);
                String redirectUrl = url.toString() + "?init-state=true";
                return new RedirectionResponse(redirectUrl);
            }
		}


        // Satellites
        Set<Satellite> satellites;
        try {
            satellites = cmsService.getSatellites();
        } catch (CMSException e) {
            satellites = null;
        }
        List<Satellite> allSatellites;
        if (CollectionUtils.isEmpty(satellites)) {
            allSatellites = new ArrayList<>(1);
        } else {
            allSatellites = new ArrayList<>(satellites);
        }
        allSatellites.add(0, Satellite.MAIN);

        // Reload CMS session
        for (Satellite satellite : allSatellites) {
            cmsContext.setSatellite(satellite);

            try {
                cmsService.reloadSession(cmsContext);
            } catch (CMSException e) {
                throw new ControllerException(e);
            }
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
