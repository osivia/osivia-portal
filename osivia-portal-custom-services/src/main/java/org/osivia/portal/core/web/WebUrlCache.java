package org.osivia.portal.core.web;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osivia.portal.api.cache.services.ICacheService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cms.DocumentsMetadata;

/**
 * Web URL cache.
 *
 * @author Cédric Krommenhoek
 */
public class WebUrlCache {

    /** Cache. */
    private final Map<Key, Value> cache;
    /** Cache validity (in milliseconds). */
    private final long validity;
    /** Cache service. */
    private final ICacheService cacheService;


    /**
     * Constructor.
     *
     * @param validity cache validity (in milliseconds)
     */
    public WebUrlCache(long validity) {
        super();
        this.cache = new ConcurrentHashMap<Key, Value>();
        this.validity = validity;

        // Cache service
        this.cacheService = Locator.findMBean(ICacheService.class, ICacheService.MBEAN_NAME);
    }


    /**
     * Get documents metadata.
     *
     * @param basePath CMS base path
     * @param live live version indicator
     * @return documents metadata
     */
    public DocumentsMetadata getMetadata(String basePath, boolean live) {
        Key key = new Key(basePath, live);
        Value value = this.cache.get(key);

        DocumentsMetadata metadata;
        if (value == null) {
            // Unknown value
            metadata = null;
        } else {
            // Cached value
            boolean expired = (value.timestamp + this.validity) < System.currentTimeMillis();
            boolean reinitialized = !this.cacheService.checkIfPortalParametersReloaded(value.timestamp);

            if (expired || reinitialized) {
                // Expired or reinitilized value
                this.cache.remove(key);
                metadata = null;
            } else {
                // Up to date value
                metadata = value.metadata;
            }
        }

        return metadata;
    }


    /**
     * Set documents metadata.
     *
     * @param basePath CMS base path
     * @param live live version indicator
     * @param metadata documents metadata
     */
    public void setMetadata(String basePath, boolean live, DocumentsMetadata metadata) {
        Key key = new Key(basePath, live);
        Value value = new Value(metadata, System.currentTimeMillis());
        this.cache.put(key, value);
    }


    /**
     * Cache key inner-class.
     *
     * @author Cédric Krommenhoek
     */
    private class Key {

        /** Base path. */
        private final String basePath;
        /** Live version indicator. */
        private final boolean live;


        /**
         * Constructor.
         *
         * @param basePath CMS base path
         * @param live live version indicator
         */
        public Key(String basePath, boolean live) {
            super();
            this.basePath = basePath;
            this.live = live;
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + this.getOuterType().hashCode();
            result = (prime * result) + ((this.basePath == null) ? 0 : this.basePath.hashCode());
            result = (prime * result) + (this.live ? 1231 : 1237);
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
            Key other = (Key) obj;
            if (!this.getOuterType().equals(other.getOuterType())) {
                return false;
            }
            if (this.basePath == null) {
                if (other.basePath != null) {
                    return false;
                }
            } else if (!this.basePath.equals(other.basePath)) {
                return false;
            }
            if (this.live != other.live) {
                return false;
            }
            return true;
        }

        /**
         * Get outer type.
         *
         * @return outer type
         */
        private WebUrlCache getOuterType() {
            return WebUrlCache.this;
        }

    }


    /**
     * Cache value inner-class.
     *
     * @author Cédric Krommenhoek
     */
    private class Value {

        /** Documents metadata. */
        private final DocumentsMetadata metadata;
        /** Timestamp. */
        private final long timestamp;


        /**
         * Constructor.
         *
         * @param metadata documents metadata
         * @param timestamp timestamp
         */
        public Value(DocumentsMetadata metadata, long timestamp) {
            super();
            this.metadata = metadata;
            this.timestamp = timestamp;
        }

    }

}
