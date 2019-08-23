package org.osivia.portal.api.user;

import java.util.Objects;

/**
 * User saved search java-bean.
 *
 * @author Cédric Krommenhoek
 */
public class UserSavedSearch {

    /**
     * Identifier.
     */
    private final int id;

    /**
     * Display name.
     */
    private String displayName;
    /**
     * Order.
     */
    private Integer order;
    /**
     * Search data.
     */
    private String data;


    /**
     * Constructor.
     *
     * @param id identifier
     */
    public UserSavedSearch(int id) {
        super();
        this.id = id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSavedSearch that = (UserSavedSearch) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    public int getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
