/******************************************************************************
 * JBoss, a division of Red Hat *
 * Copyright 2006, Red Hat Middleware, LLC, and individual *
 * contributors as indicated by the @authors tag. See the *
 * copyright.txt in the distribution for a full listing of *
 * individual contributors. *
 * *
 * This is free software; you can redistribute it and/or modify it *
 * under the terms of the GNU Lesser General Public License as *
 * published by the Free Software Foundation; either version 2.1 of *
 * the License, or (at your option) any later version. *
 * *
 * This software is distributed in the hope that it will be useful, *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU *
 * Lesser General Public License for more details. *
 * *
 * You should have received a copy of the GNU Lesser General Public *
 * License along with this software; if not, write to the Free *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org. *
 ******************************************************************************/
package org.osivia.portal.core.renderers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.dom4j.dom.DOMElement;
import org.dom4j.io.HTMLWriter;
import org.jboss.portal.theme.render.AbstractObjectRenderer;
import org.jboss.portal.theme.render.RenderException;
import org.jboss.portal.theme.render.RendererContext;
import org.jboss.portal.theme.render.renderer.ActionRendererContext;
import org.jboss.portal.theme.render.renderer.WindowRenderer;
import org.jboss.portal.theme.render.renderer.WindowRendererContext;
import org.osivia.portal.api.Constants;
import org.osivia.portal.core.formatters.IFormatter;
import org.osivia.portal.core.page.PageProperties;


/**
 * Implementation of a WindowRenderer, based on div tags.
 * 
 * @author <a href="mailto:mholzner@novell.com>Martin Holzner</a>
 * @version $LastChangedRevision: 8784 $, $LastChangedDate: 2007-10-27 19:01:46 -0400 (Sat, 27 Oct 2007) $
 * @see org.jboss.portal.theme.render.renderer.WindowRenderer
 */
public class DivWindowRenderer extends AbstractObjectRenderer implements WindowRenderer {

