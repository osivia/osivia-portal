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

package org.osivia.portal.core.mt;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;




public class KillersThreadsPool {

    private static KillersThreadsPool INSTANCE = null;
    
    int poolSize = 5;
    
    int maxPoolSize = 20;
 
    long keepAliveTime = 30;
 
    public static KillersThreadsPool getInstance() {
        if (INSTANCE == null) {
            INSTANCE = createInstance();
        }
        return INSTANCE;
    }

    public static synchronized KillersThreadsPool createInstance() {
        if (INSTANCE == null) {
            INSTANCE = new KillersThreadsPool();
        }
        return INSTANCE;
    }
    
    public static synchronized void cancelInstance() {
    	if( INSTANCE != null){
    		INSTANCE.pool.purge();
    		INSTANCE = null;
    	}
            
        return;
    }    
    
    protected ThreadPoolExecutor pool;
    
    final ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>( maxPoolSize);


    private KillersThreadsPool() {
       pool =  new ThreadPoolExecutor(poolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS, queue);
    }
    
    public Future execute( Runnable service)	throws Exception {
        return pool.submit(service);
    }

}