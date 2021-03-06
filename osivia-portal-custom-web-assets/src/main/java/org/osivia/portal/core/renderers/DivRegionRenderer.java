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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.dom.DOMElement;
import org.dom4j.io.HTMLWriter;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.theme.render.AbstractObjectRenderer;
import org.jboss.portal.theme.render.RenderException;
import org.jboss.portal.theme.render.RendererContext;
import org.jboss.portal.theme.render.renderer.DecorationRendererContext;
import org.jboss.portal.theme.render.renderer.RegionRenderer;
import org.jboss.portal.theme.render.renderer.RegionRendererContext;
import org.jboss.portal.theme.render.renderer.WindowRendererContext;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.html.AccessibilityRoles;
import org.osivia.portal.api.html.DOM4JUtils;
import org.osivia.portal.api.html.HTMLConstants;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.cms.CMSConfigurationItem;
import org.osivia.portal.core.cms.RegionInheritance;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.customizers.RegionsDefaultCustomizerPortlet;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.theming.IRegionRendererContext;
import org.osivia.portal.core.theming.RegionDecorator;

/**
 * Implementation of a Region renderer, based on div tags.
 *
 * @author <a href="mailto:mholzner@novell.com>Martin Holzner</a>
 * @author <a href="mailto:roy@jboss.org>Roy Russo</a>
 * @version $LastChangedRevision: 8784 $, $LastChangedDate: 2007-10-27 19:01:46 -0400 (Sat, 27 Oct 2007) $
 * @see org.jboss.portal.theme.render.renderer.RegionRenderer
 */
public class DivRegionRenderer extends AbstractObjectRenderer implements RegionRenderer {

    /** Bundle factory. */
    private final IBundleFactory bundleFactory;
    /** Portal URL factory. */
    private final IPortalUrlFactory portalURLFactory;

    /** list of regions in head. */
    private final List<String> headerRegions;


    /**
     * Constructor.
     */
    public DivRegionRenderer() {
        super();

        // Bundle factory
        IInternationalizationService internationalizationService = Locator.findMBean(IInternationalizationService.class,
                IInternationalizationService.MBEAN_NAME);
        this.bundleFactory = internationalizationService.getBundleFactory(this.getClass().getClassLoader());
        // Portal URL factory
        this.portalURLFactory = Locator.findMBean(IPortalUrlFactory.class, IPortalUrlFactory.MBEAN_NAME);

        this.headerRegions = new ArrayList<String>();
        this.headerRegions.add(RegionsDefaultCustomizerPortlet.REGION_HEADER_METADATA);
    }


