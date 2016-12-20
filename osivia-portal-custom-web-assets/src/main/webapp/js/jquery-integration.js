var $JQry = jQuery.noConflict();


// Redefine jQuery.ready() for AJAX reload
(function($, undefined) {
    var isFired = false;
    var oldReady = jQuery.fn.ready;
    $(function() {
        isFired = true;
        $(document).ready();
    });
    jQuery.fn.ready = function(fn) {
        if(fn === undefined) {
            $(document).trigger('_is_ready');
            return;
        }
        if(isFired) {
            window.setTimeout(fn, 1);
        }
        $(document).bind('_is_ready', fn);
    };
})(jQuery);


$JQry(document).ajaxStart(function() {
	var $ajaxWaiter = $JQry(".ajax-waiter");
	
	$ajaxWaiter.delay(200).addClass("in");
});


$JQry(document).ajaxStop(function() {
	var $ajaxWaiter = $JQry(".ajax-waiter");
	
	$ajaxWaiter.clearQueue();
	$ajaxWaiter.removeClass("in");
});
