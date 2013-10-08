package org.osivia.portal.core.theming.attributesbundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.dom.DOMElement;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.theme.PageRendition;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.HTMLConstants;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.theming.IAttributesBundle;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.page.PagePathUtils;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;
import org.osivia.portal.core.security.CmsPermissionHelper;
import org.osivia.portal.core.security.CmsPermissionHelper.Level;

/**
 * 
 * Generator of the <head> meta datas informations as title, meta:author, meta:description...
 * 
 */
public class HeaderMetadataAttributesBundle implements IAttributesBundle {

    /** Singleton instance. */
    private static HeaderMetadataAttributesBundle instance;

    /** CMS service. */
    private final ICMSService cmsService;

    /** params. */
    private final Set<String> names;

    /**
     * Default constructor.
     */
    private HeaderMetadataAttributesBundle() {
        // CMS service
        ICMSServiceLocator cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        this.cmsService = cmsServiceLocator.getCMSService();

        // Properties
        names = new TreeSet<String>();
        names.add(Constants.ATTR_HEADER_METADATA_CONTENT);

    }


    /**
     * Generate html.
     * 
     * @param renderPageCommand command
     * @param pageRendition ..
     * @param attributes attributes
     * 
     */
    public void fill(RenderPageCommand renderPageCommand, PageRendition pageRendition, Map<String, Object> attributes) throws ControllerException {
        ControllerContext controllerContext = renderPageCommand.getControllerContext();
        Page page = renderPageCommand.getPage();

        // SEO content
        String content = this.formatHTMLHeaderMetaDatas(controllerContext, page);
        attributes.put(Constants.ATTR_HEADER_METADATA_CONTENT, content);

    }

    /**
     * Generate html.
     * 
     * @param context context
     * @param page the page
     * @return html output
     */
    private String formatHTMLHeaderMetaDatas(ControllerContext context, Page page) {
        CMSServiceCtx cmsCtx = new CMSServiceCtx();
        cmsCtx.setServerInvocation(context.getServerInvocation());
        cmsCtx.setControllerContext(context);

        // Get content path
        String contentPath = PagePathUtils.getContentPath(context, page.getId());

        // Get ECM object
        CMSItem doc = null;
        try {
            if (CmsPermissionHelper.getCurrentPageSecurityLevel(context, contentPath) == Level.allowPreviewVersion) {
                cmsCtx.setDisplayLiveVersion("1");
            }

            doc = this.cmsService.getContent(cmsCtx, contentPath);

        } catch (CMSException e) {
            // Do nothing
        }
        String result = "";

        // Generate html
        Element title = new DOMElement(QName.get(HTMLConstants.TITLE));
        List<Element> listMeta = new ArrayList<Element>();


        if (doc != null) {

            // if ECM document exists
            for (Map.Entry<String, String> metas : doc.getMetaProperties().entrySet()) {
                if (metas.getKey().equals(Constants.HEADER_TITLE)) {
                    title.setText(metas.getValue());
                } else if (metas.getKey().startsWith(Constants.HEADER_META)) {
                    Element meta = new DOMElement(QName.get(HTMLConstants.META));
                    meta.addAttribute(QName.get(HTMLConstants.NAME), metas.getKey().replace(Constants.HEADER_META.concat("."), ""));
                    meta.addAttribute(QName.get(HTMLConstants.CONTENT), metas.getValue());
                    listMeta.add(meta);
                }
            }

        } else {
            // else get the default display name of the page
            String name = PortalObjectUtils.getDisplayName(page, context.getServerInvocation().getRequest().getLocales());
            title.setText(name);
        }

        result = title.asXML();
        for (Element meta : listMeta) {
            result = result.concat(meta.asXML());
        }

        return result;
    }

    /**
     * Public attributes for this bundle.
     * 
     * @return Public attributes for this bundle
     */
    public Set<String> getAttributeNames() {
        return names;
    }


    /**
     * Singleton instance access.
     * 
     * @return singleton instance
     */
    public static HeaderMetadataAttributesBundle getInstance() {
        if (instance == null) {
            instance = new HeaderMetadataAttributesBundle();
        }
        return instance;
    }

}
