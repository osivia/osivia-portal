<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="fr" lang="fr">

<head>

	<link rel="stylesheet" media="print" type="text/css" href="/osivia-portal-custom-web-assets/js/print/print.css" />
	<link rel="stylesheet" media="screen" type="text/css" href="/osivia-portal-custom-web-assets/js/print/print.css" />
	
	<script type="text/javascript" src="/osivia-portal-custom-web-assets/js/jquery/jquery-1.8.3.min.js"></script>	
	<script type='text/javascript' src='/portal-ajax/dyna/prototype.js'></script>
	<script type='text/javascript' src='/portal-ajax/dyna/effects.js'></script>
	<script type='text/javascript' src='/portal-ajax/dyna/dyna.js'></script>

<script type="text/javascript">
function callHtmlPortlet()
{
	//self.opener.focus();
	var htmlportlet = window.opener.document.getElementById('<%=request.getParameter("portlet")%>').innerHTML;
	$('print').update(htmlportlet);
	
}
</script>   

</head>

<body>
 <%=request.getParameter("title")%>
<hr/>


<div id="print" style="display:inline"></div>



<script type="text/javascript">
callHtmlPortlet();

var jQuery = jQuery.noConflict();
jQuery(window).load(function() {
	window.print();
	//window.close();
	});
</script> 



</body>

</html>