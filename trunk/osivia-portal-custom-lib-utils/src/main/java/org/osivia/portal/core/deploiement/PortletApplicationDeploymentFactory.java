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
package org.osivia.portal.core.deploiement;

import java.net.URL;

import javax.management.MBeanServer;

import org.jboss.deployment.DeploymentException;
import org.jboss.portal.core.deployment.jboss.PortletAppDeployment;
import org.jboss.portal.core.deployment.jboss.PortletAppDeploymentFactory;
import org.jboss.portal.server.deployment.PortalWebApp;
import org.jboss.portal.server.deployment.jboss.Deployment;

public class PortletApplicationDeploymentFactory extends PortletAppDeploymentFactory {
	
	   public Deployment newInstance(URL url, PortalWebApp pwa, MBeanServer mbeanServer) throws DeploymentException
	   {
	      return new PortletApplicationDeployment(url, pwa, bridgeToInvoker, mbeanServer, this);
	   }

}
