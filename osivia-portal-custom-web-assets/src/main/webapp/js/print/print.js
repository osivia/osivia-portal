function popup2print(title, divName) {
	url = '/osivia-portal-custom-web-assets/js/print/print.jsp?portlet=' + divName + "&title=" + encodeURIComponent(title);
	w = window.open(url);
}
