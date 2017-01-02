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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import javax.naming.InitialContext;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.cache.TreeCacheMBean;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jgroups.stack.IpAddress;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cache.CacheService;
import org.osivia.portal.core.cache.ClusterNotifier;
import org.osivia.portal.core.cache.XMLGenerator;
import org.osivia.portal.core.cache.global.ICacheService;
import org.osivia.portal.core.mt.ThreadsPool;
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;

import com.thoughtworks.xstream.io.json.JsonWriter.Format;


/**
 * Synchronisation des imports : computing node side
 * 
 * @author jeanseb
 * 
 */

public class AsyncImportCheckerServerThread implements Runnable {

    private static Log logger = LogFactory.getLog(AsyncImportCheckerServerThread.class);

    private static long THREAD_DELAY = 10000L;


    public boolean stop = false;
    private long lastCheckTimestamp = 0L;
    private CacheService cacheService;

    public AsyncImportCheckerServerThread(CacheService cacheService) {
        super();
        this.cacheService = cacheService;
    }


    public void run() {

        while (!stop) {

            
            
            try {

                Thread.sleep(THREAD_DELAY);

                ImportCheckerDatas checkerData = cacheService.getImportCheckerDatas();

                if (checkerData != null && checkerData.isChecking()) {

                    if (checkerData.getCheckerTimestamp() != lastCheckTimestamp && checkerData.getPortalObjectId() != null) {

                        TreeCacheMBean treeCache = ((CacheService) cacheService).getHibernateTreeCache();

                        Object localAddress = treeCache.getLocalAddress();
                        String name = localAddress.toString();

                        if (localAddress instanceof IpAddress) {
                            name = ((org.jgroups.stack.IpAddress) localAddress).getIpAddress().getHostName();
                        }



                        UserTransaction tx = null;
                        boolean transactionBegin = false;


                        InitialContext ctx = new InitialContext();
                        tx = (UserTransaction) ctx.lookup("UserTransaction");


                        if (tx.getStatus() == Status.STATUS_NO_TRANSACTION) {
                            tx.begin();
                            transactionBegin = true;
                        }


                        String digest = "";

                        try {

                            digest = cacheService.generateControlKey(checkerData.getPortalObjectId());
                             
                            if (transactionBegin)
                                tx.commit();

                            
                        } catch (Exception e) {

                            try {
                                if (transactionBegin)
                                    tx.rollback();
                            } catch (Exception e2) {
                                logger.error(e2);
                            }

                            logger.error(e);
                        }



                        // Notify local digest to cluster

                        ThreadsPool.getInstance().execute(
                                new ClusterNotifier(cacheService, ClusterNotifier.ACTION.REGISTERPAGE, new ImportCheckerNode(name, digest)));

                        lastCheckTimestamp = checkerData.getCheckerTimestamp();
                    }
                }


            } catch (Exception e) {
                // logger.error(e);

                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                String stacktrace = sw.toString();
                logger.error(stacktrace);
            }

        }

    }

}
