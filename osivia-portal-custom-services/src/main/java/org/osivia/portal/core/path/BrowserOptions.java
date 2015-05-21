package org.osivia.portal.core.path;

import javax.portlet.PortletRequest;

import org.apache.commons.lang.BooleanUtils;
import org.osivia.portal.api.context.PortalControllerContext;

/**
 * Browser options java-bean.
 * 
 * @author Cédric Krommenhoek
 */
public class BrowserOptions {

    /** Current path. */
    private final String path;
    /** Current document path, required for move. */
    private final String documentPath;
    /** CMS base path. */
    private final String cmsBasePath;
    /** Live indicator. */
    private final boolean live;
    /** Accepted type, required for move. */
    private final String acceptedType;
    /** Workspaces indicator. */
    private final boolean workspaces;
    /** Link indicator. */
    private final boolean link;
    /** Link display context. */
    private final String displayContext;
    /** Popup indicator. */
    private final boolean popup;


    /**
     * Constructor.
     * 
     * @param portalControllerContext portal controller context
     */
    public BrowserOptions(PortalControllerContext portalControllerContext) {
        // Request
        PortletRequest request = portalControllerContext.getRequest();

        // Request parameters
        this.path = request.getParameter("path");
        this.documentPath = request.getParameter("documentPath");
        this.cmsBasePath = request.getParameter("cmsBasePath");
        this.live = BooleanUtils.toBoolean(request.getParameter("live"));
        this.acceptedType = request.getParameter("acceptedType");
        this.workspaces = BooleanUtils.toBoolean(request.getParameter("workspaces"));
        this.link = BooleanUtils.toBoolean(request.getParameter("link"));
        this.displayContext = request.getParameter("displayContext");
        this.popup = BooleanUtils.toBoolean(request.getParameter("popup"));
    }


    /**
     * Getter for path.
     * 
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * Getter for documentPath.
     * 
     * @return the documentPath
     */
    public String getDocumentPath() {
        return documentPath;
    }

    /**
     * Getter for cmsBasePath.
     * 
     * @return the cmsBasePath
     */
    public String getCmsBasePath() {
        return cmsBasePath;
    }

    /**
     * Getter for live.
     * 
     * @return the live
     */
    public boolean isLive() {
        return live;
    }

    /**
     * Getter for acceptedType.
     * 
     * @return the acceptedType
     */
    public String getAcceptedType() {
        return acceptedType;
    }

    /**
     * Getter for workspaces.
     * 
     * @return the workspaces
     */
    public boolean isWorkspaces() {
        return workspaces;
    }

    /**
     * Getter for link.
     * 
     * @return the link
     */
    public boolean isLink() {
        return link;
    }

    /**
     * Getter for displayContext.
     * 
     * @return the displayContext
     */
    public String getDisplayContext() {
        return displayContext;
    }

    /**
     * Getter for popup.
     * 
     * @return the popup
     */
    public boolean isPopup() {
        return popup;
    }

}
