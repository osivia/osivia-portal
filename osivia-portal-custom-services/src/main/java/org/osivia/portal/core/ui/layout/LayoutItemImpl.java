package org.osivia.portal.core.ui.layout;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.osivia.portal.api.ui.layout.LayoutItem;

import java.util.List;

/**
 * Layout item implementation.
 *
 * @author Cédric Krommenhoek
 * @see LayoutItem
 */
public class LayoutItemImpl implements LayoutItem {

    /**
     * Identifier.
     */
    @JsonProperty("id")
    private String id;

    /**
     * Label.
     */
    @JsonProperty("label")
    private String label;

    /**
     * Icon.
     */
    @JsonProperty("icon")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String icon;

    /**
     * Profiles.
     */
    @JsonProperty("profiles")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> profiles;


    /**
     * Constructor.
     */
    public LayoutItemImpl() {
        super();
    }


    @Override
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getIcon() {
        return this.icon;
    }

    @Override
    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public List<String> getProfiles() {
        return this.profiles;
    }

    public void setProfiles(List<String> profiles) {
        this.profiles = profiles;
    }

}
