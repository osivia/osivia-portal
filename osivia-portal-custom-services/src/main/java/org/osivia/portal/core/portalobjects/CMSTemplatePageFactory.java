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

package org.osivia.portal.core.portalobjects;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.impl.model.portal.PortalObjectImpl;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.model.portal.navstate.PortalObjectNavigationalStateContext;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.core.page.IPortalObjectContainer;


/**
 * create a cms dynamic page (/CMS_LAYOUT)
 * 
 * @author Jean-Sébastien Steux
 *
 */
public class CMSTemplatePageFactory {

    private static CMSTemplatePageDescriptor getCMSTemplate(DynamicPortalObjectContainer dynaContainer, IPortalObjectContainer container, PortalObjectPath cmsPagePath) {


        PageNavigationalState ns = null;

        ControllerContext controllerContext = dynaContainer.getCommandContext();
        if (controllerContext != null) {

            NavigationalStateContext nsContext = (NavigationalStateContext) controllerContext.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
            ns = nsContext.getPageNavigationalState(cmsPagePath.toString());


        } else {
        	/* can't compile : incorrect constructor PortalObjectNavigationalStateContext
            ServerInvocation invocation = dynaContainer.getInvocation();
            PortalObjectNavigationalStateContext pnsCtx = new PortalObjectNavigationalStateContext(invocation.getContext().getAttributeResolver(
                    ControllerCommand.PRINCIPAL_SCOPE));

            ns = pnsCtx.getPageNavigationalState(cmsPagePath.toString());
            */

        }


        if (ns != null) {
            String layoutPath[] = ns.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.layout_path"));

            if (layoutPath != null) {
                String currentPortalName = StringUtils.split(cmsPagePath.toString(), "/")[0];
                String layoutPortalName = StringUtils.split(layoutPath[0], "/")[0];
                String currentTemplatePath = StringUtils.replace(layoutPath[0], layoutPortalName, currentPortalName, 1);

                PortalObjectPath currentTemplateObjectPath = PortalObjectPath.parse(currentTemplatePath, PortalObjectPath.CANONICAL_FORMAT);
                PortalObjectId currentTemplateId = new PortalObjectId("", currentTemplateObjectPath);
                PortalObjectImpl portalObject = (PortalObjectImpl) container.getNonDynamicObject(currentTemplateId);

                if ((portalObject == null) && !StringUtils.equals(currentPortalName, layoutPortalName)) {
                    PortalObjectPath layoutObjectPath = PortalObjectPath.parse(layoutPath[0], PortalObjectPath.CANONICAL_FORMAT);
                    PortalObjectId layoutId = new PortalObjectId("", layoutObjectPath);
                    portalObject = (PortalObjectImpl) container.getNonDynamicObject(layoutId);
                }

                if (portalObject == null) {
                    throw new IllegalArgumentException("Template " + layoutPath[0] + " doesn't exist");
                }
                
                String layoutTheme = null;
                
                String layoutThemes[] = ns.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.theme_path"));
                if (layoutThemes != null) {
                    layoutTheme = layoutThemes[0];
                    
                }

                return new CMSTemplatePageDescriptor(portalObject, layoutTheme);

            }
        }

        return null;
    }


    /**
     * create the CMS_LAYOUT page
     * 
     * @param dynaContainer
     * @param container
     * @param currentPath
     * @return
     */
    public static CMSTemplatePage getCMSPage(DynamicPortalObjectContainer dynaContainer, IPortalObjectContainer container, PortalObjectPath currentPath) {
 
        CMSTemplatePageDescriptor cmsPageDescriptor = CMSTemplatePageFactory.getCMSTemplate(dynaContainer, container, currentPath);

        if (cmsPageDescriptor != null) {

            PortalObjectId cmsParentId = new PortalObjectId("", currentPath.getParent());
            
            CMSTemplatePage dynamicPage = CMSTemplatePage.createPage(container, cmsParentId,  cmsPageDescriptor.getTemplate(), cmsPageDescriptor.getTheme(), dynaContainer);

            return dynamicPage;
        }

        return null;
    }


}
