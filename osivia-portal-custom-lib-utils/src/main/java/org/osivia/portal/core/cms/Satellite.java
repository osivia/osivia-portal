package org.osivia.portal.core.cms;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * Satellite java-bean.
 * 
 * @author ckrommenhoek
 */
public class Satellite {

    /** Main satellite. */
    public static final Satellite MAIN = new Satellite();
    public static final String MAIN_DISPLAY_STRING = "MAIN";

    /** Label. */
    private String label;
    /** Public host. */
    private String publicHost;
    /** Public port. */
    private String publicPort;
    /** Private host. */
    private String privateHost;
    /** Private port. */
    private String privatePort;
    /** Path patterns. */
    private List<Pattern> paths;


    /** Identifier. */
    private final String id;
    /** Main satellite indicator. */
    private final boolean main;


    /**
     * Constructor.
     * 
     * @param id identifier
     */
    public Satellite(String id) {
        super();
        this.id = id;
        this.main = false;
    }

    /**
     * Main satellite constructor.
     */
    private Satellite() {
        super();
        this.id = StringUtils.EMPTY;
        this.main = true;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Satellite other = (Satellite) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }


    /**
     * Getter for label.
     * 
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Setter for label.
     * 
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Getter for publicHost.
     * 
     * @return the publicHost
     */
    public String getPublicHost() {
        return publicHost;
    }

    /**
     * Setter for publicHost.
     * 
     * @param publicHost the publicHost to set
     */
    public void setPublicHost(String publicHost) {
        this.publicHost = publicHost;
    }

    /**
     * Getter for publicPort.
     * 
     * @return the publicPort
     */
    public String getPublicPort() {
        return publicPort;
    }

    /**
     * Setter for publicPort.
     * 
     * @param publicPort the publicPort to set
     */
    public void setPublicPort(String publicPort) {
        this.publicPort = publicPort;
    }

    /**
     * Getter for privateHost.
     * 
     * @return the privateHost
     */
    public String getPrivateHost() {
        return privateHost;
    }

    /**
     * Setter for privateHost.
     * 
     * @param privateHost the privateHost to set
     */
    public void setPrivateHost(String privateHost) {
        this.privateHost = privateHost;
    }

    /**
     * Getter for privatePort.
     * 
     * @return the privatePort
     */
    public String getPrivatePort() {
        return privatePort;
    }

    /**
     * Setter for privatePort.
     * 
     * @param privatePort the privatePort to set
     */
    public void setPrivatePort(String privatePort) {
        this.privatePort = privatePort;
    }

    /**
     * Getter for paths.
     * 
     * @return the paths
     */
    public List<Pattern> getPaths() {
        return paths;
    }

    /**
     * Setter for paths.
     * 
     * @param paths the paths to set
     */
    public void setPaths(List<Pattern> paths) {
        this.paths = paths;
    }

    /**
     * Getter for id.
     * 
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Getter for main.
     * 
     * @return the main
     */
    public boolean isMain() {
        return main;
    }

    
    /**
     * return a key compatible value (not empty) for loggers
     * 
     * @return the main
     */
    public static String getAsKey(Satellite satellite) {
    	if( satellite == null || satellite.isMain())
    		return MAIN_DISPLAY_STRING;
    	else	{
    		return satellite.getId();
    	}
    }


    
    
}
