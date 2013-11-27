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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.dom4j.QName;
import org.dom4j.dom.DOMElement;
import org.dom4j.io.HTMLWriter;
import org.jboss.portal.theme.render.AbstractObjectRenderer;
import org.jboss.portal.theme.render.RenderException;
import org.jboss.portal.theme.render.RendererContext;
import org.jboss.portal.theme.render.renderer.RegionRenderer;
import org.jboss.portal.theme.render.renderer.RegionRendererContext;
import org.jboss.portal.theme.render.renderer.WindowRendererContext;
import org.osivia.portal.api.HTMLConstants;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
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

    /** Internationalization service. */
    private static final IInternationalizationService INTERNATIONALIZATION_SERVICE = Locator.findMBean(IInternationalizationService.class,
            IInternationalizationService.MBEAN_NAME);

    /** Fancybox class, required for link. */
    private static final String CLASS_FANCYBOX = "fancybox_inline";
    /** Regions commands class. */
    private static final String CLASS_REGIONS_COMMANDS = "osivia-portal-regions-commands";
    /** Regions template name class. */
    private static final String CLASS_REGIONS_NAME_TPL_SPAN = "osivia-portal-regions-template-name-span";
    /** Regions CMS name class. */
    private static final String CLASS_REGIONS_NAME_CMS_SPAN = "osivia-portal-regions-cms-name-span";
    /** "Add" image source. */
    private static final String SRC_IMG_ADD = "/osivia-portal-custom-web-assets/images/icons/icon_add_window.png";

    /** list of regions in head. */
    private final List<String> headerRegions;


    /**
     * Default constructor.
     */
    public DivRegionRenderer() {
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

        PrintWriter markup = rendererContext.getWriter();

        // Main DIV region (not shown in <head> tag)
        if (!this.headerRegions.contains(rrc.getCSSId())) {
            markup.print("<div");

            if (rrc.getCSSId() != null) {
                markup.print(" id='");
                markup.print(rrc.getCSSId());
                markup.print("'");
            }
            markup.print(">");
        }

        // in cms mode, create a new fragment on the top of this region
        if (this.showCmsTools(rendererContext, irrc)) {
            markup.print("<div class=\"cms-commands\">");

            markup.print("<a class=\"fancyframe_refresh cmd add\" onClick=\"callbackUrl='" + rendererContext.getProperty("osivia.cmsCreateCallBackURL")
                    + "';setCallbackFromEcmParams('','" + rendererContext.getProperty("osivia.ecmBaseUrl") + "')\" href=\""
                    + rendererContext.getProperty("osivia.cmsCreateUrl") + "\">" + INTERNATIONALIZATION_SERVICE.getString("CMS_ADD_FRAGMENT", locale) + "</a>");


            markup.println("</div>");

            if (rrc.getWindows().size() == 1) {
                WindowRendererContext wrc = (WindowRendererContext) rrc.getWindows().iterator().next();
                if (wrc.getId().contains("_PIA_EMPTY")) {

                    markup.println("<div id=\"emptyRegion_" + rrc.getId() + "\" class=\"fragmentPreview\">"
                            + INTERNATIONALIZATION_SERVICE.getString("CMS_EMPTY_REGION", locale) + "</div>");

                }
            }

            // Begin of DIV for Drag n drop
            // each cms region is a drag n drop zone
            markup.println("<div id=\"region_" + rrc.getId() + "\" class=\"dnd-region\">");
        }

        // Add portlet link
        this.addPortletLink(rendererContext, irrc, locale, markup);

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
        if (this.showCmsTools(rendererContext, irrc)) {
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
    private Boolean showCmsTools(RendererContext rendererContext, IRegionRendererContext irrc) {
        Boolean showCmsTools = false;

        String property = irrc.getProperty("osivia.cmsShowTools");
        if (property != null) {
            showCmsTools = Boolean.valueOf(property);
        }

        return irrc.isCMS() && showCmsTools;
    }


    /**
     * Utility method used to add portlet link.
     *
     * @param rendererContext renderer context
     * @param irrc region renderer context
     * @param locale current locale
     * @param markup markup
     * @throws RenderException
     */
    private void addPortletLink(RendererContext rendererContext, IRegionRendererContext irrc, Locale locale, PrintWriter markup) throws RenderException {
        // Lien d'ajout de portlet
        if (InternalConstants.VALUE_WINDOWS_WIZARD_TEMPLATE_MODE.equals(rendererContext.getProperty(InternalConstants.ATTR_WINDOWS_WIZARD_MODE))) {
            DOMElement div = new DOMElement(QName.get(HTMLConstants.DIV));
            if (irrc.isCMS()) {
                div.addAttribute(QName.get(HTMLConstants.CLASS), CLASS_REGIONS_COMMANDS);

                // region id
                DOMElement span = new DOMElement(QName.get(HTMLConstants.SPAN));
                span.addAttribute(QName.get(HTMLConstants.CLASS), CLASS_REGIONS_NAME_CMS_SPAN);
                span.addAttribute(QName.get(HTMLConstants.TITLE), INTERNATIONALIZATION_SERVICE.getString("REGION_CMS_TITLE", locale));
                span.setText(INTERNATIONALIZATION_SERVICE.getString("REGION_CMS", locale).concat(irrc.getId()));
                div.add(span);
            } else {

                String url = rendererContext.getProperty(InternalConstants.ATTR_WINDOWS_ADD_PORTLET_URL);
                div.addAttribute(QName.get(HTMLConstants.CLASS), CLASS_REGIONS_COMMANDS);

                DOMElement a = new DOMElement(QName.get(HTMLConstants.A));
                a.addAttribute(QName.get(HTMLConstants.HREF), url);
                a.addAttribute(QName.get(HTMLConstants.CLASS), CLASS_FANCYBOX);
                a.addAttribute(QName.get(HTMLConstants.ONCLICK), "regionId = '" + irrc.getId() + "'");
                div.add(a);

                DOMElement img = new DOMElement(QName.get(HTMLConstants.IMG));
                img.addAttribute(QName.get(HTMLConstants.SRC), SRC_IMG_ADD);
                a.add(img);

                // region id
                DOMElement span = new DOMElement(QName.get(HTMLConstants.SPAN));
                span.addAttribute(QName.get(HTMLConstants.CLASS), CLASS_REGIONS_NAME_TPL_SPAN);
                span.setText(INTERNATIONALIZATION_SERVICE.getString("REGION_TEMPLATE", locale).concat(irrc.getId()));
                div.add(span);
            }

            HTMLWriter htmlWriter = new HTMLWriter(markup);
            try {
                htmlWriter.write(div);
            } catch (IOException e) {
                throw new RenderException(e);
            }
        }
    }

}
