<%@page import="org.osivia.portal.api.internationalization.IInternationalizationService"%>
<%@page import="org.osivia.portal.core.constants.InternalConstants"%>
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
<%@page import="org.jboss.portal.core.model.portal.Window"%>

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>


<%
//Internationalization service
IInternationalizationService is = (IInternationalizationService) request.getAttribute(InternalConstants.ATTR_INTERNATIONALIZATION_SERVICE);

// Formatter
IFormatter formatter = (IFormatter) request.getAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_FORMATTER);
// Controller context
ControllerContext context = (ControllerContext) request.getAttribute(InternalConstants.ATTR_CONTROLLER_CONTEXT);

// Generic command URL
String commandUrl = (String) request.getAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_COMMAND_URL);
// Current page
Page currentPage = (Page) request.getAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_PAGE);
// CMS templated
Boolean cmsTemplated = (Boolean) request.getAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_CMS_TEMPLATED);
// Draft page
Boolean draftPage = (Boolean) request.getAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_DRAFT_PAGE);
// Layout list
@SuppressWarnings("unchecked")
List<PortalLayout> layoutsList = (List<PortalLayout>) request.getAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_LAYOUTS_LIST);
// Current layout
String currentLayout = (String) request.getAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_CURRENT_LAYOUT);
//Themes list
@SuppressWarnings("unchecked")
List<PortalTheme> themesList = (List<PortalTheme>) request.getAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_THEMES_LIST);
// Current theme
String currentTheme = (String) request.getAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_CURRENT_THEME);
//Current category
String currentCategory = (String) request.getAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_PAGE_CUR_CATEGORY);
//Categories list
@SuppressWarnings("unchecked")
Map<String, String> categoriesList = (Map<String, String>) request.getAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_PAGE_CATEGORIES);
// Roles
@SuppressWarnings("unchecked")
List<Role> roles = (List<Role>) request.getAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_ROLES);
// Actions for roles
@SuppressWarnings("unchecked")
Map<String, Set<String>> actionsForRoles = (Map<String, Set<String>>) request.getAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_ACTIONS_FOR_ROLES);
// Delete page command URL
String deletePageCommandUrl = (String) request.getAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_DELETE_PAGE_COMMAND_URL);
// CMS scope select
String cmsScopeSelect = (String) request.getAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_CMS_SCOPE_SELECT);
// CMS display live version
String cmsDisplayLiveVersion = (String) request.getAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_CMS_DISPLAY_LIVE_VERSION);   
// CMS recontextualization support
String cmsRecontextualizationSupport = (String) request.getAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_CMS_RECONTEXTUALIZATION_SUPPORT);
// CMS base path
String cmsBasePath = (String) request.getAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_CMS_BASE_PATH);
// Current page windows list
@SuppressWarnings("unchecked")
List<Window> windows = (List<Window>) request.getAttribute(InternalConstants.ATTR_WINDOWS_CURRENT_LIST);

// CMS templated disabled configurations
String disabledCMSTemplated = StringUtils.EMPTY;
if (BooleanUtils.isTrue(cmsTemplated)) {
    disabledCMSTemplated = "disabled";
}

// Draft page checkbox value
String checkDraft = StringUtils.EMPTY;
if (BooleanUtils.isTrue(draftPage)) {
    checkDraft = "checked";
}

// Template disabled configurations
String disabledTemplate = StringUtils.EMPTY;
if (PortalObjectUtils.isTemplate(currentPage)) {
    disabledTemplate = "disabled";
}


// Locales
Locale locale = request.getLocale();
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

var cmsPath = '<%=request.getAttribute(InternalConstants.ATTR_CMS_PATH) %>';
var commandPrefix = '<%=request.getAttribute(InternalConstants.ATTR_COMMAND_PREFIX) %>';

var portalId = '<%=portalId %>';
var currentPageId = '<%=currentPageId %>';

// Variables filled when opening fancybox
var regionId;
var windowId;


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

// Onclick action for add portlet formulaire submit
function selectPortlet(instanceId, formulaire) {
    formulaire.instanceId.value = instanceId;
    formulaire.regionId.value = regionId;
}

// Onclick action for delete portlet formulaire submit
function selectWindow(formulaire) {
    formulaire.windowId.value = windowId;
}

