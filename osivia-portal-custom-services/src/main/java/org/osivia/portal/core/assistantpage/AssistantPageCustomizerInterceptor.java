/**
 *
 */
package org.osivia.portal.core.assistantpage;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.dom.DOMElement;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerInterceptor;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.instance.InstanceContainer;
import org.jboss.portal.core.model.instance.InstanceDefinition;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.portlet.info.PortletIconInfo;
import org.jboss.portal.core.portlet.info.PortletInfoInfo;
import org.jboss.portal.portlet.Portlet;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.info.PortletInfo;
import org.jboss.portal.portlet.state.PropertyMap;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManagerFactory;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.html.DOM4JUtils;
import org.osivia.portal.api.html.HTMLConstants;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.constants.InternationalizationConstants;
import org.osivia.portal.core.formatters.IFormatter;
import org.osivia.portal.core.profils.IProfilManager;
import org.osivia.portal.core.profils.ProfilBean;

/**
 * Assistant page customizer interceptor.
 *
 * @see ControllerInterceptor
 * @see IFormatter
 */
public class AssistantPageCustomizerInterceptor extends ControllerInterceptor implements IFormatter {

    /** Default icon location. */
    private static final String DEFAULT_ICON_LOCATION = "/portal-core/images/portletIcon_Default1.gif";

    /** CMS service locator. */
    private static ICMSServiceLocator cmsServiceLocator;

    /** Instance container. */
    private InstanceContainer instanceContainer;
    /** Portal object container. */
    private PortalObjectContainer portalObjectContainer;
    /** Profile manager. */
    private IProfilManager profileManager;
    /** Portal authorization manager factory. */
    private PortalAuthorizationManagerFactory portalAuthorizationManagerFactory;
    /** Internationalization service. */
    private IInternationalizationService internationalizationService;


    /**
     * Default constructor.
     */
    public AssistantPageCustomizerInterceptor() {
        super();
    }


    /**
     * Static access to CMS service.
     *
     * @return CMS service
     * @throws Exception
     */
    public static ICMSService getCMSService() throws Exception {
        if (cmsServiceLocator == null) {
            cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        }
        return cmsServiceLocator.getCMSService();
    }


    /**
     * {@inheritDoc}
     */
    public String formatContextualization(PortalObject po, String selectedValue) throws Exception {
        Map<String, String> contextualization = new LinkedHashMap<String, String>();

        contextualization.put(IPortalUrlFactory.CONTEXTUALIZATION_PORTLET, "Mode portlet");
        contextualization.put(IPortalUrlFactory.CONTEXTUALIZATION_PAGE, "Mode page");
        contextualization.put(IPortalUrlFactory.CONTEXTUALIZATION_PORTAL, "Mode portail");

        StringBuffer select = new StringBuffer();
        select.append("<select name=\"contextualization\">");


        /* Héritage */
        PortalObject parent = po.getParent();
        String parentScope = parent.getProperty("osivia.cms.contextualization");
        String inheritedLabel = null;
        if (parentScope != null) {
            inheritedLabel = contextualization.get(parentScope);
        }

        if (inheritedLabel == null) {
            inheritedLabel = "Mode portlet";

        }
        inheritedLabel = "Herité [" + inheritedLabel + "]";


        if ((selectedValue == null) || (selectedValue.length() == 0)) {

            select.append("<option selected=\"selected\" value=\"\">" + inheritedLabel + "</option>");

        } else {

            select.append("<option value=\"\">" + inheritedLabel + "</option>");

        }

        for (Entry<String, String> entry : contextualization.entrySet()) {
            if ((selectedValue != null) && (selectedValue.length() != 0) && entry.getKey().equals(selectedValue)) {
                select.append("<option selected=\"selected\" value=\"" + entry.getKey() + "\">" + entry.getValue() + "</option>");
            } else {
                select.append("<option value=\"" + entry.getKey() + "\">" + entry.getValue() + "</option>");
            }
        }

        select.append("</select>");

        return select.toString();

    }


