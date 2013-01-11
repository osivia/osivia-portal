/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2009, Red Hat Middleware, LLC, and individual                    *
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
import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.theme.page.DecorationRendererContextImpl;
import org.jboss.portal.theme.render.AbstractObjectRenderer;
import org.jboss.portal.theme.render.RenderException;
import org.jboss.portal.theme.render.RendererContext;
import org.jboss.portal.theme.render.renderer.ActionRendererContext;
import org.jboss.portal.theme.render.renderer.DecorationRenderer;
import org.jboss.portal.theme.render.renderer.DecorationRendererContext;
import org.osivia.portal.core.page.PageProperties;


import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Implementation of a decoration renderer, based on div tags.
 *
 * @author <a href="mailto:mholzner@novell.com>Martin Holzner</a>
 * @version $LastChangedRevision: 12912 $, $LastChangedDate: 2009-02-28 11:37:59 -0500 (Sat, 28 Feb 2009) $
 * @see org.jboss.portal.theme.render.renderer.DecorationRenderer
 */
public class DivDecorationRenderer extends AbstractObjectRenderer
   implements DecorationRenderer
{

   public void render(RendererContext rendererContext, DecorationRendererContext drc) throws RenderException
   {
      PrintWriter markup = rendererContext.getWriter();

       
      PageProperties properties = PageProperties.getProperties();
      String currentWindowId = properties.getCurrentWindowId();
      
  
       
      renderTitle(rendererContext, drc, properties.getWindowProperty(currentWindowId, "osivia.title"));
      
     
//      if(  "1".equals(properties.getWindowProperty(currentWindowId, "osivia.displayDecorators")) )	{
    	  


       	
      
    	markup.print("<div class=\"portlet-mode-container\">");
    	
    	
	
       	
    	
    	if( "1".equals(properties.getWindowProperty(currentWindowId, "osivia.displayDecorators")) ){
    		renderTriggerableActions(rendererContext, drc, ActionRendererContext.MODES_KEY);
    		renderTriggerableActions(rendererContext, drc, ActionRendererContext.WINDOWSTATES_KEY);
    	}
    	
  
    	
        String closeUrl = properties.getWindowProperty(currentWindowId, "osivia.closeUrl");
        
      	
      	if (closeUrl != null)
        {
           PrintWriter out = rendererContext.getWriter();
           out.print("<span class=\"mode-button\" title=\"fermer\"><a class=\"portlet-close\" href=\"");

           out.print(closeUrl);
           out.print("\">&nbsp;</a></span>");
        }
      	

      	
      	markup.print("</div>");
//      }
  
  	

       		
    

      
      
   }

   private static void renderTitle(RendererContext ctx, DecorationRendererContext drc, String title)
   {
	   
	  //System.out.println( "DivDecorationRenderer.render title=" + drc.getTitle());
	   
      PrintWriter out = ctx.getWriter();
      out.print("<div class=\"portlet-titlebar-decoration\"></div>");
      out.print("<span class=\"portlet-titlebar-title\">");
      
 
      if( title != null)
    	  out.print(title);
    	else
    		out.print(drc.getTitle());
      out.print("</span>");
   }

   private static void renderTriggerableActions(RendererContext ctx, DecorationRendererContext drc, String selector)
   {
      Collection modesOrStates = drc.getTriggerableActions(selector);
      if (modesOrStates == null)
      {
         return;
      }

      //
      if (modesOrStates instanceof List)
      {
         List list = (List)modesOrStates;
         Collections.sort(list, new ModeAndStateComparator());
         modesOrStates = list;
      }

      //
      for (Iterator i = modesOrStates.iterator(); i.hasNext();)
      {
         ActionRendererContext action = (ActionRendererContext)i.next();
         if (action.isEnabled() && !"admin".equals(action.getName()) && !"minimized".equals(action.getName()))
         {
            PrintWriter out = ctx.getWriter();
            out.print("<span class=\"mode-button\" title=\"");
            out.print(action.getName());
            out.print("\"><a class=\"portlet-mode-");
            out.print(action.getName());
            out.print("\" href=\"");
            out.print(action.getURL());
            out.print("\">&nbsp;</a></span>");
         }
      }
   }

   private static class ModeAndStateComparator implements Comparator
   {

      /** . */
      private final Map modeOrState2Index = new HashMap();

      /** . */
      private int lastModeIndex = 1;

      /** . */
      private int lastStateIndex = 101;

      public ModeAndStateComparator()
      {
         modeOrState2Index.put(Mode.EDIT.toString(), new Integer(97));
         modeOrState2Index.put(Mode.HELP.toString(), new Integer(98));
         modeOrState2Index.put(Mode.VIEW.toString(), new Integer(99));
         modeOrState2Index.put(Mode.ADMIN.toString(), new Integer(100));
         modeOrState2Index.put(WindowState.MINIMIZED.toString(), new Integer(198));
         modeOrState2Index.put(WindowState.NORMAL.toString(), new Integer(199));
         modeOrState2Index.put(WindowState.MAXIMIZED.toString(), new Integer(200));
      }

      public int compare(Object o1, Object o2)
      {
         ActionRendererContext action1 = (ActionRendererContext)o1;
         ActionRendererContext action2 = (ActionRendererContext)o2;

         //
         Object origin1 = action1.getName();
         Object origin2 = action2.getName();

         //
         int index1 = getIndexFor(origin1);
         int index2 = getIndexFor(origin2);

         //
         return index1 - index2;
      }

      private int getIndexFor(Object origin)
      {
         Integer index = (Integer)modeOrState2Index.get(origin);
         if (index == null)
         {
            index = (origin instanceof Mode) ? new Integer(lastModeIndex++) : new Integer(lastStateIndex++);
            modeOrState2Index.put(origin, index);
         }
         return index.intValue();
      }
   }
}