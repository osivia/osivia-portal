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
import java.io.OutputStream;

import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.lang.StringUtils;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.core.portlets.browser.service.IBrowserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import org.springframework.web.portlet.context.PortletContextAware;

/**
 * Live content browser portlet Spring controller.
 * 
 * @author Cédric Krommenhoek
 */
@Controller(value = "browserPortletController")
@RequestMapping(value = "VIEW")
public class BrowserPortletController implements PortletContextAware {

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
     * Default constructor.
     */
    public BrowserPortletController() {
        super();
    }


    /**
     * View page render action.
     * 
     * @param request render request
     * @param response render response
     * @return view page path
     */
    @RenderMapping
    public String view(RenderRequest request, RenderResponse response) {
        return VIEW_PATH;
    }


    /**
     * Serve lazy content.
     * 
     * @param request resource request
     * @param response resource response
     * @param nodeId current node identifier
     * @throws PortletException
     * @throws IOException
     */
    @ResourceMapping(value = "lazyContent")
    public void serveLazyContent(ResourceRequest request, ResourceResponse response, @RequestParam(value = "nodeId", required = false) String nodeId)
            throws PortletException, IOException {
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
        String parentId = StringUtils.removeStart(nodeId, response.getNamespace());
        String lazyContent = this.browserService.browse(portalControllerContext, parentId);

        OutputStream output = response.getPortletOutputStream();
        output.write(lazyContent.getBytes());
    }


    /**
     * Handle exception.
     * 
     * @param exception handled exception
     * @return error page path
     */
    @ExceptionHandler(value = PortletException.class)
    public ModelAndView handleException(PortletException exception) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", exception);
        mav.setViewName(ERROR_PATH);
        return mav;
    }


    /**
     * {@inheritDoc}
     */
    public void setPortletContext(PortletContext portletContext) {
        this.portletContext = portletContext;
    }


    /**
     * Getter for browserService.
     * 
     * @return the browserService
     */
    public IBrowserService getBrowserService() {
        return this.browserService;
    }

    /**
     * Setter for browserService.
     * 
     * @param browserService the browserService to set
     */
    public void setBrowserService(IBrowserService browserService) {
        this.browserService = browserService;
    }

}
