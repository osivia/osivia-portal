package org.osivia.portal.core.sequencing;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.portal.core.model.portal.PortalObjectId;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.sequencing.IPortletSequencingService;
import org.osivia.portal.core.attributes.AttributesStorage;
import org.osivia.portal.core.attributes.AttributesStorageService;

/**
 * Portlet sequencing service implementation.
 *
 * @author Cédric Krommenhoek
 * @see AttributesStorageService
 * @see PortletSequencingAttributeKey
 * @see PortletSequencingAttributeValue
 * @see IPortletSequencingService
 */
public class PortletSequencingService extends AttributesStorageService<PortletSequencingAttributeKey, PortletSequencingAttributeValue> implements
        IPortletSequencingService {

    /**
     * Constructor.
     */
    public PortletSequencingService() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public Object getAttribute(PortalControllerContext portalControllerContext, String name) {
        // Page identifier
        PortalObjectId pageId = this.getPageId(portalControllerContext);

        // Key
        PortletSequencingAttributeKey key = new PortletSequencingAttributeKey(pageId, name);

        // Value
        PortletSequencingAttributeValue value = this.getStorageAttribute(portalControllerContext, key);

        return value.getAttribute();
    }


    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getAttributes(PortalControllerContext portalControllerContext) {
        Map<PortletSequencingAttributeKey, PortletSequencingAttributeValue> storage = this.getStorage(portalControllerContext,
                AttributesStorage.PORTLET_SEQUENCING);

        Map<String, Object> result = new HashMap<String, Object>(storage.size());
        for (Entry<PortletSequencingAttributeKey, PortletSequencingAttributeValue> entry : storage.entrySet()) {
            String name = entry.getKey().getName();
            Object attribute = entry.getValue().getAttribute();
            result.put(name, attribute);
        }

        return result;
    }


    /**
     * {@inheritDoc}
     */
    public void setAttribute(PortalControllerContext portalControllerContext, String name, Object attribute) {
        // Page identifier
        PortalObjectId pageId = this.getPageId(portalControllerContext);

        // Key
        PortletSequencingAttributeKey key = new PortletSequencingAttributeKey(pageId, name);

        // Value
        PortletSequencingAttributeValue value = new PortletSequencingAttributeValue(attribute);

        this.setStorageAttributes(portalControllerContext, key, value);
    }

}
