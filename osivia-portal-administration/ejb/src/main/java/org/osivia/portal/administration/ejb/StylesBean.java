package org.osivia.portal.administration.ejb;

import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Styles bean.
 *
 * @author Cédric Krommenhoek
 * @see AbstractAdministrationBean
 */
@Name("stylesBean")
@Scope(ScopeType.PAGE)
public class StylesBean extends AbstractAdministrationBean {

    /** Default serial version ID. */
    private static final long serialVersionUID = 1L;

    /** Portal styles declared property name. */
    private static final String PORTAL_STYLES_DECLARED_PROPERTY = "osivia.liste_styles";
    /** String separator. */
    private static final String SEPARATOR = ",";

    /** Style. */
    private String style;
    /** Old style value for edition. */
    private String oldStyle;
    /** Selected styles. */
    private Set<String> selectedStyles;
    /** Styles list. */
    private List<String> styles;


    /**
     * {@inheritDoc}
     */
    @Create
    @Override
    public void init() {
        super.init();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void update(Observable observable, Object arg) {
        this.style = null;
        this.selectedStyles = null;
        this.refreshStyles();
    }


    /**
     * Add style action.
     */
    public void addStyle() {
        this.style = StringUtils.EMPTY;
        this.oldStyle = null;
        this.setPopupTitle("Ajouter style");
    }


    /**
     * Edit style action.
     */
    public void editStyle() {
        if (this.selectedStyles.isEmpty()) {
            this.addStyle();
        } else {
            this.style = this.selectedStyles.iterator().next();
            this.oldStyle = this.style;
            this.setPopupTitle("Editer style " + this.style);
        }
    }


    /**
     * Delete style action.
     */
    public void deleteStyle() {
        for (String selectedStyle : this.selectedStyles) {
            this.styles.remove(selectedStyle);
        }
    }


    /**
     * Add or edit popup submit action.
     */
    public void submit() {
        if (this.oldStyle == null) {
            // Add
            if (this.styles.contains(this.style)) {
                this.setMessages("Le style existe déjà.");
            } else {
                this.styles.add(this.style);
            }
        } else {
            // Edit
            this.styles.remove(this.oldStyle);
            this.styles.add(this.style);
        }
    }


    /**
     * Refresh styles action.
     */
    public void refreshStyles() {
        Portal portal = this.getPortal();
        String property = portal.getDeclaredProperty(PORTAL_STYLES_DECLARED_PROPERTY);
        if (property == null) {
            property = StringUtils.EMPTY;
        }
        String[] stringsArray = StringUtils.split(property, SEPARATOR);
        this.styles = Arrays.asList(stringsArray);
    }


    /**
     * Save styles action.
     */
    public void save() {
        Portal portal = this.getPortal();
        String[] stringsArray = this.styles.toArray(new String[this.styles.size()]);
        String property = StringUtils.join(stringsArray, SEPARATOR);
        portal.setDeclaredProperty(PORTAL_STYLES_DECLARED_PROPERTY, property);
        this.setMessages("Les styles ont été mis à jour.");
    }


    /**
     * Getter for style.
     *
     * @return the style
     */
    public String getStyle() {
        return this.style;
    }


    /**
     * Setter for style.
     *
     * @param style the style to set
     */
    public void setStyle(String style) {
        this.style = style;
    }


    /**
     * Getter for selectedStyles.
     *
     * @return the selectedStyles
     */
    public Set<String> getSelectedStyles() {
        return this.selectedStyles;
    }


    /**
     * Setter for selectedStyles.
     *
     * @param selectedStyles the selectedStyles to set
     */
    public void setSelectedStyles(Set<String> selectedStyles) {
        this.selectedStyles = selectedStyles;
    }


    /**
     * Getter for styles.
     *
     * @return the styles
     */
    public List<String> getStyles() {
        return this.styles;
    }


    /**
     * Setter for styles.
     *
     * @param styles the styles to set
     */
    public void setStyles(List<String> styles) {
        this.styles = styles;
    }

}
