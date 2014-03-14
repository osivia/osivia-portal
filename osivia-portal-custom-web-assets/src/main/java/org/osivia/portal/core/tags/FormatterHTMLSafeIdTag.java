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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.jboss.portal.core.model.portal.PortalObjectId;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;

/**
 * Formatter tag for HTML safe identifier access.
 *
 * @author Cédric Krommenhoek
 * @see SimpleTagSupport
 */
public class FormatterHTMLSafeIdTag extends SimpleTagSupport {

    /** Exported variable name. */
    private String var;
    /** Portal object identifier. */
    private PortalObjectId portalObjectId;


    /**
     * Default constructor.
     */
    public FormatterHTMLSafeIdTag() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void doTag() throws JspException, IOException {
        String htmlSafeId = PortalObjectUtils.getHTMLSafeId(this.portalObjectId);

        this.getJspContext().setAttribute(this.var, htmlSafeId);
    }


    /**
     * Getter for var.
     * 
     * @return the var
     */
    public String getVar() {
        return this.var;
    }

    /**
     * Setter for var.
     * 
     * @param var the var to set
     */
    public void setVar(String var) {
        this.var = var;
    }

    /**
     * Getter for portalObjectId.
     * 
     * @return the portalObjectId
     */
    public PortalObjectId getPortalObjectId() {
        return this.portalObjectId;
    }

    /**
     * Setter for portalObjectId.
     * 
     * @param portalObjectId the portalObjectId to set
     */
    public void setPortalObjectId(PortalObjectId portalObjectId) {
        this.portalObjectId = portalObjectId;
    }

}
