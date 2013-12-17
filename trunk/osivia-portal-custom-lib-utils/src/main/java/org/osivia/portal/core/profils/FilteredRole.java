package org.osivia.portal.core.profils;

import org.jboss.portal.identity.Role;

/**
 * Filtered role.
 *
 * @see Role
 */
public class FilteredRole implements Role {

    /** Unchecked role name. */
    public static final String UNCHECKED_ROLE_NAME = "role-aucun-profil";
    /** Unchecked role display name. */
    public static final String UNCHECKED_ROLE_DISPLAY_NAME = "Utilisateurs sans profil";
    /** Authenticated role display name. */
    public static final String AUTHENTICATED_ROLE_DISPLAY_NAME = "Utilisateurs connectés";
    /** Administrators role name. */
    public static final String ADMINISTRATORS_ROLE_NAME = "Administrators";
    /** Administrators role display name. */
    public static final String ADMINISTRATORS_ROLE_DISPLAY_NAME = "Administrateurs du portail";

    /** Role name. */
    private final String name;
    /** Role display name. */
    private String displayName;


    /**
     * Constructor.
     *
     * @param name role name
     * @param displayName role display name
     */
    public FilteredRole(String name, String displayName) {
        super();
        this.name = name;
        this.displayName = displayName;
    }


    /**
     * {@inheritDoc}
     */
    public Object getId() {
        return this.name;
    }


    /**
     * {@inheritDoc}
     */
    public String getName() {
        return this.name;
    }


    /**
     * {@inheritDoc}
     */
    public String getDisplayName() {
        return this.displayName;
    }


    /**
     * {@inheritDoc}
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((this.name == null) ? 0 : this.name.hashCode());
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
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        FilteredRole other = (FilteredRole) obj;
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

}