    /** Windows settings commands class. */
    private static final String CLASS_COMMANDS = "commands";
    /** Fancybox class, required for link. */
    private static final String CLASS_FANCYBOX_INLINE = "fancybox_inline";
    /** Fancybox class, required for link. */
    private static final String CLASS_FANCYBOX_FRAME = "fancyframe_refresh";
    /** Up move command link image source. */
    private static final String UP_LINK_IMAGE_SOURCE = "/osivia-portal-custom-web-assets/images/arrow_up.png";
    /** Down move command link image source. */
    private static final String DOWN_LINK_IMAGE_SOURCE = "/osivia-portal-custom-web-assets/images/arrow_down.png";
    /** Previous region move command link image source. */
    private static final String PREVIOUS_REGION_LINK_IMAGE_SOURCE = "/osivia-portal-custom-web-assets/images/arrow_left.png";
    /** Next region move command link image source. */
    private static final String NEXT_REGION_LINK_IMAGE_SOURCE = "/osivia-portal-custom-web-assets/images/arrow_right.png";    
    /** Display window settings command link image source. */
    private static final String DISPLAY_SETTINGS_LINK_IMAGE_SOURCE = "/osivia-portal-custom-web-assets/images/application_edit.png";
    /** Display portlet administration command link image source. */
    private static final String DISPLAY_ADMIN_LINK_IMAGE_SOURCE = "/osivia-portal-custom-web-assets/images/application_form.png";
    /** Delete portlet command link image source. */
    private static final String DELETE_LINK_IMAGE_SOURCE = "/osivia-portal-custom-web-assets/images/cross.png";
    /** Shading image source. */
    private static final String SHADING_IMAGE_SOURCE = "/osivia-portal-custom-web-assets/images/shading.png";
        
    
    /**
     * {@inheritDoc}
     */
    public void render(RendererContext rendererContext, WindowRendererContext wrc) throws RenderException {
        PrintWriter out = rendererContext.getWriter();


        PageProperties properties = PageProperties.getProperties();

        // Pour les décorateurs
        properties.setCurrentWindowId(wrc.getId());


        String hidePortlet = properties.getWindowProperty(wrc.getId(), "osivia.hidePortlet");

        if ("1".equals(hidePortlet))
            return;


        String scripts =  properties.getWindowProperty(wrc.getId(), "osivia.popupScript");
        if( scripts != null)
            out.print(scripts);
        
        
        // Activation des liens Ajax
        String ajaxLink = properties.getWindowProperty(wrc.getId(), "osivia.ajaxLink");
        if (!"1".equals(ajaxLink) || "wizzard".equals(wrc.getProperty("osivia.windowSettingMode")))
            out.print("<div class=\"no-ajax-link\">");


        String style = properties.getWindowProperty(wrc.getId(), "osivia.style");

        if (style != null)
            style = style.replaceAll(",", " ");
        else
            style = "";


        /*
         * if( style != null)
         * out.print("<div id=\""+style+"\">");
         */

        out.println("<div class=\"dyna-window-content\" >");

        out.print("<div class=\"portlet-container " + style + "\">");


        // Print portlet commands
        this.printPortletCommands(out, wrc, properties);


        out.print("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");


        if ("1".equals(properties.getWindowProperty(wrc.getId(), "osivia.displayTitle"))) {


            out.print("<tr><td class=\"portlet-titlebar-left\"></td>");
            out.print("<td class=\"portlet-titlebar-center\">");

            rendererContext.render(wrc.getDecoration());
            out.print("</td><td class=\"portlet-titlebar-right\"></td></tr>");
        }

        //
        out.print("<tr><td class=\"portlet-content-left\"></td>");
        out.print("<td class=\"portlet-body\"><div class=\"portlet-content-center\">");


  
        rendererContext.render(wrc.getPortlet());


        out.print("</div></td><td class=\"portlet-content-right\"></td></tr>");

        //
        out.print("<tr><td class=\"portlet-footer-left\"></td>");
        out.print("<td class=\"portlet-footer-center\"></td>");
        out.print("<td class=\"portlet-footer-right\"></td></tr>");
        out.print("</table></div>");

        // fin du style
        /*
         * if( style != null)
         * out.print("</div>");
         */
        out.print("</div>"); // dyna-window-content

        // Activation des liens Ajax
        if (!"1".equals(ajaxLink) || "wizzard".equals(wrc.getProperty("osivia.windowSettingMode")))
            out.print("</div>");


    }

    
    /**
     * Utility method used to print portlet commands.
     * @param writer renderer writer
     * @param windowRendererContext window renderer context
     * @throws RenderException
     */
    @SuppressWarnings("unchecked")
    private void printPortletCommands(PrintWriter writer, WindowRendererContext windowRendererContext, PageProperties properties) throws RenderException {
        String windowSettingsMode = windowRendererContext.getProperty(Constants.ATTR_WINDOWS_SETTING_MODE);
        if (Constants.VALUE_WINDOWS_SETTING_WIZARD_MODE.equals(windowSettingsMode)) {
            String windowId = windowRendererContext.getId();
            String onclickAction = "windowId = '" + windowId + "'";

            // Commands container
            DOMElement div = new DOMElement(IFormatter.QNAME_NODE_DIV);
            div.addAttribute(IFormatter.QNAME_ATTRIBUTE_CLASS, CLASS_COMMANDS);
            
            // Up move command
            String upUrl = windowRendererContext.getProperty(Constants.ATTR_WINDOWS_UP_COMMAND_URL);
            DOMElement upLink = this.generatePortletCommandLink(upUrl, null, UP_LINK_IMAGE_SOURCE,  null,null);
            div.add(upLink);

            // Down move command
            String downUrl = windowRendererContext.getProperty(Constants.ATTR_WINDOWS_DOWN_COMMAND_URL);
            DOMElement downLink = this.generatePortletCommandLink(downUrl, null, DOWN_LINK_IMAGE_SOURCE,  null,null);
            div.add(downLink);
            
            // Previous region move command
            String previousRegionUrl = windowRendererContext.getProperty(Constants.ATTR_WINDOWS_PREVIOUS_REGION_COMMAND_URL);
            DOMElement previousRegionLink = this.generatePortletCommandLink(previousRegionUrl, null, PREVIOUS_REGION_LINK_IMAGE_SOURCE,  null,null);
            div.add(previousRegionLink);
            
            // Next region move command
            String nextRegionUrl = windowRendererContext.getProperty(Constants.ATTR_WINDOWS_NEXT_REGION_COMMAND_URL);
            DOMElement nextRegionLink = this.generatePortletCommandLink(nextRegionUrl, null, NEXT_REGION_LINK_IMAGE_SOURCE,  null,null);
            div.add(nextRegionLink);
            
            // Shading
            DOMElement imgShading = new DOMElement(IFormatter.QNAME_NODE_IMG);
            imgShading.addAttribute(IFormatter.QNAME_ATTRIBUTE_SRC, SHADING_IMAGE_SOURCE);
            div.add(imgShading);
            
            // Window settings display command
            String displaySettingsUrl = windowRendererContext.getProperty(Constants.ATTR_WINDOWS_DISPLAY_SETTINGS_URL);
            DOMElement displaySettingsLink = this.generatePortletCommandLink(displaySettingsUrl, onclickAction, DISPLAY_SETTINGS_LINK_IMAGE_SOURCE, CLASS_FANCYBOX_INLINE, null);
            div.add(displaySettingsLink);
            
            // Portlet administration display command
            Collection<ActionRendererContext> actions = windowRendererContext.getDecoration().getTriggerableActions(ActionRendererContext.MODES_KEY);
            for (ActionRendererContext action : actions) {
                if (("admin".equals(action.getName())) && (action.isEnabled())) {
                    String title = properties.getWindowProperty(windowId, "osivia.title");
                    if( title == null)
                        title = "";
                    
                    String link = action.getURL()+"&windowstate=maximized";
                    
                    String instanceName = windowRendererContext.getProperty("osivia.instanceDisplayName");
                    title += "   ["+instanceName+"]";                    
                    DOMElement displayAdminLink = this.generatePortletCommandLink(link, onclickAction, DISPLAY_ADMIN_LINK_IMAGE_SOURCE, CLASS_FANCYBOX_FRAME, title);
                    div.add(displayAdminLink);


                }
            }
            
            // Shading (can't reuse first shading node)
            DOMElement imgShading2 = (DOMElement) imgShading.cloneNode(true);
            div.add(imgShading2);
            
            // Delete portlet command
            String deleteUrl = windowRendererContext.getProperty(Constants.ATTR_WINDOWS_DELETE_PORTLET_URL);
            DOMElement deleteLink = this.generatePortletCommandLink(deleteUrl, onclickAction, DELETE_LINK_IMAGE_SOURCE, CLASS_FANCYBOX_INLINE, null);
            div.add(deleteLink);
            
            // Print
            HTMLWriter htmlWriter = new HTMLWriter(writer);
            try {
                htmlWriter.write(div);
            } catch (IOException e) {
                throw new RenderException(e);
            }
        }
    }
    