// Toggle row display
function toggleRow(link, divClass) {
    var divToggleRow = $JQry(link).parents(".fancybox-table-row").siblings("." + divClass)[0];
    if (undefined != divToggleRow) {
        if (divToggleRow.style.display == "none") {
            divToggleRow.style.display = "table-row";           
        } else {
            divToggleRow.style.display = "none";
        }
        parent.jQuery.fancybox.update();
    }   
}

</script>



<!-- Fancybox de création de page -->
<div class="fancybox-content">
    <div id="page-creation">
        <form action="<%=commandUrl %>" method="get" class="fancybox-form">
            <input type="hidden" name="action" value="createPage" />
            <input type="hidden" name="jstreePageParentSelect" value="<%=portalId %>" />
            <input type="hidden" name="jstreePageModelSelect" />

            <div class="fancybox-table">
                <div class="fancybox-table-row">
                    <div class="fancybox-table-cell fancybox-label required"><%=is.getString("NEW_PAGE_NAME", locale) %></div>
                    <div class="fancybox-table-cell">
                        <input type="text" name="name" required />
                    </div>
                    <div class="fancybox-table-cell">&nbsp;</div>
                    <div class="fancybox-table-cell">&nbsp;</div>
                </div>

                <div class="fancybox-table-row">
                    <div class="fancybox-table-cell fancybox-label"><%=is.getString("NEW_PAGE_MODEL", locale) %></div>
                    <div class="fancybox-table-cell">
                        <input type="text" onkeyup="jstreeSearch('jstreePageModelSelect', this.value)" class="filter" placeholder="<%=is.getString("JSTREE_FILTER", locale) %>" />                       
                    </div>
                
                    <div class="fancybox-table-cell fancybox-label required"><%=is.getString("NEW_PAGE_PARENT", locale) %></div>
                    <div class="fancybox-table-cell">
                        <input type="text" onkeyup="jstreeSearch('jstreePageParentSelect', this.value)" class="filter" placeholder="<%=is.getString("JSTREE_FILTER", locale) %>" />                                               
                    </div>                    
                </div>
                
                <div class="fancybox-table-row">
                    <div class="fancybox-table-cell fancybox-label fancybox-upper">
                        <label for="checkboxNoModel"><%=is.getString("NEW_PAGE_NO_MODEL", locale) %></label>                        
                        <input id="checkboxNoModel" type="checkbox" onchange="jstreeToggleLock('jstreePageModelSelect', this.checked)" checked="checked" class="inline-checkbox" />
                    </div>
                    
                    <div class="fancybox-table-cell">
                        <div id="jstreePageModelSelect" class="jstree-select-unique locked">
                            <%=formatter.formatHTMLTreeModels(currentPage, context, "jstreePageModelSelect") %>
                        </div>
                    </div>
                
                    <div class="fancybox-table-cell">&nbsp;</div>
                    
                    <div class="fancybox-table-cell">
                        <div id="jstreePageParentSelect" class="jstree-select-unique">
                            <%=formatter.formatHTMLTreePageParent(currentPage, context, "jstreePageParentSelect") %>
                        </div>                        
                    </div>
                </div>
            </div>

            <div class="fancybox-center-content">
                <input type="submit" value='<%=is.getString("NEW_PAGE_SUBMIT", locale) %>' />
                <input type="button" value='<%=is.getString("CANCEL", locale) %>' onclick="closeFancybox()" />
            </div>
        </form>
    </div>
</div>


