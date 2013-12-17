package org.osivia.portal.core.portalobjects;

import java.util.Locale;
import java.util.Map;

import org.jboss.portal.common.i18n.LocalizedString;
import org.jboss.portal.core.impl.model.portal.PortalObjectImpl;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.dynamic.DynamicPageBean;
import org.osivia.portal.core.dynamic.ITemplatePortalObject;
import org.osivia.portal.core.page.PortalObjectContainer;

/**
 * Dynamic template page.
 *
 * @see TemplatePage
 * @see ITemplatePortalObject
 */
@SuppressWarnings("unchecked")
public final class DynamicTemplatePage extends TemplatePage implements ITemplatePortalObject {

    /** Page bean. */
    private final DynamicPageBean pageBean;
    /** Local display name. */
    private final Map<Locale, String> localDisplayName;


    /**
     * Create dynamic template page.
     *
     * @param container portal object container
     * @param parentId parent portal object identifier
     * @param name name
     * @param displayNames display names
     * @param template template portal object
     * @param dynamicContainer dynamic portal object container
     * @param pageBean dynamic page bean
     * @param poid portal object identifier
     * @return dynamic template page
     */
    public static DynamicTemplatePage createPage(PortalObjectContainer container, PortalObjectId parentId, String name, Map<Locale, String> displayNames,
            PortalObjectImpl template, DynamicPortalObjectContainer dynamicContainer, DynamicPageBean pageBean, PortalObjectId poid) {
        DynamicTemplatePage page = null;
        try {
            page = new DynamicTemplatePage(container, parentId, name, displayNames, template, dynamicContainer, pageBean);
        } catch (Exception e) {
            PortalObjectId pageId = new PortalObjectId("", new PortalObjectPath(parentId.getPath().toString().concat("/").concat(name),
                    PortalObjectPath.CANONICAL_FORMAT));

            // Page non accessible, le template peut avoir été supprimé (auquel cas le template est null)
            // On supprime la page dynamique pour ne plus rencontrer d'erreurs
            dynamicContainer.removeDynamicPage(pageId.toString(PortalObjectPath.SAFEST_FORMAT));

            throw new RuntimeException("Page " + pageId + " has not be created. Exception = " + e.getMessage() + ". Check the template " + poid.toString());

        }

        return page;

    }


    /**
     * Constructor.
     *
     * @param container portal object container
     * @param parentId parent portal object identifier
     * @param name name
     * @param displayNames display names
     * @param template template portal object
     * @param dynamicContainer dynamic portal object container
     * @param pageBean dynamic page bean
     */
    private DynamicTemplatePage(PortalObjectContainer container, PortalObjectId parentId, String name, Map<Locale, String> displayNames,
            PortalObjectImpl template, DynamicPortalObjectContainer dynamicContainer, DynamicPageBean pageBean) {
        super(container, parentId, name, template, dynamicContainer);

        this.pageBean = pageBean;

        this.getLocalProperties().putAll(pageBean.getPageProperties());

        this.getLocalProperties().put(InternalConstants.TABS_ORDER_PROPERTY, "" + pageBean.getOrder());
        this.getLocalProperties().put(InternalConstants.PAGE_PROP_NAME_DYNAMIC, InternalConstants.PROP_VALUE_ON);


        this.localDisplayName = displayNames;

        // TODO : analyser si on peut faire du lazy fetching sur les propriétés
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getDeclaredProperty(String name) {
        String value = this.getLocalProperties().get(name);
        if (value == null) {
            value = super.getDeclaredProperty(name);
        }
        return value;

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public LocalizedString getDisplayName() {
        if (this.localDisplayName != null) {
            return new LocalizedString(this.localDisplayName, Locale.ENGLISH);
        } else {
            return super.getDisplayName();
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isClosable() {
        return this.pageBean.isClosable();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.getId().toString();
    }


    /**
     * Getter for pageBean.
     *
     * @return the pageBean
     */
    public DynamicPageBean getPageBean() {
        return this.pageBean;
    }


}
