/*
 * (C) Copyright 2014 OSIVIA (http://www.osivia.com)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package org.osivia.portal.core.tokens;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.cache.CacheException;
import org.jboss.cache.Fqn;
import org.jboss.cache.TreeCacheMBean;
import org.osivia.portal.api.tokens.ITokenService;

/**
 * @author loic
 *
 */
public class TokenService implements ITokenService {

    /** Logger. */
    private static final Log logger = LogFactory.getLog(TokenService.class);

    /** Cache for tokens */
    
    private static final String OSIVIA_TOKENS = "osivia/tokens";
    private TreeCacheMBean treeCache;
    
    private String sync = new String("SYNC");
    private int modulo = 1;


    /**
     * Getter for treeCache.
     * 
     * @return the treeCache
     */
    public TreeCacheMBean getTreeCache() {
        return treeCache;
    }


    /**
     * Setter for treeCache.
     * 
     * @param treeCache the treeCache to set
     */
    public void setTreeCache(TreeCacheMBean treeCache) {
        this.treeCache = treeCache;
    }

    /**
     *  Timeout for token validity (10 minutes)
     */
    private long TOKEN_TIMEOUT = 600000L;

    /* (non-Javadoc)
     * @see org.osivia.portal.api.tokens.ITokenService#generateToken(java.util.Map)
     */
    
    public String generateToken(Map<String, String> attributes) {

        if( logger.isDebugEnabled())
            logger.debug("generateToken");
        
        String tokenKey;
        
        synchronized (sync)   {
            // Avoid doublon in same ms
            modulo = ( modulo + 1 ) % 100;
            tokenKey = new String(Base64.encodeBase64(("key_" + System.currentTimeMillis() + "_" + modulo).getBytes()));            
        }

        
        // Hashmap is ready for serialization
        HashMap<String, String> serMap = new HashMap<>(attributes);
        Token token = new Token(serMap);

        try {
            getTreeCache().put(getFqn(), tokenKey, token);
            return tokenKey;
        } catch (CacheException e) {
            throw new RuntimeException(e);
        }

    }


    private Fqn getFqn() {
        Fqn fqn = new Fqn(OSIVIA_TOKENS);
        return fqn;
    }


    /* (non-Javadoc)
     * @see org.osivia.portal.api.tokens.ITokenService#validateToken(java.lang.String, boolean)
     */
    public Map<String, String> validateToken(String tokenKey, boolean renew) {
        
        if( logger.isDebugEnabled())
            logger.debug("validateToken" + tokenKey);
        
        Map<String, String> attributes = null;
        Token token;
        try {
            token = (Token) getTreeCache().get(getFqn(), tokenKey);

            if (token != null) {
                long ts = System.currentTimeMillis();
                if (ts - token.getCreationTs() < TOKEN_TIMEOUT) {
                    attributes = token.getAttributes();
                    if (!renew) {
                        getTreeCache().remove(getFqn(), tokenKey);
                    }
                }
            }
            return attributes;
        } catch (CacheException e) {
            throw new RuntimeException(e);
        }
    }

    /* (non-Javadoc)
     * @see org.osivia.portal.api.tokens.ITokenService#validateToken(java.lang.String)
     */
    public Map<String, String> validateToken(String tokenKey) {
        return validateToken(tokenKey, false);
    }


}
