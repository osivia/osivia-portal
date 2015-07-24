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
package org.osivia.portal.core.share;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.common.util.ParameterMap;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.command.mapper.AbstractCommandFactory;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.core.web.IWebIdService;


/**
 * @author David Chevrier.
 *
 */
public class ShareCommandFactoryService extends AbstractCommandFactory {

    /**
     * {@inheritDoc}
     */
    public ControllerCommand doMapping(ControllerContext controllerContext, ServerInvocation invocation, String host, String contextPath, String requestPath) {
        
        ShareCommand shareCmd = new ShareCommand(requestPath);

        ParameterMap parameterMap = controllerContext.getServerInvocation().getServerContext().getQueryParameterMap();
        if (parameterMap != null) {
            String[] parameters = parameterMap.get(ShareURLFactory.PARENT_ID_PARAM_KEY);

            if (parameters != null && parameters.length > 0) {
                String parentId = parameters[0];

                if (StringUtils.isNotBlank(parentId)) {
                    try {
                        shareCmd.setParentId(URLDecoder.decode(parentId, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        // Ignore
                    }
                }
            }
        }

        return shareCmd;
    }

}
