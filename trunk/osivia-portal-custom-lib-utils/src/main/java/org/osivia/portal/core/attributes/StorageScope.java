package org.osivia.portal.core.attributes;

/**
 * Storage scope enumeration.
 *
 * @author Cédric Krommenhoek
 */
public enum StorageScope {

    /** Session scope. */
    SESSION("Session"),
    /** Navigation scope. */
    NAVIGATION("Navigation"),
    /** Page scope. */
    PAGE("Page");


    /** Default storage scope. */
    public static final StorageScope DEFAULT = NAVIGATION;


    /** Scope name. */
    private final String name;


    /**
     * Constructor.
     *
     * @param name scope name
     */
    private StorageScope(String name) {
        this.name = name;
    }


    /**
     * Get storage scope from name.
     *
     * @param name scope name
     * @return scope
     */
    public static StorageScope fromName(String name) {
        StorageScope result = DEFAULT;
        for (StorageScope scope : StorageScope.values()) {
            if (scope.name.equals(name)) {
                result = scope;
                break;
            }
        }
        return result;
    }


    /**
     * Getter for name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

}
