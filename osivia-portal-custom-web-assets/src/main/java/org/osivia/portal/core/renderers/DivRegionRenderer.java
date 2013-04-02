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

import org.jboss.portal.theme.render.AbstractObjectRenderer;
import org.jboss.portal.theme.render.RenderException;
import org.jboss.portal.theme.render.RendererContext;
import org.jboss.portal.theme.render.renderer.RegionRenderer;
import org.jboss.portal.theme.render.renderer.RegionRendererContext;
import org.jboss.portal.theme.render.renderer.WindowRendererContext;

import java.io.PrintWriter;
import java.util.Iterator;

/**
 * Implementation of a Region renderer, based on div tags.
 *
 * @author <a href="mailto:mholzner@novell.com>Martin Holzner</a>
 * @author <a href="mailto:roy@jboss.org>Roy Russo</a>
 * @version $LastChangedRevision: 8784 $, $LastChangedDate: 2007-10-27 19:01:46 -0400 (Sat, 27 Oct 2007) $
 * @see org.jboss.portal.theme.render.renderer.RegionRenderer
 */
public class DivRegionRenderer extends AbstractObjectRenderer
   implements RegionRenderer
{

   public void renderHeader(RendererContext rendererContext, RegionRendererContext rrc) throws RenderException
   {
      PrintWriter markup = rendererContext.getWriter();
      markup.print("<div");

      if (rrc == null) // || region.getWindows().size() <= 0)
      {
         markup.print(" class='empty-region' />");
      }
      else if (rrc.getCSSId() != null)
      {
         markup.print(" id='");
         markup.print(rrc.getCSSId());
         markup.print("'>");
      }
 
      // Lien ajout portlet
      if( "pageTemplate".equals(rendererContext.getProperty("osivia.wizzardMode")))	{
    	  markup.print("<div>");
    	  markup.print("<a href=\"#\" onClick=\""+rendererContext.getProperty("osivia.addPortletUrl")+"\"><img src=\"/osivia-portal-custom-web-assets/images/application_add.png\" border=0/></a>");
    	  markup.print("</div>");            
      }
    
      if( "cmsEdition".equals(rendererContext.getProperty("osivia.wizzardMode")))	{
    	  markup.print("<div>");
    	  markup.print("<a class=\"fancyframe_refresh no-ajax-link\" href=\""+rendererContext.getProperty("osivia.addPortletUrl")+"\"> <img src=\"/osivia-portal-custom-web-assets/images/application_add.png\" border=0/></a>");
    	  markup.print("</div>");            
      }

      
      
   }

   public void renderBody(RendererContext rendererContext, RegionRendererContext rrc) throws RenderException
   {
      for (Iterator i = rrc.getWindows().iterator(); i.hasNext();)
      {
         WindowRendererContext wrc = (WindowRendererContext)i.next();
         rendererContext.render(wrc);
      }
   }

   public void renderFooter(RendererContext rendererContext, RegionRendererContext rrc) throws RenderException
   {
      PrintWriter markup = rendererContext.getWriter();
      markup.print("</div>");
   }
}
