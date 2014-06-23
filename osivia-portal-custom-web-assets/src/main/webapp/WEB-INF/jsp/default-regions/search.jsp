<%@ taglib uri="internationalization" prefix="is" %>

<script type="text/javascript">

function onsubmitGlobalSearch(form) {
   var searchUrl = "${requestScope['osivia.search.url']}";
   
   searchUrl = searchUrl.replace("__REPLACE_KEYWORDS__", form.keywords.value);
   form.action = searchUrl;
}

</script>


<div class="pull-right hidden-xs">
    <form class="form-inline" onsubmit="return onsubmitGlobalSearch(this);" method="post" role="search">
        <div class="form-group">
            <label class="sr-only" for="search-input">Search</label>
            <div class="input-group input-group-sm">
                <input id="search-input" type="text" name="keywords" class="form-control" placeholder='<is:getProperty key="SEARCH_PLACEHOLDER" />'>
                <span class="input-group-btn">
                    <button type="submit" class="btn btn-default" title='<is:getProperty key="SEARCH_TITLE" />' data-toggle="tooltip" data-placement="bottom">
                        <span class="glyphicons halflings search"></span>
                    </button>
                </span>
            </div>
        </div>
    </form>
</div>
