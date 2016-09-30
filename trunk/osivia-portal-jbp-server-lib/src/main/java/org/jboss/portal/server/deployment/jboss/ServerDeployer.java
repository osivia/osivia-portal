/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
package org.jboss.portal.server.deployment.jboss;

import org.apache.log4j.Logger;
import org.jboss.deployment.DeploymentException;
import org.jboss.deployment.DeploymentInfo;
import org.jboss.deployment.SubDeployerSupport;
import org.jboss.mx.loading.RepositoryClassLoader;
import org.jboss.portal.common.net.URLFilter;
import org.jboss.portal.common.net.URLTools;
import org.jboss.portal.server.Server;
import org.jboss.portal.server.deployment.PortalWebApp;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.tag.HeaderContentTagHandler;
import org.osivia.portal.core.theming.IPageHeaderResourceService;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 * OSIVIA : register the webapps to rename resource pages (css, javascript)
 */
public class ServerDeployer extends SubDeployerSupport implements ServerDeployerMBean
{

   /** The loggger. */
   private final Logger log;

   /** The server. */
   private Server portalServer;

   /** The factory contexts. */
   private Map factoryContexts;

   /** The portal deployment info context map. */
   private Map infoContexts;

   
   IPageHeaderResourceService pageHeaderResourceService;
   
   
   IPageHeaderResourceService getPageHeaderResourceService()   {
       if( pageHeaderResourceService == null)
           pageHeaderResourceService = Locator.findMBean(IPageHeaderResourceService.class, "osivia:service=PageHeaderResourceService");
       return pageHeaderResourceService;
   }

   

public ServerDeployer()
   {
      log = Logger.getLogger(ServerDeployer.class);
      factoryContexts = Collections.synchronizedMap(new HashMap());
      infoContexts = Collections.synchronizedMap(new HashMap());
   }

   public Set getFactories()
   {
      return null;
   }

   /** This should never be called for server deployment. */
   public boolean accepts(DeploymentInfo di)
   {
      return false;
   }

   public Server getPortalServer()
   {
      return portalServer;
   }

   public void setPortalServer(Server portalServer)
   {
      this.portalServer = portalServer;
   }

   public ServerDeployer getDeployer()
   {
      return this;
   }

   public void registerFactory(String name, URLFilter filter, DeploymentFactory factory, URL setupURL)
   {
      log.debug("Registering factory " + name);

      // Check against dual registration
      if (factoryContexts.containsKey(name))
      {
         throw new IllegalArgumentException("Attempty to register a factory twice " + name);
      }

      // Add the factory
      DeploymentFactoryContext factoryCtx = new DeploymentFactoryContext(name, filter, factory, setupURL);
      factoryContexts.put(name, factoryCtx);
      log.debug("Added factory " + name);

      // Process the setup URL
      if (setupURL != null && URLTools.exists(setupURL))
      {
         try
         {
            log.debug("Found valid setup url to deploy provided by factory " + name + " : " + setupURL);
            PortalDeploymentInfo pdi = new PortalDeploymentInfo(setupURL, server);
            pdi.deployer = this;
            infoContexts.put(pdi.url, new PortalDeploymentInfoContext(pdi));
            mainDeployer.deploy(pdi);
         }
         catch (DeploymentException e)
         {
            log.error("Failed to deploy setup url " + setupURL);
         }
      }

      //
      for (Iterator i = new ArrayList(infoContexts.values()).iterator(); i.hasNext();)
      {
         PortalDeploymentInfoContext pdiCtx = (PortalDeploymentInfoContext)i.next();
         try
         {
            log.debug("Adding factory " + name + " to pdi " + pdiCtx.getInfo().url);
            pdiCtx.add(factoryCtx, true);
         }
         catch (DeploymentException e)
         {
            log.error("Failed to deploy url " + pdiCtx.getInfo().url);
         }
      }
   }

