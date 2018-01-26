$JQry(function() {

    // Image error message
    $JQry("img[data-error-message]").on("error", function (event) {
        var $target = $JQry(event.target);
        var message = $target.data("error-message");
        var $message;
    
        if (message) {
            $message = $JQry(document.createElement("span"));
            $message.text(message);
    
            $target.replaceWith($message);
        }
    });

});
