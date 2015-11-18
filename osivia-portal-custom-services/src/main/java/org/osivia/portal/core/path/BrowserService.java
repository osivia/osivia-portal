package org.osivia.portal.core.path;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.theme.impl.render.dynamic.json.JSONArray;
import org.jboss.portal.theme.impl.render.dynamic.json.JSONException;
import org.jboss.portal.theme.impl.render.dynamic.json.JSONObject;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.DocumentType;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.path.IBrowserService;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSObjectPath;
import org.osivia.portal.core.cms.CMSPublicationInfos;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.page.PageCustomizerInterceptor;

/**
 * Documents browser service implementation.
 *
 * @author Cédric Krommenhoek
 * @see IBrowserService
 */
public class BrowserService implements IBrowserService {

    /** Portal URL factory. */
    private IPortalUrlFactory portalURLFactory;
    /** CMS service locator. */
    private ICMSServiceLocator cmsServiceLocator;


    /**
     * Constructor.
     */
    public BrowserService() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public String browse(PortalControllerContext portalControllerContext) throws PortalException {
        try {
            // Browser options
            BrowserOptions options = new BrowserOptions(portalControllerContext);

            // JSON array
            JSONArray jsonArray;

            if (options.isWorkspaces()) {
                jsonArray = this.generateWorkspacesJSONArray(portalControllerContext, options);
            } else {
                jsonArray = this.generateLazyJSONArray(portalControllerContext, options);
            }

            return jsonArray.toString();
        } catch (CMSException e) {
            throw new PortalException(e);
        } catch (JSONException e) {
            throw new PortalException(e);
        }
    }


    /**
     * Generate workspaces JSON array.
     *
     * @param portalControllerContext portal controller context
     * @param options browser options
     * @return JSON array
     * @throws CMSException
     * @throws JSONException
     */
    private JSONArray generateWorkspacesJSONArray(PortalControllerContext portalControllerContext, BrowserOptions options) throws CMSException, JSONException {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);
        // CMS context
        CMSServiceCtx cmsContext = this.getCMSContext(portalControllerContext, options);
        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();

        // Administrator indicator
        boolean administrator = PageCustomizerInterceptor.isAdministrator(controllerContext);


        // JSON array
        JSONArray jsonArray = new JSONArray();

        // User workspaces
        List<CMSItem> userWorkspaces = cmsService.getWorkspaces(cmsContext, true, administrator);
        for (CMSItem userWorkspace : userWorkspaces) {
            JSONObject jsonObject = this.generateJSONObject(portalControllerContext, userWorkspace, true, options);
            jsonArray.put(jsonObject);
        }

        // Workspaces
        List<CMSItem> workspaces = cmsService.getWorkspaces(cmsContext, false, administrator);
        SortedSet<BrowserWorkspaceDomain> domains = this.getSortedWorkspacesDomains(portalControllerContext, workspaces);

        for (BrowserWorkspaceDomain domain : domains) {
            JSONObject jsonObject = this.generateJSONObject(portalControllerContext, domain);
            jsonArray.put(jsonObject);
        }

