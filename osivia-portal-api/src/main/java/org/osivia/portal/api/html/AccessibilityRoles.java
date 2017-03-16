package org.osivia.portal.api.html;

/**
 * Accessibility roles enumeration.
 *
 * @author Cédric Krommenhoek
 */
public enum AccessibilityRoles {

    /** Dialog role. */
    DIALOG("dialog"),
    /** Document role. */
    DOCUMENT("document"),
    /** Form role. */
    FORM(HTMLConstants.ROLE_FORM),
    /** Menu role. */
    MENU(HTMLConstants.ROLE_MENU),
    /** Menu item role. */
    MENU_ITEM(HTMLConstants.ROLE_MENU_ITEM),
    /** Navigation role. */
    NAVIGATION(HTMLConstants.ROLE_NAVIGATION),
    /** Presentation role. */
    PRESENTATION(HTMLConstants.ROLE_PRESENTATION),
    /** Progress bar role. */
    PROGRESS_BAR("progressbar"),
    /** Toolbar role. */
    TOOLBAR(HTMLConstants.ROLE_TOOLBAR);


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
