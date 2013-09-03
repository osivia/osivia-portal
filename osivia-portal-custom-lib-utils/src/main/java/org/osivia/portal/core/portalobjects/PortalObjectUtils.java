/**
 *
 */
package org.osivia.portal.core.portalobjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.common.i18n.LocalizedString;
import org.jboss.portal.common.i18n.LocalizedString.Value;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.osivia.portal.core.constants.InternalConstants;


/**
 * Utility class with null-safe methods for portal objects.
 *
 * @author Cédric Krommenhoek
 * @see PortalObject
 */
public class PortalObjectUtils {

    /**
     * Default constructor.
     * PortalObjectUtils instances should NOT be constructed in standard programming.
     * This constructor is public to permit tools that require a JavaBean instance to operate.
     */
    public PortalObjectUtils() {
        super();
    }


    /**
     * Check if object "po1" is an ancestor of object "po2".
     *
     * @param po1 first object, perhaps ancestor of the second, may be null
     * @param po2 second object, perhaps child of the first, may be null
     * @return true if the first objet is an ancestor of the second (they are both not null)
     */
    public static final boolean isAncestor(PortalObject po1, PortalObject po2) {
        if ((po1 == null) || (po2 == null)) {
            return false;
        }

        PortalObject parent = po2.getParent();
        if (parent == null) {
            return false;
        } else if (parent.equals(po1)) {
            return true;
        } else {
            return isAncestor(po1, parent);
        }
    }


    /**
     * Return the display name of object "po", in the most accurate locale, otherwise the technical name.
     *
     * @param po portal object, may be null
     * @param locales locales, may be null
     * @return the display name
     */
    public static final String getDisplayName(PortalObject po, Locale[] locales) {
        if (po == null) {
            return null;
        }
        if (locales == null) {
            return po.getName();
        }

        // Get display names
        LocalizedString localizedString = po.getDisplayName();
        if (localizedString == null) {
            return po.getName();
        }
        Map<Locale, Value> displayNames = localizedString.getValues();
        if (displayNames == null) {
            return po.getName();
        }

        // Search the most accurate display name for each locale
        for (Locale locale : locales) {
            // Exact locale match
            if (displayNames.containsKey(locale)) {
                Value displayName = displayNames.get(locale);
                return displayName.getString();
            }
            // Language match
            Locale languageLocale = new Locale(locale.getLanguage());
            if (displayNames.containsKey(languageLocale)) {
                Value displayName = displayNames.get(languageLocale);
                return displayName.getString();
            }
            // Other country match
            for (Locale displayNameLocale : displayNames.keySet()) {
                if (locale.getLanguage().equals(displayNameLocale.getLanguage())) {
                    Value displayName = displayNames.get(displayNameLocale);
                    return displayName.getString();
                }
            }
        }

        return po.getName();
    }


    /**
     * Return the display name of object "po", in the most accurate locale, otherwise the technical name.
     *
     * @param po portal object, may be null
     * @param locales locales, may be null
     * @return the display name
     */
    public static final String getDisplayName(PortalObject po, Enumeration<Locale> locales) {
        if (po == null) {
            return null;
        }
        if (locales == null) {
            return po.getName();
        }

        // Convert Enumeration into array
        ArrayList<Locale> collection = Collections.list(locales);
        Locale[] array = collection.toArray(new Locale[collection.size()]);

        return getDisplayName(po, array);
    }


    /**
     * Return the display name of object "po", in the specified locale, otherwise the technical name.
     *
     * @param po portal object, may be null
     * @param locale locale, may be null
     * @return the display name
     */
    public static final String getDisplayName(PortalObject po, Locale locale) {
        Locale[] locales = null;
        if (locale != null) {
            locales = new Locale[]{locale};
        }

        return getDisplayName(po, locales);
    }


    /**
     * Check if portal object is a template.
     *
     * @param po portal object to check, may be null
     * @return true if portal object is a template
     */
    public static final boolean isTemplate(PortalObject po) {
        if (po == null) {
            return false;
        }

        PortalObject parent = po.getParent();
        if (parent == null) {
            return false;
        } else if (parent instanceof Portal) {
            return InternalConstants.TEMPLATES_PATH_NAME.equals(po.getName());
        } else {
            return isTemplate(parent);
        }
    }


    /**
     * Access to the templates root of the current portal objects tree.
     * If the templates root does not exist, it will be created.
     *
     * @param po a portal object of the current tree
     * @return templates root
     */
    public static final PortalObject getTemplatesRoot(PortalObject po) {
        if (po == null) {
            return null;
        }

        // Get current portal
        Portal portal;
        if (po instanceof Portal) {
            portal = (Portal) po;
        } else if (po instanceof Page) {
            Page page = (Page) po;
            portal = page.getPortal();
        } else {
            return null;
        }

        // Access templates root
        PortalObject templatesRoot = portal.getChild(InternalConstants.TEMPLATES_PATH_NAME);
        if (templatesRoot == null) {
            // Create templates root if it does not exist
            try {
                portal.createPage(InternalConstants.TEMPLATES_PATH_NAME);
                templatesRoot = portal.getChild(InternalConstants.TEMPLATES_PATH_NAME);
            } catch (Exception e) {
                return null;
            }
        }

        return templatesRoot;
    }


    /**
     * Check if a portal object belongs to a portal type "space".
     *
     * @param po portal object to check, may be null
     * @return true if portal object belongs to a space site
     */
    public static final boolean isSpaceSite(PortalObject po) {
        if (po == null) {
            return false;
        }

        // Get current portal
        Portal portal;
        if (po instanceof Portal) {
            portal = (Portal) po;
        } else if (po instanceof Page) {
            Page page = (Page) po;
            portal = page.getPortal();
        } else {
            return false;
        }

        String portalType = portal.getProperty(InternalConstants.PORTAL_PROP_NAME_PORTAL_TYPE);
        return StringUtils.equals(InternalConstants.PORTAL_TYPE_SPACE, portalType);
    }

}
