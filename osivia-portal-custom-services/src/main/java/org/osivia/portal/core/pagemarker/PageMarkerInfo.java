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
package org.osivia.portal.core.pagemarker;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.osivia.portal.api.notifications.Notifications;
import org.osivia.portal.api.selection.SelectionItem;
import org.osivia.portal.api.theming.Breadcrumb;
import org.osivia.portal.api.theming.UserPortal;
import org.osivia.portal.core.dynamic.DynamicPageBean;
import org.osivia.portal.core.dynamic.DynamicWindowBean;
import org.osivia.portal.core.selection.SelectionMapIdentifiers;


/**
 * Permet de stocker l'état d'une page et facilite notamment la gestion des
 * backs
 *
 * @author jeanseb
 *
 */
public class PageMarkerInfo implements Serializable {

    /** Default serial version ID. */
    private static final long serialVersionUID = 1L;

    /** Selections map. */
    private Map<SelectionMapIdentifiers, Set<SelectionItem>> selectionsMap;
	public PageMarkerInfo(String pageMarker) {
		super();
		this.pageMarker = pageMarker;
	}

	Integer firstTab;

	PortalObjectId currentPageId;

    Long selectionTs;

    String popupMode;
    PortalObjectId popupModeWindowID;
    PortalObjectId popupModeOriginalPageID;

    
    public PortalObjectId getPopupModeOriginalPageID() {
        return popupModeOriginalPageID;
    }

    
    public void setPopupModeOrginalPageID(PortalObjectId popupModeOriginalPageID) {
        this.popupModeOriginalPageID = popupModeOriginalPageID;
    }

    /** Closing popup action indicator. */
    private boolean closingPopupAction;


    /** Notifications. */
    private List<Notifications> notificationsList;

    public PortalObjectId getPopupModeWindowID() {
		return this.popupModeWindowID;
	}

	public void setPopupModeWindowID(PortalObjectId popupModeWindowID) {
		this.popupModeWindowID = popupModeWindowID;
	}

	public String getPopupMode() {
		return this.popupMode;
	}

	public void setPopupMode(String popupMode) {
		this.popupMode = popupMode;
	}

	public PortalObjectId getCurrentPageId() {
		return this.currentPageId;
	}

	public void setCurrentPageId(PortalObjectId currentPageId) {
		this.currentPageId = currentPageId;
	}

	public Integer getFirstTab() {
		return this.firstTab;
	}

	public void setFirstTab(Integer firstTab) {
		this.firstTab = firstTab;
	}
	String pageMarker;



	public String getPageMarker() {
		return this.pageMarker;
	}

	public void setPageMarker(String pageMarker) {
		this.pageMarker = pageMarker;
	}

	Long lastTimeStamp;
	public Long getLastTimeStamp() {
		return this.lastTimeStamp;
	}

	public void setLastTimeStamp(Long lastTimeStamp) {
		this.lastTimeStamp = lastTimeStamp;
	}

	PortalObjectId pageId = null;
	Map<PortalObjectId, WindowStateMarkerInfo> windowInfos;
	List<DynamicWindowBean> dynamicWindows;
	List<DynamicPageBean> dynamicPages;
	UserPortal tabbedNavHeaderUserPortal;
	public Long getTabbedNavheaderCount() {
		return this.tabbedNavheaderCount;
	}

	public void setTabbedNavheaderCount(Long tabbedNavheaderCount) {
		this.tabbedNavheaderCount = tabbedNavheaderCount;
	}

	public String getTabbedNavheaderUsername() {
		return this.tabbedNavheaderUsername;
	}

	public void setTabbedNavheaderUsername(String tabbedNavheaderUsername) {
		this.tabbedNavheaderUsername = tabbedNavheaderUsername;
	}

	public UserPortal getTabbedNavHeaderUserPortal() {
		return this.tabbedNavHeaderUserPortal;
	}

	public void setTabbedNavHeaderUserPortal(UserPortal tabbedNavHeaderUserPortal) {
		this.tabbedNavHeaderUserPortal = tabbedNavHeaderUserPortal;
	}


	Long tabbedNavheaderCount;
	String tabbedNavheaderUsername;


	public List<DynamicPageBean> getDynamicPages() {
		return this.dynamicPages;
	}

	public void setDynamicPages(List<DynamicPageBean> dynamicPages) {
		this.dynamicPages = dynamicPages;
	}


	PageNavigationalState pns;
	Breadcrumb breadcrumb = new Breadcrumb();

	public Breadcrumb getBreadcrumb() {
		return this.breadcrumb;
	}

	public void setBreadcrumb(Breadcrumb breadcrumb) {
		this.breadcrumb = breadcrumb;
	}

	public List<DynamicWindowBean> getDynamicWindows() {
		return this.dynamicWindows;
	}

	public Map<PortalObjectId, WindowStateMarkerInfo> getWindowInfos() {
		return this.windowInfos;
	}

	public void setWindowInfos(Map<PortalObjectId, WindowStateMarkerInfo> windowInfos) {
		this.windowInfos = windowInfos;
	}

	public void setDynamicWindows(List<DynamicWindowBean> dynamicWindows) {
		this.dynamicWindows = dynamicWindows;
	}

	public PortalObjectId getPageId() {
		return this.pageId;
	}

	public void setPageId(PortalObjectId pageId) {
		this.pageId = pageId;
	}

	public void setPageNavigationState(PageNavigationalState pns) {
		this.pns = pns;
	}

	public PageNavigationalState getPageNavigationState() {
		return this.pns;
	}

 /**
     * Getter.
     *
     * @return the selectionsMap
     */
    public Map<SelectionMapIdentifiers, Set<SelectionItem>> getSelectionsMap() {
        return this.selectionsMap;
    }

    /**
     * Setter.
     *
     * @param selectionsMap the selectionsMap to set
     */
    public void setSelectionsMap(Map<SelectionMapIdentifiers, Set<SelectionItem>> selectionsMap) {
        this.selectionsMap = selectionsMap;
    }

    public Long getSelectionTs() {
		return this.selectionTs;
	}

	public void setSelectionTs(Long selectionTs) {
		this.selectionTs = selectionTs;
	}

    /**
     * Getter for notificationsList.
     *
     * @return the notificationsList
     */
    public List<Notifications> getNotificationsList() {
        return this.notificationsList;
    }

    /**
     * Setter for notificationsList.
     *
     * @param notificationsList the notificationsList to set
     */
    public void setNotificationsList(List<Notifications> notificationsList) {
        this.notificationsList = notificationsList;
    }




    /**
     * Getter for closingPopupAction.
     * 
     * @return the closingPopupAction
     */
    public boolean isClosingPopupAction() {
        return this.closingPopupAction;
    }

    /**
     * Setter for closingPopupAction.
     * 
     * @param closingPopupAction the closingPopupAction to set
     */
    public void setClosingPopupAction(boolean closingPopupAction) {
        this.closingPopupAction = closingPopupAction;
    }

}
