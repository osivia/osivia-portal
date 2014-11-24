<!DOCTYPE html>
<%@ taglib prefix="p" uri="portal-layout"%>


<html>

<head>
    <title>Popup</title>
    
    <p:headerContent />
    <p:theme themeName="osivia-popup" />
</head>


<body id="popup-body">
    <!-- AJAX scripts -->
    <p:region regionName="AJAXScripts" />

    <p:region regionName="popup_header" />

    <div class="container-fluid">
        <p:region regionName="notifications" />
        <p:region regionName="popup" />
    </div>

	<!-- AJAX footer -->
	<p:region regionName="AJAXFooter" />
</body>

</html>
