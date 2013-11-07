package org.osivia.portal.api.urls;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;



public class WindowProperties {
	
	public static String encodeProperties( Map <String, String> props)	{
		
		try	{
		String url = "";
		
		for( String name : props.keySet())	{
			if( url.length() > 0)
				url += "&";
			url += encodeValue(name) + "=" + encodeValue(props.get(name));
		}
		
		return URLEncoder.encode(url , "UTF-8");
		} catch( Exception e)	{
			throw new RuntimeException( e);
		}
	}
	
	public static Map<String,String> decodeProperties( String urlParams)	{
		try	{
		
		String decodedParam = URLDecoder.decode(urlParams , "UTF-8");
		
		Map<String, String> params = new HashMap<String, String>();
		
		String[] tabParams = decodedParam.split("&");	
		
		for(int i=0; i< tabParams.length; i++){
			String[] valParams = tabParams[i].split("=");
			
			if( valParams.length != 2)
				throw new IllegalArgumentException("Bad parameter format");
			
			params.put(valParams[0], valParams[1]);
		}
		
		return params;
		
		} catch( Exception e)	{
			throw new RuntimeException( e);
		}

	}

	
	private static String encodeValue( String origValue)	{
		
		if( origValue.contains("="))
			throw new RuntimeException("Bad parameter format");
		if( origValue.contains("&"))
			throw new RuntimeException("Bad parameter format");
		
		return origValue;
				
		
	}

}
