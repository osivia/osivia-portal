<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects/>

<div>
	<form method="post" action="<portlet:actionURL />">	
		<label>Identifiant de sélection : </label>
        <input type="text" name="selectionId" value="<%=renderRequest.getAttribute("selectionId") %>" />
		<input type="submit" name="save" value="Enregistrer" />
		<input type="submit" name="cancel" value="Annuler" />
	</form>
</div>
