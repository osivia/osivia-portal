/*
 * (C) Copyright 2014 OSIVIA (http://www.osivia.com) 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
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
            String refUri = req.getParameter("refUri");
            int fromPosInt = Integer.parseInt(fromPos);
            int toPosInt = Integer.parseInt(toPos);

            return new CmsMoveFragmentCommand(windowId, fromRegion, fromPosInt, toRegion, toPosInt, refUri);
        }

        //
        return null;
    }
}
