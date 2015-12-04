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
 */
package org.osivia.portal.core.pagemarker;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.osivia.portal.api.notifications.Notifications;
import org.osivia.portal.api.theming.Breadcrumb;
import org.osivia.portal.api.theming.UserPortal;
import org.osivia.portal.core.attributes.AttributesStorage;
import org.osivia.portal.core.attributes.StorageAttributeKey;
import org.osivia.portal.core.attributes.StorageAttributeValue;
import org.osivia.portal.core.dynamic.DynamicPageBean;
import org.osivia.portal.core.dynamic.DynamicWindowBean;
import org.osivia.portal.core.portlet.PortletStatusContainer;


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


    /** Back page marker. */
    private String backPageMarker;
    /** Breadcrumb. */
    private Breadcrumb breadcrumb;
    /** Closing popup action indicator. */
    private boolean closingPopupAction;
    /** Current page identifier. */
    private PortalObjectId currentPageId;
    /** Dynamic pages. */
    private List<DynamicPageBean> dynamicPages;
    /** Dynamic windows. */
    private List<DynamicWindowBean> dynamicWindows;
    /** First tab index. */
    private Integer firstTab;
    /** Last timestamp. */
    private Long lastTimeStamp;
    /** Mobile back page marker. */
    private String mobileBackPageMarker;
    /** Mobile refresh back indicator. */
    private boolean mobileRefreshBack;
    /** Notifications. */
    private List<Notifications> notificationsList;
    /** Page identifier. */
    private PortalObjectId pageId;
    /** Page navigational state. */
    private PageNavigationalState pageNavigationalState;
    /** Popup mode. */
    private String popupMode;
    /** Popup mode original page identifier. */
    private PortalObjectId popupModeOriginalPageID;
    /** Popup mode window identifier. */
    private PortalObjectId popupModeWindowID;
    /** Portlet status container. */
    private PortletStatusContainer portletStatusContainer;
    /** Refresh back indicator. */
    private boolean refreshBack;
    /** Storage. */
    private Map<AttributesStorage, Map<StorageAttributeKey, StorageAttributeValue>> storage;
    /** Storage timestamps. */
    private Map<AttributesStorage, Long> storageTimestamps;
    /** User tabs count. */
    private Long tabbedNavHeaderCount;
    /** User name. */
    private String tabbedNavHeaderUsername;
    /** User tabs. */
    private UserPortal tabbedNavHeaderUserPortal;
    /** Window infos. */
    private Map<PortalObjectId, WindowStateMarkerInfo> windowInfos;



    /** Page marker. */
    private final String pageMarker;


    /**
     * Constructor.
     *
     * @param pageMarker page marker
     */
    public PageMarkerInfo(String pageMarker) {
        super();
        this.pageMarker = pageMarker;
        this.breadcrumb = new Breadcrumb();
    }


    /**
     * Getter for backPageMarker.
     *
     * @return the backPageMarker
     */
    public String getBackPageMarker() {
        return this.backPageMarker;
    }

    /**
     * Setter for backPageMarker.
     *
     * @param backPageMarker the backPageMarker to set
     */
    public void setBackPageMarker(String backPageMarker) {
        this.backPageMarker = backPageMarker;
    }

    /**
     * Getter for breadcrumb.
     *
     * @return the breadcrumb
     */
    public Breadcrumb getBreadcrumb() {
        return this.breadcrumb;
    }

    /**
     * Setter for breadcrumb.
     *
     * @param breadcrumb the breadcrumb to set
     */
    public void setBreadcrumb(Breadcrumb breadcrumb) {
        this.breadcrumb = breadcrumb;
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

    /**
     * Getter for currentPageId.
     *
     * @return the currentPageId
     */
    public PortalObjectId getCurrentPageId() {
        return this.currentPageId;
    }

    /**
     * Setter for currentPageId.
     *
     * @param currentPageId the currentPageId to set
     */
    public void setCurrentPageId(PortalObjectId currentPageId) {
        this.currentPageId = currentPageId;
    }

    /**
     * Getter for dynamicPages.
     *
     * @return the dynamicPages
     */
    public List<DynamicPageBean> getDynamicPages() {
        return this.dynamicPages;
    }

    /**
     * Setter for dynamicPages.
     *
     * @param dynamicPages the dynamicPages to set
     */
    public void setDynamicPages(List<DynamicPageBean> dynamicPages) {
        this.dynamicPages = dynamicPages;
    }

    /**
     * Getter for dynamicWindows.
     *
     * @return the dynamicWindows
     */
    public List<DynamicWindowBean> getDynamicWindows() {
        return this.dynamicWindows;
    }

    /**
     * Setter for dynamicWindows.
     *
     * @param dynamicWindows the dynamicWindows to set
     */
    public void setDynamicWindows(List<DynamicWindowBean> dynamicWindows) {
        this.dynamicWindows = dynamicWindows;
    }

    /**
     * Getter for firstTab.
     *
     * @return the firstTab
     */
    public Integer getFirstTab() {
        return this.firstTab;
    }

    /**
     * Setter for firstTab.
     *
     * @param firstTab the firstTab to set
     */
    public void setFirstTab(Integer firstTab) {
        this.firstTab = firstTab;
    }

    /**
     * Getter for lastTimeStamp.
     *
     * @return the lastTimeStamp
     */
    public Long getLastTimeStamp() {
        return this.lastTimeStamp;
    }

    /**
     * Setter for lastTimeStamp.
     *
     * @param lastTimeStamp the lastTimeStamp to set
     */
    public void setLastTimeStamp(Long lastTimeStamp) {
        this.lastTimeStamp = lastTimeStamp;
    }

    /**
     * Getter for mobileBackPageMarker.
     *
     * @return the mobileBackPageMarker
     */
    public String getMobileBackPageMarker() {
        return this.mobileBackPageMarker;
    }

    /**
     * Setter for mobileBackPageMarker.
     *
     * @param mobileBackPageMarker the mobileBackPageMarker to set
     */
    public void setMobileBackPageMarker(String mobileBackPageMarker) {
        this.mobileBackPageMarker = mobileBackPageMarker;
    }

    /**
     * Getter for mobileRefreshBack.
     *
     * @return the mobileRefreshBack
     */
    public boolean isMobileRefreshBack() {
        return this.mobileRefreshBack;
    }

    /**
     * Setter for mobileRefreshBack.
     *
     * @param mobileRefreshBack the mobileRefreshBack to set
     */
    public void setMobileRefreshBack(boolean mobileRefreshBack) {
        this.mobileRefreshBack = mobileRefreshBack;
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
     * Getter for pageId.
     *
     * @return the pageId
     */
    public PortalObjectId getPageId() {
        return this.pageId;
    }

    /**
     * Setter for pageId.
     *
     * @param pageId the pageId to set
     */
    public void setPageId(PortalObjectId pageId) {
        this.pageId = pageId;
    }

    /**
     * Getter for pageNavigationalState.
     *
     * @return the pageNavigationalState
     */
    public PageNavigationalState getPageNavigationalState() {
        return this.pageNavigationalState;
    }

    /**
     * Setter for pageNavigationalState.
     *
     * @param pageNavigationalState the pageNavigationalState to set
     */
    public void setPageNavigationalState(PageNavigationalState pageNavigationalState) {
        this.pageNavigationalState = pageNavigationalState;
    }

    /**
     * Getter for popupMode.
     *
     * @return the popupMode
     */
    public String getPopupMode() {
        return this.popupMode;
    }

    /**
     * Setter for popupMode.
     *
     * @param popupMode the popupMode to set
     */
    public void setPopupMode(String popupMode) {
        this.popupMode = popupMode;
    }

    /**
     * Getter for popupModeOriginalPageID.
     *
     * @return the popupModeOriginalPageID
     */
    public PortalObjectId getPopupModeOriginalPageID() {
        return this.popupModeOriginalPageID;
    }

    /**
     * Setter for popupModeOriginalPageID.
     *
     * @param popupModeOriginalPageID the popupModeOriginalPageID to set
     */
    public void setPopupModeOriginalPageID(PortalObjectId popupModeOriginalPageID) {
        this.popupModeOriginalPageID = popupModeOriginalPageID;
    }

    /**
     * Getter for popupModeWindowID.
     *
     * @return the popupModeWindowID
     */
    public PortalObjectId getPopupModeWindowID() {
        return this.popupModeWindowID;
    }

    /**
     * Setter for popupModeWindowID.
     *
     * @param popupModeWindowID the popupModeWindowID to set
     */
    public void setPopupModeWindowID(PortalObjectId popupModeWindowID) {
        this.popupModeWindowID = popupModeWindowID;
    }

    /**
     * Getter for portletStatusContainer.
     *
     * @return the portletStatusContainer
     */
    public PortletStatusContainer getPortletStatusContainer() {
        return this.portletStatusContainer;
    }

    /**
     * Setter for portletStatusContainer.
     *
     * @param portletStatusContainer the portletStatusContainer to set
     */
    public void setPortletStatusContainer(PortletStatusContainer portletStatusContainer) {
        this.portletStatusContainer = portletStatusContainer;
    }

    /**
     * Getter for refreshBack.
     *
     * @return the refreshBack
     */
    public boolean isRefreshBack() {
        return this.refreshBack;
    }

    /**
     * Setter for refreshBack.
     *
     * @param refreshBack the refreshBack to set
     */
    public void setRefreshBack(boolean refreshBack) {
        this.refreshBack = refreshBack;
    }

    /**
     * Getter for storage.
     *
     * @return the storage
     */
    public Map<AttributesStorage, Map<StorageAttributeKey, StorageAttributeValue>> getStorage() {
        return this.storage;
    }

    /**
     * Setter for storage.
     *
     * @param storage the storage to set
     */
    public void setStorage(Map<AttributesStorage, Map<StorageAttributeKey, StorageAttributeValue>> storage) {
        this.storage = storage;
    }

    /**
     * Getter for storageTimestamps.
     * 
     * @return the storageTimestamps
     */
    public Map<AttributesStorage, Long> getStorageTimestamps() {
        return this.storageTimestamps;
    }

    /**
     * Setter for storageTimestamps.
     * 
     * @param storageTimestamps the storageTimestamps to set
     */
    public void setStorageTimestamps(Map<AttributesStorage, Long> storageTimestamps) {
        this.storageTimestamps = storageTimestamps;
    }

    /**
     * Getter for tabbedNavHeaderCount.
     *
     * @return the tabbedNavHeaderCount
     */
    public Long getTabbedNavHeaderCount() {
        return this.tabbedNavHeaderCount;
    }

    /**
     * Setter for tabbedNavHeaderCount.
     *
     * @param tabbedNavHeaderCount the tabbedNavHeaderCount to set
     */
    public void setTabbedNavHeaderCount(Long tabbedNavHeaderCount) {
        this.tabbedNavHeaderCount = tabbedNavHeaderCount;
    }

    /**
     * Getter for tabbedNavHeaderUsername.
     *
     * @return the tabbedNavHeaderUsername
     */
    public String getTabbedNavHeaderUsername() {
        return this.tabbedNavHeaderUsername;
    }

    /**
     * Setter for tabbedNavHeaderUsername.
     *
     * @param tabbedNavHeaderUsername the tabbedNavHeaderUsername to set
     */
    public void setTabbedNavHeaderUsername(String tabbedNavHeaderUsername) {
        this.tabbedNavHeaderUsername = tabbedNavHeaderUsername;
    }

    /**
     * Getter for tabbedNavHeaderUserPortal.
     *
     * @return the tabbedNavHeaderUserPortal
     */
    public UserPortal getTabbedNavHeaderUserPortal() {
        return this.tabbedNavHeaderUserPortal;
    }

    /**
     * Setter for tabbedNavHeaderUserPortal.
     *
     * @param tabbedNavHeaderUserPortal the tabbedNavHeaderUserPortal to set
     */
    public void setTabbedNavHeaderUserPortal(UserPortal tabbedNavHeaderUserPortal) {
        this.tabbedNavHeaderUserPortal = tabbedNavHeaderUserPortal;
    }

    /**
     * Getter for windowInfos.
     *
     * @return the windowInfos
     */
    public Map<PortalObjectId, WindowStateMarkerInfo> getWindowInfos() {
        return this.windowInfos;
    }

    /**
     * Setter for windowInfos.
     *
     * @param windowInfos the windowInfos to set
     */
    public void setWindowInfos(Map<PortalObjectId, WindowStateMarkerInfo> windowInfos) {
        this.windowInfos = windowInfos;
    }

    /**
     * Getter for pageMarker.
     *
     * @return the pageMarker
     */
    public String getPageMarker() {
        return this.pageMarker;
    }

}
