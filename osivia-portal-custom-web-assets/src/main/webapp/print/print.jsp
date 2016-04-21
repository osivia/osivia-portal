<!DOCTYPE html>
<html>

<head>
<meta charset="UTF-8">
<title>${param.title}</title>

<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

<link rel="stylesheet" href="/osivia-portal-custom-web-assets/css/print.min.css">
<link rel="stylesheet" href="/osivia-portal-custom-web-assets/css/osivia.min.css">

<link rel="stylesheet" href="/osivia-portal-custom-web-assets/css/glyphicons.min.css">
<link rel="stylesheet" href="/osivia-portal-custom-web-assets/css/glyphicons-1.8.min.css">

<script type="text/javascript" src="/osivia-portal-custom-web-assets/components/jquery/jquery-1.8.3.min.js"></script>
<script type="text/javascript" src="/osivia-portal-custom-web-assets/js/jquery-integration.min.js"></script>
<script type="text/javascript" src="/osivia-portal-custom-web-assets/js/print.js"></script>
</head>


<body>
    <div class="container-fluid">
        <div class="page-header">
            <h1 class="h3">${param.title}</h1>
        </div>

        <div class="print-content" data-id="${param.id}"></div>
    </div>
</body>

</html>
