package org.jboss.portal.core.model.portal;

import org.jboss.portal.common.util.MarkupInfo;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.response.ThrowableResponse;
import org.jboss.portal.core.controller.handler.AbstractResponseHandler;
import org.jboss.portal.core.controller.handler.HTTPResponse;
import org.jboss.portal.core.controller.handler.HandlerResponse;
import org.jboss.portal.core.controller.handler.ResponseForward;
import org.jboss.portal.core.controller.handler.ResponseHandlerException;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.core.model.portal.command.response.UpdateWindowResponse;
import org.jboss.portal.core.theme.PageRendition;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerInvocationContext;
import org.jboss.portal.web.ServletContextDispatcher;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequestWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 10845 $
 */
public class PortalObjectResponseHandler extends AbstractResponseHandler
{

   public HandlerResponse processCommandResponse(ControllerContext controllerContext, ControllerCommand ceommand, ControllerResponse controllerResponse) throws ResponseHandlerException
   {
      if (controllerResponse instanceof UpdateWindowResponse)
      {
         UpdateWindowResponse uwmr = (UpdateWindowResponse)controllerResponse;
         PortalObjectContainer portalObjectContainer = controllerContext.getController().getPortalObjectContainer();
         PortalObject window = portalObjectContainer.getObject(uwmr.getWindowId());
         Page page = (Page)window.getParent();

         // We can do that safely as we know that only this class is responsible for evaluating UpdatePageResponse objects
         controllerResponse = new UpdatePageResponse(page.getId());
      }

      //
      if (controllerResponse instanceof UpdatePageResponse)
      {
         try
         {
            UpdatePageResponse uvr = (UpdatePageResponse)controllerResponse;
            RenderPageCommand rpc = new RenderPageCommand(uvr.getPageId());
            ControllerResponse resp = controllerContext.execute(rpc);

            //
            if (resp instanceof PageRendition)
            {
               final PageRendition rendition = (PageRendition)resp;
               final ServerInvocation invocation = controllerContext.getServerInvocation();
               return new HTTPResponse()
               {
                  public void sendResponse(ServerInvocationContext ctx) throws IOException, ServletException
                  {
                     HttpServletRequestWrapper request = new HttpServletRequestWrapper(invocation.getServerContext().getClientRequest()) {
                        @Override
                        public Locale getLocale()
                        {
                           return invocation.getRequest().getLocale();
                        }

                        @Override
                        public Enumeration<Locale> getLocales()
                        {
                           return null;
                        }

                     };
                	 ServletContextDispatcher dispatcher = new ServletContextDispatcher(request, invocation.getServerContext().getClientResponse(), invocation.getRequest().getServer().getServletContainer());
                     MarkupInfo markupInfo = (MarkupInfo)invocation.getResponse().getContentInfo();
                     rendition.render(markupInfo, dispatcher);
                  }
               };
            }
            else
            {
               // Another kind of response is not interpretable
               return new ResponseForward(resp);
            }
         }
         catch (ControllerException e)
         {
            return new ResponseForward(new ThrowableResponse(e));
         }
      }

      //
      return null;
   }
}
