<%@page import="org.apache.commons.lang.BooleanUtils"%>
<%@page import="org.jboss.portal.theme.PortalTheme"%>
<%@page import="org.jboss.portal.core.controller.ControllerContext"%>
<%@page import="org.osivia.portal.core.portalobjects.PortalObjectUtils"%>
<%@page import="java.util.Enumeration"%>
<%@page import="java.util.Locale"%>
<%@page import="org.osivia.portal.core.formatters.IFormatter"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Map"%>
<%@page import="org.jboss.portal.identity.Role"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="java.util.List"%>
<%@page import="org.jboss.portal.theme.PortalLayout"%>
<%@page import="org.apache.commons.collections.CollectionUtils"%>
<%@page import="org.jboss.portal.core.model.portal.PortalObjectPath"%>
<%@page import="java.util.Collection"%>
<%@page import="org.jboss.portal.core.model.portal.Page"%>
<%@page import="java.util.ResourceBundle"%>
<%@page import="org.osivia.portal.api.Constants"%>

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>


<%
// Resource bundle
ResourceBundle rb = ResourceBundle.getBundle(IFormatter.RESOURCE_BUNDLE_NAME, request.getLocale());

// Formatter
IFormatter formatter = (IFormatter) request.getAttribute(Constants.ATTR_TOOLBAR_SETTINGS_FORMATTER);
// Controller context
ControllerContext context = (ControllerContext) request.getAttribute(Constants.ATTR_TOOLBAR_SETTINGS_CONTROLLER_CONTEXT);
// Generic command URL
String commandUrl = (String) request.getAttribute(Constants.ATTR_TOOLBAR_SETTINGS_COMMAND_URL);
// Current page
Page currentPage = (Page) request.getAttribute(Constants.ATTR_TOOLBAR_SETTINGS_PAGE);
// Default page
Boolean defaultPage = (Boolean) request.getAttribute(Constants.ATTR_TOOLBAR_SETTINGS_DEFAULT_PAGE);
// Draft page
Boolean draftPage = (Boolean) request.getAttribute(Constants.ATTR_TOOLBAR_SETTINGS_DRAFT_PAGE);
// Layout list
@SuppressWarnings("unchecked")
List<PortalLayout> layoutsList = (List<PortalLayout>) request.getAttribute(Constants.ATTR_TOOLBAR_SETTINGS_LAYOUTS_LIST);
// Current layout
String currentLayout = (String) request.getAttribute(Constants.ATTR_TOOLBAR_SETTINGS_CURRENT_LAYOUT);
//Themes list
@SuppressWarnings("unchecked")
List<PortalTheme> themesList = (List<PortalTheme>) request.getAttribute(Constants.ATTR_TOOLBAR_SETTINGS_THEMES_LIST);
// Current theme
String currentTheme = (String) request.getAttribute(Constants.ATTR_TOOLBAR_SETTINGS_CURRENT_THEME);
// Siblings pages
@SuppressWarnings("unchecked")
Collection<Page> siblings = (Collection<Page>) request.getAttribute(Constants.ATTR_TOOLBAR_SETTINGS_SIBLINGS_PAGES);
// Roles
@SuppressWarnings("unchecked")
List<Role> roles = (List<Role>) request.getAttribute(Constants.ATTR_TOOLBAR_SETTINGS_ROLES);
// Actions for roles
@SuppressWarnings("unchecked")
Map<String, Set<String>> actionsForRoles = (Map<String, Set<String>>) request.getAttribute(Constants.ATTR_TOOLBAR_SETTINGS_ACTIONS_FOR_ROLES);
// Delete page command URL
String deletePageCommandUrl = (String) request.getAttribute(Constants.ATTR_TOOLBAR_SETTINGS_DELETE_PAGE_COMMAND_URL);
// CMS scope select
String cmsScopeSelect = (String) request.getAttribute(Constants.ATTR_TOOLBAR_SETTINGS_CMS_SCOPE_SELECT);
// CMS display live version
String cmsDisplayLiveVersion = (String) request.getAttribute(Constants.ATTR_TOOLBAR_SETTINGS_CMS_DISPLAY_LIVE_VERSION);   
// CMS recontextualization support
String cmsRecontextualizationSupport = (String) request.getAttribute(Constants.ATTR_TOOLBAR_SETTINGS_CMS_RECONTEXTUALIZATION_SUPPORT);
// CMS base path
String cmsBasePath = (String) request.getAttribute(Constants.ATTR_TOOLBAR_SETTINGS_CMS_BASE_PATH);
// CMS navigation mode
Boolean cmsNavigationMode = (Boolean) request.getAttribute(Constants.ATTR_TOOLBAR_SETTINGS_CMS_NAVIGATION_MODE);


