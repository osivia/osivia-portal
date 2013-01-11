
package org.osivia.portal.core.portlets.interceptors;

import org.jboss.portal.portlet.invocation.PortletInvocation;
import org.jboss.portal.portlet.invocation.RenderInvocation;
import org.jboss.portal.portlet.invocation.ResourceInvocation;
import org.jboss.portal.portlet.PortletInvokerInterceptor;
import org.jboss.portal.portlet.aspects.portlet.cache.ContentRef;
import org.jboss.portal.portlet.aspects.portlet.cache.StrongContentRef;
import org.jboss.portal.portlet.invocation.response.FragmentResponse;
import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;
import org.jboss.portal.portlet.invocation.response.ResponseProperties;
import org.jboss.portal.portlet.invocation.response.RevalidateMarkupResponse;
import org.jboss.portal.portlet.invocation.response.ContentResponse;
import org.jboss.portal.portlet.StateString;
import org.jboss.portal.portlet.ParametersStateString;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.spi.UserContext;
import org.jboss.portal.portlet.cache.CacheControl;
import org.jboss.portal.portlet.cache.CacheScope;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.common.util.ParameterMap;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.PortalObjectPath.CanonicalFormat;
import org.jboss.portal.core.model.portal.portlet.WindowContextImpl;
import org.jboss.portal.WindowState;
import org.jboss.portal.Mode;
import org.nuxeo.runtime.model.ContributionFragmentRegistry.Fragment;
import org.osivia.portal.api.cache.services.ICacheService;
import org.osivia.portal.core.mt.CacheEntry;
import org.osivia.portal.core.pagemarker.PageMarkerUtils;
import org.osivia.portal.core.portalobjects.DynamicPersistentWindow;
import org.osivia.portal.core.portalobjects.DynamicWindow;
import org.osivia.portal.core.tracker.ITracker;



import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

/**
 * Cache markup on the portal.
 *

 */
public class ConsumerCacheInterceptor extends PortletInvokerInterceptor
{

	private ITracker tracker;
	private org.osivia.portal.api.cache.services.ICacheService cacheService;	
	
	public ITracker getTracker() {
		return tracker;
	}
	public void setTracker(ITracker tracker) {
		this.tracker = tracker;
	}
	
	public ICacheService getServicesCacheService() {
		return cacheService;
	}

	public void setServicesCacheService(ICacheService cacheService) {
		this.cacheService = cacheService;
	}	

	
   public static Map<String, CacheEntry> globalWindowCaches = new Hashtable<String, CacheEntry>();
	
