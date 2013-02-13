
package org.osivia.portal.core.cms;

import org.jboss.portal.common.util.ParameterMap;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.command.SignOutCommand;
import org.jboss.portal.core.controller.command.mapper.AbstractCommandFactory;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.urls.WindowPropertiesEncoder;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;


public class CmsCommandFactoryService extends AbstractCommandFactory implements CmsCommandFactory
{
	
   public static final String PORTAL_NAME = "/_PN_/"; 
   public static final String DOC_ID = "/_ID_/"; 
   
	ICMSService cmsService ;
	
	

	public ICMSService getCMSService () throws Exception	{
		if( cmsService == null)
			cmsService  = Locator.findMBean(ICMSService.class,"osivia:service=NuxeoService");
		
		return cmsService;
	}
	


   public ControllerCommand doMapping(ControllerContext controllerContext, ServerInvocation invocation, String host,
                                      String contextPath, String requestPath)
   {
      String cmsPath = null;
      String pagePath = null;
      Map<String, String> pageParams = null;
      String contextualization = null;
  	 String displayContext = null;
	 String hideMetaDatas  = null;
	 String scope  = null;
	 String displayLiveVersion = null;
	 String windowPermReference = null;	 
	 String addToBreadcrumb = null;
	 String portalPersistentName = null;
	 
	 String toAnalize = requestPath;
	 
	 
	 ParameterMap parameterMap = controllerContext.getServerInvocation().getServerContext().getQueryParameterMap();
	 
	 
	 
	 /* Check if url is encoded by external system */
	 
	 Map<String, String> externalCMSCommandProperties = null;
	 
	 CMSServiceCtx cmsContext = new CMSServiceCtx();
	 cmsContext.setServerInvocation(invocation);	
	 
	 Map<String, String> requestParams= new HashMap<String, String>();
	 for( String key : parameterMap.keySet())	{
		 String value[] = parameterMap.get(key);
		 if( value != null && value.length == 1)
			  try
         {
			 requestParams.put(key, URLDecoder.decode(value[0], "UTF-8"));
         }
	         catch (UnsupportedEncodingException e)
	         {
	            // ignore
	         }			 
	 }
	 
	 
	 try {
		 externalCMSCommandProperties = getCMSService().parseCMSURL(cmsContext, requestPath, requestParams);
	} catch (Exception e) {
		throw new RuntimeException(e);
	}
	
	
	if( externalCMSCommandProperties != null){
		String commandPath =  externalCMSCommandProperties.get("cmsPath");
		return new CmsCommand( null, commandPath, null, null,  null, null, null, null, null, null, null);
	}
	
	
	/* Standard decoding */
	
	toAnalize = requestPath;
	 
	 if( toAnalize.startsWith(DOC_ID))	{
		 if( toAnalize.indexOf('/', DOC_ID.length()) > 0)
			 cmsPath = toAnalize.substring(DOC_ID.length(), toAnalize.indexOf('/', DOC_ID.length()));
		 else
			 cmsPath = toAnalize.substring(DOC_ID.length());
		 toAnalize = toAnalize.substring(DOC_ID.length() + cmsPath.length());
	 } 
	 
	
	 if( toAnalize.startsWith((PORTAL_NAME)))	{
		 portalPersistentName = toAnalize.substring(PORTAL_NAME.length(), toAnalize.indexOf('/', PORTAL_NAME.length()));
		 toAnalize = toAnalize.substring(PORTAL_NAME.length() + portalPersistentName.length());
	 } 
	 
	 
	 // No path, get all url
	 if( cmsPath == null)	 
		 cmsPath = toAnalize;

      if (parameterMap != null)
      {
         try
         {
            if (parameterMap.get("cmsPath") != null)
            {
            	cmsPath = URLDecoder.decode(parameterMap.get("cmsPath")[0], "UTF-8");
            }
         }
         catch (UnsupportedEncodingException e)
         {
            // ignore
         }
         
         try
         {
            if (parameterMap.get("pagePath") != null)
            {
            	pagePath = URLDecoder.decode(parameterMap.get("pagePath")[0], "UTF-8");
            }
         }
         catch (UnsupportedEncodingException e)
         {
            // ignore
         }         
         
         try
         {
            if (parameterMap.get("cmsPath") != null)
            {
            	cmsPath = URLDecoder.decode(parameterMap.get("cmsPath")[0], "UTF-8");
            }
         }
         catch (UnsupportedEncodingException e)
         {
            // ignore
         }    
         
         try
         {
            if (parameterMap.get("pageParams") != null)
            {
            	String sPageParms = URLDecoder.decode(parameterMap.get("pageParams")[0], "UTF-8");
            	pageParams =  WindowPropertiesEncoder.decodeProperties(sPageParms);
            }
         }
         catch (UnsupportedEncodingException e)
         {
            // ignore
         }  
         
         try
         {
            if (parameterMap.get("contextualization") != null)
            {
            	contextualization = URLDecoder.decode(parameterMap.get("contextualization")[0], "UTF-8");
            }
         }
         catch (UnsupportedEncodingException e)
         {
            // ignore
         }   
         
         

         try
         {
            if (parameterMap.get("displayContext") != null)
            {
            	displayContext = URLDecoder.decode(parameterMap.get("displayContext")[0], "UTF-8");
            }
         }
         catch (UnsupportedEncodingException e)
         {
            // ignore
         }   
          
         

         try
         {
            if (parameterMap.get("hideMetaDatas") != null)
            {
            	hideMetaDatas = URLDecoder.decode(parameterMap.get("hideMetaDatas")[0], "UTF-8");
            }
         }
         catch (UnsupportedEncodingException e)
         {
            // ignore
         }       
         
 
         
         try
         {
            if (parameterMap.get("scope") != null)
            {
            	scope = URLDecoder.decode(parameterMap.get("scope")[0], "UTF-8");
            }
         }
         catch (UnsupportedEncodingException e)
         {
            // ignore
         }       
         
         try
         {
            if (parameterMap.get("displayLiveVersion") != null)
            {
            	displayLiveVersion = URLDecoder.decode(parameterMap.get("displayLiveVersion")[0], "UTF-8");
            }
         }
         catch (UnsupportedEncodingException e)
         {
            // ignore
         }      
         
         try
         {
            if (parameterMap.get("windowPermReference") != null)
            {
            	windowPermReference = URLDecoder.decode(parameterMap.get("windowPermReference")[0], "UTF-8");
            }
         }
         catch (UnsupportedEncodingException e)
         {
            // ignore
         }      
         

         try
         {
            if (parameterMap.get("addToBreadcrumb") != null)
            {
            	addToBreadcrumb = URLDecoder.decode(parameterMap.get("addToBreadcrumb")[0], "UTF-8");
            }
         }
         catch (UnsupportedEncodingException e)
         {
            // ignore
         }      
         
    

  
         
            

             
      }


         return new CmsCommand( pagePath, cmsPath, pageParams, contextualization,  displayContext, hideMetaDatas, scope, displayLiveVersion, windowPermReference, addToBreadcrumb, portalPersistentName);

   }

}

