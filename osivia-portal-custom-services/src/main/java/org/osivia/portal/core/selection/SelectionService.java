/*
 * (C) Copyright 2014 OSIVIA (http://www.osivia.com)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package org.osivia.portal.core.selection;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.selection.ISelectionService;
import org.osivia.portal.api.selection.SelectionItem;
import org.osivia.portal.core.attributes.AttributesStorageService;
import org.osivia.portal.core.attributes.StorageScope;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;

/**
 * Selection service implementation.
 *
 * @author Cédric Krommenhoek
 * @see AttributesStorageService
 * @see SelectionAttributeKey
 * @see SelectionAttributeValue
 * @see ISelectionService
 */
public class SelectionService extends AttributesStorageService<SelectionAttributeKey, SelectionAttributeValue> implements ISelectionService {

    /** Selection scope property prefix. */
    private static final String SCOPE_PREFIX = "osivia.selection.";
    /** Selection scope property suffix. */
    private static final String SCOPE_SUFFIX = ".scope";


    /** Logger. */
    private final Log logger;


    /**
     * Constructor.
     */
    public SelectionService() {
        super();
        this.logger = LogFactory.getLog(this.getClass());
    }


    /**
     * {@inheritDoc}
     */
    public boolean addItem(PortalControllerContext portalControllerContext, String id, SelectionItem item) {
        // Debug log
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("addItem");
        }

        // Key
        SelectionAttributeKey key = this.generateKey(portalControllerContext, id);

        // Selection items
        Set<SelectionItem> items = this.getSelectionItems(portalControllerContext, key);

        // Add
        boolean isAdded = items.add(item);
        if (isAdded) {
            this.notifyUpdate(portalControllerContext, key);
        }

        return isAdded;
    }


    /**
     * {@inheritDoc}
     */
    public boolean removeItem(PortalControllerContext portalControllerContext, String id, String itemId) {
        // Debug log
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("removeItem");
        }

        // Key
        SelectionAttributeKey key = this.generateKey(portalControllerContext, id);

        // Selection items
        Set<SelectionItem> items = this.getSelectionItems(portalControllerContext, key);

        // Remove
        SelectionItem item = new SelectionItem(itemId, null, null);
        boolean isRemoved = items.remove(item);
        if (isRemoved) {
            this.notifyUpdate(portalControllerContext, key);
        }

        return isRemoved;
    }


    /**
     * {@inheritDoc}
     */
    public Set<SelectionItem> getSelectionItems(PortalControllerContext portalControllerContext, String id) {
        // Debug log
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("getSelectionItemsSet");
        }

        // Key
        SelectionAttributeKey key = this.generateKey(portalControllerContext, id);

        return this.getSelectionItems(portalControllerContext, key);
    }


    /**
     * {@inheritDoc}
     */
    public void deleteSelection(PortalControllerContext portalControllerContext, String id) {
        // Debug log
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("deleteSelection");
        }

        // Key
        SelectionAttributeKey key = this.generateKey(portalControllerContext, id);

        // Selection items
        Set<SelectionItem> items = this.getSelectionItems(portalControllerContext, key);

        boolean empty = items.isEmpty();
        items.clear();
        if (!empty) {
            this.notifyUpdate(portalControllerContext, key);
        }
    }


    /**
     * Generate selection attribute key.
     *
     * @param portalControllerContext portal controller context
     * @param selectionId selection identifier
     * @return key
     */
    private SelectionAttributeKey generateKey(PortalControllerContext portalControllerContext, String selectionId) {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        // Page
        Page page = PortalObjectUtils.getPage(controllerContext);

        // Scope
        String scopeName = page.getDeclaredProperty(SCOPE_PREFIX + selectionId + SCOPE_SUFFIX);
        StorageScope scope = StorageScope.fromName(scopeName);

        // Page identifier
        PortalObjectId pageId;
        if (StorageScope.PAGE.equals(scope)) {
            pageId = page.getId();
        } else {
            pageId = null;
        }

        return new SelectionAttributeKey(selectionId, scope, pageId);
    }


    /**
     * Get selection items.
     *
     * @param portalControllerContext portal controller context
     * @param key selection attribute key
     * @return selection items
     */
    private Set<SelectionItem> getSelectionItems(PortalControllerContext portalControllerContext, SelectionAttributeKey key) {
        SelectionAttributeValue attribute = this.getStorageAttribute(portalControllerContext, key);
        if (attribute == null) {
            attribute = new SelectionAttributeValue();
            this.setStorageAttributes(portalControllerContext, key, attribute);

            if (this.logger.isDebugEnabled()) {
                this.logger.info("Selection items initialized (id = " + key.getId() + ").");
            }
        }
        return attribute.getItems();
    }

}
