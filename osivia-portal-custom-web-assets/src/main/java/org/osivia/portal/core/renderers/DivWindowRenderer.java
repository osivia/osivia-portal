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

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Locale;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.dom.DOMElement;
import org.jboss.portal.theme.page.WindowContext;
import org.jboss.portal.theme.render.AbstractObjectRenderer;
import org.jboss.portal.theme.render.RenderException;
import org.jboss.portal.theme.render.RendererContext;
import org.jboss.portal.theme.render.renderer.ActionRendererContext;
import org.jboss.portal.theme.render.renderer.WindowRenderer;
import org.jboss.portal.theme.render.renderer.WindowRendererContext;
import org.osivia.portal.api.html.HTMLConstants;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.constants.InternationalizationConstants;
import org.osivia.portal.core.page.PageProperties;

/**
 * Implementation of a WindowRenderer, based on div tags.
 *
 * @author <a href="mailto:mholzner@novell.com>Martin Holzner</a>
 * @version $LastChangedRevision: 8784 $, $LastChangedDate: 2007-10-27 19:01:46 -0400 (Sat, 27 Oct 2007) $
 * @see org.jboss.portal.theme.render.renderer.WindowRenderer
 */
public class DivWindowRenderer extends AbstractObjectRenderer implements WindowRenderer {

    /** Fancybox class, required for link. */
    private static final String CLASS_FANCYBOX_INLINE = "fancybox_inline";
    /** Fancybox class, required for link. */
    private static final String CLASS_FANCYBOX_FRAME = "fancyframe_refresh";
    /** Delete portlet message. */
    private static final String CMS_DELETE_CONFIRM_MESSAGE = "CMS_DELETE_CONFIRM_MESSAGE";

    /** Internationalization service. */
    private final IInternationalizationService internationalizationService;


    /**
     * Constructor.
     */
    public DivWindowRenderer() {
        super();

        this.internationalizationService = Locator.findMBean(IInternationalizationService.class, IInternationalizationService.MBEAN_NAME);
    }


