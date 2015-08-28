<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="internationalization" prefix="is"%>
<%@ taglib uri="/WEB-INF/tld/formatter.tld" prefix="formatter" %>


<c:set var="commandUrl" value="${requestScope['osivia.toolbarSettings.commandURL']}" />
<c:set var="currentPage" value="${requestScope['osivia.toolbarSettings.page']}" />
<c:set var="portalId"><formatter:safeId portalObjectId="${currentPage.portal.id}" /></c:set>
<c:set var="currentPageId"><formatter:safeId portalObjectId="${currentPage.id}" /></c:set>
<c:set var="currentPageDisplayName"><formatter:displayName object="${currentPage}"/></c:set>

<c:if test="${requestScope['osivia.toolbarSettings.draftPage']}">
</c:if>

<c:if test="${requestScope['osivia.toolbarSettings.selectorsPropagation']}">
    <c:set var="selectorsPropationChecked" value="checked" />
</c:if>

<c:if test="${requestScope['osivia.toolbarSettings.cmsTemplated']}">
    <c:set var="propertiesDisabled" value="disabled" />
</c:if>


<script type="text/javascript">
// CMS path and command prefix, required for drag&drop in webpage mode
var cmsPath = "${requestScope['osivia.cms.path']}";
var commandPrefix = "${requestScope['osivia.command.prefix']}";

// Variables required for JStree integration
var portalId = '${portalId}';
var currentPageId = '${currentPageId}';
</script>


