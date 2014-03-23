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

import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.RenderResponse;

import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.server.ServerInvocation;




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
    
    private String previewVersionPath;
	
     
    public String getPreviewVersionPath() {
        return previewVersionPath;
    }


    
    public void setPreviewVersionPath(String previewVersionPath) {
        this.previewVersionPath = previewVersionPath;
    }


    public String getCreationPath() {
        return creationPath;
    }


    
    public void setCreationPath(String creationPath) {
        this.creationPath = creationPath;
    }


    public String getCreationType() {
        return creationType;
    }

    
    public void setCreationType(String creationType) {
        this.creationType = creationType;
    }

    private PortletRequest request;
	private PortletContext portletCtx;	
	private RenderResponse response;
	private String pageId;
	private Object doc;
	

    private boolean streamingSupport = false;
    
    
    public boolean isStreamingSupport() {
        return streamingSupport;
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
		return forceReload;
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
	
	
	public ControllerContext getControllerContext() {
		return controllerContext;
	}

	public void setControllerContext(ControllerContext controllerContext) {
		this.controllerContext = controllerContext;
		this.serverInvocation = controllerContext.getServerInvocation();
	}
	
	
	public ServerInvocation getServerInvocation() {
		return serverInvocation;
	}

	public void setServerInvocation(ServerInvocation invocation) {
		this.serverInvocation = invocation;
	}

	public String getContextualizationBasePath() {
		return contextualizationBasePath;
	}

	public void setContextualizationBasePath(String contextualizationBasePath) {
		this.contextualizationBasePath = contextualizationBasePath;
	}

	
	public String getDisplayContext() {
		return displayContext;
	}

	public void setDisplayContext(String displayContext) {
		this.displayContext = displayContext;
	}
	public PortletContext getPortletCtx() {
		return portletCtx;
	}

	public void setPortletCtx(PortletContext portletCtx) {
		this.portletCtx = portletCtx;
	}

	
	public String getDisplayLiveVersion() {
		return displayLiveVersion;
	}

	public void setDisplayLiveVersion(String displayLiveVersion) {
		this.displayLiveVersion = displayLiveVersion;
	}

	public PortletRequest getRequest() {
		return request;
	}

	public void setRequest(PortletRequest request) {
		this.request = request;
	}

	public RenderResponse getResponse() {
		return response;
	}

	public void setResponse(RenderResponse response) {
		this.response = response;
	}

	public String getPageId() {
		return pageId;
	}

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	public Object getDoc() {
		return doc;
	}

	public void setDoc(Object doc) {
		this.doc = doc;
	}

	public String getHideMetaDatas() {
		return hideMetaDatas;
	}

	public void setHideMetaDatas(String hideMetaDatas) {
		this.hideMetaDatas = hideMetaDatas;
	}

	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getForcePublicationInfosScope() {
		return forcePublicationInfosScope;
	}

	public void setForcePublicationInfosScope(String forcePublicationInfosScope) {
		this.forcePublicationInfosScope = forcePublicationInfosScope;
	}

	public boolean isAsyncCacheRefreshing() {
		return isAsyncCacheRefreshing;
	}

	public void setAsyncCacheRefreshing(boolean isAsyncCacheRefreshing) {
		this.isAsyncCacheRefreshing = isAsyncCacheRefreshing;
	}

}
