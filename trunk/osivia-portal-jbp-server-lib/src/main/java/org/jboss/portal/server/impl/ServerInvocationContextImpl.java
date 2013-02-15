/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
package org.jboss.portal.server.impl;

import org.jboss.portal.common.invocation.AbstractInvocationContext;
import org.jboss.portal.common.invocation.resolver.RequestAttributeResolver;
import org.jboss.portal.common.net.media.MediaType;
import org.jboss.portal.common.text.CharBuffer;
import org.jboss.portal.common.text.FastURLEncoder;
import org.jboss.portal.common.util.ParameterMap;
import org.jboss.portal.server.PortalConstants;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerInvocationContext;
import org.jboss.portal.server.ServerURL;
import org.jboss.portal.server.impl.invocation.SessionAttributeResolver;
import org.jboss.portal.server.request.URLContext;
import org.jboss.portal.server.request.URLFormat;
import org.jboss.portal.web.Body;
import org.jboss.portal.web.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 11136 $
 */
public class ServerInvocationContextImpl extends AbstractInvocationContext implements ServerInvocationContext
{

   /** The fast url encoder. */
   private static final FastURLEncoder urlEncoder = FastURLEncoder.getUTF8Instance();

   /** The client request. */
   private final HttpServletRequest req;

   /** The client request. */
   private final WebRequest webReq;

   /** The client response. */
   private final HttpServletResponse resp;

   /** The portal context path. */
   private String portalContextPath;

   /** The portal request path. */
   private String portalRequestPath;

   /** The portal host. */
   private String portalHost;

   /** The url context. */
   private URLContext urlContext;

   /** . */
   private Buffer[] buffers;

   /** . */
   private final String requestRelativePrefix;

   /** . */
   private final String requestPrefix;

   public ServerInvocationContextImpl(
      HttpServletRequest req,
      HttpServletResponse resp,
      WebRequest webReq,
      String portalHost,
      String portalRequestPath,
      String portalContextPath,
      URLContext urlContext)
   {
      if (req == null)
      {
         throw new IllegalArgumentException();
      }
      if (resp == null)
      {
         throw new IllegalArgumentException();
      }

      //
      this.req = req;
      this.webReq = webReq;
      this.resp = resp;
      this.portalRequestPath = portalRequestPath;
      this.portalContextPath = portalContextPath;
      this.portalHost = portalHost;
      this.urlContext = urlContext;
      this.buffers = new Buffer[16];

      //
      StringBuffer requestRelativePrefix = new StringBuffer();
      requestRelativePrefix.append(req.getScheme()).append("://").append(req.getServerName());
      if (req.isSecure())
      {
         if (req.getServerPort() != 443)
         {
            requestRelativePrefix.append(":").append(Integer.toString(req.getServerPort()));
         }
      }
      else if (req.getServerPort() != 80)
      {
         requestRelativePrefix.append(":").append(Integer.toString(req.getServerPort()));
      }
      requestRelativePrefix.append(req.getContextPath());

      //
      this.requestRelativePrefix = requestRelativePrefix.toString();
      this.requestPrefix = req.getContextPath();

      //
      addResolver(ServerInvocation.REQUEST_SCOPE, new RequestAttributeResolver(req));
      addResolver(ServerInvocation.SESSION_SCOPE, new SessionAttributeResolver(req, PortalConstants.PORTAL_SESSION_MAP_KEY, false));
      addResolver(ServerInvocation.PRINCIPAL_SCOPE, new SessionAttributeResolver(req, PortalConstants.PORTAL_PRINCIPAL_MAP_KEY, true));
   }

   public WebRequest getWebRequest()
   {
      return webReq;
   }

   public HttpServletRequest getClientRequest()
   {
      return req;
   }

   public HttpServletResponse getClientResponse()
   {
      return resp;
   }

   public String getMediaType()
   {
      MediaType mediaType = webReq.getMediaType();
      if(mediaType == null)
      {
         return null;
      }
      return mediaType.getValue();
   }

   public URLContext getURLContext()
   {
      return urlContext;
   }

   public ParameterMap getQueryParameterMap()
   {
      return ParameterMap.wrap(webReq.getQueryParameterMap());
   }

   public ParameterMap getBodyParameterMap()
   {
      Body body = webReq.getBody();

      //
      if (body instanceof Body.Form)
      {
         return ParameterMap.wrap(((Body.Form)body).getParameters());
      }

      //
      return null;
   }

   public String getPortalRequestPath()
   {
      return portalRequestPath;
   }

   public String getPortalContextPath()
   {
      return portalContextPath;
   }

   public String getPortalHost()
   {
      return portalHost;
   }

   public String renderURL(ServerURL url, URLContext context, URLFormat format)
   {
	   // Modif PIA
	   /*
      int index = context.getMask() << 2 | format.getMask();
      if (buffers[index] == null)
      {
         buffers[index] = new Buffer(resp, context, format);
      }
      Buffer buffer = buffers[index];
      */
	   // TODO : a optimiser (non multi-threads)
      
      Buffer buffer = new Buffer(resp, context, format);
      return buffer.toString(url);
   }

   public class Buffer extends CharBuffer
   {

      /** . */
      private final HttpServletResponse resp;

      /** . */
      private final URLFormat format;

      /** . */
      private final int prefixLength;

      public Buffer(HttpServletResponse resp, URLContext context, URLFormat format)
      {
         this.resp = resp;
         this.format = format;

         //
         if (!format.isRelative())
         {
            append(requestRelativePrefix);
         }
         else
         {
            append(requestPrefix);
         }

         // Append the servlet path
         switch (context.getMask())
         {
            case URLContext.AUTH_MASK + URLContext.SEC_MASK:
               append("/authsec");
               break;
            case URLContext.AUTH_MASK:
               append("/auth");
               break;
            case URLContext.SEC_MASK:
               append("/sec");
               break;
         }

         // Save the prefix length
         this.prefixLength = length;
      }

      public String toString(ServerURL url)
      {
         // Reset the prefix length
         this.length = prefixLength;

         // julien : check UTF-8 is ok and should not be dependant on the response charset
         append(url.getPortalRequestPath());

         //
         boolean first = true;
         for (Iterator i = url.getParameterMap().entrySet().iterator(); i.hasNext();)
         {
            Map.Entry parameter = (Map.Entry)i.next();
            String name = (String)parameter.getKey();
            String[] values = (String[])parameter.getValue();
            for (int j = 0; j < values.length; j++)
            {
               String value = values[j];
               append(first ? '?' : '&');
               append(name, urlEncoder);
               append('=');
               append(value, urlEncoder);
               first = false;
            }
         }

         // Stringify
         String s = asString();

         // Let the servlet rewrite the URL if necessary
         if (format.isServletEncoded())
         {
        	 synchronized (resp){
        		 	s = resp.encodeURL(s);
        	 }
         }

         //
         return s;
      }
   }
}

