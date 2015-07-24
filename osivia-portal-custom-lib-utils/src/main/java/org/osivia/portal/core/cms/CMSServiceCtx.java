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
package org.osivia.portal.core.cms;

import javax.portlet.MimeResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.core.context.ControllerContextAdapter;




public class CMSServiceCtx {


	private ControllerContext controllerContext;
	private ServerInvocation serverInvocation;
	private String scope;
	/**
	 * Variable indiquant si le résultat de la commande
	 * effectuée avec ce contexte doit être mise à jour
	 * en cache de façon asynchrone.
	 */
	private boolean isAsyncCacheRefreshing = false;
	private String displayLiveVersion;
	private String hideMetaDatas;
	private String displayContext;
	private String contextualizationBasePath;

    private String creationType;
    private String creationPath;

    private String forcedLivePath;

    /** The referrer base path. */
    private String cmsReferrerNavigationPath;
    /** The parent identifier (webid). */
    private String parentId;
    /** The parent path. */
    private String parentPath;
    
    
    public String getCmsReferrerNavigationPath() {
        return this.cmsReferrerNavigationPath;
    }

    public void setCmsReferrerNavigationPath(String cmsReferrerNavigationPath) {
        this.cmsReferrerNavigationPath = cmsReferrerNavigationPath;
    }

    /**
     * @return the parentId
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * @param parentId the parentId to set
     */
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
    
    /**
     * @return the parentPath
     */
    public String getParentPath() {
        return parentPath;
    }

    /**
     * @param parentPath the parentPath to set
     */
    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public String getForcedLivePath() {
        return this.forcedLivePath;
    }

    public void setForcedLivePath(String forcedLivePath) {
        this.forcedLivePath = forcedLivePath;
    }


    public String getCreationPath() {
        return this.creationPath;
    }



    public void setCreationPath(String creationPath) {
        this.creationPath = creationPath;
    }


    public String getCreationType() {
        return this.creationType;
    }


    public void setCreationType(String creationType) {
        this.creationType = creationType;
    }

    private PortletRequest request;
    private HttpServletRequest servletRequest;

    public HttpServletRequest getServletRequest() {
        return this.servletRequest;
    }




    public void setServletRequest(HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
    }

    private PortletContext portletCtx;
    private MimeResponse response;
	private String pageId;
	private Object doc;


    private boolean streamingSupport = false;


    public boolean isStreamingSupport() {
        return this.streamingSupport;
    }


    public void setStreamingSupport(boolean streamingSupport) {
        this.streamingSupport = streamingSupport;
    }

	/** if 'true', indicate to don't access the cache. load the latest data */
	private boolean forceReload = false;

	/**
	 * @return the forceReload
	 */
	public boolean isForceReload() {
		return this.forceReload;
	}

	/**
	 * @param forceReload the forceReload to set
	 */
	public void setForceReload(boolean forceReload) {
		this.forceReload = forceReload;
	}

	/**
	 * Variable permettant de forcer le scope
	 * de mise en cache de l'objet de retour
	 * de la méthode getPublicationInfos (dans CMSService)
	 */
	private String forcePublicationInfosScope;


    /**
     * Set portal controller context to initialized controller context and server invocation.
     *
     * @param portalControllerContext portal controller context
     */
    public void setPortalControllerContext(PortalControllerContext portalControllerContext) {
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);
        this.setControllerContext(controllerContext);
    }


	public ControllerContext getControllerContext() {
		return this.controllerContext;
	}


	public void setControllerContext(ControllerContext controllerContext) {
		this.controllerContext = controllerContext;
		this.serverInvocation = controllerContext.getServerInvocation();
	}



	public ServerInvocation getServerInvocation() {
		return this.serverInvocation;
	}

	public void setServerInvocation(ServerInvocation invocation) {
		this.serverInvocation = invocation;
	}

	public String getContextualizationBasePath() {
		return this.contextualizationBasePath;
	}

	public void setContextualizationBasePath(String contextualizationBasePath) {
		this.contextualizationBasePath = contextualizationBasePath;
	}


	public String getDisplayContext() {
		return this.displayContext;
	}

	public void setDisplayContext(String displayContext) {
		this.displayContext = displayContext;
	}
	public PortletContext getPortletCtx() {
		return this.portletCtx;
	}

	public void setPortletCtx(PortletContext portletCtx) {
		this.portletCtx = portletCtx;
	}


	public String getDisplayLiveVersion() {
		return this.displayLiveVersion;
	}

	public void setDisplayLiveVersion(String displayLiveVersion) {
		this.displayLiveVersion = displayLiveVersion;
	}

	public PortletRequest getRequest() {
		return this.request;
	}

	public void setRequest(PortletRequest request) {
		this.request = request;
	}

    public MimeResponse getResponse() {
		return this.response;
	}

    public void setResponse(MimeResponse response) {
		this.response = response;
	}

	public String getPageId() {
		return this.pageId;
	}

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	public Object getDoc() {
		return this.doc;
	}

	public void setDoc(Object doc) {
		this.doc = doc;
	}

	public String getHideMetaDatas() {
		return this.hideMetaDatas;
	}

	public void setHideMetaDatas(String hideMetaDatas) {
		this.hideMetaDatas = hideMetaDatas;
	}

	public String getScope() {
		return this.scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getForcePublicationInfosScope() {
		return this.forcePublicationInfosScope;
	}

	public void setForcePublicationInfosScope(String forcePublicationInfosScope) {
		this.forcePublicationInfosScope = forcePublicationInfosScope;
	}

	public boolean isAsyncCacheRefreshing() {
		return this.isAsyncCacheRefreshing;
	}

	public void setAsyncCacheRefreshing(boolean isAsyncCacheRefreshing) {
		this.isAsyncCacheRefreshing = isAsyncCacheRefreshing;
	}

}
