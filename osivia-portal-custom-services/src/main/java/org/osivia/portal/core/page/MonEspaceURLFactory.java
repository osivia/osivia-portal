
package org.osivia.portal.core.page;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.command.SignOutCommand;
import org.jboss.portal.core.controller.command.mapper.URLFactoryDelegate;
import org.jboss.portal.server.AbstractServerURL;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerURL;




public class MonEspaceURLFactory extends URLFactoryDelegate
{


   private String path;

   public ServerURL doMapping(ControllerContext controllerContext, ServerInvocation invocation, ControllerCommand cmd)
   {
      if (cmd == null)
      {
         throw new IllegalArgumentException("No null command accepted");
      }
      if (cmd instanceof MonEspaceCommand)
      {
         
    	  MonEspaceCommand accueilCommand = (MonEspaceCommand)cmd;
    	  
         AbstractServerURL asu = new AbstractServerURL();
         asu.setPortalRequestPath(path);
         
         String portal = accueilCommand.getPortalName();
         if (portal != null)
         {
            try
            {
               asu.setParameterValue("portal", URLEncoder.encode(portal, "UTF-8"));
            }
            catch (UnsupportedEncodingException e)
            {
               // ignore
            }
         }
         
        
         return asu;
      }
      return null;
   }

   public String getPath()
   {
      return path;
   }

   public void setPath(String path)
   {
      this.path = path;
   }

}