    /**
     * {@inheritDoc}
     */
    public String formatRequestFilteringPolicyList(PortalObject po, String policyName, String selectedPolicy) throws Exception {
        Map<String, String> policies = new LinkedHashMap<String, String>();

        policies.put(InternalConstants.PORTAL_CMS_REQUEST_FILTERING_POLICY_LOCAL, "Contenus du portail courant");
        policies.put(InternalConstants.PORTAL_CMS_REQUEST_FILTERING_POLICY_NO_FILTER, "Tous les contenus");

        String inheritedFilteringPolicy = po.getParent().getProperty(InternalConstants.PORTAL_PROP_NAME_CMS_REQUEST_FILTERING_POLICY);

        String inheritedLabel = null;

        if (InternalConstants.PORTAL_CMS_REQUEST_FILTERING_POLICY_LOCAL.equals(inheritedFilteringPolicy)) {
            inheritedLabel = "Contenus du portail courant";
        } else if (InternalConstants.PORTAL_CMS_REQUEST_FILTERING_POLICY_NO_FILTER.equals(inheritedFilteringPolicy)) {
            inheritedLabel = "Tous les contenus";
        } else {
            String portalType = po.getProperty(InternalConstants.PORTAL_PROP_NAME_PORTAL_TYPE);
            if (InternalConstants.PORTAL_TYPE_SPACE.equals(portalType)) {
                inheritedLabel = "Contenus du portail courant";
            } else {
                inheritedLabel = "Tous les contenus";
            }
        }


        inheritedLabel = "Hérité du portail [" + inheritedLabel + "]";

        // Select
        Element select = DOM4JUtils.generateElement(HTMLConstants.SELECT, "form-control", null);
        DOM4JUtils.addAttribute(select, HTMLConstants.ID, "cms-filter");
        DOM4JUtils.addAttribute(select, HTMLConstants.NAME, policyName);

        // Default option
        Element defaultOption = DOM4JUtils.generateElement(HTMLConstants.OPTION, null, inheritedLabel);
        DOM4JUtils.addAttribute(defaultOption, HTMLConstants.VALUE, StringUtils.EMPTY);
        if (StringUtils.isEmpty(selectedPolicy)) {
            DOM4JUtils.addAttribute(defaultOption, HTMLConstants.SELECTED, HTMLConstants.INPUT_SELECTED);
        }
        select.add(defaultOption);

        for (Entry<String, String> entry : policies.entrySet()) {
            // Option
            Element option = DOM4JUtils.generateElement(HTMLConstants.OPTION, null, entry.getValue());
            DOM4JUtils.addAttribute(option, HTMLConstants.VALUE, entry.getKey());
            if (entry.getKey().equals(selectedPolicy)) {
                DOM4JUtils.addAttribute(option, HTMLConstants.SELECTED, HTMLConstants.INPUT_SELECTED);
            }
            select.add(option);
        }

        return DOM4JUtils.write(select);
    }


    /**
     * {@inheritDoc}
     */
    public String formatScopeList(PortalObject po, String scopeName, String selectedScope) throws Exception {

        // On sélectionne les profils ayant un utilisateur Nuxeo
        List<ProfilBean> profils = this.profileManager.getListeProfils();

        Map<String, String> scopes = new LinkedHashMap<String, String>();

        scopes.put("anonymous", "Anonyme");

        String parentScope = po.getParent().getProperty(Constants.WINDOW_PROP_SCOPE);
        String inheritedLabel = null;
        if (parentScope != null) {
            inheritedLabel = scopes.get(parentScope);
        }

        if (inheritedLabel == null) {
            inheritedLabel = "Pas de cache";
        }
        inheritedLabel = "Hérité [" + inheritedLabel + "]";

        scopes.put("__inherited", inheritedLabel);


        for (ProfilBean profil : profils) {
            if ((profil.getNuxeoVirtualUser() != null) && (profil.getNuxeoVirtualUser().length() > 0)) {
                scopes.put(profil.getName(), "Profil " + profil.getName());
            }
        }


        // Select
        Element select = DOM4JUtils.generateElement(HTMLConstants.SELECT, "form-control", null);
        DOM4JUtils.addAttribute(select, HTMLConstants.ID, "cms-scope");
        DOM4JUtils.addAttribute(select, HTMLConstants.NAME, scopeName);
        if (StringUtils.isNotEmpty(po.getDeclaredProperty("osivia.cms.basePath"))) {
            DOM4JUtils.addAttribute(select, HTMLConstants.DISABLED, HTMLConstants.DISABLED);
        }

        if (!scopes.isEmpty()) {
            // Default option
            Element defaultOption = DOM4JUtils.generateElement(HTMLConstants.OPTION, null, "Pas de cache");
            DOM4JUtils.addAttribute(defaultOption, HTMLConstants.VALUE, StringUtils.EMPTY);
            if (StringUtils.isEmpty(selectedScope)) {
                DOM4JUtils.addAttribute(defaultOption, HTMLConstants.SELECTED, HTMLConstants.INPUT_SELECTED);
            }
            select.add(defaultOption);

            for (Entry<String, String> entry : scopes.entrySet()) {
                // Option
                Element option = DOM4JUtils.generateElement(HTMLConstants.OPTION, null, entry.getValue());
                DOM4JUtils.addAttribute(option, HTMLConstants.VALUE, entry.getKey());
                if (entry.getKey().equals(selectedScope)) {
                    DOM4JUtils.addAttribute(option, HTMLConstants.SELECTED, HTMLConstants.INPUT_SELECTED);
                }
                select.add(option);
            }
        }

        return DOM4JUtils.write(select);
    }


