/*
 * (C) Copyright 2014 OSIVIA (http://www.osivia.com) 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package org.osivia.portal.core.portlets.sitemap;

import static org.osivia.portal.api.html.HTMLConstants.A;
import static org.osivia.portal.api.html.HTMLConstants.CLASS;
import static org.osivia.portal.api.html.HTMLConstants.CLASS_NAVIGATION_ITEM;
import static org.osivia.portal.api.html.HTMLConstants.HREF;
import static org.osivia.portal.api.html.HTMLConstants.LI;
import static org.osivia.portal.api.html.HTMLConstants.UL;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.CharEncoding;
import org.dom4j.Element;
import org.dom4j.dom.DOMElement;
import org.dom4j.io.HTMLWriter;
import org.osivia.portal.core.sitemap.Sitemap;


public class SitemapFormatter {

    public String formatHtmlTreeSitemap(Sitemap rootSitemap) throws IOException {


        if ((rootSitemap == null)) {
            return null;
        }

        // Locale locale = context.getServerInvocation().getRequest().getLocale();
        // ResourceBundle resourceBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, locale);

        // Generate HTML node for each page
        Element root = new DOMElement(UL);
        root.addAttribute(CLASS, CLASS_NAVIGATION_ITEM);

        String pageName = rootSitemap.getTitle();
        String url = rootSitemap.getUrl();

        Element li = new DOMElement(LI);
        li.addAttribute(CLASS, CLASS_NAVIGATION_ITEM);
        li.addAttribute("rel", "pageOnline");
        li.addAttribute("id", "rootSitemap");

        root.add(li);

        Element a = new DOMElement(A);
        a.addAttribute(HREF, url);
        a.setText(pageName);
        li.add(a);


        // Recursive tree generation
        Element ulChildren = this.generateRecursiveHtmlTreeSitemap(rootSitemap);
        if (ulChildren != null) {
            li.add(ulChildren);
        }

        // Get HTML data
        String resultat = this.writeHtmlData(root);
        return resultat;
    }


    /**
     * Utility method used to generate recursive HTML tree of sitmap
     * 
     * @param rootSitemap root of the sitemap
     * @return HTML "ul" node
     * @throws IOException
     */
    private Element generateRecursiveHtmlTreeSitemap(Sitemap rootSitemap) throws IOException {
        // Locale[] locales = context.getServerInvocation().getRequest().getLocales();

        Collection<Sitemap> children = rootSitemap.getChildren();
        if (CollectionUtils.isEmpty(children)) {
            return null;
        }

        // Contrôle des droits et tri des pages
        // TODO gestion des droits
        // PortalAuthorizationManager authManager = this.portalAuthorizationManagerFactory.getManager();
        //
        List<Sitemap> sortedPages = new ArrayList<Sitemap>();

        for (Sitemap child : children) {
            Sitemap page = (Sitemap) child;
            sortedPages.add(page);
        }

        if (CollectionUtils.isEmpty(sortedPages)) {
            return null;
        }

        // Generate HTML node for each page
        Element ul = new DOMElement(UL);
        ul.addAttribute(CLASS, CLASS_NAVIGATION_ITEM);

        for (Sitemap page : sortedPages) {
            String pageName = page.getTitle();

            String url = page.getUrl();

            Element li = new DOMElement(LI);
            li.addAttribute(CLASS, CLASS_NAVIGATION_ITEM);

            li.addAttribute("rel", "pageOnline");

            ul.add(li);

            Element a = new DOMElement(A);
            a.addAttribute(HREF, url);
            a.setText(pageName);
            li.add(a);

            // Recursive generation
            Element ulChildren = this.generateRecursiveHtmlTreeSitemap(page);
            if (ulChildren != null) {
                li.add(ulChildren);
            }
        }


        return ul;
    }

    /**
     * Utility method, used to write HTML data.
     * 
     * @param htmlElement HTML element to write
     * @return HTML data
     * @throws IOException
     */
    private String writeHtmlData(Element htmlElement) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        OutputStream bufferedOutput = new BufferedOutputStream(output);
        HTMLWriter htmlWriter = null;
        String resultat = null;
        try {
            htmlWriter = new HTMLWriter(bufferedOutput);
            htmlWriter.setEscapeText(false);
            htmlWriter.write(htmlElement);

            resultat = output.toString(CharEncoding.UTF_8);
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                bufferedOutput.close();
                output.close();
                htmlWriter.close();
            } catch (IOException e) {
                throw e;
            }
        }
        return resultat;
    }

}
