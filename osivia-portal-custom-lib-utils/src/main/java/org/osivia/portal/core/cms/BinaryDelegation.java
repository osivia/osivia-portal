package org.osivia.portal.core.cms;


public class BinaryDelegation {
    
    private boolean grantedAccess= false;
    
    
    public boolean isGrantedAccess() {
        return grantedAccess;
    }
    
    public void setGrantedAccess(boolean grantedAccess) {
        this.grantedAccess = grantedAccess;
    }


    private String userName = null;

    
    public String getUserName() {
        return userName;
    }

    
    public void setUserName(String userName) {
        this.userName = userName;
    }

}
