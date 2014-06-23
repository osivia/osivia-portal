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

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.formatters.IFormatter;

/**
 * Formatter tag for JSTree HTML data generation.
 *
 * @author Cédric Krommenhoek
 * @see SimpleTagSupport
 */
public class FormatterHTMLTreeTag extends SimpleTagSupport {

    /** Model tree type. */
    public static final String MODEL = "model";
    /** Parent page tree type. */
    public static final String PARENT_PAGE = "parentPage";
    /** Parent template tree type. */
    public static final String PARENT_TEMPLATE = "parentTemplate";
    /** Move tree type. */
    public static final String MOVE = "move";
    /** Alpha ordered tree type. */
    public static final String ALPHA_ORDER = "alphaOrder";

    /** Bundle factory. */
    private static IBundleFactory bundleFactory;
    /** Formatter. */
    private static IFormatter formatter;

    /** Tree identifier. */
    private String id;
    /** Tree type. */
    private String type;


    /**
     * Constructor.
     */
    public FormatterHTMLTreeTag() {
        super();

        if (bundleFactory == null) {
            ClassLoader classLoader = this.getClass().getClassLoader();
            IInternationalizationService internationalizationService = Locator.findMBean(IInternationalizationService.class,
                    IInternationalizationService.MBEAN_NAME);
            bundleFactory = internationalizationService.getBundleFactory(classLoader);
        }

        if (formatter == null) {
            formatter = Locator.findMBean(IFormatter.class, "osivia:service=Interceptor,type=Command,name=AssistantPageCustomizer");
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void doTag() throws JspException, IOException {
        // Context
        PageContext pageContext = (PageContext) this.getJspContext();
        // Request
        ServletRequest request = pageContext.getRequest();
        // Current page
        Page currentPage = (Page) request.getAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_PAGE);
        // Controller context
        ControllerContext controllerContext = (ControllerContext) request.getAttribute(InternalConstants.ATTR_CONTROLLER_CONTEXT);

        // Generated tree content
        String content = StringUtils.EMPTY;
        if (MODEL.equals(this.type)) {
            content = formatter.formatHTMLTreeModels(currentPage, controllerContext, this.id);
        } else if (PARENT_PAGE.equals(this.type)) {
            content = formatter.formatHTMLTreePageParent(currentPage, controllerContext, this.id);
        } else if (PARENT_TEMPLATE.equals(this.type)) {
            content = formatter.formatHTMLTreeTemplateParent(currentPage, controllerContext, this.id);
        } else if (MOVE.equals(this.type)) {
            content = formatter.formatHTMLTreePortalObjectsMove(currentPage, controllerContext, this.id);
        } else if (ALPHA_ORDER.equals(this.type)) {
            content = formatter.formatHTMLTreePortalObjectsAlphaOrder(currentPage, controllerContext, this.id);
        }

        JspWriter out = pageContext.getOut();
        out.write(content);
        out.flush();
    }


    /**
     * Getter for id.
     *
     * @return the id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Setter for id.
     *
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter for type.
     *
     * @return the type
     */
    public String getType() {
        return this.type;
    }

    /**
     * Setter for type.
     *
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

}