<!-- Administrator content -->
<c:if test="${requestScope['osivia.user.administrator']}">
    <script type="text/javascript">
    // Variables filled when opening fancybox
    var regionId;
    var windowId;
    
    // Toggle CMS properties
    function toggleCMS() {
        var form = document.getElementById("formCMSProperties");
        var cms = form.elements["cmsBasePath"];
        var disabled = ("" != cms.value);
        
        var fieldset = document.getElementById("fieldsetCMSProperties");
        fieldset.disabled = disabled;
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
    
    
    function toggleBeanShell(checkbox) {
    	var $content = $JQry("#" + checkbox.id + "-content");
    	if (checkbox.checked) {
    		$content.collapse("show");
    	} else {
    		$content.collapse("hide");
    	}
    }
    
    // Window properties : mobile collapse check event
    $JQry(document).ready(function() {
    	$JQry("input[name=displayTitle], input[name=bootstrapPanelStyle], input[name=mobileCollapse]").change(function() {
            var $form = $JQry(this).parents("form");
            var $displayTitle = $form.find("input[name=displayTitle]");
            var displayTitle = $displayTitle.is(":checked");
            var $displayTitleDecorators = $form.find("input[name=displayDecorators]");
            var $displayPanel = $form.find("input[name=bootstrapPanelStyle]");
            var displayPanel = $displayPanel.is(":checked");
            var $panelCollapse = $form.find("input[name=mobileCollapse]");
            var panelCollapse = $panelCollapse.is(":checked");
            
            // Title decorators
            if (!displayTitle) {
            	$displayTitleDecorators.prop("checked", false);
            }
            $displayTitleDecorators.prop("disabled", !displayTitle);
            
            // Panel collapse
            if (panelCollapse) {
            	$displayTitle.prop("checked", true);
                $displayPanel.prop("checked", true);
            }
            $displayTitle.prop("disabled", panelCollapse);
            $displayPanel.prop("disabled", panelCollapse);
            $panelCollapse.prop("disabled", !(displayTitle && displayPanel));
    	});
    });
    </script>


    <div class="hidden">
    
        <!-- Page creation -->
        <div id="page-creation" class="container">
            <form action="${commandUrl}" method="get" class="form-horizontal" role="form">
                <input type="hidden" name="action" value="createPage" />
                <input type="hidden" name="jstreePageParentSelect" value="${portalId}" />
                <input type="hidden" name="jstreePageModelSelect" />
            
                <!-- Name -->
                <div class="form-group">
                    <label for="new-page-name" class="control-label required col-sm-4 col-lg-3"><is:getProperty key="NEW_PAGE_NAME" /></label>
                    <div class="col-sm-8 col-lg-3">
                        <input id="new-page-name" type="text" name="name" class="form-control" placeholder='<is:getProperty key="NEW_PAGE_NAME" />' required="required" />
                    </div>
                </div>
                
                <div class="form-group">
                    <!-- Model -->
                    <label class="control-label col-sm-4 col-lg-3"><is:getProperty key="NEW_PAGE_MODEL" /></label>
                    <div class="col-sm-8 col-lg-3">
                        <div class="checkbox">
                            <label>
                                <input type="checkbox" onchange="jstreeToggleLock('jstreePageModelSelect', this.checked)" />
                                <is:getProperty key="NEW_PAGE_NO_MODEL" />
                            </label>
                        </div>
                        <div class="well">
                            <div class="jstree-filter input-group input-group-sm">
                                <span class="input-group-addon"><i class="halflings halflings-filter"></i></span>
                                <input type="text" class="form-control" onkeyup="jstreeSearch('jstreePageModelSelect', this.value)" placeholder='<is:getProperty key="JSTREE_FILTER" />' />
                            </div>
                            <div id="jstreePageModelSelect" class="jstree-select-unique">
                                <formatter:tree id="jstreePageModelSelect" type="model" />
                            </div>
                        </div>
                    </div>
                    
                    <!-- Parent -->
                    <label class="control-label col-sm-4 col-lg-3"><is:getProperty key="NEW_PAGE_PARENT" /></label>
                    <div class="col-sm-8 col-lg-3">
                        <div class="well">
                            <div class="jstree-filter input-group input-group-sm">
                                <span class="input-group-addon"><i class="halflings halflings-filter"></i></span>
                                <input type="text" class="form-control" onkeyup="jstreeSearch('jstreePageParentSelect', this.value)" placeholder='<is:getProperty key="JSTREE_FILTER" />' />
                            </div>
                            <div id="jstreePageParentSelect" class="jstree-select-unique">
                                <formatter:tree id="jstreePageParentSelect" type="parentPage" />
                            </div>
                        </div>
                    </div>
                </div>
            
                <div class="form-group">
                    <div class="col-sm-offset-4 col-lg-offset-3 col-sm-8">
                        <button type="submit" class="btn btn-default btn-primary"><is:getProperty key="NEW_PAGE_SUBMIT" /></button>
                        <button type="button" class="btn btn-default" onclick="closeFancybox()"><is:getProperty key="CANCEL" /></button>
                    </div>
                </div>
            </form>
        </div>
        
        <!-- Template creation -->
        <div id="template-creation" class="container">
            <form action="${commandUrl}" method="get" class="form-horizontal" role="form">
                <input type="hidden" name="action" value="createTemplate" />
                <input type="hidden" name="jstreeTemplateParentSelect" value="${portalId}" />
                <input type="hidden" name="jstreeTemplateModelSelect" />
                
                <!-- Name -->
                <div class="form-group">
                    <label for="new-template-name" class="control-label required col-sm-4 col-lg-3"><is:getProperty key="NEW_TEMPLATE_NAME" /></label>
                    <div class="col-sm-8 col-lg-3">
                        <input id="new-template-name" type="text" name="name" class="form-control" placeholder='<is:getProperty key="NEW_TEMPLATE_NAME" />' required="required" />
                    </div>
                </div>
                
                <div class="form-group">
                    <!-- Model -->
                    <label class="control-label col-sm-4 col-lg-3"><is:getProperty key="NEW_TEMPLATE_MODEL" /></label>
                    <div class="col-sm-8 col-lg-3">
                        <div class="checkbox">
                            <label>
                                <input type="checkbox" onchange="jstreeToggleLock('jstreeTemplateModelSelect', this.checked)" />
                                <is:getProperty key="NEW_TEMPLATE_NO_MODEL" />
                            </label>
                        </div>
                        <div class="well">
                            <div class="jstree-filter input-group input-group-sm">
                                <span class="input-group-addon"><i class="halflings halflings-filter"></i></span>
                                <input type="text" class="form-control" onkeyup="jstreeSearch('jstreeTemplateModelSelect', this.value)" placeholder='<is:getProperty key="JSTREE_FILTER" />' />
                            </div>
                            <div id="jstreeTemplateModelSelect" class="jstree-select-unique">
                                <formatter:tree id="jstreeTemplateModelSelect" type="model" />
                            </div>
                        </div>
                    </div>
                    
                    <!-- Parent -->
                    <label class="control-label col-sm-4 col-lg-3"><is:getProperty key="NEW_TEMPLATE_PARENT" /></label>
                    <div class="col-sm-8 col-lg-3">
                        <div class="well">
                            <div class="jstree-filter input-group input-group-sm">
                                <span class="input-group-addon"><i class="halflings halflings-filter"></i></span>
                                <input type="text" class="form-control" onkeyup="jstreeSearch('jstreeTemplateParentSelect', this.value)" placeholder='<is:getProperty key="JSTREE_FILTER" />' />
                            </div>
                            <div id="jstreeTemplateParentSelect" class="jstree-select-unique">
                                <formatter:tree id="jstreeTemplateParentSelect" type="parentTemplate" />
                            </div>
                        </div>
                    </div>
                </div>
            
                <div class="form-group">
                    <div class="col-sm-offset-4 col-lg-offset-3 col-sm-8">
                        <button type="submit" class="btn btn-default btn-primary"><is:getProperty key="NEW_TEMPLATE_SUBMIT" /></button>
                        <button type="button" class="btn btn-default" onclick="closeFancybox()"><is:getProperty key="CANCEL" /></button>
                    </div>
                </div>
            </form>
        </div>
        
        
        <!-- Properties -->
        <div id="page-properties" class="container">
            <form action="${commandUrl}" method="get" class="form-horizontal" role="form">
                <input type="hidden" name="action" value="changePageProperties" />
                <input type="hidden" name="pageId" value="${currentPageId}" />
                
                <!-- Name -->
                <div class="form-group">                
                    <label for="properties-page-name" class="control-label required col-sm-4"><is:getProperty key="PAGE_NAME" /></label>
                    <div class="col-sm-8">
                        <input id="properties-page-name" type="text" name="displayName" value="${currentPageDisplayName}" class="form-control" placeholder='<is:getProperty key="PAGE_NAME" />' required="required" />
                    </div>
                </div>
                
                <!-- Draft mode -->
                <div class="form-group">
                    <label for="properties-page-draft-mode" class="control-label col-sm-4"><is:getProperty key="PAGE_DRAFT_MODE" /></label>
                    <div class="col-sm-8">
                        <div class="checkbox">
                            <input id="properties-page-draft-mode" type="checkbox" name="draftPage" value="1" ${draftModeChecked} ${propertiesDisabled} />
                        </div>
                    </div>
                </div>
                
                <!-- Layout -->
                <div class="form-group">
                    <label for="properties-page-layout" class="control-label col-sm-4"><is:getProperty key="PAGE_LAYOUT" /></label>
                    <div class="col-sm-8">
                        <select id="properties-page-layout" name="newLayout" class="form-control" ${propertiesDisabled}>
                            <!-- Default layout -->
                            <c:if test="${empty requestScope['osivia.toolbarSettings.currentLayout']}">
                                <c:set var="defaultLayoutSelected" value="selected" />
                            </c:if> 
                            <option value="" ${defaultLayoutSelected}><is:getProperty key="PAGE_DEFAULT_LAYOUT" /></option>
                        
                            <!-- Layouts list -->
                            <c:forEach var="layout" items="${requestScope['osivia.toolbarSettings.layoutsList']}">
                                <c:if test="${requestScope['osivia.toolbarSettings.currentLayout'] eq layout.layoutInfo.name}">
                                    <c:set var="layoutSelected" value="selected" />
                                </c:if>
                                
                                <option value="${layout.layoutInfo.name}" ${layoutSelected}>${layout.layoutInfo.name}</option>
                                
                                <c:remove var="layoutSelected" />
                            </c:forEach>
                        </select>
                    </div>
                </div>
                
                <!-- Theme -->
                <div class="form-group">
                    <label for="properties-page-theme" class="control-label col-sm-4"><is:getProperty key="PAGE_THEME" /></label>
                    <div class="col-sm-8">
                        <select id="properties-page-theme" name="newTheme" class="form-control" ${propertiesDisabled}>
                            <!-- Default theme -->
                            <c:if test="${empty requestScope['osivia.toolbarSettings.currentTheme']}">
                                <c:set var="defaultThemeSelected" value="selected" />
                            </c:if> 
                            <option value="" ${defaultThemeSelected}><is:getProperty key="PAGE_DEFAULT_THEME" /></option>
                        
                            <!-- Themes list -->
                            <c:forEach var="theme" items="${requestScope['osivia.toolbarSettings.themesList']}">
                                <c:if test="${requestScope['osivia.toolbarSettings.currentTheme'] eq theme.themeInfo.name}">
                                    <c:set var="themeSelected" value="selected" />
                                </c:if>
                                
                                <option value="${theme.themeInfo.name}" ${themeSelected}>${theme.themeInfo.name}</option>
                                
                                <c:remove var="themeSelected" />
                            </c:forEach>
                        </select>
                    </div>
                </div>
                
                <!-- Category -->
                <div class="form-group">
                    <label for="properties-page-category" class="control-label col-sm-4"><is:getProperty key="PAGE_CATEGORY" /></label>
                    <div class="col-sm-8">
                        <select id="properties-page-category" name="pageCategory" class="form-control" ${propertiesDisabled}>
                            <c:forEach var="category" items="${requestScope['osivia.toolbarSettings.pageCategories']}">
                                <c:if test="${requestScope['osivia.toolbarSettings.pageCategory'] eq category.key}">
                                    <c:set var="categorySelected" value="selected" />
                                </c:if>                                                    
                                <option value="${category.key}" ${categorySelected}>${category.value}</option>
                                <c:remove var="categorySelected" />
                            </c:forEach>
                        </select>
                    </div>
                </div>
                
                 <!-- Selectors propagation mode -->
                <div class="form-group">
                    <label for="properties-page-draft-mode" class="control-label col-sm-4"><is:getProperty key="PAGE_SELECTOR_PROPAGATION" /></label>
                    <div class="col-sm-8">
                        <div class="checkbox">
                            <input id="properties-page-draft-mode" type="checkbox" name="selectorsPropagation" value="1" ${selectorsPropationChecked} ${propertiesDisabled} />
                        </div>
                    </div>
                </div>
                
                <div class="form-group">
                    <div class="col-sm-offset-4 col-sm-8">
                        <c:if test="${currentPageTemplateIndicator}">
                            <p class="help-block"><is:getProperty key="PAGE_CMS_TEMPLATED_PROPERTIES_DISABLED" /></p>
                        </c:if>
                        <button type="submit" class="btn btn-default btn-primary"><is:getProperty key="CHANGE" /></button>
                        <button type="button" class="btn btn-default" onclick="closeFancybox()"><is:getProperty key="CANCEL" /></button>
                    </div>
                </div>
            </form>
        </div>
        
        
        <!-- Move -->
        <div id="page-location" class="container-fluid">
            <form action="${commandUrl}" method="get" class="form-horizontal" role="form">
                <input type="hidden" name="action" value="changePageOrder" />
                <input type="hidden" name="pageId" value="${currentPageId}" />
                <input type="hidden" name="jstreePageOrder" />
                
                <div class="form-group">
                    <p class="help-block"><is:getProperty key="PAGE_ORDER" /></p>
                    <div class="well">
                        <div class="jstree-filter input-group input-group-sm">
                            <span class="input-group-addon"><i class="halflings halflings-filter"></i></span>
                            <input type="text" class="form-control" onkeyup="jstreeSearch('jstreePageOrder', this.value)" placeholder='<is:getProperty key="JSTREE_FILTER" />' />
                        </div>
                        <div id="jstreePageOrder" class="jstree-select-unique">
                            <formatter:tree id="jstreePageOrder" type="move" />
                        </div>
                    </div>
                </div>
                
                <div class="form-group">
                    <div class="col-sm-offset-4 col-sm-8">
                        <button type="submit" class="btn btn-default btn-primary"><is:getProperty key="PAGE_ORDER_SUBMIT" /></button>
                        <button type="button" class="btn btn-default" onclick="closeFancybox()"><is:getProperty key="CANCEL" /></button>
                    </div>
                </div>
            </form>
        </div>
        
        
        <!-- Rights -->
        <div id="page-rights" class="container-fluid">
            <form action="${commandUrl}" method="get" class="form-horizontal" role="form">
                <input type="hidden" name="action" value="securePage" />
                <input type="hidden" name="pageId" value="${currentPageId}" />
                
                <div class="form-group">
                    <table class="table table-condensed">
                        <thead>
                            <tr>
                                <th><is:getProperty key="PAGE_ROLES" /></th>
                                <th><is:getProperty key="PAGE_ACCESS" /></th>
                            </tr>
                        </thead>
                        
                        <tbody>
                            <c:forEach var="role" items="${requestScope['osivia.toolbarSettings.roles']}">
                                <c:set var="roleChecked" value="" />
                                <c:forEach var="item" items="${requestScope['osivia.toolbarSettings.actionsForRoles'][role.name]}">
                                    <c:if test='${item eq "view"}'>
                                        <c:set var="roleChecked" value="checked" />
                                    </c:if>
                                </c:forEach>
    
                                <tr>
                                    <td>${role.displayName}</td>
                                    <td>
                                        <input type="checkbox" name="view" value="${role.name}" ${roleChecked} />
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                
                <div class="form-group">
                    <div class="text-center">
                        <button type="submit" class="btn btn-default btn-primary"><is:getProperty key="PAGE_RIGHTS_SUBMIT" /></button>
                        <button type="button" class="btn btn-default" onclick="closeFancybox()"><is:getProperty key="CANCEL" /></button>
                    </div>
                </div>
            </form>
        </div>
        
        
        <!-- CMS -->
        <div id="page-cms" class="container">
            <form id="formCMSProperties" action="${commandUrl}" method="get" class="form-horizontal" role="form">
                <input type="hidden" name="action" value="changeCMSProperties" />
                <input type="hidden" name="pageId" value="${currentPageId}" />
                
                <!-- Path -->
                <div class="form-group">                
                    <label for="cms-path" class="control-label col-sm-4"><is:getProperty key="PAGE_CMS_PATH" /></label>
                    <div class="col-sm-8">
                        <input id="cms-path" type="text" name="cmsBasePath" value="${requestScope['osivia.toolbarSettings.cmsBasePath']}" onkeyup="toggleCMS()" class="form-control" placeholder="<is:getProperty key='PAGE_CMS_PATH' />" />
                    </div>
                </div>
                
                <fieldset id="fieldsetCMSProperties">
                    <!-- Scope -->
                    <div class="form-group">                
                        <label for="cms-scope" class="control-label col-sm-4"><is:getProperty key="PAGE_CMS_SCOPE" /></label>
                        <div class="col-sm-8">
                            <span>${requestScope['osivia.toolbarSettings.cmsScopeSelect']}</span>
                        </div>
                    </div>
                
                    <!-- Version -->
                    <div class="form-group">                
                        <label for="cms-version" class="control-label col-sm-4"><is:getProperty key="PAGE_CMS_VERSION" /></label>
                        <div class="col-sm-8">
                            <span>${requestScope['osivia.toolbarSettings.cmsDisplayLiveVersion']}</span>
                        </div>
                    </div>
                    
                    <!-- Contextualization -->
                    <div class="form-group">                
                        <label for="cms-contextualization" class="control-label col-sm-4"><is:getProperty key="PAGE_CMS_CONTEXTUALIZATION" /></label>
                        <div class="col-sm-8">
                            <span>${requestScope['osivia.toolbarSettings.cmsRecontextualizationSupport']}</span>
                        </div>
                    </div>
                </fieldset>
                
                <div class="form-group">
                    <div class="col-sm-offset-4 col-sm-8">
                        <button type="submit" class="btn btn-default btn-primary"><is:getProperty key="PAGE_CMS_SUBMIT" /></button>
                        <button type="button" class="btn btn-default" onclick="closeFancybox()"><is:getProperty key="CANCEL" /></button>
                    </div>
                </div>
            </form>
        </div>
        
        
        <!-- Delete page -->
        <div id="page-suppression" class="container-fluid">
            <form action="${commandUrl}" method="get" class="form-horizontal" role="form">
                <input type="hidden" name="action" value="deletePage" />
                <input type="hidden" name="pageId" value="${currentPageId}" />            
                <div class="form-group">
                    <p><is:getProperty key="PAGE_SUPPRESSION_CONFIRM_MESSAGE" /></p>
                    <div class="text-center">
                        <button type="submit" class="btn btn-warning">
                            <i class="halflings halflings-alert"></i>
                            <span><is:getProperty key="YES" /></span>
                        </button>
                        <button type="button" class="btn btn-default" onclick="closeFancybox()"><is:getProperty key="NO" /></button>
                    </div>
                </div>
            </form>
        </div>
        
        
        <!-- Elements list -->
        <div id="pages-list" class="container-fluid">
            <div class="well">
                <div class="jstree-filter input-group input-group-sm">
                    <span class="input-group-addon"><i class="halflings halflings-filter"></i></span>
                    <input type="text" class="form-control" onkeyup="jstreeSearch('jstreePagesList', this.value)" placeholder='<is:getProperty key="JSTREE_FILTER" />' />
                </div>
                <div id="jstreePagesList" class="jstree-links">
                    <formatter:tree id="jstreePagesList" type="alphaOrder" />
                </div>
            </div>
        </div>
        
        
        <!-- Add portlet -->
        <div id="add-portlet" class="container-fluid">
            <form action="${commandUrl}" method="get" class="form-horizontal" role="form">
                <input type="hidden" name="action" value="addPortlet" />
                <input type="hidden" name="pageId" value="${currentPageId}" />
                <input type="hidden" name="regionId" />
                <input type="hidden" name="instanceId" />
    
                <formatter:portletsList />
                
                <div class="form-group">
                    <div class="text-center">
                        <button type="button" class="btn btn-default" onclick="closeFancybox()"><is:getProperty key="CANCEL" /></button>
                    </div>
                </div>
            </form>
        </div>
        
        
        <!-- Delete portlet  -->
        <div id="delete-portlet" class="container-fluid">
            <form action="${commandUrl}" method="get" class="form-horizontal" role="form">
                <input type="hidden" name="action" value="deleteWindow" />
                <input type="hidden" name="windowId" />
    
                <p><is:getProperty key="PORTLET_SUPPRESSION_CONFIRM_MESSAGE" /></p>
                <div class="text-center">
                    <button type="submit" class="btn btn-warning"  onclick="selectWindow(this.form)">
                        <i class="halflings halflings-alert"></i>
                        <span><is:getProperty key="YES" /></span>
                    </button>
                    <button type="button" class="btn btn-default" onclick="closeFancybox()"><is:getProperty key="NO" /></button>
                </div>
            </form>
        </div>
        
        
        <!-- Windows settings -->
        <c:forEach var="window" items="${requestScope['osivia.toolbarSettings.windowSettings']}">
            <div id="window-settings-${window.id}" class="fancybox-bottom-controls">
                <div class="container">
                    <form action="${commandUrl}" method="get" class="form-horizontal" role="form">
                        <input type="hidden" name="action" value="changeWindowSettings">
                        <input type="hidden" name="windowId" value="${window.id}">
                        
                        <fieldset>
                            <legend>
                                <i class="glyphicons glyphicons-display"></i>
                                <span><is:getProperty key="WINDOW_PROPERTIES_DISPLAY" /></span>
                            </legend>
                            
                            <!-- Title -->                        
                            <div class="form-group">
                                <label for="${window.id}-title" class="control-label col-sm-3"><is:getProperty key="WINDOW_PROPERTIES_TITLE" /></label>
                                <div class="col-sm-9">
                                    <input id="${window.id}-title" type="text" name="title" value="${window.title}" class="form-control">
                                    
                                    <div class="checkbox">
                                        <label>
                                            <input type="checkbox" name="displayTitle" value="1" ${displayTitleChecked}
                                                <c:if test="${window.displayTitle or window.panelCollapse}">checked="checked"</c:if>
                                                <c:if test="${window.panelCollapse}">disabled="disabled"</c:if>
                                            >
                                            <span><is:getProperty key="WINDOW_PROPERTIES_TITLE_DISPLAY" /></span>
                                        </label>
                                    </div>
                                    
                                    <div class="checkbox">
                                        <label>
                                            <input type="checkbox" name="displayDecorators" value="1" ${displayDecoratorsChecked}
                                                <c:if test="${window.displayTitleDecorators}">checked="checked"</c:if>
                                                <c:if test="${not window.displayTitle}">disabled="disabled"</c:if>
                                            >
                                            <span><is:getProperty key="WINDOW_PROPERTIES_TITLE_MORE" /></span>
                                        </label>
                                    </div>
                                </div>
                            </div>
                            
                            <!-- Panel -->
                            <div class="form-group">
                                <label for="${window.id}-panel" class="control-label col-sm-3"><is:getProperty key="WINDOW_PROPERTIES_PANEL" /></label>
                                <div class="col-sm-9">
                                    <div class="checkbox">
                                        <label>
                                            <input id="${window.id}-panel" type="checkbox" name="bootstrapPanelStyle"
                                                <c:if test="${window.displayPanel or window.panelCollapse}">checked="checked"</c:if>
                                                <c:if test="${window.panelCollapse}">disabled="disabled"</c:if>
                                            >
                                            <span><is:getProperty key="WINDOW_PROPERTIES_PANEL_DISPLAY" /></span>
                                        </label>
                                    </div>
                                    
                                    <div class="checkbox">
                                        <label>
                                            <input type="checkbox" name="mobileCollapse"
                                                <c:if test="${window.panelCollapse}">checked="checked"</c:if>
                                                <c:if test="${not (window.displayTitle and window.displayPanel)}">disabled="disabled"</c:if>
                                            >
                                            <span><is:getProperty key="WINDOW_PROPERTIES_PANEL_COLLAPSE" /></span>
                                        </label>
                                    </div>
                                </div>
                            </div>
                            
                            <!-- Ajax -->
                            <div class="form-group">
                                <label for="${window.id}-ajax" class="control-label col-sm-3"><is:getProperty key="WINDOW_PROPERTIES_AJAX" /></label>
                                <div class="col-sm-9">
                                    <div class="checkbox">
                                        <label>
                                            <input id="${window.id}-ajax" type="checkbox" name="ajaxLink" value="1"
                                                <c:if test="${window.ajax}">checked="checked"</c:if>
                                            >
                                            <span><is:getProperty key="WINDOW_PROPERTIES_AJAX_ACTIVATE" /></span>
                                        </label>
                                    </div>
                                </div>
                            </div>
                            
                            <!-- Hide empty portlet -->
                            <div class="form-group">
                                <label for="${window.id}-hide-empty" class="control-label col-sm-3"><is:getProperty key="WINDOW_PROPERTIES_HIDE_EMPTY" /></label>
                                <div class="col-sm-9">
                                    <div class="checkbox">
                                        <label>
                                            <input id="${window.id}-hide-empty" type="checkbox" name="hideEmptyPortlet" value="1"
                                                <c:if test="${window.hideEmpty}">checked="checked"</c:if>
                                            >
                                            <span><is:getProperty key="WINDOW_PROPERTIES_HIDE_EMPTY_ACTIVATE" /></span>
                                        </label>
                                    </div>
                                </div>
                            </div>
                            
                            <!-- Print -->
                            <div class="form-group">
                                <label for="${window.id}-print" class="control-label col-sm-3"><is:getProperty key="WINDOW_PROPERTIES_PRINT" /></label>
                                <div class="col-sm-9">
                                    <div class="checkbox">
                                        <label>
                                            <input id="${window.id}-print" type="checkbox" name="printPortlet" value="1"
                                                <c:if test="${window.print}">checked="checked"</c:if>
                                            >
                                            <span><is:getProperty key="WINDOW_PROPERTIES_PRINT_ACTIVATE" /></span>
                                        </label>
                                    </div>
                                </div>
                            </div>
                            
                            <!-- Styles -->
                            <div class="form-group">
                                <label class="control-label col-sm-3"><is:getProperty key="WINDOW_PROPERTIES_STYLES" /></label>
                                <div class="col-sm-9">
                                    <div class="row">
                                        <c:forEach var="style" items="${window.styles}">
                                            <div class="col-sm-4 col-md-3">
                                                <label class="checkbox-inline text-overflow">
                                                    <input type="checkbox" name="style" value="${style.key}"
                                                        <c:if test="${style.value}">checked="checked"</c:if>
                                                    >
                                                    <span>${style.key}</span>
                                                </label>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>
                            </div>
                            
                        </fieldset>
                        
                        <fieldset>
                            <legend>
                                <i class="halflings halflings-dashboard"></i>
                                <span><is:getProperty key="WINDOW_PROPERTIES_ADVANCED_OPTIONS" /></span>
                            </legend>
                            
                            <!-- Scopes -->
                            <div class="form-group">
                                <label for="${window.id}-scopes" class="control-label col-sm-3"><is:getProperty key="WINDOW_PROPERTIES_SCOPE_DISPLAY" /></label>
                                <div class="col-sm-9">
                                    <select id="${window.id}-scopes" name="conditionalScope" class="form-control">
                                        <option value=""
                                            <c:if test="${empty window.selectedScope}">selected="selected"</c:if>
                                        ><is:getProperty key="WINDOW_PROPERTIES_SCOPE_ALL_PROFILES" /></option>
                                        
                                        <c:forEach var="scope" items="${window.scopes}">
                                            <option value="${scope.key}"
                                                <c:if test="${window.selectedScope eq scope.key}">selected="selected"</c:if>
                                            >${scope.value}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                            
                            <!-- Customization identifier -->
                            <div class="form-group">
                                <label for="${window.id}-custom-id" class="control-label col-sm-3"><is:getProperty key="WINDOW_PROPERTIES_CUSTOM_ID" /></label>
                                <div class="col-sm-9">
                                    <input id="${window.id}-custom-id" type="text" name="idPerso" value="${window.customizationId}" class="form-control">
                                </div>
                            </div>
                            
                            <!-- Shared cache identifier -->
                            <div class="form-group">
                                <label for="${window.id}-shared-cache-id" class="control-label col-sm-3"><is:getProperty key="WINDOW_PROPERTIES_SHARED_CACHE_ID" /></label>
                                <div class="col-sm-9">
                                    <input id="${window.id}-shared-cache-id" type="text" name="cacheID" value="${window.sharedCacheId}" class="form-control">
                                </div>
                            </div>
                            
                            <!-- BeanShell -->
                            <c:remove var="beanShellContent" />
                            <c:if test="${window.beanShell}">
                                <c:set var="beanShellContent" value="in" />
                            </c:if>
                            
                            <div class="form-group">
                                <label for="${window.id}-beanShell" class="control-label col-sm-3"><is:getProperty key="WINDOW_PROPERTIES_DYNAMIC_PROPERTIES" /></label>
                                <div class="col-sm-9">
                                    <div class="checkbox">
                                        <p>
                                            <label>
                                                <input id="${window.id}-beanShell" type="checkbox" name="bshActivation" value="1" onclick="toggleBeanShell(this)"
                                                    <c:if test="${window.beanShell}">checked="checked"</c:if>
                                                >
                                                <span><is:getProperty key="WINDOW_PROPERTIES_BEAN_SHELL" /></span>
                                            </label>
                                        </p>
                                    </div>
                                    
                                    <div id="${window.id}-beanShell-content" class="collapse
                                        <c:if test="${window.beanShell}">in</c:if>
                                    ">
                                        <p>
                                            <textarea rows="5" name="bshScript" class="form-control">${window.beanShellContent}</textarea>
                                        </p>
                                        
                                        <div class="panel panel-info">
                                            <div class="panel-heading">
                                                <div class="panel-title">
                                                    <a href="#${window.id}-beanShell-example" class="no-ajax-link" data-toggle="collapse">
                                                        <i class="halflings halflings-info-sign"></i>
                                                        <span><is:getProperty key="WINDOW_PROPERTIES_BEAN_SHELL_EXAMPLE" /></span>
                                                    </a>
                                                </div>
                                            </div>
                                            
                                            <div id="${window.id}-beanShell-example" class="panel-collapse collapse">
                                                <div class="panel-body">
                                                    <p>Implicits variables :</p>
                                                    <dl>
                                                        <dt>pageParamsEncoder</dt>
                                                        <dd>Parameters encoder, decoded to <code>List&lt;String&gt;</code></dd>
                                                        
                                                        <dt>windowsProperties</dt>
                                                        <dd>Window dynamic properties: <code>Map&lt;String, String&gt;</code></dd>
                                                        
                                                        <dt>osivia.dynamicCSSClasses</dt>
                                                        <dd>CSS class names separated by a space: <code>css1 css2</code></dd>
                                                    </dl>

<pre>
import java.util.List;

List cssSelectorValues =  pageParamsEncoder.decode("selectors", "cssSelector");
if (cssSelectorValues != null) {
    windowProperties.put("osivia.dynamicCSSClasses", cssSelectorValues.get(0));
}
rightCellToggle.add(example);
</pre>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            
                            <!-- Selection dependency indicator -->
                            <div class="form-group">
                                <label for="${window.id}-selection-dependency" class="control-label col-sm-3"><is:getProperty key="WINDOW_PROPERTIES_SELECTION_DEPENDENCY" /></label>
                                <div class="col-sm-9">
                                    <div class="checkbox">
                                        <label>
                                            <input id="${window.id}-selection-dependency" type="checkbox" name="selectionDep" value="1"
                                                <c:if test="${window.selectionDependency}">checked="checked"</c:if>
                                            >
                                            <span><is:getProperty key="WINDOW_PROPERTIES_SELECTION_DEPENDENCY_ACTIVATE" /></span>
                                        </label>
                                    </div>
                                </div>
                            </div>
                            
                        </fieldset>
                        
                        <div class="navbar navbar-default navbar-fixed-bottom">
                            <div class="col-sm-offset-3 col-sm-9">
                                <button type="submit" class="btn btn-primary navbar-btn">
                                    <i class="halflings halflings-floppy-disk"></i>
                                    <span><is:getProperty key="SAVE" /></span>
                                </button>
                                
                                <button type="button" class="btn btn-default navbar-btn" onclick="closeFancybox()"><is:getProperty key="CANCEL" /></button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </c:forEach>
    
    </div>
</c:if>
