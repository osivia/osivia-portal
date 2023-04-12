$JQry(function () {
    $JQry(".sortable-default").each(function (index, element) {
        const $element = $JQry(element);

        if (!$element.data("sortable-loaded")) {
            $element.sortable({
                axis: "y",
                cursor: "move",
                forcePlaceholderSize: true,
                placeholder: "bg-info",
                tolerance: "pointer",

                update: function (event, ui) {
                    const $form = $element.closest("form");
                    const $input = $form.find("input[name=order]");
                    var order = "";

                    $element.children().each(function (index, element) {
                        if (index > 0) {
                            order += "|";
                        }
                        order += $JQry(element).data("id");
                    });

                    // Update input value
                    $input.val(order);
                }
            });

            $element.disableSelection();


            $element.data("sortable-loaded", true);
        }
    });
});
