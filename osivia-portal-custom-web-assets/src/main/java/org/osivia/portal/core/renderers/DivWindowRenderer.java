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
package org.osivia.portal.core.renderers;

import org.apache.commons.lang.StringEscapeUtils;
import org.jboss.dom4j.rule.Mode;
import org.jboss.portal.core.aspects.controller.PageCustomizerInterceptor;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.theme.render.AbstractObjectRenderer;
import org.jboss.portal.theme.render.RenderException;
import org.jboss.portal.theme.render.RendererContext;
import org.jboss.portal.theme.render.renderer.ActionRendererContext;
import org.jboss.portal.theme.render.renderer.WindowRenderer;
import org.jboss.portal.theme.render.renderer.WindowRendererContext;
import org.osivia.portal.core.page.PageProperties;


import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of a WindowRenderer, based on div tags.
 *
 * @author <a href="mailto:mholzner@novell.com>Martin Holzner</a>
 * @version $LastChangedRevision: 8784 $, $LastChangedDate: 2007-10-27 19:01:46 -0400 (Sat, 27 Oct 2007) $
 * @see org.jboss.portal.theme.render.renderer.WindowRenderer
 */
public class DivWindowRenderer extends AbstractObjectRenderer
   implements WindowRenderer
{
   public void render(RendererContext rendererContext, WindowRendererContext wrc) throws RenderException
   {
      PrintWriter out = rendererContext.getWriter();
      
  
      
      
      
      PageProperties properties = PageProperties.getProperties();
      
      // Pour les décorateurs
      properties.setCurrentWindowId(wrc.getId());
      
      
      String hidePortlet = properties.getWindowProperty(wrc.getId(), "osivia.hidePortlet");
      
      if( "1".equals(hidePortlet))
    	  return;
      

      
      // Activation des liens Ajax
      String ajaxLink = properties.getWindowProperty(wrc.getId(), "osivia.ajaxLink");
      if( ! "1".equals(ajaxLink) || "wizzard".equals(wrc.getProperty("osivia.windowSettingMode")))
    	  out.print("<div class=\"no-ajax-link\">");   	
      
      
      
      String style = properties.getWindowProperty(wrc.getId(), "osivia.style");
      
      if( style != null)	
    	  style = style.replaceAll(",", " ");
      else 
    	  style = "";
      
	
      
      
      /*
      if( style != null)
    	  out.print("<div id=\""+style+"\">");
    */	  
      
      
      out.print("<div class=\"portlet-container "+style+"\">");
      
      

      
      
      //if( "wizzard".equals(  properties.getWindowProperty(wrc.getId(), "osivia.windowSettingMode")))	{     
      if( "wizzard".equals(wrc.getProperty("osivia.windowSettingMode")))	{
       

      out.print("<div class=\"pia-window-settings-border\">");
      out.print("<div class=\"pia-window-settings\">");

      out.print("<a href=\""+wrc.getProperty("osivia.upUrl")+"\"><img src=\"/osivia-portal-custom-web-assets/images/arrow_up.png\" alt=\"Vers le haut\" border=0/></a>");
      out.print("&nbsp;");
      out.print("<a href=\""+wrc.getProperty("osivia.downUrl")+"\"><img src=\"/osivia-portal-custom-web-assets/images/arrow_down.png\" border=0/></a>");
      out.print("&nbsp;");
      out.print("<a href=\""+wrc.getProperty("osivia.previousRegionUrl")+"\"><img src=\"/osivia-portal-custom-web-assets/images/arrow_left.png\" border=0/></a>");
      out.print("&nbsp;");
      out.print("<a href=\""+wrc.getProperty("osivia.nextRegionUrl")+"\"><img src=\"/osivia-portal-custom-web-assets/images/arrow_right.png\" border=0/></a>");
      out.print("&nbsp;<img src=\"/osivia-portal-custom-web-assets/images/shading.png\" border=0/>&nbsp;");      
      out.print("<a href=\"#\" onClick=\""+wrc.getProperty("osivia.settingUrl")+"\"><img src=\"/osivia-portal-custom-web-assets/images/application_edit.png\" border=0/></a>");
      out.print("&nbsp;");
      Collection modesOrStates = wrc.getDecoration().getTriggerableActions(ActionRendererContext.MODES_KEY);
      for (Iterator i = modesOrStates.iterator(); i.hasNext();)	{
    	  ActionRendererContext action = (ActionRendererContext)i.next();
    	  if( "admin".equals(action.getName()) && action.isEnabled())	{
    		     out.print("<a href=\""+action.getURL()+"\"><img src=\"/osivia-portal-custom-web-assets/images/application_form.png\" border=0/></a>");
    		       		  
    	  }
         }      
      out.print("&nbsp;<img src=\"/osivia-portal-custom-web-assets/images/shading.png\" border=0/>&nbsp;");       
      out.print("<a href=\"#\" onClick=\""+wrc.getProperty("osivia.destroyUrl")+"\"><img src=\"/osivia-portal-custom-web-assets/images/cross.png\" border=0/></a>");

      out.print("</div>");
      out.print("</div>");
      }
      
      out.print("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");

     

      if(  "1".equals(properties.getWindowProperty(wrc.getId(), "osivia.displayTitle")))	{
      
 	  
    	  out.print("<tr><td class=\"portlet-titlebar-left\"></td>");
    	  out.print("<td class=\"portlet-titlebar-center\">");
    	  
    	  rendererContext.render(wrc.getDecoration());
    	  out.print("</td><td class=\"portlet-titlebar-right\"></td></tr>");
      }	

      //
      out.print("<tr><td class=\"portlet-content-left\"></td>");
      out.print("<td class=\"portlet-body\"><div class=\"portlet-content-center\">");
     

      if( "admin".equals(wrc.getMode().toString()))	{
    	  // Mode administration
    	  	out.print("<div id=\"admin-mode-background\">");
    	    out.print("<div id=\"admin-mode-window\">");
    	   
    	    // Url du mode VIEW
    	    String viewURL = "";
    	    Collection modesOrStates = wrc.getDecoration().getTriggerableActions(ActionRendererContext.MODES_KEY);
    	      for (Iterator i = modesOrStates.iterator(); i.hasNext();)	{
    	    	  ActionRendererContext action = (ActionRendererContext)i.next();
    	    	  if( "view".equals(action.getName()) )	{
    	    		  viewURL = action.getURL();
    	    	  }
    	         }      

    	    
    	      String title = properties.getWindowProperty(wrc.getId(), "osivia.title");
    	      if( title == null)
    	    	  title = "";
    	      
    	      String instanceName = wrc.getProperty("osivia.instanceDisplayName");
    	      title += "   ["+instanceName+"]";


    	      
    	    out.print("<div id=\"admin-mode-title\" onclick=\"location.href='"+viewURL+"';\"><img src=\"/osivia-portal-custom-web-assets/images/blank.png\"/><span class=\"title-settings\">Administration "+title+"</span></div>");
    	    out.print("<div id=\"admin-mode-content\">");
       }
      
     
      
      rendererContext.render(wrc.getPortlet());
      
      if( "admin".equals(wrc.getMode().toString()))	{
 	     out.print("</div>");
 	     out.print("</div>");
 	    out.print("</div>");
      }
      

      
      out.print("</div></td><td class=\"portlet-content-right\"></td></tr>");

      //
      out.print("<tr><td class=\"portlet-footer-left\"></td>");
      out.print("<td class=\"portlet-footer-center\"></td>");
      out.print("<td class=\"portlet-footer-right\"></td></tr>");
      out.print("</table></div>");
      
      //fin du style
      /*
      if( style != null)      
    	  out.print("</div>");
      */
      
      // Activation des liens Ajax
      if( ! "1".equals(ajaxLink) || "wizzard".equals(wrc.getProperty("osivia.windowSettingMode")))
    	     	  out.print("</div>"); 	  


   }
}
