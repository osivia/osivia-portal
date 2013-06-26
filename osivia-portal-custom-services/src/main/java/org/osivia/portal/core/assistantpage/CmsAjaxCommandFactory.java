package org.osivia.portal.core.assistantpage;

import javax.servlet.http.HttpServletRequest;

import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.command.mapper.AbstractCommandFactory;
import org.jboss.portal.server.ServerInvocation;

/**
 * build commands provided by ajax action requests
 */
public class CmsAjaxCommandFactory extends AbstractCommandFactory {

    public ControllerCommand doMapping(ControllerContext controllerContext, ServerInvocation invocation, String host, String contextPath, String requestPath) {


        HttpServletRequest req = invocation.getServerContext().getClientRequest();
        String action = req.getParameter("action");

        // drag & drop command
        if ("windowmove".equals(action)) {

            String windowId = req.getParameter("windowId");
            String fromPos = req.getParameter("fromPos");
            String fromRegion = req.getParameter("fromRegion");
            String toPos = req.getParameter("toPos");
            String toRegion = req.getParameter("toRegion");
            int fromPosInt = Integer.parseInt(fromPos);
            int toPosInt = Integer.parseInt(toPos);

            return new CmsMoveFragmentCommand(windowId, fromRegion, fromPosInt, toRegion, toPosInt);
        }

        //
        return null;
    }
}
