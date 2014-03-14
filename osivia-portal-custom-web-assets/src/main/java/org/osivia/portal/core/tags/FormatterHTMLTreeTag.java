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
package org.osivia.portal.core.tags;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.dom.DOMElement;
import org.osivia.portal.api.HTMLConstants;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;

/**
 * Formatter tag for JSTree HTML data generation.
 *
 * @author Cédric Krommenhoek
 * @see SimpleTagSupport
 */
public class FormatterHTMLTreeTag extends SimpleTagSupport {

    /** Filter HTML class. */
    private static final String HTML_CLASS_FILTER = "filter";

    /** Bundle factory. */
    private static IBundleFactory bundleFactory;

    /** Tree identifier. */
    private String treeId;
    /** HTML class. */
    private String htmlClass;
    /** Filter indicator. */
    private Boolean filter;


    /**
     * Default constructor.
     */
    public FormatterHTMLTreeTag() {
        super();

        if (bundleFactory == null) {
            ClassLoader classLoader = this.getClass().getClassLoader();
            IInternationalizationService internationalizationService = Locator.findMBean(IInternationalizationService.class,
                    IInternationalizationService.MBEAN_NAME);
            bundleFactory = internationalizationService.getBundleFactory(classLoader);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void doTag() throws JspException, IOException {
        JspWriter out = this.getJspContext().getOut();

        Element tree = this.generateTree();
        tree.write(out);
        out.flush();
    }


    private Element generateTree() {
        PageContext pageContext = (PageContext) this.getJspContext();
        Locale locale = pageContext.getRequest().getLocale();
        Bundle bundle = bundleFactory.getBundle(locale);

        Element parent = new DOMElement(QName.get(HTMLConstants.DIV));
        if (StringUtils.isNotBlank(this.htmlClass)) {
            parent.addAttribute(QName.get(HTMLConstants.CLASS), this.htmlClass);
        }

        Element tree;
        if (BooleanUtils.isTrue(this.filter)) {
            Element filter = new DOMElement(QName.get(HTMLConstants.DIV));
            parent.add(filter);

            Element input = new DOMElement(QName.get(HTMLConstants.INPUT));
            input.addAttribute(QName.get(HTMLConstants.TYPE), HTMLConstants.INPUT_TYPE_TEXT);
            input.addAttribute(QName.get(HTMLConstants.CLASS), HTML_CLASS_FILTER);
            input.addAttribute(QName.get(HTMLConstants.ONKEYUP), "jstreeSearch('" + this.treeId + "', this.value)");
            input.addAttribute(QName.get(HTMLConstants.PLACEHOLDER), bundle.getString("JSTREE_FILTER"));
            filter.add(input);

            tree = new DOMElement(QName.get(HTMLConstants.DIV));
            parent.add(tree);
        } else {
            tree = parent;
        }
        tree.addAttribute(QName.get(HTMLConstants.ID), this.treeId);


        return parent;
    }


    /**
     * Getter for treeId.
     *
     * @return the treeId
     */
    public String getTreeId() {
        return this.treeId;
    }

    /**
     * Setter for treeId.
     *
     * @param treeId the treeId to set
     */
    public void setTreeId(String treeId) {
        this.treeId = treeId;
    }

    /**
     * Getter for htmlClass.
     *
     * @return the htmlClass
     */
    public String getHtmlClass() {
        return this.htmlClass;
    }

    /**
     * Setter for htmlClass.
     *
     * @param htmlClass the htmlClass to set
     */
    public void setHtmlClass(String htmlClass) {
        this.htmlClass = htmlClass;
    }

    /**
     * Getter for filter.
     *
     * @return the filter
     */
    public Boolean getFilter() {
        return this.filter;
    }

    /**
     * Setter for filter.
     *
     * @param filter the filter to set
     */
    public void setFilter(Boolean filter) {
        this.filter = filter;
    }

}
