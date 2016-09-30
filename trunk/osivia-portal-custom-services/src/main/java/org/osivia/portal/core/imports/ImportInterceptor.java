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
package org.osivia.portal.core.imports;

import java.util.concurrent.Future;

import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.server.ServerInterceptor;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.core.cache.ClusterNotifier;
import org.osivia.portal.core.cache.global.ICacheService;
import org.osivia.portal.core.mt.ThreadsPool;

/**
 * Susupension des requetes pendant les imports (à positionner avant la gestion
 * des transactions)
 */

public class ImportInterceptor extends ServerInterceptor {

    public static boolean isImportRunningLocally = false;

    public static boolean isPageImportTerminated = false;
    public static boolean isPortalImportTerminated = false;

    public static int nbPendingRequest = 0;

    protected ICacheService cacheService;

    public ICacheService getCacheService() {
        return cacheService;
    }

    public void setCacheService(ICacheService cacheService) {
        this.cacheService = cacheService;
    }

    protected void invoke(ServerInvocation invocation) throws Exception, InvocationException {


        if (getCacheService().isImportRunning() == true) {

            while (getCacheService().isImportRunning() == true) {
                Thread.sleep(1000L);
            }
        }

        try {
            nbPendingRequest++;

            invocation.invokeNext();
        } finally {

            if (nbPendingRequest > 0)
                nbPendingRequest--;

        

        if (isImportRunningLocally && (isPageImportTerminated || isPortalImportTerminated)) {

            // Wait after the commit for asynchronous updates
            // (to avoid JCA exception from container)

            if (isPortalImportTerminated)
                Thread.sleep(15000L);
            else
                Thread.sleep(3000L);

            // Eventual loops will be ignored for the next import
            nbPendingRequest = 0;

            // Import is terminated ant transaction is committed
            // release other threads
            isImportRunningLocally = false;
            
            
            ThreadsPool.getInstance().execute(new ClusterNotifier(cacheService, ClusterNotifier.ACTION.STOPPING_IMPORT));

            // Release cluster nodes
            //getCacheService().setImportRunning(false);

        }
        }

    }
}
