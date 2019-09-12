package org.osivia.portal.core.tasks;

import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.controller.command.response.ErrorResponse;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.cms.*;

import java.util.UUID;

/**
 * Update task command.
 *
 * @author Jean-Sébastien Steux
 * @author Cédric Krommenhoek
 * @see ControllerCommand
 */
public class ViewTaskCommand extends ControllerCommand {

    /**
     * Command action name.
     */
    public static final String ACTION = "viewTask";


    /**
     * UUID.
     */
    private final UUID uuid;

    /**
     * Command info.
     */
    private final CommandInfo commandInfo;

    /**
     * Portal URL factory.
     */
    private final IPortalUrlFactory portalUrlFactory;
    /**
     * CMS service locator.
     */
    private final ICMSServiceLocator cmsServiceLocator;


    /**
     * Constructor.
     *
     * @param uuid UUID
     */
    public ViewTaskCommand(UUID uuid) {
        super();
        this.uuid = uuid;

        // Command info
        this.commandInfo = new ActionCommandInfo(false);

        // Portal URL factory
        this.portalUrlFactory = Locator.findMBean(IPortalUrlFactory.class, IPortalUrlFactory.MBEAN_NAME);
        // CMS service locator
        this.cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, ICMSServiceLocator.MBEAN_NAME);
    }


    @Override
    public CommandInfo getInfo() {
        return this.commandInfo;
    }


    @Override
    public ControllerResponse execute() throws ControllerException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.context);

        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();
        // CMS context
        CMSServiceCtx cmsContext = new CMSServiceCtx();
        cmsContext.setControllerContext(this.context);

        // Response
        ControllerResponse response;
        try {
            CMSItem task = cmsService.getTask(cmsContext, uuid);
            if (task != null) {
                // Display context
                String displayContext = task.getProperties().get("displayContext");
                // Redirection URL
                String redirectionUrl = this.portalUrlFactory.getCMSUrl(portalControllerContext, null, task.getCmsPath(), null, null, displayContext, null, null, null, null);
                response = new RedirectionResponse(redirectionUrl);
            } else {
                throw new CMSException(CMSException.ERROR_NOTFOUND);
            }
        } catch (CMSException e) {
            response = new ErrorResponse(e, true);
        }

        return response;
    }


    /**
     * Getter for uuid.
     *
     * @return the uuid
     */
    public UUID getUuid() {
        return this.uuid;
    }

}
