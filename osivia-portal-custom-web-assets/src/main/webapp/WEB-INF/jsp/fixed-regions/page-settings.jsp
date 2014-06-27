<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="internationalization" prefix="is"%>
<%@ taglib uri="/WEB-INF/tld/formatter.tld" prefix="formatter" %>


<c:set var="commandUrl" value="${requestScope['osivia.toolbarSettings.commandURL']}" />
<c:set var="currentPage" value="${requestScope['osivia.toolbarSettings.page']}" />
<c:set var="portalId"><formatter:safeId portalObjectId="${currentPage.portal.id}" /></c:set>
<c:set var="currentPageId"><formatter:safeId portalObjectId="${currentPage.id}" /></c:set>
<c:set var="currentPageDisplayName"><formatter:displayName object="${currentPage}"/></c:set>

<c:if test="${requestScope['osivia.toolbarSettings.draftPage']}">
    <c:set var="draftModeChecked" value="checked" />
</c:if>

<c:if test="${requestScope['osivia.toolbarSettings.cmsTemplated']}">
    <c:set var="propertiesDisabled" value="disabled" />
</c:if>


<script type="text/javascript">
// Variables used by JStree integration
var portalId = '${portalId}';
var currentPageId = '${currentPageId}';

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

// Window properties : mobile collapse check event
$JQry(document).ready(function($) {
    $("input[name=mobileCollapse]").change(function() {
    	var $form = $(this).parents("form");
    	var $displayTitle = $form.find("input[name=displayTitle]");
    	var $bootstrapPanelStyle = $form.find("input[name=bootstrapPanelStyle]");
    	
    	var checked = $(this).is(":checked");
    	
    	if (checked) {
    		// Force checked value
    		$displayTitle.prop("checked", true);
    		$bootstrapPanelStyle.prop("checked", true);
    	}
    	
    	// Toggle disabled state
    	$displayTitle.prop("disabled", checked);
    	$bootstrapPanelStyle.prop("disabled", checked);
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
                            <span class="input-group-addon"><span class="glyphicons halflings filter"></span></span>
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
                            <span class="input-group-addon"><span class="glyphicons halflings filter"></span></span>
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
                            <span class="input-group-addon"><span class="glyphicons halflings filter"></span></span>
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
                            <span class="input-group-addon"><span class="glyphicons halflings filter"></span></span>
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
                            <option ${categorySelected}>${category.value}</option>
                            <c:remove var="categorySelected" />
                        </c:forEach>
                    </select>
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
                        <span class="input-group-addon"><span class="glyphicons halflings filter"></span></span>
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
            <div class="form-group">
                <p class="help-block"><is:getProperty key="PAGE_SUPPRESSION_CONFIRM_MESSAGE" /></p>
                <div class="text-center">
                    <button type="submit" class="btn btn-default btn-warning">
                        <span class="glyphicons halflings warning-sign"></span>
                        <is:getProperty key="YES" />
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
                <span class="input-group-addon"><span class="glyphicons halflings filter"></span></span>
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

            <p class="help-block"><is:getProperty key="PORTLET_SUPPRESSION_CONFIRM_MESSAGE" /></p>
            <div class="text-center">
                <button type="submit" class="btn btn-default btn-warning" onclick="selectWindow(this.form)">
                    <span class="glyphicons halflings warning-sign"></span>
                    <is:getProperty key="YES" />
                </button>
                <button type="button" class="btn btn-default" onclick="closeFancybox()"><is:getProperty key="NO" /></button>
            </div>
        </form>
    </div>
    
    <!-- Windows settings -->
    <formatter:windowsSettings />

</div>
