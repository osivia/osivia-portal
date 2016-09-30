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
import java.util.Map;

import org.jboss.portal.common.util.ParameterMap;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.command.mapper.AbstractCommandFactory;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.core.urls.WindowPropertiesEncoder;


/**
 * @author David Chevrier.
 *
 */
public class ShareCommandFactoryService extends AbstractCommandFactory {

    /**
     * {@inheritDoc}
     */
    public ControllerCommand doMapping(ControllerContext controllerContext, ServerInvocation invocation, String host, String contextPath, String requestPath) {

        final ShareCommand shareCmd = new ShareCommand(requestPath);
        final ParameterMap parameterMap = controllerContext.getServerInvocation().getServerContext().getQueryParameterMap();
        if (parameterMap != null) {
            try {
                if (parameterMap.get("pageParams") != null) {
                    final String sPageParms = URLDecoder.decode(parameterMap.get("pageParams")[0], "UTF-8");
                    final Map<String, String> pageParams = WindowPropertiesEncoder.decodeProperties(sPageParms);
                    shareCmd.setParams(pageParams);
                }
            } catch (final UnsupportedEncodingException e) {
                // Ignore
            }
        }

        return shareCmd;
    }

}
