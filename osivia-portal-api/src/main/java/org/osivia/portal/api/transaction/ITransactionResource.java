package org.osivia.portal.api.transaction;

import javax.transaction.UserTransaction;

/**
 * @author Jean-Sébastien
 */
public interface ITransactionResource {
    
    /**
     * @return
     */
    Object getInternalTransaction();
    
    /**
     * commits the internal resources associated with the current transaction
     */
    void commit();

    /**
     * rollback the internal resource associated with the current transaction
     */
    void rollback();
}
