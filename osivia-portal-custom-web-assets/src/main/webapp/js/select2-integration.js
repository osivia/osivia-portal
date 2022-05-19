$JQry(function() {

    $JQry("select.select2.select2-default").each(function(index, element) {
        var $element = $JQry(element);
        var url = $element.data("url");
        var options = {
            minimumInputLength : 0,
            theme : "bootstrap",
            width : "resolve"
        };

        if (url !== undefined) {
            options["ajax"] = {
                url : url,
                dataType : "json",
                delay : 250,
                data : function(params) {
                    return {
                        filter : params.term,
                    };
                },
                processResults : function(data, params) {
                    return {
                        results : data
                    };
                },
                cache : true
            };

            options["escapeMarkup"] = function(markup) {
                return markup;
            };

            options["templateResult"] = function(params) {
                var $result = $JQry(document.createElement("span"));

                if (params.loading) {
                    $result.text(params.text);
                } else {
                    $result.text(params.text);
                    if (params.level !== undefined) {
                        $result.addClass("level-" + params.level);
                    }
                    if (params.optgroup) {
                        $result.addClass("optgroup");
                    }
                }

                return $result;
            };

            options["templateSelection"] = function(params) {
                return params.text;
            };
        }


        // Internationalization
        options["language"] = {};
        if ($element.data("input-too-short") !== undefined) {
            options["language"]["inputTooShort"] = function() {
                return $element.data("input-too-short");
            }
        }
        if ($element.data("error-loading") !== undefined) {
            options["language"]["errorLoading"] = function() {
                return $element.data("error-loading");
            }
        }
        if ($element.data("loading-more") !== undefined) {
            options["language"]["loadingMore"] = function() {
                return $element.data("loading-more");
            }
        }
        if ($element.data("searching") !== undefined) {
            options["language"]["searching"] = function() {
                return $element.data("searching");
            }
        }
        if ($element.data("no-results") !== undefined) {
            options["language"]["noResults"] = function() {
                return $element.data("no-results");
            }
        }


        // Force width to 100%
        $element.css({
            width : "100%"
        });


        $element.select2(options);


        // Clear button
        $element.siblings().find("button[name=clear]").click(function(event) {
            $element.val("");
            $element.trigger("change");
        });


        // Auto submit on change
        if ($element.data("onchange") == "submit") {
            $element.on("select2:unselecting", function(event) {
                var $target = $JQry(event.target);

                $element.data("unselecting", true);
            });

            $element.change(function(event) {
                var $form = $element.closest("form");
                var $submit = $form.find("button[type=submit][name=save]");

                $submit.click();
            });

            $element.on("select2:opening", function(event) {
                var $target = $JQry(event.target);

                if ($target.data("unselecting")) {
                    event.preventDefault();
                }
            });
        }
    });


    $JQry("select.select2.select2-person").each(function(index, element) {
        var $element = $JQry(element);
        var url = $element.data("url");
        var minimumInputLength = $element.data("minimum-input-length");
        var options = {
            minimumInputLength : (minimumInputLength ? minimumInputLength : 3),
            theme : "bootstrap",
            width : "resolve"
        };

        if (url !== undefined) {
            options["ajax"] = {
                url : url,
                dataType : "json",
                delay : 1000,
                data : function(params) {
                    return {
                        filter : params.term,
                        page : params.page
                    };
                },
                processResults : function(data, params) {
                    params.page = params.page || 1;

                    return {
                        results : data.items,
                        pagination : {
                            more : (params.page * data.pageSize) < data.total
                        }
                    };
                },
                cache : true
            };

            // Result template
            options["templateResult"] = function(params) {
                var $result, $personAvatar, $avatar, $icon, $personTitle;

                $result = $JQry(document.createElement("div"));

                if (params.loading) {
                    $result.text(params.text);
                } else {
                    $result.addClass("person");

                    // Person avatar
                    $personAvatar = $JQry(document.createElement("div"));
                    $personAvatar.addClass("person-avatar");
                    $personAvatar.appendTo($result);

                    if (params.avatar) {
                        // Avatar
                        $avatar = $JQry(document.createElement("img"));
                        $avatar.attr("src", params.avatar);
                        $avatar.attr("alt", "");
                        $avatar.appendTo($personAvatar);
                    } else {
                        // Icon
                        $icon = $JQry(document.createElement("i"));
                        $icon.addClass("glyphicons glyphicons-user");
                        $icon.text("");
                        $icon.appendTo($personAvatar);
                    }

                    // Person title
                    $personTitle = $JQry(document.createElement("div"));
                    $personTitle.addClass("person-title");
                    $personTitle.text(params.displayName);
                    $personTitle.appendTo($result);
                }

                return $result;
            };

            // Selection template
            options["templateSelection"] = function(params) {
                var $selection, $personTitle;

                // Selection
                $selection = $JQry(document.createElement("div"));
                $selection.addClass("workspace-member-selection");

                // Person title
                $personTitle = $JQry(document.createElement("div"));
                $personTitle.addClass("person-title");
                if (params.displayName === undefined) {
                    $personTitle.text(params.text);
                } else {
                    $personTitle.text(params.displayName);
                }
                $personTitle.appendTo($selection);

                return $selection;
            };
        }


        // Internationalization
        options["language"] = {};
        if ($element.data("input-too-short") !== undefined) {
            options["language"]["inputTooShort"] = function() {
                return $element.data("input-too-short");
            }
        }
        if ($element.data("error-loading") !== undefined) {
            options["language"]["errorLoading"] = function() {
                return $element.data("error-loading");
            }
        }
        if ($element.data("loading-more") !== undefined) {
            options["language"]["loadingMore"] = function() {
                return $element.data("loading-more");
            }
        }
        if ($element.data("searching") !== undefined) {
            options["language"]["searching"] = function() {
                return $element.data("searching");
            }
        }
        if ($element.data("no-results") !== undefined) {
            options["language"]["noResults"] = function() {
                return $element.data("no-results");
            }
        }


        // Force width to 100%
        $element.css({
            width : "100%"
        });


        $element.select2(options);


        // Clear button
        $element.siblings().find("button[name=clear]").click(function(event) {
            $element.val("");
            $element.trigger("change");
        });


        // Auto submit on change
        if ($element.data("onchange") == "submit") {
            $element.on("select2:unselecting", function(event) {
                var $target = $JQry(event.target);

                $element.data("unselecting", true);
            });

            $element.change(function(event) {
                var $form = $element.closest("form");
                var $submit = $form.find("button[type=submit][name=save]");

                $submit.click();
            });

            $element.on("select2:opening", function(event) {
                var $target = $JQry(event.target);

                if ($target.data("unselecting")) {
                    event.preventDefault();
                }
            });
        }
    });


    $JQry("select.select2.select2-tags").each(function(index, element) {
        var $element = $JQry(element);
        var options = {
            minimumInputLength : 1,
            tags: true,
            theme: "bootstrap",
            width: "resolve"
        };


        // Internationalization
        options["language"] = {};
        if ($element.data("input-too-short") !== undefined) {
            options["language"]["inputTooShort"] = function() {
                return $element.data("input-too-short");
            }
        }


        // Force width to 100%
        $element.css({
            width : "100%"
        });


        $element.select2(options);
    });
});
