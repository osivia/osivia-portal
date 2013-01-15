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
	private String displayLiveVersion;
	private String hideMetaDatas;
	private String displayContext;
	private String contextualizationBasePath; 

	private PortletRequest request;
	private PortletContext portletCtx;	
	private RenderResponse response;
	private String pageId;
	private Object doc;
	
	private boolean forceScopeToAnonymous = false;
	
	
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

	public boolean isForceScopeToAnonymous() {
		return forceScopeToAnonymous;
	}

	public void setForceScopeToAnonymous(boolean forceScopeToAnonymous) {
		this.forceScopeToAnonymous = forceScopeToAnonymous;
	}


}
