/*
 * (C) Copyright 2014 OSIVIA (http://www.osivia.com) 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package org.osivia.portal.core.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.EmptyStackException;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jboss.cache.CacheException;
import org.jboss.cache.Fqn;

import org.jboss.cache.TreeCacheListener;
import org.jboss.cache.TreeCacheMBean;

import org.jboss.portal.common.http.HttpRequest;
import org.jboss.portal.common.xml.NullEntityResolver;
import org.jboss.portal.common.xml.XMLTools;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerInterceptor;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.security.AuthorizationDomainRegistry;
import org.jboss.system.ServiceMBeanSupport;
import org.osivia.portal.core.cache.global.CacheDatas;
import org.osivia.portal.core.cache.global.ICacheService;
import org.osivia.portal.core.imports.AsyncImportCheckerClientThread;
import org.osivia.portal.core.imports.AsyncImportCheckerServerThread;
import org.osivia.portal.core.imports.ImportCheckerDatas;
import org.osivia.portal.core.mt.ThreadsPool;
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;
import org.osivia.portal.core.tracker.ITracker;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;


/**
 * 
 * Gestionnaire de cache TreeCache pour synchroniser les noeuds
 * 
 * 
 * @author cap2j
 *
 */
@SuppressWarnings("unchecked")

public class CacheService extends ServiceMBeanSupport implements  ICacheService, Serializable {
	

	private static final long serialVersionUID = 3361892990187034534L;


	private Log log = LogFactory.getLog(CacheService.class);
	
	private TreeCacheMBean piaTreeCache;
	private TreeCacheMBean hibernateTreeCache;
    private AsyncImportCheckerServerThread asyncThread;
    private XMLGenerator generator;
    private boolean checking;
    
    protected PortalObjectContainer portalObjectContainer;
    protected IDynamicObjectContainer dynamicObjectContainer;
    protected AuthorizationDomainRegistry authorizationDomainRegistry;


    
    public IDynamicObjectContainer getDynamicObjectContainer() {
        return dynamicObjectContainer;
    }

    
    public void setDynamicObjectContainer(IDynamicObjectContainer dynamicObjectContainer) {
        this.dynamicObjectContainer = dynamicObjectContainer;
    }

    
    public AuthorizationDomainRegistry getAuthorizationDomainRegistry() {
        return authorizationDomainRegistry;
    }

    
    public void setAuthorizationDomainRegistry(AuthorizationDomainRegistry authorizationDomainRegistry) {
        this.authorizationDomainRegistry = authorizationDomainRegistry;
    }

    
    public void setPortalObjectContainer(PortalObjectContainer portalObjectContainer) {
        this.portalObjectContainer = portalObjectContainer;
    }

    ExecutorService executor;
	

	public TreeCacheMBean getPiaTreeCache() {
		return piaTreeCache;
	}

	public void setPiaTreeCache(TreeCacheMBean piaTreeCache) {
		this.piaTreeCache = piaTreeCache;
	}

	public TreeCacheMBean getHibernateTreeCache() {
		return hibernateTreeCache;
	}

	public void setHibernateTreeCache(TreeCacheMBean hibernateTreeCache) {
		this.hibernateTreeCache = hibernateTreeCache;
	}
		
	
    
    public PortalObjectContainer getPortalObjectContainer() {
        return portalObjectContainer;
    }

    
	private CacheDatas getCacheDatas() throws RuntimeException	{
		
		Fqn fqn = new Fqn("osivia");
		CacheDatas cache = null;

		 try {
			cache = (CacheDatas) getPiaTreeCache().get(fqn, "main");
		} catch (CacheException e) {
			throw new RuntimeException( e);
		}

		if( cache == null)
			cache = new CacheDatas();
		return cache;
	
	}
	
	private void setCacheDatas(CacheDatas cache) throws RuntimeException	{
		
		Fqn fqn = new Fqn("osivia");
	
		 try {
			 getPiaTreeCache().put(fqn, "main", cache);
		} catch (CacheException e) {
			throw new RuntimeException( e);
		}
	
	}
	
	
	public long getHeaderCount() {
		return getCacheDatas().getHeaderCount();
		
	}
	
