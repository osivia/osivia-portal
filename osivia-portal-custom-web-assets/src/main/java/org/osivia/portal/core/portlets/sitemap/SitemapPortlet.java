package org.osivia.portal.core.portlets.sitemap;

import java.io.IOException;
import java.util.List;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerContext;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.windows.PortalWindow;
import org.osivia.portal.api.windows.WindowFactory;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.sitemap.Sitemap;


public class SitemapPortlet extends GenericPortlet {

    public final static String ATTR_SITEMAP_CONTEXT = "osivia.sitemap.context";

    public final static String ATTR_SITEMAP_FORMATTER = "osivia.sitemap.formatter";

    public final static String ATTR_SITEMAP_ITEMS = "osivia.sitemap.itemsToDisplay";

    private static Log logger = LogFactory.getLog(SitemapPortlet.class);

    ICMSService cmsService;

    private static ICMSServiceLocator cmsServiceLocator;

    private static SitemapFormatter formatter = new SitemapFormatter();


    public static ICMSService getCMSService() throws Exception {

        if (cmsServiceLocator == null) {
            cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        }

        return cmsServiceLocator.getCMSService();

    }

    private IPortalUrlFactory portalUrlFactory;

    @Override
    public void init(PortletConfig config) throws PortletException {
        super.init(config);

        portalUrlFactory = (IPortalUrlFactory) getPortletContext().getAttribute("UrlService");
        if (portalUrlFactory == null) {
            throw new PortletException("Cannot start Sitemap Portlet due to service unavailability");
        }
    }

    private Sitemap createServiceItem(ControllerContext ctx, CMSServiceCtx cmsReadNavContext, PortalControllerContext portalCtx, String basePath, String nuxeoPath, boolean isParentNavigable) throws Exception {
        CMSItem cmsItem = getCMSService().getPortalNavigationItem(cmsReadNavContext, basePath, nuxeoPath);

        if (cmsItem != null) {

            String title = cmsItem.getProperties().get("title");

            String navPath = cmsItem.getPath();
            String pageUrl = portalUrlFactory.getCMSUrl(portalCtx, null, navPath, null, null, "sitemap", null, null, null, null);
            pageUrl = portalUrlFactory.adaptPortalUrlToPopup(portalCtx, pageUrl, portalUrlFactory.POPUP_URL_ADAPTER_CLOSE);

            Sitemap displayItem = new Sitemap(title, pageUrl);
            displayItem.setPublished(cmsItem.getPublished());

            String displayMode = cmsItem.getProperties().get("pageDisplayMode");
            // String navigationElement = cmsItem.getProperties().get("navigationElement");
            if (displayMode != null && displayMode.equals("1")) {

                List<CMSItem> navItems = getCMSService().getPortalNavigationSubitems(cmsReadNavContext, basePath, nuxeoPath);

                for (CMSItem child : navItems) {


                    Sitemap newItem = createServiceItem(ctx, cmsReadNavContext, portalCtx, basePath, child.getPath(), true);

                    if (newItem != null)
                        displayItem.getChildren().add(newItem);

                }
            }

            return displayItem;

        } else
            return null;
    }

    @Override
    protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {

        logger.debug("doView");
        // TODO lire une prop du fichier de config
        PortalWindow window = WindowFactory.getWindow(request);
        String basePath = window.getProperty("osivia.cms.basePath");

        try {

            PortalControllerContext portalCtx = new PortalControllerContext(getPortletContext(), request, response);

            String pageUrl = portalUrlFactory.getCMSUrl(portalCtx, null, basePath, null, null, null, null, null, null, null);

            pageUrl = portalUrlFactory.adaptPortalUrlToPopup(portalCtx, pageUrl, portalUrlFactory.POPUP_URL_ADAPTER_CLOSE);
            request.setAttribute("pageUrl", pageUrl);


            response.setContentType("text/html");


            if (basePath != null) {

                CMSServiceCtx cmsReadNavContext = new CMSServiceCtx();

                ControllerContext context = (ControllerContext) request.getAttribute("osivia.controller");

                cmsReadNavContext.setControllerContext(context);
                cmsReadNavContext.setDisplayLiveVersion("1");


                Sitemap displayItem = createServiceItem(context, cmsReadNavContext, new PortalControllerContext(getPortletContext(), request, response),
                        basePath, basePath, true);

                if (displayItem != null) {

                    if (displayItem.getTitle() != null)
                        response.setTitle(displayItem.getTitle());

                    request.setAttribute(ATTR_SITEMAP_ITEMS, displayItem);
                }

                request.setAttribute(ATTR_SITEMAP_FORMATTER, formatter);

                getPortletContext().getRequestDispatcher("/WEB-INF/jsp/sitemap/view.jsp").include(request, response);

            }
        } catch (IOException e) {
            throw new PortletException(e);
        } catch (CMSException e) {
            throw new PortletException(e);
        } catch (Exception e) {
            throw new PortletException(e);
        }


        logger.debug("doView end");
    }
}
