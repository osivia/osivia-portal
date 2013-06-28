package org.osivia.portal.core.page;

import java.util.List;
import java.util.Map;

import org.osivia.portal.api.page.PageParametersEncoder;

public class EncodedParams {
	 Map<String, String[]> publicNavigationalState;

	public Map<String, String[]> getPublicNavigationalState() {
		return publicNavigationalState;
	}

	public void setPublicNavigationalState(Map<String, String[]> publicNavigationalState) {
		this.publicNavigationalState = publicNavigationalState;
	}

	public EncodedParams(Map<String, String[]> publicNavigationalState) {
		super();
		this.publicNavigationalState = publicNavigationalState;
	}
	
	public  List<String> decode( String urlParams, String name)	{
		
		String[] value = publicNavigationalState.get(urlParams);
		if( value == null || value.length != 1)
			return null;
		
		 Map<String,List<String>> params = PageParametersEncoder.decodeProperties(value[0]);
		 if( params == null)
			 return null;
		 
		 return params.get(name);
		
	}

}
