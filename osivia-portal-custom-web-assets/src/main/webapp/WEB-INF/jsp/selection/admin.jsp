<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/internationalization.tld" prefix="is" %>

<portlet:defineObjects/>

<div>
	<form method="post" action="<portlet:actionURL />">	
		<label><is:getProperty key="SELECTION_PORTLET_ID_LABEL" /></label>
        <input type="text" name="selectionId" value='<c:out value="${requestScope['selectionId']}" />' />
		<input type="submit" name="save" value='<is:getProperty key="SAVE" />' />
		<input type="submit" name="cancel" value='<is:getProperty key="CANCEL" />' />
	</form>
</div>
