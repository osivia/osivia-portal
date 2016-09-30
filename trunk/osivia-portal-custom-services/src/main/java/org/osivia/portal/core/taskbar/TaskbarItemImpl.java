package org.osivia.portal.core.taskbar;

import org.osivia.portal.api.panels.PanelPlayer;
import org.osivia.portal.api.taskbar.TaskbarItem;
import org.osivia.portal.api.taskbar.TaskbarItemType;

/**
 * Taskbar item implementation.
 *
 * @author Cédric Krommenhoek
 * @see TaskbarItem
 */
public class TaskbarItemImpl implements TaskbarItem {

    /** Default order. */
    private static final int DEFAULT_ORDER = 100;


    /** Identifier. */
    private String id;
    /** Type. */
    private TaskbarItemType type;
    /** Internationalization key. */
    private String key;
    /** Customized class loader. */
    private ClassLoader customizedClassLoader;
    /** Icon. */
    private String icon;

    /** Player. */
    private PanelPlayer player;
    /** Template. */
    private String template;

    /** Document type. */
    private String documentType;

    /** Default indicator. */
    private boolean defaultItem;
    /** Order. */
    private int order;


    /**
     * Constructor.
     */
    public TaskbarItemImpl() {
        super();

        this.order = DEFAULT_ORDER;
    }


    /**
     * {@inheritDoc}
     */
    public String getId() {
        return this.id;
    }


    /**
     * {@inheritDoc}
     */
    public TaskbarItemType getType() {
        return this.type;
    }


    /**
     * {@inheritDoc}
     */
    public String getKey() {
        return this.key;
    }


    /**
     * {@inheritDoc}
     */
    public ClassLoader getCustomizedClassLoader() {
        return this.customizedClassLoader;
    }


    /**
     * {@inheritDoc}
     */
    public String getIcon() {
        return this.icon;
    }


    /**
     * {@inheritDoc}
     */
    public PanelPlayer getPlayer() {
        return this.player;
    }


    /**
     * {@inheritDoc}
     */
    public String getTemplate() {
        return this.template;
    }


    /**
     * {@inheritDoc}
     */
    public String getDocumentType() {
        return this.documentType;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isDefault() {
        return this.defaultItem;
    }


    /**
     * {@inheritDoc}
     */
    public int getOrder() {
        return this.order;
    }


    /**
     * {@inheritDoc}
     */
    public void setToDefault(int order) {
        this.defaultItem = true;
        this.order = order;
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
        if (!(obj instanceof TaskbarItem)) {
            return false;
        }
        TaskbarItem other = (TaskbarItem) obj;
        if (this.id == null) {
            if (other.getId() != null) {
                return false;
            }
        } else if (!this.id.equals(other.getId())) {
            return false;
        }
        return true;
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
     * Setter for type.
     *
     * @param type the type to set
     */
    public void setType(TaskbarItemType type) {
        this.type = type;
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
     * Setter for customizedClassLoader.
     *
     * @param customizedClassLoader the customizedClassLoader to set
     */
    public void setCustomizedClassLoader(ClassLoader customizedClassLoader) {
        this.customizedClassLoader = customizedClassLoader;
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
     * Setter for player.
     *
     * @param player the player to set
     */
    public void setPlayer(PanelPlayer player) {
        this.player = player;
    }

    /**
     * Setter for template.
     *
     * @param template the template to set
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    /**
     * Setter for documentType.
     *
     * @param documentType the documentType to set
     */
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

}
