<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>
<%@ taglib uri="/WEB-INF/tld/formatter.tld" prefix="formatter" %>

<%@ page contentType="text/html" isELIgnored="false"%>


<c:set var="commandUrl" value="${requestScope['osivia.toolbarSettings.commandURL']}" />
<c:set var="currentPage" value="${requestScope['osivia.toolbarSettings.page']}" />
<c:set var="portalId"><formatter:safeId portalObjectId="${currentPage.portal.id}" /></c:set>
<c:set var="currentPageId"><formatter:safeId portalObjectId="${currentPage.id}" /></c:set>
<c:set var="currentPageDisplayName"><formatter:displayName object="${currentPage}"/></c:set>


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
    	$JQry("input[name=displayTitle], input[name=displayDecorators], input[name=bootstrapPanelStyle], input[name=mobileCollapse]").change(function() {
            var $form = $JQry(this).parents("form"),
                $displayTitle = $form.find("input[name=displayTitle]"),
                displayTitle = $displayTitle.is(":checked"),
                $displayTitleDecorators = $form.find("input[name=displayDecorators]"),
                displayTitleDecorators = $displayTitleDecorators.is(":checked"),
                $maximizedToCms = $form.find("input[name=maximizedToCms]"),
                $displayPanel = $form.find("input[name=bootstrapPanelStyle]"),
                displayPanel = $displayPanel.is(":checked"),
                $panelCollapse = $form.find("input[name=mobileCollapse]"),
                panelCollapse = $panelCollapse.is(":checked");
            
            // Title decorators
            if (!displayTitle) {
            	$displayTitleDecorators.prop("checked", false);
            	$maximizedToCms.prop("checked", false);
            }
            if (!displayTitleDecorators) {
            	$maximizedToCms.prop("checked", false);
            }
            $displayTitleDecorators.prop("disabled", !displayTitle);
            $maximizedToCms.prop("disabled", !(displayTitle && displayTitleDecorators));
            
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
        <div id="page-creation" class="flexbox">
            <div class="scrollbox">
                <form action="${commandUrl}" method="get" class="form-horizontal" role="form">
                    <fieldset>
                        <legend>
                            <i class="glyphicons glyphicons-plus"></i>
                            <span><op:translate key="SUBMENU_PAGE_CREATION" /></span>
                        </legend>
    
                        <input type="hidden" name="action" value="createPage">
                        <input type="hidden" name="template" value="false">
                    
                        <!-- Name -->
                        <div class="form-group">
                            <label for="new-page-name" class="col-sm-4 col-lg-2 control-label required"><op:translate key="NAME" /></label>
                            <div class="col-sm-8 col-lg-10">
                                <input id="new-page-name" type="text" name="name" class="form-control" required="required">
                            </div>
                        </div>
                        
                        <div class="row">
                            <div class="col-lg-6">
                                <!-- Model -->
                                <div class="form-group">
                                    <label class="col-sm-4 control-label"><op:translate key="NEW_PAGE_MODEL" /></label>
                                    <div class="col-sm-8">
                                        <div class="selector">
                                            <input type="hidden" name="model" class="selector-value">
                                    
                                            <div class="panel panel-default">
                                                <div class="panel-body">
                                                    <div class="fancytree fancytree-selector fixed-height">
                                                        <p class="input-group input-group-sm">
                                                            <span class="input-group-addon">
                                                                <i class="halflings halflings-filter"></i>
                                                            </span>
                                                            
                                                            <input type="text" class="form-control" placeholder="${filterLabel}">
                                                            
                                                            <span class="input-group-btn">
                                                                <button type="button" class="btn btn-default" title="${clearFilterLabel}" data-toggle="tooltip" data-placement="bottom">
                                                                    <i class="halflings halflings-erase"></i>
                                                                    <span class="sr-only">${clearFilterLabel}</span>
                                                                </button>
                                                            </span>
                                                        </p>
                                
                                                        <c:out value="${requestScope['osivia.settings.models']}" escapeXml="false" />
                                                    </div>
                                                </div>  
                                            </div>
                                        </div>
                                        
                                        <div class="checkbox">
                                            <label>
                                                <input type="checkbox" data-toggle="fancytree">
                                                <span><op:translate key="NEW_PAGE_NO_MODEL" /></span>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="col-lg-6">
                                <!-- Parent -->
                                <div class="form-group">
                                    <label class="col-sm-4 control-label required"><op:translate key="NEW_PAGE_PARENT" /></label>
                                    <div class="col-sm-8">
                                        <div class="selector">
                                            <input type="hidden" name="parent" class="selector-value">
                                    
                                            <div class="panel panel-default">
                                                <div class="panel-body">
                                                    <div class="fancytree fancytree-selector fixed-height">
                                                        <p class="input-group input-group-sm">
                                                            <span class="input-group-addon">
                                                                <i class="halflings halflings-filter"></i>
                                                            </span>
                                                            
                                                            <input type="text" class="form-control" placeholder="${filterLabel}">
                                                            
                                                            <span class="input-group-btn">
                                                                <button type="button" class="btn btn-default" title="${clearFilterLabel}" data-toggle="tooltip" data-placement="bottom">
                                                                    <i class="halflings halflings-erase"></i>
                                                                    <span class="sr-only">${clearFilterLabel}</span>
                                                                </button>
                                                            </span>
                                                        </p>
                                
                                                        <c:out value="${requestScope['osivia.settings.pageParents']}" escapeXml="false" />
                                                    </div>
                                                </div>  
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        <div class="row">
                            <div class="col-sm-offset-4 col-sm-8 col-lg-offset-2 col-lg-10">
                                <button type="submit" class="btn btn-primary">
                                    <i class="glyphicons glyphicons-floppy-disk"></i>
                                    <span><op:translate key="SAVE" /></span>
                                </button>
                            
                                <button type="button" class="btn btn-default" onclick="closeFancybox()">
                                    <span><op:translate key="CANCEL" /></span>
                                </button>
                            </div>
                        </div>
                    </fieldset>
                </form>
            </div>
        </div>
        
        
        <!-- Template creation -->
        <div id="template-creation" class="flexbox">
            <div class="scrollbox">
                <form action="${commandUrl}" method="get" class="form-horizontal" role="form">
                    <fieldset>
                        <legend>
                            <i class="glyphicons glyphicons-plus"></i>
                            <span><op:translate key="SUBMENU_TEMPLATE_CREATION" /></span>
                        </legend>
                        
                        <input type="hidden" name="action" value="createPage">
                        <input type="hidden" name="template" value="true">
                    
                        <!-- Name -->
                        <div class="form-group">
                            <label for="new-template-name" class="col-sm-4 col-lg-2 control-label required"><op:translate key="NAME" /></label>
                            <div class="col-sm-8 col-lg-10">
                                <input id="new-template-name" type="text" name="name" class="form-control" required="required">
                            </div>
                        </div>
                        
                        <div class="row">
                            <div class="col-lg-6">
                                <!-- Model -->
                                <div class="form-group">
                                    <label class="col-sm-4 control-label"><op:translate key="NEW_TEMPLATE_MODEL" /></label>
                                    <div class="col-sm-8">
                                        <div class="selector">
                                            <input type="hidden" name="model" class="selector-value">
                                    
                                            <div class="panel panel-default">
                                                <div class="panel-body">
                                                    <div class="fancytree fancytree-selector fixed-height">
                                                        <p class="input-group input-group-sm">
                                                            <span class="input-group-addon">
                                                                <i class="halflings halflings-filter"></i>
                                                            </span>
                                                            
                                                            <input type="text" class="form-control" placeholder="${filterLabel}">
                                                            
                                                            <span class="input-group-btn">
                                                                <button type="button" class="btn btn-default" title="${clearFilterLabel}" data-toggle="tooltip" data-placement="bottom">
                                                                    <i class="halflings halflings-erase"></i>
                                                                    <span class="sr-only">${clearFilterLabel}</span>
                                                                </button>
                                                            </span>
                                                        </p>
                                
                                                        <c:out value="${requestScope['osivia.settings.models']}" escapeXml="false" />
                                                    </div>
                                                </div>  
                                            </div>
                                        </div>
                                        
                                        <div class="checkbox">
                                            <label>
                                                <input type="checkbox" data-toggle="fancytree">
                                                <span><op:translate key="NEW_TEMPLATE_NO_MODEL" /></span>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="col-lg-6">
                                <!-- Parent -->
                                <div class="form-group">
                                    <label class="col-sm-4 control-label required"><op:translate key="NEW_TEMPLATE_PARENT" /></label>
                                    <div class="col-sm-8">
                                        <div class="selector">
                                            <input type="hidden" name="parent" class="selector-value">
                                    
                                            <div class="panel panel-default">
                                                <div class="panel-body">
                                                    <div class="fancytree fancytree-selector fixed-height">
                                                        <p class="input-group input-group-sm">
                                                            <span class="input-group-addon">
                                                                <i class="halflings halflings-filter"></i>
                                                            </span>
                                                            
                                                            <input type="text" class="form-control" placeholder="${filterLabel}">
                                                            
                                                            <span class="input-group-btn">
                                                                <button type="button" class="btn btn-default" title="${clearFilterLabel}" data-toggle="tooltip" data-placement="bottom">
                                                                    <i class="halflings halflings-erase"></i>
                                                                    <span class="sr-only">${clearFilterLabel}</span>
                                                                </button>
                                                            </span>
                                                        </p>
                                
                                                        <c:out value="${requestScope['osivia.settings.templateParents']}" escapeXml="false" />
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        <div class="row">
                            <div class="col-sm-offset-4 col-sm-8 col-lg-offset-2 col-lg-10">
                                <button type="submit" class="btn btn-primary">
                                    <i class="glyphicons glyphicons-floppy-disk"></i>
                                    <span><op:translate key="SAVE" /></span>
                                </button>
                            
                                <button type="button" class="btn btn-default" onclick="closeFancybox()">
                                    <span><op:translate key="CANCEL" /></span>
                                </button>
                            </div>
                        </div>
                    </fieldset>
                </form>
            </div>
        </div>
        
        
        <!-- Properties -->
        <div id="page-properties" class="flexbox">
            <div class="scrollbox">
                <form action="${commandUrl}" method="get" class="form-horizontal" role="form">
                    <fieldset>
                        <legend>
                            <i class="glyphicons glyphicons-cogwheel"></i>
                            <span><op:translate key="SUBMENU_PROPERTIES" /></span>
                        </legend>
                    
                        <input type="hidden" name="action" value="changePageProperties" />
                        <input type="hidden" name="pageId" value="${currentPageId}" />
                        
                        <!-- Name -->
                        <div class="form-group">                
                            <label for="properties-page-name" class="col-sm-3 control-label required"><op:translate key="PAGE_NAME" /></label>
                            <div class="col-sm-9">
                                <input id="properties-page-name" type="text" name="displayName" value="${currentPageDisplayName}" class="form-control" required="required">
                            </div>
                        </div>
                    </fieldset>
                    
                    <fieldset
                        <c:if test="${requestScope['osivia.toolbarSettings.cmsTemplated']}">disabled="disabled"</c:if>
                    >    
                        <!-- Draft mode -->
                        <div class="form-group">
                            <label for="properties-page-draft-mode" class="col-sm-3 control-label"><op:translate key="PAGE_DRAFT_MODE" /></label>
                            <div class="col-sm-9">
                                <div class="checkbox">
                                    <label>
                                        <input id="properties-page-draft-mode" type="checkbox" name="draftPage" value="1"
                                            <c:if test="${requestScope['osivia.toolbarSettings.draftPage']}">checked="checked"</c:if>
                                        >
                                        <span><op:translate key="PAGE_DRAFT_MODE_ACTION" /></span>
                                    </label>
                                </div>
                                
                                <div class="help-block"><op:translate key="PAGE_DRAFT_MODE_HELP" /></div>
                            </div>
                        </div>
                        
                        <!-- Layout -->
                        <div class="form-group">
                            <label for="properties-page-layout" class="col-sm-3 control-label"><op:translate key="PAGE_LAYOUT" /></label>
                            <div class="col-sm-9">
                                <select id="properties-page-layout" name="newLayout" class="form-control">
                                    <!-- Default layout -->
                                    <option value=""
                                        <c:if test="${empty requestScope['osivia.toolbarSettings.currentLayout']}">selected="selected"</c:if>
                                    >
                                        <op:translate key="PAGE_DEFAULT_LAYOUT" />
                                    </option>
                                
                                    <!-- Layouts list -->
                                    <c:forEach var="layout" items="${requestScope['osivia.toolbarSettings.layoutsList']}">
                                        <option value="${layout.layoutInfo.name}" ${layoutSelected}
                                            <c:if test="${requestScope['osivia.toolbarSettings.currentLayout'] eq layout.layoutInfo.name}">selected="selected"</c:if>
                                        >
                                            ${layout.layoutInfo.name}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        
                        <!-- Theme -->
                        <div class="form-group">
                            <label for="properties-page-theme" class="col-sm-3 control-label"><op:translate key="PAGE_THEME" /></label>
                            <div class="col-sm-9">
                                <select id="properties-page-theme" name="newTheme" class="form-control">
                                    <!-- Default theme -->
                                    <option value=""
                                        <c:if test="${empty requestScope['osivia.toolbarSettings.currentTheme']}">selected="selected"</c:if>
                                    >
                                        <op:translate key="PAGE_DEFAULT_THEME" />
                                    </option>
                                
                                    <!-- Themes list -->
                                    <c:forEach var="theme" items="${requestScope['osivia.toolbarSettings.themesList']}">
                                        <option value="${theme.themeInfo.name}" ${themeSelected}
                                            <c:if test="${requestScope['osivia.toolbarSettings.currentTheme'] eq theme.themeInfo.name}">selected="selected"</c:if>
                                        >
                                            ${theme.themeInfo.name}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        
                        <!-- Category -->
                        <div class="form-group">
                            <label for="properties-page-category" class="col-sm-3 control-label"><op:translate key="PAGE_CATEGORY" /></label>
                            <div class="col-sm-9">
                                <select id="properties-page-category" name="pageCategory" class="form-control">
                                    <c:forEach var="category" items="${requestScope['osivia.toolbarSettings.pageCategories']}">                                                
                                        <option value="${category.key}"
                                            <c:if test="${requestScope['osivia.toolbarSettings.pageCategory'] eq category.key}">selected="selected"</c:if>
                                        >
                                            ${category.value}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        
                         <!-- Selectors propagation mode -->
                        <div class="form-group">
                            <label for="properties-page-selectors-propagation" class="col-sm-3 control-label"><op:translate key="PAGE_SELECTOR_PROPAGATION" /></label>
                            <div class="col-sm-9">
                                <div class="checkbox">
                                    <label>
                                        <input id="properties-page-selectors-propagation" type="checkbox" name="selectorsPropagation" value="1"
                                            <c:if test="${requestScope['osivia.toolbarSettings.selectorsPropagation']}">checked="checked"</c:if>
                                        >
                                        <span><op:translate key="PAGE_SELECTOR_PROPAGATION_ACTION" /></span>
                                    </label>
                                </div>
                            </div>
                        </div>
                    </fieldset>
                        
                    <div class="row">
                        <div class="col-sm-offset-3 col-sm-9">
                            <c:if test="${currentPageTemplateIndicator}">
                                <p class="help-block"><op:translate key="PAGE_CMS_TEMPLATED_PROPERTIES_DISABLED" /></p>
                            </c:if>
                            
                            <button type="submit" class="btn btn-primary">
                                <i class="glyphicons glyphicons-floppy-disk"></i>
                                <op:translate key="CHANGE" />
                            </button>
                            
                            <button type="button" class="btn btn-default" onclick="closeFancybox()">
                                <op:translate key="CANCEL" />
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
        
        
        <!-- Move -->
        <div id="page-location" class="flexbox">
            <div class="scrollbox">
                <form action="${commandUrl}" method="get" class="form-horizontal" role="form">
                    <fieldset>
                        <legend>
                            <i class="glyphicons glyphicons-move"></i>
                            <span><op:translate key="SUBMENU_LOCATION" /></span>
                        </legend>
                        
                        <input type="hidden" name="action" value="changePageOrder" />
                        <input type="hidden" name="pageId" value="${currentPageId}" />
                        
                        <div class="form-group">
                            <label class="col-sm-3 control-label required"><op:translate key="LOCATION" /></label>
                            <div class="col-sm-9">
                                <div class="selector">
                                    <input type="hidden" name="destination" class="selector-value">
                            
                                    <div class="panel panel-default">
                                        <div class="panel-body">
                                            <div class="fancytree fancytree-selector fixed-height">
                                                <p class="input-group input-group-sm">
                                                    <span class="input-group-addon">
                                                        <i class="halflings halflings-filter"></i>
                                                    </span>
                                                    
                                                    <input type="text" class="form-control" placeholder="${filterLabel}">
                                                    
                                                    <span class="input-group-btn">
                                                        <button type="button" class="btn btn-default" title="${clearFilterLabel}" data-toggle="tooltip" data-placement="bottom">
                                                            <i class="halflings halflings-erase"></i>
                                                            <span class="sr-only">${clearFilterLabel}</span>
                                                        </button>
                                                    </span>
                                                </p>
                        
                                                <c:out value="${requestScope['osivia.settings.locations']}" escapeXml="false" />
                                            </div>
                                        </div>  
                                    </div>
                                </div>
                                
                                <div class="help-block"><op:translate key="PAGE_ORDER_HELP" /></div>
                            </div>
                        </div>
                        
                        <div class="row">
                            <div class="col-sm-offset-3 col-sm-9">
                                <button type="submit" class="btn btn-primary">
                                    <i class="glyphicons glyphicons-floppy-disk"></i>
                                    <span><op:translate key="SAVE" /></span>
                                </button>
                            
                                <button type="button" class="btn btn-default" onclick="closeFancybox()">
                                    <span><op:translate key="CANCEL" /></span>
                                </button>
                            </div>
                        </div>
                    </fieldset>
                </form>
            </div>
        </div>
        
        
        <!-- Rights -->
        <div id="page-rights" class="flexbox">
            <div class="scrollbox">
                <form action="${commandUrl}" method="get" class="form-horizontal" role="form">
                    <input type="hidden" name="action" value="securePage" />
                    <input type="hidden" name="pageId" value="${currentPageId}" />
                    
                    <div class="form-group">
                        <label class="col-sm-3 control-label"><op:translate key="PAGE_ACCESS" /></label>
                        <div class="col-sm-9">
                            <c:forEach var="role" items="${requestScope['osivia.toolbarSettings.roles']}">
                                <c:remove var="roleChecked" />
                                <c:forEach var="item" items="${requestScope['osivia.toolbarSettings.actionsForRoles'][role.name]}">
                                    <c:if test='${item eq "view"}'>
                                        <c:set var="roleChecked" value="checked" />
                                    </c:if>
                                </c:forEach>
                            
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="view" value="${role.name}" ${roleChecked} />
                                        <span>${role.displayName}</span>
                                    </label>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                    
                    <div class="text-center">
                        <button type="submit" class="btn btn-default btn-primary"><op:translate key="PAGE_RIGHTS_SUBMIT" /></button>
                        <button type="button" class="btn btn-default" onclick="closeFancybox()"><op:translate key="CANCEL" /></button>
                    </div>
                </form>
            </div>
        </div>
        
        
        <!-- CMS -->
        <div id="page-cms" class="flexbox">
            <div class="scrollbox">
                <form id="formCMSProperties" action="${commandUrl}" method="get" class="form-horizontal" role="form">
                    <input type="hidden" name="action" value="changeCMSProperties" />
                    <input type="hidden" name="pageId" value="${currentPageId}" />
                    
                    <!-- Path -->
                    <div class="form-group">                
                        <label for="cms-path" class="control-label col-sm-4"><op:translate key="PAGE_CMS_PATH" /></label>
                        <div class="col-sm-8">
                            <input id="cms-path" type="text" name="cmsBasePath" value="${requestScope['osivia.toolbarSettings.cmsBasePath']}" onkeyup="toggleCMS()" class="form-control" placeholder="<op:translate key='PAGE_CMS_PATH' />" />
                        </div>
                    </div>
                    
                    <fieldset id="fieldsetCMSProperties">
                        <!-- Scope -->
                        <div class="form-group">                
                            <label for="cms-scope" class="control-label col-sm-4"><op:translate key="PAGE_CMS_SCOPE" /></label>
                            <div class="col-sm-8">
                                <span>${requestScope['osivia.toolbarSettings.cmsScopeSelect']}</span>
                            </div>
                        </div>
                    
                        <!-- Version -->
                        <div class="form-group">                
                            <label for="cms-version" class="control-label col-sm-4"><op:translate key="PAGE_CMS_VERSION" /></label>
                            <div class="col-sm-8">
                                <span>${requestScope['osivia.toolbarSettings.cmsDisplayLiveVersion']}</span>
                            </div>
                        </div>
                        
                        <!-- Contextualization -->
                        <div class="form-group">                
                            <label for="cms-contextualization" class="control-label col-sm-4"><op:translate key="PAGE_CMS_CONTEXTUALIZATION" /></label>
                            <div class="col-sm-8">
                                <span>${requestScope['osivia.toolbarSettings.cmsRecontextualizationSupport']}</span>
                            </div>
                        </div>
                    </fieldset>
                    
                    <div class="row">
                        <div class="col-sm-offset-4 col-sm-8">
                            <button type="submit" class="btn btn-default btn-primary"><op:translate key="PAGE_CMS_SUBMIT" /></button>
                            <button type="button" class="btn btn-default" onclick="closeFancybox()"><op:translate key="CANCEL" /></button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
        
        
        <!-- Delete page -->
        <div id="page-suppression" class="flexbox">
            <div class="scrollbox">
                <form action="${commandUrl}" method="get" role="form">
                    <input type="hidden" name="action" value="deletePage" />
                    <input type="hidden" name="pageId" value="${currentPageId}" />
                    
                    <p class="form-control-static text-center"><op:translate key="PAGE_SUPPRESSION_CONFIRM_MESSAGE" /></p>
                    
                    <div class="text-center">
                        <button type="submit" class="btn btn-warning">
                            <i class="halflings halflings-alert"></i>
                            <span><op:translate key="YES" /></span>
                        </button>
                        <button type="button" class="btn btn-default" onclick="closeFancybox()"><op:translate key="NO" /></button>
                    </div>
                </form>
            </div>
        </div>
        
        
        <!-- Elements list -->
        <c:set var="filterLabel"><op:translate key="FILTER" /></c:set>
        <c:set var="clearFilterLabel"><op:translate key="CLEAR_FILTER" /></c:set>
        <div id="pages-list" class="flexbox">
            <div class="scrollbox">
                <p class="lead">
                    <i class="glyphicons glyphicons-sort-by-alphabet"></i>
                    <span><op:translate key="SUBMENU_PAGES_LIST" /></span>
                </p>
            
                <div class="fancytree fancytree-links fixed-height">
                    <p class="input-group input-group-sm">
                        <span class="input-group-addon">
                            <i class="halflings halflings-filter"></i>
                        </span>
                        
                        <input type="text" class="form-control" placeholder="${filterLabel}">
                        
                        <span class="input-group-btn">
                            <button type="button" class="btn btn-default" title="${clearFilterLabel}" data-toggle="tooltip" data-placement="bottom">
                                <i class="halflings halflings-erase"></i>
                                <span class="sr-only">${clearFilterLabel}</span>
                            </button>
                        </span>
                    </p>

                    <c:out value="${requestScope['osivia.settings.elements']}" escapeXml="false" />
                </div>
            </div>
        </div>

        
        <!-- Add portlet -->
        <div id="add-portlet" class="flexbox">
            <div class="scrollbox">
                <form action="${commandUrl}" method="get" role="form">
                    <input type="hidden" name="action" value="addPortlet" />
                    <input type="hidden" name="pageId" value="${currentPageId}" />
                    <input type="hidden" name="regionId" />
                    <input type="hidden" name="instanceId" />
        
                    <formatter:portletsList />
                    
                    <div class="text-center">
                        <button type="button" class="btn btn-default" onclick="closeFancybox()"><op:translate key="CANCEL" /></button>
                    </div>
                </form>
            </div>
        </div>
        
        
        <!-- Delete portlet  -->
        <div id="delete-portlet" class="flexbox">
            <div class="scrollbox">
                <form action="${commandUrl}" method="get" class="form-horizontal" role="form">
                    <input type="hidden" name="action" value="deleteWindow" />
                    <input type="hidden" name="windowId" />
        
                    <p class="form-control-static text-center"><op:translate key="PORTLET_SUPPRESSION_CONFIRM_MESSAGE" /></p>
                    
                    <div class="text-center">
                        <button type="submit" class="btn btn-warning"  onclick="selectWindow(this.form)">
                            <i class="halflings halflings-alert"></i>
                            <span><op:translate key="YES" /></span>
                        </button>
                        <button type="button" class="btn btn-default" onclick="closeFancybox()"><op:translate key="NO" /></button>
                    </div>
                </form>
            </div>
        </div>
        
        
        <!-- Windows settings -->
        <c:forEach var="window" items="${requestScope['osivia.toolbarSettings.windowSettings']}">
            <div id="window-settings-${window.id}" class="flexbox">
                <form action="${commandUrl}" method="get" class="form-horizontal flexbox" role="form">
                    <input type="hidden" name="action" value="changeWindowSettings">
                    <input type="hidden" name="windowId" value="${window.id}">
                    
                    <div class="flexbox">
                        <div class="scrollbox">
                            <fieldset>
                                <legend>
                                    <span>&nbsp;</span>
                                    <i class="glyphicons glyphicons-display"></i>
                                    <span><op:translate key="WINDOW_PROPERTIES_DISPLAY" /></span>
                                </legend>
                                
                                <!-- Title -->                        
                                <div class="form-group">
                                    <label for="${window.id}-title" class="control-label col-sm-3"><op:translate key="WINDOW_PROPERTIES_TITLE" /></label>
                                    <div class="col-sm-9">
                                        <input id="${window.id}-title" type="text" name="title" value="${window.title}" class="form-control">
                                        
                                        <div class="checkbox">
                                            <label>
                                                <input type="checkbox" name="displayTitle" value="1"
                                                    <c:if test="${window.displayTitle or window.panelCollapse}">checked="checked"</c:if>
                                                    <c:if test="${window.panelCollapse}">disabled="disabled"</c:if>
                                                >
                                                <span><op:translate key="WINDOW_PROPERTIES_TITLE_DISPLAY" /></span>
                                            </label>
                                        </div>
                                        
                                        <div class="checkbox">
                                            <label>
                                                <input type="checkbox" name="displayDecorators" value="1"
                                                    <c:if test="${window.displayTitleDecorators}">checked="checked"</c:if>
                                                    <c:if test="${not window.displayTitle}">disabled="disabled"</c:if>
                                                >
                                                <span><op:translate key="WINDOW_PROPERTIES_TITLE_MORE" /></span>
                                            </label>
                                        </div>
                                        
                                        <div class="checkbox">
                                            <label>
                                                <input type="checkbox" name="maximizedToCms"
                                                    <c:if test="${window.maximizedToCms}">checked="checked"</c:if>
                                                    <c:if test="${not window.displayTitleDecorators}">disabled="disabled"</c:if>
                                                >
                                                <span><op:translate key="WINDOW_PROPERTIES_MAXIMIZED_TO_CMS" /></span>
                                            </label>
                                            <div class="help-block"><op:translate key="WINDOW_PROPERTIES_MAXIMIZED_TO_CMS_HELP" /></div>
                                        </div>
                                    </div>
                                </div>
                                
                                <!-- Panel -->
                                <div class="form-group">
                                    <label for="${window.id}-panel" class="control-label col-sm-3"><op:translate key="WINDOW_PROPERTIES_PANEL" /></label>
                                    <div class="col-sm-9">
                                        <div class="checkbox">
                                            <label>
                                                <input id="${window.id}-panel" type="checkbox" name="bootstrapPanelStyle"
                                                    <c:if test="${window.displayPanel or window.panelCollapse}">checked="checked"</c:if>
                                                    <c:if test="${window.panelCollapse}">disabled="disabled"</c:if>
                                                >
                                                <span><op:translate key="WINDOW_PROPERTIES_PANEL_DISPLAY" /></span>
                                            </label>
                                        </div>
                                        
                                        <div class="checkbox">
                                            <label>
                                                <input type="checkbox" name="mobileCollapse"
                                                    <c:if test="${window.panelCollapse}">checked="checked"</c:if>
                                                    <c:if test="${not (window.displayTitle and window.displayPanel)}">disabled="disabled"</c:if>
                                                >
                                                <span><op:translate key="WINDOW_PROPERTIES_PANEL_COLLAPSE" /></span>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                                
                                <!-- Ajax -->
                                <div class="form-group">
                                    <label for="${window.id}-ajax" class="control-label col-sm-3"><op:translate key="WINDOW_PROPERTIES_AJAX" /></label>
                                    <div class="col-sm-9">
                                        <div class="checkbox">
                                            <label>
                                                <input id="${window.id}-ajax" type="checkbox" name="ajaxLink" value="1"
                                                    <c:if test="${window.ajax}">checked="checked"</c:if>
                                                >
                                                <span><op:translate key="WINDOW_PROPERTIES_AJAX_ACTIVATE" /></span>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                                
                                <!-- Hide empty portlet -->
                                <div class="form-group">
                                    <label for="${window.id}-hide-empty" class="control-label col-sm-3"><op:translate key="WINDOW_PROPERTIES_HIDE_EMPTY" /></label>
                                    <div class="col-sm-9">
                                        <div class="checkbox">
                                            <label>
                                                <input id="${window.id}-hide-empty" type="checkbox" name="hideEmptyPortlet" value="1"
                                                    <c:if test="${window.hideEmpty}">checked="checked"</c:if>
                                                >
                                                <span><op:translate key="WINDOW_PROPERTIES_HIDE_EMPTY_ACTIVATE" /></span>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                                
                                <!-- Print -->
                                <div class="form-group">
                                    <label for="${window.id}-print" class="control-label col-sm-3"><op:translate key="WINDOW_PROPERTIES_PRINT" /></label>
                                    <div class="col-sm-9">
                                        <div class="checkbox">
                                            <label>
                                                <input id="${window.id}-print" type="checkbox" name="printPortlet" value="1"
                                                    <c:if test="${window.print}">checked="checked"</c:if>
                                                >
                                                <span><op:translate key="WINDOW_PROPERTIES_PRINT_ACTIVATE" /></span>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                                
                                <!-- Styles -->
                                <div class="form-group">
                                    <label class="control-label col-sm-3"><op:translate key="WINDOW_PROPERTIES_STYLES" /></label>
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
                                        
                                        <c:if test="${empty window.styles}">
                                            <p class="form-control-static">
                                                <span class="text-muted"><op:translate key="WINDOW_PROPERTIES_NO_STYLE" /></span>
                                            </p>
                                        </c:if>
                                    </div>
                                </div>
                            </fieldset>
                        
                            <fieldset>
                                <legend>
                                    <span>&nbsp;</span>
                                    <i class="halflings halflings-dashboard"></i>
                                    <span><op:translate key="WINDOW_PROPERTIES_ADVANCED_OPTIONS" /></span>
                                </legend>
                                
                                <!-- Scopes -->
                                <div class="form-group">
                                    <label for="${window.id}-scopes" class="control-label col-sm-3"><op:translate key="WINDOW_PROPERTIES_SCOPE_DISPLAY" /></label>
                                    <div class="col-sm-9">
                                        <select id="${window.id}-scopes" name="conditionalScope" class="form-control">
                                            <option value=""
                                                <c:if test="${empty window.selectedScope}">selected="selected"</c:if>
                                            ><op:translate key="WINDOW_PROPERTIES_SCOPE_ALL_PROFILES" /></option>
                                            
                                            <c:forEach var="scope" items="${window.scopes}">
                                                <option value="${scope.key}"
                                                    <c:if test="${window.selectedScope eq scope.key}">selected="selected"</c:if>
                                                >${scope.value}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>
                                
                                <!-- Linked taskbar item -->
                                <div class="form-group">
                                    <label for="${window.id}-linked-taskbar-item" class="control-label col-sm-3"><op:translate key="WINDOW_PROPERTIES_LINKED_TASKBAR_ITEM" /></label>
                                    <div class="col-sm-9">
                                        <select id="${window.id}-linked-taskbar-item" name="taskbarItemId" class="form-control">
                                            <option value=""
                                                <c:if test="${empty window.taskbarItemId}">selected="selected"</c:if>
                                            ><op:translate key="WINDOW_PROPERTIES_NO_LINKED_TASKBAR_ITEM" /></option>
                                            
                                            <c:forEach var="item" items="${window.taskbarItems}">
                                                <option value="${item.value}"
                                                    <c:if test="${item.value eq window.taskbarItemId}">selected="selected"</c:if>
                                                >${item.key}</option>
                                            </c:forEach>
                                        </select>
                                        <p class="help-block"><op:translate key="WINDOW_PROPERTIES_LINKED_TASKBAR_ITEM_HELP" /></p>
                                    </div>
                                </div>
                                
                                <!-- Customization identifier -->
                                <div class="form-group">
                                    <label for="${window.id}-custom-id" class="control-label col-sm-3"><op:translate key="WINDOW_PROPERTIES_CUSTOM_ID" /></label>
                                    <div class="col-sm-9">
                                        <input id="${window.id}-custom-id" type="text" name="idPerso" value="${window.customizationId}" class="form-control">
                                    </div>
                                </div>
                                
                                <!-- Shared cache identifier -->
                                <div class="form-group">
                                    <label for="${window.id}-shared-cache-id" class="control-label col-sm-3"><op:translate key="WINDOW_PROPERTIES_SHARED_CACHE_ID" /></label>
                                    <div class="col-sm-9">
                                        <input id="${window.id}-shared-cache-id" type="text" name="cacheID" value="${window.sharedCacheId}" class="form-control">
                                    </div>
                                </div>
                                
                                <!-- Shared cache identifier -->
                                <div class="form-group">
                                    <label for="${window.id}-priority" class="control-label col-sm-3"><op:translate key="WINDOW_PROPERTIES_PRIORITY" /></label>
                                    <div class="col-sm-9">
                                        <input id="${window.id}-priority" type="number" name="priority" value="${window.priority}" class="form-control">
                                    </div>
                                </div>
                                
                                <!-- Selection dependency indicator -->
                                <div class="form-group">
                                    <label for="${window.id}-selection-dependency" class="control-label col-sm-3"><op:translate key="WINDOW_PROPERTIES_SELECTION_DEPENDENCY" /></label>
                                    <div class="col-sm-9">
                                        <div class="checkbox">
                                            <label>
                                                <input id="${window.id}-selection-dependency" type="checkbox" name="selectionDep" value="1"
                                                    <c:if test="${window.selectionDependency}">checked="checked"</c:if>
                                                >
                                                <span><op:translate key="WINDOW_PROPERTIES_SELECTION_DEPENDENCY_ACTIVATE" /></span>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                                
                                <!-- BeanShell -->
                                <c:remove var="beanShellContent" />
                                <c:if test="${window.beanShell}">
                                    <c:set var="beanShellContent" value="in" />
                                </c:if>
                                
                                <div class="form-group">
                                    <label for="${window.id}-beanShell" class="control-label col-sm-3"><op:translate key="WINDOW_PROPERTIES_DYNAMIC_PROPERTIES" /></label>
                                    <div class="col-sm-9">
                                        <div class="checkbox">
                                            <p>
                                                <label>
                                                    <input id="${window.id}-beanShell" type="checkbox" name="bshActivation" value="1" onclick="toggleBeanShell(this)"
                                                        <c:if test="${window.beanShell}">checked="checked"</c:if>
                                                    >
                                                    <span><op:translate key="WINDOW_PROPERTIES_BEAN_SHELL" /></span>
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
                                                            <span><op:translate key="WINDOW_PROPERTIES_BEAN_SHELL_EXAMPLE" /></span>
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
                                
                                <!-- Satellite -->
                                <c:if test="${not empty window.satellites}">
                                    <div class="form-group">
                                        <label for="${window.id}-satellite" class="control-label col-sm-3"><op:translate key="WINDOW_PROPERTIES_SATELLITE" /></label>
                                        <div class="col-sm-9">
                                            <select id="${window.id}-satellite" name="satellite" class="form-control">
                                                <c:forEach var="satellite" items="${window.satellites}">
                                                    <option value="${satellite.key}" ${satellite.key eq window.selectedSatellite ? 'selected="selected"' : ''}>${satellite.value}</option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                    </div>          
                                </c:if>
                            </fieldset>
                        </div>
                    </div>
                    
                    <div class="row">
                        <div class="col-sm-offset-3 col-sm-9">
                            <button type="submit" class="btn btn-primary navbar-btn">
                                <i class="halflings halflings-floppy-disk"></i>
                                <span><op:translate key="SAVE" /></span>
                            </button>
                            
                            <button type="button" class="btn btn-default navbar-btn" onclick="closeFancybox()"><op:translate key="CANCEL" /></button>
                        </div>
                    </div>
                </form>
            </div>
        </c:forEach>
    </div>
</c:if>


<!-- Modal -->
<div id="osivia-modal" class="modal fade" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <!-- Header -->
            <div class="modal-header hidden">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <p class="h4 modal-title"></p>
            </div>
            
            <!-- Body -->
            <div class="modal-body">
                <div class="dyna-region">
                    <div id="modal-region">
                        <div class="dyna-window">
                            <div id="modal-window" class="partial-refresh-window">
                                <div class="dyna-window-content">
                                    <div class="text-center text-muted modal-waiter">
                                        <i class="halflings halflings-refresh"></i>
                                        <span><op:translate key="AJAX_REFRESH" /></span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Footer -->
            <div class="modal-footer hidden">
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    <span><op:translate key="CLOSE" /></span>
                </button>
            </div>
        </div>
    </div>
    
    <div class="modal-clone hidden"></div>
</div>


<!-- Reload -->
<c:set var="urls" value="${requestScope['osivia.session.reload.urls']}" />
<div id="session-reload" data-reload="true" data-urls="${urls}"></div>
