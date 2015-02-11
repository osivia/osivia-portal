package org.osivia.portal.core.deploiement;

import java.util.concurrent.Callable;

import javax.transaction.TransactionManager;


import org.jboss.portal.common.transaction.TransactionManagerProvider;
import org.jboss.portal.common.transaction.Transactions;
import org.osivia.portal.core.cache.global.ICacheService;
import org.osivia.portal.core.imports.ImportInterceptor;


public class TransactionThread   implements Callable<String>  {
    
    ICacheService cacheService;

    public TransactionThread(ICacheService cacheService) {
        super();
        this.cacheService = cacheService;
        
    }

    public String call() throws Exception {
        
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
        
        return null;

    }

}
