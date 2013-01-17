
package org.osivia.portal.core.cache.services;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;




public class CacheThreadsPool {

    private static CacheThreadsPool INSTANCE = null;
    
    
	// v.1.0.21 : redimensionnement des pools
    int poolSize = 20;
    
    int maxPoolSize = 100;
 
    long keepAliveTime = 30;
 
    public static CacheThreadsPool getInstance() {
        if (INSTANCE == null) {
            INSTANCE = createInstance();
        }
        return INSTANCE;
    }

    public static synchronized CacheThreadsPool createInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CacheThreadsPool();
        }
        return INSTANCE;
    }
    
   
    // v 1.0.23 : shutdown à la place de purge
    public static synchronized void shutdown() {
    	if( INSTANCE != null){
    		INSTANCE.pool.shutdownNow();
    		INSTANCE = null;
    	}
   	
    }
    
    protected ThreadPoolExecutor pool;
    
    final ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>( maxPoolSize);


    private CacheThreadsPool() {
       pool =  new ThreadPoolExecutor(poolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS, queue);
    }
    
    public Future execute( Runnable service)	throws Exception {
        return pool.submit(service);
    }

}