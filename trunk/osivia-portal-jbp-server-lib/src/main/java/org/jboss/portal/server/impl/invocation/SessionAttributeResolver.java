/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2008, Red Hat Middleware, LLC, and individual                    *
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
package org.jboss.portal.server.impl.invocation;

import org.jboss.portal.common.invocation.AttributeResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Set;
import java.util.HashSet;
import java.util.Enumeration;
import java.util.Collections;
import java.security.Principal;

/**
 * correction JSS : ne pas ignorer les portal.principal meme quand l'utilisateur est connecté
 * 
 */
public class SessionAttributeResolver implements AttributeResolver
{

   /** . */
   protected final HttpServletRequest req;

   /** . */
   protected final String prefix;
   
   protected String anonymousPrefix;

   public SessionAttributeResolver(
      HttpServletRequest req,
      String prefix,
      boolean principalScoped)
   {
      if (req == null)
      {
         throw new IllegalArgumentException();
      }
      if (prefix == null)
      {
         throw new IllegalArgumentException();
      }

      //
      if (principalScoped)
      {
         Principal principal = req.getUserPrincipal();
         if (principal != null)
         {
        	anonymousPrefix = prefix;
            prefix = prefix + principal.getName();
         }
      }

      //
      this.req = req;
      this.prefix =  prefix;
   }

   public Set getKeys()
   {
      HttpSession session = req.getSession(false);

      //
      if (session == null)
      {
         return Collections.EMPTY_SET;
      }

      //
      Set keys = new HashSet();
      for (Enumeration e = session.getAttributeNames();e.hasMoreElements();)
      {
         String key = (String)e.nextElement();

         //
         if (key.startsWith(prefix))
         {
            keys.add(key);
         }
      }

      //
      return keys;
   }

   public Object getAttribute(Object o) throws IllegalArgumentException
   {
      HttpSession session = req.getSession(false);

      //
      if (session == null)
      {
         return null;
      }

      Object returnValue = session.getAttribute(prefix + o);
      
      if( returnValue == null) {
    	 if( anonymousPrefix != null)	{
    		 // JSS v2 : 
    		 // lorsque j'accède à une page CMS créée en mode non connectée
    		 // je dois pouvoir la récupérer lorsque je me connecte, sinon elle n'est pas visible dans le PortalObjectContainer
    		 // et peut faire  planter
    		 
    		 
    		 // peut-etre faudrait-il généraliser à toutes les données, mais impacts difficiles à évaluer
    		 
    		 
    		 // Scenario : 
    		 // - affichage d'un blog dynamique en mode anonyme
    		 // - lien depuis ce blog (dans zone HTML) vers une note protégée qui sera affichée en navigation PORTLET
    		 // - redirection vers mire de connection
    		 // - génére un plantage
    		 
    		 
    		 if( o.toString().endsWith("_CMS_LAYOUT"))
    		 	returnValue = session.getAttribute(anonymousPrefix + o);
    	 }
      }
      
      return returnValue;
   }

   public void setAttribute(Object o, Object o1) throws IllegalArgumentException
   {
      req.getSession().setAttribute(prefix + o, o1);
   }
}