package org.jboss.portal.core.controller.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;

import javax.portlet.ResourceResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.common.io.IOTools;
import org.jboss.portal.common.util.MultiValuedPropertyMap;
import org.jboss.portal.server.ServerInvocationContext;

/**
 * Response that sends a response to the http layer.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 13937 $
 */
public abstract class HTTPResponse extends HandlerResponse
{

   public abstract void sendResponse(ServerInvocationContext ctx) throws IOException, ServletException;

   public static HTTPResponse sendRedirect(final String redirect)
   {
      return new HTTPResponse()
      {
         public void sendResponse(ServerInvocationContext ctx) throws IOException
         {
            HttpServletResponse resp = ctx.getClientResponse();
            String absoluteRedirection;
            if (StringUtils.startsWith(redirect, "/")) {
                String host = ctx.getClientRequest().getHeader("osivia-virtual-host");
                if (StringUtils.isEmpty(host)) {
                    // TODO log d'erreur
                    absoluteRedirection = redirect;
                } else {
                    absoluteRedirection = host + redirect;
                }
            } else {
                absoluteRedirection = redirect;
            }
            resp.sendRedirect(absoluteRedirection);
         }
      };
   }

   public static HTTPResponse sendBinary(final String contentType, final long lastModified, final MultiValuedPropertyMap<String> properties, final InputStream in)
   {
      return new HTTPResponse()
      {
         public void sendResponse(ServerInvocationContext ctx) throws IOException
         {
            HttpServletResponse resp = ctx.getClientResponse();

            //
            resp.setContentType(contentType);

            //
            if (lastModified > 0)
            {
               resp.addDateHeader("Last-Modified", lastModified);
            }
            
            if (properties != null)
            {
            	for (String key: properties.keySet())
            	{
            		if (properties.getValue(key) != null)
            		{
                		if (key.equals(ResourceResponse.HTTP_STATUS_CODE)) {
            				resp.setStatus(Integer.parseInt(properties.getValue(key)));
            			} else {
            				resp.setHeader(key, properties.getValue(key));
            			}
            		}
            	}
            }

            //
            ServletOutputStream sout = null;
            try
            {
               sout = resp.getOutputStream();
               IOTools.copy(in, sout);
            }
            finally
            {
               IOTools.safeClose(in);
               IOTools.safeClose(sout);
            }
         }
      };
   }

   public static HTTPResponse sendBinary(final String contentType, final long lastModified, final MultiValuedPropertyMap<String> properties, final Reader reader)
   {
      return new HTTPResponse()
      {
         public void sendResponse(ServerInvocationContext ctx) throws IOException
         {
            HttpServletResponse resp = ctx.getClientResponse();

            //
            resp.setContentType(contentType);

            //
            if (lastModified > 0)
            {
               resp.addDateHeader("Last-Modified", lastModified);
            }

            if (properties != null)
            {
            	for (String key: properties.keySet())
            	{
            		if (properties.getValue(key) != null)
            		{
            			if (key.equals(ResourceResponse.HTTP_STATUS_CODE)) {
            				resp.setStatus(Integer.parseInt(properties.getValue(key)));
            			} else {
            				resp.setHeader(key, properties.getValue(key));
            			}
            		}
            	}
            }

            //
            Writer writer = null;
            try
            {
               writer = resp.getWriter();
               IOTools.copy(reader, writer);
            }
            finally
            {
               IOTools.safeClose(reader);
               IOTools.safeClose(writer);
            }
         }
      };
   }

   public static HTTPResponse sendForbidden()
   {
      return sendStatus(HttpServletResponse.SC_FORBIDDEN, null);
   }

   public static HTTPResponse sendNotFound()
   {
      return sendStatus(HttpServletResponse.SC_NOT_FOUND, null);
   }

   public static HTTPResponse sendError()
   {
      return sendStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, null);
   }

   public static HTTPResponse sendForbidden(String message)
   {
      return sendStatus(HttpServletResponse.SC_FORBIDDEN, message);
   }

   public static HTTPResponse sendNotFound(String message)
   {
      return sendStatus(HttpServletResponse.SC_NOT_FOUND, message);
   }

   public static HTTPResponse sendError(String message)
   {
      return sendStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
   }

   private static HTTPResponse sendStatus(final int statusCode, final String message)
   {
      return new HTTPResponse()
      {
         public void sendResponse(ServerInvocationContext ctx) throws IOException
         {
            HttpServletResponse resp = ctx.getClientResponse();
            if (message == null)
            {
               resp.sendError(statusCode);
            }
            else
            {
               resp.sendError(statusCode, message);
            }
         }
      };
   }
}
