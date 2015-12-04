package org.osivia.portal.core.attributes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;

/**
 * Attributes storage service implentation abstract super-class.
 *
 * @author Cédric Krommenhoek
 * @param <K> attribute key class
 * @param <V> attribute value class
 */
public abstract class AttributesStorageService<K extends StorageAttributeKey, V extends StorageAttributeValue> {

    /**
     * Constructor.
     */
    protected AttributesStorageService() {
        super();
    }


    /**
     * Get storage attribute.
     *
     * @param portalControllerContext portal controller context
     * @param key attribute key
     * @return attribute value
     */
    protected V getStorageAttribute(PortalControllerContext portalControllerContext, K key) {
        // Storage
        Map<K, V> storage = this.getStorage(portalControllerContext, key);

        return storage.get(key);
    }


    /**
     * Set storage attribute.
     *
     * @param portalControllerContext portal controller context
     * @param key attribute key
     * @param value attribute value
     */
    protected void setStorageAttributes(PortalControllerContext portalControllerContext, K key, V value) {
        // Storage
        Map<K, V> storage = this.getStorage(portalControllerContext, key);

        storage.put(key, value);

        this.notifyUpdate(portalControllerContext, key);
    }


    /**
     * Notify update.
     *
     * @param portalControllerContext portal controller context
     * @param key attribute key
     */
    protected void notifyUpdate(PortalControllerContext portalControllerContext, K key) {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        controllerContext.setAttribute(Scope.PRINCIPAL_SCOPE, key.getStorage().getTimestampAttributeName(), System.currentTimeMillis());
    }


    /**
     * Get page identifier.
     *
     * @param portalControllerContext portal controller context
     * @return page identifier
     */
    protected PortalObjectId getPageId(PortalControllerContext portalControllerContext) {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        return PortalObjectUtils.getPageId(controllerContext);
    }


    /**
     * Get attributes storage.
     *
     * @param portalControllerContext portal controller context
     * @param key attribute key
     * @return attributes
     */
    @SuppressWarnings("unchecked")
    private Map<K, V> getStorage(PortalControllerContext portalControllerContext, K key) {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        // Attribute name
        String attribute = key.getStorage().getAttributeName();

        // Storage
        Map<K, V> storage = (Map<K, V>) controllerContext.getAttribute(Scope.PRINCIPAL_SCOPE, attribute);
        if (storage == null) {
            storage = new ConcurrentHashMap<K, V>();
            controllerContext.setAttribute(Scope.PRINCIPAL_SCOPE, attribute, storage);
        }

        return storage;
    }

}
