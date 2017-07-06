package org.osivia.portal.core.ui;

import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.ui.IResizableService;

/**
 * Save jQuery UI resizable component width command.
 * 
 * @author Cédric Krommenhoek
 * @see ControllerCommand
 */
public class SaveResizableWidthCommand extends ControllerCommand {

    /** Command action name. */
    public static final String ACTION = "saveResizable";

    /** Linked to tasks indicator parameter name. */
    public static final String LINKED_TO_TASKS_PARAMETER = "linkedToTasks";
    /** Resizable width parameter name. */
    public static final String WIDTH_PARAMETER = "width";


    /** Linked to tasks indicator. */
    private final boolean linkedToTasks;
    /** Resizable width. */
    private final Integer width;

    /** Command info. */
    private final CommandInfo commandInfo;
    /** Resizable service. */
    private final IResizableService resizableService;


    /**
     * Constructor.
     * 
     * @param linkedToTasks linked to tasks indicator
     */
    public SaveResizableWidthCommand(boolean linkedToTasks) {
        this(linkedToTasks, null);
    }


    /**
     * Constructor.
     * 
     * @param linkedToTasks linked to tasks indicator
     * @param width resizable width
     */
    public SaveResizableWidthCommand(boolean linkedToTasks, Integer width) {
        super();
        this.linkedToTasks = linkedToTasks;
        this.width = width;


        // Command info
        this.commandInfo = new ActionCommandInfo(false);
        // Resizable service
        this.resizableService = Locator.findMBean(IResizableService.class, IResizableService.MBEAN_NAME);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public CommandInfo getInfo() {
        return this.commandInfo;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerResponse execute() throws ControllerException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.getControllerContext());

        try {
            this.resizableService.saveWidth(portalControllerContext, this.linkedToTasks, this.width);
        } catch (PortalException e) {
            throw new ControllerException(e);
        }

        return null;
    }


    /**
     * Getter for linkedToTasks.
     * 
     * @return the linkedToTasks
     */
    public boolean isLinkedToTasks() {
        return linkedToTasks;
    }

    /**
     * Getter for width.
     * 
     * @return the width
     */
    public Integer getWidth() {
        return width;
    }

}
