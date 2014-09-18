<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="internationalization" prefix="is" %>


<script type="text/javascript">

function onsubmitGlobalSearch(form) {
   var searchUrl = "${requestScope['osivia.search.url']}";
   
   searchUrl = searchUrl.replace("__REPLACE_KEYWORDS__", form.keywords.value);
   form.action = searchUrl;
}

</script>


<c:set var="searchTitle"><is:getProperty key="SEARCH_TITLE" /></c:set>
<c:set var="searchPlaceholder"><is:getProperty key="SEARCH_PLACEHOLDER" /></c:set>


<div class="pull-right hidden-xs">
    <form class="form-inline" onsubmit="return onsubmitGlobalSearch(this);" method="post" role="search">
        <div class="form-group">
            <label class="sr-only" for="search-input"><is:getProperty key="SEARCH" /></label>
            <div class="input-group input-group-sm">
                <input id="search-input" type="text" name="keywords" class="form-control" placeholder="${searchPlaceholder}">
                <span class="input-group-btn">
                    <button type="submit" class="btn btn-default" title="${searchTitle}" data-toggle="tooltip" data-placement="bottom">
                        <i class="glyphicons halflings search"></i>
                    </button>
                </span>
            </div>
        </div>
    </form>
</div>
