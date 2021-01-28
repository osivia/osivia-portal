package org.osivia.portal.core.theming.attributesbundle;

/**
 * Window settings select option.
 *
 * @author Cédric Krommenhoek
 */
public class WindowSettingsSelectOption {

    /**
     * Option identifier.
     */
    private String id;
    /**
     * Option display text.
     */
    private String text;
    /**
     * Selected option indicator.
     */
    private boolean selected;


    /**
     * Constructor.
     */
    public WindowSettingsSelectOption() {
        super();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
