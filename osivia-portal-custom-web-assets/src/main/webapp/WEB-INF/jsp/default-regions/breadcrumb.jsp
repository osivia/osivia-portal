<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>

<nav>
    <h2 class="hidden"><op:translate key="BREADCRUMB_TITLE" /></h2>
    <ol class="breadcrumb hidden-xs">
        <c:forEach var="child" items="${requestScope['osivia.breadcrumb'].childs}">
            <li>
                <a href="${child.url}">${child.name}</a>
            </li>
        </c:forEach>
    </ol>
</nav>