    /**
     * Utility method used to generate an image command link.
     * @param href link URL
     * @param onclick onclick action, may be null
     * @param imageSource image source
     * @param fancybox true if the link must open a fancybox
     * @return HTML "a" node
     */
    private DOMElement generatePortletCommandLink(String href, String onclick, String imageSource, String fancyboxClass, String title) {
        // HTML "a" node
        DOMElement a = new DOMElement(IFormatter.QNAME_NODE_A);
        a.addAttribute(IFormatter.QNAME_ATTRIBUTE_HREF, href);
        if (onclick != null) {
            a.addAttribute(IFormatter.QNAME_ATTRIBUTE_ONCLICK, onclick);
        }
        if (StringUtils.isNotEmpty(fancyboxClass)) {
            a.addAttribute(IFormatter.QNAME_ATTRIBUTE_CLASS, fancyboxClass);
        }
        if (StringUtils.isNotEmpty(title)) {
            a.addAttribute(IFormatter.QNAME_ATTRIBUTE_TITLE, title);
        }

        
        // HTML "img" sub node
        DOMElement img = new DOMElement(IFormatter.QNAME_NODE_IMG);
        img.addAttribute(IFormatter.QNAME_ATTRIBUTE_SRC, imageSource);
        a.add(img);
        
        return a;
    }

}
