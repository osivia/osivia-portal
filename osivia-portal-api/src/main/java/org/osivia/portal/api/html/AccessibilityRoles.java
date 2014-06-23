package org.osivia.portal.api.html;

/**
 * Accessibility roles enumeration.
 *
 * @author Cédric Krommenhoek
 */
public enum AccessibilityRoles {

    /** Presentation role. */
    PRESENTATION(HTMLConstants.ROLE_PRESENTATION),
    /** Toolbar role. */
    TOOLBAR(HTMLConstants.ROLE_TOOLBAR),
    /** Menu role. */
    MENU(HTMLConstants.ROLE_MENU),
    /** Menu item role. */
    MENU_ITEM(HTMLConstants.ROLE_MENU_ITEM),
    /** Form role. */
    FORM(HTMLConstants.ROLE_FORM);


    /** Role value. */
    private final String value;


    /**
     * Constructor.
     *
     * @param value role value
     */
    private AccessibilityRoles(String value) {
        this.value = value;
    }


    /**
     * Getter for value.
     *
     * @return the value
     */
    public String getValue() {
        return this.value;
    }

}
