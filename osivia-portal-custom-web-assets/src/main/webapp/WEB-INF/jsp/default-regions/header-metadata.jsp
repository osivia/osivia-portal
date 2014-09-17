<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="internationalization" prefix="is" %>


<meta charset="UTF-8">
<title>${requestScope['osivia.header.title']} - <is:getProperty key="BRAND" /></title>

<c:forEach var="meta" items="${requestScope['osivia.header.metadata']}">
<meta name="${meta.key}" content="${meta.value}">
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
