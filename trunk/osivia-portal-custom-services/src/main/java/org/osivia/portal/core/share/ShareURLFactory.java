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

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.command.mapper.URLFactoryDelegate;
import org.jboss.portal.server.AbstractServerURL;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerURL;
import org.osivia.portal.api.urls.ExtendedParameters;
import org.osivia.portal.core.cms.CmsExtendedParameters;


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
            
            ShareCommand shareCmd = (ShareCommand) cmd;
            AbstractServerURL asu = new AbstractServerURL();
            
            String portalRequestPath = this.path;
            
            String webId = shareCmd.getWebId();
            if(StringUtils.isNotBlank(webId)){
                portalRequestPath = portalRequestPath.concat(webId);
            }
            
            asu.setPortalRequestPath(portalRequestPath);
            
            ExtendedParameters extendedParameters = shareCmd.getExtendedParameters();
            if (extendedParameters != null) {

                String parentId = extendedParameters.getParameter(CmsExtendedParameters.parentId.name());
                String parentPath = extendedParameters.getParameter(CmsExtendedParameters.parentId.name());
                try {
                    if (StringUtils.isNotBlank(parentId)) {
                        asu.setParameterValue(CmsExtendedParameters.parentId.name(), URLEncoder.encode(parentId, "UTF-8"));
                    } else if (StringUtils.isNotBlank(parentPath)) {
                        asu.setParameterValue(CmsExtendedParameters.parentPath.name(), URLEncoder.encode(parentId, "UTF-8"));
                    }
                } catch (UnsupportedEncodingException e) {
                    // Ignore
                }

            }
            
            return asu;
            
        }
        return null;
        
    }

}
