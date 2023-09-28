package org.osivia.portal.core.cms;

import org.apache.commons.lang.StringUtils;
import org.osivia.portal.api.cms.Symlink;

import java.util.Objects;

/**
 * Symlink implementation.
 * 
 * @author Cédric Krommenhoek
 * @see Symlink
 */
public class SymlinkImpl implements Symlink {

    /** Parent path. */
    private String parentPath;
    /** Parent. */
    private Symlink parent;
    /** Segment. */
    private String segment;
    /** Target path. */
    private String targetPath;
    /** Target webId. */
    private String targetWebId;


    /**
     * Constructor.
     */
    public SymlinkImpl() {
        super();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SymlinkImpl symlink = (SymlinkImpl) o;

        if (!Objects.equals(parentPath, symlink.parentPath)) return false;
        if (!Objects.equals(parent, symlink.parent)) return false;
        if (!Objects.equals(segment, symlink.segment)) return false;
        if (!Objects.equals(targetPath, symlink.targetPath)) return false;
        return Objects.equals(targetWebId, symlink.targetWebId);
    }


    @Override
    public int hashCode() {
        int result = parentPath != null ? parentPath.hashCode() : 0;
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = 31 * result + (segment != null ? segment.hashCode() : 0);
        result = 31 * result + (targetPath != null ? targetPath.hashCode() : 0);
        result = 31 * result + (targetWebId != null ? targetWebId.hashCode() : 0);
        return result;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getParentPath() {
        String parentPath;

        if (this.parent == null) {
            parentPath = this.parentPath;
        } else {
            parentPath = this.parent.getVirtualPath();
        }

        return parentPath;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getSegment() {
        return this.segment;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getTargetPath() {
        return this.targetPath;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getTargetWebId() {
        return this.targetWebId;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getVirtualPath() {
        return this.getParentPath() + "/symlink_" + this.segment;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getNavigationPath() {
        String navigationPath;

        if (this.parent == null) {
            navigationPath = this.parentPath;
        } else {
            navigationPath = this.parent.getNavigationPath() + "/_" + StringUtils.substringAfterLast(this.parent.getTargetPath(), "/");
        }

        return navigationPath;
    }


    /**
     * Setter for parentPath.
     * 
     * @param parentPath the parentPath to set
     */
    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    /**
     * Setter for parent.
     * 
     * @param parent the parent to set
     */
    public void setParent(Symlink parent) {
        this.parent = parent;
    }

    /**
     * Setter for segment.
     * 
     * @param segment the segment to set
     */
    public void setSegment(String segment) {
        this.segment = segment;
    }

    /**
     * Setter for targetPath.
     * 
     * @param targetPath the targetPath to set
     */
    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    /**
     * Setter for targetWebId.
     * 
     * @param targetWebId the targetWebId to set
     */
    public void setTargetWebId(String targetWebId) {
        this.targetWebId = targetWebId;
    }

}
