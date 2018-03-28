package org.osivia.portal.core.cms;

import org.osivia.portal.api.cms.IVirtualNavigationService;
import org.osivia.portal.api.cms.Symlink;

/**
 * Virtual navigation service implementation.
 * 
 * @author Cédric Krommenhoek
 * @see IVirtualNavigationService
 */
public class VirtualNavigationServiceImpl implements IVirtualNavigationService {

    /**
     * Constructor.
     */
    public VirtualNavigationServiceImpl() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Symlink createSymlink(String parentPath, String segment, String targetPath, String targetWebId) {
        return this.createSymlink(parentPath, null, segment, targetPath, targetWebId);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Symlink createSymlink(Symlink parent, String segment, String targetPath, String targetWebId) {
        return this.createSymlink(null, parent, segment, targetPath, targetWebId);
    }


    /**
     * Create symlink.
     * 
     * @param parentPath parent path
     * @param parent parent symlink
     * @param segment segment
     * @param targetPath target path
     * @param targetWebId target webId
     * @return symlink
     */
    private Symlink createSymlink(String parentPath, Symlink parent, String segment, String targetPath, String targetWebId) {
        SymlinkImpl symlink = new SymlinkImpl();
        symlink.setParentPath(parentPath);
        symlink.setParent(parent);
        symlink.setSegment(segment);
        symlink.setTargetPath(targetPath);
        symlink.setTargetWebId(targetWebId);
        return symlink;
    }

}
