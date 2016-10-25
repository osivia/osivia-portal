package org.osivia.portal.core.error;


public class UserAction {
    
    public UserAction(long timestamp, String command) {
        super();
        this.timestamp = timestamp;
        this.command = command;
    }

    long timestamp;
    String command;
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getCommand() {
        return command;
    }
    
    public void setCommand(String command) {
        this.command = command;
    }

}
