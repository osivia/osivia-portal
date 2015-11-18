package org.osivia.portal.core.path;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.portlet.PortletRequest;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.osivia.portal.api.context.PortalControllerContext;

/**
 * Browser options java-bean.
 *
 * @author Cédric Krommenhoek
 */
public class BrowserOptions {

    /** Requested path, used for lazy loading resource request ; DO NOT SET IT. */
    private final String path;
    /** CMS base path. */
    private final String cmsBasePath;
    /** CMS navigation path. */
    private final String cmsNavigationPath;
    /** Ignored paths. */
    private final Set<String> ignoredPaths;
    /** Live indicator. */
    private final boolean live;
    /** Accepted types, required for move. */
    private final Set<String> acceptedTypes;
    /** Included types, all by default. */
    private final Set<String> includedTypes;
    /** Excluded types, neither by default. */
    private final Set<String> excludedTypes;
    /** Workspaces indicator. */
    private final boolean workspaces;
    /** Link indicator. */
    private final boolean link;
    /** Link display context. */
    private final String displayContext;
    /** Force reload indicator. */
    private final boolean forceReload;
    /** Popup indicator. */
    private final boolean popup;
    /** Highlight indicator. */
    private final boolean highlight;


    /**
     * Constructor.
     *
     * @param portalControllerContext portal controller context
     */
    public BrowserOptions(PortalControllerContext portalControllerContext) {
        super();

        // Request
        PortletRequest request = portalControllerContext.getRequest();

        // Request parameters
        this.path = request.getParameter("path");
        this.cmsBasePath = request.getParameter("cmsBasePath");
        this.cmsNavigationPath = request.getParameter("cmsNavigationPath");
        this.live = BooleanUtils.toBoolean(request.getParameter("live"));
        this.workspaces = BooleanUtils.toBoolean(request.getParameter("workspaces"));
        this.link = BooleanUtils.toBoolean(request.getParameter("link"));
        this.displayContext = request.getParameter("displayContext");
        this.forceReload = BooleanUtils.toBoolean(request.getParameter("forceReload"));
        this.popup = BooleanUtils.toBoolean(request.getParameter("popup"));
        this.highlight = BooleanUtils.toBoolean(request.getParameter("highlight"));

        // Ignored paths
        String[] ignoredPaths = StringUtils.split(request.getParameter("ignoredPaths"), ",");
        if (ignoredPaths == null) {
            this.ignoredPaths = null;
        } else {
            this.ignoredPaths = new HashSet<String>(Arrays.asList(ignoredPaths));
        }

        // Accepted types
        String[] acceptedTypes = StringUtils.split(request.getParameter("acceptedTypes"), ",");
        if (acceptedTypes == null) {
            this.acceptedTypes = null;
        } else {
            this.acceptedTypes = new HashSet<String>(Arrays.asList(acceptedTypes));
        }

        // Included types
        String[] includedTypes = StringUtils.split(request.getParameter("includedTypes"), ",");
        if (includedTypes == null) {
            this.includedTypes = null;
        } else {
            this.includedTypes = new HashSet<String>(Arrays.asList(includedTypes));
        }

        // Excluded types
        String[] excludedTypes = StringUtils.split(request.getParameter("excludedTypes"), ",");
        if (excludedTypes == null) {
            this.excludedTypes = null;
        } else {
            this.excludedTypes = new HashSet<String>(Arrays.asList(excludedTypes));
        }
    }


    /**
     * Clone constructor.
     *
     * @param options original browser options
     * @param path new requested path
     */
    public BrowserOptions(BrowserOptions options, String path) {
        super();

        this.path = path;
        this.cmsBasePath = options.cmsBasePath;
        this.cmsNavigationPath = options.cmsNavigationPath;
        this.ignoredPaths = options.ignoredPaths;
        this.live = options.live;
        this.acceptedTypes = options.acceptedTypes;
        this.includedTypes = options.includedTypes;
        this.excludedTypes = options.excludedTypes;
        this.workspaces = options.workspaces;
        this.link = options.link;
        this.displayContext = options.displayContext;
        this.forceReload = options.forceReload;
        this.popup = options.popup;
        this.highlight = options.highlight;
    }


    /**
     * Getter for path.
     *
     * @return the path
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Getter for cmsBasePath.
     *
     * @return the cmsBasePath
     */
    public String getCmsBasePath() {
        return this.cmsBasePath;
    }

    /**
     * Getter for cmsNavigationPath.
     *
     * @return the cmsNavigationPath
     */
    public String getCmsNavigationPath() {
        return this.cmsNavigationPath;
    }

    /**
     * Getter for ignoredPaths.
     *
     * @return the ignoredPaths
     */
    public Set<String> getIgnoredPaths() {
        return this.ignoredPaths;
    }

    /**
     * Getter for live.
     *
     * @return the live
     */
    public boolean isLive() {
        return this.live;
    }

    /**
     * Getter for acceptedTypes.
     *
     * @return the acceptedTypes
     */
    public Set<String> getAcceptedTypes() {
        return this.acceptedTypes;
    }

    /**
     * Getter for includedTypes.
     *
     * @return the includedTypes
     */
    public Set<String> getIncludedTypes() {
        return this.includedTypes;
    }

    /**
     * Getter for excludedTypes.
     *
     * @return the excludedTypes
     */
    public Set<String> getExcludedTypes() {
        return this.excludedTypes;
    }

    /**
     * Getter for workspaces.
     *
     * @return the workspaces
     */
    public boolean isWorkspaces() {
        return this.workspaces;
    }

    /**
     * Getter for link.
     *
     * @return the link
     */
    public boolean isLink() {
        return this.link;
    }

    /**
     * Getter for displayContext.
     *
     * @return the displayContext
     */
    public String getDisplayContext() {
        return this.displayContext;
    }

    /**
     * Getter for forceReload.
     *
     * @return the forceReload
     */
    public boolean isForceReload() {
        return this.forceReload;
    }

    /**
     * Getter for popup.
     *
     * @return the popup
     */
    public boolean isPopup() {
        return this.popup;
    }

    /**
     * Getter for highlight.
     *
     * @return the highlight
     */
    public boolean isHighlight() {
        return this.highlight;
    }

}
