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
     * register a post commit resource
     * 
     * @return
     */
    
    public void registerPostcommit( IPostcommitResource resource) throws PortalException;
    
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
    public void begin();
    
    
    /**
     * commits the  resources associated with the current transaction
     */
    public void commit();

    /**
     * rollback the resource associated with the current transaction
     */
    public void rollback();


    /**
     * Check if transaction has ended.
     */
    void cleanTransactionContext();




    
}
