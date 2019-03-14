package org.osivia.portal.api.tokens;

import java.util.Map;

public class Token {
    private long creationTs;
    private Map<String, String> attributes;

    
    
    /**
     * Getter for creationTs.
     * @return the creationTs
     */
    public long getCreationTs() {
        return creationTs;
    }


    
    /**
     * Getter for uid.
     * @return the uid
     */
    public Map<String,String> getAttributes() {
        return attributes;
    }


    /**
     * Setter for creationTs.
     * @param creationTs the creationTs to set
     */
     public Token(Map<String,String> attributes) {
        this.creationTs = System.currentTimeMillis();
        this.attributes=attributes;
    }
     
     
    
}
