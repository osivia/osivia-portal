package org.osivia.portal.core.page;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.controller.command.response.UnavailableResourceResponse;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.cms.*;

import java.util.List;

/**
 * User workspace command.
 *
 * @author Cédric Krommenhoek
 * @see ControllerCommand
 */
public class UserWorkspaceCommand extends ControllerCommand {

    /** Command action name. */
    public static final String ACTION = "userWorkspace";


    /**
     * Command info.
     */
    private final CommandInfo commandInfo;


    /**
     * Log.
     */
    private final Log log;

    /**
     * CMS service locator.
     */
    private final ICMSServiceLocator cmsServiceLocator;

    /**
     * Portal URL factory.
     */
    private final IPortalUrlFactory portalUrlFactory;


    /**
     * Constructor.
     */
    public UserWorkspaceCommand() {
        super();
        this.commandInfo = new ActionCommandInfo(false);

        // Log
        this.log = LogFactory.getLog(this.getClass());
        // CMS service locator
        this.cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, ICMSServiceLocator.MBEAN_NAME);
        // Portal URL factory
        this.portalUrlFactory = Locator.findMBean(IPortalUrlFactory.class, IPortalUrlFactory.MBEAN_NAME);
    }


    @Override
    public CommandInfo getInfo() {
        return this.commandInfo;
    }


    @Override
    public ControllerResponse execute() throws ControllerException {
        // Controller context
        ControllerContext controllerContext = this.getControllerContext();
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(controllerContext);

        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();
        // CMS context
        CMSServiceCtx cmsContext = new CMSServiceCtx();
        cmsContext.setControllerContext(controllerContext);

        // User workspace
        CMSItem userWorkspace = this.getUserWorkspace(cmsService, cmsContext);

        // Response
        ControllerResponse response;

        if ((userWorkspace == null) || StringUtils.isEmpty(userWorkspace.getCmsPath())) {
            response = new UnavailableResourceResponse("UserWorkspace", false);
        } else {
            // URL
            String url = this.portalUrlFactory.getCMSUrl(portalControllerContext, null, userWorkspace.getCmsPath(), null, null, null, null, null, null, null);

            response = new RedirectionResponse(url);
        }

        return response;
    }


    /**
     * Get user workspace.
     *
     * @param cmsService CMS service
     * @param cmsContext CMS context
     * @return user workspace, or null if not found
     */
    private CMSItem getUserWorkspace(ICMSService cmsService, CMSServiceCtx cmsContext) {
        // User workspaces
        List<CMSItem> userWorkspaces;
        try {
            userWorkspaces = cmsService.getWorkspaces(cmsContext, true, false);
        } catch (CMSException e) {
            userWorkspaces = null;
            this.log.error("Unable to find user workspace.", e);
        }

        // User workspace
        CMSItem userWorkspace;
        if (CollectionUtils.isEmpty(userWorkspaces)) {
            userWorkspace = null;
        } else {
            userWorkspace = userWorkspaces.get(0);
        }

        return userWorkspace;
    }

}