   public void unregisterFactory(String name)
   {
      log.debug("Unregistering factory " + name);

      //
      DeploymentFactoryContext factoryCtx = (DeploymentFactoryContext)factoryContexts.remove(name);
      if (factoryCtx == null)
      {
         throw new IllegalArgumentException("Cannot unregister non existing factory " + name);
      }

      for (Iterator i = new ArrayList(infoContexts.values()).iterator(); i.hasNext();)
      {
         PortalDeploymentInfoContext pdiCtx = (PortalDeploymentInfoContext)i.next();
         pdiCtx.remove(factoryCtx, true);
      }

      URL setupURL = factoryCtx.getSetupURL();
      if (setupURL != null && URLTools.exists(setupURL))
      {
         log.debug("Found valid setup url to undeploy provided by factory " + name + " : " + setupURL);
         if (mainDeployer.isDeployed(setupURL))
         {
            try
            {
               mainDeployer.undeploy(setupURL);
            }
            catch (DeploymentException e)
            {
               log.error("Failed to un deploy setup url " + setupURL);
            }
            finally
            {
               infoContexts.remove(setupURL);
            }
         }
         else
         {
            log.warn("Unknown setup url by main deployer provided by factory " + name + " : " + setupURL);
         }
      }

      // Log the removal
      log.debug("Removed factory " + name);
   }

   protected void processNestedDeployments(DeploymentInfo di) throws DeploymentException
   {
      // We only list if it is a directory
      PortalDeploymentInfoContext pdiCtx = (PortalDeploymentInfoContext)infoContexts.get(di.url);
      for (Iterator j = factoryContexts.values().iterator(); j.hasNext();)
      {
         DeploymentFactoryContext factoryCtx = (DeploymentFactoryContext)j.next();
         pdiCtx.add(factoryCtx, false);
      }
   }

   public DeploymentFactory findFactory(URL url)
   {
      throw new UnsupportedOperationException();
   }

   public void create(DeploymentInfo di) throws DeploymentException
   {
      PortalDeploymentInfoContext pdiCtx = (PortalDeploymentInfoContext)infoContexts.get(di.url);
      pdiCtx.create();

      //
      super.create(di);
   }

   public void start(DeploymentInfo di) throws DeploymentException
   {
      PortalDeploymentInfoContext pdiCtx = (PortalDeploymentInfoContext)infoContexts.get(di.url);
      pdiCtx.start();

      //
      super.start(di);
   }

   public void stop(DeploymentInfo di) throws DeploymentException
   {
      PortalDeploymentInfoContext pdiCtx = (PortalDeploymentInfoContext)infoContexts.get(di.url);
      pdiCtx.stop();

      //
      super.stop(di);
   }

   public void destroy(DeploymentInfo di) throws DeploymentException
   {
      PortalDeploymentInfoContext pdiCtx = (PortalDeploymentInfoContext)infoContexts.get(di.url);
      pdiCtx.destroy();

      //
      super.destroy(di);
   }

   public void deploy(PortalWebApp pwa) throws DeploymentException
   {
      // Instrument the web app
      instrument(pwa);

      // Create the deployment object
      RepositoryClassLoader rcl = Deployment.findRepositoryClassLoader(pwa.getClassLoader());
      URL url = Deployment.findWEBINFURL(pwa.getURL());

      // Create our deployment info object and pass it to main deployer
      PortalDeploymentInfo pdi = new PortalDeploymentInfo(url, null, pwa, server);
      pdi.ucl = rcl;
      pdi.deployer = this;

      // Put it in the map
      infoContexts.put(pdi.url, new PortalDeploymentInfoContext(pdi));

      // And let JBoss deploy it
      mainDeployer.deploy(pdi);
      
      // Notify portal
      getPageHeaderResourceService().deploy(pwa);
   }

   public void undeploy(PortalWebApp pwa) throws DeploymentException
   {
      URL url = Deployment.findWEBINFURL(pwa.getURL());

      //
      try
      {
          
          getPageHeaderResourceService().undeploy(pwa);
          
          
         // Undeploy
         mainDeployer.undeploy(url);
         
         
      }
      catch (Exception e)
      {
         log.error("Error when undeploying portal web app", e);
      }
      finally
      {
         // Remove it in the map
         infoContexts.remove(url);
      }
   }

   /** Instrument the portal web app. */
   private void instrument(PortalWebApp pwa)
   {
      try
      {
         // Instrument war file first
         pwa.instrument();
      }
      catch (Exception e)
      {
         log.error("Cannot instrument the web application", e);
      }
   }
}
