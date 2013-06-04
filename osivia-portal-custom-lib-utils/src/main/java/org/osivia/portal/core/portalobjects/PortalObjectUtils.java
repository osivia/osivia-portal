/**
 * 
 */
package org.osivia.portal.core.portalobjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import org.jboss.portal.common.i18n.LocalizedString;
import org.jboss.portal.common.i18n.LocalizedString.Value;
import org.jboss.portal.core.model.portal.PortalObject;


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
    public static boolean isAncestor(PortalObject po1, PortalObject po2) {
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
    public static String getDisplayName(PortalObject po, Locale[] locales) {
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
    public static String getDisplayName(PortalObject po, Enumeration<Locale> locales) {
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

}
