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
package org.osivia.portal.core.portlets.browser.service;

import java.util.List;

import javax.portlet.PortletException;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.portal.theme.impl.render.dynamic.json.JSONArray;
import org.jboss.portal.theme.impl.render.dynamic.json.JSONException;
import org.jboss.portal.theme.impl.render.dynamic.json.JSONObject;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.windows.PortalWindow;
import org.osivia.portal.api.windows.WindowFactory;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSItemType;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.constants.InternalConstants;
import org.springframework.stereotype.Service;

/**
 * Live content browser service implementation.
 *
 * @author Cédric Krommenhoek
 * @see IBrowserService
 */
@Service
public class BrowserServiceImpl implements IBrowserService {

    /** CMS service locator. */
    private final ICMSServiceLocator cmsServiceLocator;
    /** Portal URL factory. */
    private final IPortalUrlFactory portalURLFactory;
    /** Bundle factory. */
    private final IBundleFactory bundleFactory;


    /**
     * Constructor.
     */
    public BrowserServiceImpl() {
        super();

        // CMS service locator
        this.cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, ICMSServiceLocator.MBEAN_NAME);
        // Portal URL factory
        this.portalURLFactory = Locator.findMBean(IPortalUrlFactory.class, IPortalUrlFactory.MBEAN_NAME);
        // Bundle factory
        IInternationalizationService internationalizationService = Locator.findMBean(IInternationalizationService.class,
                IInternationalizationService.MBEAN_NAME);
        this.bundleFactory = internationalizationService.getBundleFactory(this.getClass().getClassLoader());
    }


    /**
     * {@inheritDoc}
     */
    public JSONArray browse(PortalControllerContext portalControllerContext, String path) throws PortletException {
        try {
            // CMS service
            ICMSService cmsService = this.cmsServiceLocator.getCMSService();

            // CMS context
            CMSServiceCtx cmsContext = new CMSServiceCtx();
            cmsContext.setPortalControllerContext(portalControllerContext);
            cmsContext.setDisplayLiveVersion("1");

            // Current window
            PortalWindow window = WindowFactory.getWindow(portalControllerContext.getRequest());

            // CMS base path
            String cmsBasePath = window.getProperty("osivia.browser.path");
            if (StringUtils.isBlank(cmsBasePath)) {
                cmsBasePath = window.getPageProperty("osivia.cms.basePath");
                if (StringUtils.isBlank(cmsBasePath)) {
                    Bundle bundle = this.bundleFactory.getBundle(portalControllerContext.getRequest().getLocale());
                    throw new PortletException(bundle.getString("ERROR_MESSAGE_BROWSER_WITHOUT_CMS"));
                }
            }


            // JSON objects
            JSONArray array = new JSONArray();
            JSONArray childrenArray;


            // Parent path
            String parentPath;

            if (path == null) {
                // Root
                CMSItem cmsItem = cmsService.getPortalNavigationItem(cmsContext, cmsBasePath, cmsBasePath);
                JSONObject object = this.generateJSONObject(portalControllerContext, cmsItem, true);
                childrenArray = new JSONArray();
                object.put("children", childrenArray);
                array.put(object);

                parentPath = cmsItem.getPath();
            } else {
                childrenArray = array;

                parentPath = path;
            }


            // Children lazy loading nodes
            List<CMSItem> cmsSubItems = cmsService.getPortalSubitems(cmsContext, parentPath);
            if (cmsSubItems != null) {
                for (CMSItem cmsSubItem : cmsSubItems) {
                    childrenArray.put(this.generateJSONObject(portalControllerContext, cmsSubItem, false));
                }
            }


            return array;
        } catch (CMSException e) {
            throw new PortletException(e);
        } catch (JSONException e) {
            throw new PortletException(e);
        }
    }


    /**
     * Generate JSON object from CMS item.
     *
     * @param portalControllerContext portal controller context
     * @param cmsItem current CMS item
     * @param root root node indicator
     * @return JSON object
     * @throws PortletException
     * @throws JSONException
     */
    private JSONObject generateJSONObject(PortalControllerContext portalControllerContext, CMSItem cmsItem, boolean root) throws PortletException,
            JSONException {
        // CMS item type
        CMSItemType type = cmsItem.getType();
        boolean browsable = ((type == null) || type.isBrowsable());

        // URL
        String cmsURL = this.portalURLFactory.getCMSUrl(portalControllerContext, null, cmsItem.getPath(), null, null, InternalConstants.PROXY_PREVIEW, null,
                null, null, null);
        String popupURL = this.portalURLFactory.adaptPortalUrlToPopup(portalControllerContext, cmsURL, IPortalUrlFactory.POPUP_URL_ADAPTER_CLOSE);


        JSONObject object = new JSONObject();

        // Title
        object.put("title", cmsItem.getProperties().get("displayName"));

        // Folder indicator
        object.put("folder", browsable);

        // Lazy indicator
        object.put("lazy", !root && browsable);

        // Expanded indicator
        object.put("expanded", root);

        // Path
        object.put("path", cmsItem.getPath());

        // URL
        object.put("href", popupURL);

        // Icon
        if (root) {
            object.put("iconclass", "halflings halflings-map-marker");
        } else if (BooleanUtils.isFalse(cmsItem.getPublished()) || BooleanUtils.isTrue(cmsItem.getBeingModified())) {
            object.put("iconclass", "halflings halflings-pencil text-info");
        } else {
            object.put("iconclass", "halflings halflings-ok text-success");
        }

        return object;
    }

}
