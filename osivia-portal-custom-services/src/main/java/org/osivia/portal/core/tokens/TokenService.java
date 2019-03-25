/**
 * 
 */
package org.osivia.portal.core.tokens;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.codec.binary.Base64;
import org.osivia.portal.api.tokens.ITokenService;
import org.osivia.portal.api.tokens.Token;

/**
 * @author loic
 *
 */
public class TokenService implements ITokenService {

    private Map<String, Token> tokens = new ConcurrentHashMap<>();
    private long TOKEN_TIMEOUT = 120000L;

    public String generateToken(Map<String, String> attributes) {
        
        String tokenKey = new String(Base64.encodeBase64(("key_" + System.currentTimeMillis()).getBytes()));
        Token idToken = new Token(attributes);
        tokens.put(tokenKey, idToken);
        return tokenKey;
    }


    public Map<String, String> validateToken(String tokenKey, boolean renew) {
        Map<String, String> attributes = null;
        Token token = tokens.get(tokenKey);
        if (token != null) {
            long ts = System.currentTimeMillis();
            if (ts - token.getCreationTs() < TOKEN_TIMEOUT) {
                attributes = token.getAttributes();
                if(! renew)
                    tokens.remove(tokenKey);
            }
        }
        return attributes;
    }

    public Map<String, String> validateToken(String tokenKey) {
        return validateToken(tokenKey, false);

    }
}
