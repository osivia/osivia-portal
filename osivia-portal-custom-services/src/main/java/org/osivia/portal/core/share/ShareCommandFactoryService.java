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

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.portal.common.util.ParameterMap;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.command.mapper.AbstractCommandFactory;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.api.urls.ExtendedParameters;
import org.osivia.portal.core.cms.CmsExtendedParameters;


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
            String[] parentIdParameters = parameterMap.get(CmsExtendedParameters.parentId.name());
            String[] parentPathParameters = parameterMap.get(CmsExtendedParameters.parentPath.name());
            
            try {
                if (ArrayUtils.isNotEmpty(parentIdParameters)) {
                    String parentId = parentIdParameters[0];
                    if (StringUtils.isNotBlank(parentId)) {
                        setShareCmdParametres(shareCmd, CmsExtendedParameters.parentId.name(), parentId);
                    }
                } else if (ArrayUtils.isNotEmpty(parentPathParameters)) {
                    String parentPath = parentPathParameters[0];
                    if(StringUtils.isNotBlank(parentPath)){
                        setShareCmdParametres(shareCmd, CmsExtendedParameters.parentPath.name(), parentPath);
                    }
                }
            } catch (UnsupportedEncodingException e) {
                // Ignore
            }
        }

        return shareCmd;
    }

    /**
     * Set extended parameters.
     * 
     * @param shareCmd
     * @param paramKey
     * @param param
     * @throws UnsupportedEncodingException
     */
    private void setShareCmdParametres(ShareCommand shareCmd, String paramKey, String param) throws UnsupportedEncodingException {
        ExtendedParameters extendedParameters = new ExtendedParameters();
        extendedParameters.addParameter(paramKey, URLDecoder.decode(param, "UTF-8"));
        shareCmd.setExtendedParameters(extendedParameters);
    }

}
