package org.osivia.portal.core.ui.layout;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.osivia.portal.api.ui.layout.LayoutGroup;
import org.osivia.portal.api.ui.layout.LayoutItem;

import java.io.Serializable;
import java.util.List;

/**
 * Layout groups container.
 *
 * @author Cédric Krommenhoek
 */
public class LayoutGroupsContainer {

    /**
     * Layout groups.
     */
    @JsonProperty("groups")
    private List<LayoutGroupImpl> groups;


    /**
     * Constructor.
     */
    public LayoutGroupsContainer() {
        super();
    }


    public List<LayoutGroupImpl> getGroups() {
        return groups;
    }

    public void setGroups(List<LayoutGroupImpl> groups) {
        this.groups = groups;
    }
}
