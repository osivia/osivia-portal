package org.osivia.portal.api.cms;

/**
 * Virtual navigation service interface.
 * 
 * @author Cédric Krommenhoek
 */
public interface IVirtualNavigationService {

    /** MBean name. */
    String MBEAN_NAME = "osivia:service=VirtualNavigationService";


    /**
     * Create symlink.
     * 
     * @param parentPath symlink parent path
     * @param segment symlink segment
     * @param targetPath symlink target path
     * @param targetWebId symlink target webId
     * @return symlink
     */
    Symlink createSymlink(String parentPath, String segment, String targetPath, String targetWebId);


    /**
     * Create symlink.
     * 
     * @param parent parent symlink
     * @param segment symlink segment
     * @param targetPath symlink target path
     * @param targetWebId symlink target webId
     * @return symlink
     */
    Symlink createSymlink(Symlink parent, String segment, String targetPath, String targetWebId);

}
