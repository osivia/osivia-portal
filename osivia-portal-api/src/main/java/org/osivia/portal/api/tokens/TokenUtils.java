package org.osivia.portal.api.tokens;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.codec.binary.Base64;

public class TokenUtils {
    
    private static Map<String, Token> tokens =  new ConcurrentHashMap<>();
    private static long TOKEN_TIMEOUT = 120000L;
    
    public static String generateToken(String uid)   {
        String tokenKey = new String(Base64.encodeBase64(("key_"+System.currentTimeMillis()).getBytes()));
        Token idToken = new Token( uid);
        tokens.put(tokenKey, idToken);
        return tokenKey;
    }
    
    public static String validateToken(String tokenKey)   {
        String uid = null;;
        Token token = tokens.get(tokenKey);
        if( token != null) {
            long ts = System.currentTimeMillis();
            if( ts - token.getCreationTs() <  TOKEN_TIMEOUT)    {
                uid = token.getUid();
                tokens.remove(tokenKey);
            }
        }
        return uid;
    }
  
    
}
