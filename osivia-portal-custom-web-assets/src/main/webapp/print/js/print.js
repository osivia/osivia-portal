
$JQry(document).ready(function() {
	var $content = $JQry(".print-content"),
		id = $content.data("id"),
		window = parent.document.getElementById(id);
	
	$content.append(window.innerHTML);
});


$JQry(window).load(function() {
	window.print();
});
