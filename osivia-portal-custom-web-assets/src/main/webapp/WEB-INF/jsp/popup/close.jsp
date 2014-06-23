<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="fr" lang="fr">

<head>


	<script type="text/javascript" src="/osivia-portal-custom-web-assets/js/jquery.js"></script>	
	<script type='text/javascript' src='/portal-ajax/dyna/prototype.js'></script>
	<script type='text/javascript' src='/portal-ajax/dyna/effects.js'></script>
	<script type='text/javascript' src='/portal-ajax/dyna/dyna.js'></script>


</head>

<body>


  <%=request.getParameter("url")%>

    <script type="text/javascript">
    

    parent.setCallbackParams(  null,    '<%=request.getParameter("url")%>');


     	parent.jQuery.fancybox.close();

        
     </script>
    


</body>

</html>