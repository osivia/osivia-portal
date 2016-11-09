package org.osivia.portal.api.theming;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * User pages group java-bean.
 *
 * @author Cédric Krommenhoek
 */
public class UserPagesGroup {

    /** Icon. */
    private String icon;
    /** Display name. */
    private String displayName;
    /** Root page. */
    private UserPage rootPage;

    /** Group name. */
    private final String name;
    /** User pages. */
    private final Set<UserPage> pages;
    /** Displayed user pages. */
    private final Set<UserPage> displayedPages;
    /** Hidden user pages. */
    private final Set<UserPage> hiddenPages;


    /**
     * Constructor.
     *
     * @param name group name
     */
    public UserPagesGroup(String name) {
        super();
        this.name = name;
        this.pages = new LinkedHashSet<UserPage>();
        this.displayedPages = new LinkedHashSet<UserPage>();
        this.hiddenPages = new LinkedHashSet<UserPage>();
    }


    /**
     * Add user page.
     *
     * @param page user page
     * @param displayed displayed page indicator
     */
    public void add(UserPage page, boolean displayed) {
        this.pages.add(page);
        if (displayed) {
            this.displayedPages.add(page);
        } else {
            this.hiddenPages.add(page);
        }
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
        UserPagesGroup other = (UserPagesGroup) obj;
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!this.name.equals(other.name)) {
            return false;
        }
        return true;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UserPagesGroup [name=");
        builder.append(this.name);
        builder.append("]");
        return builder.toString();
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
     * Getter for displayName.
     * 
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Setter for displayName.
     * 
     * @param displayName the displayName to set
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Getter for rootPage.
     * 
     * @return the rootPage
     */
    public UserPage getRootPage() {
        return rootPage;
    }

    /**
     * Setter for rootPage.
     * 
     * @param rootPage the rootPage to set
     */
    public void setRootPage(UserPage rootPage) {
        this.rootPage = rootPage;
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
     * Getter for pages.
     *
     * @return the pages
     */
    public Set<UserPage> getPages() {
        return this.pages;
    }

    /**
     * Getter for displayedPages.
     *
     * @return the displayedPages
     */
    public Set<UserPage> getDisplayedPages() {
        return this.displayedPages;
    }

    /**
     * Getter for hiddenPages.
     *
     * @return the hiddenPages
     */
    public Set<UserPage> getHiddenPages() {
        return this.hiddenPages;
    }

}
