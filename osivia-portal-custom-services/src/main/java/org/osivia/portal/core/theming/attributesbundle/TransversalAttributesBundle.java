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
package org.osivia.portal.core.theming.attributesbundle;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.commons.lang.ArrayUtils;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.jboss.portal.core.theme.PageRendition;
import org.jboss.portal.server.ServerInvocationContext;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.theming.IAttributesBundle;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CmsCommand;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.formatters.IFormatter;
import org.osivia.portal.core.page.PageType;
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
        this.names.add(InternalConstants.ATTR_TOOLBAR_SETTINGS_COMMAND_URL);
        this.names.add(Constants.ATTR_PAGE_CATEGORY);   
        this.names.add(Constants.ATTR_USER_DATAS); 
        this.names.add(Constants.ATTR_SPACE_CONFIG);         
        this.names.add(Constants.ATTR_PORTAL_CTX);
        this.names.add(Constants.ATTR_URL_FACTORY);
        this.names.add(Constants.ATTR_WIZARD_MODE);
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
    @SuppressWarnings("unchecked")
    public void fill(RenderPageCommand renderPageCommand, PageRendition pageRendition, Map<String, Object> attributes) throws ControllerException {
        // Controller context
        ControllerContext controllerContext = renderPageCommand.getControllerContext();
        attributes.put(InternalConstants.ATTR_CONTROLLER_CONTEXT, controllerContext);
        // Server context
        ServerInvocationContext serverContext = controllerContext.getServerInvocation().getServerContext();

        // Current page
        Page page = renderPageCommand.getPage();
        PageType pageType = PageType.getPageType(page, controllerContext);
        if (PageType.CMS_TEMPLATED_PAGE.equals(pageType)) {
            page = (Page) page.getParent();
        }

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
        // Generic command URL
        String commandUrl = serverContext.getPortalContextPath() + "/commands";
        attributes.put(InternalConstants.ATTR_TOOLBAR_SETTINGS_COMMAND_URL, commandUrl);

        // User datas
        Map<String, Object> userDatas = (Map<String, Object>) controllerContext.getServerInvocation().getAttribute(Scope.SESSION_SCOPE, "osivia.userDatas");
        attributes.put(Constants.ATTR_USER_DATAS, userDatas);

        attributes.put(Constants.ATTR_PAGE_CATEGORY, renderPageCommand.getPage().getProperty("osivia.pageCategory"));
        CMSItem spaceItem;
        try {
            spaceItem = (CMSItem) controllerContext.getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.cms.spaceConfig");

        } catch (Exception e) {
           throw new ControllerException(e);
        }
        if( spaceItem != null)
            attributes.put(Constants.ATTR_SPACE_CONFIG, spaceItem.getNativeItem());

        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(controllerContext);
        attributes.put(Constants.ATTR_PORTAL_CTX, portalControllerContext);
        // URL factory
        attributes.put(Constants.ATTR_URL_FACTORY, this.urlFactory);
        // Wizard mode indicator
        String mode = (String) controllerContext.getAttribute(ControllerCommand.SESSION_SCOPE, InternalConstants.ATTR_WINDOWS_SETTING_MODE);
        attributes.put(Constants.ATTR_WIZARD_MODE, InternalConstants.VALUE_WINDOWS_SETTING_WIZARD_MODE.equals(mode));
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
