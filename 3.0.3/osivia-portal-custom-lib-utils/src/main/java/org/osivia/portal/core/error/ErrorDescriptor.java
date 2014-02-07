package org.osivia.portal.core.error;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;



public class ErrorDescriptor {
	
	public final static int NO_HTTP_ERR_CODE = -1;

	protected long errorId;
	protected Date registrationDate;
	protected int httpErrCode;
	protected Throwable exception;	
	/** Trace or message if exception is null. */
	protected String trace;
	protected String userId;
	protected Map<String, Object> properties;

	public ErrorDescriptor(int httpErrCode, Throwable exception, 
			String trace, String userId,
			Map<String, Object> properties) {
		super();
		this.registrationDate = new Date();
		this.httpErrCode = httpErrCode;
		this.exception = exception;
		this.trace = trace;
		this.userId = userId;
		this.properties = new HashMap<String, Object>();
		if( properties != null) {
			this.properties.putAll(properties);
		}
	}

	public long getErrorId() {
		return errorId;
	}	

	protected void setErrorId(long errorId) {
		this.errorId = errorId;
	}

	public Date getDate() {
		return registrationDate;
	}

	public int getHttpErrCode() {
		return httpErrCode;
	}

	public Throwable getException() {
		return exception;
	}

	public String getTrace() {
		if( exception == null) {
			return trace;
		} else {
			return Debug.throwableToString(exception);
		}
	}

	public String getUserId() {
		return userId;
	}

	public Map<String, Object> getProperties() {
		return new HashMap<String, Object>(properties);
	}

	public String getProperty(String key) {
		Object value = properties.get(key);
		if( value == null) {
			return null;
		} else {
			return value.toString();
		}
	}
	
	public void addProperty(String key, Object value) {
		properties.put(key, value);		
	}

}
