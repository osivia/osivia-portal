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
package org.osivia.portal.api.path;

import javax.portlet.PortletException;

import org.osivia.portal.api.context.PortalControllerContext;

/**
 * CMS path service interface.
 * 
 * @author Cédric Krommenhoek
 */
public interface ICMSPathService {

    /** MBean name. */
    String MBEAN_NAME = "osivia:service=CMSPathService";


    /**
     * Browse CMS content in lazy-loading mode.
     * This method return JSON formatted content, useful for JSTree.
     * 
     * @param portalControllerContext portal controller context
     * @param path CMS path
     * @param liveContent live content indicator
     * @param onlyNavigableItems browse only navigable items indicator
     * @return JSON formatted content
     * @throws PortletException
     */
    String browseContentLazyLoading(PortalControllerContext portalControllerContext, String path, boolean liveContent, boolean onlyNavigableItems)
            throws PortletException;

}
