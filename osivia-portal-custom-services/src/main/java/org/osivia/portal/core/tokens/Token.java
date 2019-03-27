package org.osivia.portal.core.tokens;

import java.io.Serializable;
import java.util.Map;

public class Token implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = -5752502017626465091L;



    public Token() {
        super();
   }


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



    
    /**
     * Setter for creationTs.
     * @param creationTs the creationTs to set
     */
    public void setCreationTs(long creationTs) {
        this.creationTs = creationTs;
    }



    
    /**
     * Setter for attributes.
     * @param attributes the attributes to set
     */
    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
     
     
    
}