// Draft page checkbox value
String checkDraft = StringUtils.EMPTY;
if (BooleanUtils.isTrue(draftPage)) {
    checkDraft = "checked=\"checked\"";
}
// CMS navigation mode checkbox value
String checkCmsNavigationMode = StringUtils.EMPTY; 
if (BooleanUtils.isTrue(cmsNavigationMode)) {
    checkCmsNavigationMode = "checked=\"checked\"";
}


// Locales
@SuppressWarnings("unchecked")
Enumeration<Locale> locales = request.getLocales();

// Portal ID
String portalId = formatter.formatHtmlSafeEncodingId(currentPage.getPortal().getId());
// Current page ID
String currentPageId = formatter.formatHtmlSafeEncodingId(currentPage.getId());
// Current page name
String currentPageName = PortalObjectUtils.getDisplayName(currentPage, locales);
%>


<script type="text/javascript">
function disableOrNotPreviousFormValues(cmsPathInput){
    var cmsForm = document.forms["formCMSProperties"];
    if(cmsPathInput.value != ''){
        cmsForm.elements["scope"].disabled = true;
        cmsForm.elements["displayLiveVersion"].disabled = true;
        cmsForm.elements["outgoingRecontextualizationSupport"].disabled = true;
    } else {
        cmsForm.elements["scope"].disabled = false;
        cmsForm.elements["displayLiveVersion"].disabled = false;
        cmsForm.elements["outgoingRecontextualizationSupport"].disabled = false;
    }
}

var portalId = '<%=portalId %>';
var currentPageId = '<%=currentPageId %>';
</script>


<!-- Fancybox de création de page -->
<div class="fancybox-content">
    <div id="page-creation">
        <form action="<%=commandUrl %>" method="get" class="fancybox-form">
            <input type="hidden" name="action" value="createPage" />
            <input type="hidden" name="jstreeParentSelect" value="<%=portalId %>" />
            <input type="hidden" name="jstreeModelSelect" />

            <div class="fancybox-table">
                <div class="fancybox-table-row">
                    <div class="fancybox-table-cell label required"><%=rb.getString("NEW_PAGE_NAME") %></div>
                    <div class="fancybox-table-cell">
                        <input type="text" name="name" required />
                    </div>
                    <div class="fancybox-table-cell">&nbsp;</div>
                    <div class="fancybox-table-cell">&nbsp;</div>
                </div>

                <div class="fancybox-table-row">
                    <div class="fancybox-table-cell label"><%=rb.getString("NEW_PAGE_MODEL") %></div>
                    <div class="fancybox-table-cell">
                        <input type="text" onkeyup="jstreeSearch('jstreeModelSelect', this.value)" class="filter" placeholder="<%=rb.getString("JSTREE_FILTER") %>" />                       
                    </div>
                
                    <div class="fancybox-table-cell label required"><%=rb.getString("NEW_PAGE_PARENT") %></div>
                    <div class="fancybox-table-cell">
                        <input type="text" onkeyup="jstreeSearch('jstreeParentSelect', this.value)" class="filter" placeholder="<%=rb.getString("JSTREE_FILTER") %>" />                                               
                    </div>                    
                </div>
                
                <div class="fancybox-table-row">
                    <div class="fancybox-table-cell">
                        <label for="checkboxNoModel"><%=rb.getString("NEW_PAGE_NO_MODEL") %></label>                        
                        <input id="checkboxNoModel" type="checkbox" onchange="jstreeToggleLock('jstreeModelSelect', this.checked)" class="inline-checkbox" />
                    </div>
                    
                    <div class="fancybox-table-cell">
                        <div id="jstreeModelSelect" class="jstree-select-unique">
                            <%=formatter.formatHtmlTreePortalObjects(currentPage, context, "jstreeModelSelect") %>
                        </div>
                    </div>
                
                    <div class="fancybox-table-cell">&nbsp;</div>
                    
                    <div class="fancybox-table-cell">
                        <div id="jstreeParentSelect" class="jstree-select-unique">
                            <%=formatter.formatHtmlTreePortalObjects(currentPage, context, "jstreeParentSelect", true, false, false) %>
                        </div>                        
                    </div>
                </div>
            </div>

            <div class="fancybox-center-content">
                <input type="submit" value='<%=rb.getString("NEW_PAGE_SUBMIT") %>' />
                <input type="button" value='<%=rb.getString("CANCEL") %>' onclick="closeFancybox()" />
            </div>
        </form>
    </div>
