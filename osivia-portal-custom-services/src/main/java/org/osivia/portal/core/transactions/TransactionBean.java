package org.osivia.portal.core.transactions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osivia.portal.api.transaction.IPostcommitResource;
import org.osivia.portal.api.transaction.ITransactionResource;

/**
 * Store datas associated to the current transaction
 * 
 * @author Jean-Sébastien
 */
public class TransactionBean {

    /**
     * registered resources
     */
    private Map<String, ITransactionResource> resources = new HashMap<String, ITransactionResource>();


    
    /**
     * postcommit
     */
    private List<IPostcommitResource> postcommits = new ArrayList<IPostcommitResource>();



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
    
    /**
     * add a postcommit
     * 
     * @param resourceId
     * @param resource
     */
    public void registerPostcommitResource(IPostcommitResource postcommit) {
        postcommits.add(postcommit);
    }
   
    
    /**
     * Getter for postcommits.
     * @return the postcommits
     */
    public List<IPostcommitResource> getPostcommitResources() {
        return postcommits;
    }
}
