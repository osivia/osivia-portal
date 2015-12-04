package org.osivia.portal.core.sequencing;

import org.jboss.portal.core.model.portal.PortalObjectId;
import org.osivia.portal.core.attributes.AttributesStorage;
import org.osivia.portal.core.attributes.StorageAttributeKey;
import org.osivia.portal.core.attributes.StorageScope;

/**
 * Portlet sequencing attribute key.
 *
 * @author Cédric Krommenhoek
 * @see StorageAttributeKey
 */
public class PortletSequencingAttributeKey implements StorageAttributeKey {

    /** Page identifier. */
    private final PortalObjectId pageId;
    /** Attribute name. */
    private final String name;


    /**
     * Constructor.
     */
    public PortletSequencingAttributeKey(PortalObjectId pageId, String name) {
        super();
        this.pageId = pageId;
        this.name = name;
    }


    /**
     * {@inheritDoc}
     */
    public AttributesStorage getStorage() {
        return AttributesStorage.PORTLET_SEQUENCING;
    }


    /**
     * {@inheritDoc}
     */
    public StorageScope getScope() {
        return StorageScope.PAGE;
    }


    /**
     * {@inheritDoc}
     */
    public PortalObjectId getPageId() {
        return this.pageId;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((this.name == null) ? 0 : this.name.hashCode());
        result = (prime * result) + ((this.pageId == null) ? 0 : this.pageId.hashCode());
        return result;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        PortletSequencingAttributeKey other = (PortletSequencingAttributeKey) obj;
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!this.name.equals(other.name)) {
            return false;
        }
        if (this.pageId == null) {
            if (other.pageId != null) {
                return false;
            }
        } else if (!this.pageId.equals(other.pageId)) {
            return false;
        }
        return true;
    }

}
