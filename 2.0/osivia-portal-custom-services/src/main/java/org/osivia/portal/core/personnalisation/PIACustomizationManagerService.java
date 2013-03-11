package org.osivia.portal.core.personnalisation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.impl.model.CustomizationManagerService;
import org.jboss.portal.core.impl.model.content.portlet.PortletContent;
import org.jboss.portal.core.model.content.Content;
import org.jboss.portal.core.model.instance.Instance;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.identity.User;
import org.osivia.portal.core.page.PageCustomizerInterceptor;


public class PIACustomizationManagerService extends CustomizationManagerService {
	
	protected static final Log logger = LogFactory.getLog(PIACustomizationManagerService.class);

	public Instance getInstance(Window window, User user) throws IllegalArgumentException
	   {
		
		//long debut = System.currentTimeMillis();
		
	      if (window == null)
	      {
	         throw new IllegalArgumentException("No window provided");
	      }

	      //
	      Content content = window.getContent();

	      //
	      String instanceId = ((PortletContent)content).getInstanceRef();
	      if (instanceId == null)
	      {
	         return null;
	      }

	      // Get the instance
	      Instance instance = getInstanceContainer().getDefinition(instanceId);
	      if (instance != null)
	      {
	         // If we are in the context of an existing user we get a customization for that user
	         if (user != null)
	         {
	            String userId = user.getUserName();

	            /*
	            // And if it is in a dashboard context we get the per window customization
	            if (isDashboard(window, user))
	            {
	               // That's how we manufacture dash board keys
	               String dashboardId = window.getId().toString();

	               //
	               instance = instance.getCustomization(dashboardId);
	            }
	            else
	            {
	            */
	            	
	    			//long inter = System.currentTimeMillis();
	    			
	    			//logger.debug("PIACustomizationManagerService int : " + (inter -debut));
	    		            	
	            	
	            	String personnalisationId =  window.getProperty("osivia.idPerso");
	            	
	            	if( personnalisationId != null)
	            		instance = instance.getCustomization(personnalisationId + ":" +userId);	  
	            	
	            	// NON PERFORMANT : !!! voir avec JBoss
	            	// Pour l'instant on positionne de manière explicite
	            	//else
	            	//	instance = instance.getCustomization(userId);
	           // }
	         }
	      }
			//long fin = System.currentTimeMillis();
			
			//logger.debug("PIACustomizationManagerService durée : " + (fin -debut));
	      
	      //
	      return instance;
	   }	

}