<!-- Fancybox de création de template -->
<div class="fancybox-content">
    <div id="template-creation">
        <form action="<%=commandUrl %>" method="get" class="fancybox-form">
            <input type="hidden" name="action" value="createTemplate" />
            <input type="hidden" name="jstreeTemplateParentSelect" value="<%=portalId %>" />
            <input type="hidden" name="jstreeTemplateModelSelect" />

            <div class="fancybox-table">
                <div class="fancybox-table-row">
                    <div class="fancybox-table-cell fancybox-label required"><%=is.getString("NEW_TEMPLATE_NAME", locale) %></div>
                    <div class="fancybox-table-cell">
                        <input type="text" name="name" required />
                    </div>
                    <div class="fancybox-table-cell">&nbsp;</div>
                    <div class="fancybox-table-cell">&nbsp;</div>
                </div>

                <div class="fancybox-table-row">
                    <div class="fancybox-table-cell fancybox-label"><%=is.getString("NEW_TEMPLATE_MODEL", locale) %></div>
                    <div class="fancybox-table-cell">
                        <input type="text" onkeyup="jstreeSearch('jstreeTemplateModelSelect', this.value)" class="filter" placeholder="<%=is.getString("JSTREE_FILTER", locale) %>" />                       
                    </div>
                
                    <div class="fancybox-table-cell fancybox-label required"><%=is.getString("NEW_TEMPLATE_PARENT", locale) %></div>
                    <div class="fancybox-table-cell">
                        <input type="text" onkeyup="jstreeSearch('jstreeTemplateParentSelect', this.value)" class="filter" placeholder="<%=is.getString("JSTREE_FILTER", locale) %>" />                                               
                    </div>                    
                </div>
                
                <div class="fancybox-table-row">
                    <div class="fancybox-table-cell fancybox-label fancybox-upper">
                        <label for="checkboxNoModel"><%=is.getString("NEW_TEMPLATE_NO_MODEL", locale) %></label>                        
                        <input id="checkboxNoModel" type="checkbox" onchange="jstreeToggleLock('jstreeTemplateModelSelect', this.checked)" checked="checked" class="inline-checkbox" />
                    </div>
                    
                    <div class="fancybox-table-cell">
                        <div id="jstreeTemplateModelSelect" class="jstree-select-unique locked">
                            <%=formatter.formatHTMLTreeModels(currentPage, context, "jstreeTemplateModelSelect") %>
                        </div>
                    </div>
                
                    <div class="fancybox-table-cell">&nbsp;</div>
                    
                    <div class="fancybox-table-cell">
                        <div id="jstreeTemplateParentSelect" class="jstree-select-unique">
                            <%=formatter.formatHTMLTreeTemplateParent(currentPage, context, "jstreeTemplateParentSelect") %>
                        </div>                        
                    </div>
                </div>
            </div>

            <div class="fancybox-center-content">
                <input type="submit" value='<%=is.getString("NEW_TEMPLATE_SUBMIT", locale) %>' />
                <input type="button" value='<%=is.getString("CANCEL", locale) %>' onclick="closeFancybox()" />
            </div>
        </form>
    </div>
</div>


