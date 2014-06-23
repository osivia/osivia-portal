<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<ol class="breadcrumb hidden-xs">
    <c:forEach var="child" items="${requestScope['osivia.breadcrumb'].childs}">
        <li>
            <a href="${child.url}">${child.name}</a>
        </li>
    </c:forEach>
</ol>
