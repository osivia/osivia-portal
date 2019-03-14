package org.osivia.portal.api.tokens;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.codec.binary.Base64;

public class TokenUtils {

    private static Map<String, Token> tokens = new ConcurrentHashMap<>();
    private static long TOKEN_TIMEOUT = 120000L;

    public static String generateToken(Map<String, String> attributes) {
        
        String tokenKey = new String(Base64.encodeBase64(("key_" + System.currentTimeMillis()).getBytes()));
        Token idToken = new Token(attributes);
        tokens.put(tokenKey, idToken);
        return tokenKey;
    }


    public static Map<String, String> validateToken(String tokenKey, boolean renew) {
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

    public static Map<String, String> validateToken(String tokenKey) {
        return validateToken(tokenKey, false);

    }

}
