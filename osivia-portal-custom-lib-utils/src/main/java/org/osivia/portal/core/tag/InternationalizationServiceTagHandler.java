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
package org.osivia.portal.core.tag;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;

/**
 * Internationalization service tag handler.
 *
 * @author Cédric Krommenhoek
 * @see SimpleTagSupport
 */
public class InternationalizationServiceTagHandler extends SimpleTagSupport {

    /** Bundle factory attribute name. */
    private static final String BUNDLE_FACTORY_ATTRIBUTE_NAME = "osivia.internationalization.bundleFactory";

    /** Resource property key. */
    private String key;


    /**
     * Default constructor.
     */
    public InternationalizationServiceTagHandler() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void doTag() throws JspException, IOException {
        PageContext pageContext = (PageContext) this.getJspContext();
        Locale locale = pageContext.getRequest().getLocale();

        IBundleFactory bundleFactory = (IBundleFactory) pageContext.getAttribute(BUNDLE_FACTORY_ATTRIBUTE_NAME, PageContext.APPLICATION_SCOPE);
        if (bundleFactory == null) {
            // Internationalization service
            IInternationalizationService internationalizationService = Locator.findMBean(IInternationalizationService.class,
                    IInternationalizationService.MBEAN_NAME);
            // Current class loader
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            // Set bundle factory
            bundleFactory = internationalizationService.getBundleFactory(classLoader);
            pageContext.setAttribute(BUNDLE_FACTORY_ATTRIBUTE_NAME, PageContext.APPLICATION_SCOPE);
        }

        // Internationalization service invocation
        Bundle bundle = bundleFactory.getBundle(locale);
        String property = bundle.getString(this.key);

        // Write property into JSP
        JspWriter out = pageContext.getOut();
        out.write(property);
        out.flush();
    }


    /**
     * Getter for key.
     *
     * @return the key
     */
    public String getKey() {
        return this.key;
    }

    /**
     * Setter for key.
     *
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

}
