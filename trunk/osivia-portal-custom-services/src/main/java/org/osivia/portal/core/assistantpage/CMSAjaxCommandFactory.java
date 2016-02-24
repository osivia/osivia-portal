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
 */
package org.osivia.portal.core.assistantpage;

import javax.servlet.http.HttpServletRequest;

import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.command.mapper.AbstractCommandFactory;
import org.jboss.portal.server.ServerInvocation;

/**
 * Build commands provided by AJAX action requests.
 *
 * @see AbstractCommandFactory
 */
public class CMSAjaxCommandFactory extends AbstractCommandFactory {

    /**
     * Constructor.
     */
    public CMSAjaxCommandFactory() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public ControllerCommand doMapping(ControllerContext controllerContext, ServerInvocation invocation, String host, String contextPath, String requestPath) {
        HttpServletRequest request = invocation.getServerContext().getClientRequest();
        String action = request.getParameter("action");

        // Drag'n'drop command
        if ("windowmove".equals(action)) {
            String windowId = request.getParameter("windowId");
            String fromPos = request.getParameter("fromPos");
            String fromRegion = request.getParameter("fromRegion");
            String toPos = request.getParameter("toPos");
            String toRegion = request.getParameter("toRegion");
            String refUri = request.getParameter("refUri");
            int fromPosInt = Integer.parseInt(fromPos);
            int toPosInt = Integer.parseInt(toPos);

            return new CMSMoveFragmentCommand(windowId, fromRegion, fromPosInt, toRegion, toPosInt, refUri);
        }

        //
        return null;
    }
}
