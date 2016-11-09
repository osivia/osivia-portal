package org.osivia.portal.core.assistantpage;

import org.apache.commons.lang.BooleanUtils;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.core.constants.InternalConstants;


/**
 * Toggle advanced CMS tools command.
 *
 * @author Cédric Krommenhoek
 * @see ControllerCommand
 */
public class ToggleAdvancedCMSToolsCommand extends ControllerCommand {

    /** Action value. */
    public static final String ACTION = ToggleAdvancedCMSToolsCommand.class.getSimpleName();


    /** Current page identifier. */
    private final String pageId;
    /** Command info. */
    private final CommandInfo commandInfo;


    /**
     * Constructor.
     *
     * @param pageId current page identifier
     */
    public ToggleAdvancedCMSToolsCommand(String pageId) {
        super();
        this.pageId = pageId;

        // Command info
        this.commandInfo = new ActionCommandInfo(false);
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
        // Controller context
        ControllerContext controllerContext = this.getControllerContext();
        // Page portal object identifier
        PortalObjectId pagePortalObjectId = PortalObjectId.parse(this.pageId, PortalObjectPath.SAFEST_FORMAT);

        // Show advanced CMS tools indicator new value
        boolean showAdvancedTools = BooleanUtils.isNotTrue((Boolean) controllerContext.getAttribute(SESSION_SCOPE,
                InternalConstants.SHOW_ADVANCED_CMS_TOOLS_INDICATOR));
        controllerContext.setAttribute(SESSION_SCOPE, InternalConstants.SHOW_ADVANCED_CMS_TOOLS_INDICATOR, BooleanUtils.toBooleanObject(showAdvancedTools));

        // Update current page
        return new UpdatePageResponse(pagePortalObjectId);
    }


    /**
     * Getter for pageId.
     *
     * @return the pageId
     */
    public String getPageId() {
        return this.pageId;
    }

}