</div>


<!-- Fancybox de sélection de la page d'accueil -->
<div class="fancybox-content">
    <div id="home-page-selection">
        <form action="<%=commandUrl %>" method="get" class="fancybox-form">
            <input type="hidden" name="action" value="makeDefaultPage" />
            <input type="hidden" name="jstreeHomeSelect" />
  
            <div class="fancybox-table">
                <div class="fancybox-table-row">
                    <div class="fancybox-table-cell">
                        <label for="rename-page-filter"><%=rb.getString("JSTREE_FILTER") %></label>
                        <input id="rename-page-filter" type="text" onkeyup="jstreeSearch('jstreeHomeSelect', this.value)" />
                    </div>
                </div>
                <div class="fancybox-table-row">
                    <div class="fancybox-table-cell">
                        <div id="jstreeHomeSelect" class="jstree-select-unique">
                            <%=formatter.formatHtmlTreePortalObjects(currentPage, context, "jstreeHomeSelect") %>
                        </div>
                    </div>
                </div>
            </div>
  
            <div class="fancybox-center-content">
                <input type="submit" value='<%=rb.getString("HOME_PAGE_SELECTION_SUBMIT") %>' />
                <input type="button" value='<%=rb.getString("CANCEL") %>' onclick="closeFancybox()" />
            </div>
        </form>
    </div>
</div>





<!-- Fancybox de renommage de la page courante -->
<div class="fancybox-content">
    <div id="current-page-rename">
        <form action="<%=commandUrl %>" method="get" class="fancybox-form">
            <input type="hidden" name="action" value="renamePage" />
            <input type="hidden" name="pageId" value="<%=currentPageId %>" />    
            
            <div class="fancybox-table">
                <div class="fancybox-table-row">
                    <div class="fancybox-table-cell label">
                        <p><%=rb.getString("PAGE_NAME") %></p>
                    </div>
                    <div class="fancybox-table-cell">
                        <input type="text" name="displayName" value="<%=currentPageName %>" />                    
                    </div>
                </div>
            </div>
            
            <div class="fancybox-center-content">
                <input type="submit" value='<%=rb.getString("PAGE_RENAME_SUBMIT") %>' />
                <input type="button" value='<%=rb.getString("CANCEL") %>' onclick="closeFancybox()" />
            </div>
        </form>
    </div>
</div>


