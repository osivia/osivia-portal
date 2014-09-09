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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.dom.DOMElement;
import org.dom4j.io.HTMLWriter;
import org.jboss.portal.theme.render.AbstractObjectRenderer;
import org.jboss.portal.theme.render.RenderException;
import org.jboss.portal.theme.render.RendererContext;
import org.jboss.portal.theme.render.renderer.RegionRenderer;
import org.jboss.portal.theme.render.renderer.RegionRendererContext;
import org.jboss.portal.theme.render.renderer.WindowRendererContext;
import org.osivia.portal.api.html.AccessibilityRoles;
import org.osivia.portal.api.html.DOM4JUtils;
import org.osivia.portal.api.html.HTMLConstants;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cms.RegionInheritance;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.customizers.RegionsDefaultCustomizerPortlet;
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

    /** list of regions in head. */
    private final List<String> headerRegions;


    /**
     * Constructor.
     */
    public DivRegionRenderer() {
        super();

        IInternationalizationService internationalizationService = Locator.findMBean(IInternationalizationService.class,
                IInternationalizationService.MBEAN_NAME);
        this.bundleFactory = internationalizationService.getBundleFactory(this.getClass().getClassLoader());

        this.headerRegions = new ArrayList<String>();
        this.headerRegions.add(RegionsDefaultCustomizerPortlet.REGION_HEADER_METADATA);
    }


    /**
     * {@inheritDoc}
     */
    public void renderHeader(RendererContext rendererContext, RegionRendererContext rrc) throws RenderException {
        IRegionRendererContext irrc = (IRegionRendererContext) rrc;

        String language = rrc.getProperty("osivia.language");
        Locale locale = null;
        if (language != null) {
            locale = new Locale(language);
        } else {
            locale = Locale.US;
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
                markup.print(" class='region wizard-edging'");
            }
            markup.print(">");
        }

        // in cms mode, create a new fragment on the top of this region
        if (this.showCmsTools(rendererContext, irrc)) {
            this.printFragmentCommands(rendererContext, irrc, bundle, markup);

            if (!BooleanUtils.toBoolean(irrc.getProperty(InternalConstants.INHERITANCE_INDICATOR_PROPERTY))) {
                // Begin of DIV for Drag n drop
                // each cms region is a drag n drop zone
                markup.println("<div id=\"region_" + rrc.getId() + "\" class=\"dnd-region\">");
            }
        }

        // Add portlet link
        this.addPortletLink(rendererContext, irrc, bundle, markup);

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
        for (Iterator<?> i = rrc.getWindows().iterator(); i.hasNext();) {
            WindowRendererContext wrc = (WindowRendererContext) i.next();
            rendererContext.render(wrc);
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

        // End of DIV for Drag n drop
        if (this.showCmsTools(rendererContext, irrc) && !BooleanUtils.toBoolean(irrc.getProperty(InternalConstants.INHERITANCE_INDICATOR_PROPERTY))) {
            markup.print("</div>");
        }

        // End of Main DIV region (not shown in <head> tag)
        if (!this.headerRegions.contains(rrc.getCSSId())) {
            markup.print("</div>");
        }
    }


    /**
     * Display CMS Tools if region is marked "CMS" (dynamic region) and if the tools are enabled in the session.
     *
     * @param rendererContext page context
     * @param irrc region renderer context
     * @return true if CMS tools must be shown
     */
    private boolean showCmsTools(RendererContext rendererContext, IRegionRendererContext irrc) {
        boolean showCmsTools = BooleanUtils.toBoolean(irrc.getProperty(InternalConstants.SHOW_CMS_TOOLS_INDICATOR_PROPERTY))
                && !BooleanUtils.toBoolean(irrc.getProperty(InternalConstants.INHERITANCE_LOCKED_INDICATOR_PROPERTY));
        return irrc.isCMS() && showCmsTools;
    }


    /**
     * Utility method used to add portlet link.
     *
     * @param rendererContext renderer context
     * @param irrc region renderer context
     * @param bundle internationalization bundle
     * @param markup markup
     * @throws RenderException
     */
    private void addPortletLink(RendererContext rendererContext, IRegionRendererContext irrc, Bundle bundle, PrintWriter markup) throws RenderException {
        // Lien d'ajout de portlet
        if (InternalConstants.VALUE_WINDOWS_WIZARD_TEMPLATE_MODE.equals(rendererContext.getProperty(InternalConstants.ATTR_WINDOWS_WIZARD_MODE))) {
            // Button
            Element button = new DOMElement(QName.get(HTMLConstants.A));

            String href;
            String text;
            if (irrc.isCMS()) {
                href = HTMLConstants.A_HREF_DEFAULT;
                text = bundle.getString("REGION_CMS");
                button.addAttribute(QName.get(HTMLConstants.DISABLED), HTMLConstants.DISABLED);
            } else {
                href = rendererContext.getProperty(InternalConstants.ATTR_WINDOWS_ADD_PORTLET_URL);
                text = bundle.getString("REGION_TEMPLATE");
                button.addAttribute(QName.get(HTMLConstants.ONCLICK), "regionId = '" + irrc.getId() + "'");

                // Glyph
                Element glyph = new DOMElement(QName.get(HTMLConstants.I));
                glyph.addAttribute(QName.get(HTMLConstants.CLASS), "glyphicons halflings plus");
                glyph.setText(StringUtils.EMPTY);
                button.add(glyph);
                button.addText(" ");
            }

            button.addAttribute(QName.get(HTMLConstants.HREF), href);
            button.addAttribute(QName.get(HTMLConstants.CLASS), "btn btn-default fancybox_inline");
            button.addText(text + irrc.getId());

            // Write HTML data
            markup.write(button.asXML());
        }
    }


    /**
     * Print fragment commands.
     *
     * @param rendererContext renderer context
     * @param irrc region renderer context
     * @param bundle internationalization bundle
     * @param markup print writer markup
     */
    private void printFragmentCommands(RendererContext rendererContext, IRegionRendererContext irrc, Bundle bundle, PrintWriter markup) {
        RegionInheritance inheritance = RegionInheritance.fromValue(irrc.getProperty(InternalConstants.INHERITANCE_VALUE_REGION_PROPERTY));
        boolean inherited = BooleanUtils.toBoolean(irrc.getProperty(InternalConstants.INHERITANCE_INDICATOR_PROPERTY));
        String saveURL = irrc.getProperty("osivia.cms.saveInheritanceConfigurationURL");


        // Parent DIV
        Element parent = DOM4JUtils.generateDivElement(null);

        // Toolbar
        Element toolbar = DOM4JUtils.generateDivElement("btn-toolbar", AccessibilityRoles.TOOLBAR);
        parent.add(toolbar);


        // Button group #1
        Element group = DOM4JUtils.generateDivElement("btn-group");
        toolbar.add(group);


        // Add fragment button
        Element addFragmentButton;
        String text = bundle.getString("ADD");
        if (inherited) {
            addFragmentButton = DOM4JUtils.generateElement(HTMLConstants.P, "btn btn-default disabled", text, "halflings plus", null);
        } else {
            // Add fragment button
            String addFragmentURL = rendererContext.getProperty("osivia.cmsCreateUrl");
            StringBuilder addFragmentOnClick = new StringBuilder();
            addFragmentOnClick.append("callbackUrl='");
            addFragmentOnClick.append(rendererContext.getProperty("osivia.cmsCreateCallBackURL"));
            addFragmentOnClick.append("'; setCallbackFromEcmParams('', '");
            addFragmentOnClick.append(rendererContext.getProperty("osivia.ecmBaseUrl"));
            addFragmentOnClick.append("');");

            addFragmentButton = DOM4JUtils.generateLinkElement(addFragmentURL, null, addFragmentOnClick.toString(), "btn btn-default fancyframe_refresh", text,
                    "halflings plus");
        }
        DOM4JUtils.addTooltip(addFragmentButton, bundle.getString("CMS_ADD_FRAGMENT"));
        group.add(addFragmentButton);


        // Dropdown menu container (button group #2)
        Element dropdownContainer = DOM4JUtils.generateDivElement("btn-group");
        toolbar.add(dropdownContainer);

        // Dropdown menu button
        Element dropdownButton = DOM4JUtils.generateElement(HTMLConstants.BUTTON, "btn btn-default dropdown-toggle", HTMLConstants.TEXT_DEFAULT,
                "halflings uni-wrench", null);
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
                DOM4JUtils.addAttribute(hiddenInput, HTMLConstants.VALUE, URLDecoder.decode(StringUtils.substringAfter(parameter, "="), CharEncoding.UTF_8));
            } catch (UnsupportedEncodingException e) {
                // Do nothing
            }
            form.add(hiddenInput);
        }


        for (RegionInheritance value : RegionInheritance.values()) {
            // Radio container
            Element radioContainer = DOM4JUtils.generateDivElement("radio");
            form.add(radioContainer);

            // Radio label
            Element radioLabel = DOM4JUtils.generateElement(HTMLConstants.LABEL, null, null);
            radioContainer.add(radioLabel);

            // Radio input
            Element radioInput = DOM4JUtils.generateElement(HTMLConstants.INPUT, null, null);
            DOM4JUtils.addAttribute(radioInput, HTMLConstants.TYPE, "radio");
            DOM4JUtils.addAttribute(radioInput, HTMLConstants.NAME, "inheritance");
            DOM4JUtils.addAttribute(radioInput, HTMLConstants.VALUE, StringUtils.trimToEmpty(value.getValue()));
            if (value.equals(inheritance)) {
                DOM4JUtils.addAttribute(radioInput, HTMLConstants.CHECKED, HTMLConstants.CHECKED);
            }
            radioLabel.add(radioInput);

            radioLabel.setText(bundle.getString(value.getInternationalizationKey()));

            // Help message
            Element radioHelp = DOM4JUtils.generateElement(HTMLConstants.SPAN, "help-block", bundle.getString(value.getInternationalizationKey() + "_HELP"));
            radioLabel.add(radioHelp);
        }

        // Submit button
        Element submit = DOM4JUtils.generateElement(HTMLConstants.BUTTON, "btn btn-default btn-primary", bundle.getString("SAVE"));
        DOM4JUtils.addAttribute(submit, HTMLConstants.TYPE, "submit");
        form.add(submit);


        // Empty region indicator
        if (irrc.getWindows().size() == 1) {
            WindowRendererContext wrc = (WindowRendererContext) irrc.getWindows().iterator().next();
            if (wrc.getId().contains("_PIA_EMPTY")) {
                String emptyRegionIndicatorTitle = bundle.getString("CMS_EMPTY_REGION");
                Element emptyRegionIndicator = DOM4JUtils.generateElement(HTMLConstants.P, "btn btn-default btn-block disabled", emptyRegionIndicatorTitle,
                        "transfer", null);
                DOM4JUtils.addAttribute(emptyRegionIndicator, HTMLConstants.ID, "emptyRegion_" + irrc.getId());
                parent.add(emptyRegionIndicator);
            }
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

}
