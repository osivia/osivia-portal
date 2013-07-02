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
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.dom.DOMElement;
import org.dom4j.io.HTMLWriter;
import org.jboss.portal.theme.page.WindowContext;
import org.jboss.portal.theme.render.AbstractObjectRenderer;
import org.jboss.portal.theme.render.RenderException;
import org.jboss.portal.theme.render.RendererContext;
import org.jboss.portal.theme.render.renderer.ActionRendererContext;
import org.jboss.portal.theme.render.renderer.WindowRenderer;
import org.jboss.portal.theme.render.renderer.WindowRendererContext;
import org.osivia.portal.api.HtmlConstants;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.page.PageProperties;

/**
 * Implementation of a WindowRenderer, based on div tags.
 *
 * @author <a href="mailto:mholzner@novell.com>Martin Holzner</a>
 * @version $LastChangedRevision: 8784 $, $LastChangedDate: 2007-10-27 19:01:46 -0400 (Sat, 27 Oct 2007) $
 * @see org.jboss.portal.theme.render.renderer.WindowRenderer
 */
public class DivWindowRenderer extends AbstractObjectRenderer implements WindowRenderer {

    /** Internationalization service. */
    private static final IInternationalizationService INTERNATIONALIZATION_SERVICE = Locator.findMBean(IInternationalizationService.class,
            IInternationalizationService.MBEAN_NAME);

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

        String language = wrc.getProperty("osivia.language");
        Locale locale = null;
        if (language != null) {
            locale = new Locale(language);
        } else {
            locale = Locale.US;
        }

        PrintWriter out = rendererContext.getWriter();

        PageProperties properties = PageProperties.getProperties();

        // Pour les décorateurs
        properties.setCurrentWindowId(wrc.getId());

        // Déterminer si la window appartient à une région CMS
        Boolean regionCms = false;
        if(wrc instanceof WindowContext) {
      	  WindowContext wc = (WindowContext) wrc;

      	  if((wc.getRegionCms() != null) && (wrc.getProperty("osivia.windowId") != null)) {
      		  regionCms = wc.getRegionCms();
      	  }
        }

        String hidePortlet = properties.getWindowProperty(wrc.getId(), "osivia.hidePortlet");

        if( "1".equals(hidePortlet)) {
            return;
        }



        // Activation des liens Ajax
        String ajaxLink = properties.getWindowProperty(wrc.getId(), "osivia.ajaxLink");


        if( ! "1".equals(ajaxLink) || "wizzard".equals(wrc.getProperty("osivia.windowSettingMode"))) {
      	  // if is window cms, prepare the drag & drop style
            // if(regionCms) {
            // out.print("<div id=\"zone_" + wrc.getProperty("osivia.windowId") + "\" class=\"no-ajax-link\"> " + "<div id=\"window_"
            // + wrc.getProperty("osivia.windowId") + "\">");
            // }
            // else {
            out.print("<div class=\"no-ajax-link\"> ");
            // + "<div>");
            // }
        }


        String style = properties.getWindowProperty(wrc.getId(), "osivia.style");

        if( style != null) {
            style = style.replaceAll(",", " ");
        } else {
            style = "";
        }



  	   String cssFragment = "";
  	   if(regionCms && "preview".equals(rendererContext.getProperty("osivia.cmsEditionMode"))) {
  		   cssFragment = "fragmentPreview";
  	   }

        out.println("<div class=\" dyna-window-content "+cssFragment+"\" >");

        // edit / remove fragment actions
        if (regionCms && "preview".equals(rendererContext.getProperty("osivia.cmsEditionMode"))) {
            // out.println("<div class=\" previewOverlay\" >");

            out.print("<a class=\"fancyframe_refresh edit\" onClick=\"callbackUrl='" + rendererContext.getProperty("osivia.cmsEditCallbackUrl")
                    + "';callBackId='" + rendererContext.getProperty("osivia.cmsEditCallbackId") + "'\" href=\"" + wrc.getProperty("osivia.cmsEditUrl") + "\">"
                    + INTERNATIONALIZATION_SERVICE.getString("CMS_EDIT_FRAGMENT", locale) + "</a> ");
            out.print("<a href=\"" + wrc.getProperty("osivia.cmsDeleteUrl") + "\" class=\"delete\">"
                    + INTERNATIONALIZATION_SERVICE.getString("CMS_DELETE_FRAGMENT", locale) + "</a> ");

            // out.println("</div>");

        }



        String scripts =  properties.getWindowProperty(wrc.getId(), "osivia.popupScript");
        if( scripts != null) {
            out.print(scripts);
        }



        out.print("<div class=\"portlet-container "+style+"\">");

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

      // in cms mode, create a new fragment below the current window
      if( regionCms && (wrc.getProperty("osivia.cmsEditUrl") != null))	{


    	  out.print("<div class=\"regionPreview\">");
            // out.println("<div class=\"previewOverlay\" >");

            out.print("<a class=\"fancyframe_refresh add\" onClick=\"callbackUrl='" + wrc.getProperty("osivia.cmsCreateCallBackURL") + "'\" href=\""
                    + wrc.getProperty("osivia.cmsCreateUrl") + "\">" + INTERNATIONALIZATION_SERVICE.getString("CMS_ADD_FRAGMENT", locale)
                    + "</a>");

            // out.print("</div>");
    	  out.println("</div>");

      }


