var tableToolbarXhr;


$JQry(function() {
    var isChromeAndroid = /Chrome/i.test(navigator.userAgent) && /Mobile/i.test(navigator.userAgent) && /Android/i.test(navigator.userAgent);
    var $container = $JQry(".portal-table-container");
    var previousIndex = -1;

    if (!isChromeAndroid && !$container.data("loaded")) {
        // Selectable
        $JQry(".portal-table-selectable").selectable({
            cancel: "a, button, .portal-table-selectable-cancel",
            filter: ".portal-table-selectable-filter",

            selecting: function(event, ui) {
                var $selecting = $JQry(ui.selecting);
                var $selectable = $selecting.closest(".portal-table-selectable");
                var $selectee = $selectable.find(".portal-table-selectable-filter");
                var currentIndex = $selectee.index(ui.selecting);

                if (event.shiftKey && previousIndex > -1) {
                    $selectee.slice(Math.min(previousIndex, currentIndex), Math.max(previousIndex, currentIndex) + 1).addClass("ui-selected");
                } else {
                    previousIndex = currentIndex;
                }
            },

            stop: function(event, ui) {
                var $target = $JQry(event.target);

                // Update toolbar
                updateTableToolbar($target);
            }
        });


        // Checkbox
        $JQry(".portal-table-checkbox a").click(function(event) {
            var $target = $JQry(event.target).closest("a");
            var $selectee;

            if ($target.closest(".portal-table-header-group").length) {
                var $table = $target.closest(".portal-table");
                $selectee = $table.find(".portal-table-selectable-filter");

                if ($target.hasClass("checked")) {
                    $selectee.removeClass("ui-selected");
                    $target.removeClass("checked");
                } else {
                    $selectee.addClass("ui-selected");
                    $target.addClass("checked");
                }
            } else {
                $selectee = $target.closest(".portal-table-selectable-filter");

                if ($selectee.hasClass("ui-selected")) {
                    $selectee.removeClass("ui-selected");
                } else {
                    $selectee.addClass("ui-selected");
                }
            }

            // Update toolbar
            updateTableToolbar($target);
        });


        // Double click
        $JQry(".portal-table-selectable-filter[data-double-click-target]").dblclick(function(event) {
            var $target = $JQry(event.target);
            var $selectee = $target.closest(".portal-table-selectable-filter");
            var $link = $selectee.find($selectee.data("double-click-target")).first();

            if ($link.length) {
                $link.get(0).click();
            } else {
                console.error("Double click event failed: link not found.");
            }
        });


        // Update scrollbar width
        updateTableScrollbarWidth();


        // Loaded indicator
        $container.data("loaded", true);
    }
});


$JQry(window).resize(function() {
    // Update scrollbar width
    updateTableScrollbarWidth();
});


function updateTableToolbar($target) {
    var $container = $target.closest(".portal-table-container");
    var allSelected = ($container.find(".portal-table-selectable-filter").length === $container.find(".ui-selected").length);
    var $selectAll = $container.find(".portal-table-header-group .portal-table-checkbox a");
    var $toolbarContainer = $container.siblings(".portal-table-toolbar-container");
    var $toolbar = $toolbarContainer.find(".portal-table-toolbar");
    var $rows = $container.find(".portal-table-row");
    var $selected = $container.find(".ui-selected");
    var indexes = "";
    var $menubar = $JQry("#menubar");
    var $menubarItems = $menubar.find("a");

    // Disable toolbar
    $toolbar.find("a").addClass("disabled");

    // Abort previous AJAX request
    if (tableToolbarXhr && tableToolbarXhr.readyState !== 4) {
        tableToolbarXhr.abort();
    }


    // Build selected indexes
    $selected.each(function(index, element) {
        var $element = $JQry(element);
        var rowIndex = $rows.index($element);

        if (indexes.length) {
            indexes += ",";
        }

        indexes += rowIndex;
    });


    if (indexes.length) {
        // AJAX
        tableToolbarXhr = jQuery.ajax({
            url: $toolbarContainer.data("url"),
            async: true,
            cache: false,
            data: {
                indexes: indexes
            },
            dataType: "html",
            success : function(data, status, xhr) {
                $toolbar.html(data);

                // Call jQuery.ready() events
                $JQry(document).ready();
            }
        });

        $menubarItems.addClass("disabled");
    } else {
        $toolbar.html("");

        $menubarItems.removeClass("disabled");
    }


    // Update "select all" checkbox
    if ($selectAll.hasClass("checked") && !allSelected) {
        $selectAll.removeClass("checked");
    } else if (!$selectAll.hasClass("checked") && allSelected) {
        $selectAll.addClass("checked");
    }
}


function updateTableScrollbarWidth() {
    var $filler = $JQry(".portlet-filler");

    $filler.each(function(index, element) {
        var $element = $JQry(element);
        var width = Math.round($element.innerWidth() - $element.children().outerWidth(true));
        var $header = $element.find(".portal-table-header-group");

        // Update header
        $header.css({
            "padding-right": width
        });
    });
}
