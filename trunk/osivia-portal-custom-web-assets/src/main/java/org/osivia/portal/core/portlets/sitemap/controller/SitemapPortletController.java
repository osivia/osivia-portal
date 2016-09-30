package org.osivia.portal.core.portlets.sitemap.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.jboss.portal.core.controller.ControllerContext;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.path.IBrowserService;
import org.osivia.portal.api.windows.PortalWindow;
import org.osivia.portal.api.windows.WindowFactory;
import org.osivia.portal.core.security.CmsPermissionHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import org.springframework.web.portlet.context.PortletContextAware;

/**
 * Site map portlet controller.
 *
 * @author Cédric Krommenhoek
 */
@Controller
@RequestMapping(value = "VIEW")
public class SitemapPortletController implements PortletContextAware {

    /** Portlet context. */
    private PortletContext portletContext;


    /** Browser service. */
    private final IBrowserService browserService;


    /**
     * Constructor.
     */
    public SitemapPortletController() {
        super();

        // Browser service
        this.browserService = Locator.findMBean(IBrowserService.class, IBrowserService.MBEAN_NAME);
    }


    /**
     * View render mapping.
     *
     * @param request render request
     * @param response render response
     * @return path
     */
    @RenderMapping
    public String view(RenderRequest request, RenderResponse response) {
        // Window
        PortalWindow window = WindowFactory.getWindow(request);
        // Controller context
        ControllerContext controllerContext = (ControllerContext) request.getAttribute("osivia.controller");


        // CMS base path
        String cmsBasePath = window.getProperty("osivia.cms.basePath");
        request.setAttribute("cmsBasePath", cmsBasePath);

        // CMS navigation path
        String cmsNavigationPath = window.getProperty("osivia.cms.path");
        request.setAttribute("cmsNavigationPath", cmsNavigationPath);

        // Live indicator
        String version = CmsPermissionHelper.getCurrentCmsVersion(controllerContext);
        boolean live = !CmsPermissionHelper.CMS_VERSION_ONLINE.equals(version);
        request.setAttribute("live", live);

        return "sitemap/view";
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
        return "error";
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

        try {
            String data = this.browserService.browse(portalControllerContext);

            // Content type
            response.setContentType("application/json");

            // Content
            PrintWriter printWriter = new PrintWriter(response.getPortletOutputStream());
            printWriter.write(data);
            printWriter.close();
        } catch (PortalException e) {
            throw new PortletException(e);
        }
    }


    /**
     * {@inheritDoc}
     */
    public void setPortletContext(PortletContext portletContext) {
        this.portletContext = portletContext;
    }

}
