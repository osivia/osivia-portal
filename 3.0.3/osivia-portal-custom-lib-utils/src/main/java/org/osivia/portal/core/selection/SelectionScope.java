package org.osivia.portal.core.selection;

import org.apache.commons.lang.StringUtils;

/**
 * Selection scope enumeration.
 * 
 * @author Cédric Krommenhoek
 */
public enum SelectionScope {

    /** Enumeration. */
    SCOPE_SESSION("Session"), SCOPE_NAVIGATION("Navigation"), SCOPE_PAGE("Page");

    /** Scope name. */
    private final String scopeName;

    /**
     * Constructor.
     * 
     * @param scopeName scope name
     */
    private SelectionScope(String scopeName) {
        this.scopeName = scopeName;
    }

    /**
     * Access selection scope from his scope name. Default value is "Navigation scope".
     * 
     * @param scopeName scope name
     * @return selection scope
     */
    public static SelectionScope fromScopeName(String scopeName) {
        for (SelectionScope scope : SelectionScope.values()) {
            if (StringUtils.equalsIgnoreCase(scopeName, scope.scopeName)) {
                return scope;
            }
        }

        // Default selection scope
        return SCOPE_NAVIGATION;
    }

    /**
     * Getter.
     * 
     * @return the scopeName
     */
    public String getScopeName() {
        return scopeName;
    }

}