    /**
     * {@inheritDoc}
     */
    public String formatDisplayLiveVersionList(CMSServiceCtx cmxCtx, PortalObject po, String versionName, String selectedVersion) throws Exception {
        Map<String, String> versions = new LinkedHashMap<String, String>();

        versions.put("1", "Live");
        versions.put("2", "Live et Publiée");

        String inheritedLabel = null;

        // Calcul du label hérité

        if (inheritedLabel == null) {
            Page page = null;

            if (po instanceof Page) {
                page = (Page) po;
            }
            if (po instanceof Window) {
                page = (Page) po.getParent();
            }

            String spacePath = page.getProperty("osivia.cms.basePath");

            if (spacePath != null) {
                // Publication par path

                CMSItem publishSpaceConfig = getCMSService().getSpaceConfig(cmxCtx, spacePath);
                if (publishSpaceConfig != null) {

                    String displayLiveVersion = publishSpaceConfig.getProperties().get("displayLiveVersion");

                    if (displayLiveVersion != null) {
                        inheritedLabel = versions.get(displayLiveVersion);
                    }
                }
            } else {
                // Heriatge page parent
                String parentVersion = po.getParent().getProperty(Constants.WINDOW_PROP_VERSION);
                if (parentVersion != null) {

                    inheritedLabel = versions.get(parentVersion);

                }
            }
        }

        if (inheritedLabel == null) {
            inheritedLabel = "Publié";
        }


        inheritedLabel = "Hérité [" + inheritedLabel + "]";

        versions.put("__inherited", inheritedLabel);


        // Select
        Element select = DOM4JUtils.generateElement(HTMLConstants.SELECT, "form-control", null);
        DOM4JUtils.addAttribute(select, HTMLConstants.ID, "cms-version");
        DOM4JUtils.addAttribute(select, HTMLConstants.NAME, versionName);
        if (StringUtils.isNotEmpty(po.getDeclaredProperty("osivia.cms.basePath"))) {
            DOM4JUtils.addAttribute(select, HTMLConstants.DISABLED, HTMLConstants.DISABLED);
        }

        if (!versions.isEmpty()) {
            // Default option
            Element defaultOption = DOM4JUtils.generateElement(HTMLConstants.OPTION, null, "Publiée");
            DOM4JUtils.addAttribute(defaultOption, HTMLConstants.VALUE, StringUtils.EMPTY);
            if (StringUtils.isEmpty(selectedVersion)) {
                DOM4JUtils.addAttribute(defaultOption, HTMLConstants.SELECTED, HTMLConstants.INPUT_SELECTED);
            }
            select.add(defaultOption);

            for (Entry<String, String> entry : versions.entrySet()) {
                // Option
                Element option = DOM4JUtils.generateElement(HTMLConstants.OPTION, null, entry.getValue());
                DOM4JUtils.addAttribute(option, HTMLConstants.VALUE, entry.getKey());
                if (entry.getKey().equals(selectedVersion)) {
                    DOM4JUtils.addAttribute(option, HTMLConstants.SELECTED, HTMLConstants.INPUT_SELECTED);
                }
                select.add(option);
            }
        }

        return DOM4JUtils.write(select);
    }


    /**
     * {@inheritDoc}
     */
    public String formatHtmlSafeEncodingId(PortalObjectId id) throws IOException {
        if (id == null) {
            return null;
        } else {
            String safestFormat = id.toString(PortalObjectPath.SAFEST_FORMAT);
            return URLEncoder.encode(safestFormat, CharEncoding.UTF_8);
        }
    }


