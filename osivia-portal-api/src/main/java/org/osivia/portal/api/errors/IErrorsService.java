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
package org.osivia.portal.api.errors;

import java.util.Map;

import org.osivia.portal.api.context.PortalControllerContext;

/**
 * Errors service interface.
 *
 * @author Jean-Sébastien Steux
 */
public interface IErrorsService {

    /** MBean name. */
    static final String MBEAN_NAME = "osivia:service=ErrorsService";


    /**
     * Add a simple error.
     *
     * @param portalControllerContext portal controller context
     * @param message notification message
     * @param type notification type
     */
    long logError(PortalControllerContext portalControllerContext, String message, Throwable cause, Map<String,Object> properties);

 
}
