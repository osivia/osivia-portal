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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Locale;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.LocaleUtils;
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
import org.osivia.portal.api.html.AccessibilityRoles;
import org.osivia.portal.api.html.DOM4JUtils;
import org.osivia.portal.api.html.HTMLConstants;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
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

    /** Fancybox class, required for link. */
    private static final String CLASS_FANCYBOX_INLINE = "fancybox_inline";
    /** Fancybox with title class, required for link. */
    private static final String CLASS_FANCYBOX_INLINE_TITLE = "fancybox_inline_title";
    /** Fancybox class, required for link. */
    private static final String CLASS_FANCYBOX_FRAME = "fancyframe_refresh";
    /** Delete portlet message. */
    private static final String CMS_DELETE_CONFIRM_MESSAGE = "CMS_DELETE_CONFIRM_MESSAGE";

    /** Bundle factory. */
    private final IBundleFactory bundleFactory;


    /**
     * Constructor.
     */
    public DivWindowRenderer() {
        super();

        // Bundle factory
        IInternationalizationService internationalizationService = Locator.findMBean(IInternationalizationService.class,
                IInternationalizationService.MBEAN_NAME);
        this.bundleFactory = internationalizationService.getBundleFactory(this.getClass().getClassLoader());
    }


    /**
     * {@inheritDoc}
     */
    public void render(RendererContext rendererContext, WindowRendererContext wrc) throws RenderException {
        Locale locale = LocaleUtils.toLocale(PageProperties.getProperties().getWindowProperty(wrc.getId(), InternalConstants.LOCALE_PROPERTY));
        Bundle bundle = this.bundleFactory.getBundle(locale);

        PrintWriter out = rendererContext.getWriter();

        PageProperties properties = PageProperties.getProperties();
        // Set current window identifier for decorators
        properties.setCurrentWindowId(wrc.getId());

        // Show CMS tools indicator
        boolean showCMSTools = this.showCMSTools(wrc);

        // Mobile collapse indicator
        boolean mobileCollapse = BooleanUtils.toBoolean(properties.getWindowProperty(wrc.getId(), "osivia.mobileCollapse"));
        // Bootstrap panel style indicator
        boolean bootstrapPanelStyle = BooleanUtils.toBoolean(properties.getWindowProperty(wrc.getId(), "osivia.bootstrapPanelStyle"));
        // Hide portlet indicator
        boolean hidePortlet = !showCMSTools && "1".equals(properties.getWindowProperty(wrc.getId(), "osivia.hidePortlet"));
        // AJAX links indicator
        boolean ajaxLink = "1".equals(properties.getWindowProperty(wrc.getId(), "osivia.ajaxLink"));

        if (hidePortlet) {
            // v2.0.22 : portlet vide non rafraichi en ajax
            out.println("<div class=\"dyna-window-content\" ></div>");
            return;
        }

        // Wizard mode indicator
        String windowsSettingMode = wrc.getProperty(InternalConstants.ATTR_WINDOWS_SETTING_MODE);
        boolean wizard = InternalConstants.VALUE_WINDOWS_SETTING_WIZARD_MODE.equals(windowsSettingMode);

        // Window identifier
        String windowId = wrc.getProperty("osivia.windowId");


        // Styles
        String styles = properties.getWindowProperty(wrc.getId(), "osivia.style");
        if (styles != null) {
            styles = styles.replaceAll(",", " ");
        } else {
            styles = StringUtils.EMPTY;
        }


        // Window root element
        out.print("<div");
        if (windowId != null) {
            out.print(" id='");
            out.print(windowId);
            out.print("'");
        }
        if (!ajaxLink || wizard) {
            out.print(" class='no-ajax-link'");
        }
        out.print(">");


        // Dyna window content
        out.print("<div class='dyna-window-content");
        if (showCMSTools) {
            out.print(" well well-sm clearfix");
        }
        out.print("'>");


        // Edit / remove fragment actions
        if (showCMSTools) {
            // Delete confirmation fancybox
            Element deleteConfirmation;
            try {
                deleteConfirmation = this.generateDeleteFancyBox(bundle, wrc.getProperty("osivia.cmsDeleteUrl"), wrc.getProperty("osivia.windowId"));
            } catch (UnsupportedEncodingException e) {
                throw new RenderException(e);
            }


            // CMS commands toolbar
            Element toolbar = DOM4JUtils.generateDivElement("btn-toolbar", AccessibilityRoles.TOOLBAR);

            // Buttons group
            Element buttonsGroup = DOM4JUtils.generateDivElement("btn-group btn-group-sm");
            toolbar.add(buttonsGroup);

            // Edition button
            String editionURL = wrc.getProperty("osivia.cmsEditUrl");
            StringBuilder editionOnClick = new StringBuilder();
            editionOnClick.append("callbackUrl='");
            editionOnClick.append(rendererContext.getProperty("osivia.cmsEditCallbackUrl"));
            editionOnClick.append("'; callBackId='");
            editionOnClick.append(rendererContext.getProperty("osivia.cmsEditCallbackId"));
            editionOnClick.append("'; setCallbackFromEcmParams('', '");
            editionOnClick.append(rendererContext.getProperty("osivia.ecmBaseUrl"));
            editionOnClick.append("');");
            String editionTitle = bundle.getString("CMS_EDIT_FRAGMENT");
            Element edition = DOM4JUtils.generateLinkElement(editionURL, null, editionOnClick.toString(), "btn btn-default fancyframe_refresh", editionTitle,
                    "halflings pencil");
            buttonsGroup.add(edition);

            // Delete button
            String deleteURL = "#delete_" + windowId;
            String deleteTitle = bundle.getString("CMS_DELETE_FRAGMENT");
            Element delete = DOM4JUtils.generateLinkElement(deleteURL, null, null, "btn btn-default no-ajax-link fancybox_inline", null, "halflings remove");
            DOM4JUtils.addTooltip(delete, deleteTitle);
            buttonsGroup.add(delete);


            // Write HTML
            HTMLWriter htmlWriter = new HTMLWriter(out);
            htmlWriter.setEscapeText(false);
            try {
                htmlWriter.write(deleteConfirmation);
                htmlWriter.write(toolbar);
            } catch (IOException e) {
                // Do nothing
            }
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
            out.print("<section class='panel panel-default portlet-container " + styles + "'>");
        } else {
            out.print("<section class='portlet-container " + styles + "'>");
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
            if (!"1".equals(properties.getWindowProperty(wrc.getId(), "osivia.displayTitle"))) {
                headerClass += " hidden";
            }

            // Header
            out.print("<div class='" + headerClass + " clearfix'>");
            rendererContext.render(wrc.getDecoration());
            out.print("</div>");

            // Body
            if (mobileCollapse) {
                out.print("<div id='body_");
                out.print(wrc.getId());
                out.print("' class='panel-collapse collapse");

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
        out.print("</section>");
        // Wizard edging
        if (wizard) {
            out.print("</div>");
        }
        // Dyna window content
        out.print("</div>");

        // in cms mode, create a new fragment below the current window
        if (showCMSTools) {
            // Toolbar
            Element toolbar = DOM4JUtils.generateDivElement("btn-toolbar", AccessibilityRoles.TOOLBAR);

            // Button group
            Element group = DOM4JUtils.generateDivElement("btn-group");
            toolbar.add(group);

            // Add fragment
            String addFragmentURL = wrc.getProperty("osivia.cmsCreateUrl");
            StringBuilder addFragmentOnClick = new StringBuilder();
            addFragmentOnClick.append("callbackUrl='");
            addFragmentOnClick.append(wrc.getProperty("osivia.cmsCreateCallBackURL"));
            addFragmentOnClick.append("'; setCallbackFromEcmParams('', '");
            addFragmentOnClick.append(rendererContext.getProperty("osivia.ecmBaseUrl"));
            addFragmentOnClick.append("');");

            Element addFragmentButton = DOM4JUtils.generateLinkElement(addFragmentURL, null, addFragmentOnClick.toString(),
                    "btn btn-default fancyframe_refresh", null, "halflings plus");
            DOM4JUtils.addTooltip(addFragmentButton, bundle.getString("CMS_ADD_FRAGMENT"));
            group.add(addFragmentButton);

            // Write HTML
            HTMLWriter htmlWriter = new HTMLWriter(out);
            htmlWriter.setEscapeText(false);
            try {
                htmlWriter.write(toolbar);
            } catch (IOException e) {
                // Do nothing
            }
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
    private boolean showCMSTools(WindowRendererContext wrc) {
        boolean showCmsTools = false;

        if (wrc instanceof WindowContext) {
            WindowContext wc = (WindowContext) wrc;

            if (BooleanUtils.isTrue(wc.getRegionCms()) && (wrc.getProperty("osivia.windowId") != null)) {
                showCmsTools = BooleanUtils.toBoolean(wrc.getProperty(InternalConstants.SHOW_CMS_TOOLS_INDICATOR_PROPERTY))
                        && !BooleanUtils.toBoolean(wrc.getProperty(InternalConstants.INHERITANCE_INDICATOR_PROPERTY));
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


        String windowTitle = properties.getWindowProperty(windowId, InternalConstants.PROP_WINDOW_TITLE);
        if (windowTitle == null) {
            windowTitle = StringUtils.EMPTY;
        }

        String instanceName = windowRendererContext.getProperty(InternalConstants.ATTR_WINDOWS_INSTANCE_DISPLAY_NAME);
        windowTitle += "   [" + instanceName + "]";

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
        Element upLink = this.generatePortletCommandLink(upUrl, null, "halflings arrow-up", null, null);
        moveGroup.add(upLink);

        // Down move command
        String downUrl = windowRendererContext.getProperty(InternalConstants.ATTR_WINDOWS_DOWN_COMMAND_URL);
        Element downLink = this.generatePortletCommandLink(downUrl, null, "glyphicons halflings arrow-down", null, null);
        moveGroup.add(downLink);

        // Previous region move command
        String previousRegionUrl = windowRendererContext.getProperty(InternalConstants.ATTR_WINDOWS_PREVIOUS_REGION_COMMAND_URL);
        Element previousRegionLink = this.generatePortletCommandLink(previousRegionUrl, null, "halflings arrow-left", null, null);
        moveGroup.add(previousRegionLink);

        // Next region move command
        String nextRegionUrl = windowRendererContext.getProperty(InternalConstants.ATTR_WINDOWS_NEXT_REGION_COMMAND_URL);
        Element nextRegionLink = this.generatePortletCommandLink(nextRegionUrl, null, "halflings arrow-right", null, null);
        moveGroup.add(nextRegionLink);


        // Settings commands group
        Element settingsGroup = new DOMElement(QName.get(HTMLConstants.DIV));
        settingsGroup.addAttribute(QName.get(HTMLConstants.CLASS), "btn-group btn-group-sm");
        toolbar.add(settingsGroup);

        // Window settings display command
        String displaySettingsUrl = windowRendererContext.getProperty(InternalConstants.ATTR_WINDOWS_DISPLAY_SETTINGS_URL);
        Element displaySettingsLink = this.generatePortletCommandLink(displaySettingsUrl, onclickAction, "halflings uni-wrench", CLASS_FANCYBOX_INLINE_TITLE, windowTitle);
        settingsGroup.add(displaySettingsLink);

        // Portlet administration display command
        Collection<ActionRendererContext> actions = windowRendererContext.getDecoration().getTriggerableActions(ActionRendererContext.MODES_KEY);
        for (ActionRendererContext action : actions) {
            if ((InternalConstants.ACTION_ADMIN.equals(action.getName())) && (action.isEnabled())) {
                String link = action.getURL() + "&windowstate=maximized";
                Element displayAdminLink = this.generatePortletCommandLink(link, onclickAction, "halflings cog", CLASS_FANCYBOX_FRAME, windowTitle);
                settingsGroup.add(displayAdminLink);
            }
        }

        // Delete command group
        Element deleteGroup = new DOMElement(QName.get(HTMLConstants.DIV));
        deleteGroup.addAttribute(QName.get(HTMLConstants.CLASS), "btn-group btn-group-sm");
        toolbar.add(deleteGroup);

        // Delete portlet command
        String deleteUrl = windowRendererContext.getProperty(InternalConstants.ATTR_WINDOWS_DELETE_PORTLET_URL);
        Element deleteLink = this.generatePortletCommandLink(deleteUrl, onclickAction, "halflings remove", CLASS_FANCYBOX_INLINE, null);
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
    private Element generatePortletCommandLink(String href, String onclick, String glyphicon, String additionalHTMLClass, String title) {
        // HTML "a" node
        DOMElement a = new DOMElement(QName.get(HTMLConstants.A));
        a.addAttribute(QName.get(HTMLConstants.HREF), href);
        if (onclick != null) {
            a.addAttribute(QName.get(HTMLConstants.ONCLICK), onclick);
        }
        if (title != null) {
            a.addAttribute(QName.get(HTMLConstants.TITLE), title);
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
     * @param bundle internationalization bundle
     * @param urlDelete delete fragment URL
     * @param fragmentId fragment identifier
     * @return fancybox div
     * @throws UnsupportedEncodingException
     */
    private Element generateDeleteFancyBox(Bundle bundle, String urlDelete, String fragmentId) throws UnsupportedEncodingException {
        String id = "delete_".concat(fragmentId);

        String[] splitURL = urlDelete.split("\\?");
        String action = splitURL[0];
        String[] args = splitURL[1].split("&");


        // Root
        Element root = DOM4JUtils.generateDivElement("hidden");

        // Container
        Element container = DOM4JUtils.generateDivElement("container-fluid");
        DOM4JUtils.addAttribute(container, HTMLConstants.ID, id);
        root.add(container);

        // Form
        Element form = DOM4JUtils.generateElement(HTMLConstants.FORM, "text-center", null, null, AccessibilityRoles.FORM);
        DOM4JUtils.addAttribute(form, HTMLConstants.ACTION, action);
        DOM4JUtils.addAttribute(form, HTMLConstants.METHOD, HTMLConstants.FORM_METHOD_GET);
        container.add(form);

        // Message
        Element message = DOM4JUtils.generateElement(HTMLConstants.P, null, bundle.getString(CMS_DELETE_CONFIRM_MESSAGE));
        form.add(message);

        // Hidden fields
        for (String arg : args) {
            String[] argSplit = arg.split("=");
            Element hidden = DOM4JUtils.generateElement(HTMLConstants.INPUT, null, null);
            DOM4JUtils.addAttribute(hidden, HTMLConstants.TYPE, HTMLConstants.INPUT_TYPE_HIDDEN);
            DOM4JUtils.addAttribute(hidden, HTMLConstants.NAME, argSplit[0]);
            DOM4JUtils.addAttribute(hidden, HTMLConstants.VALUE, URLDecoder.decode(argSplit[1], CharEncoding.UTF_8));
            form.add(hidden);
        }

        // OK button
        Element okButton = DOM4JUtils.generateElement(HTMLConstants.BUTTON, "btn btn-default btn-warning", bundle.getString("YES"), "halflings warning-sign",
                null);
        DOM4JUtils.addAttribute(okButton, HTMLConstants.TYPE, HTMLConstants.INPUT_TYPE_SUBMIT);
        form.add(okButton);

        // Cancel button
        Element cancelButton = DOM4JUtils.generateElement(HTMLConstants.BUTTON, "btn btn-default", bundle.getString("NO"));
        DOM4JUtils.addAttribute(cancelButton, HTMLConstants.TYPE, HTMLConstants.INPUT_TYPE_BUTTON);
        DOM4JUtils.addAttribute(cancelButton, HTMLConstants.ONCLICK, "closeFancybox()");
        form.add(cancelButton);

        return root;
    }

}
