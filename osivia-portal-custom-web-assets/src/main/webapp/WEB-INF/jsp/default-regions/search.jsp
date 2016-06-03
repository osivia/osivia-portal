<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>

<%@ page contentType="text/html" isELIgnored="false"%>


<c:set var="title"><op:translate key="SEARCH_TITLE" /></c:set>
<c:set var="placeholder"><op:translate key="SEARCH_PLACEHOLDER" /></c:set>


<div class="pull-right hidden-xs">
    <form action="${requestScope['osivia.search.url']}" method="post" class="form-inline" role="search">
        <div class="form-group">
            <label class="sr-only" for="search-input"><op:translate key="SEARCH" /></label>
            <div class="input-group input-group-sm">
                <input id="search-input" type="text" name="keywords" class="form-control" placeholder="${placeholder}">
                <span class="input-group-btn">
                    <button type="submit" class="btn btn-default" title="${title}" data-toggle="tooltip" data-placement="bottom">
                        <i class="halflings halflings-search"></i>
                    </button>
                </span>
            </div>
        </div>
    </form>
</div>
