package org.osivia.portal.core.theming;

import org.osivia.portal.api.theming.IAttributesBundle;
import org.osivia.portal.core.theming.attributesbundle.BreadcrumbAttributesBundle;
import org.osivia.portal.core.theming.attributesbundle.ToolbarAttributesBundle;
import org.osivia.portal.core.theming.attributesbundle.PageSettingsAttributesBundle;


public enum DefaultAttributesBundles {

    /** Breadcrumb attributes bundle. */
    BREADCRUMB(BreadcrumbAttributesBundle.getInstance()),
    /** Toolbar attributes bundle. */
    TOOLBAR(ToolbarAttributesBundle.getInstance()),
    /** Toolbar settings attributes bundle. */
    TOOLBAR_SETTINGS(PageSettingsAttributesBundle.getInstance());


    /** Attributes bundle. */
    private final IAttributesBundle bundle;


    /**
     * Constructor.
     *
     * @param bundle attributes bundle
     */
    private DefaultAttributesBundles(IAttributesBundle bundle) {
        this.bundle = bundle;
    }


    /**
     * Getter for bundle.
     *
     * @return the bundle
     */
    public IAttributesBundle getBundle() {
        return this.bundle;
    }

}