	public void incrementHeaderCount( )	{
		CacheDatas cache = getCacheDatas();
		cache.setHeaderCount(cache.getHeaderCount()+1);
		setCacheDatas(cache);
		
	}
	
	public long getProfilsCount() {
		return getCacheDatas().getProfilsCount();
		
	}
	
	public void incrementProfilsCount( )	{
		CacheDatas cache = getCacheDatas();
		cache.setProfilsCount(cache.getProfilsCount()+1);
		setCacheDatas(cache);
		
	}


    public long getGlobalParametersCount() {
        return getCacheDatas().getGlobalParametersCount();

    }

    public void incrementGlobalParametersCount() {
        CacheDatas cache = getCacheDatas();
        cache.setGlobalParametersCount(cache.getGlobalParametersCount() + 1);
        setCacheDatas(cache);
    }


	public void startService() throws Exception {
		log.info("start service CacheService");
		
        asyncThread = new  AsyncImportCheckerServerThread( this);
        executor = Executors.newSingleThreadExecutor();
        executor.submit(asyncThread);
        
        
        
        generator = new XMLGenerator(getPortalObjectContainer(), getDynamicObjectContainer(), getAuthorizationDomainRegistry());

		
	}

    public void setImportRunning(boolean importRunning) {
        CacheDatas cache = getCacheDatas();
        cache.setImportRunning(importRunning);
        setCacheDatas(cache);

    }

    public boolean isImportRunning() {
        return getCacheDatas().isImportRunning();

    }
    
    public ImportCheckerDatas getImportCheckerDatas() {
        return getCacheDatas().getImportCheckerDatas();
    }
    
    public void setImportCheckerDatas(ImportCheckerDatas datas) {
       CacheDatas cache = getCacheDatas();
        cache.setImportCheckerDatas(datas);
        setCacheDatas(cache);
        
    }
	
	public void stopService() throws Exception {
		log.info("stop service CacheService");
        asyncThread.stop = true;
        // Arret du thread
        if (executor != null) {
            executor.shutdownNow();
        }
	}


    public void configExport(OutputStream output, PortalObject portalObject, String filter) throws Exception {
        generator.configExport(output, portalObject, filter);
    }

    
    
    public void startCheckPortalObject( String portalObjectToCheckOnCluster)  throws Exception  {
        
        
        this.checking = true;
        
        // Control thread
        ThreadsPool.getInstance().execute(new AsyncImportCheckerClientThread(this, portalObjectToCheckOnCluster));
        
    }
    
    
    public void stopCheckPortalObject( )  throws Exception  {
        this.checking = false;
             
    }
        
    
    
    /**
     * Computes an unique hashkey for the XML document
     *  - each element is hashed including its level
     *  - each child hash code is added to parent
     *
     * @param rootElt the root elt
     * @param level the level
     * @return the key
     */
    
    private long getKey( Node rootElt, long level) {
         
        long key = (long) rootElt.getNodeName().hashCode();
        
        if( rootElt.getNodeValue() != null)
            key += 10L * ((long) rootElt.getNodeValue().hashCode());
        
        key = key * level;
        

        NodeList nodeList = rootElt.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
               //calls this method for all the children which is Element
                key += getKey( currentNode, level+1);
 
        }
        
         
        return key;
    }
    
    
    
    /* (non-Javadoc)
     * @see org.osivia.portal.core.cache.global.ICacheService#generateControlKey(java.lang.String)
     */
    
    
    public String generateControlKey(String portalCheckObject) throws Exception   {
        

        getDynamicObjectContainer().startPersistentIteration();

        PortalObject po = getPortalObjectContainer().getObject(PortalObjectId.parse(portalCheckObject, PortalObjectPath.CANONICAL_FORMAT));

        // Compute a digest reference
        ByteArrayOutputStream os = new ByteArrayOutputStream(1000);
        configExport(os, po, null);
    
                  
        os.close();

        String digest = "";
        
        
        DocumentBuilder builder = XMLTools.getDocumentBuilderFactory().newDocumentBuilder();

        Document doc = builder.parse(new ByteArrayInputStream(os.toByteArray()));

        digest = Long.toString(getKey(doc.getDocumentElement(), 1));
        
        getDynamicObjectContainer().stopPersistentIteration();
        
        return digest;
    }



    public boolean isChecking() {
        return this.checking;
    }
}
