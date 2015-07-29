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

}
