<!DOCTYPE html>
<%@ taglib prefix="p" uri="portal-layout"%>


<html>

<head>
    <title>Popup</title>
    
    <p:headerContent />
    <p:theme themeName="osivia-popup" />
</head>


<body id="popup-body">
    <p:region regionName="popup_header" />

    <p:region regionName="maximized" />
       
    <!-- Notifications -->
    <p:region regionName="notifications" />
    <!-- AJAX scripts -->
    <p:region regionName="AJAXScripts" />
    <!-- AJAX footer -->
    <p:region regionName="AJAXFooter" />
</body>

</html>
