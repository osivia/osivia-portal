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
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;


public abstract class AbstractAdministrationBean implements Serializable, Observer {

    /** Default serial version ID. */
    private static final long serialVersionUID = 1L;

    /** Portal bean. */
    @In("portalBean")
    protected PortalBean portalBean;

    /** Portlet context. */
    protected PortletContext portletContext;
    /** Portal object container. */
    protected PortalObjectContainer portalObjectContainer;
    /** Dynamic object container. */
    protected IDynamicObjectContainer dynamicObjectContainer;

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
