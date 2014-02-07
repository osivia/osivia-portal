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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang.BooleanUtils;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.jboss.portal.theme.LayoutConstants;
import org.jboss.portal.theme.page.PageResult;
import org.jboss.portal.theme.page.WindowContext;
import org.jboss.portal.theme.page.WindowResult;
import org.osivia.portal.core.constants.InternalConstants;
import org.w3c.dom.Element;

/**
 * Ce tag a été modifié pour éviter les duplications de déclarations pour les différents portlets.
 */
public class HeaderContentTagHandler extends SimpleTagSupport {

    protected static final OutputFormat serializerOutputFormat = new OutputFormat() {
        {
            this.setOmitXMLDeclaration(true);
        }
    };


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

        // JQuery 1.8.3 for fancybox 2.1.3 compatibility
        out.write("<script type=\"text/javascript\" src=\"/osivia-portal-custom-web-assets/jquery/jquery-1.8.3.min.js\"></script>");

        //
        Map<?, ?> results = page.getWindowContextMap();
        Set<String> insertedRefs = new HashSet<String>();
        for (Object name : results.values()) {
            WindowContext wc = (WindowContext) name;
            WindowResult result = wc.getResult();
            List<Element> headElements = result.getHeaderContent();
            if (headElements != null) {
                XMLSerializer elementSerializer = new XMLSerializer(out, serializerOutputFormat);

                for (Element element : headElements) {
                    if (!"title".equals(element.getNodeName().toLowerCase())) {
                        String ref = element.toString();

                        // PIA : Test d'insertion
                        if (!insertedRefs.contains(ref)) {
                            try {

                                elementSerializer.serialize(element);
                            } catch (UnsupportedOperationException uoe) {
                                // handle the pseudo-Elements org.jboss.portal.core.metadata.portlet classes
                                out.println(element);
                            }
                        }
                        if (ref != null) {
                            insertedRefs.add(ref);
                        }
                    }
                }
            }
        }

        // CSS
        out.write("<link rel=\"stylesheet\" id=\"settings_css\" href=\"/osivia-portal-custom-web-assets/common-css/common.css\" type=\"text/css\"/>");
        out.write("<link rel=\"stylesheet\" id=\"toolbar_css\" href=\"/osivia-portal-custom-web-assets/common-css/toolbar.css\" type=\"text/css\"/>");
        out.write("<link rel=\"stylesheet\" id=\"tabs_css\" href=\"/osivia-portal-custom-web-assets/common-css/tabs.css\" type=\"text/css\"/>");
        out.write("<link rel=\"stylesheet\" id=\"breadcrumb_css\" href=\"/osivia-portal-custom-web-assets/common-css/breadcrumb.css\" type=\"text/css\"/>");
        out.write("<link rel=\"stylesheet\" id=\"modecms_css\" href=\"/osivia-portal-custom-web-assets/common-css/modecms.css\" type=\"text/css\"/>");

        // post messages API (must be loaded before fancy integration)
        out.write("<script type=\"text/javascript\" src=\"/osivia-portal-custom-web-assets/js/postmessage.js\"></script>");

        // Fancybox
        out.write("<link rel=\"stylesheet\" id=\"main_css\" href=\"/osivia-portal-custom-web-assets/fancybox/jquery.fancybox.css\" type=\"text/css\"/>");
        out.write("<script type=\"text/javascript\" src=\"/osivia-portal-custom-web-assets/fancybox/jquery.fancybox.js\"></script>");
        out.write("<script type=\"text/javascript\" src=\"/osivia-portal-custom-web-assets/fancybox/jquery.fancybox.pack.js\"></script>");
        out.write("<script type=\"text/javascript\" src=\"/osivia-portal-custom-web-assets/js/fancy-integration.js\"></script>");

        // JSTree
        out.write("<script type=\"text/javascript\" src=\"/osivia-portal-custom-web-assets/jstree/jquery.jstree.js\"></script>");
        out.write("<script type=\"text/javascript\" src=\"/osivia-portal-custom-web-assets/js/jstree-integration.js\"></script>");

        // Modernizr
        out.write("<script type=\"text/javascript\" src=\"/osivia-portal-custom-web-assets/js/modernizr.js\"></script>");
        out.write("<script type=\"text/javascript\" src=\"/osivia-portal-custom-web-assets/js/modernizr-integration.js\"></script>");
    }

}
