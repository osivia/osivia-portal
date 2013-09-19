<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/internationalization.tld" prefix="is" %>


<portlet:defineObjects />

<div class="portlet-selection">
    <ul>
        <c:forEach var="item" items="${requestScope['selection']}">
            <li>
                <jsp:getProperty property="displayTitle" name="item" />
                <a href="<portlet:actionURL>
                            <portlet:param name="action" value="delete" />
                            <portlet:param name="id" value="${item.id}" />
                        </portlet:actionURL>">
                    <img src="${pageContext.request.contextPath}/images/cross.png" />
                </a>
            </li>
        </c:forEach>
    </ul>
    
    <div class="delete-all">
        <a href="<portlet:actionURL>
                    <portlet:param name="action" value="deleteAll" />
                </portlet:actionURL>">
            <is:getProperty key="SELECTION_PORTLET_DELETE_ALL" />
        </a>
    </div>

</div>
