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
