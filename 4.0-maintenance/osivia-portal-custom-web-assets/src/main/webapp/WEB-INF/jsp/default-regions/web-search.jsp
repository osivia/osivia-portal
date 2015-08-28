<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="internationalization" prefix="is" %>


<c:set var="searchTitle"><is:getProperty key="SEARCH_TITLE" /></c:set>
<c:set var="searchPlaceholder"><is:getProperty key="SEARCH_PLACEHOLDER" /></c:set>


<div class="pull-right hidden-xs">
    <form class="form-inline" action="${requestScope['osivia.search.web.url']}" method="get" role="search">
        <div class="form-group">
            <label class="sr-only" for="search-input"><is:getProperty key="SEARCH" /></label>
            <div class="input-group input-group-sm">
                <input id="search-input" type="text" name="q" class="form-control" placeholder="${searchPlaceholder}">
                <span class="input-group-btn">
                    <button type="submit" class="btn btn-default" title="${searchTitle}" data-toggle="tooltip" data-placement="bottom">
                        <i class="halflings halflings-search"></i>
                    </button>
                </span>
            </div>
        </div>
    </form>
</div>
