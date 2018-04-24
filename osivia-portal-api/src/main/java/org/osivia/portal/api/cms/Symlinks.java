package org.osivia.portal.api.cms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

/**
 * Symlinks container.
 * 
 * @author Cédric Krommenhoek
 */
public class Symlinks {

    /** Links. */
    private List<Symlink> links;

    /** Paths. */
    private final Set<String> paths;


    /**
     * Constructor.
     */
    public Symlinks() {
        super();
        this.paths = new HashSet<>();
    }


    /**
     * Add symlinks.
     * 
     * @param symlinks
     */
    public void addAll(Symlinks symlinks) {
        if (CollectionUtils.isNotEmpty(symlinks.getLinks())) {
            if (this.links == null) {
                this.links = new ArrayList<>(symlinks.getLinks());
            } else {
                this.links.addAll(symlinks.getLinks());
            }
        }

        if (CollectionUtils.isNotEmpty(symlinks.getPaths())) {
            this.paths.addAll(symlinks.getPaths());
        }
    }


    /**
     * Getter for links.
     * 
     * @return the links
     */
    public List<Symlink> getLinks() {
        return links;
    }

    /**
     * Setter for links.
     * 
     * @param links the links to set
     */
    public void setLinks(List<Symlink> links) {
        this.links = links;
    }

    /**
     * Getter for paths.
     * 
     * @return the paths
     */
    public Set<String> getPaths() {
        return paths;
    }

}
