<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="internationalization" prefix="is" %>


<c:set var="backTitle"><is:getProperty key="BACK" /></c:set>


<c:if test="${not empty requestScope['osivia.back.url']}">
    <div class="btn-toolbar hidden-xs">
        <a href="${requestScope['osivia.back.url']}" title="${backTitle}" class="btn btn-primary" data-toggle="tooltip" data-placement="bottom">
            <i class="halflings halflings-arrow-left"></i>
            <span class="sr-only">${backTitle}</span>
        </a>
    </div>
</c:if>
