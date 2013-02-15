
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