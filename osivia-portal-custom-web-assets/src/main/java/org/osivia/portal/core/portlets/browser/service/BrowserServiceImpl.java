package org.osivia.portal.core.portlets.browser.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.portlet.PortletException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.dom.DOMElement;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.theme.impl.render.dynamic.json.JSONArray;
import org.jboss.portal.theme.impl.render.dynamic.json.JSONException;
import org.jboss.portal.theme.impl.render.dynamic.json.JSONObject;
import org.osivia.portal.api.HTMLConstants;
import org.osivia.portal.api.context.PortalControllerContext;
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
    private final IPortalUrlFactory portalUrlFactory;


    /**
     * Constructor.
     */
    public BrowserServiceImpl() {
        super();
        this.cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        this.portalUrlFactory = Locator.findMBean(IPortalUrlFactory.class, "osivia:service=UrlFactory");
    }


    /**
     * {@inheritDoc}
     */
    public String browseLiveContent(PortalControllerContext portalControllerContext) throws PortletException {
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
        // Controller context
        ControllerContext controllerContext = (ControllerContext) portalControllerContext.getControllerCtx();
        // CMS context
        CMSServiceCtx cmsContext = new CMSServiceCtx();
        cmsContext.setControllerContext(controllerContext);
        cmsContext.setDisplayLiveVersion("1");

        try {
            Element element = this.generateRecursiveHTMLTree(portalControllerContext, cmsContext, cmsBasePath, null);
            return element.asXML();
        } catch (CMSException e) {
            throw new PortletException(e);
        }
    }


    /**
     * Utility method used to generate recursive HTML tree.
     * 
     * @param portalControllerContext portal controller context
     * @param cmsContext CMS context
     * @param cmsBasePath CMS base path
     * @param currentCMSItem current CMS item
     * @return HTML element
     * @throws CMSException
     */
    private Element generateRecursiveHTMLTree(PortalControllerContext portalControllerContext, CMSServiceCtx cmsContext, String cmsBasePath,
            CMSItem currentCMSItem) throws CMSException {
        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();

        boolean rootElement = false;
        Element element = null;

        CMSItem cmsItem = currentCMSItem;
        if (cmsItem == null) {
            rootElement = true;
            cmsItem = cmsService.getPortalNavigationItem(cmsContext, cmsBasePath, cmsBasePath);
        }

        if (cmsItem != null) {
            // Current CMS item properties
            String title = cmsItem.getProperties().get("displayName");
            String path = cmsItem.getPath();
            String cmsUrl = this.portalUrlFactory.getCMSUrl(portalControllerContext, null, path, null, null, InternalConstants.LIVE_EDITION, null, null, null,
                    null);
            String popupUrl = this.portalUrlFactory.adaptPortalUrlToPopup(portalControllerContext, cmsUrl, IPortalUrlFactory.POPUP_URL_ADAPTER_CLOSE);

            // Current CMS item nodes
            Element li = new DOMElement(QName.get(HTMLConstants.LI));
            Element a = new DOMElement(QName.get(HTMLConstants.A));
            a.addAttribute(QName.get(HTMLConstants.HREF), popupUrl);
            a.setText(title);
            li.add(a);

            // Result element
            if (rootElement) {
                Element root = new DOMElement(QName.get(HTMLConstants.UL));
                root.add(li);
                element = root;
            } else {
                element = li;
            }

            // CMS sub-items
            List<CMSItem> cmsSubItems = cmsService.getPortalSubitems(cmsContext, cmsItem.getPath());
            if (CollectionUtils.isNotEmpty(cmsSubItems)) {
                Element ul = new DOMElement(QName.get(HTMLConstants.UL));
                li.add(ul);
                for (CMSItem cmsSubItem : cmsSubItems) {
                    Element subElement = this.generateRecursiveHTMLTree(portalControllerContext, cmsContext, cmsBasePath, cmsSubItem);
                    if (subElement != null) {
                        ul.add(subElement);
                    }
                }
            }
        }
        return element;
    }


    /**
     * {@inheritDoc}
     */
    public String browseLazyLiveContent(PortalControllerContext portalControllerContext, String path) throws PortletException {
        try {
            // CMS service
            ICMSService cmsService = this.cmsServiceLocator.getCMSService();

            // Controller context
            ControllerContext controllerContext = (ControllerContext) portalControllerContext.getControllerCtx();
            // CMS context
            CMSServiceCtx cmsContext = new CMSServiceCtx();
            cmsContext.setControllerContext(controllerContext);
            cmsContext.setDisplayLiveVersion("1");

            JSONArray array = new JSONArray();
            JSONArray childrenArray;

            String parentPath = path;
            if (parentPath == null) {
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
            List<CMSItem> cmsSubItems = cmsService.getPortalSubitems(cmsContext, parentPath);
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
        String cmsUrl = this.portalUrlFactory
                .getCMSUrl(portalControllerContext, null, path, null, null, InternalConstants.LIVE_EDITION, null, null, null,
                        null);
        String popupUrl = this.portalUrlFactory.adaptPortalUrlToPopup(portalControllerContext, cmsUrl, IPortalUrlFactory.POPUP_URL_ADAPTER_CLOSE);
        String id;
        try {
            id = namespace + URLEncoder.encode(path, CharEncoding.UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new PortletException(e);
        }

        // CMS item type
        CMSItemType type = cmsItem.getType();
        if (type == null) {
            // Default CMS item type : folder
            type = CMSItemType.FOLDER;
        }

        // JSON object
        JSONObject object = new JSONObject();
        if (root) {
            object.put("state", "open");
        } else if (type.isContainer()) {
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
        if (root) {
            attr.put("rel", "Root");
        } else {
            attr.put("rel", type.getName());
        }
        object.put("attr", attr);

        return object;
    }

}
