package org.osivia.portal.core.theming;

import org.osivia.portal.api.theming.IAttributesBundle;
import org.osivia.portal.core.theming.attributesbundle.BreadcrumbAttributesBundle;
import org.osivia.portal.core.theming.attributesbundle.PageSettingsAttributesBundle;
import org.osivia.portal.core.theming.attributesbundle.SearchAttributesBundle;
import org.osivia.portal.core.theming.attributesbundle.SiteMapAttributesBundle;
import org.osivia.portal.core.theming.attributesbundle.TabsAttributesBundle;
import org.osivia.portal.core.theming.attributesbundle.ToolbarAttributesBundle;
import org.osivia.portal.core.theming.attributesbundle.TransversalAttributesBundle;

/**
 * Default attributes bundles enumeration.
 *
 * @author Cédric Krommenhoek
 */
public enum DefaultAttributesBundles {

    /** Breadcrumb attributes bundle. */
    BREADCRUMB(BreadcrumbAttributesBundle.getInstance()),
    /** Page settings attributes bundle. */
    PAGE_SETTINGS(PageSettingsAttributesBundle.getInstance()),
    /** Search attributes bundle. */
    SEARCH(SearchAttributesBundle.getInstance()),
    /** Site map attributes bundle. */
    SITE_MAP(SiteMapAttributesBundle.getInstance()),
    /** Tabs attributes bundle. */
    TABS(TabsAttributesBundle.getInstance()),
    /** Toolbar attributes bundle. */
    TOOLBAR(ToolbarAttributesBundle.getInstance()),
    /** Transversal attributes bundle. */
    TRANSVERSAL(TransversalAttributesBundle.getInstance());


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
