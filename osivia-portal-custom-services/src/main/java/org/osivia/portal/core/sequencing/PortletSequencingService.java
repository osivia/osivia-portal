package org.osivia.portal.core.sequencing;

import org.jboss.portal.core.model.portal.PortalObjectId;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.sequencing.IPortletSequencingService;
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
