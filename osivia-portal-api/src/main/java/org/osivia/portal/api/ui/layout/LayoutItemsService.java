package org.osivia.portal.api.ui.layout;

import org.jboss.portal.core.model.portal.Portal;
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
    String LINKED_ITEM_ID_WINDOW_PROPERTY = "osivia.layout.item.id";


    /**
     * Get layout groups.
     *
     * @param portalControllerContext portal controller context
     * @return layout groups
     */
    List<LayoutGroup> getGroups(PortalControllerContext portalControllerContext) throws PortalException;


    /**
     * Get layout group.
     *
     * @param portalControllerContext portal controller context
     * @param groupId                 layout group identifier
     * @return layout group
     */
    LayoutGroup getGroup(PortalControllerContext portalControllerContext, String groupId) throws PortalException;


    /**
     * Set layout group.
     *
     * @param portalControllerContext portal controller context
     * @param group                   layout group
     */
    void setGroup(PortalControllerContext portalControllerContext, LayoutGroup group) throws PortalException;


    /**
     * Get layout items.
     *
     * @param portalControllerContext portal controller context
     * @param groupId                 layout group identifier
     * @return layout items
     */
    List<LayoutItem> getItems(PortalControllerContext portalControllerContext, String groupId) throws PortalException;


    /**
     * Get current layout items.
     *
     * @param portalControllerContext portal controller context
     * @return layout items
     */
    List<LayoutItem> getCurrentItems(PortalControllerContext portalControllerContext) throws PortalException;


    /**
     * Get current layout item.
     *
     * @param portalControllerContext portal controller context
     * @return layout item
     */
    LayoutItem getCurrentItem(PortalControllerContext portalControllerContext, String groupId) throws PortalException;


    /**
     * Select layout item.
     *
     * @param portalControllerContext portal controller context
     * @param itemId                  layout item identifier
     */
    void selectItem(PortalControllerContext portalControllerContext, String itemId) throws PortalException;


    /**
     * Check if layout item is selected.
     *
     * @param portalControllerContext portal controller context
     * @param itemId                  layout item identifier
     * @return true if layout item is selected
     * @throws PortalException
     */
    boolean isSelected(PortalControllerContext portalControllerContext, String itemId) throws PortalException;

}