<!-- Fancybox de propriétés de la page -->
<div class="fancybox-content">
    <div id="page-properties">
        <div class="fancybox-table">
        
            <!-- Renommer la page -->
            <form action="<%=commandUrl %>" method="get" class="fancybox-form">
                <input type="hidden" name="action" value="renamePage" />
                <input type="hidden" name="pageId" value="<%=currentPageId %>" />    

                <div class="fancybox-table-row">
                    <div class="fancybox-table-cell label">
                        <p><%=rb.getString("PAGE_NAME") %></p>
                    </div>
                    <div class="fancybox-table-cell">
                        <input type="text" name="displayName" value="<%=currentPageName %>" />                    
                    </div>
                    <div class="fancybox-table-cell">
                        <input type="submit" value='<%=rb.getString("PAGE_RENAME_SUBMIT") %>' />
                    </div>
                </div>
            </form>        
        
            <!-- Mode brouillon -->  
            <form action="<%=commandUrl %>" method="get" class="fancybox-form">
                <input type="hidden" name="action" value="changePageProperties" />
                <input type="hidden" name="pageId" value="<%=currentPageId %>" />    
                
                <div class="fancybox-table-row">
                    <div class="fancybox-table-cell label">
                        <p><%=rb.getString("PAGE_DRAFT_MODE") %></p>
                    </div>
                    <div class="fancybox-table-cell">
                        <input type="checkbox" name="draftPage" value="1" <%=checkDraft %> class="small-input" />                        
                    </div>
                    <div class="fancybox-table-cell">
                        <input type="submit" value='<%=rb.getString("PAGE_PROPERTIES_SUBMIT") %>' />
                    </div>
                </div>
            </form>
            
            <!-- Sélectionner le layout -->
            <form action="<%=commandUrl %>" method="get" class="fancybox-form">
                <input type="hidden" name="action" value="changeLayout" />
                <input type="hidden" name="pageId" value="<%=currentPageId %>" />

                <div class="fancybox-table-row">
                    <div class="fancybox-table-cell label">
                        <p><%=rb.getString("PAGE_LAYOUT") %></p>
                    </div>
                    <div class="fancybox-table-cell">
                        <select name="newLayout">
                            <%
                            if (CollectionUtils.isNotEmpty(layoutsList)) {
                                if (StringUtils.isEmpty(currentLayout)) {
                                    %>
                            <option selected="selected" value=""><%=rb.getString("PAGE_DEFAULT_LAYOUT") %></option>
                                    <%
                                } else {
                                    %>
                            <option value=""><%=rb.getString("PAGE_DEFAULT_LAYOUT") %></option>
                                    <%
                                }
                                
                                for (PortalLayout portalLayout : layoutsList) {
                                    String portalLayoutName = portalLayout.getLayoutInfo().getName();
                                    if (StringUtils.isNotEmpty(currentLayout) && StringUtils.equals(currentLayout, portalLayoutName)) {
                                        %>
                            <option selected="selected" value="<%=portalLayoutName %>"><%=portalLayoutName %></option>
                                        <%
                                    } else {
                                        %>
                            <option value="<%=portalLayoutName %>"><%=portalLayoutName %></option>
                                        <%
                                    }
                                }
                            }
                            %>
                        </select>                        
                    </div>
                    <div class="fancybox-table-cell">
                        <input type="submit" value='<%=rb.getString("PAGE_LAYOUT_SUBMIT") %>' />
                    </div>
                </div>
            </form>
            
            <!-- Sélectionner le thème -->
            <form action="<%=commandUrl %>" method="get" class="fancybox-form">
                <input type="hidden" name="action" value="changeTheme" />
                <input type="hidden" name="pageId" value="<%=currentPageId %>" />
                
                <div class="fancybox-table-row">
                    <div class="fancybox-table-cell label">
                        <p><%=rb.getString("PAGE_THEME") %></p>
                    </div>
                    <div class="fancybox-table-cell">
                        <select name="newTheme">
                            <%
                            if (CollectionUtils.isNotEmpty(themesList)) {
                                if (StringUtils.isEmpty(currentTheme)) {
                                    %>
                            <option selected="selected" value=""><%=rb.getString("PAGE_DEFAULT_THEME") %></option>
                                    <%
                                } else {
                                    %>
                            <option value=""><%=rb.getString("PAGE_DEFAULT_THEME") %></option>
                                    <%
                                }
                                
                                for (PortalTheme theme : themesList) {
                                    String themeName = theme.getThemeInfo().getName();
                                    if (StringUtils.isNotEmpty(currentTheme) && StringUtils.equals(currentTheme, themeName)) {
                                        %>
                            <option selected="selected" value="<%=themeName %>"><%=themeName %></option>
                                        <%
                                    } else {
                                        %>
                            <option value="<%=themeName %>"><%=themeName %></option>
                                        <%
                                    }
                                }
                            }
                            %>
                        </select>                        
                    </div>
                    <div class="fancybox-table-cell">
                        <input type="submit" value='<%=rb.getString("PAGE_THEME_SUBMIT") %>' />
                    </div>
                </div>
            </form>
            
        </div>
    </div>
