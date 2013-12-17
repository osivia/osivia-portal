package org.osivia.portal.api.theming;

/**
 * Portlets region java bean.
 *
 * @author Cédric Krommenhoek
 * @see AbstractRegionBean
 */
public class PortletsRegionBean extends AbstractRegionBean {

    /** Header path. */
    private final String headerPath;
    /** Footer path. */
    private final String footerPath;


    /**
     * Constructor.
     *
     * @param name portlets region name
     * @param headerPath header path, may be null
     * @param footerPath footer path, may be null
     */
    public PortletsRegionBean(String name, String headerPath, String footerPath) {
        super(name);
        this.headerPath = headerPath;
        this.footerPath = footerPath;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCustomizable() {
        return true;
    }


    /**
     * Getter for headerPath.
     *
     * @return the headerPath
     */
    public String getHeaderPath() {
        return this.headerPath;
    }

    /**
     * Getter for footerPath.
     *
     * @return the footerPath
     */
    public String getFooterPath() {
        return this.footerPath;
    }

}
