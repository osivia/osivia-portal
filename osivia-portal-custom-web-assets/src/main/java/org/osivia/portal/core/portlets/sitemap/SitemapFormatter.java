package org.osivia.portal.core.portlets.sitemap;

import static org.osivia.portal.api.HtmlConstants.A;
import static org.osivia.portal.api.HtmlConstants.CLASS;
import static org.osivia.portal.api.HtmlConstants.CLASS_NAVIGATION_ITEM;
import static org.osivia.portal.api.HtmlConstants.HREF;
import static org.osivia.portal.api.HtmlConstants.LI;
import static org.osivia.portal.api.HtmlConstants.UL;

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


        // Recursive tree generation
        Element ulChildren = this.generateRecursiveHtmlTreeSitemap(rootSitemap);

        // Get HTML data
        String resultat = this.writeHtmlData(ulChildren);
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
        // if (sortAlphabetically) {
        // sortedPages = new TreeSet<Page>(PageUtils.nameComparator);
        // } else {
        // sortedPages = new TreeSet<Page>(PageUtils.orderComparator);
        // }

        for (Sitemap child : children) {
            // PortalObjectPermission permission = new PortalObjectPermission(child.getId(), PortalObjectPermission.VIEW_MASK);
            //
            // if (authManager.checkPermission(permission)) {
            Sitemap page = (Sitemap) child;
            sortedPages.add(page);
            // }
        }

        if (CollectionUtils.isEmpty(sortedPages)) {
            return null;
        }

        // Generate HTML node for each page
        Element ul = new DOMElement(UL);
        ul.addAttribute(CLASS, CLASS_NAVIGATION_ITEM);

        for (Sitemap page : sortedPages) {
            // String pageId = this.formatHtmlSafeEncodingId(page.getId());
            String pageName = page.getTitle();

            String url = page.getUrl();

            Element li = new DOMElement(LI);
            // li.addAttribute(QNAME_ATTRIBUTE_ID, idPrefix + pageId);
            li.addAttribute(CLASS, CLASS_NAVIGATION_ITEM);
            li.addAttribute("rel", "page");
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
