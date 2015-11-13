<!DOCTYPE html>


<html>

<head>
    <title>${param.title}</title>
    
    <meta charset="UTF-8">

    <meta name="application-name" content="OSIVIA">

    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    
    
    <link rel="stylesheet" href="/osivia-portal-custom-web-assets/css/print.min.css">
    <link rel="stylesheet" href="/osivia-portal-custom-web-assets/css/osivia.min.css">
    
    <link rel="stylesheet" href="/osivia-portal-custom-web-assets/components/glyphicons/css/glyphicons.css">
    <link rel="stylesheet" href="/osivia-portal-custom-web-assets/components/glyphicons/css/glyphicons-halflings.css">
    <link rel="stylesheet" href="/osivia-portal-custom-web-assets/components/glyphicons/css/glyphicons-filetypes.css">
    <link rel="stylesheet" href="/osivia-portal-custom-web-assets/components/glyphicons/css/glyphicons-social.css">
    
    <script type='text/javascript' src='/osivia-portal-custom-web-assets/components/jquery/jquery-1.8.3.min.js'></script>
    <script type='text/javascript' src='/osivia-portal-custom-web-assets/js/print.js'></script>
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
