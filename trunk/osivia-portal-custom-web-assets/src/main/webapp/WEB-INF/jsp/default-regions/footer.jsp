<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ page contentType="text/html" isELIgnored="false"%>


<c:set var="userPages" value="${requestScope['osivia.siteMap'].userPages}" />

<ul class="footer-level-1">
    <c:forEach var="userPageL1" items="${userPages}" varStatus="status" >
        <li>
            <a href="${userPageL1.url}">${userPageL1.name}</a>            
            <c:if test="${fn:length(userPageL1.children) > 0}">
                <ul class="footer-level-2">
                    <c:forEach var="userPageL2" items="${userPageL1.children}">
                        <li>
                            <a href="${userPageL2.url}">${userPageL2.name}</a>                            
                            <c:if test="${fn:length(userPageL2.children) > 0}">
                                <ul class="footer-level-3">
                                    <c:forEach var="userPageL3" items="${userPageL2.children}">
                                        <li>
                                            <a href="${userPageL3.url}">${userPageL3.name}</a>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </c:if>
                        </li>
                    </c:forEach>
                </ul>
            </c:if>
        </li>
    </c:forEach>
</ul>
