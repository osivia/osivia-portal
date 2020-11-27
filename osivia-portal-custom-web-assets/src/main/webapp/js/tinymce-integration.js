$JQry(function() {
    tinymceInitialization();
});


function tinymceInitialization() {
    // TinyMCE default
    $JQry("textarea.tinymce.tinymce-default").each(function(index, element) {
        var $textarea = $JQry(element);
        var id = $textarea.attr("id");
        var editorUrl = $textarea.data("editor-url");

        if (editorUrl === undefined) {
            console.error("Editor URL is undefined.")
        } else if (tinymce.get(id)) {
            if (!$textarea.data("tinymce-default-loaded")) {
                tinymce.execCommand("mceRemoveEditor", true, id);
                tinymce.execCommand("mceAddEditor", true, id);
            }
        } else {
            tinymce.init({
                selector: "textarea.tinymce.tinymce-default",
                language: "fr_FR",
                plugins: "autosave code fullscreen lists media noneditable paste",
                external_plugins: {
                    "osivia-image": "/osivia-services-editor-helpers/js/image/plugin.min.js",
                    "osivia-link": "/osivia-services-editor-helpers/js/link/plugin.min.js"
                },

                branding: false,
                menubar: false,
                toolbar: "undo redo | bold italic underline strikethrough | alignleft aligncenter alignright alignjustify | bullist numlist | outdent indent | osivia-link osivia-image media | code fullscreen",
                statusbar: false,

                schema: "html5",
                element_format : "html",
                formats: {
                    alignleft: {
                        selector: "p,ul,ol,li",
                        classes: "text-left"
                    },
                    aligncenter: {
                        selector: "p,ul,ol,li",
                        classes: "text-center"
                    },
                    alignright: {
                        selector: "p,ul,ol,li",
                        classes: "text-right"
                    },
                    alignjustify: {
                        selector: "p,ul,ol,li",
                        classes: "text-justify"
                    },
                    bold: {
                        inline: "strong"
                    },
                    italic: {
                        inline: "em"
                    },
                    underline: {
                        inline: "u",
                        exact: true
                    },
                    strikethrough: {
                        inline: "del"
                    }
                },

                browser_spellcheck: true,
                contextmenu: false,
                content_css: ["/osivia-portal-custom-web-assets/css/bootstrap.min.css", "/osivia-portal-custom-web-assets/css/osivia.min.css"],
                height: "400",

                convert_urls: false,
                paste_as_text: true
            });
        }

        $textarea.data("tinymce-default-loaded", true);
    });


    // TinyMCE default
    $JQry("textarea.tinymce.tinymce-simple").each(function(index, element) {
        var $textarea = $JQry(element);
        var id = $textarea.attr("id");

        if (tinymce.get(id)) {
            if (!$textarea.data("tinymce-simple-loaded")) {
                tinymce.execCommand("mceRemoveEditor", true, id);
                tinymce.execCommand("mceAddEditor", true, id);
            }
        } else {
            tinymce.init({
                selector: "textarea.tinymce.tinymce-simple",
                language: "fr_FR",

                branding: false,
                menubar: false,
                toolbar: "bold italic underline | alignleft aligncenter alignright alignjustify | bullist numlist",
                statusbar: false,

                element_format: "html",
                formats: {
                    alignleft: {
                        selector: "p, ul, ol, li",
                        classes: "text-left"
                    },
                    aligncenter: {
                        selector: "p, ul, ol, li",
                        classes: "text-center"
                    },
                    alignright: {
                        selector: "p, ul, ol, li",
                        classes: "text-right"
                    },
                    alignjustify: {
                        selector: "p, ul, ol, li",
                        classes: "text-justify"
                    },
                    bold: {
                        inline: "strong"
                    },
                    italic: {
                        inline: "em"
                    },
                    underline: {
                        inline: "u"
                    }
                },

                content_css: ["/osivia-portal-custom-web-assets/css/bootstrap.min.css"],
                height: 200,
                width: "auto",

                // Prevent relative URL conversion
                convert_urls: false,
                // Remove style on paste
                paste_as_text: true,

                browser_spellcheck: true
            });
        }

        $textarea.data("tinymce-simple-loaded", true);
    });



    function urlConverterCallback(url, node, on_save, name) {
        console.log("[urlConverterCallback] url = " + url);
        console.log("[urlConverterCallback] node = " + node);
        console.log("[urlConverterCallback] on_save = " + on_save);
        console.log("[urlConverterCallback] name = " + name);

        return url;
    }
}