        return jsonArray;
    }


    /**
     * Get sorted workspaces domains.
     *
     * @param portalControllerContext portal controller context
     * @param workspaces workspaces
     * @return domains
     * @throws CMSException
     */
    private SortedSet<BrowserWorkspaceDomain> getSortedWorkspacesDomains(PortalControllerContext portalControllerContext, List<CMSItem> workspaces)
            throws CMSException {
        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();
        // CMS context
        CMSServiceCtx cmsContext = new CMSServiceCtx();
        cmsContext.setPortalControllerContext(portalControllerContext);
        cmsContext.setForcePublicationInfosScope("superuser_context");

        // Workpace glyph
        String glyph = null;
        if (!workspaces.isEmpty()) {
            CMSItem workspace = workspaces.get(0);
            DocumentType type = workspace.getType();
            if (type != null) {
                glyph = type.getGlyph();
            }
        }


        // Workspaces paths
        Map<String, BrowserWorkspace> workspacesPaths = new HashMap<String, BrowserWorkspace>(workspaces.size());
        // Domains paths
        Map<String, BrowserWorkspaceDomain> domainsPaths = new HashMap<String, BrowserWorkspaceDomain>();

        for (CMSItem workspaceCMSItem : workspaces) {
            String workspacePath = workspaceCMSItem.getPath();
            String domainPath = "/" + StringUtils.split(workspacePath, "/")[0];

            // Workspace
            BrowserWorkspace workspace = new BrowserWorkspace();
            workspace.setTitle(workspaceCMSItem.getProperties().get("displayName"));
            workspace.setPath(workspaceCMSItem.getPath());
            workspace.setGlyph(glyph);
            workspacesPaths.put(workspacePath, workspace);

            // Domain
            if (!domainsPaths.containsKey(domainPath)) {
                CMSItem domainCMSItem = cmsService.getContent(cmsContext, domainPath);

                BrowserWorkspaceDomain domain = new BrowserWorkspaceDomain();
                domain.setTitle(domainCMSItem.getProperties().get("displayName"));

                domainsPaths.put(domainPath, domain);
            }
        }


        // Workspace parent association
        for (Entry<String, BrowserWorkspace> workspaceEntry : workspacesPaths.entrySet()) {
            String workspacePath = workspaceEntry.getKey();
            BrowserWorkspace workspace = workspaceEntry.getValue();

            // Find parent
            BrowserWorkspaceObject parent = null;
            String parentPath = StringUtils.substringBeforeLast(workspacePath, "/");
            while (StringUtils.contains(parentPath, "/") && (parent == null)) {
                if (StringUtils.countMatches(parentPath, "/") > 1) {
                    // Search workspace
                    parent = workspacesPaths.get(parentPath);
                } else {
                    // Search domain
                    parent = domainsPaths.get(parentPath);
                }

                parentPath = StringUtils.substringBeforeLast(parentPath, "/");
            }

            if (parent != null) {
                parent.getChildren().add(workspace);
            }
        }


        // Sorted domains
        SortedSet<BrowserWorkspaceDomain> domains = new TreeSet<BrowserWorkspaceDomain>();
        domains.addAll(domainsPaths.values());

        return domains;
    }





    /**
     * Generate lazy JSON array.
     *
     * @param portalControllerContext portal controller context
     * @param options browser options
     * @return JSON array
     * @throws CMSException
     * @throws JSONException
     */
    private JSONArray generateLazyJSONArray(PortalControllerContext portalControllerContext, BrowserOptions options) throws CMSException, JSONException {
        // JSON objects
        JSONArray jsonArray = new JSONArray();
        JSONArray childrenJSONArray;

        // Parent path
        String parentPath;

        if (options.getPath() == null) {
            // Root
            CMSItem cmsItem = this.getBaseCMSItem(portalControllerContext, options);

            JSONObject jsonObject = this.generateJSONObject(portalControllerContext, cmsItem, true, options);
            childrenJSONArray = new JSONArray();
            jsonObject.put("children", childrenJSONArray);
            jsonArray.put(jsonObject);

            parentPath = cmsItem.getPath();
        } else {
            childrenJSONArray = jsonArray;

            parentPath = options.getPath();
        }

        // Children lazy loading nodes
        List<CMSItem> cmsSubItems = this.getChildrenCMSItem(portalControllerContext, options, parentPath);
        if (cmsSubItems != null) {
            for (CMSItem cmsSubItem : cmsSubItems) {
                // Add child indicator
                boolean add = this.isAcceptable(cmsSubItem, options);
                if (!add) {
                    DocumentType type = cmsSubItem.getType();
                    add = ((type != null) && type.isFolderish());
                }

                if (add) {
                    childrenJSONArray.put(this.generateJSONObject(portalControllerContext, cmsSubItem, false, options));
                }
            }
        }

        return jsonArray;
    }


    /**
     * Get base CMS item.
     *
     * @param portalControllerContext portal controller context
     * @param options browser options
     * @return CMS item
     * @throws CMSException
     */
    private CMSItem getBaseCMSItem(PortalControllerContext portalControllerContext, BrowserOptions options) throws CMSException {
        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();

        // CMS context
        CMSServiceCtx cmsContext = this.getCMSContext(portalControllerContext, options);

        return cmsService.getContent(cmsContext, options.getCmsBasePath());
    }


    /**
     * Get children CMS items.
     *
     * @param portalControllerContext portal controller context
     * @param options browser options
     * @param parentPath parent path
     * @return CMS items
     * @throws CMSException
     */
    private List<CMSItem> getChildrenCMSItem(PortalControllerContext portalControllerContext, BrowserOptions options, String parentPath) throws CMSException {
        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();

        // CMS context
        CMSServiceCtx cmsContext = this.getCMSContext(portalControllerContext, options);

        return cmsService.getPortalSubitems(cmsContext, parentPath);
    }


    /**
     * Generate JSON object.
     *
     * @param portalControllerContext portal controller context
     * @param cmsItem CMS item
     * @param root root indicator
     * @param options browser options
     * @return JSON object
     * @throws CMSException
     * @throws JSONException
     */
    private JSONObject generateJSONObject(PortalControllerContext portalControllerContext, CMSItem cmsItem, boolean root, BrowserOptions options)
            throws CMSException, JSONException {
        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();
        // CMS context
        CMSServiceCtx cmsContext = this.getCMSContext(portalControllerContext, options);
        // Publication infos
        CMSPublicationInfos publicationInfos = cmsService.getPublicationInfos(cmsContext, cmsItem.getPath());
        // Path
        String path = publicationInfos.getDocumentPath();

        // CMS item type
        DocumentType type = cmsItem.getType();
        boolean browsable = false;
        String glyph = null;
        if (type != null) {
            browsable = type.isBrowsable();
            glyph = type.getGlyph();
        }

        // Acceptable indicator
        boolean acceptable = this.isAcceptable(cmsItem, options);

        if (acceptable && (options.getAcceptedTypes() != null) && (options.getIgnoredPaths() != null)) {
            String currentPath = cmsItem.getPath();
            Set<String> ignoredPaths = options.getIgnoredPaths();
            for (String ignoredPath : ignoredPaths) {
                String ignoredParentPath = CMSObjectPath.parse(ignoredPath).getParent().toString();

                if (StringUtils.startsWith(currentPath, ignoredPath) || StringUtils.equals(currentPath, ignoredParentPath)) {
                    acceptable = false;
                    break;
                }
            }
        }

        // Expanded indicator
        boolean expanded = root;
        if (!expanded && (options.getCmsNavigationPath() != null)) {
            expanded = StringUtils.startsWith(options.getCmsNavigationPath() + "/", path + "/");
        }

        // URL
        String url = null;
        if (options.isLink()) {
            String displayLiveVersion = BooleanUtils.toString(options.isLive(), "1", null);
            url = this.portalURLFactory.getCMSUrl(portalControllerContext, null, cmsItem.getPath(), null, null, options.getDisplayContext(), null, null,
                    displayLiveVersion, null);
            if (options.isPopup()) {
                url = this.portalURLFactory.adaptPortalUrlToPopup(portalControllerContext, url, IPortalUrlFactory.POPUP_URL_ADAPTER_CLOSE);
            }
        }


        JSONObject object = new JSONObject();

        // Title
        object.put("title", cmsItem.getProperties().get("displayName"));

        // Folder indicator
        object.put("folder", browsable);

        // Lazy indicator
        object.put("lazy", !expanded && browsable);

        // Expanded indicator
        object.put("expanded", expanded);

        // Path
        object.put("path", cmsItem.getPath());

        // URL
        if (url != null) {
            object.put("href", url);
        }

        // Acceptable
        object.put("acceptable", acceptable);

        // Icon
        if ((glyph != null) && (!glyph.contains("folder"))) {
            object.put("iconclass", glyph);
        }

        // Extra-classes
        StringBuilder extraClasses = new StringBuilder();
        if (!acceptable) {
            extraClasses.append("text-muted not-allowed ");
        } else if (options.isHighlight() && (BooleanUtils.isFalse(cmsItem.getPublished()) || BooleanUtils.isTrue(cmsItem.getBeingModified()))) {
            extraClasses.append("text-warning ");
        }
        if (StringUtils.equals(options.getCmsNavigationPath(), path)) {
            extraClasses.append("current ");
        }

        object.put("extraClasses", extraClasses.toString());

        // Children
        if (!root && expanded) {
            BrowserOptions childrenOptions = new BrowserOptions(options, path);
            JSONArray childrenJSONArray = this.generateLazyJSONArray(portalControllerContext, childrenOptions);
            object.put("children", childrenJSONArray);
        }

        return object;
    }


    /**
     * Generate JSON object.
     *
     * @param portalControllerContext portal controller context
     * @param workspaceObject workspace object
     * @return JSON object
     * @throws JSONException
     */
    private JSONObject generateJSONObject(PortalControllerContext portalControllerContext, BrowserWorkspaceObject workspaceObject) throws JSONException {
        JSONObject object = new JSONObject();

        // Title
        object.put("title", workspaceObject.getTitle());

        // Folder indicator
        object.put("folder", true);

        // Expanded indicator
        object.put("expanded", true);

        // Icon
        if (workspaceObject.getGlyph() != null) {
            object.put("iconclass", workspaceObject.getGlyph());
        }

        if (workspaceObject instanceof BrowserWorkspace) {
            BrowserWorkspace workspace = (BrowserWorkspace) workspaceObject;

            // Path
            object.put("path", workspace.getPath());

            // Acceptable
            object.put("acceptable", true);
        } else if (workspaceObject instanceof BrowserWorkspaceDomain) {
            // Acceptable
            object.put("acceptable", false);

            // Extra-classes
            object.put("extraClasses", "text-muted");
        }

        // Children
        if (!workspaceObject.getChildren().isEmpty()) {
            JSONArray childrenArray = new JSONArray();
            for (BrowserWorkspace child : workspaceObject.getChildren()) {
                JSONObject childObject = this.generateJSONObject(portalControllerContext, child);
                childrenArray.put(childObject);
            }
            object.put("children", childrenArray);
        }

        return object;
    }


    /**
     * Check if a CMS item is acceptable.
     *
     * @param cmsItem CMS item
     * @param options browser options
     * @return true if acceptable
     */
    private boolean isAcceptable(CMSItem cmsItem, BrowserOptions options) {
        DocumentType type = cmsItem.getType();

        // Accepted child
        boolean acceptedChild;
        if (options.getAcceptedTypes() == null) {
            acceptedChild = true;
        } else if (type == null) {
            acceptedChild = false;
        } else {
            acceptedChild = CollectionUtils.containsAny(type.getPortalFormSubTypes(), options.getAcceptedTypes());
        }

        // Included child
        boolean includedChild;
        if (options.getIncludedTypes() == null) {
            includedChild = true;
        } else if (type == null) {
            includedChild = false;
        } else {
            includedChild = options.getIncludedTypes().contains(type.getName());
        }

        // Excluded child
        boolean excludedChild;
        if ((options.getExcludedTypes() == null) || (type == null)) {
            excludedChild = false;
        } else {
            excludedChild = options.getExcludedTypes().contains(type.getName());
        }

        return acceptedChild && includedChild && !excludedChild;
    }


    /**
     * Get CMS context.
     *
     * @param portalControllerContext portal controller context
     * @param options browser options
     * @return CMS context
     */
    private CMSServiceCtx getCMSContext(PortalControllerContext portalControllerContext, BrowserOptions options) {
        CMSServiceCtx cmsContext = new CMSServiceCtx();
        cmsContext.setPortalControllerContext(portalControllerContext);
        if (options.isLive()) {
            cmsContext.setDisplayLiveVersion("1");
        }
        cmsContext.setForceReload(options.isForceReload());
        return cmsContext;
    }


    /**
     * Setter for portalURLFactory.
     *
     * @param portalURLFactory the portalURLFactory to set
     */
    public void setPortalURLFactory(IPortalUrlFactory portalURLFactory) {
        this.portalURLFactory = portalURLFactory;
    }

    /**
     * Setter for cmsServiceLocator.
     *
     * @param cmsServiceLocator the cmsServiceLocator to set
     */
    public void setCmsServiceLocator(ICMSServiceLocator cmsServiceLocator) {
        this.cmsServiceLocator = cmsServiceLocator;
    }

}
