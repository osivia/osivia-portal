package org.osivia.portal.core.transactions;

import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.transaction.IPostcommitResource;
import org.osivia.portal.api.transaction.ITransactionResource;
import org.osivia.portal.api.transaction.ITransactionService;
import org.osivia.portal.core.tracker.TrackerService;

/**
 * @author Jean-Sébastien
 */
public class TransactionServiceImpl implements ITransactionService {


    private Log log = LogFactory.getLog(TrackerService.class);
    
    private static ThreadLocal<TransactionBean> transactionBeanLocal = new ThreadLocal<TransactionBean>();
    
    private TransactionBean getTransactionBean(){
        
        TransactionBean bean = transactionBeanLocal.get();
 
        return bean;
    }
    
    
    @Override
    public void register(String resourceId, ITransactionResource resource) throws PortalException {
        
        TransactionBean transaction = getTransactionBean();
        if( transaction == null)
            throw new PortalException("Transaction must be started to unroll");
        
        transaction.register(resourceId, resource);
    }

    @Override
    public ITransactionResource getResource(String resourceId) {
        TransactionBean transaction = getTransactionBean();
        if( transaction == null)
            return null;
        
        return transaction.getResources().get(resourceId);
    }

    @Override
    public boolean isStarted() {
        TransactionBean currentTransation = getTransactionBean();
        if( currentTransation != null)
            return true;
        return false;
    }

    @Override
    public void commit() {
        TransactionBean transaction = getTransactionBean();
        if( transaction == null)
            throw new RuntimeException("Transaction must be started to commit");
       
        for(Entry<String, ITransactionResource> resource: transaction.getResources().entrySet())    {
            resource.getValue().commit();
        }

        transactionBeanLocal.set(null);
        
        for(IPostcommitResource resource: transaction.getPostcommitResources())    {
            resource.run();
        }
        

    }

    @Override
    public void rollback() {
        TransactionBean transaction = getTransactionBean();
        if( transaction == null)
            throw new RuntimeException("Transaction must be started to rollback");
       
        for(Entry<String, ITransactionResource> resource: transaction.getResources().entrySet())    {
            resource.getValue().rollback();
        }
        transactionBeanLocal.set(null);
    }


    @Override
    public void begin()  {
        if( getTransactionBean() != null)
            throw new RuntimeException("Transaction already started");
        TransactionBean bean = new TransactionBean( );
        transactionBeanLocal.set(bean);
    }
    

    @Override
    public void cleanTransactionContext()  {
        try {
            if (getTransactionBean() != null) {
                log.info("no explicit commit -> rollback");
                rollback();
            }
        } finally {
            transactionBeanLocal.set(null);
        }

    }


    @Override
    public void registerPostcommit(IPostcommitResource resource) throws PortalException {
        TransactionBean transaction = getTransactionBean();
        if(!isStarted())    {
           // No transaction : execute immediatly
            resource.run();
        } else
        transaction.registerPostcommitResource(resource);
    }

}
