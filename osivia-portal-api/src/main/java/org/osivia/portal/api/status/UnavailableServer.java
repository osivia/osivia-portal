package org.osivia.portal.api.status;

public class UnavailableServer extends Exception {


	private static final long serialVersionUID = -19758871528355142L;
	
	int httpCode = -1;
	
	String message = null;

	public UnavailableServer( int httpCode) {
		this.httpCode = httpCode;
	}

	public UnavailableServer( String message) {
		this.message = message;
	}
	
	public String toString()	{
		String res = "";
		if( httpCode != -1)
			res += "http_code : " + httpCode;
		if( message != null)
			res += "message : " + message;
		return res;
	}


}
