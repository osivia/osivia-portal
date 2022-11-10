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


<%-- Administrator content --%>
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


    <div class="d-none">

        <%-- Page creation --%>
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

                        <%-- Name --%>
                        <div class="mb-3">
                            <label for="new-page-name" class="col-sm-4 col-lg-2 control-label required"><op:translate key="NAME" /></label>
                            <div class="col-sm-8 col-lg-10">
                                <input id="new-page-name" type="text" name="name" class="form-control" required="required">
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-lg-6">
                                <%-- Model --%>
                                <div class="mb-3">
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
                                                                <button type="button" class="btn btn-secondary" title="${clearFilterLabel}" data-toggle="tooltip" data-placement="bottom">
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
                                <%-- Parent --%>
                                <div class="mb-3">
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
                                                                <button type="button" class="btn btn-secondary" title="${clearFilterLabel}" data-toggle="tooltip" data-placement="bottom">
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

                                <button type="button" class="btn btn-secondary" onclick="closeFancybox()">
                                    <span><op:translate key="CANCEL" /></span>
                                </button>
                            </div>
                        </div>
                    </fieldset>
                </form>
            </div>
        </div>


        <%-- Template creation --%>
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

                        <%-- Name --%>
                        <div class="mb-3">
                            <label for="new-template-name" class="col-sm-4 col-lg-2 control-label required"><op:translate key="NAME" /></label>
                            <div class="col-sm-8 col-lg-10">
                                <input id="new-template-name" type="text" name="name" class="form-control" required="required">
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-lg-6">
                                <%-- Model --%>
                                <div class="mb-3">
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
                                                                <button type="button" class="btn btn-secondary" title="${clearFilterLabel}" data-toggle="tooltip" data-placement="bottom">
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
                                <%-- Parent --%>
                                <div class="mb-3">
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
                                                                <button type="button" class="btn btn-secondary" title="${clearFilterLabel}" data-toggle="tooltip" data-placement="bottom">
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

                                <button type="button" class="btn btn-secondary" onclick="closeFancybox()">
                                    <span><op:translate key="CANCEL" /></span>
                                </button>
                            </div>
                        </div>
                    </fieldset>
                </form>
            </div>
        </div>


        <%-- Properties --%>
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

                        <%-- Name --%>
                        <div class="mb-3">
                            <label for="properties-page-name" class="col-sm-3 control-label required"><op:translate key="PAGE_NAME" /></label>
                            <div class="col-sm-9">
                                <input id="properties-page-name" type="text" name="displayName" value="${currentPageDisplayName}" class="form-control" required="required">
                            </div>
                        </div>
                    </fieldset>

                    <fieldset
                        <c:if test="${requestScope['osivia.toolbarSettings.cmsTemplated']}">disabled="disabled"</c:if>
                    >
                        <%-- Draft mode --%>
                        <div class="mb-3">
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

                                <div class="form-text text-muted"><op:translate key="PAGE_DRAFT_MODE_HELP"/></div>
                            </div>
                        </div>

                        <%-- Layout --%>
                        <div class="mb-3">
                            <label for="properties-page-layout" class="col-sm-3 control-label"><op:translate key="PAGE_LAYOUT" /></label>
                            <div class="col-sm-9">
                                <select id="properties-page-layout" name="newLayout" class="form-control">
                                    <%-- Default layout --%>
                                    <option value=""
                                        <c:if test="${empty requestScope['osivia.toolbarSettings.currentLayout']}">selected="selected"</c:if>
                                    >
                                        <op:translate key="PAGE_DEFAULT_LAYOUT" />
                                    </option>

                                    <%-- Layouts list --%>
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

                        <%-- Theme --%>
                        <div class="mb-3">
                            <label for="properties-page-theme" class="col-sm-3 control-label"><op:translate key="PAGE_THEME" /></label>
                            <div class="col-sm-9">
                                <select id="properties-page-theme" name="newTheme" class="form-control">
                                    <%-- Default theme --%>
                                    <option value=""
                                        <c:if test="${empty requestScope['osivia.toolbarSettings.currentTheme']}">selected="selected"</c:if>
                                    >
                                        <op:translate key="PAGE_DEFAULT_THEME" />
                                    </option>

                                    <%-- Themes list --%>
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

                        <%-- Category --%>
                        <div class="mb-3">
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

                         <%-- Selectors propagation mode --%>
                        <div class="mb-3">
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
                                <p class="form-text text-muted"><op:translate key="PAGE_CMS_TEMPLATED_PROPERTIES_DISABLED"/></p>
                            </c:if>

                            <button type="submit" class="btn btn-primary">
                                <i class="glyphicons glyphicons-floppy-disk"></i>
                                <op:translate key="CHANGE" />
                            </button>

                            <button type="button" class="btn btn-secondary" onclick="closeFancybox()">
                                <op:translate key="CANCEL" />
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>


        <%-- Move --%>
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

                        <div class="mb-3">
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
                                                        <button type="button" class="btn btn-secondary" title="${clearFilterLabel}" data-toggle="tooltip" data-placement="bottom">
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

                                <div class="form-text text-muted"><op:translate key="PAGE_ORDER_HELP"/></div>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-sm-offset-3 col-sm-9">
                                <button type="submit" class="btn btn-primary">
                                    <i class="glyphicons glyphicons-floppy-disk"></i>
                                    <span><op:translate key="SAVE" /></span>
                                </button>

                                <button type="button" class="btn btn-secondary" onclick="closeFancybox()">
                                    <span><op:translate key="CANCEL" /></span>
                                </button>
                            </div>
                        </div>
                    </fieldset>
                </form>
            </div>
        </div>


        <%-- Rights --%>
        <div id="page-rights" class="flexbox">
            <div class="scrollbox">
                <form action="${commandUrl}" method="get" class="form-horizontal" role="form">
                    <input type="hidden" name="action" value="securePage" />
                    <input type="hidden" name="pageId" value="${currentPageId}" />

                    <div class="mb-3">
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
                        <button type="submit" class="btn btn-primary"><op:translate key="PAGE_RIGHTS_SUBMIT" /></button>
                        <button type="button" class="btn btn-secondary" onclick="closeFancybox()"><op:translate key="CANCEL" /></button>
                    </div>
                </form>
            </div>
        </div>


        <%-- CMS --%>
        <div id="page-cms" class="flexbox">
            <div class="scrollbox">
                <form id="formCMSProperties" action="${commandUrl}" method="get" class="form-horizontal" role="form">
                    <input type="hidden" name="action" value="changeCMSProperties" />
                    <input type="hidden" name="pageId" value="${currentPageId}" />

                    <%-- Path --%>
                    <div class="mb-3">
                        <label for="cms-path" class="control-label col-sm-4"><op:translate key="PAGE_CMS_PATH" /></label>
                        <div class="col-sm-8">
                            <input id="cms-path" type="text" name="cmsBasePath" value="${requestScope['osivia.toolbarSettings.cmsBasePath']}" onkeyup="toggleCMS()" class="form-control" placeholder="<op:translate key='PAGE_CMS_PATH' />" />
                        </div>
                    </div>

                    <fieldset id="fieldsetCMSProperties">
                        <%-- Scope --%>
                        <div class="mb-3">
                            <label for="cms-scope" class="control-label col-sm-4"><op:translate key="PAGE_CMS_SCOPE" /></label>
                            <div class="col-sm-8">
                                <span>${requestScope['osivia.toolbarSettings.cmsScopeSelect']}</span>
                            </div>
                        </div>

                        <%-- Version --%>
                        <div class="mb-3">
                            <label for="cms-version" class="control-label col-sm-4"><op:translate key="PAGE_CMS_VERSION" /></label>
                            <div class="col-sm-8">
                                <span>${requestScope['osivia.toolbarSettings.cmsDisplayLiveVersion']}</span>
                            </div>
                        </div>

                        <%-- Contextualization --%>
                        <div class="mb-3">
                            <label for="cms-contextualization" class="control-label col-sm-4"><op:translate key="PAGE_CMS_CONTEXTUALIZATION" /></label>
                            <div class="col-sm-8">
                                <span>${requestScope['osivia.toolbarSettings.cmsRecontextualizationSupport']}</span>
                            </div>
                        </div>
                    </fieldset>

                    <div class="row">
                        <div class="col-sm-offset-4 col-sm-8">
                            <button type="submit" class="btn btn-primary"><op:translate key="PAGE_CMS_SUBMIT" /></button>
                            <button type="button" class="btn btn-secondary" onclick="closeFancybox()"><op:translate key="CANCEL" /></button>
                        </div>
                    </div>
                </form>
            </div>
        </div>


        <%-- Delete page --%>
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
                        <button type="button" class="btn btn-secondary" onclick="closeFancybox()"><op:translate key="NO" /></button>
                    </div>
                </form>
            </div>
        </div>


        <%-- Elements list --%>
        <c:set var="filterLabel"><op:translate key="FILTER" /></c:set>
        <c:set var="clearFilterLabel"><op:translate key="CLEAR_FILTER" /></c:set>
        <div id="pages-list" class="flexbox">
            <div class="scrollbox">
                <p class="lead">
                    <i class="glyphicons glyphicons-sort-by-alphabet"></i>
                    <span><op:translate key="SUBMENU_PAGES_LIST" /></span>
                </p>

                <div class="fancytree fancytree-links fixed-height">
                    <div class="input-group input-group-sm mb-3">
                        <div class="input-group-prepend">
                            <span class="input-group-text">
                                <i class="glyphicons glyphicons-basic-filter"></i>
                            </span>
                        </div>

                        <input type="text" class="form-control" placeholder="${filterLabel}">

                        <div class="input-group-append">
                            <button type="button" class="btn btn-outline-secondary" title="${clearFilterLabel}"
                                    data-toggle="tooltip" data-placement="bottom">
                                <i class="halflings halflings-erase"></i>
                                <span class="sr-only">${clearFilterLabel}</span>
                            </button>
                        </div>
                    </div>

                    <c:out value="${requestScope['osivia.settings.elements']}" escapeXml="false" />
                </div>
            </div>
        </div>


        <%-- Add portlet --%>
        <div id="add-portlet" class="flexbox">
            <div class="scrollbox">
                <form action="${commandUrl}" method="get" role="form">
                    <input type="hidden" name="action" value="addPortlet" />
                    <input type="hidden" name="pageId" value="${currentPageId}" />
                    <input type="hidden" name="regionId" />
                    <input type="hidden" name="instanceId" />

                    <formatter:portletsList />

                    <div class="text-center">
                        <button type="button" class="btn btn-secondary" onclick="closeFancybox()"><op:translate key="CANCEL" /></button>
                    </div>
                </form>
            </div>
        </div>


        <%-- Delete portlet  --%>
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
                        <button type="button" class="btn btn-secondary" onclick="closeFancybox()"><op:translate key="NO" /></button>
                    </div>
                </form>
            </div>
        </div>


        <%-- Windows settings --%>
        <c:forEach var="window" items="${requestScope['osivia.toolbarSettings.windowSettings']}" varStatus="windowStatus">
            <div id="window-settings-${window.id}">
                <form action="${commandUrl}" method="get" role="form">
                    <input type="hidden" name="action" value="changeWindowSettings">
                    <input type="hidden" name="windowId" value="${window.id}">

                    <fieldset class="mb-3">
                        <legend>
                            <i class="glyphicons glyphicons-basic-monitor"></i>
                            <span><op:translate key="WINDOW_PROPERTIES_DISPLAY"/></span>
                        </legend>

                            <%-- Title --%>
                        <div class="mb-3">
                            <label for="${window.id}-title"><op:translate key="WINDOW_PROPERTIES_TITLE"/></label>
                            <input id="${window.id}-title" type="text" name="title" value="${window.title}"
                                   class="form-control mb-2">

                            <div class="form-check">
                                <input id="${window.id}-display-title" type="checkbox" name="displayTitle" value="1"
                                       class="form-check-input" ${window.displayTitle or window.panelCollapse ? 'checked="checked"' : ''} ${window.panelCollapse ? 'disabled="disabled"' : ''}>
                                <label for="${window.id}-display-title" class="form-check-label"><op:translate
                                        key="WINDOW_PROPERTIES_TITLE_DISPLAY"/></label>
                            </div>

                            <div class="form-check">
                                <input id="${window.id}-display-decorators" type="checkbox" name="displayDecorators"
                                       value="1"
                                       class="form-check-input" ${window.displayTitleDecorators ? 'checked="checked"' : ''} ${window.displayTitle ? '' : 'disabled="disabled"'}>
                                <label for="${window.id}-display-decorators" class="form-check-label"><op:translate
                                        key="WINDOW_PROPERTIES_TITLE_MORE"/></label>
                            </div>

                            <div class="form-check">
                                <input id="${window.id}-maximized-to-cms" type="checkbox" name="maximizedToCms"
                                       class="form-check-input" ${window.maximizedToCms ? 'checked="checked"' : ''} ${window.displayTitleDecorators ? '' : 'disabled="disabled"'}>
                                <label for="${window.id}-maximized-to-cms" class="form-check-label"><op:translate
                                        key="WINDOW_PROPERTIES_MAXIMIZED_TO_CMS"/></label>
                                <small class="form-text text-muted"><op:translate
                                        key="WINDOW_PROPERTIES_MAXIMIZED_TO_CMS_HELP"/></small>
                            </div>
                        </div>

                            <%-- Panel --%>
                        <div class="mb-3">
                            <label><op:translate key="WINDOW_PROPERTIES_PANEL"/></label>
                            <div class="form-check">
                                <input id="${window.id}-panel" type="checkbox" name="bootstrapPanelStyle"
                                       class="form-check-input" ${window.displayPanel or window.panelCollapse ? 'checked="checked"' : ''} ${window.panelCollapse ? 'disabled="disabled"' : ''}>
                                <label for="${window.id}-panel" class="form-check-label"><op:translate
                                        key="WINDOW_PROPERTIES_PANEL_DISPLAY"/></label>
                            </div>

                            <div class="form-check">
                                <input id="${window.id}-mobile-collapse" type="checkbox" name="mobileCollapse"
                                       class="form-check-input" ${window.panelCollapse ? 'checked="checked"' : ''} ${window.displayTitle and window.displayPanel ? '' : 'disabled="disabled"'}>
                                <label for="${window.id}-mobile-collapse" class="form-check-label"><op:translate
                                        key="WINDOW_PROPERTIES_PANEL_COLLAPSE"/></label>
                            </div>
                        </div>

                            <%-- Ajax --%>
                        <div class="mb-3">
                            <label for="${window.id}-ajax"><op:translate key="WINDOW_PROPERTIES_AJAX"/></label>
                            <div class="form-check">
                                <input id="${window.id}-ajax" type="checkbox" name="ajaxLink" value="1"
                                       class="form-check-input" ${window.ajax ? 'checked="checked"' : ''}>
                                <label for="${window.id}-ajax" class="form-check-label"><op:translate
                                        key="WINDOW_PROPERTIES_AJAX_ACTIVATE"/></label>
                            </div>
                        </div>

                            <%-- Hide empty portlet --%>
                        <div class="mb-3">
                            <label for="${window.id}-hide-empty"><op:translate key="WINDOW_PROPERTIES_HIDE_EMPTY"/></label>
                            <div class="form-check">
                                <input id="${window.id}-hide-empty" type="checkbox" name="hideEmptyPortlet" value="1"
                                       class="form-check-input" ${window.hideEmpty ? 'checked="checked"' : ''}>
                                <label for="${window.id}-hide-empty" class="form-check-label"><op:translate
                                        key="WINDOW_PROPERTIES_HIDE_EMPTY_ACTIVATE"/></label>
                            </div>
                        </div>

                            <%-- Print --%>
                        <div class="mb-3">
                            <label for="${window.id}-print"><op:translate key="WINDOW_PROPERTIES_PRINT"/></label>
                            <div class="form-check">
                                <input id="${window.id}-print" type="checkbox" name="printPortlet" value="1"
                                       class="form-check-input" ${window.print ? 'checked="checked"' : ''}>
                                <label for="${window.id}-print" class="form-check-label"><op:translate
                                        key="WINDOW_PROPERTIES_PRINT_ACTIVATE"/></label>
                            </div>
                        </div>

                            <%-- Styles --%>
                        <div class="mb-3">
                            <label><op:translate key="WINDOW_PROPERTIES_STYLES"/></label>
                            <div class="row">
                                <c:forEach var="style" items="${window.styles}" varStatus="status">
                                    <div class="form-check form-check-inline">
                                        <input id="${window.id}-style-${status.index}" type="checkbox" name="style"
                                               value="${style.key}"
                                               class="form-check-input" ${style.value ? 'checked="checked"' : ''}>
                                        <label for="${window.id}-style-${status.index}"
                                               class="form-check-label">${style.key}</label>
                                    </div>
                                </c:forEach>
                            </div>

                            <c:if test="${empty window.styles}">
                                <div class="form-control-plaintext text-muted"><op:translate
                                        key="WINDOW_PROPERTIES_NO_STYLE"/></div>
                            </c:if>
                        </div>
                    </fieldset>

                    <fieldset class="mb-3">
                        <legend>
                            <i class="glyphicons glyphicons-basic-dashboard"></i>
                            <span><op:translate key="WINDOW_PROPERTIES_ADVANCED_OPTIONS"/></span>
                        </legend>

                        <%-- Scopes --%>
                        <div class="mb-3">
                            <label for="${window.id}-scopes" class="form-label"><op:translate key="WINDOW_PROPERTIES_SCOPE_DISPLAY" /></label>
                            <div>
                                <c:forEach var="scope" items="${window.scopes}" varStatus="scopeStatus">
                                    <div class="form-check form-check-inline">
                                        <input id="window-${windowStatus.index}-scope-${scopeStatus.index}" type="checkbox" name="conditionalScopes" value="${scope.id}" ${scope.selected ? 'checked' : ''} class="form-check-input">
                                        <label for="window-${windowStatus.index}-scope-${scopeStatus.index}" class="form-check-label">${scope.text}</label>
                                    </div>
                                </c:forEach>

                                <c:if test="${empty window.scopes}">
                                    <p class="form-control-static">
                                        <span class="text-muted"><op:translate key="WINDOW_PROPERTIES_NO_SCOPE" /></span>
                                    </p>
                                </c:if>
                            </div>
                        </div>

                        <%-- Linked taskbar item --%>
                        <div class="mb-3">
                            <label for="${window.id}-linked-taskbar-item"><op:translate
                                    key="WINDOW_PROPERTIES_LINKED_TASKBAR_ITEM"/></label>
                            <select id="${window.id}-linked-taskbar-item" name="taskbarItemId" class="form-control">
                                <option value="" ${empty window.taskbarItemId ? 'selected="selected"' : ''}><op:translate
                                        key="WINDOW_PROPERTIES_NO_LINKED_TASKBAR_ITEM"/></option>
                                <c:forEach var="item" items="${window.taskbarItems}">
                                    <option value="${item.value}" ${item.value eq window.taskbarItemId ? 'selected="selected"' : ''}>${item.key}</option>
                                </c:forEach>
                            </select>
                            <small class="form-text text-muted"><op:translate
                                    key="WINDOW_PROPERTIES_LINKED_TASKBAR_ITEM_HELP"/></small>
                        </div>

                            <%--Linked layout item--%>
                        <div class="mb-3">
                            <label for="${window.id}-linked-layout-item"><op:translate
                                    key="WINDOW_PROPERTIES_LINKED_LAYOUT_ITEM"/></label>
                            <select id="${window.id}-linked-layout-item" name="layoutItemId" class="form-control">
                                <option value="" ${empty window.layoutItemId ? 'selected="selected"' : ''}><op:translate
                                        key="WINDOW_PROPERTIES_NO_LINKED_LAYOUT_ITEM"/></option>
                                <c:forEach var="group" items="${window.layoutGroups}">
                                    <optgroup label="${group.label}">
                                        <c:forEach var="item" items="${group.items}">
                                            <option value="${item.id}" ${window.layoutItemId eq item.id ? 'selected="selected"' : ''}>${item.label}</option>
                                        </c:forEach>
                                    </optgroup>
                                </c:forEach>
                            </select>
                            <small class="form-text text-muted"><op:translate
                                    key="WINDOW_PROPERTIES_LINKED_LAYOUT_ITEM_HELP"/></small>
                        </div>

                            <%-- Customization identifier --%>
                        <div class="mb-3">
                            <label for="${window.id}-custom-id"><op:translate key="WINDOW_PROPERTIES_CUSTOM_ID"/></label>
                            <input id="${window.id}-custom-id" type="text" name="idPerso" value="${window.customizationId}"
                                   class="form-control">
                        </div>

                            <%-- Shared cache identifier --%>
                        <div class="mb-3">
                            <label for="${window.id}-shared-cache-id"><op:translate
                                    key="WINDOW_PROPERTIES_SHARED_CACHE_ID"/></label>
                            <input id="${window.id}-shared-cache-id" type="text" name="cacheID"
                                   value="${window.sharedCacheId}" class="form-control">
                        </div>


                            <%-- Priority --%>
                        <div class="mb-3">
                            <label for="${window.id}-priority"><op:translate key="WINDOW_PROPERTIES_PRIORITY"/></label>
                            <input id="${window.id}-priority" type="number" name="priority" value="${window.priority}"
                                   class="form-control">
                        </div>


                            <%-- Selection dependency indicator --%>
                        <div class="mb-3">
                            <label for="${window.id}-selection-dependency"><op:translate
                                    key="WINDOW_PROPERTIES_SELECTION_DEPENDENCY"/></label>
                            <div class="form-check">
                                <input id="${window.id}-selection-dependency" type="checkbox" name="selectionDep" value="1"
                                       class="form-check-input" ${window.selectionDependency ? 'checked="checked"' : ''}>
                                <label for="${window.id}-selection-dependency" class="form-check-label"><op:translate
                                        key="WINDOW_PROPERTIES_SELECTION_DEPENDENCY_ACTIVATE"/></label>
                            </div>
                        </div>

                            <%-- BeanShell --%>
                            <c:remove var="beanShellContent"/>
                        <c:if test="${window.beanShell}">
                            <c:set var="beanShellContent" value="in"/>
                        </c:if>

                        <div class="mb-3">
                            <label for="${window.id}-beanShell"><op:translate
                                    key="WINDOW_PROPERTIES_DYNAMIC_PROPERTIES"/></label>
                            <div class="form-check mb-2">
                                <input id="${window.id}-beanShell" type="checkbox" name="bshActivation" value="1"
                                       class="form-check-input"
                                       onclick="toggleBeanShell(this)" ${window.beanShell ? 'checked="checked"' : ''}>
                                <label for="${window.id}-beanShell" class="form-check-label"><op:translate
                                        key="WINDOW_PROPERTIES_BEAN_SHELL"/></label>
                            </div>

                            <div id="${window.id}-beanShell-content" class="collapse ${window.beanShell ? 'in' : ''}">
                                <textarea rows="5" name="bshScript"
                                          class="form-control mb-2">${window.beanShellContent}</textarea>

                                <div class="card border-info">
                                    <div class="card-body">
                                        <p class="card-title text-info mb-0">
                                            <a href="#${window.id}-beanShell-example" class="no-ajax-link"
                                               data-toggle="collapse">
                                                <i class="glyphicons glyphicons-basic-circle-info"></i>
                                                <span><op:translate key="WINDOW_PROPERTIES_BEAN_SHELL_EXAMPLE"/></span>
                                            </a>
                                        </p>

                                        <div id="${window.id}-beanShell-example" class="collapse mt-3">
                                            <p>Implicits variables :</p>
                                            <dl>
                                                <dt>pageParamsEncoder</dt>
                                                <dd>Parameters encoder, decoded to <code>List&lt;String&gt;</code></dd>

                                                <dt>windowsProperties</dt>
                                                <dd>Window dynamic properties: <code>Map&lt;String, String&gt;</code></dd>

                                                <dt>osivia.dynamicCSSClasses</dt>
                                                <dd>CSS class names separated by a space: <code>css1 css2</code></dd>
                                            </dl>

                                            <pre class="mb-0">
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

                        <%-- Satellite --%>
                        <c:if test="${not empty window.satellites}">
                            <div class="mb-3">
                        <label for="${window.id}-satellite"><op:translate key="WINDOW_PROPERTIES_SATELLITE"/></label>
                                    <select id="${window.id}-satellite" name="satellite" class="form-control">
                                        <c:forEach var="satellite" items="${window.satellites}">
                                            <option value="${satellite.key}" ${satellite.key eq window.selectedSatellite ? 'selected="selected"' : ''}>${satellite.value}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                        </c:if>
                    </fieldset>

                    <div class="text-right">
                        <button type="button" class="btn btn-secondary" onclick="closeFancybox()">
                            <span><op:translate key="CANCEL"/></span>
                        </button>

                        <button type="submit" class="btn btn-primary">
                            <span><op:translate key="SAVE" /></span>
                        </button>
                    </div>
                </form>
            </div>
        </c:forEach>
    </div>
</c:if>


<%-- Modal --%>
<div id="osivia-modal" class="modal fade" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <%-- Header --%>
            <div class="modal-header d-none">
                <h5 class="modal-title"></h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>

            <%-- Body --%>
            <div class="modal-body">
                <div class="dyna-region">
                    <div id="modal-region">
                        <div class="dyna-window">
                            <div id="modal-window" class="partial-refresh-window">
                                <div class="dyna-window-content">
                                    <div class="p-4 text-center">
                                        <div class="spinner-border" role="status">
                                            <span class="sr-only">Loading...</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <%-- Footer --%>
            <div class="modal-footer d-none">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">
                    <span><op:translate key="CLOSE" /></span>
                </button>
            </div>
        </div>
    </div>

    <div class="modal-clone d-none"></div>
</div>


<%-- Reload --%>
<c:set var="urls" value="${requestScope['osivia.session.reload.urls']}" />
<c:if test="${not empty urls}">
    <div id="session-reload" data-reload="true" data-urls="${urls}"></div>
</c:if>
