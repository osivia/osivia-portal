<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="internationalization" prefix="is" %>


<meta charset="UTF-8">
<title>${requestScope['osivia.header.title']} - <is:getProperty key="BRAND" /></title>
<meta http-equiv="X-UA-Compatible" content="IE=edge">

<c:forEach var="meta" items="${requestScope['osivia.header.metadata']}">
<meta name="${meta.key}" content="${fn:escapeXml(meta.value)}">
</c:forEach>

<c:if test="${not empty requestScope['osivia.header.canonical.url']}">
<link rel="canonical" href="${requestScope['osivia.header.canonical.url']}">
</c:if>


<c:if test="${requestScope['osivia.spaceSite']}">
<script type="application/ld+json">
{
  "@context": "http://schema.org",
  "@type": "WebSite",
  "url": "${requestScope['osivia.header.portal.url']}",
  "potentialAction": {
    "@type": "SearchAction",
    "target": "${requestScope['osivia.header.portal.url']}/web/search?q={search_term}",
    "query-input": "required name=search_term"
  }
}
</script>
</c:if>
