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
package org.osivia.portal.core.portlets.browser.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.jboss.portal.theme.impl.render.dynamic.json.JSONArray;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.portlet.PortalGenericPortlet;
import org.osivia.portal.core.portlets.browser.service.IBrowserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import org.springframework.web.portlet.context.PortletContextAware;

/**
 * Live content browser portlet controller.
 *
 * @author Cédric Krommenhoek
 * @see PortalGenericPortlet
 */
@Controller
@RequestMapping(value = "VIEW")
public class BrowserPortletController extends PortalGenericPortlet implements PortletContextAware {

    /** View path. */
    private static final String VIEW_PATH = "browser/view";
    /** Error path. */
    private static final String ERROR_PATH = "error";

    /** Browser service. */
    @Autowired
    private IBrowserService browserService;

    /** Portlet context. */
    private PortletContext portletContext;


    /**
     * Constructor.
     */
    public BrowserPortletController() {
        super();
    }


    /**
     * Render mapping.
     *
     * @param request render request
     * @param response render response
     * @return view path
     * @throws PortletException
     */
    @RenderMapping
    public String view(RenderRequest request, RenderResponse response) throws PortletException {
        return VIEW_PATH;
    }


    /**
     * Exception handler.
     *
     * @param exception handled exception
     * @return error path
     */
    @ExceptionHandler(value = PortletException.class)
    public String handleException(PortletException exception, PortletRequest request) {
        request.setAttribute("exception", exception);
        return ERROR_PATH;
    }


    /**
     * Lazy loading resource mapping.
     *
     * @param request resource request
     * @param response resource response
     * @param path parent path, may be null for root node
     * @throws PortletException
     * @throws IOException
     */
    @ResourceMapping(value = "lazyLoading")
    public void lazyLoading(ResourceRequest request, ResourceResponse response, @RequestParam(value = "path", required = false) String path)
            throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        // JSON data
        JSONArray data = this.browserService.browse(portalControllerContext, path);

        // Content type
        response.setContentType("application/json");

        // Content
        PrintWriter printWriter = new PrintWriter(response.getPortletOutputStream());
        printWriter.write(data.toString());
        printWriter.close();
    }


    /**
     * {@inheritDoc}
     */
    public void setPortletContext(PortletContext portletContext) {
        this.portletContext = portletContext;
    }

}
