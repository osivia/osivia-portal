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
package org.jboss.portal.theme.tag;

import org.jboss.logging.Logger;
import org.jboss.portal.theme.LayoutConstants;
import org.jboss.portal.theme.Orientation;
import org.jboss.portal.theme.impl.JSPRendererContext;
import org.jboss.portal.theme.page.WindowContext;
import org.jboss.portal.theme.render.RenderException;
import org.jboss.portal.theme.render.renderer.PageRendererContext;
import org.jboss.portal.theme.render.renderer.RegionRendererContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Tag handler for the region tag. <p>A region represents a subsection of a portal page. A region can host several
 * portlets. The portlets can be arranged horizontally or vertically. The region tag utilizes a render set to create the
 * markup around the individual portlets.</p>
 *
 * @author <a href="mailto:mholzner@novell.com>Martin Holzner</a>
 * @author <a href="mailto:roy@jboss.org>Roy Russo</a>
 * @version $LastChangedRevision: 8784 $, $LastChangedDate: 2007-10-27 19:01:46 -0400 (Sat, 27 Oct 2007) $
 * @see org.jboss.portal.theme.PortalRenderSet
 */
public class RegionTagHandler
   extends SimpleTagSupport
{
   private static Logger log = Logger.getLogger(RegionTagHandler.class);
   //default to vertical

   private Orientation regionOrientation;
   private String regionName = null;
   private String regionCssId = null;
   private Boolean regionCms = null;

   /**
    * create the markup of this tag
    *
    * @throws JspException
    * @throws IOException
    */
   public void doTag() throws JspException, IOException
   {
      if (regionCssId == null)
      {
         regionCssId = regionName;
      }
      log.debug("rendering " + regionName + " [" + regionOrientation + "]  cssId[" + regionCssId + "]");

      // get page and region
      PageContext app = (PageContext)getJspContext();
      HttpServletRequest request = (HttpServletRequest)app.getRequest();

      PageRendererContext page = (PageRendererContext)request.getAttribute(LayoutConstants.ATTR_PAGE);
      JspWriter out = this.getJspContext().getOut();
      if (page == null)
      {
         out.write("<p bgcolor='red'>No page to render!</p>");
         out.write("<p bgcolor='red'>The page to render (PageResult) must be set in the request attribute '" +
            LayoutConstants.ATTR_PAGE + "'</p>");
         out.flush();
         return;
      }

      final String cssId = regionCssId == null ? regionName : regionCssId;
      final Orientation orientation = this.regionOrientation == null ? Orientation.DEFAULT : this.regionOrientation;

      if (page.getRegion(regionName) == null) // non-window display of content
      {
         // Create a dummy region obj, based on region name
         RegionRendererContext rrc = new RegionRendererContext()
         {
            public String getId()
            {
               return regionName;
            }

            public Collection getWindows()
            {
               return Collections.EMPTY_LIST;
            }

            public PageRendererContext getPage()
            {
               return null;
            }

            public Orientation getOrientation()
            {
               return orientation;
            }

            public String getCSSId()
            {
               return cssId;
            }

            public String getProperty(String name)
            {
               return null;
            }

            public Map getProperties()
            {
               return Collections.EMPTY_MAP;
            }

			public Boolean getRegionCms() {
				return regionCms;
			}
         };

         JSPRendererContext renderContext = (JSPRendererContext)request.getAttribute(LayoutConstants.ATTR_RENDERCONTEXT);
         try
         {
            PrintWriter pw = new PrintWriter(out);
            renderContext.setWriter(pw);
            renderContext.render(rrc);
            pw.flush();
         }
         catch (RenderException e)
         {
            throw new JspException(e);
         }
      }
      else
      {
         // window-centric display of content
         JSPRendererContext renderContext = (JSPRendererContext)request.getAttribute(LayoutConstants.ATTR_RENDERCONTEXT);
         if (renderContext == null)
         {
            log.debug("no render context available in request");
            return;
         }

         final RegionRendererContext region = page.getRegion(regionName);
         RegionRendererContext rrc = new RegionRendererContext()
         {
            public String getId()
            {
               return region.getId();
            }

            public Collection getWindows()
            {
            	for(Object window : region.getWindows()) {
            		if(window instanceof WindowContext) {
            			WindowContext w = (WindowContext) window;
            			w.setRegionCms(regionCms);
            		}
            	}
            	
               return region.getWindows();
            }

            public Orientation getOrientation()
            {
               return orientation;
            }

            public String getCSSId()
            {
               return cssId;
            }

            public String getProperty(String name)
            {
               return region.getProperty(name);
            }

            public Map getProperties()
            {
               return region.getProperties();
            }

			public Boolean getRegionCms() {
				return regionCms;
			}
         };
         try
         {
            PrintWriter pw = new PrintWriter(out);
            renderContext.setWriter(pw);
            renderContext.render(rrc);
            pw.flush();
         }
         catch (RenderException e)
         {
            throw new JspException(e);
         }

         log.debug("done rendering page region [" + regionName + "]");
      }
   }

   // ------ attribute handlers

   /**
    * Attribute handler for the orientation attribute of this tag
    *
    * @param orientation the orientation attribute value set in the hosting jsp
    */
   public void setOrientation(String orientation)
   {
      if (orientation == null)
      {
         log.error("no null value allowed");
      }

      try
      {
         this.regionOrientation = Orientation.parse(orientation);
      }
      catch (IllegalArgumentException e)
      {
         log.error(e);
         this.regionOrientation = Orientation.DEFAULT;
      }
   }

   /**
    * Attribute handler for the region name attribute of this tag
    *
    * @param regionName the name of the region this tag should render the markup for
    */
   public void setRegionName(String regionName)
   {
      this.regionName = regionName;
   }

   /**
    * Attribute handler for the region id attribute of this tag <p>The region Id can be used as the value of the id
    * attribute of the region container tag. In case of the DivRenderer render set, this is the id attribute of the div
    * tag that represents this region on the page.</p>
    *
    * @param regionID the id attribute value to set for this tag
    */
   public void setRegionID(String regionID)
   {
      this.regionCssId = regionID;
   }

	/**
	 * @return the regionCms
	 */
	public Boolean getRegionCms() {
		return regionCms;
	}

	/**
	 * @param regionCms
	 *            the regionCms to set
	 */
	public void setRegionCms(Boolean regionCms) {
		this.regionCms = regionCms;
	}
   
}
