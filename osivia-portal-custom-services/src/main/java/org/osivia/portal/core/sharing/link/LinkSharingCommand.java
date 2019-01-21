package org.osivia.portal.core.sharing.link;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.controller.command.response.SecurityErrorResponse;
import org.jboss.portal.server.ServerInvocationContext;
import org.osivia.portal.core.cms.CmsCommand;

/**
 * Link sharing command.
 * 
 * @author Cédric Krommenhoek
 * @see ControllerCommand
 */
public class LinkSharingCommand extends ControllerCommand {

    /** Link identifier. */
    private String id;


    /** Command info. */
    private final CommandInfo commandInfo;


    /**
     * Constructor.
     */
    public LinkSharingCommand() {
        super();

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
        // Server context
        ServerInvocationContext serverContext = this.context.getServerInvocation().getServerContext();
        // HTTP servlet request
        HttpServletRequest servletRequest = serverContext.getClientRequest();
        // Remote user
        String user = servletRequest.getRemoteUser();

        // Controller response
        ControllerResponse response;

        if (StringUtils.isEmpty(user)) {
            response = new SecurityErrorResponse(SecurityErrorResponse.NOT_AUTHORIZED, false);
        } else {
            // TODO Auto-generated method stub


            String cmsPath = "/default-domain/workspaces/workspace-1/documents";
            CmsCommand cmsCommand = new CmsCommand(null, cmsPath, null, null, null, null, null, null, null, null, null);

            response = this.context.execute(cmsCommand);
        }

        return response;
    }


    /**
     * Getter for id.
     * 
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Setter for id.
     * 
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

}
