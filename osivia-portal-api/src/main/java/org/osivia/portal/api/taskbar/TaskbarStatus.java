package org.osivia.portal.api.taskbar;

import java.util.HashMap;
import java.util.Map;

/**
 * Taskbar status.
 *
 * @author Cédric Krommenhoek
 */
public class TaskbarStatus implements Cloneable {

    /** Taskbar states. */
    private final Map<String, TaskbarState> states;


    /**
     * Constructor.
     */
    public TaskbarStatus() {
        this.states = new HashMap<String, TaskbarState>();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public TaskbarStatus clone() {
        TaskbarStatus clone = new TaskbarStatus();
        clone.states.putAll(this.states);
        return clone;
    }


    /**
     * Getter for states.
     * 
     * @return the states
     */
    public Map<String, TaskbarState> getStates() {
        return this.states;
    }

}
