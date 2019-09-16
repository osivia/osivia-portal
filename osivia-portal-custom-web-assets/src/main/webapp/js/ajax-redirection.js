/*
 * Adapt standard portal to ajax redirection
 * Without this adaptation, CAS login may appear directly in the modified fragment
 */


function adaptAjaxRedirection( url)        {
   var result = url.replace("/auth/pagemarker/", "/auth/noredirect/pagemarker/");
   return result;
}

/*
 * Handle ajax redirection if the response contains such information
 */

function handleAjaxRedirection( response)        {
	var handled = false;
	if( response.includes("<!--RELOAD-->"))	{
		$JQry('body').append(response);
			handled = true;
	}
	return handled;
}

