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
package org.jboss.portal.server.deployment;

import org.apache.commons.lang.StringUtils;
import org.jboss.deployment.DeploymentInfo;
import org.jboss.deployment.SubDeployer;
import org.jboss.portal.jems.as.system.AbstractJBossService;
import org.jboss.web.WebApplication;
import org.osivia.portal.api.deployment.DeploymentContext;
import org.xml.sax.EntityResolver;

import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Relay web deployments. When a web deployment occurs, it is abstracted into a PortalWebApp object that provides a
 * consistent way to getPortalObjectContext informations and modify the web application.
 * <p/>
 * When this service stops it does not send undeployment notifications, therefore it is up to the client of this service
 * to perform any cleanup task associated to a deployment web application. The purpose of this is that most of the time
 * clients of this service will be stopped before this one and they would receive undeployments in a not started state.
 * 
 * Modification OSIVIA : rechargement des portlets sauf portlet directory
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public abstract class WebAppIntercepter
   extends AbstractJBossService
   implements NotificationListener
{

   /** WARDeployer. */
   private ObjectName interceptedDeployer;

   /** A copy of the WARDeployer used for notification subscription removal. */
   private ObjectName currentInterceptedDeployer;

   /** The current deployements. */
   private Map deployments;

   /** The factory creating the portal web app objects. */
   private PortalWebAppFactory factory;

   /** The right classloader for fixing the issue with the fact that the classloader is not good on event notifications. */
   private ClassLoader classLoader;

   /** The entity resolver for jboss-app.xml. */
   private EntityResolver jbossAppEntityResolver;

   public WebAppIntercepter()
   {
      deployments = Collections.synchronizedMap(new HashMap());
      classLoader = Thread.currentThread().getContextClassLoader();
   }

   public EntityResolver getJBossAppEntityResolver()
   {
      return jbossAppEntityResolver;
   }

   public void setJBossAppEntityResolver(EntityResolver jbossAppEntityResolver)
   {
      this.jbossAppEntityResolver = jbossAppEntityResolver;
   }

   /** Set the deployer on this service. */
   public void setInterceptedDeployer(ObjectName interceptedDeployer)
   {
      this.interceptedDeployer = interceptedDeployer;
   }

   /** Return the intercepted deployer. */
   public ObjectName getInterceptedDeployer()
   {
      return interceptedDeployer;
   }

   /** Clone and return the deployed URLs. */
   public Collection getDeployedURLs()
   {
      return new ArrayList(deployments.keySet());
   }

   private WebApplication findWebApp(DeploymentInfo info) throws Exception
   {
      // Get all the deployed web applications, our must be among these
      Iterator iterator = (Iterator)server.getAttribute(interceptedDeployer, "DeployedApplications");
      while (iterator.hasNext())
      {
         WebApplication webApp = (WebApplication)iterator.next();
         if (info == webApp.getDeploymentInfo())
         {
            return webApp;
         }
      }

      // Not found
      return null;
   }

   /** Only take care of start notifications. */
   public void handleNotification(Notification notification, Object handback)
   {
      String type = notification.getType();
      boolean start = SubDeployer.START_NOTIFICATION.equals(type);
      boolean stop = SubDeployer.STOP_NOTIFICATION.equals(type);

      // Do we do something ?
      if (start || stop)
      {
         // The previous loader
         ClassLoader previousLoader = Thread.currentThread().getContextClassLoader();

         try
         {
            // This call is coming with the MainDeployer classloader as context loader
            // For this call we change the context loader to the container one.
            Thread.currentThread().setContextClassLoader(classLoader);

            // Create the portal web app
            DeploymentInfo info = (DeploymentInfo)notification.getUserData();

            //
            URL keyURL = info.url;

            //
            if (start)
            {
               WebApplication webApp = findWebApp(info);
               PortalWebApp pwa = factory.create(webApp, jbossAppEntityResolver);
               deployments.put(keyURL, pwa);
               log.debug("Seen URL " + keyURL + " about to deploy");
               deploy(pwa);
            }
            if (stop)
            {
               // Look if we have something for that url
               PortalWebApp pwa = (PortalWebApp)deployments.remove(keyURL);

               // Notify
               if (pwa != null)
               {
                  log.debug("Undeploying URL " + keyURL);
                  undeploy(pwa);
               }
            }
         }
         catch (Exception e)
         {
            log.error("Cannot handle the intercepted deployment", e);
         }
         finally
         {
            // Put previous context loader back
            Thread.currentThread().setContextClassLoader(previousLoader);
         }
      }
   }

   /** Start listening to the deployer notifications. */
   protected void startService() throws Exception
   {
       
       
      String curContext = (String) DeploymentContext.getContext().get("osivia.portlet.currentContext"); 
       
      if (interceptedDeployer != null)
      {
         // Create factory
         factory = new PortalWebAppFactory(server);

         // Copy the name for the stop phase
         currentInterceptedDeployer = interceptedDeployer;

         // Register to notifications
         log.debug("Start listening notifications from intercepted deployer" + currentInterceptedDeployer);
         server.addNotificationListener(currentInterceptedDeployer, this, null, null);

         // Scans all the previously deployed applications
         Iterator iterator = (Iterator)server.getAttribute(currentInterceptedDeployer, "DeployedApplications");
         log.debug("Scan previously deployed web applications");
         while (iterator.hasNext())
         {
            WebApplication webApp = (WebApplication)iterator.next();
            URL keyURL = webApp.getDeploymentInfo().url;
            
            if( StringUtils.equals(curContext, webApp.getMetaData().getContextRoot()))  {
                log.debug("Avoir reentrancy" + curContext);
                
            }   else    {
            
            if (!deployments.containsKey(keyURL))
            {
               PortalWebApp pwa = factory.create(webApp, jbossAppEntityResolver);
               deployments.put(keyURL, pwa);
               log.debug("Seen URL " + keyURL + " about to deploy");
               deploy(pwa);
            }
            }
         }

      }
      else
      {
         throw new Exception("No intercepted deployer name present");
      }
   }

   /** Stop listening to the deployer notifications. */
   protected void stopService() throws Exception
   {
       
       String curContext = (String) DeploymentContext.getContext().get("osivia.portlet.currentContext"); 
       
      if (currentInterceptedDeployer != null)
      {
         // Remove all previously deployed applications
         for (Iterator i = deployments.entrySet().iterator(); i.hasNext();)
         {
            Map.Entry entry = (Map.Entry)i.next();
            URL keyURL = (URL)entry.getKey();
            PortalWebApp pwa = (PortalWebApp)entry.getValue();
            
                if (StringUtils.equals(curContext, pwa.getContextPath())) {
                    log.debug("Avoir reentrancy" + curContext);

                } else {

                    i.remove();
                    log.debug("Removing web application with URL " + keyURL);
                    undeploy(pwa);
                }
         }

         // Do not listen notifications anymore
         log.debug("Stop listening notifications from intercepted deployer" + currentInterceptedDeployer);
         server.removeNotificationListener(currentInterceptedDeployer, this);

         // Remove factory
         factory = null;

         //
         currentInterceptedDeployer = null;
      }
   }

   /** Perform the deploy notification. */
   protected abstract void deploy(PortalWebApp pwa);

   /** Perform the undeploy notification. */
   protected abstract void undeploy(PortalWebApp pwa);

}