   public PortletInvocationResponse invoke(PortletInvocation invocation) throws IllegalArgumentException, PortletInvokerException
   {
      // Compute the cache key
      String scopeKey = "cached_markup." + invocation.getWindowContext().getId();
      
  
      
      
      // We use the principal scope to avoid security issues like a user loggedout seeing a cached entry
      // by a previous logged in user
      UserContext userContext = invocation.getUserContext();
      
      ControllerContext ctx = (ControllerContext) invocation.getAttribute("controller_context");

      //
      if (invocation instanceof RenderInvocation)
      {
    	  
    	     

    	  
    	     // Affichage timeout	
    	     if( ctx != null && "1".equals(ctx.getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.timeout")))	{
    	    	 
    	 		StringWriter sw = new StringWriter();
    			PrintWriter pw = new PrintWriter(sw);

    			pw.println("<p align=\"center\">");
    			pw.println("	Délai expiré <br/> [<a href=\"javascript:location.reload(true)\">Recharger</a>]");
    			pw.println("</p>");


    	 	 
    	    	  return new FragmentResponse(
    	    	         null,
    	    	         new HashMap<String, Object>(),
    	    	         "text/plain",
    	    	         null,
    	    	         sw.toString(),
    	    	         null,
    	    	         new CacheControl(0, CacheScope.PRIVATE, null),
    	    	         null);
    	   } 	  
    	  
    	  
    	  
         RenderInvocation renderInvocation = (RenderInvocation)invocation;
         
         String windowCreationPageMarker = null;
         
         // Correction JSS 06932012 v1.0.6 : test du type de contexte
         
         Window window = null;
         
         if( invocation.getWindowContext() instanceof WindowContextImpl)	{
        	 
        	 // In case of dynamicWindows with same name, 
        	 // we must make sure it is the same window
        	 // before serving cache
         
        	 String windowId = invocation.getWindowContext().getId();
        	 PortalObjectId poid = PortalObjectId.parse(windowId, PortalObjectPath.CANONICAL_FORMAT);
        	 window = (Window) ctx.getController().getPortalObjectContainer().getObject(poid);
		 
	 
		 	if( window instanceof DynamicWindow)	{
		 		DynamicWindow dynaWIndow = ((DynamicWindow) window);
		 		if( dynaWIndow.isSessionWindow())
		 			windowCreationPageMarker = ((DynamicWindow) window).getDynamicWindowBean().getInitialPageMarker();
		 	}
         }


         //
         StateString navigationalState = renderInvocation.getNavigationalState();
         Map<String, String[]> publicNavigationalState = renderInvocation.getPublicNavigationalState();
         WindowState windowState = renderInvocation.getWindowState();
         Mode mode = renderInvocation.getMode();

         //
         CacheEntry cachedEntry = (CacheEntry)userContext.getAttribute(scopeKey);
         boolean globalCache = false;
         
         
         // v 1.0.13 : Cache anonyme sur la page d'accueil
          if( cachedEntry == null && (ctx != null && "1".equals(ctx.getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.useGlobalWindowCaches"))))	{
         	  cachedEntry = globalWindowCaches.get(invocation.getWindowContext().getId());
        	 globalCache = true;
        }

         //
         if (cachedEntry != null)
         {
            // Check time validity for fragment
            boolean useEntry = false;
            StateString entryNavigationalState = cachedEntry.navigationalState;
            Map<String, String[]> entryPublicNavigationalState = cachedEntry.publicNavigationalState;

            // Then check nav state equality
            if (navigationalState == null)
            {
               if (entryNavigationalState == null)
               {
                  useEntry = true;
               }
               else if (entryNavigationalState instanceof ParametersStateString)
               {
                  // We consider a parameters state string empty equivalent to a null value
                  useEntry = ((ParametersStateString)entryNavigationalState).getSize() == 0;
               }
            }
            else if (entryNavigationalState == null)
            {
               if (navigationalState instanceof ParametersStateString)
               {
                  useEntry = ((ParametersStateString)navigationalState).getSize() == 0;
               }
            }
            else
            {
               useEntry = navigationalState.equals(entryNavigationalState);
            }

            // Check public nav state equality
            if (useEntry)
            {
               if (publicNavigationalState == null)
               {
                  if (entryPublicNavigationalState == null)
                  {
                     //
                  }
                  else
                  {
                     useEntry = entryPublicNavigationalState.size() == 0;
                  }
               }
               else if (entryPublicNavigationalState == null)
               {
                  useEntry = publicNavigationalState.size() == 0;
               }
               else
               {
                  ParameterMap publicPM = ParameterMap.wrap(publicNavigationalState);
                  ParameterMap entryPM = ParameterMap.wrap(entryPublicNavigationalState);
                  useEntry = publicPM.equals(entryPM);
               }
            }

            
            if (useEntry)
            {
            	// Avoid dynamic windows with same name
            	if( windowCreationPageMarker != null )	{
            		if( !windowCreationPageMarker.equals(cachedEntry.creationPageMarker))
            			useEntry = false;
            	} 
            }
            
            // Then check window state equality
            useEntry &= windowState.equals(cachedEntry.windowState);

            // Then check mode equality
            useEntry &= mode.equals(cachedEntry.mode);

            // Clean if it is null
            if (!useEntry)
            {
               cachedEntry = null;
               userContext.setAttribute(scopeKey, null);
            }
         }
         


         ContentResponse fragment = cachedEntry != null ? cachedEntry.contentRef.getContent() : null;

         // If no valid fragment we must invoke
         if (fragment == null || cachedEntry.expirationTimeMillis < System.currentTimeMillis() || cachedEntry.creationTimeMillis < getServicesCacheService().getCacheInitialisationTs())
         {
            // Set validation token for revalidation only we have have a fragment
            if (fragment != null)
            {
               renderInvocation.setValidationToken(cachedEntry.validationToken);
            }

            // Invoke
            PortletInvocationResponse response = super.invoke(invocation);

            // Try to cache any fragment result
            CacheControl control = null;
            if (response instanceof ContentResponse)
            {
               fragment = (ContentResponse)response;
               control = fragment.getCacheControl();
            }
            else if (response instanceof RevalidateMarkupResponse)
            {
               RevalidateMarkupResponse revalidate = (RevalidateMarkupResponse)response;
               control = revalidate.getCacheControl();
            }

            // Compute expiration time, i.e when it will expire
            long expirationTimeMillis = 0;
            String validationToken = null;
            if (control != null)
            {
               if (control.getExpirationSecs() == -1)
               {
                  expirationTimeMillis = Long.MAX_VALUE;
               }
               else if (control.getExpirationSecs() > 0)
               {
                  expirationTimeMillis = System.currentTimeMillis() + control.getExpirationSecs() * 1000;
               }
               if (control.getValidationToken() != null)
               {
                  validationToken = control.getValidationToken();
               }
               else if (cachedEntry != null)
               {
                  validationToken = cachedEntry.validationToken;
               }
            }

            // Cache if we can
            if (expirationTimeMillis > 0)
            {
            	
            	ContentResponse cacheFragment = fragment;
            	
            	if( fragment instanceof FragmentResponse)	{
            		
            		
            		FragmentResponse orig = (FragmentResponse) fragment;
            		
            		Map<String, Object> filterAttributes = new HashMap<String, Object>();
            		
            		// Filtre des atttributs devant etre persistés dans le cache
            		
            		filterAttributes.put("osivia.emptyResponse", orig.getAttributes().get("osivia.emptyResponse"));
            		filterAttributes.put("osivia.menuBar", orig.getAttributes().get("osivia.menuBar"));
            		filterAttributes.put("osivia.portletPath", orig.getAttributes().get("osivia.portletPath"));
            	
            	    cacheFragment = new FragmentResponse(orig.getProperties(),   filterAttributes, orig.getContentType(), orig.getBytes(), orig.getChars(), orig.getTitle(), orig.getCacheControl(), orig.getNextModes());
            	}
            	
            	
               CacheEntry cacheEntry = new CacheEntry(
                  navigationalState,
                  publicNavigationalState,
                  windowState,
                  mode,
                  cacheFragment,
                  expirationTimeMillis,
                  validationToken,
                  windowCreationPageMarker);
               
               
               userContext.setAttribute(scopeKey, cacheEntry);
               
               
               
               // For other users
               if(  "1".equals(ctx.getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.useGlobalWindowCaches")))	{
            	   
            	   HttpServletRequest request = ctx.getServerInvocation().getServerContext().getClientRequest();            	   
            	   
            	   // On controle que l'état permet une mise dans le cache global
            	   
            	   if( navigationalState == null
            			   && (publicNavigationalState == null || publicNavigationalState.size() == 0) 
            			   && (windowState == null || WindowState.NORMAL.equals(windowState))
            			   && (mode == null || Mode.VIEW.equals(mode))
            			   && (window != null && ( window instanceof DynamicPersistentWindow))
            			   // Pas de cache sur les deconnexions
            			   && (request.getCookies() == null)
            		)	{
            	   
          		   
            		   CacheEntry initCacheEntry = new CacheEntry(
                           navigationalState,
                           publicNavigationalState,
                           windowState,
                           mode,
                           fragment,
                           System.currentTimeMillis() + 30 * 1000, // 10 sec.
                           null,
                           null);
                         userContext.setAttribute(scopeKey, cacheEntry);
                        

            	   
                        globalWindowCaches.put(invocation.getWindowContext().getId(), initCacheEntry);
            	   }
               }

            }

            //
            return response;
         }
         else
         {
            // Use the cached fragment
        	 if( fragment instanceof FragmentResponse){
        		 
        		 
        		 FragmentResponse fr = (FragmentResponse) fragment;
        		 
        		 String updatedFragment = fr.getChars();
        		 ResponseProperties updateProperties = fr.getProperties();
        		 
        		 
        		 boolean fragmentUpdated = false;
        		 
        		 if(fr.getChars() != null)	{
        			 // Gestion du cache partagé
        			 
        			 if( globalCache){
        				 HttpServletRequest request = ctx.getServerInvocation().getServerContext().getClientRequest();
        				 if( request.getSession() != null)	{
        					 if( request.getCookies() == null){
        						 //Premier affichage : on remplace le portalsessionid
        						 updatedFragment = updatedFragment.replaceAll(";portalsessionid=([a-zA-Z0-9.]*)",";portalsessionid="+request.getSession().getId());
        						 fragmentUpdated = true;
        					 }	else	{
        						 // Déconnexion : on onleve le portalsessionid
        						 updatedFragment = updatedFragment.replaceAll(";portalsessionid=([a-zA-Z0-9.]*)","");
        						 fragmentUpdated = true;        						 
        					 }
        				 }
        				 
        			 }
        			 
            	// Actualisation des markers de page
        			 
        		 if( fr.getChars().indexOf("/pagemarker/") != -1)	{
        			// String pageMarker = (String) ctx.getAttribute(Scope.REQUEST_SCOPE, "currentPageMarker");
        			 String pageMarker = PageMarkerUtils.getCurrentPageMarker(ctx);

        			 updatedFragment =  updatedFragment.replaceAll("/pagemarker/([0-9]*)/","/pagemarker/"+pageMarker+"/");
        			 fragmentUpdated = true;
              		 
        		  }
        		 
        		 if( fragmentUpdated)
               		 return new FragmentResponse(updateProperties,   fr.getAttributes(), fr.getContentType(), fr.getBytes(), updatedFragment, fr.getTitle(), fr.getCacheControl(), fr.getNextModes());
        			 
        		 }
         	 }
        		  
            return fragment;
         }
      }
      /*
      else
      {
         // Invalidate
         userContext.setAttribute(scopeKey, null);

         // Invoke
         return super.invoke(invocation);
      }*/
      
      return super.invoke(invocation);
   }
 

  
}
