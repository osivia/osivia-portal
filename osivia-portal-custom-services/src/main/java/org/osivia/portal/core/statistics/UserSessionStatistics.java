package org.osivia.portal.core.statistics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Statistics session attribute.
 * 
 * @author Cédric Krommenhoek
 */
public class UserSessionStatistics {

    /** Hits grouped by root paths. */
    private final Map<String, Integer> hits;


    /**
     * Constructor.
     */
    public UserSessionStatistics() {
        super();
        this.hits = new ConcurrentHashMap<>();
    }


    /**
     * Increments hits.
     * 
     * @param path root path
     */
    public void increments(String path) {
        Integer count = this.hits.get(path);

        if (count == null) {
            count = 1;
        } else {
            count += 1;
        }

        this.hits.put(path, count);
    }


    /**
     * Getter for hits.
     * 
     * @return the hits
     */
    public Map<String, Integer> getHits() {
        return this.hits;
    }

}
