package org.osivia.portal.core.cms;

import org.apache.commons.lang.StringUtils;
import org.osivia.portal.api.cms.Symlink;

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
        StringBuilder builder = new StringBuilder();
        builder.append(this.getParentPath());
        builder.append("/symlink_");
        builder.append(this.segment);
        return builder.toString();
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
            StringBuilder builder = new StringBuilder();
            builder.append(this.parent.getNavigationPath());
            builder.append("/_");
            builder.append(StringUtils.substringAfterLast(this.parent.getTargetPath(), "/"));
            navigationPath = builder.toString();
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
