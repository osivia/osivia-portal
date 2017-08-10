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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.command.mapper.URLFactoryDelegate;
import org.jboss.portal.server.AbstractServerURL;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerURL;
import org.osivia.portal.api.page.PageParametersEncoder;
import org.osivia.portal.core.urls.WindowPropertiesEncoder;


/**
 * Share URL factory.
 * 
 * @author David Chevrier.
 * @see URLFactoryDelegate
 */
public class ShareURLFactory extends URLFactoryDelegate {

    /** Configured path. */
    private String path;


    /**
     * {@inheritDoc}
     */
    public ServerURL doMapping(ControllerContext controllerContext, ServerInvocation invocation, ControllerCommand controllerCommand) {
        if (controllerCommand == null) {
            throw new IllegalArgumentException("No null command accepted");
        }

        if (controllerCommand instanceof ShareCommand) {
            // Share command
            ShareCommand command = (ShareCommand) controllerCommand;
            // Abstract server URL
            AbstractServerURL asu = new AbstractServerURL();

            // Portal request path
            String portalRequestPath = this.path;

            // WebId
            String webId = command.getWebId();
            if (StringUtils.isNotBlank(webId)) {
                portalRequestPath = portalRequestPath.concat(webId);
            }

            asu.setPortalRequestPath(portalRequestPath);

            // Parent webId
            String parentWebId = command.getParentWebId();
            if (StringUtils.isNotBlank(parentWebId)) {
                try {
                    asu.setParameterValue("l", URLEncoder.encode(parentWebId, CharEncoding.UTF_8));
                } catch (UnsupportedEncodingException e) {
                    // Ignore
                }
            }
            
            // Parent webId
            String displayContext = command.getDisplayContext();
            if (StringUtils.isNotBlank(displayContext)) {
                try {
                    asu.setParameterValue("displayContext", URLEncoder.encode(displayContext, CharEncoding.UTF_8));
                } catch (UnsupportedEncodingException e) {
                    // Ignore
                }
            }

            // Page parameters
            Map<String, String> pageParams = command.getParameters();
            if (pageParams != null) {
                boolean emptySelectors = false;
                if (pageParams.containsKey("selectors")) {
                    Map<String, List<String>> properties = PageParametersEncoder.decodeProperties(pageParams.get("selectors"));
                    emptySelectors = MapUtils.isEmpty(properties);
                }

                if (!pageParams.isEmpty() && !((pageParams.size() == 1) && emptySelectors)) {
                    try {
                        asu.setParameterValue("pageParams", URLEncoder.encode(WindowPropertiesEncoder.encodeProperties(pageParams), CharEncoding.UTF_8));
                    } catch (UnsupportedEncodingException e) {
                        // Ignore
                    }
                }
            }

            return asu;
        }

        return null;
    }


    /**
     * Setter for path.
     * 
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

}
