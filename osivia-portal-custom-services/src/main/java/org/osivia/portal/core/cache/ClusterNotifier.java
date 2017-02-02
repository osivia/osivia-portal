package org.osivia.portal.core.cache;

import java.io.ByteArrayOutputStream;

import javax.naming.InitialContext;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.osivia.portal.core.cache.global.ICacheService;
import org.osivia.portal.core.imports.ImportCheckerDatas;
import org.osivia.portal.core.imports.ImportCheckerNode;


/**
 * this class is used to notify the cache cluster in an asynchronous way
 * 
 * it allows to notify of a change in a separated transaction
 * 
 * BE CAREFULL : in a main transaction, all the calls to the cache service must be made by this way
 * 
 * for imports only !!!
 * 
 * @author Jean-Sébastien Steux
 *
 */
public class ClusterNotifier implements Runnable {
    
    public enum ACTION {
        RUNNING_IMPORT, STOPPING_IMPORT, INCREMENT_COUNTER, START_CHECKPAGE, REGISTERPAGE, END_CHECKPAGE;
       }

    ICacheService cacheService;
    boolean importRunning = false;
    ACTION action;
    Object param;
    
    protected static final Log logger = LogFactory.getLog(ClusterNotifier.class);
    
    public ClusterNotifier(ICacheService cacheService, ACTION action) {
        super();
        this.cacheService = cacheService;
        this.action = action;
    }

    
    public ClusterNotifier(ICacheService cacheService, ACTION action, Object param ) {
        super();
        this.cacheService = cacheService;
        this.action = action;
        this.param = param;
    }

  

    

  
    public void run() {
        UserTransaction tx = null;
        boolean transactionBegin = false;        

        try {

            InitialContext ctx = new InitialContext();
            tx = (UserTransaction) ctx.lookup("UserTransaction");


            if (tx.getStatus() == Status.STATUS_NO_TRANSACTION) {

                tx.begin();
                transactionBegin = true;                
            }
            
            if( action == ACTION.RUNNING_IMPORT)    {
                cacheService.setImportRunning(true);
            }
            if( action == ACTION.STOPPING_IMPORT)
                cacheService.setImportRunning(false);
            if( action == ACTION.INCREMENT_COUNTER)
                cacheService.incrementHeaderCount();
            if( action == ACTION.START_CHECKPAGE)   {
                String portalCheckObject = (String) param;
                String digest = cacheService.generateControlKey(portalCheckObject);
 
                ImportCheckerDatas datas = new ImportCheckerDatas(portalCheckObject,digest);
                datas.setChecking(true);
                cacheService.setImportCheckerDatas(datas);
               }
            if( action == ACTION.REGISTERPAGE)  {
                ImportCheckerDatas checkDatas = cacheService.getImportCheckerDatas();
                checkDatas.getNodes().add((ImportCheckerNode) param);
                cacheService.setImportCheckerDatas(checkDatas);
             }
            
            if( action == ACTION.END_CHECKPAGE)   {
                ImportCheckerDatas checkDatas = cacheService.getImportCheckerDatas();
                checkDatas.setChecking(false);
                cacheService.setImportCheckerDatas(checkDatas);
            }

           
            
           
            if( transactionBegin)
                tx.commit();

        } catch (Exception e) {

            try {
                if (transactionBegin)
                    tx.rollback();
            } catch (Exception e2) {
                logger.error(e2);
            }

            logger.error(e);
        }




    }

}
