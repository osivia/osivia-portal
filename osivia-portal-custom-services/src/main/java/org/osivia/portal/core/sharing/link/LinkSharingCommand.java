package org.osivia.portal.core.sharing.link;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.controller.command.response.ErrorResponse;
import org.jboss.portal.core.controller.command.response.SecurityErrorResponse;
import org.jboss.portal.core.controller.command.response.UnavailableResourceResponse;
import org.jboss.portal.server.ServerInvocationContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.CmsCommand;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;

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

    /** CMS service locator. */
    private final ICMSServiceLocator cmsServiceLocator;


    /**
     * Constructor.
     */
    public LinkSharingCommand() {
        super();

        // Command info
        this.commandInfo = new ActionCommandInfo(false);

        // CMS service locator
        this.cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, ICMSServiceLocator.MBEAN_NAME);
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
            try {
                // Link sharing target document path
                String path = cmsService.resolveLinkSharing(cmsContext, this.id);

                if (StringUtils.isEmpty(path)) {
                    response = new UnavailableResourceResponse("linkId=" + this.id, false);
                } else {
                    // CMS command
                    CmsCommand cmsCommand = new CmsCommand(null, path, null, null, null, null, null, null, null, null, null);

                    response = this.context.execute(cmsCommand);
                }
            } catch (CMSException e) {
                response = new ErrorResponse(e, true);
            }
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
