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
package org.osivia.portal.core.portlets.browser.service;

import javax.portlet.PortletException;

import org.osivia.portal.api.context.PortalControllerContext;

/**
 * Live content browser service interface.
 * 
 * @author Cédric Krommenhoek
 */
public interface IBrowserService {

    /**
     * Browse live content for current node children only, in lazy loading JSON data.
     * 
     * @param portalControllerContext portal controller context
     * @param parentPath parent path, may be null for root node
     * @return JSON data
     * @throws PortletException
     */
    String browse(PortalControllerContext portalControllerContext, String parentPath) throws PortletException;


}
