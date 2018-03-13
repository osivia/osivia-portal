package org.osivia.portal.api.statistics;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Space statistics.
 * 
 * @author Cédric Krommenhoek
 */
public class SpaceStatistics {

    /** Last update. */
    private Date lastUpdate;
    /** Current day visits. */
    private SpaceVisits currentDayVisits;
    /** Current month visits. */
    private SpaceVisits currentMonthVisits;

    /** Previous update. */
    private Date previousUpdate;


    /** Space path. */
    private final String path;
    /** Historized days visits. */
    private final Map<Date, SpaceVisits> historizedDaysVisits;
    /** Historized months visits. */
    private final Map<Date, SpaceVisits> historizedMonthsVisits;


    /**
     * Constructor.
     * 
     * @param path space path
     */
    public SpaceStatistics(String path) {
        super();
        this.path = path;
        this.historizedDaysVisits = new HashMap<>();
        this.historizedMonthsVisits = new HashMap<>();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        return result;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SpaceStatistics other = (SpaceStatistics) obj;
        if (path == null) {
            if (other.path != null)
                return false;
        } else if (!path.equals(other.path))
            return false;
        return true;
    }


    /**
     * Getter for lastUpdate.
     * 
     * @return the lastUpdate
     */
    public Date getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Setter for lastUpdate.
     * 
     * @param lastUpdate the lastUpdate to set
     */
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * Getter for currentDayVisits.
     * 
     * @return the currentDayVisits
     */
    public SpaceVisits getCurrentDayVisits() {
        return currentDayVisits;
    }

    /**
     * Setter for currentDayVisits.
     * 
     * @param currentDayVisits the currentDayVisits to set
     */
    public void setCurrentDayVisits(SpaceVisits currentDayVisits) {
        this.currentDayVisits = currentDayVisits;
    }

    /**
     * Getter for currentMonthVisits.
     * 
     * @return the currentMonthVisits
     */
    public SpaceVisits getCurrentMonthVisits() {
        return currentMonthVisits;
    }

    /**
     * Setter for currentMonthVisits.
     * 
     * @param currentMonthVisits the currentMonthVisits to set
     */
    public void setCurrentMonthVisits(SpaceVisits currentMonthVisits) {
        this.currentMonthVisits = currentMonthVisits;
    }

    /**
     * Getter for previousUpdate.
     * 
     * @return the previousUpdate
     */
    public Date getPreviousUpdate() {
        return previousUpdate;
    }

    /**
     * Setter for previousUpdate.
     * 
     * @param previousUpdate the previousUpdate to set
     */
    public void setPreviousUpdate(Date previousUpdate) {
        this.previousUpdate = previousUpdate;
    }

    /**
     * Getter for path.
     * 
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * Getter for historizedDaysVisits.
     * 
     * @return the historizedDaysVisits
     */
    public Map<Date, SpaceVisits> getHistorizedDaysVisits() {
        return historizedDaysVisits;
    }

    /**
     * Getter for historizedMonthsVisits.
     * 
     * @return the historizedMonthsVisits
     */
    public Map<Date, SpaceVisits> getHistorizedMonthsVisits() {
        return historizedMonthsVisits;
    }

}