</div>


<!-- Fancybox de déplacement de la page -->
<div class="fancybox-content">
    <div id="page-order">
        <form action="<%=commandUrl %>" method="get" class="fancybox-form">
            <input type="hidden" name="action" value="changePageOrder" />
            <input type="hidden" name="pageId" value="<%=currentPageId %>" />
            <input type="hidden" name="jstreePageOrder" />
            
            <div class="fancybox-table">
                <div class="fancybox-table-row">                
                    <div class="fancybox-table-cell label required"><%=rb.getString("PAGE_ORDER") %></div>
                    <div class="fancybox-table-cell">
                        <label for="rename-page-filter"><%=rb.getString("JSTREE_FILTER") %></label>
                        <input id="rename-page-filter" type="text" onkeyup="jstreeSearch('jstreePageOrder', this.value)" />
                    </div>
                </div>
                <div class="fancybox-table-row">
                    <div class="fancybox-table-cell">&nbsp;</div>
                    <div class="fancybox-table-cell">
                        <div id="jstreePageOrder" class="jstree-select-unique">
                            <%=formatter.formatHtmlTreePortalObjects(currentPage, context, "jstreePageOrder", false, true, false) %>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="fancybox-center-content">
                <input type="submit" value='<%=rb.getString("PAGE_ORDER_SUBMIT") %>' />
                <input type="button" value='<%=rb.getString("CANCEL") %>' onclick="closeFancybox()" />
            </div>
        </form>
    </div>
</div>


<!-- Fancybox de configuration des droits de la page -->
<div class="fancybox-content">
    <div id="page-rights">
        <form action="<%=commandUrl %>" method="get" class="fancybox-form">
            <input type="hidden" name="action" value="securePage" />
            <input type="hidden" name="pageId" value="<%= currentPageId %>" />
            
            <div class="fancybox-table">
                <div class="fancybox-table-header">
                    <div class="fancybox-table-cell label"><%=rb.getString("PAGE_ROLES") %></div>
                    <div class="fancybox-table-cell"><%=rb.getString("PAGE_ACCESS") %></div>
                </div>
                <%
                if (CollectionUtils.isNotEmpty(roles)) {
                    for (Role role : roles) {
                        Set<String> actions = null;
                        if (actionsForRoles.containsKey(role.getName())){
                            actions = actionsForRoles.get(role.getName());
                        }
                        String checked = StringUtils.EMPTY;
                        if (CollectionUtils.isNotEmpty(actions) && actions.contains("view")) {
                            checked = "checked=\"checked\"";
                        }
                        %>
                        <div class="fancybox-table-row">
                            <div class="fancybox-table-cell label">
                                <p><%=role.getDisplayName() %></p>
                            </div>
                            <div class="fancybox-table-cell">
                                <input type="checkbox" name="view" value="<%=role.getName() %>" class="small-input" <%=checked %> />
                            </div>
                        </div>
                        <%
                    }
                }
                %>
            </div>
            
            <div class="fancybox-center-content">
                <input type="submit" value='<%=rb.getString("PAGE_RIGHTS_SUBMIT") %>' />
                <input type="button" value='<%=rb.getString("CANCEL") %>' onclick="closeFancybox()" />
            </div>
        </form>
    </div>
</div>


