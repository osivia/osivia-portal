
package org.osivia.portal.core.page;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.jboss.portal.common.util.ParameterMap;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.command.mapper.AbstractCommandFactory;
import org.jboss.portal.server.ServerInvocation;


public class MonEspaceCommandFactoryService extends AbstractCommandFactory 
{

   public ControllerCommand doMapping(ControllerContext controllerContext, ServerInvocation invocation, String host,
                                      String contextPath, String requestPath)
   {
      String portal = null;
      ParameterMap parameterMap = controllerContext.getServerInvocation().getServerContext().getQueryParameterMap();
      if (parameterMap != null)
      {
         try
         {
            if (parameterMap.get("portal") != null)
            {
               portal = URLDecoder.decode(parameterMap.get("portal")[0], "UTF-8");
            }
         }
         catch (UnsupportedEncodingException e)
         {
            // ignore
         }
      }

      if (portal == null)
      {
         return new MonEspaceCommand();
      }
      else
      {
         return new MonEspaceCommand(portal);
      }
   }

}