    /**
     * {@inheritDoc}
     */
    public String formatHtmlPortletsList(ControllerContext context) throws IOException {
        if (context == null) {
            return null;
        }

        HttpServletRequest httpRequest = context.getServerInvocation().getServerContext().getClientRequest();
        Locale locale = httpRequest.getLocale();

        List<InstanceDefinition> instances = new ArrayList<InstanceDefinition>(this.instanceContainer.getDefinitions());
        if (CollectionUtils.isEmpty(instances)) {
            return StringUtils.EMPTY;
        }
        Collections.sort(instances, new InstanceComparator(locale));

        Element table = new DOMElement(QName.get(HTMLConstants.TABLE));
        table.addAttribute(QName.get(HTMLConstants.CLASS), "table table-condensed");

        String buttonText = this.internationalizationService.getString(InternationalizationConstants.KEY_ADD_PORTLET_SUBMIT_VALUE, locale);

        for (InstanceDefinition instance : instances) {
            // Get portlet and properties
            Portlet portlet;
            try {
                PropertyMap properties = instance.getProperties();
                if (properties != null) {
                    List<String> hideProperties = properties.get(InternalConstants.HIDE_PORTLET_IN_MENU_PROPERTY);
                    if ((hideProperties != null) && (hideProperties.contains(String.valueOf(true)))) {
                        // Hide portlet in menu
                        continue;
                    }
                }

                portlet = instance.getPortlet();
            } catch (PortletInvokerException e) {
                // Portlet non déployé
                continue;
            }

            // HTML elements initialization
            Element row = new DOMElement(QName.get(HTMLConstants.TR));
            table.add(row);
            Element leftCell = new DOMElement(QName.get(HTMLConstants.TD));
            row.add(leftCell);
            Element middleCell = new DOMElement(QName.get(HTMLConstants.TD));
            row.add(middleCell);
            Element rightCell = new DOMElement(QName.get(HTMLConstants.TD));
            row.add(rightCell);

            // Portlet icon
            String iconLocation = null;
            String iconSize = PortletIconInfo.SMALL;
            PortletInfo info = portlet.getInfo();
            PortletInfoInfo infoInfo = info.getAttachment(PortletInfoInfo.class);
            if ((infoInfo != null) && (infoInfo.getPortletIconInfo() != null) && (infoInfo.getPortletIconInfo().getIconLocation(iconSize) != null)) {
                iconLocation = infoInfo.getPortletIconInfo().getIconLocation(iconSize);
            }
            if (StringUtils.isEmpty(iconLocation)) {
                iconLocation = DEFAULT_ICON_LOCATION;
            }
            Element img = new DOMElement(QName.get(HTMLConstants.IMG));
            img.addAttribute(QName.get(HTMLConstants.SRC), iconLocation);
            leftCell.add(img);

            // Portlet display name
            String displayName = instance.getDisplayName().getString(locale, true);
            if (StringUtils.isEmpty(displayName)) {
                displayName = instance.getId();
            }
            middleCell.setText(displayName);

            // Submit
            Element input = new DOMElement(QName.get(HTMLConstants.BUTTON));
            input.addAttribute(QName.get(HTMLConstants.TYPE), HTMLConstants.INPUT_TYPE_SUBMIT);
            input.addAttribute(QName.get(HTMLConstants.CLASS), "btn btn-secondary btn-sm");
            input.addAttribute(QName.get(HTMLConstants.ONCLICK), "selectPortlet('" + instance.getId() + "', this.form)");
            input.setText(buttonText);
            rightCell.add(input);
        }

        // Get HTML data
        return table.asXML();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerResponse invoke(ControllerCommand command) throws Exception {
        ControllerResponse response = (ControllerResponse) command.invokeNext();

        return response;
    }


    /**
     * Getter for instanceContainer.
     *
     * @return the instanceContainer
     */
    public InstanceContainer getInstanceContainer() {
        return this.instanceContainer;
    }

    /**
     * Setter for instanceContainer.
     *
     * @param instanceContainer the instanceContainer to set
     */
    public void setInstanceContainer(InstanceContainer instanceContainer) {
        this.instanceContainer = instanceContainer;
    }

    /**
     * Getter for portalObjectContainer.
     *
     * @return the portalObjectContainer
     */
    public PortalObjectContainer getPortalObjectContainer() {
        return this.portalObjectContainer;
    }

    /**
     * Setter for portalObjectContainer.
     *
     * @param portalObjectContainer the portalObjectContainer to set
     */
    public void setPortalObjectContainer(PortalObjectContainer portalObjectContainer) {
        this.portalObjectContainer = portalObjectContainer;
    }

    /**
     * Getter for profileManager.
     *
     * @return the profileManager
     */
    public IProfilManager getProfileManager() {
        return this.profileManager;
    }

    /**
     * Setter for profileManager.
     *
     * @param profileManager the profileManager to set
     */
    public void setProfileManager(IProfilManager profileManager) {
        this.profileManager = profileManager;
    }

    /**
     * Getter for portalAuthorizationManagerFactory.
     *
     * @return the portalAuthorizationManagerFactory
     */
    public PortalAuthorizationManagerFactory getPortalAuthorizationManagerFactory() {
        return this.portalAuthorizationManagerFactory;
    }

    /**
     * Setter for portalAuthorizationManagerFactory.
     *
     * @param portalAuthorizationManagerFactory the portalAuthorizationManagerFactory to set
     */
    public void setPortalAuthorizationManagerFactory(PortalAuthorizationManagerFactory portalAuthorizationManagerFactory) {
        this.portalAuthorizationManagerFactory = portalAuthorizationManagerFactory;
    }

    /**
     * Getter for internationalizationService.
     *
     * @return the internationalizationService
     */
    public IInternationalizationService getInternationalizationService() {
        return this.internationalizationService;
    }

    /**
     * Setter for internationalizationService.
     *
     * @param internationalizationService the internationalizationService to set
     */
    public void setInternationalizationService(IInternationalizationService internationalizationService) {
        this.internationalizationService = internationalizationService;
    }

}
