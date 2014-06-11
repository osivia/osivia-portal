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
package org.osivia.portal.core.deploiement;

import org.jboss.deployment.DeploymentException;
import org.jboss.portal.common.transaction.TransactionManagerProvider;
import org.jboss.portal.common.transaction.Transactions;
import org.jboss.portal.core.model.content.spi.ContentProviderRegistry;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.controller.coordination.CoordinationConfigurator;
import org.jboss.portal.server.deployment.PortalWebApp;
import org.jboss.portal.server.deployment.jboss.AbstractDeploymentFactory;
import org.jboss.portal.server.deployment.jboss.Deployment;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cache.global.ICacheService;
import org.osivia.portal.core.deploiement.IParametresPortailDeploymentManager;
import org.osivia.portal.core.imports.ImportInterceptor;
import org.xml.sax.EntityResolver;



import javax.management.MBeanServer;
import javax.transaction.TransactionManager;

import java.io.File;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ParametresPortailDeploymentManager implements IParametresPortailDeploymentManager{


	/** . */
	protected PortalObjectContainer portalObjectContainer;

	/** . */
	protected ContentProviderRegistry contentProviderRegistry;

	/** . */
	protected EntityResolver portalObjectEntityResolver;

	/** . */
	protected CoordinationConfigurator coordinationConfigurator;
	
	static ICacheService cacheService;

	public void chargerParametres(File file, MBeanServer mbeanServer) {
		
		boolean portalImport = false;
		
		try {
		    
             cacheService =  Locator.findMBean(ICacheService.class,"osivia:service=Cache");
            
            
            TransactionManager tm = TransactionManagerProvider.JBOSS_PROVIDER
                    .getTransactionManager();
            
            
            
            
            Transactions.requiresNew(tm, new Transactions.Runnable()
            {
               public Object run() throws Exception
               {
            
                  // notify cluster
                   // must be included in a transaction
                  cacheService.setImportRunning(true);
                  ImportInterceptor.isImportRunningLocally = true;
                   
                  return true;
               }
            });
            
            // waiting for cluster notification
            Thread.sleep(1000L);
            
             
			ImportInterceptor.isPageImportTerminated = false;
			ImportInterceptor.isPortalImportTerminated = false;
			
			// Wait for pendings transaction to stop (max 10 sec)
			int nbTries = 0;
			while (ImportInterceptor.nbPendingRequest > 1 && nbTries < 10)	{
				Thread.sleep(1000L);
				nbTries++;
			}
			
			
			
			tm = TransactionManagerProvider.JBOSS_PROVIDER
					.getTransactionManager();
			ParametresPortailDeployment deploiement = new ParametresPortailDeployment(
					file.toURL(), mbeanServer, tm, this);
			portalImport = deploiement.doStart();
			
			
			
			
			//Impact sur les caches du bandeau
			cacheService.incrementHeaderCount();
			
		
				
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally	{
			if( portalImport)
				ImportInterceptor.isPortalImportTerminated = true;
			else
				ImportInterceptor.isPageImportTerminated = true;
		}
	}

	public EntityResolver getPortalObjectEntityResolver() {
		return portalObjectEntityResolver;
	}

	public void setPortalObjectEntityResolver(
			EntityResolver portalObjectEntityResolver) {
		this.portalObjectEntityResolver = portalObjectEntityResolver;
	}

	public PortalObjectContainer getPortalObjectContainer() {
		return portalObjectContainer;
	}

	public void setPortalObjectContainer(
			PortalObjectContainer portalObjectContainer) {
		this.portalObjectContainer = portalObjectContainer;
	}

	public ContentProviderRegistry getContentProviderRegistry() {
		return contentProviderRegistry;
	}

	public void setContentProviderRegistry(
			ContentProviderRegistry contentProviderRegistry) {
		this.contentProviderRegistry = contentProviderRegistry;
	}

	public CoordinationConfigurator getCoordinationConfigurator() {
		return coordinationConfigurator;
	}

	public void setCoordinationConfigurator(
			CoordinationConfigurator coordinationConfigurator) {
		this.coordinationConfigurator = coordinationConfigurator;
	}
}
