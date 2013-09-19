package org.osivia.portal.core.theming;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.jboss.portal.common.servlet.BufferingRequestWrapper;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.theme.PageRendition;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.theming.IRegionsThemingService;

/**
 * Regions HTTP servlet request wrapper.
 *
 * @author Cédric Krommenhoek
 * @see BufferingRequestWrapper
 */
public class RegionsRequestWrapper extends BufferingRequestWrapper {

    /** Regions theming service. */
    private final IRegionsThemingService regionsThemingService;

    /** Render page command. */
    private final RenderPageCommand renderPageCommand;
    /** Page rendition. */
    private final PageRendition pageRendition;


    /**
     * Constructor.
     *
     * @param renderPageCommand render page command
     * @param pageRendition page rendition
     * @param servletRequest HTTP servlet request
     * @param contextPath context path
     * @param locales locales
     */
    public RegionsRequestWrapper(RenderPageCommand renderPageCommand, PageRendition pageRendition, HttpServletRequest servletRequest, String contextPath,
            Locale[] locales) {
        super(servletRequest, contextPath, locales);
        this.renderPageCommand = renderPageCommand;
        this.pageRendition = pageRendition;

        this.regionsThemingService = Locator.findMBean(IRegionsThemingService.class, IRegionsThemingService.MBEAN_NAME);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Object getAttribute(String name) {
        Object attribute = super.getAttribute(name);
        if (attribute == null) {
            attribute = this.regionsThemingService.getAttribute(this.renderPageCommand, this.pageRendition, name);
        }
        return attribute;
    }

}
