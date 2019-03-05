package org.osivia.portal.api.tokens;


public class Token {
    private long creationTs;
    private String uid;

    
    
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
    public String getUid() {
        return uid;
    }


    /**
     * Setter for creationTs.
     * @param creationTs the creationTs to set
     */
     public Token(String uid) {
        this.creationTs = System.currentTimeMillis();
        this.uid=uid;
    }
     
     
    
}