<!-- Fancybox de configuration CMS -->
<div class="fancybox-content">
    <div id="page-cms">
        <form id="formCMSProperties" action="<%=commandUrl %>" method="get" class="fancybox-form">
            <input type="hidden" name="action" value="changeCMSProperties" />
            <input type="hidden" name="pageId" value="<%= currentPageId %>" />
            
            <div class="fancybox-table">
                <div class="fancybox-table-row">
                    <div class="fancybox-table-cell label">
                        <p><%=rb.getString("PAGE_CMS_SCOPE") %></p>
                    </div>
                    <div class="fancybox-table-cell">
                        <p><%=cmsScopeSelect %></p>
                    </div>
                </div>
                
                <div class="fancybox-table-row">
                    <div class="fancybox-table-cell label">
                        <p><%=rb.getString("PAGE_CMS_VERSION") %></p>
                    </div>
                    <div class="fancybox-table-cell">
                        <p><%=cmsDisplayLiveVersion %></p>
                    </div>
                </div>
                
                <div class="fancybox-table-row">
                    <div class="fancybox-table-cell label">
                        <p><%=rb.getString("PAGE_CMS_CONTEXTUALIZATION") %></p>
                    </div>
                    <div class="fancybox-table-cell">
                        <p><%=cmsRecontextualizationSupport %></p>
                    </div>
                </div>
                
                <div class="fancybox-table-row">
                    <div class="fancybox-table-cell label">
                        <p><%=rb.getString("PAGE_CMS_PATH") %></p>
                    </div>
                    <div class="fancybox-table-cell">
                        <input type="text" name="cmsBasePath" value="<%=cmsBasePath %>" onKeyup="disableOrNotPreviousFormValues(this);" onBlur="disableOrNotPreviousFormValues(this);" />
                    </div>
                </div>
                
                <div class="fancybox-table-row">
                    <div class="fancybox-table-cell label">
                        <p><%=rb.getString("PAGE_CMS_UNDER_SECTION_NAVIGATION_DISPLAY") %></p>
                    </div>
                    <div class="fancybox-table-cell">
                        <input type="checkbox" name="cmsNavigationMode" value="1" class="small-input" <%=checkCmsNavigationMode %> />
                    </div>                    
                </div>                
            </div>
            
            <div class="fancybox-center-content">
                <input type="submit" value='<%=rb.getString("PAGE_CMS_SUBMIT") %>' onMouseOver="disableOrNotPreviousFormValues(this.form['cmsBasePath']);" />
                <input type="button" value='<%=rb.getString("CANCEL") %>' onclick="closeFancybox()" />
            </div>
        </form>
    </div>
</div>


<!-- Fancybox de suppression de la page -->
<div class="fancybox-content">
    <div id="page-suppression">
        <form action="<%=deletePageCommandUrl %>" method="post" class="fancybox-form">
            <div class="fancybox-center-content">
                <p><%=rb.getString("PAGE_SUPPRESSION_CONFIRM_MESSAGE") %></p>
            </div>
            <div class="fancybox-center-content">
                <input type="submit" value='<%=rb.getString("PAGE_SUPPRESSION_SUBMIT") %>' />
                <input type="button" value='<%=rb.getString("PAGE_SUPPRESSION_CANCEL") %>' onclick="closeFancybox()" />
            </div>
        </form>
    </div>
</div>


<!-- Fancybox de liste des pages -->
<div class="fancybox-content">
    <div id="pages-list">
        <div class="fancybox-table">
            <div class="fancybox-table-row">
                <div class="fancybox-table-cell">
                    <label for="pages-list-filter"><%=rb.getString("JSTREE_FILTER") %></label>
                    <input id="pages-list-filter" type="text" onkeyup="jstreeSearch('jstreePagesList', this.value)" class="filter" />
                </div>
            </div>
            <div class="fancybox-table-row">
                <div class="fancybox-table-cell">
                    <div id="jstreePagesList" class="jstree-links">
                        <%=formatter.formatHtmlTreePortalObjects(currentPage, context, "jstreePagesList", false, false, true) %>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
