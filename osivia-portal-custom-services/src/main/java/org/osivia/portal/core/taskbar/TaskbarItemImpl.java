package org.osivia.portal.core.taskbar;

import org.osivia.portal.api.panels.PanelPlayer;
import org.osivia.portal.api.taskbar.TaskbarItem;
import org.osivia.portal.api.taskbar.TaskbarItemExecutor;
import org.osivia.portal.api.taskbar.TaskbarItemRestriction;
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

    /** Restriction. */
    private TaskbarItemRestriction restriction;

    /** Hidden indicator. */
    private boolean hidden;

    /** Executor. */
    private TaskbarItemExecutor executor;


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
    @Override
    public String getId() {
        return this.id;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public TaskbarItemType getType() {
        return this.type;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return this.key;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ClassLoader getCustomizedClassLoader() {
        return this.customizedClassLoader;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getIcon() {
        return this.icon;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public PanelPlayer getPlayer() {
        return this.player;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplate() {
        return this.template;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getDocumentType() {
        return this.documentType;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDefault() {
        return this.defaultItem;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int getOrder() {
        return this.order;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public TaskbarItemRestriction getRestriction() {
        return this.restriction;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHidden() {
        return this.hidden;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public TaskbarItemExecutor getExecutor() {
        return this.executor;
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

    /**
     * Setter for defaultItem.
     * 
     * @param defaultItem the defaultItem to set
     */
    public void setDefaultItem(boolean defaultItem) {
        this.defaultItem = defaultItem;
    }

    /**
     * Setter for order.
     * 
     * @param order the order to set
     */
    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * Setter for restriction.
     * 
     * @param restriction the restriction to set
     */
    public void setRestriction(TaskbarItemRestriction restriction) {
        this.restriction = restriction;
    }

    /**
     * Setter for hidden.
     * 
     * @param hidden the hidden to set
     */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    /**
     * Setter for executor.
     * 
     * @param executor the executor to set
     */
    public void setExecutor(TaskbarItemExecutor executor) {
        this.executor = executor;
    }

}
