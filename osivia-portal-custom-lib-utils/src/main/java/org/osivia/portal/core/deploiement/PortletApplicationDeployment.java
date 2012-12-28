package org.osivia.portal.core.deploiement;

import java.net.URL;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.jboss.deployment.DeploymentException;
import org.jboss.mx.util.MBeanProxyExt;
import org.jboss.portal.core.deployment.jboss.PortletAppDeployment;
import org.jboss.portal.core.deployment.jboss.PortletAppDeploymentFactory;

import org.jboss.portal.portlet.container.managed.ManagedObjectRegistryEventListener;
import org.jboss.portal.server.deployment.PortalWebApp;

public class PortletApplicationDeployment extends PortletAppDeployment  {
	
	  public PortletApplicationDeployment(URL url, PortalWebApp pwa, ManagedObjectRegistryEventListener listener, MBeanServer mbeanServer, PortletAppDeploymentFactory factory)
	   {
	      super(url, pwa, listener, mbeanServer, factory);
	   }

	   public void start() throws DeploymentException
	   {
	      // Inject services if needed
	      injectStandardService("CacheService","org.osivia.portal.api.cache.services.ICacheService","pia:service=CacheServices");
	      injectStandardService("StatutService","org.osivia.portal.api.statut.IStatutService","pia:service=StatutServices");
	      injectStandardService("UrlService","org.osivia.portal.api.urls.IPortalUrlFactory","pia:service=UrlFactory");
	      injectStandardService("ProfilService","org.osivia.portal.core.profils.IProfilManager","pia:service=ProfilManager");
	      injectStandardService("NuxeoService","fr.toutatice.portail.core.nuxeo.INuxeoService","pia:service=NuxeoService");
	      injectStandardService("FormatterService","org.osivia.portal.core.formatters.IFormatter","pia:service=Interceptor,type=Command,name=AssistantPageCustomizer");

	      //
	      super.start();
	   }
	   
	   protected void injectStandardService( String serviceName, String serviceClass,  String serviceRef)
	   {

	            //
	            try
	            {
	               Class proxyClass = pwa.getClassLoader().loadClass(serviceClass);
	               ObjectName objectName = ObjectName.getInstance(serviceRef);
	               Object proxy = MBeanProxyExt.create(proxyClass, objectName, mbeanServer);
	               pwa.getServletContext().setAttribute(serviceName, proxy);
	            }
	            catch (Exception e)
	            {
	               log.error("Was not able to create service proxy", e);
	            }
	         }
	      
	   
	   
}
