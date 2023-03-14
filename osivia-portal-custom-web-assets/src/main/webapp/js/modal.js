$JQry(function () {

    $JQry("#osivia-modal").each(function (index, element) {
        const $element = $JQry(element);
        const loaded = $element.data("loaded");

        if (!loaded) {
            const modalElement = $element.get(0);

            modalElement.addEventListener('show.bs.modal', function(event) {
                const $currentTarget = $JQry(event.currentTarget);
                const $relatedTarget = $JQry(event.relatedTarget);
                const $dialog = $currentTarget.find(".modal-dialog");
                const $header = $dialog.find(".modal-header");
                const $footer = $dialog.find(".modal-footer");
                const $clone = $currentTarget.children(".modal-clone");
                const $window = $dialog.find(".dyna-window");
                const url = $relatedTarget.data("load-url");
                const callbackFunction = $relatedTarget.data("load-callback-function");
                const callbackFunctionArgs = $relatedTarget.data("load-callback-function-args");
                const title = $relatedTarget.data("title");
                const footer = $relatedTarget.data("footer");
                const size = $relatedTarget.data("size");

                // Header
                if (title) {
                    $header.find(".modal-title").text(title);
                    $header.removeClass("d-none");
                }

                // Footer
                if (footer) {
                    $footer.removeClass("d-none");
                }

                // Body
                $window.children().clone().appendTo($clone);
                $window.load(url, function () {
                    if (callbackFunction) {
                        window[callbackFunction](callbackFunctionArgs);
                    }

                    // jQuery events
                    $JQry(document).ready();
                });

                // Size
                if ((size === "lg") || (size === "large")) {
                    $dialog.addClass("modal-lg");
                } else if ((size === "sm") || (size === "small")) {
                    $dialog.addClass("modal-sm");
                }
            });

            modalElement.addEventListener('hide.bs.modal', function(event) {
                const $currentTarget = $JQry(event.currentTarget);
                const $relatedTarget = $JQry(event.relatedTarget);
                const $window = $currentTarget.find(".dyna-window");
                const callbackFunction = $relatedTarget.data("callback-function");
                const callbackFunctionArgs = $relatedTarget.data("callback-function-args");
                const callbackUrl = $relatedTarget.data("callback-url");

                $window.unbind("load");

                if (callbackFunction) {
                    window[callbackFunction](callbackFunctionArgs);
                }

                if (callbackUrl) {
                    const container = null;
                    const options = {
                            method: "post"
                        };
                    const eventToStop = null;
                    const callerId = null;

                    directAjaxCall(container, options, callbackUrl, eventToStop, callerId);
                }
            });

            modalElement.addEventListener('hidden.bs.modal', function(event) {
                const $currentTarget = $JQry(event.currentTarget);
                const $dialog = $currentTarget.find(".modal-dialog");
                const $header = $dialog.find(".modal-header");
                const $footer = $dialog.find(".modal-footer");
                const $clone = $currentTarget.children(".modal-clone");
                const $window = $dialog.find(".dyna-window");

                // Header
                $header.addClass("d-none");
                $header.find(".modal-title").empty();

                // Footer
                $footer.addClass("d-none");

                // Body
                $window.empty();
                $clone.children().appendTo($window);

                // Size
                $dialog.removeClass("modal-lg modal-sm");
            });

            $element.data("loaded", true);
        }
    });


    $JQry("#osivia-modal [data-close-modal=true]").each(function (index, element) {
        const modal = bootstrap.Modal.getOrCreateInstance(document.getElementById('osivia-modal'));

        modal.hide();
    });

});
