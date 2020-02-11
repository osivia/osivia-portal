package org.osivia.portal.api.ui.layout;

import java.util.List;

/**
 * Layout item interface.
 *
 * @author Cédric Krommenhoek
 */
public interface LayoutItem {

    /**
     * Get identifier.
     *
     * @return identifier
     */
    String getId();


    /**
     * Get label.
     *
     * @return label
     */
    String getLabel();


    /**
     * Set label.
     *
     * @param label label
     */
    void setLabel(String label);


    /**
     * Get icon.
     *
     * @return icon
     */
    String getIcon();


    /**
     * Set icon.
     *
     * @param icon icon
     */
    void setIcon(String icon);


    /**
     * Get profiles.
     *
     * @return profiles
     */
    List<String> getProfiles();

}
