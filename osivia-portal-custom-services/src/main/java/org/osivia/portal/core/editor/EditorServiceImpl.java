package org.osivia.portal.core.editor;

import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerContext;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.editor.EditorModule;
import org.osivia.portal.api.editor.EditorModuleResource;
import org.osivia.portal.api.editor.EditorService;
import org.osivia.portal.api.editor.EditorTemporaryAttachedPicture;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.urls.PortalUrlType;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.context.ControllerContextAdapter;

import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.function.Consumer;

/**
 * Editor service implementation.
 *
 * @author Cédric Krommenhoek
 * @see EditorService
 */
public class EditorServiceImpl implements EditorService {

    /**
     * Temporary attached pictures attribute.
     */
    public static final String TEMPORARY_ATTACHED_PICTURES_ATTRIBUTE = "osivia.editor.temporary-attached-pictures";


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
            // Editor resource identifier
            String resourceId = request.getParameter("editorResourceId");
            EditorModuleResource resource;
            if (StringUtils.isEmpty(resourceId) || MapUtils.isEmpty(module.getResources())) {
                resource = null;
            } else {
                resource = module.getResources().get(resourceId);
            }

            if (resource == null) {
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
                if (BooleanUtils.toBoolean(request.getParameter("creation"))) {
                    properties.put(WINDOW_PROPERTY_PREFIX + "creation", String.valueOf(true));
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
            } else {
                resource.serve(portalControllerContext);
            }
        }
    }


    @Override
    public List<EditorTemporaryAttachedPicture> getTemporaryAttachedPictures(PortalControllerContext portalControllerContext, String path) {
        List<EditorTemporaryAttachedPicture> pictures;

        if (StringUtils.isEmpty(path)) {
            pictures = null;
        } else {
            // Temporary attached picture container
            EditorTemporaryAttachedPictureContainer container = this.getTemporaryAttachedPictureContainer(portalControllerContext);

            pictures = container.getMap().computeIfAbsent(path, k -> new ArrayList<>());
        }

        return pictures;
    }


    /**
     * Get temporary attached picture container.
     *
     * @param portalControllerContext portal controller context
     * @return container
     */
    private EditorTemporaryAttachedPictureContainer getTemporaryAttachedPictureContainer(PortalControllerContext portalControllerContext) {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        // Temporary attached picture container
        EditorTemporaryAttachedPictureContainer container;
        Object attribute = controllerContext.getAttribute(Scope.PRINCIPAL_SCOPE, TEMPORARY_ATTACHED_PICTURES_ATTRIBUTE);
        if (attribute instanceof EditorTemporaryAttachedPictureContainer) {
            container = (EditorTemporaryAttachedPictureContainer) attribute;
        } else {
            container = new EditorTemporaryAttachedPictureContainer();
            controllerContext.setAttribute(Scope.PRINCIPAL_SCOPE, TEMPORARY_ATTACHED_PICTURES_ATTRIBUTE, container);
        }
        return container;
    }


    @Override
    public void addTemporaryAttachedPicture(PortalControllerContext portalControllerContext, String path, EditorTemporaryAttachedPicture picture) {
        // Temporary attached pictures
        List<EditorTemporaryAttachedPicture> pictures = this.getTemporaryAttachedPictures(portalControllerContext, path);

        pictures.add(picture);
    }


    @Override
    public void clearTemporaryAttachedPictures(PortalControllerContext portalControllerContext, String path) {
        if (StringUtils.isNotEmpty(path)) {
            // Temporary attached picture container
            EditorTemporaryAttachedPictureContainer container = this.getTemporaryAttachedPictureContainer(portalControllerContext);

            List<EditorTemporaryAttachedPicture> pictures = container.getMap().get(path);
            if (CollectionUtils.isNotEmpty(pictures)) {
                pictures.stream().map(EditorTemporaryAttachedPicture::getFile).filter(Objects::nonNull).filter(file -> !file.delete()).forEach(File::deleteOnExit);

                container.getMap().remove(path);
            }
        }
    }


    @Override
    public void clearAllTemporaryAttachedPictures(HttpSession httpSession) throws PortalException {
        // Current user
        String user = (String) httpSession.getAttribute("PRINCIPAL_TOKEN");
        // Attribute name
        String name = "portal.principal" + user + TEMPORARY_ATTACHED_PICTURES_ATTRIBUTE;
        // Attribute
        Object attribute = httpSession.getAttribute(name);

        if (attribute instanceof EditorTemporaryAttachedPictureContainer) {
            // Temporary attached picture container
            EditorTemporaryAttachedPictureContainer container = (EditorTemporaryAttachedPictureContainer) attribute;

            Consumer<List<EditorTemporaryAttachedPicture>> consumer = list -> list.stream().map(EditorTemporaryAttachedPicture::getFile).filter(Objects::nonNull).filter(file -> !file.delete()).forEach(File::deleteOnExit);
            container.getMap().values().stream().filter(CollectionUtils::isNotEmpty).forEach(consumer);
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
