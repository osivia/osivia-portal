<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

    <meta charset="UTF-8">
	<title><c:out value="${requestScope['osivia.header.title']}" /></title>
<c:forEach var="metainfo" items="${requestScope['osivia.header.metadata']}">
	<meta name="<c:out value="${metainfo.key}" />" value="<c:out value="${metainfo.value}" />" /></c:forEach>
    <meta content="text">
    <meta http-equiv="default-style" content="main_css">
    <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no">     
	<meta name="generator" content="OSIVIA Portal" />