<!-- Fancybox de propriétés de la page -->
<div class="fancybox-content">
    <div id="page-properties">
        <form action="<%=commandUrl %>" method="get" class="fancybox-form">
            <input type="hidden" name="action" value="changePageProperties" />
            <input type="hidden" name="pageId" value="<%=currentPageId %>" />
        
            <div class="fancybox-table">
                        
                <!-- Renommer la page -->
                <div class="fancybox-table-row">
                    <div class="fancybox-table-cell fancybox-label required"><%=is.getString("PAGE_NAME", locale) %></div>
                    <div class="fancybox-table-cell">
                        <input type="text" name="displayName" value="<%=currentPageName %>" required />                    
                    </div>
                </div>
            
                <!-- Mode brouillon -->
                <div class="fancybox-table-row">
                    <div class="fancybox-table-cell fancybox-label"><%=is.getString("PAGE_DRAFT_MODE", locale) %></div>
                    <div class="fancybox-table-cell">
                        <input type="checkbox" name="draftPage" value="1" <%=checkDraft %> <%=disabledCMSTemplated %> class="small-input" />                        
                    </div>
                </div>
                
                <!-- Sélection du layout -->
                <div class="fancybox-table-row">
                    <div class="fancybox-table-cell fancybox-label"><%=is.getString("PAGE_LAYOUT", locale) %></div>
                    <div class="fancybox-table-cell">
                        <select name="newLayout" <%=disabledCMSTemplated %>>
                            <%
                            if (CollectionUtils.isNotEmpty(layoutsList)) {
                                if (StringUtils.isEmpty(currentLayout)) {
                                    %>
                            <option selected="selected" value=""><%=is.getString("PAGE_DEFAULT_LAYOUT", locale) %></option>
                                    <%
                                } else {
                                    %>
                            <option value=""><%=is.getString("PAGE_DEFAULT_LAYOUT", locale) %></option>
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
                </div>
                
                <!-- Sélection du thème -->
                <div class="fancybox-table-row">
                    <div class="fancybox-table-cell fancybox-label"><%=is.getString("PAGE_THEME", locale) %></div>
                    <div class="fancybox-table-cell">
                        <select name="newTheme" <%=disabledCMSTemplated %>>
                            <%
                            if (CollectionUtils.isNotEmpty(themesList)) {
                                if (StringUtils.isEmpty(currentTheme)) {
                                    %>
                            <option selected="selected" value=""><%=is.getString("PAGE_DEFAULT_THEME", locale) %></option>
                                    <%
                                } else {
                                    %>
                            <option value=""><%=is.getString("PAGE_DEFAULT_THEME", locale) %></option>
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
                </div>
                
                              <!-- Sélection de la catégorie -->
                <div class="fancybox-table-row">
                    <div class="fancybox-table-cell fancybox-label"><%=is.getString("PAGE_CATEGORY", locale) %></div>
                    <div class="fancybox-table-cell">
                        <select name="pageCategory" <%=disabledCMSTemplated %>>
                            <%
                            if (categoriesList != null) {
                                for (String possibleCategory : categoriesList.keySet()) {
                                    String selected = "";
                                    
                					if( possibleCategory.equals( currentCategory))	{
                						selected = "selected=\"selected\"";
                					}
                			%>		

                             <option  <%=selected %> value="<%= possibleCategory %>"><%= categoriesList.get(possibleCategory) %></option>
                                        <%
                                }
                            }
                            %>
                        </select>                        
                    </div>
                </div>
  
  
            </div>
            
            <!-- CMS templated message -->
            <%
            if (BooleanUtils.isTrue(cmsTemplated)) {
                %>
                <div class="fancybox-center-content">
                    <p><%=is.getString("PAGE_CMS_TEMPLATED_PROPERTIES_DISABLED", locale) %></p>
                </div>
                <%
            }
            %>
            
            <div class="fancybox-center-content">
                <input type="submit" value='<%=is.getString("CHANGE", locale) %>' />
                <input type="button" value='<%=is.getString("CANCEL", locale) %>' onclick="closeFancybox()" />
            </div>
        </form>
    </div>
</div>


<!-- Fancybox de déplacement de la page -->
<div class="fancybox-content">
    <div id="page-location">
        <form action="<%=commandUrl %>" method="get" class="fancybox-form">
            <input type="hidden" name="action" value="changePageOrder" />
            <input type="hidden" name="pageId" value="<%=currentPageId %>" />
            <input type="hidden" name="jstreePageOrder" />
            
            <div class="fancybox-table">
                <div class="fancybox-table-row">                
                    <div class="fancybox-table-cell fancybox-label"><%=is.getString("PAGE_ORDER", locale) %></div>
                </div>
                <div class="fancybox-table-row">
                    <div class="fancybox-table-cell">
                        <input type="text" onkeyup="jstreeSearch('jstreePageOrder', this.value)" class="filter" placeholder="<%=is.getString("JSTREE_FILTER", locale) %>" />
                    </div>
                </div>
                <div class="fancybox-table-row">
                    <div class="fancybox-table-cell">
                        <div id="jstreePageOrder" class="jstree-select-unique">
                            <%=formatter.formatHTMLTreePortalObjectsMove(currentPage, context, "jstreePageOrder") %>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="fancybox-center-content">
                <input type="submit" value='<%=is.getString("PAGE_ORDER_SUBMIT", locale) %>' />
                <input type="button" value='<%=is.getString("CANCEL", locale) %>' onclick="closeFancybox()" />
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
                    <div class="fancybox-table-cell fancybox-label"><%=is.getString("PAGE_ROLES", locale) %></div>
                    <div class="fancybox-table-cell"><%=is.getString("PAGE_ACCESS", locale) %></div>
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
                            <div class="fancybox-table-cell fancybox-label">
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
                <input type="submit" value='<%=is.getString("PAGE_RIGHTS_SUBMIT", locale) %>' />
                <input type="button" value='<%=is.getString("CANCEL", locale) %>' onclick="closeFancybox()" />
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
                    <div class="fancybox-table-cell fancybox-label">
                        <p><%=is.getString("PAGE_CMS_PATH", locale) %></p>
                    </div>
                    <div class="fancybox-table-cell">
                        <input type="text" name="cmsBasePath" value="<%=cmsBasePath %>" onKeyup="disableOrNotPreviousFormValues(this);" onBlur="disableOrNotPreviousFormValues(this);" <%=disabledTemplate %> />
                    </div>
                </div>
            
                <div class="fancybox-table-row">
                    <div class="fancybox-table-cell fancybox-label">
                        <p><%=is.getString("PAGE_CMS_SCOPE", locale) %></p>
                    </div>
                    <div class="fancybox-table-cell">
                        <p><%=cmsScopeSelect %></p>
                    </div>
                </div>
                
                <div class="fancybox-table-row">
                    <div class="fancybox-table-cell fancybox-label">
                        <p><%=is.getString("PAGE_CMS_VERSION", locale) %></p>
                    </div>
                    <div class="fancybox-table-cell">
                        <p><%=cmsDisplayLiveVersion %></p>
                    </div>
                </div>
                
                <div class="fancybox-table-row">
                    <div class="fancybox-table-cell fancybox-label">
                        <p><%=is.getString("PAGE_CMS_CONTEXTUALIZATION", locale) %></p>
                    </div>
                    <div class="fancybox-table-cell">
                        <p><%=cmsRecontextualizationSupport %></p>
                    </div>
                </div>
            
            </div>
            
            <div class="fancybox-center-content">
                <input type="submit" value='<%=is.getString("PAGE_CMS_SUBMIT", locale) %>' onMouseOver="disableOrNotPreviousFormValues(this.form['cmsBasePath']);" />
                <input type="button" value='<%=is.getString("CANCEL", locale) %>' onclick="closeFancybox()" />
            </div>
        </form>
    </div>
</div>


<!-- Fancybox de suppression de la page -->
<div class="fancybox-content">
    <div id="page-suppression">
        <form action="<%=deletePageCommandUrl %>" method="post" class="fancybox-form">
            <div class="fancybox-center-content">
                <p><%=is.getString("PAGE_SUPPRESSION_CONFIRM_MESSAGE", locale) %></p>
            </div>
            <div class="fancybox-center-content">
                <input type="submit" value='<%=is.getString("YES", locale) %>' />
                <input type="button" value='<%=is.getString("NO", locale) %>' onclick="closeFancybox()" />
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
                    <input type="text" onkeyup="jstreeSearch('jstreePagesList', this.value)" class="filter" placeholder="<%=is.getString("JSTREE_FILTER", locale) %>" />
                </div>
            </div>
            <div class="fancybox-table-row">
                <div class="fancybox-table-cell">
                    <div id="jstreePagesList" class="jstree-links">
                        <%=formatter.formatHTMLTreePortalObjectsAlphaOrder(currentPage, context, "jstreePagesList") %>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Fancybox d'ajout de portlet -->
<div class="fancybox-content">
    <div id="add-portlet">
        <form action="<%=commandUrl %>" method="get" class="fancybox-form">
            <input type="hidden" name="action" value="addPortlet" />
            <input type="hidden" name="pageId" value="<%=currentPageId %>" />
            <input type="hidden" name="regionId" />
            <input type="hidden" name="instanceId" />

            <%=formatter.formatHtmlPortletsList(context) %>
            
            <div class="fancybox-center-content">
                <input type="button" value='<%=is.getString("CANCEL", locale) %>' onclick="closeFancybox()" />
            </div>
        </form>
    </div>
</div>


<!-- Fancyboxes d'affichage des paramètres des fenêtres -->
<%=formatter.formatHtmlWindowsSettings(currentPage, windows, context) %>


<!-- Fancybox de suppression de portlet -->
<div class="fancybox-content">
    <div id="delete-portlet">
        <form action="<%=commandUrl %>" method="get" class="fancybox-form">
            <input type="hidden" name="action" value="deleteWindow" />
            <input type="hidden" name="windowId" />
            
            <div class="fancybox-center-content">
                <p><%=is.getString("PORTLET_SUPPRESSION_CONFIRM_MESSAGE", locale) %></p>
            </div>
            <div class="fancybox-center-content">
                <input type="submit" value='<%=is.getString("YES", locale) %>' onclick="selectWindow(this.form)" />
                <input type="button" value='<%=is.getString("NO", locale) %>' onclick="closeFancybox()" />
            </div>
        </form>
    </div>
</div>
