package org.osivia.portal.core.portlet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jboss.portal.core.model.portal.PortalObjectId;
import org.osivia.portal.api.portlet.PortletStatus;

/**
 * Portlet status container.
 *
 * @author Cédric Krommenhoek
 */
public class PortletStatusContainer implements Cloneable {

    /** Status map, grouped by page identifier and portlet name. */
    private final Map<PortletKey, PortletStatus> map;


    /**
     * Constructor.
     */
    public PortletStatusContainer() {
        super();
        this.map = new HashMap<PortletKey, PortletStatus>();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public PortletStatusContainer clone() {
        PortletStatusContainer clone = new PortletStatusContainer();

        for (Entry<PortletKey, PortletStatus> entry : this.map.entrySet()) {
            if (entry.getValue() != null) {
                clone.map.put(entry.getKey(), entry.getValue().clone());
            }
        }

        return clone;
    }


    /**
     * Get portlet status.
     *
     * @param pageId page identifier
     * @param portletName portlet name
     * @return portlet status
     */
    public PortletStatus getPortletStatus(PortalObjectId pageId, String portletName) {
        PortletKey key = new PortletKey(pageId, portletName);
        return this.map.get(key);
    }


    /**
     * Set portlet status
     *
     * @param pageId page identifier
     * @param portletName portlet name
     * @param status portlet status
     */
    public void setPortletStatus(PortalObjectId pageId, String portletName, PortletStatus status) {
        PortletKey key = new PortletKey(pageId, portletName);
        this.map.put(key, status);
    }


    /**
     * Reset task dependent portlet status.
     * 
     * @param pageId page identifier
     */
    public void resetTaskDependentPortletStatus(PortalObjectId pageId) {
        Set<Entry<PortletKey, PortletStatus>> clone = new HashSet<Map.Entry<PortletKey, PortletStatus>>(this.map.entrySet());
        for (Entry<PortletKey, PortletStatus> entry : clone) {
            if (pageId.equals(entry.getKey().pageId) && (entry.getValue().getTaskId() != null)) {
                this.map.remove(entry.getKey());
            }
        }
    }


    /**
     * Portlet status map key.
     *
     * @author Cédric Krommenhoek
     */
    private class PortletKey {

        /** Page identifier. */
        private final PortalObjectId pageId;
        /** Portlet name. */
        private final String portletName;


        /**
         * Constructor.
         *
         * @param pageId page identifier
         * @param portletName portlet name
         */
        private PortletKey(PortalObjectId pageId, String portletName) {
            super();
            this.pageId = pageId;
            this.portletName = portletName;
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + ((this.pageId == null) ? 0 : this.pageId.hashCode());
            result = (prime * result) + ((this.portletName == null) ? 0 : this.portletName.hashCode());
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
            PortletKey other = (PortletKey) obj;
            if (this.pageId == null) {
                if (other.pageId != null) {
                    return false;
                }
            } else if (!this.pageId.equals(other.pageId)) {
                return false;
            }
            if (this.portletName == null) {
                if (other.portletName != null) {
                    return false;
                }
            } else if (!this.portletName.equals(other.portletName)) {
                return false;
            }
            return true;
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("PortletKey [pageId=");
            builder.append(this.pageId);
            builder.append(", portletName=");
            builder.append(this.portletName);
            builder.append("]");
            return builder.toString();
        }

    }

}
