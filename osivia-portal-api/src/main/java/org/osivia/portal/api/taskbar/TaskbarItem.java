package org.osivia.portal.api.taskbar;

import org.osivia.portal.api.panels.PanelPlayer;

/**
 * Taskbar item.
 *
 * @author Cédric Krommenhoek
 */
public interface TaskbarItem {

    /**
     * Get identifier.
     *
     * @return identifier
     */
    String getId();


    /**
     * Get type.
     *
     * @return type
     */
    TaskbarItemType getType();


    /**
     * Get internationalization key.
     *
     * @return internationalization key
     */
    String getKey();


    /**
     * Get customized class loader.
     * 
     * @return class loader
     */
    ClassLoader getCustomizedClassLoader();


    /**
     * Get icon.
     *
     * @return icon
     */
    String getIcon();


    /**
     * Get player.
     *
     * @return player
     */
    PanelPlayer getPlayer();


    /**
     * Get document type.
     *
     * @return document type
     */
    String getDocumentType();

}