    /**
     * {@inheritDoc}
     */
    public void render(RendererContext rendererContext, WindowRendererContext wrc) throws RenderException {
        String language = wrc.getProperty("osivia.language");
        Locale locale;
        if (language != null) {
            locale = new Locale(language);
        } else {
            locale = Locale.getDefault();
        }

        PrintWriter out = rendererContext.getWriter();

        PageProperties properties = PageProperties.getProperties();
        // Set current window identifier for decorators
        properties.setCurrentWindowId(wrc.getId());


        // Mobile collapse indicator
        boolean mobileCollapse = BooleanUtils.toBoolean(properties.getWindowProperty(wrc.getId(), "osivia.mobileCollapse"));
        // Bootstrap panel style indicator
        boolean bootstrapPanelStyle = BooleanUtils.toBoolean(properties.getWindowProperty(wrc.getId(), "osivia.bootstrapPanelStyle"));
        // Hide portlet indicator
        String hidePortlet = properties.getWindowProperty(wrc.getId(), "osivia.hidePortlet");
        // AJAX links indicator
        String ajaxLink = properties.getWindowProperty(wrc.getId(), "osivia.ajaxLink");

        if ("1".equals(hidePortlet)) {
            // v2.0.22 : portlet vide non rafraichi en ajax
            out.println("<div class=\"dyna-window-content\" ></div>");
            return;
        }

        // Wizard mode indicator
        String windowsSettingMode = wrc.getProperty(InternalConstants.ATTR_WINDOWS_SETTING_MODE);
        boolean wizard = InternalConstants.VALUE_WINDOWS_SETTING_WIZARD_MODE.equals(windowsSettingMode);

        out.print("<div ");
        if (wrc.getProperty("osivia.windowId") != null) {

            out.print("id=\"" + wrc.getProperty("osivia.windowId") + "\" ");
        }
        if (!"1".equals(ajaxLink) || wizard) {
            out.print("class=\"no-ajax-link\" ");

        }
        out.print("/> ");


        String style = properties.getWindowProperty(wrc.getId(), "osivia.style");

        if (style != null) {
            style = style.replaceAll(",", " ");
        } else {
            style = "";
        }

        String cssFragment = "";
        if (this.showCmsTools(wrc)) {
            cssFragment = "fragmentPreview";
        }

        // Dyna window content
        out.println("<div class=\" dyna-window-content " + cssFragment + "\" >");

        // Edit / remove fragment actions
        if (this.showCmsTools(wrc)) {

            Element divFancyBoxDelete;
            try {
                divFancyBoxDelete = this.generateDeleteFancyBox(locale, wrc.getProperty("osivia.cmsDeleteUrl"), wrc.getProperty("osivia.windowId"));
            } catch (UnsupportedEncodingException e) {
                throw new RenderException(e);
            }

            out.println(divFancyBoxDelete.asXML());

            out.println("<div class=\"cms-commands\">");

            out.print("<a class=\"fancyframe_refresh cmd edit\" onClick=\"callbackUrl='" + rendererContext.getProperty("osivia.cmsEditCallbackUrl")
                    + "';callBackId='" + rendererContext.getProperty("osivia.cmsEditCallbackId") + "';setCallbackFromEcmParams('','"
                    + rendererContext.getProperty("osivia.ecmBaseUrl") + "')\" href=\"" + wrc.getProperty("osivia.cmsEditUrl") + "\">"
                    + this.internationalizationService.getString("CMS_EDIT_FRAGMENT", locale) + "</a> ");
            out.print("<a href=\"#delete_" + wrc.getProperty("osivia.windowId") + "\" class=\"cmd delete no-ajax-link fancybox_inline\">"
                    + this.internationalizationService.getString("CMS_DELETE_FRAGMENT", locale) + "</a> ");

            out.print("</div>");
        }


        String scripts = properties.getWindowProperty(wrc.getId(), "osivia.popupScript");
        if (scripts != null) {
            out.print(scripts);
        }

        // Wizard edging
        if (wizard) {
            out.print("<div class='window wizard-edging'>");
        }

        // Print portlet commands
        if (wizard) {
            this.printPortletCommands(out, wrc, properties);
        }

        // Portlet container
        if (bootstrapPanelStyle) {
            out.print("<div class='panel panel-default portlet-container " + style + "'>");
        } else {
            out.print("<div class='portlet-container " + style + "'>");
        }

        // Portlet container rendering
        String portletsRendering = System.getProperty(InternalConstants.SYSTEM_PROPERTY_PORTLETS_RENDERING);
        if (InternalConstants.SYSTEM_PROPERTY_PORTLETS_RENDERING_VALUE_DIV.equals(portletsRendering)) {
            // Div rendering

            String headerClass;
            String bodyClass;
            if (bootstrapPanelStyle) {
                headerClass = "panel-heading";
                bodyClass = "panel-body";
            } else {
                headerClass = "portlet-header";
                bodyClass = "portlet-content-center";
            }

            // Header
            if ("1".equals(properties.getWindowProperty(wrc.getId(), "osivia.displayTitle"))) {
                out.print("<div class='" + headerClass + " clearfix'>");
                rendererContext.render(wrc.getDecoration());
                out.print("</div>");
            }

            // Body
            if (mobileCollapse) {
                out.print("<div id='body_");
                out.print(wrc.getId());
                out.print("' class='panel-collapse collapse mobile-collapse");

                if (BooleanUtils.toBoolean(properties.getWindowProperty(wrc.getId(), "osivia.collapse.forceDisplay"))) {
                    out.print(" in");
                }

                out.print("'>");
            }
            out.print("<div class='" + bodyClass + "'>");
            rendererContext.render(wrc.getPortlet());
            out.print("</div>");
            if (mobileCollapse) {
                out.print("</div>");
            }

            // Footer

        } else {
            // Table rendering
            out.print("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");

            // Header
            if ("1".equals(properties.getWindowProperty(wrc.getId(), "osivia.displayTitle"))) {
                out.print("<tr><td class=\"portlet-titlebar-left\"></td>");
                out.print("<td class=\"portlet-titlebar-center\">");
                rendererContext.render(wrc.getDecoration());
                out.print("</td><td class=\"portlet-titlebar-right\"></td></tr>");
            }

            // Body
            out.print("<tr><td class=\"portlet-content-left\"></td>");
            out.print("<td class=\"portlet-body\"><div class=\"portlet-content-center\">");
            rendererContext.render(wrc.getPortlet());
            out.print("</div></td><td class=\"portlet-content-right\"></td></tr>");

            // Footer
            out.print("<tr><td class=\"portlet-footer-left\"></td>");
            out.print("<td class=\"portlet-footer-center\"></td>");
            out.print("<td class=\"portlet-footer-right\"></td></tr>");
            out.print("</table>");
        }

        // Portlet container
        out.print("</div>");
        // Wizard edging
        if (wizard) {
            out.print("</div>");
        }
        // Dyna window content
        out.print("</div>");

        // in cms mode, create a new fragment below the current window
        if (this.showCmsTools(wrc)) {


            out.print("<div class=\"cms-commands cms-region-commands\">");

            out.print("<a class=\"fancyframe_refresh cmd add\" onClick=\"callbackUrl='" + wrc.getProperty("osivia.cmsCreateCallBackURL")
                    + "';setCallbackFromEcmParams('','" + rendererContext.getProperty("osivia.ecmBaseUrl") + "')\" href=\""
                    + wrc.getProperty("osivia.cmsCreateUrl") + "\">" + this.internationalizationService.getString("CMS_ADD_FRAGMENT", locale) + "</a>");

            out.println("</div>");

        }


        // End of DIV for osivia.windowID or no ajax link
        out.print("</div>");


    }

