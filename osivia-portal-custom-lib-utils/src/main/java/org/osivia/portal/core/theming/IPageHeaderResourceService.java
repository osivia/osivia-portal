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
package org.osivia.portal.core.theming;

import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.server.deployment.PortalWebApp;


/**
 * Page header resource service interface.
 *
 * @author Jean-Sébastien Steux
 * @author Cédric Krommenhoek
 */
public interface IPageHeaderResourceService {

    /** MBean name. */
    String MBEAN_NAME = "osivia:service=PageHeaderResourceService";


    /**
     * Portal web-app deployment.
     *
     * @param portalWebApp the portal web-app to deploy
     */
    void deploy(PortalWebApp portalWebApp);


    /**
     * Portal web-app undeployment.
     *
     * @param portalWebApp the portal web-app to undeploy
     */
    void undeploy(PortalWebApp portalWebApp);


    /**
     * Adapt resource element.
     *
     * @param originalElement original resource element
     * @return adapted resource element
     */
    String adaptResourceElement(String originalElement);


    /**
     * Get portal version.
     *
     * @param controllerContext controller context
     * @return portal version
     */
    String getPortalVersion(ControllerContext controllerContext);

}
