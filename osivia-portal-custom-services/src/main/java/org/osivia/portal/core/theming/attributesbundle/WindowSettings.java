package org.osivia.portal.core.theming.attributesbundle;

import org.osivia.portal.api.ui.layout.LayoutGroup;

import java.util.*;


/**
 * Window settings java-bean.
 *
 * @author Cédric Krommenhoek
 */
public class WindowSettings {

    /**
     * Window identifier.
     */
    private final String id;
    /**
     * Styles.
     */
    private final Map<String, Boolean> styles;
    /**
     * Scopes.
     */
    private final Map<String, String> scopes;
    /**
     * Taskbar items.
     */
    private final SortedMap<String, String> taskbarItems;
    /**
     * Layout groups.
     */
    private final List<LayoutGroup> layoutGroups;
    /**
     * Nuxeo satellites.
     */
    private final Map<String, String> satellites;
    /**
     * Window title.
     */
    private String title;
    /**
     * Display window title indicator.
     */
    private boolean displayTitle;
    /**
     * Display window title decorators indicator.
     */
    private boolean displayTitleDecorators;
    /**
     * Maximized to CMS indicator.
     */
    private boolean maximizedToCms;
    /**
     * Display panel indicator.
     */
    private boolean displayPanel;
    /**
     * Panel collapse indicator.
     */
    private boolean panelCollapse;
    /**
     * Ajax indicator.
     */
    private boolean ajax;
    
    /** 
     * The cache deactivation. 
     */
    private boolean cacheDeactivation;
    
    
    /**
     * Hide empty portlet indicator.
     */
    private boolean hideEmpty;
    /**
     * Print indicator.
     */
    private boolean print;
    /**
     * Selected scope.
     */
    private String selectedScope;
    /**
     * Linked taskbar item identifier.
     */
    private String taskbarItemId;
    /**
     * Linked layout item identifier.
     */
    private String layoutItemId;
    /**
     * Customization identifier.
     */
    private String customizationId;
    /**
     * Shared cache identifier.
     */
    private String sharedCacheId;
    /**
     * BeanShell indicator.
     */
    private boolean beanShell;
    /**
     * BeanShell content.
     */
    private String beanShellContent;
    /**
     * Selection dependency indicator.
     */
    private boolean selectionDependency;
    /**
     * The priority.
     */
    private String priority;
    /**
     * Selected satellite.
     */
    private String selectedSatellite;


    /**
     * Constructor.
     *
     * @param id window indentifier
     */
    public WindowSettings(String id) {
        super();
        this.id = id;
        this.styles = new LinkedHashMap<>();
        this.scopes = new LinkedHashMap<>();
        this.taskbarItems = new TreeMap<>();
        this.layoutGroups = new ArrayList<>();
        this.satellites = new LinkedHashMap<>();
    }


    /**
     * Getter for title.
     *
     * @return the title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Setter for title.
     *
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter for displayTitle.
     *
     * @return the displayTitle
     */
    public boolean isDisplayTitle() {
        return this.displayTitle;
    }

    /**
     * Setter for displayTitle.
     *
     * @param displayTitle the displayTitle to set
     */
    public void setDisplayTitle(boolean displayTitle) {
        this.displayTitle = displayTitle;
    }

    /**
     * Getter for displayTitleDecorators.
     *
     * @return the displayTitleDecorators
     */
    public boolean isDisplayTitleDecorators() {
        return this.displayTitleDecorators;
    }

    /**
     * Setter for displayTitleDecorators.
     *
     * @param displayTitleDecorators the displayTitleDecorators to set
     */
    public void setDisplayTitleDecorators(boolean displayTitleDecorators) {
        this.displayTitleDecorators = displayTitleDecorators;
    }

    /**
     * Getter for maximizedToCms.
     *
     * @return the maximizedToCms
     */
    public boolean isMaximizedToCms() {
        return this.maximizedToCms;
    }

    /**
     * Setter for maximizedToCms.
     *
     * @param maximizedToCms the maximizedToCms to set
     */
    public void setMaximizedToCms(boolean maximizedToCms) {
        this.maximizedToCms = maximizedToCms;
    }

    /**
     * Getter for displayPanel.
     *
     * @return the displayPanel
     */
    public boolean isDisplayPanel() {
        return this.displayPanel;
    }

    /**
     * Setter for displayPanel.
     *
     * @param displayPanel the displayPanel to set
     */
    public void setDisplayPanel(boolean displayPanel) {
        this.displayPanel = displayPanel;
    }

    /**
     * Getter for panelCollapse.
     *
     * @return the panelCollapse
     */
    public boolean isPanelCollapse() {
        return this.panelCollapse;
    }

    /**
     * Setter for panelCollapse.
     *
     * @param panelCollapse the panelCollapse to set
     */
    public void setPanelCollapse(boolean panelCollapse) {
        this.panelCollapse = panelCollapse;
    }

    /**
     * Getter for ajax.
     *
     * @return the ajax
     */
    public boolean isAjax() {
        return this.ajax;
    }

    /**
     * Setter for ajax.
     *
     * @param ajax the ajax to set
     */
    public void setAjax(boolean ajax) {
        this.ajax = ajax;
    }

