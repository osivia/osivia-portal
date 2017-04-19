/*
 * (C) Copyright 2014 Académie de Rennes (http://www.ac-rennes.fr/), OSIVIA (http://www.osivia.com) and others.
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
package org.osivia.portal.core.imports;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.cache.TreeCacheMBean;
import org.jgroups.stack.IpAddress;
import org.osivia.portal.core.cache.CacheService;
import org.osivia.portal.core.cache.ClusterNotifier;
import org.osivia.portal.core.cache.global.ICacheService;
import org.osivia.portal.core.mt.ThreadsPool;


/**
 * This class synchronizes imports check code
 * 
 * @author jeanseb
 * 
 */

public class AsyncImportCheckerClientThread implements Runnable {

    private static Log logger = LogFactory.getLog(AsyncImportCheckerClientThread.class);



    private ICacheService cacheService;
    private String portalObjectToCheckOnCluster;

    public AsyncImportCheckerClientThread(ICacheService cacheService, String portalObjectToCheckOnCluster) {
        super();

        this.cacheService = cacheService;
        this.portalObjectToCheckOnCluster = portalObjectToCheckOnCluster;
    }


    public void run() {


        try {

            // Wait for the current import to terminate and commit
            Thread.sleep(1000L);
            
            // STart check
            ThreadsPool.getInstance().execute(new ClusterNotifier(cacheService,  ClusterNotifier.ACTION.START_CHECKPAGE, portalObjectToCheckOnCluster));

            
            TreeCacheMBean treeCache = ((CacheService) cacheService).getPiaTreeCache();
            int nbMembers = treeCache.getMembers().size();

            boolean elapsedTime = false;
            long begin = System.currentTimeMillis();
            
            // wait for the start process
            while (((cacheService.getImportCheckerDatas() == null || !cacheService.getImportCheckerDatas().isChecking()) ) && !elapsedTime) {
                long end = System.currentTimeMillis();
                if (end - begin > 20000L)
                    elapsedTime = true;
            }
            
            if( ! elapsedTime){
                begin = System.currentTimeMillis();

                // Wait for others nodes to terminate
                while ((cacheService.getImportCheckerDatas().getNodes().size() < nbMembers) && !elapsedTime) {

                    Thread.sleep(1000L);

                    long end = System.currentTimeMillis();
                    if (end - begin > 20000L)
                        elapsedTime = true;

                }
            }
            
            ThreadsPool.getInstance().execute(
                    new ClusterNotifier(cacheService, ClusterNotifier.ACTION.END_CHECKPAGE));
            
            // UI notification
            cacheService.stopCheckPortalObject();



        } catch (Exception e) {
            // logger.error(e);

            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stacktrace = sw.toString();
            logger.error(stacktrace);
        }

    }

}
