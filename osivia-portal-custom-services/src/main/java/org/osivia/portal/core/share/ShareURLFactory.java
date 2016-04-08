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
package org.osivia.portal.core.share;

import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.command.mapper.URLFactoryDelegate;
import org.jboss.portal.server.AbstractServerURL;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerURL;
import org.osivia.portal.core.urls.WindowPropertiesEncoder;


/**
 * @author David Chevrier.
 *
 */
public class ShareURLFactory extends URLFactoryDelegate {

    /** Configured path. */
    private String path;

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * {@inheritDoc}
     */
    public ServerURL doMapping(ControllerContext controllerContext, ServerInvocation invocation, ControllerCommand cmd) {

        if (cmd == null) {
            throw new IllegalArgumentException("No null command accepted");
        }

        if(cmd instanceof ShareCommand){

            final ShareCommand shareCmd = (ShareCommand) cmd;
            final AbstractServerURL asu = new AbstractServerURL();

            String portalRequestPath = this.path;

            final String webId = shareCmd.getWebId();
            if(StringUtils.isNotBlank(webId)){
                portalRequestPath = portalRequestPath.concat(webId);
            }

            asu.setPortalRequestPath(portalRequestPath);

            final Map<String, String> pageParams = shareCmd.getParams();
            if (pageParams != null) {
                try {
                    asu.setParameterValue("pageParams", URLEncoder.encode(WindowPropertiesEncoder.encodeProperties(pageParams), "UTF-8"));
                } catch (final Exception e) {
                    // ignore
                }
            }

            return asu;

        }
        return null;

    }

}
