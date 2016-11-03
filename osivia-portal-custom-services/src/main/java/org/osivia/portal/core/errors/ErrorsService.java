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
package org.osivia.portal.core.errors;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletRequest;

import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Window;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.errors.IErrorsService;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.error.CustomPageControlPolicy;
import org.osivia.portal.core.error.ErrorDescriptor;
import org.osivia.portal.core.error.GlobalErrorHandler;

/**
 * The Class ErrorsService.
 * 
 * @author Jean-Sébastien Steux
 * @see IErrorsService
 */

public class ErrorsService implements IErrorsService {

    /**
     * Default constructor.
     */
    public ErrorsService() {
        super();
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.osivia.portal.api.errors.IErrorsService#logError(org.osivia.portal.api.context.PortalControllerContext, java.lang.String)
     */
    public long logError(PortalControllerContext portalControllerContext, String message, Throwable cause, Map<String, Object> properties) {


        String userId = null;
        Map<String, Object> errorProps = new HashMap<String, Object>();


        if (properties != null)
            errorProps.putAll(properties);

        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        if (controllerContext.getUser() != null)
            userId = controllerContext.getUser().getUserName();


        PortletRequest request = portalControllerContext.getRequest();


        // Get portlet Name
        if (request != null) {

            Window window = (Window) request.getAttribute("osivia.window");
            if (window != null) {
                String portletName = CustomPageControlPolicy.getPortletName(ControllerContextAdapter.getControllerContext(portalControllerContext),
                        window.getId());
                if (portletName != null)
                    errorProps.put("osivia.portal.portlet", portletName);
            }
        }
        
        // Log the error

        ErrorDescriptor errDescriptor = new ErrorDescriptor(ErrorDescriptor.NO_HTTP_ERR_CODE, cause, message, userId, errorProps);
        return GlobalErrorHandler.getInstance().logError(errDescriptor);

    }


}
