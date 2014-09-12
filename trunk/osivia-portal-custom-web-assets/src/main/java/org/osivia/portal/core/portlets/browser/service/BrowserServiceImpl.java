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
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.path.ICMSPathService;
import org.springframework.stereotype.Service;

/**
 * Live content browser service implementation.
 * 
 * @author Cédric Krommenhoek
 * @see IBrowserService
 */
@Service
public class BrowserServiceImpl implements IBrowserService {

    /** CMS path service. */
    private final ICMSPathService cmsPathService;


    /**
     * Constructor.
     */
    public BrowserServiceImpl() {
        super();
        this.cmsPathService = Locator.findMBean(ICMSPathService.class, ICMSPathService.MBEAN_NAME);
    }


    /**
     * {@inheritDoc}
     */
    public String browse(PortalControllerContext portalControllerContext, String path) throws PortletException {
        return this.cmsPathService.browseContentLazyLoading(portalControllerContext, path, true, false);
    }

}
