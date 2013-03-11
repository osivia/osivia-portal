package org.osivia.portal.core.error;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class GlobalErrorHandler {
	
	private static final String	DATE_TIME_FORMAT = 	"dd/MM/yyyy HH:mm:ss";
	
	protected static final Log logger = LogFactory.getLog(GlobalErrorHandler.class);
	
	private static GlobalErrorHandler s_instance = null;
	
	public static synchronized GlobalErrorHandler getInstance() {
		if( s_instance == null) {
			s_instance = new GlobalErrorHandler();
		}
		return s_instance;		
	}
	
	private long currentErrorId = 0;
	private Log log;
	
	protected GlobalErrorHandler() {
		super();
		log = LogFactory.getLog("PORTAL_USER_ERROR");
	}
	
	// v.1.0.21 : desynchronisation
	public long registerError(ErrorDescriptor error) {
		currentErrorId++;
		error.setErrorId(currentErrorId);
		log.error(asString(error));
		
		logger.error(error.getTrace());
		return currentErrorId;
	}
		
	
	public static String asString(ErrorDescriptor error) {
		StringBuffer sb = new StringBuffer();
		appendAsText(sb, error);
		return sb.toString();
	}
	
	public static void appendAsText(StringBuffer sb, ErrorDescriptor error) {
		if (error != null) {
			
			sb.append("\nNuméro: " + error.getErrorId());
			
			Date errDate = error.getDate();
			if (errDate != null) {
				sb.append("\ndate: ");
				sb.append(format(errDate));
				sb.append("\n");
			}
			String userId = error.getUserId();
			if (isNotEmpty(userId)) {
				sb.append("user: ");
				sb.append(userId);
				sb.append("\n");
			}
			Map<String, Object> errContext = error.getProperties();
			if (errContext != null) {
				for (Map.Entry<String, Object> entry : errContext.entrySet()) {
					sb.append(entry.getKey()).append(": ");
					sb.append(entry.getValue());
					sb.append("\n");
				}
			}
			sb.append("\n");
			sb.append(error.getTrace());
		}
	}

	private static String format(Date date) {
		return new SimpleDateFormat(DATE_TIME_FORMAT).format(date);
	}

	private static boolean isNotEmpty(String str) {
		return str != null && str.length() > 0;
	}

}
