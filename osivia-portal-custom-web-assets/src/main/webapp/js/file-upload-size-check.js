$JQry(function () {

    $JQry("input[type=file][data-max-size]").each(function (index, element) {
        var $input = $JQry(element);

        if (!$input.data("size-check-loaded")) {
            var maxSize = $input.data("max-size");
            var message = $input.data("size-limit-exceeded-message");
            var $submit = $JQry("#" + $input.data("submit"));

            $input.change(function (event) {
                var $target = $JQry(event.currentTarget);
                var files = event.currentTarget.files;
                var totalSize = 0;

                for (var i = 0; i < files.length; i++) {
                    totalSize += files[i].size;
                }

                if (totalSize > maxSize) {
                    // Clear file input
                    $target.val("");

                    // Add error class to form group
                    var $formGroup = $target.closest(".form-group");
                    $formGroup.addClass("has-error");

                    if (message) {
                        // Add error message
                        var $message = $JQry(document.createElement("p"));
                        $message.addClass("help-block");
                        $message.text(message);

                        $target.closest(".form-group, [class*=col]").append($message);
                    }
                } else if ($submit) {
                    $submit.click();
                }
            });

            $input.data("size-check-loaded", true);
        }
    });

});
