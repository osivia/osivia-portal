package org.osivia.portal.core.panels;

import org.osivia.portal.api.portlet.PortletStatus;

/**
 * Panel status.
 *
 * @author Cédric Krommenhoek
 * @see PortletStatus
 */
public class PanelStatus implements PortletStatus {

    /** Hidden panel indicator. */
    private boolean hidden;

    /** Task identifier, may be null. */
    private final String taskId;


    /**
     * Constructor.
     *
     * @param taskId task identifier, may be null
     */
    public PanelStatus(String taskId) {
        super();
        this.taskId = taskId;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public PortletStatus clone() {
        PanelStatus clone = new PanelStatus(this.taskId);
        clone.hidden = this.hidden;
        return clone;
    }


    /**
     * {@inheritDoc}
     */
    public String getTaskId() {
        return this.taskId;
    }


    /**
     * Getter for hidden.
     * 
     * @return the hidden
     */
    public boolean isHidden() {
        return this.hidden;
    }

    /**
     * Setter for hidden.
     * 
     * @param hidden the hidden to set
     */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

}
