package org.osivia.portal.core.cms;

public class CMSException extends Exception {
	
	public static int ERROR_FORBIDDEN = 1;
	public static int ERROR_UNAVAILAIBLE = 2;
	public static int ERROR_NOTFOUND = 3;	
	

	private static final long serialVersionUID = 1L;
	
	private int errorCode;
	
	public int getErrorCode() {
		return errorCode;
	}

	public CMSException( Throwable e)	{
		super( e);
	}

	public CMSException(int errorCode) {
        this.errorCode = errorCode;
    }	
	
}
