package org.osivia.portal.api.statistics;

import java.util.HashSet;
import java.util.Set;

/**
 * Space visits.
 * 
 * @author Cédric Krommenhoek
 */
public class SpaceVisits {

    /** Hits count. */
    private int hits;
    /** Anonymous visitors count. */
    private int anonymousVisitors;
    /** Unique visitors count. */
    private int uniqueVisitors;


    /** Identified visitors. */
    private final Set<String> visitors;


    /**
     * Constructor.
     */
    public SpaceVisits() {
        super();
        this.visitors = new HashSet<>();
    }


    /**
     * Getter for hits.
     * 
     * @return the hits
     */
    public int getHits() {
        return hits;
    }

    /**
     * Setter for hits.
     * 
     * @param hits the hits to set
     */
    public void setHits(int hits) {
        this.hits = hits;
    }

    /**
     * Getter for anonymousVisitors.
     * 
     * @return the anonymousVisitors
     */
    public int getAnonymousVisitors() {
        return anonymousVisitors;
    }

    /**
     * Setter for anonymousVisitors.
     * 
     * @param anonymousVisitors the anonymousVisitors to set
     */
    public void setAnonymousVisitors(int anonymousVisitors) {
        this.anonymousVisitors = anonymousVisitors;
    }

    /**
     * Getter for uniqueVisitors.
     * 
     * @return the uniqueVisitors
     */
    public int getUniqueVisitors() {
        return uniqueVisitors;
    }

    /**
     * Setter for uniqueVisitors.
     * 
     * @param uniqueVisitors the uniqueVisitors to set
     */
    public void setUniqueVisitors(int uniqueVisitors) {
        this.uniqueVisitors = uniqueVisitors;
    }

    /**
     * Getter for visitors.
     * 
     * @return the visitors
     */
    public Set<String> getVisitors() {
        return visitors;
    }

}
