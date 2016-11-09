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
package org.osivia.portal.api.cms.impl;

import org.osivia.portal.api.cms.DocumentState;
import org.osivia.portal.api.cms.PublicationInfos;

/**
 * Default class for publication informations on a document
 * @author Loïc Billon
 *
 */
public class BasicPublicationInfos implements PublicationInfos {


    /**
	 * property : the path of the container of the document (a workspace, a publication space)
	 */
	public static final String BASE_PATH = "osivia.cms.basePath";
	
    /**
	 * property : should display the live version
	 */
	public static final String DISPLAY_LIVE_VERSION = "osivia.cms.displayLiveVersion";
	
    /**
	 * property : should display the live version
	 */
	public static final String REQUEST_LIVE_VERSION = "displayLiveVersion";

    /**
	 * property : the current path of the navigable élément (could be the container of the current document) 
	 */
	public static final String NAVIGATION_PATH = "osivia.cms.path";
	
    /**
	 * property : the path of the document
	 */
	public static final String CONTENT_PATH = "osivia.cms.contentPath";
	
    /**
	 * property : should reload the page
	 */
	public static final String RELOAD_RESOURCE = "refresh";
	
	
	/** the path of the container of the document (a workspace, a publication space) */
	private String basePath;
	
	/** the current path of the navigable élément (could be the container of the current document)  */
	private String navigationPath;
	
	/** the path of the document */
	private String contentPath;
	
	/** if specified, the path of a live document (used to go to a document in edition mode) */
	private String forcedLivePath;	
	
	/** State of the document (live or published) */
	private DocumentState state = DocumentState.LIVE;
	
	/** flag to reload the content */
	private boolean reloadResource = false;
	
	/** ID of a document in is live state */
	private String liveId;
	
	/** true if this document is a container of live documents only */
	private boolean liveSpace = false;
	
	/** true if this document is displayed in his navigation context */
	private boolean contextualized = false;
	
	/** true if this document is displayed in his navigation context */
	private boolean draft = false;	
	
	/** ???? */
	private String scope;
	
	/** If a document should be displayed in a specific context */
	private String displayContext;

	/**
	 * @return the basePath
	 */
	public String getBasePath() {
		return basePath;
	}

	/**
	 * @param basePath the basePath to set
	 */
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	/**
	 * @return the navigationPath
	 */
	public String getNavigationPath() {
		return navigationPath;
	}

	/**
	 * @param navigationPath the navigationPath to set
	 */
	public void setNavigationPath(String navigationPath) {
		this.navigationPath = navigationPath;
	}

	/**
	 * @return the contentPath
	 */
	public String getContentPath() {
		return contentPath;
	}

	/**
	 * @param contentPath the contentPath to set
	 */
	public void setContentPath(String contentPath) {
		this.contentPath = contentPath;
	}

	
	
	/**
	 * @return the forcedLivePath
	 */
	public String getForcedLivePath() {
		return forcedLivePath;
	}

	/**
	 * @param forcedLivePath the forcedLivePath to set
	 */
	public void setForcedLivePath(String forcedLivePath) {
		this.forcedLivePath = forcedLivePath;
	}

	/**
	 * @return the state
	 */
	public DocumentState getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(DocumentState state) {
		this.state = state;
	}

	/**
	 * @return the reloadResource
	 */
	public boolean isReloadResource() {
		return reloadResource;
	}

	/**
	 * @param reloadResource the reloadResource to set
	 */
	public void setReloadResource(boolean reloadResource) {
		this.reloadResource = reloadResource;
	}

	/**
	 * @return the liveId
	 */
	public String getLiveId() {
		return liveId;
	}

	/**
	 * @param liveId the liveId to set
	 */
	public void setLiveId(String liveId) {
		this.liveId = liveId;
	}
	
	

	/**
	 * @return the liveSpace
	 */
	public boolean isLiveSpace() {
		return liveSpace;
	}

	/**
	 * @param liveSpace the liveSpace to set
	 */
	public void setLiveSpace(boolean liveSpace) {
		this.liveSpace = liveSpace;
	}

	/**
	 * @return the contextualized
	 */
	public boolean isContextualized() {
		return contextualized;
	}

	/**
	 * @param contextualized the contextualized to set
	 */
	public void setContextualized(boolean contextualized) {
		this.contextualized = contextualized;
	}

	/**
	 * @return the scope
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * @param scope the scope to set
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}

	/**
	 * @return the displayContext
	 */
	public String getDisplayContext() {
		return displayContext;
	}

	/**
	 * @param displayContext the displayContext to set
	 */
	public void setDisplayContext(String displayContext) {
		this.displayContext = displayContext;
	}

	/**
	 * @return the draft
	 */
	public boolean isDraft() {
		return draft;
	}

	/**
	 * @param draft the draft to set
	 */
	public void setDraft(boolean draft) {
		this.draft = draft;
	}

	
	
	
}
