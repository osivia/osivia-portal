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
	 * Create a new token associated with attributes
	 * 
	 * @param attributes
	 * @return
	 */
	String generateToken(Map<String, String> attributes);

	/**
	 * Validate the token
	 * 
	 * @param tokenKey
	 * @param renew
	 * @return the original attributes
	 */
	Map<String, String> validateToken(String tokenKey, boolean renew);

	/**
	 * Validate (and remove) the token
	 * 
	 * @param tokenKey
	 * @return the original attributes
	 */
	Map<String, String> validateToken(String tokenKey);
    
    
}
