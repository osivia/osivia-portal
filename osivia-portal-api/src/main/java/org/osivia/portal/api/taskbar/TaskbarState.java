package org.osivia.portal.api.taskbar;

/**
 * Taskbar state.
 *
 * @author Cédric Krommenhoek
 */
public class TaskbarState {

    /** Task. */
    private TaskbarTask task;
    /** Closed taskbar indicator. */
    private boolean closed;
    /** Hide taskbar toggle command indicator. */
    private boolean hideToggle;


    /**
     * Constructor.
     */
    public TaskbarState() {
        super();
    }


    /**
     * Getter for task.
     *
     * @return the task
     */
    public TaskbarTask getTask() {
        return this.task;
    }

    /**
     * Setter for task.
     *
     * @param task the task to set
     */
    public void setTask(TaskbarTask task) {
        this.task = task;
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
