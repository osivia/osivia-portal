package org.osivia.portal.core.pagemarker;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.osivia.portal.api.charte.Breadcrumb;
import org.osivia.portal.api.charte.UserPortal;
import org.osivia.portal.api.selection.SelectionItem;
import org.osivia.portal.core.dynamic.DynamicPageBean;
import org.osivia.portal.core.dynamic.DynamicWindowBean;
import org.osivia.portal.core.selection.SelectionMapIdentifiers;


/**
 * Permet de stocker l'état d'une page et facilite notamment la gestion des backs.
 * 
 * @author jeanseb
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

    public PortalObjectId getCurrentPageId() {
        return currentPageId;
    }

    public void setCurrentPageId(PortalObjectId currentPageId) {
        this.currentPageId = currentPageId;
    }

    public Integer getFirstTab() {
        return firstTab;
    }

    public void setFirstTab(Integer firstTab) {
        this.firstTab = firstTab;
    }

    String pageMarker;


    public String getPageMarker() {
        return pageMarker;
    }

    public void setPageMarker(String pageMarker) {
        this.pageMarker = pageMarker;
    }

    Long lastTimeStamp;

    public Long getLastTimeStamp() {
        return lastTimeStamp;
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
        return tabbedNavheaderCount;
    }

    public void setTabbedNavheaderCount(Long tabbedNavheaderCount) {
        this.tabbedNavheaderCount = tabbedNavheaderCount;
    }

    public String getTabbedNavheaderUsername() {
        return tabbedNavheaderUsername;
    }

    public void setTabbedNavheaderUsername(String tabbedNavheaderUsername) {
        this.tabbedNavheaderUsername = tabbedNavheaderUsername;
    }

    public UserPortal getTabbedNavHeaderUserPortal() {
        return tabbedNavHeaderUserPortal;
    }

    public void setTabbedNavHeaderUserPortal(UserPortal tabbedNavHeaderUserPortal) {
        this.tabbedNavHeaderUserPortal = tabbedNavHeaderUserPortal;
    }


    Long tabbedNavheaderCount;
    String tabbedNavheaderUsername;


    public List<DynamicPageBean> getDynamicPages() {
        return dynamicPages;
    }

    public void setDynamicPages(List<DynamicPageBean> dynamicPages) {
        this.dynamicPages = dynamicPages;
    }


    PageNavigationalState pns;
    Breadcrumb breadcrumb = new Breadcrumb();

    public Breadcrumb getBreadcrumb() {
        return breadcrumb;
    }

    public void setBreadcrumb(Breadcrumb breadcrumb) {
        this.breadcrumb = breadcrumb;
    }

    public List<DynamicWindowBean> getDynamicWindows() {
        return dynamicWindows;
    }

    public Map<PortalObjectId, WindowStateMarkerInfo> getWindowInfos() {
        return windowInfos;
    }

    public void setWindowInfos(Map<PortalObjectId, WindowStateMarkerInfo> windowInfos) {
        this.windowInfos = windowInfos;
    }

    public void setDynamicWindows(List<DynamicWindowBean> dynamicWindows) {
        this.dynamicWindows = dynamicWindows;
    }

    public PortalObjectId getPageId() {
        return pageId;
    }

    public void setPageId(PortalObjectId pageId) {
        this.pageId = pageId;
    }

    public void setPageNavigationState(PageNavigationalState pns) {
        this.pns = pns;
    }

    public PageNavigationalState getPageNavigationState() {
        return pns;
    }

    /**
     * Getter.
     * 
     * @return the selectionsMap
     */
    public Map<SelectionMapIdentifiers, Set<SelectionItem>> getSelectionsMap() {
        return selectionsMap;
    }

    /**
     * Setter.
     * 
     * @param selectionsMap the selectionsMap to set
     */
    public void setSelectionsMap(Map<SelectionMapIdentifiers, Set<SelectionItem>> selectionsMap) {
        this.selectionsMap = selectionsMap;
    }

}
