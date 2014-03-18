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
package org.osivia.portal.core.path;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.portlet.PortletException;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.theme.impl.render.dynamic.json.JSONArray;
import org.jboss.portal.theme.impl.render.dynamic.json.JSONException;
import org.jboss.portal.theme.impl.render.dynamic.json.JSONObject;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.path.ICMSPathService;
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

/**
 * CMS path service implementation.
 *
 * @author Cédric Krommenhoek
 * @see ICMSPathService
 */
public class CMSPathService implements ICMSPathService {

    /** CMS service locator. */
    private ICMSServiceLocator cmsServiceLocator;
    /** Portal URL factory. */
    private IPortalUrlFactory portalURLFactory;

    /**
     * Default contructor.
     */
    public CMSPathService() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public String browseContentLazyLoading(PortalControllerContext portalControllerContext, String path, boolean liveContent, boolean onlyNavigableItems)
            throws PortletException {
        try {
            // CMS service
            ICMSService cmsService = this.cmsServiceLocator.getCMSService();

            // Controller context
            ControllerContext controllerContext = (ControllerContext) portalControllerContext.getControllerCtx();
            // CMS context
            CMSServiceCtx cmsContext = new CMSServiceCtx();
            cmsContext.setControllerContext(controllerContext);
            if (liveContent) {
                cmsContext.setDisplayLiveVersion("1");
            }

            // Current window
            PortalWindow window = WindowFactory.getWindow(portalControllerContext.getRequest());
            // CMS base path
            String cmsBasePath = window.getProperty("osivia.browser.path");
            if (StringUtils.isBlank(cmsBasePath)) {
                cmsBasePath = window.getPageProperty("osivia.cms.basePath");
                if (StringUtils.isBlank(cmsBasePath)) {
                    throw new PortletException("Le contexte courant ne dispose pas de chemin CMS.");
                }
            }

            // JSON objects
            JSONArray array = new JSONArray();
            JSONArray childrenArray;

            String parentPath = path;
            if (parentPath == null) {
                CMSItem cmsItem = cmsService.getPortalNavigationItem(cmsContext, cmsBasePath, cmsBasePath);
                JSONObject object = this.generateJSONObject(portalControllerContext, cmsItem, true);
                childrenArray = new JSONArray();
                object.put("children", childrenArray);
                array.put(object);

                parentPath = cmsItem.getPath();
            } else {
                childrenArray = array;
            }

            // Children lazy loading nodes
            List<CMSItem> cmsSubItems;
            if (onlyNavigableItems) {
                cmsSubItems = cmsService.getPortalNavigationSubitems(cmsContext, cmsBasePath, parentPath);
            } else {
                cmsSubItems = cmsService.getPortalSubitems(cmsContext, parentPath);
            }

            if (cmsSubItems != null) {
                for (CMSItem cmsSubItem : cmsSubItems) {
                    childrenArray.put(this.generateJSONObject(portalControllerContext, cmsSubItem, false));
                }
            }

            return array.toString();
        } catch (CMSException e) {
            throw new PortletException(e);
        } catch (JSONException e) {
            throw new PortletException(e);
        }
    }


    /**
     * Utility method used to generate JSON object from CMS item.
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
        // Namespace
        String namespace = portalControllerContext.getResponse().getNamespace();

        // CMS item properties
        String title = cmsItem.getProperties().get("displayName");
        String path = cmsItem.getPath();
        String cmsUrl = this.portalURLFactory
                .getCMSUrl(portalControllerContext, null, path, null, null, InternalConstants.LIVE_EDITION, null, null, null, null);
        String popupUrl = this.portalURLFactory.adaptPortalUrlToPopup(portalControllerContext, cmsUrl, IPortalUrlFactory.POPUP_URL_ADAPTER_CLOSE);
        String id;
        try {
            id = namespace + URLEncoder.encode(path, CharEncoding.UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new PortletException(e);
        }
        String jstreeType;
        if (BooleanUtils.isFalse(cmsItem.getPublished()) || BooleanUtils.isTrue(cmsItem.getBeingModified())) {
            jstreeType = "live";
        } else {
            jstreeType = "published";
        }

        // CMS item type folderish indicator
        boolean folderish = true;
        CMSItemType type = cmsItem.getType();
        if (type != null) {
            folderish = type.isFolderish();
        }

        // JSON object
        JSONObject object = new JSONObject();
        if (root) {
            object.put("state", "open");
        } else if (folderish) {
            object.put("state", "closed");
        }

        // JSON object data
        JSONObject data = new JSONObject();
        data.put("title", title);
        object.put("data", data);

        // JSON object data attributes
        JSONObject dataAttr = new JSONObject();
        dataAttr.put("href", popupUrl);
        data.put("attr", dataAttr);

        // JSON object attributes
        JSONObject attr = new JSONObject();
        attr.put("id", id);
        attr.put("rel", jstreeType); // FIXME
        object.put("attr", attr);

        return object;
    }


    /**
     * Getter for cmsServiceLocator.
     *
     * @return the cmsServiceLocator
     */
    public ICMSServiceLocator getCmsServiceLocator() {
        return this.cmsServiceLocator;
    }

    /**
     * Setter for cmsServiceLocator.
     *
     * @param cmsServiceLocator the cmsServiceLocator to set
     */
    public void setCmsServiceLocator(ICMSServiceLocator cmsServiceLocator) {
        this.cmsServiceLocator = cmsServiceLocator;
    }

    /**
     * Getter for portalURLFactory.
     *
     * @return the portalURLFactory
     */
    public IPortalUrlFactory getPortalURLFactory() {
        return this.portalURLFactory;
    }

    /**
     * Setter for portalURLFactory.
     *
     * @param portalURLFactory the portalURLFactory to set
     */
    public void setPortalURLFactory(IPortalUrlFactory portalURLFactory) {
        this.portalURLFactory = portalURLFactory;
    }

}
