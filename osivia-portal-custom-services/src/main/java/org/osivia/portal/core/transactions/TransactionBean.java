package org.osivia.portal.core.transactions;

import java.util.HashMap;
import java.util.Map;

import org.osivia.portal.api.transaction.ITransactionResource;

/**
 * @author Jean-Sébastien
 */
public class TransactionBean {

    /**
     * registered resources
     */
    private Map<String, ITransactionResource> resources = new HashMap<String, ITransactionResource>();


    /**
     * add a resource
     * 
     * @param resourceId
     * @param resource
     */
    public void register(String resourceId, ITransactionResource resource) {
        resources.put(resourceId, resource);
    }

    /**
     * get resources
     * 
     * @return
     */
    public Map<String, ITransactionResource> getResources() {
        return resources;
    }
}