    /**
     * {@inheritDoc}
     */
    public void renderHeader(RendererContext rendererContext, RegionRendererContext rrc) throws RenderException {
        IRegionRendererContext irrc = (IRegionRendererContext) rrc;

        // Locale
        Locale locale = null;
        String currentWindowId = PageProperties.getProperties().getCurrentWindowId();
        if (currentWindowId != null) {
            locale = LocaleUtils.toLocale(PageProperties.getProperties().getWindowProperty(currentWindowId, InternalConstants.LOCALE_PROPERTY));
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        Bundle bundle = this.bundleFactory.getBundle(locale);

        // Wizard mode indicator
        boolean wizard = false;
        Collection<?> windows = rrc.getWindows();
        if (CollectionUtils.isNotEmpty(windows)) {
            WindowRendererContext wrc = (WindowRendererContext) windows.iterator().next();
            String mode = wrc.getProperty(InternalConstants.ATTR_WINDOWS_SETTING_MODE);
            wizard = InternalConstants.VALUE_WINDOWS_SETTING_WIZARD_MODE.equals(mode);
        }

        PrintWriter markup = rendererContext.getWriter();

        // Main DIV region (not shown in <head> tag)
        if (!this.headerRegions.contains(rrc.getCSSId())) {
            markup.print("<div");

            if (rrc.getCSSId() != null) {
                markup.print(" id='");
                markup.print(rrc.getCSSId());
                markup.print("'");
            }

            if (wizard) {
                markup.print(" class='region wizard-edging clearfix'");
            }
            markup.print(">");
        }


        // Panel
        if (this.showCMSTools(irrc)) {
            boolean showAdvancedCMSTools = BooleanUtils.toBoolean(irrc.getProperty(InternalConstants.SHOW_ADVANCED_CMS_TOOLS_INDICATOR));
            boolean locked = BooleanUtils.toBoolean(irrc.getProperty(InternalConstants.INHERITANCE_LOCKED_INDICATOR_PROPERTY));

            markup.println("<div class='panel panel-default'><div class='panel-body'>");

            if (showAdvancedCMSTools) {
                markup.print("<p class='text-muted'><span>");
                markup.print(irrc.getId());
                markup.print("</span>");
                if (locked) {
                    markup.print(" <i class='halflings halflings-lock'></i>");
                }
                markup.println("</p>");
            }

            if (!locked) {
                this.printFragmentCommands(irrc, bundle, markup);
            }
        }

        // Region layout row
        if (StringUtils.isNotEmpty(irrc.getProperty(InternalConstants.CMS_REGION_LAYOUT_CODE))) {
            markup.println("<div class='row'>");
        }

        // Drag'n'drop
        if (this.showCMSTools(irrc) && !BooleanUtils.toBoolean(irrc.getProperty(InternalConstants.INHERITANCE_INDICATOR_PROPERTY))) {
            markup.print("<div id='region_");
            markup.print(rrc.getId());
            markup.print("' class='dnd-region clearfix' data-empty-title='");
            markup.print(bundle.getString("CMS_EMPTY_REGION"));
            markup.println("'>");
        }


        // Add portlet link
        this.addPortletLink(irrc, bundle, markup);


        // Theme name
        String themeName = null;
        if (rendererContext.getThemeContext().getTheme() != null) {
            themeName = rendererContext.getThemeContext().getTheme().getThemeInfo().getName();
        }


        // Maximized header
        if ("maximized".equals(irrc.getId()) && !"osivia-popup".equals(themeName)) {
            this.renderMaximizedHeader(rendererContext, rrc, bundle);
        }


        // Add header decorator
        RegionDecorator decorator = (RegionDecorator) rendererContext.getAttribute(InternalConstants.ATTR_REGIONS_DECORATORS);
        if ((decorator != null) && (decorator.getHeaderContent() != null)) {
            markup.println(decorator.getHeaderContent());
        }
    }


    /**
     * {@inheritDoc}
     */
    public void renderBody(RendererContext rendererContext, RegionRendererContext rrc) throws RenderException {
        IRegionRendererContext irrc = (IRegionRendererContext) rrc;
        boolean showCMSTools = this.showCMSTools(irrc);

        for (Iterator<?> i = rrc.getWindows().iterator(); i.hasNext();) {
            WindowRendererContext wrc = (WindowRendererContext) i.next();
            boolean empty = BooleanUtils.toBoolean(wrc.getProperty(InternalConstants.ATTR_WINDOWS_EMPTY_INDICATOR));
            boolean hidden = BooleanUtils.toBoolean(wrc.getProperty(InternalConstants.ATTR_WINDOWS_HIDDEN_INDICATOR));

            if (!(hidden || (!showCMSTools && empty))) {
                String regionLayoutWindowClass = rendererContext.getProperty(InternalConstants.CMS_REGION_LAYOUT_CLASS);

                PrintWriter markup = rendererContext.getWriter();

                if (!this.headerRegions.contains(rrc.getCSSId())) {

	                markup.print("<div class='");
	                markup.print(StringUtils.trimToEmpty(regionLayoutWindowClass));
	                markup.println("'>");
                }

                rendererContext.render(wrc);

                if (!this.headerRegions.contains(rrc.getCSSId())) {
                	markup.println("</div>");
                }
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void renderFooter(RendererContext rendererContext, RegionRendererContext rrc) throws RenderException {
        IRegionRendererContext irrc = (IRegionRendererContext) rrc;
        PrintWriter markup = rendererContext.getWriter();

        // Add footer decorator
        RegionDecorator decorator = (RegionDecorator) rendererContext.getAttribute(InternalConstants.ATTR_REGIONS_DECORATORS);
        if ((decorator != null) && (decorator.getFooterContent() != null)) {
            markup.println(decorator.getFooterContent());
        }


        // Drag'n'drop
        if (this.showCMSTools(irrc) && !BooleanUtils.toBoolean(irrc.getProperty(InternalConstants.INHERITANCE_INDICATOR_PROPERTY))) {
            markup.print("</div>");
        }

        // Region layout row
        if (StringUtils.isNotEmpty(irrc.getProperty(InternalConstants.CMS_REGION_LAYOUT_CODE))) {
            markup.println("</div>");
        }

        // Panel
        if (this.showCMSTools(irrc)) {
            markup.print("</div></div>");
        }


        // End of Main DIV region (not shown in <head> tag)
        if (!this.headerRegions.contains(rrc.getCSSId())) {
            markup.print("</div>");
        }
    }


    /**
     * Display CMS Tools if region is marked "CMS" (dynamic region) and if the tools are enabled in the session.
     *
     * @param irrc region renderer context
     * @return true if CMS tools must be shown
     */
    private boolean showCMSTools(IRegionRendererContext irrc) {
        boolean showCMSTools = BooleanUtils.toBoolean(irrc.getProperty(InternalConstants.SHOW_CMS_TOOLS_INDICATOR_PROPERTY));
        boolean showAdvancedCMSTools = BooleanUtils.toBoolean(irrc.getProperty(InternalConstants.SHOW_ADVANCED_CMS_TOOLS_INDICATOR));
        boolean inherited = BooleanUtils.toBoolean(irrc.getProperty(InternalConstants.INHERITANCE_INDICATOR_PROPERTY));

        return irrc.isCMS() && showCMSTools && (showAdvancedCMSTools || !inherited);
    }


    /**
     * Utility method used to add portlet link.
     *
     * @param irrc region renderer context
     * @param bundle internationalization bundle
     * @param markup markup
     * @throws RenderException
     */
    private void addPortletLink(IRegionRendererContext irrc, Bundle bundle, PrintWriter markup) throws RenderException {
        // Lien d'ajout de portlet
        if (InternalConstants.VALUE_WINDOWS_WIZARD_TEMPLATE_MODE.equals(irrc.getProperty(InternalConstants.ATTR_WINDOWS_WIZARD_MODE))) {
            // Button
            Element button = new DOMElement(QName.get(HTMLConstants.A));

            String href;
            String text;
            if (irrc.isCMS()) {
                href = HTMLConstants.A_HREF_DEFAULT;
                text = bundle.getString("REGION_CMS");
                button.addAttribute(QName.get(HTMLConstants.DISABLED), HTMLConstants.DISABLED);
            } else {
                href = irrc.getProperty(InternalConstants.ATTR_WINDOWS_ADD_PORTLET_URL);
                text = bundle.getString("REGION_TEMPLATE");
                button.addAttribute(QName.get(HTMLConstants.ONCLICK), "regionId = '" + irrc.getId() + "'");

                // Glyph
                Element glyph = new DOMElement(QName.get(HTMLConstants.I));
                glyph.addAttribute(QName.get(HTMLConstants.CLASS), "glyphicons glyphicons-plus");
                glyph.setText(StringUtils.EMPTY);
                button.add(glyph);
                button.addText(" ");
            }

            DOM4JUtils.addAttribute(button, HTMLConstants.HREF, "javascript:;");
            DOM4JUtils.addAttribute(button, HTMLConstants.CLASS, "btn btn-secondary");
            DOM4JUtils.addDataAttribute(button, "fancybox", StringUtils.EMPTY);
            DOM4JUtils.addDataAttribute(button, "src", href);
            button.addText(text + irrc.getId());

            // Write HTML data
            markup.write(button.asXML());
        }
    }


    /**
     * Print fragment commands.
     *
     * @param irrc region renderer context
     * @param bundle internationalization bundle
     * @param markup print writer markup
     */
    private void printFragmentCommands(IRegionRendererContext irrc, Bundle bundle, PrintWriter markup) {
        // Inherited indicator
        boolean inherited = BooleanUtils.toBoolean(irrc.getProperty(InternalConstants.INHERITANCE_INDICATOR_PROPERTY));


        // Parent DIV
        Element parent = DOM4JUtils.generateDivElement(null);

        // Toolbar
        String toolbarId = StringEscapeUtils.escapeHtml(irrc.getId() + "-toolbar");
        Element toolbar = DOM4JUtils.generateDivElement("btn-toolbar", AccessibilityRoles.TOOLBAR);
        DOM4JUtils.addAttribute(toolbar, "id", toolbarId);
        parent.add(toolbar);


        // Button group
        Element group = DOM4JUtils.generateDivElement("btn-group");
        toolbar.add(group);


        // Add fragment button
        Element addFragmentButton;
        if (inherited) {
            addFragmentButton = DOM4JUtils.generateElement(HTMLConstants.P, "btn btn-secondary disabled", null, "halflings halflings-plus", null);
        } else {
            String addFragmentURL = irrc.getProperty("osivia.cmsCreateUrl");
            StringBuilder addFragmentOnClick = new StringBuilder();
            addFragmentOnClick.append("callbackUrl='");
            addFragmentOnClick.append(irrc.getProperty("osivia.cmsCreateCallBackURL"));
            addFragmentOnClick.append("#");
            addFragmentOnClick.append(toolbarId);
            addFragmentOnClick.append("'; setCallbackFromEcmParams('', '");
            addFragmentOnClick.append(irrc.getProperty("osivia.ecmBaseUrl"));
            addFragmentOnClick.append("');");

            addFragmentButton = DOM4JUtils.generateLinkElement(addFragmentURL, null, addFragmentOnClick.toString(), "btn btn-secondary fancyframe_refresh", null,
                    "halflings halflings-plus");
        }
        DOM4JUtils.addTooltip(addFragmentButton, bundle.getString("CMS_ADD_FRAGMENT"));
        group.add(addFragmentButton);


        // Advanced CMS tools
        boolean showAdvancedTools = BooleanUtils.toBoolean(irrc.getProperty(InternalConstants.SHOW_ADVANCED_CMS_TOOLS_INDICATOR));
        if (showAdvancedTools) {
            // Inheritance menu
            this.generateInheritanceMenu(irrc, toolbar, bundle);

            // Region layout menu
            this.generateRegionLayoutMenu(irrc, toolbar, bundle);
        }

        // Write HTML
        HTMLWriter htmlWriter = new HTMLWriter(markup);
        htmlWriter.setEscapeText(false);
        try {
            htmlWriter.write(parent);
        } catch (IOException e) {
            // Do nothing
        }
    }


    /**
     * Generate inheritance menu.
     *
     * @param irrc region renderer context
     * @param toolbar parent toolbar DOM element
     * @param bundle bundle
     */
    private void generateInheritanceMenu(IRegionRendererContext irrc, Element toolbar, Bundle bundle) {
        // Inheritance
        RegionInheritance inheritance = RegionInheritance.fromValue(irrc.getProperty(InternalConstants.INHERITANCE_VALUE_REGION_PROPERTY));
        // Save URL
        String saveURL = irrc.getProperty(InternalConstants.INHERITANCE_SAVE_URL);
        // Radio name
        String radioName = "inheritance";


        // Dropdown menu container
        Element dropdownContainer = DOM4JUtils.generateDivElement("btn-group");
        toolbar.add(dropdownContainer);

        // Dropdown menu button
        Element dropdownButton = DOM4JUtils.generateElement(HTMLConstants.BUTTON, "btn btn-secondary dropdown-toggle", HTMLConstants.TEXT_DEFAULT,
                "halflings halflings-wrench", null);
        DOM4JUtils.addAttribute(dropdownButton, HTMLConstants.DATA_TOGGLE, "dropdown");
        Element caret = DOM4JUtils.generateElement(HTMLConstants.SPAN, "caret", StringUtils.EMPTY);
        dropdownButton.add(caret);
        dropdownContainer.add(dropdownButton);

        // Dropdown menu
        Element dropdownMenu = DOM4JUtils.generateElement(HTMLConstants.UL, "dropdown-menu", null, null, AccessibilityRoles.MENU);
        dropdownContainer.add(dropdownMenu);

        // Dropdown header
        Element dropdownHeader = DOM4JUtils.generateElement(HTMLConstants.LI, "dropdown-header", bundle.getString("CMS_REGION_INHERITANCE_HEADER"), null,
                AccessibilityRoles.PRESENTATION);
        dropdownMenu.add(dropdownHeader);

        // Dropdown item
        Element dropdownItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null, null, AccessibilityRoles.PRESENTATION);
        dropdownMenu.add(dropdownItem);

        // Form
        Element form = DOM4JUtils.generateElement(HTMLConstants.FORM, "form", null, null, AccessibilityRoles.FORM);
        DOM4JUtils.addAttribute(form, HTMLConstants.ACTION, StringUtils.substringBefore(saveURL, "?"));
        DOM4JUtils.addAttribute(form, HTMLConstants.METHOD, HTMLConstants.FORM_METHOD_GET);
        dropdownItem.add(form);

        // Hidden inputs
        for (String parameter : StringUtils.split(StringUtils.substringAfter(saveURL, "?"), "&")) {
            Element hiddenInput = DOM4JUtils.generateElement(HTMLConstants.INPUT, null, null);
            DOM4JUtils.addAttribute(hiddenInput, HTMLConstants.TYPE, "hidden");
            DOM4JUtils.addAttribute(hiddenInput, HTMLConstants.NAME, StringUtils.substringBefore(parameter, "="));
            try {
                DOM4JUtils
                        .addAttribute(hiddenInput, HTMLConstants.VALUE, URLDecoder.decode(StringUtils.substringAfter(parameter, "="), CharEncoding.UTF_8));
            } catch (UnsupportedEncodingException e) {
                // Do nothing
            }
            form.add(hiddenInput);
        }


        for (RegionInheritance value : RegionInheritance.values()) {
            // Radio
            String label = bundle.getString(value.getInternationalizationKey());
            String helpMessage = bundle.getString(value.getInternationalizationKey() + "_HELP");
            this.generateRadio(form, label, radioName, StringUtils.trimToEmpty(value.getValue()), value.equals(inheritance), helpMessage);
        }

        // Submit button
        Element submit = DOM4JUtils.generateElement(HTMLConstants.BUTTON, "btn btn-secondary btn-primary", bundle.getString("SAVE"));
        DOM4JUtils.addAttribute(submit, HTMLConstants.TYPE, "submit");
        form.add(submit);
    }


    /**
     * Generate region layout menu.
     *
     * @param irrc region renderer context
     * @param toolbar parent toolbar DOM element
     * @param bundle bundle
     */
    private void generateRegionLayoutMenu(IRegionRendererContext irrc, Element toolbar, Bundle bundle) {
        // Selected region layout
        String regionLayoutCode = irrc.getProperty(InternalConstants.CMS_REGION_LAYOUT_CODE);
        // Inherited indicator
        boolean inherited = BooleanUtils.toBoolean(irrc.getProperty(InternalConstants.INHERITANCE_INDICATOR_PROPERTY));
        // Save URL
        String saveURL = irrc.getProperty(InternalConstants.CMS_REGION_LAYOUT_SAVE_URL);
        // Radio name
        String radioName = "regionLayout";


        // Dropdown menu container
        Element dropdownContainer = DOM4JUtils.generateDivElement("btn-group");
        toolbar.add(dropdownContainer);

        // Dropdown menu button
        Element dropdownButton;
        if (inherited) {
            dropdownButton = DOM4JUtils.generateElement(HTMLConstants.BUTTON, "btn btn-secondary dropdown-toggle disabled", HTMLConstants.TEXT_DEFAULT,
                    "glyphicons glyphicons-sampler", null);
        } else {
            dropdownButton = DOM4JUtils.generateElement(HTMLConstants.BUTTON, "btn btn-secondary dropdown-toggle", HTMLConstants.TEXT_DEFAULT,
                    "glyphicons glyphicons-sampler", null);
            DOM4JUtils.addAttribute(dropdownButton, HTMLConstants.DATA_TOGGLE, "dropdown");
        }
        Element caret = DOM4JUtils.generateElement(HTMLConstants.SPAN, "caret", StringUtils.EMPTY);
        dropdownButton.add(caret);
        dropdownContainer.add(dropdownButton);


        if (!inherited) {
            // Dropdown menu
            Element dropdownMenu = DOM4JUtils.generateElement(HTMLConstants.UL, "dropdown-menu", null, null, AccessibilityRoles.MENU);
            dropdownContainer.add(dropdownMenu);

            // Dropdown header
            Element dropdownHeader = DOM4JUtils.generateElement(HTMLConstants.LI, "dropdown-header", bundle.getString("CMS_REGION_LAYOUT_HEADER"), null,
                    AccessibilityRoles.PRESENTATION);
            dropdownMenu.add(dropdownHeader);

            // Dropdown item
            Element dropdownItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null, null, AccessibilityRoles.PRESENTATION);
            dropdownMenu.add(dropdownItem);

            // Form
            Element form = DOM4JUtils.generateElement(HTMLConstants.FORM, "form", null, null, AccessibilityRoles.FORM);
            DOM4JUtils.addAttribute(form, HTMLConstants.ACTION, StringUtils.substringBefore(saveURL, "?"));
            DOM4JUtils.addAttribute(form, HTMLConstants.METHOD, HTMLConstants.FORM_METHOD_GET);
            dropdownItem.add(form);

            // Hidden inputs
            for (String parameter : StringUtils.split(StringUtils.substringAfter(saveURL, "?"), "&")) {
                Element hiddenInput = DOM4JUtils.generateElement(HTMLConstants.INPUT, null, null);
                DOM4JUtils.addAttribute(hiddenInput, HTMLConstants.TYPE, "hidden");
                DOM4JUtils.addAttribute(hiddenInput, HTMLConstants.NAME, StringUtils.substringBefore(parameter, "="));
                try {
                    DOM4JUtils
                            .addAttribute(hiddenInput, HTMLConstants.VALUE, URLDecoder.decode(StringUtils.substringAfter(parameter, "="), CharEncoding.UTF_8));
                } catch (UnsupportedEncodingException e) {
                    // Do nothing
                }
                form.add(hiddenInput);
            }

            // Default region layout radio
            this.generateRadio(form, bundle.getString("DEFAULT_REGION_LAYOUT"), radioName, StringUtils.EMPTY, StringUtils.isEmpty(regionLayoutCode), null);

            // Region layouts
            Set<CMSConfigurationItem> regionLayouts = PageProperties.getProperties().getRegionLayouts();
            for (CMSConfigurationItem regionLayout : regionLayouts) {
                // Radio
                boolean selected = StringUtils.equals(regionLayout.getCode(), regionLayoutCode);
                this.generateRadio(form, regionLayout.getName(), radioName, regionLayout.getCode(), selected, null);
            }


            // Submit button
            Element submit = DOM4JUtils.generateElement(HTMLConstants.BUTTON, "btn btn-secondary btn-primary", bundle.getString("SAVE"));
            DOM4JUtils.addAttribute(submit, HTMLConstants.TYPE, "submit");
            form.add(submit);
        }
    }


    /**
     * Generate radio DOM element.
     *
     * @param form parent form DOM element
     * @param label radio label
     * @param name radio name
     * @param value radio value
     * @param checked radio checked indicator
     * @param helpMessage help message, may be null
     */
    private void generateRadio(Element form, String label, String name, String value, boolean checked, String helpMessage) {
        // Radio container
        Element radioContainer = DOM4JUtils.generateDivElement("radio");
        form.add(radioContainer);

        // Radio label
        Element radioLabel = DOM4JUtils.generateElement(HTMLConstants.LABEL, null, null);
        radioContainer.add(radioLabel);

        // Radio input
        Element radioInput = DOM4JUtils.generateElement(HTMLConstants.INPUT, null, null);
        DOM4JUtils.addAttribute(radioInput, HTMLConstants.TYPE, "radio");
        DOM4JUtils.addAttribute(radioInput, HTMLConstants.NAME, name);
        DOM4JUtils.addAttribute(radioInput, HTMLConstants.VALUE, value);
        if (checked) {
            DOM4JUtils.addAttribute(radioInput, HTMLConstants.CHECKED, HTMLConstants.CHECKED);
        }
        radioLabel.add(radioInput);

        radioLabel.setText(label);

        if (StringUtils.isNotEmpty(helpMessage)) {
            // Help message
            Element radioHelp = DOM4JUtils.generateElement(HTMLConstants.SPAN, "help-block", helpMessage);
            radioLabel.add(radioHelp);
        }
    }


    /**
     * Render maximized header.
     * 
     * @param rendererContext renderer context
     * @param regionRendererContext region renderer context
     * @param bundle internationalization bundle
     * @throws RenderException
     */
    private void renderMaximizedHeader(RendererContext rendererContext, RegionRendererContext regionRendererContext, Bundle bundle) throws RenderException {
        // HTTP servlet request
        HttpServletRequest request = rendererContext.getDispatcher().getRequest();
        // Controller context
        ControllerContext controllerContext = (ControllerContext) request.getAttribute(InternalConstants.ATTR_CONTROLLER_CONTEXT);
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(controllerContext);


        // Window title, sub-title and vignette URL
        String windowTitle = null;
        String windowSubTitle = null;
        String windowVignetteUrl = null;
        Collection<?> windows = regionRendererContext.getWindows();
        if (windows.size() == 1) {
            WindowRendererContext windowRendererContext = (WindowRendererContext) windows.iterator().next();
            String id = windowRendererContext.getId();
            PageProperties properties = PageProperties.getProperties();

            if ("1".equals(properties.getWindowProperty(id, "osivia.displayTitle"))) {
                windowTitle = properties.getWindowProperty(id, InternalConstants.PROP_WINDOW_TITLE);
                if (windowTitle == null) {
                    DecorationRendererContext decoration = windowRendererContext.getDecoration();
                    windowTitle = decoration.getTitle();
                }

                windowSubTitle = properties.getWindowProperty(id, InternalConstants.PROP_WINDOW_SUB_TITLE);
                windowVignetteUrl = properties.getWindowProperty(id, InternalConstants.PROP_WINDOW_VIGNETTE_URL);
            }
        }


        // Page header
        Element pageHeader = DOM4JUtils.generateDivElement("page-header d-none d-md-flex mb-3 align-items-center");


        String backURL = this.portalURLFactory.getBackURL(portalControllerContext, false);
        if (backURL != null) {
            // Back
            Element backButton = DOM4JUtils.generateLinkElement(backURL, null, null, "btn btn-outline-primary btn-sm mr-3", null, "glyphicons glyphicons-basic-step-back");
            DOM4JUtils.addTooltip(backButton, bundle.getString("BACK"));
            pageHeader.add(backButton);
        }


        // Title
        if (windowTitle != null) {
//            if (StringUtils.isNotEmpty(windowVignetteUrl)) {
//                Element vignette = DOM4JUtils.generateElement(HTMLConstants.IMG, "pull-right", null);
//                DOM4JUtils.addAttribute(vignette, HTMLConstants.SRC, windowVignetteUrl);
//                DOM4JUtils.addAttribute(vignette, HTMLConstants.ALT, StringUtils.EMPTY);
//                pageHeader.add(vignette);
//            }
//
//            Element titleContainer = DOM4JUtils.generateDivElement("title-container");
//            pageHeader.add(titleContainer);

            Element title = DOM4JUtils.generateElement(HTMLConstants.DIV, "h2 m-0", windowTitle);
            pageHeader.add(title);

//            if (StringUtils.isNotBlank(windowSubTitle)) {
//                windowSubTitle = StringUtils.replace(windowSubTitle, "\n", HTMLConstants.LINE_BREAK);
//                Element subTitle = DOM4JUtils.generateElement(HTMLConstants.DIV, "text-muted", windowSubTitle);
//                titleContainer.add(subTitle);
//            }
        }


        // HTML writer
        if (pageHeader.hasContent()) {
            HTMLWriter htmlWriter = new HTMLWriter(rendererContext.getWriter());
            htmlWriter.setEscapeText(false);
            try {
                htmlWriter.write(pageHeader);
            } catch (IOException e) {
                throw new RenderException(e);
            }
        }
    }

}
