$JQry(document).on('click.dropdown', '[data-dropdown]', show);
$JQry(document).on('click.dropdown', hide);


function show(event, object) {

    var trigger = event ? $JQry(this) : object,
		dropdown = $JQry(trigger.attr('data-dropdown')),
		isOpen = trigger.hasClass('dropdown-open');

    // In some cases we don't want to show it
    if (event) {
        if ($JQry(event.target).hasClass('dropdown-ignore')) return;

        event.preventDefault();
        event.stopPropagation();
    } else {
        if (trigger !== object.target && $JQry(object.target).hasClass('dropdown-ignore')) return;
    }
    hide();

    if (isOpen || trigger.hasClass('dropdown-disabled')) return;

    // Show it
    trigger.addClass('dropdown-open');
    dropdown
		.data('dropdown-trigger', trigger)
		.show();

    // Trigger the show callback
    dropdown
		.trigger('show', {
			dropdown: dropdown,
			trigger: trigger
		});

}

function hide(event) {

    // In some cases we don't hide them
    var targetGroup = event ? $JQry(event.target).parents().addBack() : null;

    // Are we clicking anywhere in a dropdown?
    if (targetGroup && targetGroup.is('.dropdown')) {
        // Is it a dropdown menu?
        if (targetGroup.is('.dropdown-menu')) {
            // Did we click on an option? If so close it.
            if (!targetGroup.is('A')) return;
        } else {
            // Nope, it's a panel. Leave it open.
            return;
        }
    }

    // Hide any dropdown that may be showing
    $JQry(document).find('.dropdown:visible').each(function () {
        var dropdown = $JQry(this);
        dropdown
			.hide()
			.removeData('dropdown-trigger')
			.trigger('hide', { dropdown: dropdown });
    });

    // Remove all dropdown-open classes
    $JQry(document).find('.dropdown-open').removeClass('dropdown-open');

}
