package org.osivia.portal.core.theming.attributesbundle;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.commons.lang.ArrayUtils;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.jboss.portal.core.theme.PageRendition;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.theming.IAttributesBundle;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.formatters.IFormatter;
import org.osivia.portal.core.pagemarker.PageMarkerUtils;

/**
 * Transversal attributes bundle.
 *
 * @author Cédric Krommenhoek
 * @see IAttributesBundle
 */
public final class TransversalAttributesBundle implements IAttributesBundle {

    /** Singleton instance. */
    private static TransversalAttributesBundle instance;


    /** Formatter. */
    private final IFormatter formatter;
    /** Portal URL factory. */
    private final IPortalUrlFactory urlFactory;

    /** Toolbar attributes names. */
    private final Set<String> names;


    /**
     * Private constructor.
     */
    private TransversalAttributesBundle() {
        super();

        // Formatter
        this.formatter = Locator.findMBean(IFormatter.class, "osivia:service=Interceptor,type=Command,name=AssistantPageCustomizer");
        // URL factory
        this.urlFactory = Locator.findMBean(IPortalUrlFactory.class, "osivia:service=UrlFactory");

        this.names = new TreeSet<String>();
        this.names.add(InternalConstants.ATTR_CONTROLLER_CONTEXT);
        this.names.add(InternalConstants.ATTR_CMS_PATH);
        this.names.add(InternalConstants.ATTR_COMMAND_PREFIX);
        this.names.add(InternalConstants.ATTR_TOOLBAR_SETTINGS_PAGE);
        this.names.add(InternalConstants.ATTR_TOOLBAR_SETTINGS_FORMATTER);
        this.names.add(Constants.ATTR_PORTAL_CTX);
        this.names.add(Constants.ATTR_URL_FACTORY);
    }


    /**
     * Singleton instance access.
     *
     * @return singleton instance
     */
    public static TransversalAttributesBundle getInstance() {
        if (instance == null) {
            instance = new TransversalAttributesBundle();
        }
        return instance;
    }


    /**
     * {@inheritDoc}
     */
    public void fill(RenderPageCommand renderPageCommand, PageRendition pageRendition, Map<String, Object> attributes) throws ControllerException {
        // Current page
        Page page = renderPageCommand.getPage();

        // Controller context
        ControllerContext controllerContext = renderPageCommand.getControllerContext();
        attributes.put(InternalConstants.ATTR_CONTROLLER_CONTEXT, controllerContext);
        // CMS path
        String cmsPath = this.computeCMSPath(controllerContext, page);
        attributes.put(InternalConstants.ATTR_CMS_PATH, cmsPath);
        // Command prefix
        String commandPrefix = this.computeCommandPrefix(controllerContext);
        attributes.put(InternalConstants.ATTR_COMMAND_PREFIX, commandPrefix);
        // Current page
        attributes.put(InternalConstants.ATTR_TOOLBAR_SETTINGS_PAGE, page);
        // Formatter
        attributes.put(InternalConstants.ATTR_TOOLBAR_SETTINGS_FORMATTER, this.formatter);
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(controllerContext);
        attributes.put(Constants.ATTR_PORTAL_CTX, portalControllerContext);
        // URL factory
        attributes.put(Constants.ATTR_URL_FACTORY, this.urlFactory);
    }


    /**
     * Utility method used to compute CMS path.
     *
     * @param controllerContext controller context
     * @param page current page
     * @return CMS path
     */
    private String computeCMSPath(ControllerContext controllerContext, Page page) {
        // Navigational state context
        NavigationalStateContext navigationalStateContext = (NavigationalStateContext) controllerContext
                .getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
        // Page state
        PageNavigationalState pageState = navigationalStateContext.getPageNavigationalState(page.getId().toString());

        String[] sPath = null;
        if (pageState != null) {
            sPath = pageState.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.path"));
        }
        if (ArrayUtils.isNotEmpty(sPath)) {
            return sPath[0];
        }

        return null;
    }


    /**
     * Utility method used to compute command prefix.
     *
     * @param controllerContext controller context
     * @return command prefix
     */
    private String computeCommandPrefix(ControllerContext controllerContext) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(controllerContext.getServerInvocation().getServerContext().getPortalContextPath());
        buffer.append("/pagemarker/");
        buffer.append(PageMarkerUtils.getCurrentPageMarker(controllerContext));
        return buffer.toString();
    }


    /**
     * {@inheritDoc}
     */
    public Set<String> getAttributeNames() {
        return this.names;
    }

}
