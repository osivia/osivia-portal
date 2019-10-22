package org.osivia.portal.api.transaction;

import javax.transaction.UserTransaction;

import org.osivia.portal.api.PortalException;

/**
 * @author Jean-Sébastien
 */
public interface ITransactionService {

    final static String MBEAN_NAME = "osivia:service=TransactionService";

    
    /**
     * register a resource
     * 
     * @return
     */
    
    public void register(String resourceId, ITransactionResource resource) throws PortalException;
    
    
    /**
     * return an existing resource
     * 
     * @return
     */
    
    public ITransactionResource getResource(String resourceId) ;
    
    
    /**
     * indicates if the transaction is started (and not finished)
     * 
     * @return
     */
    public boolean isStarted() ;
    
    /**
     *start the current transaction
     */
    public void begin() throws PortalException;
    
    
    /**
     * commits the  resources associated with the current transaction
     */
    public void commit() throws PortalException;

    /**
     * rollback the resource associated with the current transaction
     */
    public void rollback() throws PortalException;
    

    /**
     * init the transaction state
     */
    public void initThreadTx();
    
}
