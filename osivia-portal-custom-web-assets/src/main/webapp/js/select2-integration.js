$JQry(function() {
	
	$JQry("select.select2.select2-default").each(function(index, element) {
		var $element = $JQry(element),
			url = $element.data("url");
			loadingLabel = $element.data("loading-label");
			options = {
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
			
			options["minimumInputLength"] = 0;
			
			options["templateResult"] = function(params) {
				$result = $JQry(document.createElement("span"));
				
				if (params.loading) {
					if (loadingLabel !== undefined) {
						$result.text(loadingLabel);
					} else {
						$result.text(params.text);
					}
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
		
		
		if ($element.data("onchange") == "submit") {
			$element.on("select2:unselecting", function(event) {
				var $target = $JQry(event.target);
				
				$element.data("unselecting", true);
			});
			
			$element.change(function(event) {
				var $form = $element.closest("form"),
					$submit = $form.find("button[type=submit][name=save]");

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
	
});