    /**
     * Display CMS Tools if window is marked "CMS" (dynamic window) and if the tools are enabled in the session.
     *
     * @param wrc window context
     * @return true if CMS tools must be shown
     */
    private Boolean showCmsTools(WindowRendererContext wrc) {
        // Déterminer si la window appartient à une région CMS
        Boolean regionCms = false;
        Boolean showCmsTools = false;
        if (wrc instanceof WindowContext) {
            WindowContext wc = (WindowContext) wrc;

            if ((wc.getRegionCms() != null) && (wrc.getProperty("osivia.windowId") != null)) {
                regionCms = wc.getRegionCms();
            }

            if (regionCms && (wrc.getProperty("osivia.cmsShowTools") != null) && Boolean.valueOf(wrc.getProperty("osivia.cmsShowTools"))) {
                showCmsTools = true;
            } else {
                showCmsTools = false;
            }
        }
        return showCmsTools;
    }


    /**
     * Utility method used to print portlet commands.
     *
     * @param writer renderer writer
     * @param windowRendererContext window renderer context
     * @param properties properties
     * @throws RenderException
     */
    @SuppressWarnings("unchecked")
    private void printPortletCommands(PrintWriter writer, WindowRendererContext windowRendererContext, PageProperties properties) throws RenderException {
        String windowId = windowRendererContext.getId();
        String onclickAction = "windowId = '" + windowId + "'";

        // Commands toolbar
        Element toolbar = new DOMElement(QName.get(HTMLConstants.DIV));
        toolbar.addAttribute(QName.get(HTMLConstants.CLASS), "btn-toolbar");
        toolbar.addAttribute(QName.get(HTMLConstants.ROLE), HTMLConstants.ROLE_TOOLBAR);


        // Move commands group
        Element moveGroup = new DOMElement(QName.get(HTMLConstants.DIV));
        moveGroup.addAttribute(QName.get(HTMLConstants.CLASS), "btn-group btn-group-sm");
        toolbar.add(moveGroup);

        // Up move command
        String upUrl = windowRendererContext.getProperty(InternalConstants.ATTR_WINDOWS_UP_COMMAND_URL);
        Element upLink = this.generatePortletCommandLink(upUrl, null, "halflings arrow-up", null);
        moveGroup.add(upLink);

        // Down move command
        String downUrl = windowRendererContext.getProperty(InternalConstants.ATTR_WINDOWS_DOWN_COMMAND_URL);
        Element downLink = this.generatePortletCommandLink(downUrl, null, "glyphicons halflings arrow-down", null);
        moveGroup.add(downLink);

        // Previous region move command
        String previousRegionUrl = windowRendererContext.getProperty(InternalConstants.ATTR_WINDOWS_PREVIOUS_REGION_COMMAND_URL);
        Element previousRegionLink = this.generatePortletCommandLink(previousRegionUrl, null, "halflings arrow-left", null);
        moveGroup.add(previousRegionLink);

        // Next region move command
        String nextRegionUrl = windowRendererContext.getProperty(InternalConstants.ATTR_WINDOWS_NEXT_REGION_COMMAND_URL);
        Element nextRegionLink = this.generatePortletCommandLink(nextRegionUrl, null, "halflings arrow-right", null);
        moveGroup.add(nextRegionLink);


        // Settings commands group
        Element settingsGroup = new DOMElement(QName.get(HTMLConstants.DIV));
        settingsGroup.addAttribute(QName.get(HTMLConstants.CLASS), "btn-group btn-group-sm");
        toolbar.add(settingsGroup);

        // Window settings display command
        String displaySettingsUrl = windowRendererContext.getProperty(InternalConstants.ATTR_WINDOWS_DISPLAY_SETTINGS_URL);
        Element displaySettingsLink = this.generatePortletCommandLink(displaySettingsUrl, onclickAction, "halflings uni-wrench", CLASS_FANCYBOX_INLINE);
        settingsGroup.add(displaySettingsLink);

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
                Element displayAdminLink = this.generatePortletCommandLink(link, onclickAction, "halflings cog", CLASS_FANCYBOX_FRAME);
                settingsGroup.add(displayAdminLink);
            }
        }

        // Delete command group
        Element deleteGroup = new DOMElement(QName.get(HTMLConstants.DIV));
        deleteGroup.addAttribute(QName.get(HTMLConstants.CLASS), "btn-group btn-group-sm");
        toolbar.add(deleteGroup);

        // Delete portlet command
        String deleteUrl = windowRendererContext.getProperty(InternalConstants.ATTR_WINDOWS_DELETE_PORTLET_URL);
        Element deleteLink = this.generatePortletCommandLink(deleteUrl, onclickAction, "halflings remove", CLASS_FANCYBOX_INLINE);
        deleteGroup.add(deleteLink);


        // Write HTML data
        writer.write(toolbar.asXML());
    }

    /**
     * Utility method used to generate an image command link.
     *
     * @param href link URL
     * @param onclick onclick action, may be null
     * @param glyphicon glyphicon name
     * @param additionalHTMLClass additional HTML class
     * @return HTML "a" node
     */
    private Element generatePortletCommandLink(String href, String onclick, String glyphicon, String additionalHTMLClass) {
        // HTML "a" node
        DOMElement a = new DOMElement(QName.get(HTMLConstants.A));
        a.addAttribute(QName.get(HTMLConstants.HREF), href);
        if (onclick != null) {
            a.addAttribute(QName.get(HTMLConstants.ONCLICK), onclick);
        }

        // HTML class
        StringBuilder htmlClass = new StringBuilder();
        htmlClass.append("btn btn-default");
        if (additionalHTMLClass != null) {
            htmlClass.append(" ");
            htmlClass.append(additionalHTMLClass);
        }
        a.addAttribute(QName.get(HTMLConstants.CLASS), htmlClass.toString());

        // Glyphicon
        if (glyphicon != null) {
            Element glyph = new DOMElement(QName.get(HTMLConstants.I));
            glyph.addAttribute(QName.get(HTMLConstants.CLASS), "glyphicons " + glyphicon);
            glyph.setText(StringUtils.EMPTY);
            a.add(glyph);
        }

        return a;
    }


    /**
     * Prepare a fancybox for deleting the fragment.
     *
     * @param locale current locale
     * @param urlDelete delete fragment URL
     * @param fragmentId fragment identifier
     * @return fancybox div
     * @throws UnsupportedEncodingException
     */
    private Element generateDeleteFancyBox(Locale locale, String urlDelete, String fragmentId) throws UnsupportedEncodingException {
        String[] split = urlDelete.split("\\?");
        String action = split[0];
        String[] args = split[1].split("&");

        DOMElement div = new DOMElement(QName.get(HTMLConstants.DIV));
        div.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYBOX_CONTAINER);

        DOMElement innerDiv = new DOMElement(QName.get(HTMLConstants.DIV));
        innerDiv.addAttribute(QName.get(HTMLConstants.ID), "delete_".concat(fragmentId));
        div.add(innerDiv);

        Element form = new DOMElement(QName.get(HTMLConstants.FORM));
        form.addAttribute(QName.get(HTMLConstants.ID), "formDelete_".concat(fragmentId));
        form.addAttribute(QName.get(HTMLConstants.METHOD), HTMLConstants.FORM_METHOD_GET);
        form.addAttribute(QName.get(HTMLConstants.ACTION), action);
        form.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYBOX_FORM);
        innerDiv.add(form);

        Element divMsg = new DOMElement(QName.get(HTMLConstants.DIV));
        divMsg.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYBOX_CENTER_CONTENT);
        divMsg.setText(this.internationalizationService.getString(CMS_DELETE_CONFIRM_MESSAGE, locale));
        form.add(divMsg);

        Element divButton = new DOMElement(QName.get(HTMLConstants.DIV));
        divButton.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYBOX_CENTER_CONTENT);

        for (String arg : args) {
            Element hiddenArg = new DOMElement(QName.get(HTMLConstants.INPUT));
            hiddenArg.addAttribute(QName.get(HTMLConstants.TYPE), HTMLConstants.INPUT_TYPE_HIDDEN);
            hiddenArg.addAttribute(QName.get(HTMLConstants.NAME), arg.split("=")[0]);
            String value = arg.split("=")[1];
            hiddenArg.addAttribute(QName.get(HTMLConstants.VALUE), URLDecoder.decode(value, "UTF-8"));
            divButton.add(hiddenArg);
        }

        Element btnOk = new DOMElement(QName.get(HTMLConstants.INPUT));
        btnOk.addAttribute(QName.get(HTMLConstants.TYPE), HTMLConstants.INPUT_TYPE_SUBMIT);
        btnOk.addAttribute(QName.get(HTMLConstants.VALUE), this.internationalizationService.getString(InternationalizationConstants.KEY_YES, locale));
        divButton.add(btnOk);

        Element btnQuit = new DOMElement(QName.get(HTMLConstants.INPUT));
        btnQuit.addAttribute(QName.get(HTMLConstants.TYPE), HTMLConstants.INPUT_TYPE_BUTTON);
        btnQuit.addAttribute(QName.get(HTMLConstants.VALUE), this.internationalizationService.getString(InternationalizationConstants.KEY_NO, locale));
        btnQuit.addAttribute(QName.get(HTMLConstants.ONCLICK), "closeFancybox()");
        divButton.add(btnQuit);

        form.add(divButton);

        return div;
    }

}
