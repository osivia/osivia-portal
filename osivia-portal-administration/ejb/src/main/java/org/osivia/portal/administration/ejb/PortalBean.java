package org.osivia.portal.administration.ejb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Observable;

import javax.faces.component.html.HtmlSelectOneListbox;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.apache.commons.lang.BooleanUtils;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.osivia.portal.administration.util.AdministrationConstants;
import org.osivia.portal.core.constants.InternalConstants;

/**
 * Portal bean.
 *
 * @author Cédric Krommenhoek
 * @see Observable
 * @see Serializable
 */
@Name("portalBean")
@Scope(ScopeType.SESSION)
public class PortalBean extends Observable implements Serializable {

    /** Default serial version ID. */
    private static final long serialVersionUID = 1L;

    /** Pages tree bean. */
    @In(value = "pagesTreeBean", create = true)
    private PagesTreeBean pagesTreeBean;

    /** Styles bean. */
    @In(value = "stylesBean", create = true)
    private StylesBean stylesBean;

    /** Layout bean. */
    @In(value = "layoutBean", create = true)
    private LayoutBean layoutBean;

    /** Theme bean. */
    @In(value = "themeBean", create = true)
    private ThemeBean themeBean;

    /** Profiles bean. */
    @In(value = "profilesBean", create = true)
    private ProfilesBean profilesBean;

    /** Portal object container. */
    private PortalObjectContainer portalObjectContainer;
    /** Portal identifier. */
    private String portalId;


    /**
     * Default constructor.
     */
    public PortalBean() {
        super();
    }


    /**
     * Init method.
     */
    @Create
    public void init() {
        PortletContext context = (PortletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        this.portalObjectContainer = (PortalObjectContainer) context.getAttribute(AdministrationConstants.PORTAL_OBJECT_CONTAINER_NAME);

        // Define administrator privilege
        PortletRequest request = (PortletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        Boolean isAdmin = (Boolean) request.getAttribute(InternalConstants.ADMINISTRATOR_INDICATOR_ATTRIBUTE_NAME);
        if (BooleanUtils.isTrue(isAdmin)) {
            PortletSession session = request.getPortletSession();
            session.setAttribute(AdministrationConstants.ADMIN_PRIVILEGES_ATTRIBUTE_NAME, true, PortletSession.APPLICATION_SCOPE);
        }

        // Add observers
        this.addObserver(this.pagesTreeBean);
        this.addObserver(this.stylesBean);
        this.addObserver(this.layoutBean);
        this.addObserver(this.themeBean);
        this.addObserver(this.profilesBean);
    }


    /**
     * Portal selection action.
     *
     * @param event value change event
     */
    public void selectPortal(ValueChangeEvent event) {
        HtmlSelectOneListbox component = (HtmlSelectOneListbox) event.getComponent();
        this.portalId = (String) component.getValue();

        // Save portal identifier in session
        PortletRequest request = (PortletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        request.getPortletSession().setAttribute(AdministrationConstants.PORTAL_ID_ATTRIBUTE_NAME, this.portalId, PortletSession.APPLICATION_SCOPE);

        // Notify observers
        this.setChanged();
        this.notifyObservers();
    }


    /**
     * Get portals list.
     *
     * @return portals list
     */
    public List<SelectItem> getPortals() {
        Collection<PortalObject> portalObjects = this.portalObjectContainer.getContext().getChildren();
        List<SelectItem> portals = new ArrayList<SelectItem>(portalObjects.size());
        for (PortalObject portalObject : portalObjects) {
            String id = portalObject.getId().toString(PortalObjectPath.SAFEST_FORMAT);
            String name = portalObject.getName();
            SelectItem item = new SelectItem(id, name);
            portals.add(item);
        }
        return portals;
    }


    /**
     * Getter for portalId.
     *
     * @return the portalId
     */
    public String getPortalId() {
        return this.portalId;
    }

    /**
     * Setter for portalId.
     *
     * @param portalId the portalId to set
     */
    public void setPortalId(String portalId) {
        this.portalId = portalId;
    }

}
