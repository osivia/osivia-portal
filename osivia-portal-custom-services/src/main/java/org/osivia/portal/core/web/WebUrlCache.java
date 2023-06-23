package org.osivia.portal.core.web;

import org.osivia.portal.core.cms.DocumentsMetadata;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Web URL cache.
 *
 * @author Cédric Krommenhoek
 */
public class WebUrlCache {

    /**
     * Cache.
     */
    private final ConcurrentMap<Key, Value> cache;


    /**
     * Constructor.
     */
    public WebUrlCache() {
        super();
        this.cache = new ConcurrentHashMap<>();
    }


    /**
     * Get cache value.
     *
     * @param basePath CMS base path
     * @param live     live version indicator
     * @return cache value
     */
    public Value getValue(String basePath, boolean live) {
        Key key = new Key(basePath, live);
        return this.cache.get(key);
    }


    /**
     * Get documents metadata.
     *
     * @param basePath CMS base path
     * @param live     live version indicator
     * @return documents metadata
     */
    public DocumentsMetadata getMetadata(String basePath, boolean live) {
        Key key = new Key(basePath, live);
        Value value = this.cache.get(key);

        DocumentsMetadata metadata;
        if (value != null) {
            metadata = value.metadata;
        } else {
            metadata = null;
        }
        return metadata;
    }


    /**
     * Set documents metadata.
     *
     * @param basePath CMS base path
     * @param live     live version indicator
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
    private static class Key {

        /**
         * Base path.
         */
        private final String basePath;
        /**
         * Live version indicator.
         */
        private final boolean live;


        /**
         * Constructor.
         *
         * @param basePath CMS base path
         * @param live     live version indicator
         */
        public Key(String basePath, boolean live) {
            super();
            this.basePath = basePath;
            this.live = live;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            if (live != key.live) return false;
            return Objects.equals(basePath, key.basePath);
        }

        @Override
        public int hashCode() {
            int result = basePath != null ? basePath.hashCode() : 0;
            result = 31 * result + (live ? 1 : 0);
            return result;
        }

    }


    /**
     * Cache value inner-class.
     *
     * @author Cédric Krommenhoek
     */
    public static class Value {

        /**
         * Documents metadata.
         */
        private final DocumentsMetadata metadata;
        /**
         * Timestamp.
         */
        private long timestamp;


        /**
         * Constructor.
         *
         * @param metadata  documents metadata
         * @param timestamp timestamp
         */
        public Value(DocumentsMetadata metadata, long timestamp) {
            super();
            this.metadata = metadata;
            this.timestamp = timestamp;
        }


        /**
         * Getter for metadata.
         *
         * @return the metadata
         */
        public DocumentsMetadata getMetadata() {
            return this.metadata;
        }

        /**
         * Getter for timestamp.
         *
         * @return the timestamp
         */
        public long getTimestamp() {
            return this.timestamp;
        }

        /**
         * Setter for timestamp.
         *
         * @param timestamp the timestamp to set
         */
        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

    }

}
