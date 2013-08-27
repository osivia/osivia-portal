package org.osivia.portal.core.page;


public class UserNotification {
    
    private boolean error;
    private String msg;
    
    public boolean isError() {
        return error;
    }
    
    public void setError(boolean error) {
        this.error = error;
    }
    
    public String getMsg() {
        return msg;
    }
    
    public void setMsg(String msg) {
        this.msg = msg;
    }

    public UserNotification(boolean error, String msg) {
        super();
        this.error = error;
        this.msg = msg;
    }

}
