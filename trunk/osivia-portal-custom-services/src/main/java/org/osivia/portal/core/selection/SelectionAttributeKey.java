package org.osivia.portal.core.selection;

import org.jboss.portal.core.model.portal.PortalObjectId;
import org.osivia.portal.core.attributes.AttributesStorage;
import org.osivia.portal.core.attributes.StorageAttributeKey;
import org.osivia.portal.core.attributes.StorageScope;

/**
 * Selection attribute key.
 *
 * @author Cédric Krommenhoek
 * @see StorageAttributeKey
 */
public class SelectionAttributeKey implements StorageAttributeKey {

    /** Selection identifier. */
    private final String id;
    /** Storage scope. */
    private final StorageScope scope;
    /** Page identifier. */
    private final PortalObjectId pageId;


    /**
     * Constructor.
     *
     * @param id selection identifier
     * @param scope storage scope
     * @param pageId page identifier
     */
    public SelectionAttributeKey(String id, StorageScope scope, PortalObjectId pageId) {
        super();
        this.id = id;
        this.scope = scope;
        this.pageId = pageId;
    }


    /**
     * {@inheritDoc}
     */
    public AttributesStorage getStorage() {
        return AttributesStorage.SELECTION;
    }


    /**
     * {@inheritDoc}
     */
    public StorageScope getScope() {
        return this.scope;
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
        result = (prime * result) + ((this.id == null) ? 0 : this.id.hashCode());
        result = (prime * result) + ((this.pageId == null) ? 0 : this.pageId.hashCode());
        result = (prime * result) + ((this.scope == null) ? 0 : this.scope.hashCode());
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
        SelectionAttributeKey other = (SelectionAttributeKey) obj;
        if (this.id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!this.id.equals(other.id)) {
            return false;
        }
        if (this.pageId == null) {
            if (other.pageId != null) {
                return false;
            }
        } else if (!this.pageId.equals(other.pageId)) {
            return false;
        }
        if (this.scope != other.scope) {
            return false;
        }
        return true;
    }


    /**
     * Getter for id.
     *
     * @return the id
     */
    public String getId() {
        return this.id;
    }

}
