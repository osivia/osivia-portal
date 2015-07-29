/**
 *
 */
package org.osivia.portal.core.portalobjects;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.jboss.portal.common.i18n.LocalizedString;
import org.jboss.portal.common.i18n.LocalizedString.Value;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.osivia.portal.api.Constants;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.page.PageProperties;


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
     * Get current page portal object identifier.
     *
     * @param controllerContext controller context
     * @return current page portal object identifier
     */
    public static final PortalObjectId getPageId(ControllerContext controllerContext) {
        PortalObjectId pageId = null;

        if (controllerContext != null) {
            // Page identifier
            pageId = (PortalObjectId) controllerContext.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, Constants.ATTR_PAGE_ID);
        }

        return pageId;
    }


    /**
     * Get current page.
     *
     * @param controllerContext controller context
     * @return current page
     */
    public static final Page getPage(ControllerContext controllerContext) {
        Page page = null;

        if (controllerContext != null) {
            // Page identifier
            PortalObjectId pageId = getPageId(controllerContext);

            if (pageId != null) {
                // Portal object container
                PortalObjectContainer portalObjectContainer = controllerContext.getController().getPortalObjectContainer();

                page = portalObjectContainer.getObject(pageId, Page.class);
            }
        }

        return page;
    }


    /**
     * Get current portal from controller context.
     *
     * @param controllerContext controller context
     * @return current portal
     */
    public static final Portal getPortal(ControllerContext controllerContext) {
        Portal portal = null;

        // Portal name
        String portalName = PageProperties.getProperties().getPagePropertiesMap().get(Constants.PORTAL_NAME);
        if ((controllerContext != null) && (portalName != null)) {
            PortalObjectContainer portalObjectContainer = controllerContext.getController().getPortalObjectContainer();
            PortalObject portalObject = portalObjectContainer.getObject(PortalObjectId.parse(StringUtils.EMPTY, "/" + portalName,
                    PortalObjectPath.CANONICAL_FORMAT));
            if (portalObject instanceof Portal) {
                portal = (Portal) portalObject;
            }
        }

        return portal;
    }


    /**
     * Get current portal from any portal object.
     *
     * @param po portal object
     * @return current portal
     */
    public static final Portal getPortal(PortalObject po) {
        Portal portal = null;
        if (po instanceof Portal) {
            portal = (Portal) po;
        } else if (po instanceof Page) {
            Page page = (Page) po;
            portal = page.getPortal();
        } else if (po instanceof Window) {
            Window window = (Window) po;
            Page page = window.getPage();
            if (page != null) {
                portal = page.getPortal();
            }
        }
        return portal;
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
        Portal portal = getPortal(po);
        if (portal == null) {
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
        Portal portal = getPortal(po);
        if (portal == null) {
            return false;
        }

        String portalType = portal.getProperty(InternalConstants.PORTAL_PROP_NAME_PORTAL_TYPE);
        return StringUtils.equals(InternalConstants.PORTAL_TYPE_SPACE, portalType);
    }


    /**
     * Check if a portal object is contained in JBoss Portal administration.
     *
     * @param po portal object to check, may be null
     * @return true if portal object is contained in JBoss Portal administration
     */
    public static boolean isJBossPortalAdministration(PortalObject po) {
        if (po == null) {
            return false;
        }

        // Get current portal
        Portal portal = getPortal(po);
        if (portal == null) {
            return false;
        }

        return StringUtils.equalsIgnoreCase(InternalConstants.JBOSS_ADMINISTRATION_PORTAL_NAME, portal.getName());
    }


    /**
     * Return HTML safe portal object identifier.
     *
     * @param id portal object identifier
     * @return HTML safe identifier
     */
    public static String getHTMLSafeId(PortalObjectId id) {
        if (id == null) {
            return null;
        }

        String safestFormat = id.toString(PortalObjectPath.SAFEST_FORMAT);
        try {
            return URLEncoder.encode(safestFormat, CharEncoding.UTF_8);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }


    /**
     * Check if a portal object is the portal default page.
     *
     * @param po portal object to check
     * @return true if portal object is the portal default page
     */
    public static boolean isPortalDefaultPage(PortalObject po) {
        if (po == null) {
            return false;
        }

        if (po instanceof Page) {
            Page page = (Page) po;
            Portal portal = page.getPortal();
            PortalObject parent = page.getParent();

            if (portal.equals(parent)) {
                String defaultPageName = portal.getDeclaredProperty(PortalObject.PORTAL_PROP_DEFAULT_OBJECT_NAME);
                return StringUtils.equals(defaultPageName, page.getName());
            }
        }

        return false;
    }

}
