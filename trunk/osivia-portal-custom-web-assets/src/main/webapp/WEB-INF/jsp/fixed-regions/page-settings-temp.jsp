<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/internationalization.tld" prefix="is" %>
<%@ taglib uri="/WEB-INF/tld/formatter.tld" prefix="f" %>

<c:set var="currentPage" value="${requestScope['osivia.toolbarSettings.page']}" />
<c:set var="commandUrl" value="${requestScope['osivia.toolbarSettings.commandURL']}" />

<f:setSafeId var="portalId" portalObjectId="${currentPage.portal.id}" />

<!-- Fancybox de création de page -->
<div class="fancybox-content">
    <div id="page-creation">
        <form action="${commandUrl}" method="get" class="fancybox-form">
            <input type="hidden" name="action" value="createPage" />
            <input type="hidden" name="jstreePageParentSelect" value="${portalId}" />
            <input type="hidden" name="jstreePageModelSelect" />
            
            <div class="fancybox-table">
                <div class="fancybox-table-row">
                    <div class="fancybox-table-cell fancybox-label required">
                        <is:getProperty key="NEW_PAGE_NAME" />
                    </div>
                    <div class="fancybox-table-cell">
                        <input type="text" name="name" required />
                    </div>
                    <div class="fancybox-table-cell">&nbsp;</div>
                    <div class="fancybox-table-cell">&nbsp;</div>
                </div>

                <div class="fancybox-table-row">
                    <div class="fancybox-table-cell fancybox-label">
                        <is:getProperty key="NEW_PAGE_MODEL" />
                    </div>
                    <div class="fancybox-table-cell">
                        <input type="text" onkeyup="jstreeSearch('jstreePageModelSelect', this.value)" class="filter" placeholder='<is:getProperty key="JSTREE_FILTER" />' />                       
                    </div>
                
                    <div class="fancybox-table-cell fancybox-label required">
                        <is:getProperty key="NEW_PAGE_PARENT" />
                    </div>
                    <div class="fancybox-table-cell">
                        <input type="text" onkeyup="jstreeSearch('jstreePageParentSelect', this.value)" class="filter" placeholder='<is:getProperty key="JSTREE_FILTER" />' />                                               
                    </div>                    
                </div>
                
                <div class="fancybox-table-row">
                    <div class="fancybox-table-cell fancybox-label fancybox-upper">
                        <label for="checkboxNoModel">
                            <is:getProperty key="NEW_PAGE_NO_MODEL" />
                        </label>                        
                        <input id="checkboxNoModel" type="checkbox" onchange="jstreeToggleLock('jstreePageModelSelect', this.checked)" class="inline-checkbox" />
                    </div>
                    
                    <div class="fancybox-table-cell">
                        <div id="jstreePageModelSelect" class="jstree-select-unique">
                            <%=formatter.formatHTMLTreePortalObjects(currentPage, context, "jstreePageModelSelect") %>
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
