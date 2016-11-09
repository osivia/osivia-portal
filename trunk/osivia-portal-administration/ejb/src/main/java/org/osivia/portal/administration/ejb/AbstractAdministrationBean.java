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
package org.osivia.portal.administration.ejb;

import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;

import javax.faces.context.FacesContext;
import javax.portlet.PortletContext;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.seam.annotations.In;
import org.osivia.portal.administration.util.AdministrationConstants;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;


/**
 * Abstract administration bean super-class.
 *
 * @author Cédric Krommenhoek
 * @see Serializable
 * @see Observer
 */
public abstract class AbstractAdministrationBean implements Serializable, Observer {

    /** Default serial version ID. */
    private static final long serialVersionUID = 1L;

    /** Portal bean. */
    @In("portalBean")
    private PortalBean portalBean;

    /** Portlet context. */
    private PortletContext portletContext;
    /** Portal object container. */
    private PortalObjectContainer portalObjectContainer;
    /** Dynamic object container. */
    private IDynamicObjectContainer dynamicObjectContainer;
    /** Internationalization service. */
    private IInternationalizationService internationalizationService;

    /** Messages. */
    private String messages;
    /** Popup title. */
    private String popupTitle;


    /**
     * Init method.
     */
    public void init() {
        this.portletContext = (PortletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        this.portalObjectContainer = (PortalObjectContainer) this.portletContext.getAttribute(AdministrationConstants.PORTAL_OBJECT_CONTAINER_NAME);
        this.dynamicObjectContainer = (IDynamicObjectContainer) this.portletContext.getAttribute(AdministrationConstants.DYNAMIC_OBJECT_CONTAINER_NAME);
        this.internationalizationService = (IInternationalizationService) this.portletContext
                .getAttribute(AdministrationConstants.INTERNATIONALIZATION_SERVICE_NAME);
    }


    /**
     * {@inheritDoc}
     */
    public void update(Observable observable, Object arg) {
        // Do nothing
    }


    /**
     * Get portal object from his identifier.
     *
     * @return portal
     */
    protected Portal getPortal() {
        Portal portal = null;
        String portalId = this.portalBean.getPortalId();
        if (StringUtils.isNotBlank(portalId)) {
            portal = (Portal) this.portalObjectContainer.getObject(PortalObjectId.parse(portalId, PortalObjectPath.SAFEST_FORMAT));
        }
        return portal;
    }


    /**
     * Getter for portletContext.
     *
     * @return the portletContext
     */
    public PortletContext getPortletContext() {
        return this.portletContext;
    }

    /**
     * Getter for portalObjectContainer.
     *
     * @return the portalObjectContainer
     */
    public PortalObjectContainer getPortalObjectContainer() {
        return this.portalObjectContainer;
    }

    /**
     * Getter for dynamicObjectContainer.
     *
     * @return the dynamicObjectContainer
     */
    public IDynamicObjectContainer getDynamicObjectContainer() {
        return this.dynamicObjectContainer;
    }

    /**
     * Getter for internationalizationService.
     *
     * @return the internationalizationService
     */
    public IInternationalizationService getInternationalizationService() {
        return this.internationalizationService;
    }

    /**
     * Getter for messages.
     *
     * @return the messages
     */
    public String getMessages() {
        String displayMessages = this.messages;
        this.messages = null;
        return displayMessages;
    }

    /**
     * Setter for messages.
     *
     * @param messages the messages to set
     */
    public void setMessages(String messages) {
        this.messages = messages;
    }

    /**
     * Getter for popupTitle.
     *
     * @return the popupTitle
     */
    public String getPopupTitle() {
        return this.popupTitle;
    }

    /**
     * Setter for popupTitle.
     *
     * @param popupTitle the popupTitle to set
     */
    public void setPopupTitle(String popupTitle) {
        this.popupTitle = popupTitle;
    }

}
