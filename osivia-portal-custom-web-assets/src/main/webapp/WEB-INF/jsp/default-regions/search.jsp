<%@ taglib uri="internationalization" prefix="is" %>

<script type="text/javascript">

function onsubmitGlobalSearch(form) {
   var searchUrl = "${requestScope['osivia.search.url']}";
   
   searchUrl = searchUrl.replace("__REPLACE_KEYWORDS__", form.keywords.value);
   form.action = searchUrl;
}

</script>


<form onsubmit="return onsubmitGlobalSearch(this);" method="post">
    <input type="text" id="search-text" name="keywords" placeholder='<is:getProperty key="SEARCH_PLACEHOLDER" />'>
    <button type="submit" id="search-submit"><is:getProperty key="SEARCH_SUBMIT" /></button>
</form>