    /**
     * Getter for cacheDeactivation.
     * @return the cacheDeactivation
     */
    public boolean isCacheDeactivation() {
        return cacheDeactivation;
    }


    
    /**
     * Setter for cacheDeactivation.
     * @param cacheDeactivation the cacheDeactivation to set
     */
    public void setCacheDeactivation(boolean cacheDeactivation) {
        this.cacheDeactivation = cacheDeactivation;
    }

    
    /**
     * Getter for hideEmpty.
     *
     * @return the hideEmpty
     */
    public boolean isHideEmpty() {
        return this.hideEmpty;
    }

    /**
     * Setter for hideEmpty.
     *
     * @param hideEmpty the hideEmpty to set
     */
    public void setHideEmpty(boolean hideEmpty) {
        this.hideEmpty = hideEmpty;
    }

    /**
     * Getter for print.
     *
     * @return the print
     */
    public boolean isPrint() {
        return this.print;
    }

    /**
     * Setter for print.
     *
     * @param print the print to set
     */
    public void setPrint(boolean print) {
        this.print = print;
    }

    /**
     * Getter for selectedScope.
     *
     * @return the selectedScope
     */
    public String getSelectedScope() {
        return this.selectedScope;
    }

    /**
     * Setter for selectedScope.
     *
     * @param selectedScope the selectedScope to set
     */
    public void setSelectedScope(String selectedScope) {
        this.selectedScope = selectedScope;
    }

    /**
     * Getter for taskbarItemId.
     *
     * @return the taskbarItemId
     */
    public String getTaskbarItemId() {
        return taskbarItemId;
    }

    /**
     * Setter for taskbarItemId.
     *
     * @param taskbarItemId the taskbarItemId to set
     */
    public void setTaskbarItemId(String taskbarItemId) {
        this.taskbarItemId = taskbarItemId;
    }

    public String getLayoutItemId() {
        return layoutItemId;
    }

    public void setLayoutItemId(String layoutItemId) {
        this.layoutItemId = layoutItemId;
    }

    /**
     * Getter for customizationId.
     *
     * @return the customizationId
     */
    public String getCustomizationId() {
        return this.customizationId;
    }

    /**
     * Setter for customizationId.
     *
     * @param customizationId the customizationId to set
     */
    public void setCustomizationId(String customizationId) {
        this.customizationId = customizationId;
    }

    /**
     * Getter for sharedCacheId.
     *
     * @return the sharedCacheId
     */
    public String getSharedCacheId() {
        return this.sharedCacheId;
    }

    /**
     * Setter for sharedCacheId.
     *
     * @param sharedCacheId the sharedCacheId to set
     */
    public void setSharedCacheId(String sharedCacheId) {
        this.sharedCacheId = sharedCacheId;
    }

    /**
     * Getter for beanShell.
     *
     * @return the beanShell
     */
    public boolean isBeanShell() {
        return this.beanShell;
    }

    /**
     * Setter for beanShell.
     *
     * @param beanShell the beanShell to set
     */
    public void setBeanShell(boolean beanShell) {
        this.beanShell = beanShell;
    }

    /**
     * Getter for beanShellContent.
     *
     * @return the beanShellContent
     */
    public String getBeanShellContent() {
        return this.beanShellContent;
    }

    /**
     * Setter for beanShellContent.
     *
     * @param beanShellContent the beanShellContent to set
     */
    public void setBeanShellContent(String beanShellContent) {
        this.beanShellContent = beanShellContent;
    }

    /**
     * Getter for selectionDependency.
     *
     * @return the selectionDependency
     */
    public boolean isSelectionDependency() {
        return this.selectionDependency;
    }

    /**
     * Setter for selectionDependency.
     *
     * @param selectionDependency the selectionDependency to set
     */
    public void setSelectionDependency(boolean selectionDependency) {
        this.selectionDependency = selectionDependency;
    }

    /**
     * Getter for priority.
     *
     * @return the priority
     */
    public String getPriority() {
        return priority;
    }

    /**
     * Setter for priority.
     *
     * @param priority the new priority
     */
    public void setPriority(String priority) {
        this.priority = priority;
    }

    /**
     * Getter for selectedSatellite.
     *
     * @return the selectedSatellite
     */
    public String getSelectedSatellite() {
        return selectedSatellite;
    }

    /**
     * Setter for selectedSatellite.
     *
     * @param selectedSatellite the selectedSatellite to set
     */
    public void setSelectedSatellite(String selectedSatellite) {
        this.selectedSatellite = selectedSatellite;
    }

    /**
     * Getter for id.
     *
     * @return the id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Getter for styles.
     *
     * @return the styles
     */
    public Map<String, Boolean> getStyles() {
        return this.styles;
    }

    /**
     * Getter for scopes.
     *
     * @return the scopes
     */
    public Map<String, String> getScopes() {
        return this.scopes;
    }

    /**
     * Getter for taskbarItems.
     *
     * @return the taskbarItems
     */
    public SortedMap<String, String> getTaskbarItems() {
        return taskbarItems;
    }

    public List<LayoutGroup> getLayoutGroups() {
        return layoutGroups;
    }

    /**
     * Getter for satellites.
     *
     * @return the satellites
     */
    public Map<String, String> getSatellites() {
        return satellites;
    }

}
