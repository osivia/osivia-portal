package org.osivia.portal.api.taskbar;

import java.util.Map;

/**
 * Taskbar player java-bean.
 *
 * @author Cédric Krommenhoek
 */
public class TaskbarPlayer {

    /** Instance name. */
    private String instance;
    /** Window properties. */
    private Map<String, String> properties;
    /** Taskbar player closed indicator. */
    private boolean closed;
    /** Hide taskbar toggle command indicator. */
    private boolean hideToggle;


    /**
     * Constructor.
     */
    public TaskbarPlayer() {
        super();
    }


    /**
     * Getter for instance.
     *
     * @return the instance
     */
    public String getInstance() {
        return this.instance;
    }

    /**
     * Setter for instance.
     *
     * @param instance the instance to set
     */
    public void setInstance(String instance) {
        this.instance = instance;
    }

    /**
     * Getter for properties.
     *
     * @return the properties
     */
    public Map<String, String> getProperties() {
        return this.properties;
    }

    /**
     * Setter for properties.
     *
     * @param properties the properties to set
     */
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    /**
     * Getter for closed.
     *
     * @return the closed
     */
    public boolean isClosed() {
        return this.closed;
    }

    /**
     * Setter for closed.
     *
     * @param closed the closed to set
     */
    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    /**
     * Getter for hideToggle.
     *
     * @return the hideToggle
     */
    public boolean isHideToggle() {
        return this.hideToggle;
    }

    /**
     * Setter for hideToggle.
     *
     * @param hideToggle the hideToggle to set
     */
    public void setHideToggle(boolean hideToggle) {
        this.hideToggle = hideToggle;
    }

}
