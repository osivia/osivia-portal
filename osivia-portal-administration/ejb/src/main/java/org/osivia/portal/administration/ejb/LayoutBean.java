package org.osivia.portal.administration.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Observable;

import javax.faces.model.SelectItem;

import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.theme.LayoutService;
import org.jboss.portal.theme.PortalLayout;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.osivia.portal.administration.util.AdministrationConstants;

/**
 * Layout bean.
 *
 * @author Cédric Krommenhoek
 * @see AbstractAdministrationBean
 */
@Name("layoutBean")
@Scope(ScopeType.PAGE)
public class LayoutBean extends AbstractAdministrationBean {

    /** Default serial version ID. */
    private static final long serialVersionUID = 1L;

    /** Default layout name. */
    private static final String DEFAULT_LAYOUT_NAME = "default";

    /** Layout service. */
    private LayoutService layoutService;
    /** Portal selected layout. */
    private String layout;
    /** Portal layouts list. */
    private List<SelectItem> layouts;


    /**
     * Default constructor.
     */
    public LayoutBean() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Create
    @Override
    public void init() {
        super.init();
        this.layoutService = (LayoutService) this.getPortletContext().getAttribute(AdministrationConstants.LAYOUT_SERVICE_NAME);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void update(Observable observable, Object arg) {
        Portal portal = this.getPortal();
        this.layout = portal.getDeclaredProperty(ThemeConstants.PORTAL_PROP_LAYOUT);

        // Portal layouts list
        Collection<PortalLayout> portalLayouts = this.layoutService.getLayouts();
        this.layouts = new ArrayList<SelectItem>(portalLayouts.size() + 1);
        for (PortalLayout portalLayout : portalLayouts) {
            String name = portalLayout.getLayoutInfo().getName();
            this.layouts.add(new SelectItem(name));
        }
        this.layouts.add(new SelectItem(DEFAULT_LAYOUT_NAME));
    }


    /**
     * Update layout action.
     */
    public void updateLayout() {
        PortalObject portal = this.getPortal();
        portal.setDeclaredProperty(ThemeConstants.PORTAL_PROP_LAYOUT, this.layout);
        this.setMessages("Layout enregistré avec succès.");
    }


    /**
     * Getter for layout.
     *
     * @return the layout
     */
    public String getLayout() {
        return this.layout;
    }

    /**
     * Setter for layout.
     *
     * @param layout the layout to set
     */
    public void setLayout(String layout) {
        this.layout = layout;
    }

    /**
     * Getter for layouts.
     *
     * @return the layouts
     */
    public List<SelectItem> getLayouts() {
        return this.layouts;
    }

    /**
     * Setter for layouts.
     *
     * @param layouts the layouts to set
     */
    public void setLayouts(List<SelectItem> layouts) {
        this.layouts = layouts;
    }

}
