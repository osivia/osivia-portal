/**
 * 
 */
package org.osivia.portal.api.tokens;

import java.util.Map;

/**
 * Service token
 * @author Loïc Billon / JS Steux
 *
 */
public interface ITokenService {

	
    /** MBean name. */
    static final String MBEAN_NAME = "osivia:service=TokenService";

	/**
	 * @param attributes
	 * @return
	 */
	String generateToken(Map<String, String> attributes);

	/**
	 * @param tokenKey
	 * @param renew
	 * @return
	 */
	Map<String, String> validateToken(String tokenKey, boolean renew);

	/**
	 * @param tokenKey
	 * @return
	 */
	Map<String, String> validateToken(String tokenKey);
    
    
}
