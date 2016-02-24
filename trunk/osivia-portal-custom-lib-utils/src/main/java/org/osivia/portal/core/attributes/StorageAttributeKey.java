package org.osivia.portal.core.attributes;

import org.jboss.portal.core.model.portal.PortalObjectId;


/**
 * Storage attribute key interface.
 *
 * @author Cédric Krommenhoek
 */
public interface StorageAttributeKey {

    /**
     * Get storage.
     *
     * @return storage
     */
    AttributesStorage getStorage();


    /**
     * Get scope.
     *
     * @return scope
     */
    StorageScope getScope();


    /**
     * Get page identifier.
     *
     * @return page identifier
     */
    PortalObjectId getPageId();

}
