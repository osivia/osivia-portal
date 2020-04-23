package org.osivia.portal.core.editor;

import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.editor.EditorModule;
import org.osivia.portal.api.editor.EditorService;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.urls.PortalUrlType;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;

import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Editor service implementation.
 *
 * @author Cédric Krommenhoek
 * @see EditorService
 */
public class EditorServiceImpl implements EditorService {

    /**
     * CMS service locator.
     */
    private ICMSServiceLocator cmsServiceLocator;
    /**
     * Portal URL factory.
     */
    private IPortalUrlFactory portalUrlFactory;
    /**
     * Internationalization service.
     */
    private IInternationalizationService internationalizationService;


    /**
     * Constructor.
     */
    public EditorServiceImpl() {
        super();
    }


    @Override
    public List<EditorModule> getModules(PortalControllerContext portalControllerContext) throws PortalException {
        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();
        // CMS context
        CMSServiceCtx cmsContext = new CMSServiceCtx();
        cmsContext.setPortalControllerContext(portalControllerContext);

        // Editor modules
        List<EditorModule> modules;
        try {
            modules = cmsService.getEditorModules(cmsContext);
        } catch (CMSException e) {
            throw new PortalException(e);
        }

        return modules;
    }


    @Override
    public void serveResource(PortalControllerContext portalControllerContext, String editorId) throws PortletException, IOException {
        // Resource request
        ResourceRequest request = (ResourceRequest) portalControllerContext.getRequest();
        // Resource response
        ResourceResponse response = (ResourceResponse) portalControllerContext.getResponse();
        // Locale
        Locale locale = request.getLocale();

        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();
        // CMS context
        CMSServiceCtx cmsContext = new CMSServiceCtx();
        cmsContext.setPortalControllerContext(portalControllerContext);

        // Editor modules
        List<EditorModule> modules;
        try {
            modules = this.getModules(portalControllerContext);
        } catch (PortalException e) {
            throw new PortletException(e);
        }

        // Editor module
        EditorModule module = null;
        if (CollectionUtils.isNotEmpty(modules)) {
            Iterator<EditorModule> iterator = modules.iterator();
            while ((module == null) && iterator.hasNext()) {
                EditorModule next = iterator.next();
                if (StringUtils.equals(editorId, next.getId())) {
                    module = next;
                }
            }
        }

        if (module != null) {
            // Editor title
            String title = this.internationalizationService.getString(module.getKey(), locale, module.getClassLoader(), module.getApplicationContext());
            // Editor instance
            String instance = module.getInstance();

            // Editor window properties
            Map<String, String> properties;
            try {
                properties = cmsService.getEditorWindowBaseProperties(cmsContext);
            } catch (CMSException e) {
                throw new PortletException(e);
            }
            if (CollectionUtils.isNotEmpty(module.getParameters())) {
                for (String parameter : module.getParameters()) {
                    String name = WINDOW_PROPERTY_PREFIX + parameter;
                    String value = request.getParameter(parameter);
                    properties.put(name, value);
                }
            }

            // URL
            String url;
            try {
                url = this.portalUrlFactory.getStartPortletUrl(portalControllerContext, instance, properties, PortalUrlType.MODAL);
            } catch (PortalException e) {
                throw new PortletException(e);
            }

            // JSON
            JSONObject object = new JSONObject();
            object.put("title", title);
            object.put("url", url);


            // Content type
            response.setContentType("application/json");

            // Content
            PrintWriter printWriter = new PrintWriter(response.getPortletOutputStream());
            printWriter.write(object.toString());
            printWriter.close();
        }
    }


    public void setCmsServiceLocator(ICMSServiceLocator cmsServiceLocator) {
        this.cmsServiceLocator = cmsServiceLocator;
    }

    public void setPortalUrlFactory(IPortalUrlFactory portalUrlFactory) {
        this.portalUrlFactory = portalUrlFactory;
    }

    public void setInternationalizationService(IInternationalizationService internationalizationService) {
        this.internationalizationService = internationalizationService;
    }
}
