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
package org.osivia.portal.core.mt;

import org.jboss.portal.portlet.invocation.PortletInvocation;
import org.jboss.portal.portlet.invocation.RenderInvocation;
import org.jboss.portal.portlet.PortletInvokerInterceptor;
import org.jboss.portal.portlet.aspects.portlet.cache.ContentRef;
import org.jboss.portal.portlet.aspects.portlet.cache.StrongContentRef;
import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;
import org.jboss.portal.portlet.invocation.response.RevalidateMarkupResponse;
import org.jboss.portal.portlet.invocation.response.ContentResponse;
import org.jboss.portal.portlet.StateString;
import org.jboss.portal.portlet.ParametersStateString;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.spi.UserContext;
import org.jboss.portal.portlet.cache.CacheControl;
import org.jboss.portal.common.util.ParameterMap;
import org.jboss.portal.WindowState;
import org.jboss.portal.Mode;

import java.io.Serializable;
import java.util.Map;



   /**
    * Encapsulate cache information.
    */
   public class CacheEntry implements Serializable
   {

      /** The entry navigational state. */
	   public final StateString navigationalState;

      /** . */
      public final WindowState windowState;

      /** . */
      public final Mode mode;

      /** . */
      public final Map<String, String[]> publicNavigationalState;

      /** The timed content. */
      public final ContentRef contentRef;

      /** . */
      public final long expirationTimeMillis;
      
      public long creationTimeMillis;

      /** . */
      public final String validationToken;
      
      public final String creationPageMarker;

      public CacheEntry(
         StateString navigationalState,
         Map<String, String[]> publicNavigationalState,
         WindowState windowState,
         Mode mode,
         ContentResponse content,
         long expirationTimeMillis,
         String validationToken,
         String creationPageMarker)
      {
         if (expirationTimeMillis <= 0)
         {
            throw new IllegalArgumentException();
         }
         this.navigationalState = navigationalState;
         this.windowState = windowState;
         this.mode = mode;
         this.publicNavigationalState = publicNavigationalState;
         this.contentRef = new StrongContentRef(content);
         this.expirationTimeMillis = expirationTimeMillis;
         this.validationToken = validationToken;
         this.creationPageMarker = creationPageMarker;
         this.creationTimeMillis = System.currentTimeMillis();
      }

}
