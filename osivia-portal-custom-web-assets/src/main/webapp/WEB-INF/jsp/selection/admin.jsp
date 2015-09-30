<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>

<portlet:defineObjects/>

<div>
	<form method="post" action="<portlet:actionURL />">	
		<label><op:translate key="SELECTION_PORTLET_ID_LABEL" /></label>
        <input type="text" name="selectionId" value='<c:out value="${requestScope['selectionId']}" />' />
		<input type="submit" name="save" value='<op:translate key="SAVE" />' />
		<input type="submit" name="cancel" value='<op:translate key="CANCEL" />' />
	</form>
</div>
