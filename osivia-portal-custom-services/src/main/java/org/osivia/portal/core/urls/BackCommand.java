package org.osivia.portal.core.urls;

import org.apache.commons.lang.BooleanUtils;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.view.ViewPageCommand;
import org.jboss.portal.server.request.URLContext;
import org.jboss.portal.server.request.URLFormat;
import org.osivia.portal.core.page.RefreshPageCommand;

/**
 * Back controller command.
 * 
 * @author Cédric Krommenhoek
 * @see ControllerCommand
 */
public class BackCommand extends ControllerCommand {

    /** Command action name. */
    public static final String ACTION = "back";

    /** Page identifier parameter name. */
    public static final String PAGE_ID_PARAMETER = "pageId";
    /** Page marker parameter name. */
    public static final String PAGE_MARKER_PARAMETER = "pageMarker";
    /** Refresh indicator parameter name. */
    public static final String REFRESH_PARAMETER = "refresh";


    /** Page portal object identifier. */
    private final PortalObjectId pageObjectId;
    /** Page marker. */
    private final String pageMarker;
    /** Refresh indicator. */
    private final boolean refresh;

    /** Command info. */
    private final CommandInfo commandInfo;


    /**
     * Constructor.
     * 
     * @param pageObjectId page portal object identifier
     * @param pageMarker page marker
     * @param refresh refresh indicator
     */
    public BackCommand(PortalObjectId pageObjectId, String pageMarker, boolean refresh) {
        super();
        this.pageObjectId = pageObjectId;
        this.pageMarker = pageMarker;
        this.refresh = refresh;

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


        // Updated refresh indicator
        boolean refresh = this.refresh;
        if (!refresh) {
            refresh = BooleanUtils.isTrue((Boolean) controllerContext.getAttribute(Scope.PRINCIPAL_SCOPE, "osivia.ajax.action"));
        }


        // URL context
        URLContext urlContext = controllerContext.getServerInvocation().getServerContext().getURLContext();
        // URL format
        URLFormat urlFormat = URLFormat.newInstance(false, true);

        // Controller command
        ControllerCommand command;
        if (refresh) {
            // Page identifier
            String pageId = this.pageObjectId.toString(PortalObjectPath.SAFEST_FORMAT);

            command = new RefreshPageCommand(pageId);
        } else {
            command = new ViewPageCommand(this.pageObjectId);
        }


        // Command URL
        String url = controllerContext.renderURL(command, urlContext, urlFormat);

        // Redirection URL
        StringBuilder builder = new StringBuilder();
        builder.append(url);
        if (url.contains("?")) {
            builder.append("&");
        } else {
            builder.append("?");
        }
        builder.append("backPageMarker=");
        builder.append(this.pageMarker);

        // Controller response
        return new RedirectionResponse(builder.toString());
    }


    /**
     * Getter for pageObjectId.
     * 
     * @return the pageObjectId
     */
    public PortalObjectId getPageObjectId() {
        return pageObjectId;
    }

    /**
     * Getter for pageMarker.
     * 
     * @return the pageMarker
     */
    public String getPageMarker() {
        return pageMarker;
    }

    /**
     * Getter for refresh.
     * 
     * @return the refresh
     */
    public boolean isRefresh() {
        return refresh;
    }

}
