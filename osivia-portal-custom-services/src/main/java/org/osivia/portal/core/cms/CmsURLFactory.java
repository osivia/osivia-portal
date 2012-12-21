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
package org.osivia.portal.core.cms;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.command.mapper.URLFactoryDelegate;
import org.jboss.portal.server.AbstractServerURL;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerURL;
import org.osivia.portal.core.urls.WindowPropertiesEncoder;



public class CmsURLFactory extends URLFactoryDelegate
{

   /** . */
   private String path;

   public ServerURL doMapping(ControllerContext controllerContext, ServerInvocation invocation, ControllerCommand cmd)
   {
      if (cmd == null)
      {
         throw new IllegalArgumentException("No null command accepted");
      }
      if (cmd instanceof CmsCommand)
      {
         CmsCommand cmsCommand = (CmsCommand)cmd;

         //
         AbstractServerURL asu = new AbstractServerURL();
         asu.setPortalRequestPath(path);
         String cmsPath = cmsCommand.getCmsPath();
         if (cmsPath != null)
         {
            try
            {
               asu.setParameterValue("cmsPath", URLEncoder.encode(cmsPath, "UTF-8"));
            }
            catch (UnsupportedEncodingException e)
            {
               // ignore
            }
         }
         
         String pagePath = cmsCommand.getPagePath();
         if (pagePath != null)
         {
            try
            {
               asu.setParameterValue("pagePath", URLEncoder.encode(pagePath, "UTF-8"));
            }
            catch (UnsupportedEncodingException e)
            {
               // ignore
            }
         }         
         
         Map<String, String> pageParams = cmsCommand.getPageParams();
         if (pagePath != null)
         {
            try
            {
               asu.setParameterValue("pageParams", WindowPropertiesEncoder.encodeProperties(pageParams));
            }
            catch (Exception e)
            {
               // ignore
            }
         }                  
         
         String contextualization = cmsCommand.getContextualization();
         if (contextualization != null)
         {
            try
            {
               asu.setParameterValue("contextualization",  URLEncoder.encode(contextualization, "UTF-8"));
            }
            catch (Exception e)
            {
               // ignore
            }
         }             
         
         String displayContext = cmsCommand.getDisplayContext();
         if (displayContext != null)
         {
            try
            {
               asu.setParameterValue("displayContext",  URLEncoder.encode(displayContext, "UTF-8"));
            }
            catch (Exception e)
            {
               // ignore
            }
         }             
         
         
         String hideMetaDatas = cmsCommand.getHideMetaDatas();
         if (hideMetaDatas != null)
         {
            try
            {
               asu.setParameterValue("hideMetaDatas",  URLEncoder.encode(hideMetaDatas, "UTF-8"));
            }
            catch (Exception e)
            {
               // ignore
            }
         }             
    
         
         String scope = cmsCommand.getScope();
         if (scope != null)
         {
            try
            {
               asu.setParameterValue("scope",  URLEncoder.encode(scope, "UTF-8"));
            }
            catch (Exception e)
            {
               // ignore
            }
         }     
         
         
         String displayLiveVersion = cmsCommand.getDisplayLiveVersion();
         if (displayLiveVersion != null)
         {
            try
            {
               asu.setParameterValue("displayLiveVersion",  URLEncoder.encode(displayLiveVersion, "UTF-8"));
            }
            catch (Exception e)
            {
               // ignore
            }
         }     
         
         String windowPermReference = cmsCommand.getWindowPermReference();
         if (windowPermReference != null)
         {
            try
            {
               asu.setParameterValue("windowPermReference",  URLEncoder.encode(windowPermReference, "UTF-8"));
            }
            catch (Exception e)
            {
               // ignore
            }
         }     
         
         String addToBreadcrumb = cmsCommand.getAddToBreadcrumb();
         if (addToBreadcrumb != null)
         {
            try
            {
               asu.setParameterValue("addToBreadcrumb",  URLEncoder.encode(addToBreadcrumb, "UTF-8"));
            }
            catch (Exception e)
            {
               // ignore
            }
         }     
         
   
         String portalPersistentName = cmsCommand.getPortalPersistentName();
         if (portalPersistentName != null)
         {
            try
            {
               asu.setParameterValue("portalPersistentName",  URLEncoder.encode(portalPersistentName, "UTF-8"));
            }
            catch (Exception e)
            {
               // ignore
            }
         }     
 
         
         return asu;
      }
      return null;
   }

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}



}

