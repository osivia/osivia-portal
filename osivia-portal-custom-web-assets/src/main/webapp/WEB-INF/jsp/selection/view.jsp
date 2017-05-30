<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>


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
                    <i class="glyphicons glyphicons-remove"></i>
                </a>
            </li>
        </c:forEach>
    </ul>
    
    <div class="delete-all">
        <a href="<portlet:actionURL>
                    <portlet:param name="action" value="deleteAll" />
                </portlet:actionURL>">
            <op:translate key="SELECTION_PORTLET_DELETE_ALL" />
        </a>
    </div>

</div>