        // Activation des liens Ajax
        if (!"1".equals(ajaxLink) || "wizzard".equals(wrc.getProperty("osivia.windowSettingMode"))) {
            // out.print("</div></div>");
            out.print("</div>");
        }


    }


    /**
     * Utility method used to print portlet commands.
     * @param writer renderer writer
     * @param windowRendererContext window renderer context
     * @throws RenderException
     */
    @SuppressWarnings("unchecked")
    private void printPortletCommands(PrintWriter writer, WindowRendererContext windowRendererContext, PageProperties properties) throws RenderException {
        String windowSettingsMode = windowRendererContext.getProperty(InternalConstants.ATTR_WINDOWS_SETTING_MODE);
        if (InternalConstants.VALUE_WINDOWS_SETTING_WIZARD_MODE.equals(windowSettingsMode)) {
            String windowId = windowRendererContext.getId();
            String onclickAction = "windowId = '" + windowId + "'";

            // Commands container
            Element div = new DOMElement(QName.get(HtmlConstants.DIV));
            div.addAttribute(QName.get(HtmlConstants.CLASS), CLASS_COMMANDS);

            // Up move command
            String upUrl = windowRendererContext.getProperty(InternalConstants.ATTR_WINDOWS_UP_COMMAND_URL);
            Element upLink = this.generatePortletCommandLink(upUrl, null, UP_LINK_IMAGE_SOURCE, null, null);
            div.add(upLink);

            // Down move command
            String downUrl = windowRendererContext.getProperty(InternalConstants.ATTR_WINDOWS_DOWN_COMMAND_URL);
            Element downLink = this.generatePortletCommandLink(downUrl, null, DOWN_LINK_IMAGE_SOURCE, null, null);
            div.add(downLink);

            // Previous region move command
            String previousRegionUrl = windowRendererContext.getProperty(InternalConstants.ATTR_WINDOWS_PREVIOUS_REGION_COMMAND_URL);
            Element previousRegionLink = this.generatePortletCommandLink(previousRegionUrl, null, PREVIOUS_REGION_LINK_IMAGE_SOURCE, null, null);
            div.add(previousRegionLink);

            // Next region move command
            String nextRegionUrl = windowRendererContext.getProperty(InternalConstants.ATTR_WINDOWS_NEXT_REGION_COMMAND_URL);
            Element nextRegionLink = this.generatePortletCommandLink(nextRegionUrl, null, NEXT_REGION_LINK_IMAGE_SOURCE, null, null);
            div.add(nextRegionLink);

            // Shading
            Element imgShading = new DOMElement(QName.get(HtmlConstants.IMG));
            imgShading.addAttribute(QName.get(HtmlConstants.SRC), SHADING_IMAGE_SOURCE);
            div.add(imgShading);

            // Window settings display command
            String displaySettingsUrl = windowRendererContext.getProperty(InternalConstants.ATTR_WINDOWS_DISPLAY_SETTINGS_URL);
            Element displaySettingsLink = this.generatePortletCommandLink(displaySettingsUrl, onclickAction, DISPLAY_SETTINGS_LINK_IMAGE_SOURCE,
                    CLASS_FANCYBOX_INLINE, null);
            div.add(displaySettingsLink);

            // Portlet administration display command
            Collection<ActionRendererContext> actions = windowRendererContext.getDecoration().getTriggerableActions(ActionRendererContext.MODES_KEY);
            for (ActionRendererContext action : actions) {
                if ((InternalConstants.ACTION_ADMIN.equals(action.getName())) && (action.isEnabled())) {
                    String title = properties.getWindowProperty(windowId, InternalConstants.PROP_WINDOW_TITLE);
                    if (title == null) {
                        title = StringUtils.EMPTY;
                    }

                    String link = action.getURL() + "&windowstate=maximized";

                    String instanceName = windowRendererContext.getProperty(InternalConstants.ATTR_WINDOWS_INSTANCE_DISPLAY_NAME);
                    title += "   [" + instanceName + "]";
                    Element displayAdminLink = this.generatePortletCommandLink(link, onclickAction, DISPLAY_ADMIN_LINK_IMAGE_SOURCE, CLASS_FANCYBOX_FRAME,
                            title);
                    div.add(displayAdminLink);
                }
            }

            // Shading (can't reuse first shading node)
            Element imgShading2 = new DOMElement(QName.get(HtmlConstants.IMG));
            imgShading2.addAttribute(QName.get(HtmlConstants.SRC), SHADING_IMAGE_SOURCE);
            div.add(imgShading2);

            // Delete portlet command
            String deleteUrl = windowRendererContext.getProperty(InternalConstants.ATTR_WINDOWS_DELETE_PORTLET_URL);
            Element deleteLink = this.generatePortletCommandLink(deleteUrl, onclickAction, DELETE_LINK_IMAGE_SOURCE, CLASS_FANCYBOX_INLINE, null);
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
    private Element generatePortletCommandLink(String href, String onclick, String imageSource, String fancyboxClass, String title) {
        // HTML "a" node
        DOMElement a = new DOMElement(QName.get(HtmlConstants.A));
        a.addAttribute(QName.get(HtmlConstants.HREF), href);
        if (onclick != null) {
            a.addAttribute(QName.get(HtmlConstants.ONCLICK), onclick);
        }
        if (StringUtils.isNotEmpty(fancyboxClass)) {
            a.addAttribute(QName.get(HtmlConstants.CLASS), fancyboxClass);
        }
        if (StringUtils.isNotEmpty(title)) {
            a.addAttribute(QName.get(HtmlConstants.TITLE), title);
        }

        // HTML "img" sub node
        Element img = new DOMElement(QName.get(HtmlConstants.IMG));
        img.addAttribute(QName.get(HtmlConstants.SRC), imageSource);
        a.add(img);

        return a;
    }

}

