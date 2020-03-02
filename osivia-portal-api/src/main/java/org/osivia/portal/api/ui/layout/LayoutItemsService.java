package org.osivia.portal.api.ui.layout;

import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;

import java.util.List;

/**
 * Layout items service interface.
 *
 * @author Cédric Krommenhoek
 */
public interface LayoutItemsService {

    /**
     * MBean name.
     */
    String MBEAN_NAME = "osivia:service=LayoutItemsService";

    /**
     * Linked layout item identifier window property.
     */
    String LINKED_ITEM_ID_WINDOW_PROPERTY = "osivia.layout-item-id";


    /**
     * Get layout items.
     *
     * @param portalControllerContext portal controller context
     * @return layout items
     */
    List<LayoutItem> getItems(PortalControllerContext portalControllerContext) throws PortalException;


    /**
     * Set layout items.
     *
     * @param portalControllerContext portal controller context
     * @param items                   layout items
     */
    void setItems(PortalControllerContext portalControllerContext, List<LayoutItem> items) throws PortalException;


    /**
     * Get current layout item.
     *
     * @param portalControllerContext portal controller context
     * @return layout item
     */
    LayoutItem getCurrentItem(PortalControllerContext portalControllerContext) throws PortalException;


    /**
     * Select layout item.
     *
     * @param portalControllerContext portal controller context
     * @param id                      layout item identifier
     */
    void selectItem(PortalControllerContext portalControllerContext, String id) throws PortalException;


    /**
     * Create layout item.
     *
     * @param portalControllerContext portal controller context
     * @param id                      layout item identifier
     * @return layout item
     */
    LayoutItem createItem(PortalControllerContext portalControllerContext, String id);

}
