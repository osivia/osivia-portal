package org.osivia.portal.api.theming;

import java.util.Map;

import org.osivia.portal.api.cms.EcmDocument;
import org.osivia.portal.api.context.PortalControllerContext;

/**
 * Tab group.
 *
 * @author Cédric Krommenhoek
 */
public interface TabGroup {

    /** Tab group name property name. */
    String NAME_PROPERTY = "osivia.tab.group";
    /** Tab group maintains visible indicator property name. */
    String MAINTAINS_PROPERTY = "osivia.tab.maintains";
    /** Tab group type property name. */
    String TYPE_PROPERTY = "osivia.tab.type";


    /**
     * Get tab group name.
     *
     * @return name
     */
    String getName();


    /**
     * Get tab group icon.
     *
     * @return icon
     */
    String getIcon();


    /**
     * Get label internationalization key.
     *
     * @return internationalization key
     */
    String getLabelKey();


    /**
     * Check if this group contains this document.
     *
     * @param portalControllerContext portal controller context
     * @param document document
     * @param type tab type
     * @param pageProperties page properties
     * @return true if this group contains this document, false otherwise
     */
    boolean contains(PortalControllerContext portalControllerContext, EcmDocument document, String type, Map<String, String> pageProperties);


    /**
     * Check if this group maintains visible this document.
     *
     * @param portalControllerContext portal controller context
     * @param document document
     * @param type tab type
     * @param pageProperties page properties
     * @return true if this group maintains visible this document, false otherwise
     */
    boolean maintains(PortalControllerContext portalControllerContext, EcmDocument document, String type, Map<String, String> pageProperties);

}
