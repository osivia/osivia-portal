package org.osivia.portal.api.taskbar;

import org.osivia.portal.api.panels.PanelPlayer;

/**
 * Taskbar task java-bean.
 *
 * @author Cédric Krommenhoek
 */
public class TaskbarTask {

    /** Identifier. */
    private String id;
    /** Name. */
    private String name;
    /** Internationalization key. */
    private String key;
    /** Icon. */
    private String icon;

    /** Path. */
    private String path;
    /** Type. */
    private String type;

    /** Maximized player. */
    private PanelPlayer maximizedPlayer;


    /**
     * Constructor.
     */
    public TaskbarTask() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((this.id == null) ? 0 : this.id.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof TaskbarTask)) {
            return false;
        }
        TaskbarTask other = (TaskbarTask) obj;
        if (this.id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!this.id.equals(other.id)) {
            return false;
        }
        return true;
    }


    /**
     * Getter for id.
     *
     * @return the id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Setter for id.
     *
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter for name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setter for name.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for key.
     *
     * @return the key
     */
    public String getKey() {
        return this.key;
    }

    /**
     * Setter for key.
     *
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Getter for icon.
     *
     * @return the icon
     */
    public String getIcon() {
        return this.icon;
    }

    /**
     * Setter for icon.
     *
     * @param icon the icon to set
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * Getter for path.
     * 
     * @return the path
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Setter for path.
     * 
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Getter for type.
     * 
     * @return the type
     */
    public String getType() {
        return this.type;
    }

    /**
     * Setter for type.
     * 
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Getter for maximizedPlayer.
     * 
     * @return the maximizedPlayer
     */
    public PanelPlayer getMaximizedPlayer() {
        return this.maximizedPlayer;
    }

    /**
     * Setter for maximizedPlayer.
     * 
     * @param maximizedPlayer the maximizedPlayer to set
     */
    public void setMaximizedPlayer(PanelPlayer maximizedPlayer) {
        this.maximizedPlayer = maximizedPlayer;
    }

}
