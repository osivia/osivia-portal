package org.osivia.portal.core.tasks;

import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.controller.command.response.ErrorResponse;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;

/**
 * Update task command.
 *
 * @author Cédric Krommenhoek
 * @see ControllerCommand
 */
public class UpdateTaskCommand extends ControllerCommand {

    /** Command action name. */
    public static final String ACTION = "updateTask";

    /** UUID parameter name. */
    public static final String UUID_PARAMETER = "uuid";
    /** Action identifier parameter name. */
    public static final String ACTION_ID_PARAMETER = "actionId";
    /** Task variables parameter name. */
    public static final String VARIABLES_PARAMETER = "variables";
    /** Redirection URL parameter name. */
    public static final String REDIRECTION_URL_PARAMETER = "redirectionUrl";


    /** UUID. */
    private final UUID uuid;
    /** Action identifier. */
    private final String actionId;
    /** Task variables. */
    private final Map<String, String> variables;
    /** Redirection URL. */
    private final String redirectionUrl;

    /** Command info. */
    private final CommandInfo commandInfo;

    /** CMS service locator. */
    private final ICMSServiceLocator cmsServiceLocator;


    /**
     * Constructor.
     *
     * @param uuid UUID
     * @param actionId action identifier
     * @param variables task variables
     * @param redirectionUrl redirection URL
     */
    public UpdateTaskCommand(UUID uuid, String actionId, Map<String, String> variables, String redirectionUrl) {
        super();
        this.uuid = uuid;
        this.actionId = actionId;
        this.variables = variables;
        this.redirectionUrl = redirectionUrl;

        // Command info
        this.commandInfo = new ActionCommandInfo(false);

        // CMS service locator
        this.cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, ICMSServiceLocator.MBEAN_NAME);
    }


    /**
     * Default constructor.
     */
    public UpdateTaskCommand() {
        this(null, null, null, null);
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
        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();
        // CMS context
        CMSServiceCtx cmsContext = new CMSServiceCtx();
        cmsContext.setControllerContext(this.context);

        // Response
        ControllerResponse response;

        try {
            cmsService.updateTask(cmsContext, this.uuid, this.actionId, this.variables);

            // Redirection
            if (StringUtils.isEmpty(this.redirectionUrl)) {
                PortalObjectId pageId = PortalObjectUtils.getPageId(this.context);
                if (pageId == null) {
                    Portal portal = PortalObjectUtils.getPortal(this.context);
                    pageId = portal.getDefaultPage().getId();
                }
                response = new UpdatePageResponse(pageId);
            } else {
                response = new RedirectionResponse(this.redirectionUrl);
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

    /**
     * Getter for actionId.
     *
     * @return the actionId
     */
    public String getActionId() {
        return this.actionId;
    }

    /**
     * Getter for variables.
     *
     * @return the variables
     */
    public Map<String, String> getVariables() {
        return this.variables;
    }

    /**
     * Getter for redirectionUrl.
     *
     * @return the redirectionUrl
     */
    public String getRedirectionUrl() {
        return this.redirectionUrl;
    }

}
