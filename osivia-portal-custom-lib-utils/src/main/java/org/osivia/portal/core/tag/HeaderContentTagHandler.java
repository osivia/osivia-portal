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
package org.osivia.portal.core.tag;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang.BooleanUtils;
import org.jboss.portal.theme.LayoutConstants;
import org.jboss.portal.theme.page.PageResult;
import org.jboss.portal.theme.page.WindowContext;
import org.jboss.portal.theme.page.WindowResult;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.theming.IPageHeaderResourceService;
import org.w3c.dom.Element;

/**
 * Ce tag a été modifié pour éviter les duplications de déclarations pour les différents portlets.
 */
public class HeaderContentTagHandler extends SimpleTagSupport {

    /** Page header resource service. */
    private final IPageHeaderResourceService pageHeaderResourceService;


    /**
     * Constructor.
     */
    public HeaderContentTagHandler() {
        super();

        // Page header resource service
        this.pageHeaderResourceService = Locator.findMBean(IPageHeaderResourceService.class, IPageHeaderResourceService.MBEAN_NAME);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void doTag() throws JspException, IOException {
        PageContext pageContext = (PageContext) this.getJspContext();
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

        Boolean layoutParsing = (Boolean) request.getAttribute(InternalConstants.ATTR_LAYOUT_PARSING);
        if (BooleanUtils.isNotTrue(layoutParsing)) {
            this.renderHeaderContent(request);
        }
    }


    /**
     * Utility method used to render header content.
     *
     * @param request current HTTP servlet request
     * @throws JspException
     * @throws IOException
     */
    private void renderHeaderContent(HttpServletRequest request) throws JspException, IOException {
        //
        PageResult page = (PageResult) request.getAttribute(LayoutConstants.ATTR_PAGE);
        JspWriter out = this.getJspContext().getOut();
        if (page == null) {
            out.write("<p bgcolor='red'>No page to render!</p>");
            out.write("<p bgcolor='red'>The page to render (PageResult) must be set in the request attribute '" + LayoutConstants.ATTR_PAGE + "'</p>");
            out.flush();
            return;
        }

        // jQuery 1.8.3 for fancybox 2.1.3 compatibility
        this.writeResource(out, "<script type='text/javascript' src='/osivia-portal-custom-web-assets/components/jquery/jquery-1.8.3.min.js'></script>");
        this.writeResource(out, "<script type='text/javascript' src='/osivia-portal-custom-web-assets/js/jquery-integration.min.js'></script>");

        // jQuery UI
        this.writeResource(out, "<script type='text/javascript' src='/osivia-portal-custom-web-assets/components/jquery-ui/jquery-ui-1.11.3.min.js'></script>");
        this.writeResource(out, "<link rel='stylesheet' href='/osivia-portal-custom-web-assets/components/jquery-ui/jquery-ui-1.11.3.min.css'>");

        // jQuery Mobile
        this.writeResource(out,
                "<script type='text/javascript' src='/osivia-portal-custom-web-assets/components/jquery-mobile/jquery.mobile.custom.min.js'></script>");


        // Portlets resources
        Map<?, ?> results = page.getWindowContextMap();
        Set<String> resources = new LinkedHashSet<String>();
        for (Object name : results.values()) {
            WindowContext wc = (WindowContext) name;
            WindowResult result = wc.getResult();

            List<Element> headElements = result.getHeaderContent();
            if (headElements != null) {
                for (Element element : headElements) {
                    if (!"title".equals(element.getNodeName().toLowerCase())) {
                        String resource = this.pageHeaderResourceService.adaptResourceElement(element.toString());
                        if (resource != null) {
                            resources.add(resource);
                        }
                    }
                }
            }
        }
        for (String resource : resources) {
            out.write(resource);
        }


        // Post messages API (must be loaded before fancy integration)
        this.writeResource(out, "<script src='/osivia-portal-custom-web-assets/js/postmessage.min.js'></script>");

        // Bootstrap
        this.writeResource(out, "<link rel='stylesheet' href='/osivia-portal-custom-web-assets/css/bootstrap/bootstrap.min.css' title='Bootstrap'>");
        this.writeResource(out, "<link rel='stylesheet' href='/osivia-portal-custom-web-assets/css/osivia.min.css'>");
        this.writeResource(out, "<script src='/osivia-portal-custom-web-assets/components/bootstrap/js/bootstrap.min.js'></script>");
        this.writeResource(out, "<script src='/osivia-portal-custom-web-assets/js/bootstrap-integration.min.js'></script>");

        // Fancybox
        this.writeResource(out, "<link rel='stylesheet' href='/osivia-portal-custom-web-assets/components/fancybox/jquery.fancybox.min.css'>");
        this.writeResource(out, "<script src='/osivia-portal-custom-web-assets/components/fancybox/jquery.fancybox.min.js'></script>");
        this.writeResource(out, "<script src='/osivia-portal-custom-web-assets/components/fancybox/jquery.fancybox.pack.min.js'></script>");
        this.writeResource(out, "<script src='/osivia-portal-custom-web-assets/js/fancybox-integration.min.js'></script>");

        // Fancytree
        this.writeResource(out, "<script src='/osivia-portal-custom-web-assets/components/fancytree/jquery.fancytree-all-2.8.0.min.js'></script>");
        this.writeResource(out, "<script src='/osivia-portal-custom-web-assets/js/fancytree-integration.min.js'></script>");

        // bxSlider
        this.writeResource(out, "<link rel='stylesheet' href='/osivia-portal-custom-web-assets/components/bxslider/jquery.bxslider.css'>");
        this.writeResource(out, "<script src='/osivia-portal-custom-web-assets/components/bxslider/jquery.bxslider.min.js'></script>");
        this.writeResource(out, "<script src='/osivia-portal-custom-web-assets/js/bxslider-integration.min.js'></script>");

        // Select2
        this.writeResource(out, "<link rel='stylesheet' href='/osivia-portal-custom-web-assets/components/select2/css/select2.min.css'>");
        this.writeResource(out, "<script src='/osivia-portal-custom-web-assets/components/select2/js/select2.min.js'></script>");
        this.writeResource(out, "<script src='/osivia-portal-custom-web-assets/js/select2-integration.min.js'></script>");

        // Search
        this.writeResource(out, "<script src='/osivia-portal-custom-web-assets/js/search.min.js'></script>");

        // Logout
        this.writeResource(out, "<script src='/osivia-portal-custom-web-assets/js/logout.min.js'></script>");

        // Glyphicons
        this.writeResource(out, "<link rel='stylesheet' href='/osivia-portal-custom-web-assets/css/glyphicons.min.css'>");

        // No JavaScript CSS styles
        out.write("<noscript>");
        this.writeResource(out, "<link rel='stylesheet' href='/osivia-portal-custom-web-assets/css/noscript.min.css'>");
        out.write("</noscript>");

        // AJAX header
        this.writeResource(out, "<script type='text/javascript' src='/portal-ajax/dyna/prototype.js'></script>");
        this.writeResource(out, "<script type='text/javascript' src='/portal-ajax/dyna/prototype-bootstrap-workaround.js'></script>");
        this.writeResource(out, "<script type='text/javascript' src='/portal-ajax/dyna/effects.js'></script>");
        this.writeResource(out, "<script type='text/javascript' src='/portal-ajax/dyna/dragdrop.js'></script>");
        this.writeResource(out, "<script type='text/javascript' src='/portal-ajax/dyna/dyna.js'></script>");
    }


    /**
     * Write resource element.
     *
     * @param out JSP writer
     * @param resource resource element
     * @throws IOException
     */
    private void writeResource(JspWriter out, String resource) throws IOException {
        out.write(this.pageHeaderResourceService.adaptResourceElement(resource));
    }

}
