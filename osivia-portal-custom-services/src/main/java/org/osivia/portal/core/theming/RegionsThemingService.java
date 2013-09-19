package org.osivia.portal.core.theming;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

import org.apache.commons.collections.MapUtils;
import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.common.servlet.BufferingResponseWrapper;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.theme.PageRendition;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerInvocationContext;
import org.jboss.portal.theme.PortalTheme;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.ThemeService;
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;
import org.jboss.portal.theme.page.Region;
import org.jboss.portal.theme.page.WindowContext;
import org.jboss.portal.theme.page.WindowResult;
import org.osivia.portal.api.customization.CustomizationContext;
import org.osivia.portal.api.theming.IAttributesBundle;
import org.osivia.portal.api.theming.IRegionsThemingService;
import org.osivia.portal.api.theming.RenderedRegionBean;
import org.osivia.portal.core.customization.ICustomizationService;

/**
 * Regions theming service implementation.
 *
 * @author Cédric Krommenhoek
 * @see IRegionsThemingService
 */
public class RegionsThemingService implements IRegionsThemingService {

    /** Request attributes key. */
    private static final String REQUEST_ATTRIBUTES_KEY = "osivia.request.attributes";
    /** Empty renderer. */
    private static final String EMPTY_RENDERER = "emptyRenderer";

    /** Default context path. */
    private String defaultContextPath;
    /** Customization service. */
    private ICustomizationService customizationService;


    /**
     * Default constructor.
     */
    public RegionsThemingService() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public void addRegion(RenderPageCommand renderPageCommand, PageRendition pageRendition, RenderedRegionBean renderedRegion) throws ControllerException {
        // Context path
        String contextPath;
        if (renderedRegion.isDefaultRegion()) {
            contextPath = this.defaultContextPath;
        } else {
            contextPath = this.getContextPath(renderPageCommand);
        }

        // Server invocation
        ServerInvocation serverInvocation = renderPageCommand.getControllerContext().getServerInvocation();
        // Server context
        ServerInvocationContext serverContext = serverInvocation.getServerContext();
        // Servlet context
        ServletContext servletContext = serverContext.getClientRequest().getSession().getServletContext().getContext(contextPath);
        // Locales
        Locale[] locales = serverInvocation.getRequest().getLocales();

        // Request
        RegionsRequestWrapper request = new RegionsRequestWrapper(renderPageCommand, pageRendition, serverContext.getClientRequest(), contextPath, locales);
        // Response
        BufferingResponseWrapper response = new BufferingResponseWrapper(serverContext.getClientResponse());

        // Request dispatcher
        RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher(renderedRegion.getPath());
        try {
            requestDispatcher.include(request, response);
        } catch (Exception e) {
            throw new ControllerException(e);
        }
        String markup = response.getContent();

        if (markup != null) {
            Map<String, String> windowProperties = new HashMap<String, String>();
            windowProperties.put(ThemeConstants.PORTAL_PROP_WINDOW_RENDERER, EMPTY_RENDERER);
            windowProperties.put(ThemeConstants.PORTAL_PROP_DECORATION_RENDERER, EMPTY_RENDERER);
            windowProperties.put(ThemeConstants.PORTAL_PROP_PORTLET_RENDERER, EMPTY_RENDERER);

            WindowResult windowResult = new WindowResult(renderedRegion.getName(), markup, MapUtils.EMPTY_MAP, windowProperties, null, WindowState.NORMAL,
                    Mode.VIEW);
            WindowContext windowContext = new WindowContext(renderedRegion.getName(), renderedRegion.getName(), null, windowResult);
            pageRendition.getPageResult().addWindowContext(windowContext);

            Region region = pageRendition.getPageResult().getRegion2(renderedRegion.getName());
            DynaRenderOptions.NO_AJAX.setOptions(region.getProperties());
        }
    }


    /**
     * {@inheritDoc}
     */
    public String getContextPath(RenderPageCommand renderPageCommand) {
        ThemeService themeService = renderPageCommand.getControllerContext().getController().getPageService().getThemeService();
        String themeId = renderPageCommand.getPage().getProperty(ThemeConstants.PORTAL_PROP_THEME);
        PortalTheme theme = themeService.getThemeById(themeId);
        return theme.getThemeInfo().getContextPath();
    }


    /**
     * {@inheritDoc}
     */
    public Object getAttribute(RenderPageCommand renderPageCommand, PageRendition pageRendition, String name) {
        ControllerContext controllerContext = renderPageCommand.getControllerContext();
        Map<String, Object> attributes = this.getRequestAttributes(controllerContext);
        if (attributes.containsKey(name)) {
            return attributes.get(name);
        } else {
            return this.computeAttributeValue(renderPageCommand, pageRendition, attributes, name);
        }
    }


    /**
     * Utility method used to get attributes map in request scope.
     *
     * @param controllerContext controller context
     * @return attributes map
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getRequestAttributes(ControllerContext controllerContext) {
        Map<String, Object> attributes = (Map<String, Object>) controllerContext.getAttribute(Scope.REQUEST_SCOPE, REQUEST_ATTRIBUTES_KEY);
        if (attributes == null) {
            attributes = new HashMap<String, Object>();
            controllerContext.setAttribute(Scope.REQUEST_SCOPE, REQUEST_ATTRIBUTES_KEY, attributes);
        }
        return attributes;
    }


    /**
     * Utility method used to compute attribute value and save it into attributes map.
     *
     * @param renderPageCommand render page command
     * @param pageRendition page rendition
     * @param attributes attributes map
     * @param name attribute name
     * @return attribute value
     */
    private Object computeAttributeValue(RenderPageCommand renderPageCommand, PageRendition pageRendition, Map<String, Object> attributes, String name) {
        IAttributesBundle bundle = this.getAttributeBundle(name);
        if (bundle != null) {
            try {
                bundle.fill(renderPageCommand, pageRendition, attributes);
                return attributes.get(name);
            } catch (ControllerException e) {
                // Do nothing
            }
        }

        attributes.put(name, null);
        return null;
    }


    /**
     * Get bundle for the current attribute name.
     *
     * @param name attribute name
     * @return attributes bundle
     */
    private IAttributesBundle getAttributeBundle(String name) {
        for (DefaultAttributesBundles value : DefaultAttributesBundles.values()) {
            IAttributesBundle bundle = value.getBundle();
            if (bundle.getAttributeNames().contains(name)) {
                return bundle;
            }
        }

        // Customizer invocation
        Map<String, Object> customizerAttributes = new HashMap<String, Object>();
        customizerAttributes.put(IAttributesBundle.CUSTOMIZER_ATTRIBUTE_NAME, name);
        CustomizationContext context = new CustomizationContext(customizerAttributes);
        this.customizationService.customize(IAttributesBundle.CUSTOMIZER_ID, context);
        return (IAttributesBundle) customizerAttributes.get(IAttributesBundle.CUSTOMIZER_ATTRIBUTE_RESULT);
    }


    /**
     * Setter for defaultContextPath.
     *
     * @param defaultContextPath the defaultContextPath to set
     */
    public void setDefaultContextPath(String defaultContextPath) {
        this.defaultContextPath = defaultContextPath;
    }

    /**
     * Setter for customizationService.
     *
     * @param customizationService the customizationService to set
     */
    public void setCustomizationService(ICustomizationService customizationService) {
        this.customizationService = customizationService;
    }

}
