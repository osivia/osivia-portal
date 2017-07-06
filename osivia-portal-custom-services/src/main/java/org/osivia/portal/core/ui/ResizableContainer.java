package org.osivia.portal.core.ui;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.portal.core.model.portal.PortalObjectId;

/**
 * jQuery UI resizable component container.
 * 
 * @author Cédric Krommenhoek
 */
public class ResizableContainer {

    /** Resizable width map. */
    private final Map<ResizableKey, Integer> map;


    /**
     * Constructor.
     */
    public ResizableContainer() {
        super();
        this.map = new ConcurrentHashMap<>();
    }


    /**
     * Get resizable width.
     * 
     * @param pageId page object identifier
     * @param taskId task identifier, may be null
     * @return resizable width
     */
    public Integer getWidth(PortalObjectId pageId, String taskId) {
        ResizableKey key = new ResizableKey(pageId, taskId);
        return this.map.get(key);
    }


    /**
     * Set resizable width.
     * 
     * @param pageId page object identifier
     * @param taskId task identifier
     * @param width resizable width
     */
    public void setWidth(PortalObjectId pageId, String taskId, Integer width) {
        ResizableKey key = new ResizableKey(pageId, taskId);
        this.map.put(key, width);
    }


    /**
     * Resizable width map key.
     * 
     * @author Cédric Krommenhoek
     */
    private class ResizableKey {

        /** Page object identifier. */
        private final PortalObjectId pageId;
        /** Task identifier, may be null. */
        private final String taskId;


        /**
         * Constructor.
         * 
         * @param pageId page object identifier
         * @param taskId task identifier, may be null
         */
        private ResizableKey(PortalObjectId pageId, String taskId) {
            super();
            this.pageId = pageId;
            this.taskId = taskId;
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + ((this.pageId == null) ? 0 : this.pageId.hashCode());
            result = (prime * result) + ((this.taskId == null) ? 0 : this.taskId.hashCode());
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
            ResizableKey other = (ResizableKey) obj;
            if (this.pageId == null) {
                if (other.pageId != null) {
                    return false;
                }
            } else if (!this.pageId.equals(other.pageId)) {
                return false;
            }
            if (this.taskId == null) {
                if (other.taskId != null) {
                    return false;
                }
            } else if (!this.taskId.equals(other.taskId)) {
                return false;
            }
            return true;
        }

    }

}
