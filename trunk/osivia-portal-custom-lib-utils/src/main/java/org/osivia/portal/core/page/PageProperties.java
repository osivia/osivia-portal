package org.osivia.portal.core.page;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;


/**
 * Gestion multi-threads des pages et des windows
 *  
 *  Cette classe permet 
 *   - d'optimiser l'acces aux propriétés de la page en mode multi-threads (en partageant les propriétés entre les threads)
 *   - d'enrichir les contextes d'affichage car les contextes de windows sont opaque (pas possibilité de les modifier avant le renderer)
 *  
 * @author jeanseb
 *
 */
public class PageProperties {
	
	public PageProperties() {
		super();
	}


	public PageProperties(Map<String, Map<String, String>> properties, Map<String,String> pageMap) {
		super();
		this.windowProperties = properties;
		this.pageMap = pageMap;
	}
	
	protected static final Log logger = LogFactory.getLog(PageProperties.class);
	
	public static ThreadLocal<PageProperties> localProperties = new ThreadLocal<PageProperties>();

	public static PageProperties getProperties()	{
		if( localProperties.get() == null)	{
			localProperties.set(new PageProperties());
		}
	return localProperties.get();
	}

	// v2.0.6 : ajout rafraichissement utilisateur sur la requete
	private boolean refreshingPage = false;
	
	
	public boolean isRefreshingPage() {
		// TODO : a finir d'implémenter
//			return false;
		
		if( parent != null)
			return parent.isRefreshingPage();
		
		return refreshingPage;
	}


	public void setRefreshingPage(boolean pageRefresh) {
		if( parent != null)	
			parent.refreshingPage = pageRefresh;
		else
			this.refreshingPage = pageRefresh;
	}

	

	


    public  Map<String, String> getPagePropertiesMap()	{
		
		Map<String, String> pageProperties;
		if( parent == null)
			pageProperties =  this.pageMap;
		else
			pageProperties =  parent.pageMap;
		
	
		return pageProperties;
		
	}
	
	private Map<String, String> pageMap = null;
	
	private PageProperties parent;
	
	public static void createThreadContext( PageProperties parent){

		PageProperties newBean = new PageProperties( );
		newBean.parent = parent;
		localProperties.set(newBean);
	}

	// JSS v 1.0.10 : Map must be thread-safe
	
	private Map<String, Map<String, String>> windowProperties = null;
	//private Map<String, Map<String, String>> windowProperties = new HashMap<String, Map<String,String>>();
	
	public String getWindowProperty( String windowId, String propertyName){
		if( parent != null)
			return parent.getWindowProperty(windowId, propertyName);
		if( windowProperties.get(windowId) != null)	{
			return  windowProperties.get(windowId).get(propertyName);
		}	else
			return null;
	}

	public void setWindowProperty( String windowId, String propertyName, String propertyValue){
		
		
		if( parent != null)	{
			
			parent.setWindowProperty(windowId, propertyName, propertyValue);
			
			//if( logger.isDebugEnabled())
			//	logger.debug("windowId: " +windowId+ " name:" + propertyName + " value:" +propertyValue);
			
			return;
		}
		
		if( windowProperties.get(windowId) == null)	{
			// JSS v 1.0.10 : Map must be thread-safe
			//windowProperties.put(windowId, new HashMap<String, String>());
			windowProperties.put(windowId, new Hashtable<String, String>());

		}
		
		if( propertyValue != null)
			windowProperties.get(windowId).put( propertyName, propertyValue);
	}
	
	
	// Pour les rendersets
	private String currentWindowId;

	public String getCurrentWindowId() {
		return currentWindowId;
	}

	public void setCurrentWindowId(String currentWindowId) {
		this.currentWindowId = currentWindowId;
	}
	
	
	public void init()	{
		localProperties.set(new PageProperties(new Hashtable<String, Map<String,String>>(), new Hashtable<String, String>()));

	}
	
}
