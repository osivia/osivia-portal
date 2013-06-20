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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

import org.dom4j.dom.DOMElement;
import org.dom4j.io.HTMLWriter;
import org.jboss.portal.theme.render.AbstractObjectRenderer;
import org.jboss.portal.theme.render.RenderException;
import org.jboss.portal.theme.render.RendererContext;
import org.jboss.portal.theme.render.renderer.RegionRenderer;
import org.jboss.portal.theme.render.renderer.RegionRendererContext;
import org.jboss.portal.theme.render.renderer.WindowRendererContext;
import org.osivia.portal.api.Constants;
import org.osivia.portal.core.formatters.IFormatter;

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

    /** Fancybox class, required for link. */
    private static final String CLASS_FANCYBOX = "fancybox_inline";
    /** Region commands class. */
    private static final String CLASS_COMMANDS = "commands";
    /** "Add" image source. */
    private static final String SRC_IMG_ADD = "/osivia-portal-custom-web-assets/images/application_add.png";

    /**
     * Render region header.
     * @param rendererContext renderer context
     * @param rrc region renderer context
     * @throws RenderException
     */	
   public void renderHeader(RendererContext rendererContext, RegionRendererContext rrc) throws RenderException
   {

        String language = rrc.getProperty("osivia.language");
        Locale locale = null;
        if (language != null) {
            locale = new Locale(language);
        }
        else {
            locale = new Locale("En_US");
        }
        ResourceBundle rb = ResourceBundle.getBundle(IFormatter.RESOURCE_BUNDLE_NAME, locale);


	   Boolean regionCms = false;
	   if(rrc.getRegionCms() != null) {
		   regionCms = rrc.getRegionCms();
	   }
	   
	   
	   String cssRegion = "";
	   if( "preview".equals(rendererContext.getProperty("osivia.cmsEditionMode"))) {
		   cssRegion = "";
	   }
	   
      PrintWriter markup = rendererContext.getWriter();
      
      
      markup.print("<div");
      if (rrc == null) // || region.getWindows().size() <= 0)
      {
         markup.print(" class='empty-region "+cssRegion+"'");
      }
      else if (rrc.getCSSId() != null)
      {
         markup.print(" id='");
         markup.print(rrc.getCSSId());
         markup.print("' class='"+cssRegion+"'");
         
      }
      markup.print(">");
 
      // Lien d'ajout de portlet
      if (Constants.VALUE_WINDOWS_WIZARD_TEMPLATE_MODE.equals(rendererContext.getProperty(Constants.ATTR_WINDOWS_WIZARD_MODE))) {
          String url = rendererContext.getProperty(Constants.ATTR_WINDOWS_ADD_PORTLET_URL);
          
          DOMElement div = new DOMElement(IFormatter.QNAME_NODE_DIV);
          div.addAttribute(IFormatter.QNAME_ATTRIBUTE_CLASS, CLASS_COMMANDS);
          
          DOMElement a = new DOMElement(IFormatter.QNAME_NODE_A);
          a.addAttribute(IFormatter.QNAME_ATTRIBUTE_HREF, url);
          a.addAttribute(IFormatter.QNAME_ATTRIBUTE_CLASS, CLASS_FANCYBOX);
          a.addAttribute(IFormatter.QNAME_ATTRIBUTE_ONCLICK, "regionId = '" + rrc.getId() + "'");
          div.add(a);
          
          DOMElement img = new DOMElement(IFormatter.QNAME_NODE_IMG);
          img.addAttribute(IFormatter.QNAME_ATTRIBUTE_SRC, SRC_IMG_ADD);
          a.add(img);
          
          HTMLWriter htmlWriter = new HTMLWriter(markup);
          try {
              htmlWriter.write(div);
          } catch (IOException e) {
              throw new RenderException(e);
          }
      }
      

      // in cms mode, create a new fragment on the top of this region
      if( regionCms && "preview".equals(rendererContext.getProperty("osivia.cmsEditionMode")))	{
    	 
    	  
    	  markup.print("<div class=\"regionPreview\">");
            // markup.println("<div class=\"previewOverlay\" >");
    	  
            markup.print("<a class=\"fancyframe_refresh add\" onClick=\"callbackUrl='"+rendererContext.getProperty("osivia.cmsCreateCallBackURL")+"'\" href=\"" + rendererContext.getProperty("osivia.cmsCreateUrl") + "\">"
                    + rb.getString("CMS_ADD_FRAGMENT") + "</a>");
    	  
            // markup.print("</div>");
    	  markup.println("</div>");
    	  
    	  // if region is empty, create a zone to drop other fragments here
    	  if(rrc.getWindows().size() == 1) {
    		  WindowRendererContext wrc = (WindowRendererContext) rrc.getWindows().iterator().next();
    		  if(wrc.getId().contains("_PIA_EMPTY")) {
    		  
                    markup.println("<div id=\"region_" + rrc.getId() + "\" class=\"dnd-region dnd-handle fragmentPreview\">"
 + rb.getString("CMS_EMPTY_REGION")
                            + "</div>");
    			  
    		  }
    	  }
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
