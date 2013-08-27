/**
 *
 */
package org.osivia.portal.core.assistantpage;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.dom.DOMElement;
import org.dom4j.io.HTMLWriter;
import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerInterceptor;
import org.jboss.portal.core.controller.ControllerRequestDispatcher;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.instance.InstanceContainer;
import org.jboss.portal.core.model.instance.InstanceDefinition;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.PortalObjectPermission;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.PageCommand;
import org.jboss.portal.core.model.portal.command.view.ViewPageCommand;
import org.jboss.portal.core.portlet.info.PortletIconInfo;
import org.jboss.portal.core.portlet.info.PortletInfoInfo;
import org.jboss.portal.core.theme.PageRendition;
import org.jboss.portal.identity.IdentityContext;
import org.jboss.portal.identity.IdentityServiceController;
import org.jboss.portal.identity.RoleModule;
import org.jboss.portal.portlet.Portlet;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.info.PortletInfo;
import org.jboss.portal.security.AuthorizationDomainRegistry;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManager;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManagerFactory;
import org.jboss.portal.server.request.URLContext;
import org.jboss.portal.server.request.URLFormat;
import org.jboss.portal.theme.LayoutService;
import org.jboss.portal.theme.PortalLayout;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.ThemeService;
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;
import org.jboss.portal.theme.page.WindowContext;
import org.jboss.portal.theme.page.WindowResult;
import org.jboss.portal.theme.render.renderer.RegionRendererContext;
import org.jboss.portal.theme.render.renderer.WindowRendererContext;
import org.osivia.portal.api.HTMLConstants;
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
import org.osivia.portal.core.page.PageType;
import org.osivia.portal.core.page.PortalURLImpl;
import org.osivia.portal.core.portalobjects.DynamicWindow;
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;
import org.osivia.portal.core.portalobjects.PortalObjectNameComparator;
import org.osivia.portal.core.portalobjects.PortalObjectOrderComparator;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;
import org.osivia.portal.core.profils.IProfilManager;
import org.osivia.portal.core.profils.ProfilBean;

/**
 * Assistant page customizer interceptor.
 *
 * @see ControllerInterceptor
 * @see IFormatter
 */
public class AssistantPageCustomizerInterceptor extends ControllerInterceptor implements IFormatter {

    /** Windows settings fancyboxes prefix. */
    private static final String PREFIX_ID_FANCYBOX_WINDOW_SETTINGS = "window-settings-";

    // HTML classes
    /** HTML toggle row display class. */
    private static final String HTML_CLASS_TOGGLE_ROW = "toggle-row";
    /** HTML class "styles-toggle-row". */
    private static final String HTML_CLASS_TOGGLE_STYLES_ROW = "styles-toggle-row";
    /** HTML class "dynamic-properties-toggle-row". */
    private static final String HTML_CLASS_TOGGLE_DYNAMIC_PROPERTIES_ROW = "dynamic-properties-toggle-row";
    /** HTML name "action". */
    private static final String HTML_NAME_ACTION = "action";
    /** HTML name "windowId". */
    private static final String HTML_NAME_WINDOW_ID = "windowId";
    /** HTML name "style". */
    private static final String HTML_NAME_STYLE = "style";
    /** HTML value for change window properties hidden input. */
    private static final String HTML_VALUE_ACTION_CHANGE_WINDOW_PROPERTIES = "changeWindowSettings";
    /** HTML rel "page" for "li" nodes. */
    private static final String HTML_REL_PAGE = "page";

    // HTML tree options names
    /** Display root option name. */
    private static final String DISPLAY_ROOT = "display-root";
    /** Display virtual end nodes option name. */
    private static final String DISPLAY_VIRTUAL_END_NODES = "display-virtual-end-nodes";
    /** Sort alphabetically option name. */
    private static final String SORT_ALPHABETICALLY = "sort-alphabetically";
    /** Hide dynamic pages option name. */
    private static final String HIDE_DYNAMIC_PAGES = "hide-dynamic-pages";
    /** Templated pages filter option name. */
    private static final String TEMPLATED_PAGES_FILTER = "templated-pages-filter";
    /** Non-templated pages filter option name. */
    private static final String NON_TEMPLATED_PAGES_FILTER = "non-templated-pages-filter";

    /** Default icon location. */
    private static final String DEFAULT_ICON_LOCATION = "/portal-core/images/portletIcon_Default1.gif";

    private static ICMSServiceLocator cmsServiceLocator;

    private String targetContextPath;

    private String pageSettingPath;

    protected LayoutService layoutService;

    protected ThemeService themeService;

    private InstanceContainer instanceContainer;

    private IdentityServiceController identityServiceController;

    private RoleModule roleModule;

    protected AuthorizationDomainRegistry authorizationDomainRegistry;

    protected PortalObjectContainer portalObjectContainer;

    protected IProfilManager profilManager;

    protected PortalAuthorizationManagerFactory portalAuthorizationManagerFactory;

    /** Internationalization service. */
    protected IInternationalizationService internationalizationService;


    public static ICMSService getCMSService() throws Exception {
        if (cmsServiceLocator == null) {
            cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        }
        return cmsServiceLocator.getCMSService();
    }


    public String formatInheritedCheckVakue(PortalObject po, String selectName, String propertyName, String selectedValue) throws Exception {

        Map<String, String> supportedValue = new LinkedHashMap<String, String>();

        supportedValue.put("0", "Non");
        supportedValue.put("1", "Oui");


        StringBuffer select = new StringBuffer();

        String disabled = "";
        if (StringUtils.isNotEmpty(po.getDeclaredProperty("osivia.cms.basePath"))) {
            disabled = "disabled='disabled'";
        }

        select.append("<select name=\"" + selectName + "\"" + disabled + ">");

        if (!supportedValue.isEmpty()) {

            /* Héritage */

            String parentScope = po.getParent().getProperty(propertyName);
            String inheritedLabel = null;
            if (parentScope != null) {
                inheritedLabel = supportedValue.get(parentScope);
            }
            ;
            if (inheritedLabel == null) {
                inheritedLabel = "Non";
            }
            inheritedLabel = "Herité [" + inheritedLabel + "]";


            if ((selectedValue == null) || (selectedValue.length() == 0)) {

                select.append("<option selected=\"selected\" value=\"\">" + inheritedLabel + "</option>");

            } else {

                select.append("<option value=\"\">" + inheritedLabel + "</option>");

            }
            for (String possibleValue : supportedValue.keySet()) {
                if ((selectedValue != null) && (selectedValue.length() != 0) && possibleValue.equals(selectedValue)) {

                    select.append("<option selected=\"selected\" value=\"" + possibleValue + "\">" + supportedValue.get(possibleValue) + "</option>");

                } else {

                    select.append("<option value=\"" + possibleValue + "\">" + supportedValue.get(possibleValue) + "</option>");

                }
            }
        }

        select.append("</select>");

        return select.toString();

    }


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
        ;
        if (inheritedLabel == null) {
            inheritedLabel = "Mode portlet";

        }
        inheritedLabel = "Herité [" + inheritedLabel + "]";


        if ((selectedValue == null) || (selectedValue.length() == 0)) {

            select.append("<option selected=\"selected\" value=\"\">" + inheritedLabel + "</option>");

        } else {

            select.append("<option value=\"\">" + inheritedLabel + "</option>");

        }
        for (String possibleContextualization : contextualization.keySet()) {
            if ((selectedValue != null) && (selectedValue.length() != 0) && possibleContextualization.equals(selectedValue)) {

                select.append("<option selected=\"selected\" value=\"" + possibleContextualization + "\">" + contextualization.get(possibleContextualization)
                        + "</option>");

            } else {

                select.append("<option value=\"" + possibleContextualization + "\">" + contextualization.get(possibleContextualization) + "</option>");

            }
        }


        select.append("</select>");

        return select.toString();

    }


    public String formatRequestFilteringPolicyList(PortalObject po, String policyName, String selectedPolicy) throws Exception {


        Map<String, String> policies = new LinkedHashMap<String, String>();

        policies.put( InternalConstants.PORTAL_CMS_REQUEST_FILTERING_POLICY_LOCAL, "Contenus du portail courant");
        policies.put( InternalConstants.PORTAL_CMS_REQUEST_FILTERING_POLICY_NO_FILTER, "Tous les contenus");


        String inheritedFilteringPolicy = po.getParent().getProperty(InternalConstants.PORTAL_PROP_NAME_CMS_REQUEST_FILTERING_POLICY);

        String inheritedLabel = null;

        if (InternalConstants.PORTAL_CMS_REQUEST_FILTERING_POLICY_LOCAL.equals(inheritedFilteringPolicy)) {
             inheritedLabel = "Contenus du portail courant";
         } else if (InternalConstants.PORTAL_CMS_REQUEST_FILTERING_POLICY_NO_FILTER.equals(inheritedFilteringPolicy)) {
             inheritedLabel = "Tous les contenus";
         } else {
              String portalType =  po.getProperty(InternalConstants.PORTAL_PROP_NAME_PORTAL_TYPE);
              if( InternalConstants.PORTAL_TYPE_SPACE.equals(portalType)) {
                inheritedLabel = "Contenus du portail courant";
              } else  {
                  inheritedLabel = "Tous les contenus";
              }
       }





        inheritedLabel = "Hérité du portail [" + inheritedLabel + "]";


        StringBuffer select = new StringBuffer();


        select.append("<select name=\"" + policyName + "\">");

        if ((selectedPolicy == null) || (selectedPolicy.length() == 0)) {

            select.append("<option selected=\"selected\" value=\"\">" + inheritedLabel + "</option>");

        } else {

            select.append("<option value=\"\">" + inheritedLabel + "</option>");

        }
        for (String possiblePolicy : policies.keySet()) {
            if ((selectedPolicy != null) && (selectedPolicy.length() != 0) && possiblePolicy.equals(selectedPolicy)) {

                select.append("<option selected=\"selected\" value=\"" + possiblePolicy + "\">" + policies.get(possiblePolicy) + "</option>");

            } else {

                select.append("<option value=\"" + possiblePolicy + "\">" + policies.get(possiblePolicy) + "</option>");

            }
        }

        select.append("</select>");

        return select.toString();

    }


    public String formatScopeList(PortalObject po, String scopeName, String selectedScope) throws Exception {

        // On sélectionne les profils ayant un utilisateur Nuxeo
        List<ProfilBean> profils = this.getProfilManager().getListeProfils();

        Map<String, String> scopes = new LinkedHashMap<String, String>();

        scopes.put("anonymous", "Anonyme");

        String parentScope = po.getParent().getProperty("osivia.cms.scope");
        String inheritedLabel = null;
        if (parentScope != null) {
            inheritedLabel = scopes.get(parentScope);
        }
        ;
        if (inheritedLabel == null) {
            inheritedLabel = "Pas de cache";
        }
        inheritedLabel = "Herité [" + inheritedLabel + "]";

        scopes.put("__inherited", inheritedLabel);


        for (ProfilBean profil : profils) {
            if ((profil.getNuxeoVirtualUser() != null) && (profil.getNuxeoVirtualUser().length() > 0)) {
                scopes.put(profil.getName(), "Profil " + profil.getName());
            }
        }


        StringBuffer select = new StringBuffer();

        String disabled = "";
        if (StringUtils.isNotEmpty(po.getDeclaredProperty("osivia.cms.basePath"))) {
            disabled = "disabled='disabled'";
        }

        select.append("<select name=\"" + scopeName + "\"" + disabled + ">");

        if (!scopes.isEmpty()) {

            if ((selectedScope == null) || (selectedScope.length() == 0)) {

                select.append("<option selected=\"selected\" value=\"\">Pas de cache</option>");

            } else {

                select.append("<option value=\"\">Pas de cache</option>");

            }
            for (String possibleScope : scopes.keySet()) {
                if ((selectedScope != null) && (selectedScope.length() != 0) && possibleScope.equals(selectedScope)) {

                    select.append("<option selected=\"selected\" value=\"" + possibleScope + "\">" + scopes.get(possibleScope) + "</option>");

                } else {

                    select.append("<option value=\"" + possibleScope + "\">" + scopes.get(possibleScope) + "</option>");

                }
            }
        }

        select.append("</select>");

        return select.toString();

    }


    public String formatDisplayLiveVersionList(CMSServiceCtx cmxCtx, PortalObject po, String versionName, String selectedVersion) throws Exception {

        Map<String, String> versions = new LinkedHashMap<String, String>();

        versions.put("1", "Live");


        String inheritedLabel = null;

        /* Calcul du label hérité */


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
                String parentVersion = po.getParent().getProperty("osivia.cms.displayLiveVersion");
                if (parentVersion != null) {

                    inheritedLabel = versions.get(parentVersion);

                }
                ;

            }

        }

        if (inheritedLabel == null) {
            inheritedLabel = "Publié";
        }


        inheritedLabel = "Herité [" + inheritedLabel + "]";

        versions.put("__inherited", inheritedLabel);


        StringBuffer select = new StringBuffer();

        String disabled = "";
        if (StringUtils.isNotEmpty(po.getDeclaredProperty("osivia.cms.basePath"))) {
            disabled = "disabled='disabled'";
        }

        select.append("<select name=\"" + versionName + "\"" + disabled + ">");

        if (!versions.isEmpty()) {

            if ((selectedVersion == null) || (selectedVersion.length() == 0)) {

                select.append("<option selected=\"selected\" value=\"\">Publiée</option>");

            } else {

                select.append("<option value=\"\">Publiée</option>");

            }
            for (String possibleVersion : versions.keySet()) {
                if ((selectedVersion != null) && (selectedVersion.length() != 0) && possibleVersion.equals(selectedVersion)) {

                    select.append("<option selected=\"selected\" value=\"" + possibleVersion + "\">" + versions.get(possibleVersion) + "</option>");

                } else {

                    select.append("<option value=\"" + possibleVersion + "\">" + versions.get(possibleVersion) + "</option>");

                }
            }
        }

        select.append("</select>");

        return select.toString();

    }


    /**
     * {@inheritDoc}
     */
    public String formatHTMLTreePortalObjects(Page currentPage, ControllerContext context, String idPrefix) throws IOException {
        Set<String> options = new TreeSet<String>();
        return this.formatHtmlTreePortalObjects(currentPage, context, idPrefix, options);
    }

    /**
     * {@inheritDoc}
     */
    public String formatHTMLTreePageParent(Page currentPage, ControllerContext context, String idPrefix) throws IOException {
        Set<String> options = new TreeSet<String>();
        options.add(DISPLAY_ROOT);
        options.add(HIDE_DYNAMIC_PAGES);
        options.add(NON_TEMPLATED_PAGES_FILTER);
        return this.formatHtmlTreePortalObjects(currentPage, context, idPrefix, options);
    }

    /**
     * {@inheritDoc}
     */
    public String formatHTMLTreeTemplateParent(Page currentPage, ControllerContext context, String idPrefix) throws IOException {
        Set<String> options = new TreeSet<String>();
        options.add(HIDE_DYNAMIC_PAGES);
        options.add(TEMPLATED_PAGES_FILTER);
        return this.formatHtmlTreePortalObjects(currentPage, context, idPrefix, options);
    }

    /**
     * {@inheritDoc}
     */
    public String formatHTMLTreePortalObjectsMove(Page currentPage, ControllerContext context, String idPrefix) throws IOException {
        Set<String> options = new TreeSet<String>();
        options.add(DISPLAY_VIRTUAL_END_NODES);
        return this.formatHtmlTreePortalObjects(currentPage, context, idPrefix, options);
    }

    /**
     * {@inheritDoc}
     */
    public String formatHTMLTreePortalObjectsAlphaOrder(Page currentPage, ControllerContext context, String idPrefix) throws IOException {
        Set<String> options = new TreeSet<String>();
        options.add(SORT_ALPHABETICALLY);
        return this.formatHtmlTreePortalObjects(currentPage, context, idPrefix, options);
    }

    /**
     * Utility method used to format hierarchical tree pages into HTML data, with UL and LI nodes.
     *
     * @param currentPage current page
     * @param context controller context
     * @param idPrefix avoid multiples identifiers with this prefix
     * @param options format options
     * @return HTML data
     */
    private String formatHtmlTreePortalObjects(Page currentPage, ControllerContext context, String idPrefix, Set<String> options) throws IOException {
        if ((currentPage == null) || (context == null)) {
            return null;
        }

        Locale locale = context.getServerInvocation().getRequest().getLocale();

        String virtualEndNodesText = null;
        if (options.contains(DISPLAY_VIRTUAL_END_NODES)) {
            virtualEndNodesText = this.internationalizationService.getString(InternationalizationConstants.KEY_VIRTUAL_END_NODES, locale);
        }


        IDynamicObjectContainer dynamicObjectContainer = Locator.findMBean(IDynamicObjectContainer.class, "osivia:service=DynamicPortalObjectContainer");
        dynamicObjectContainer.startPersistentIteration();


        Portal portal =  (Portal) this.portalObjectContainer.getObject(currentPage.getPortal().getId());

        // Recursive tree generation
        Element ulChildren = this.generateRecursiveHtmlTreePortalObjects(portal, context, idPrefix, virtualEndNodesText, options);
        dynamicObjectContainer.stopPersistentIteration();


        // Root generation
        Element ul;
        if (options.contains(DISPLAY_ROOT) && (ulChildren != null)) {
            String portalId = this.formatHtmlSafeEncodingId(portal.getId());

            ul = new DOMElement(QName.get(HTMLConstants.UL));

            Element li = new DOMElement(QName.get(HTMLConstants.LI));
            li.addAttribute(QName.get(HTMLConstants.ID), idPrefix + portalId);
            li.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_NAVIGATION_ITEM);
            ul.add(li);

            Element a = new DOMElement(QName.get(HTMLConstants.A));
            a.addAttribute(QName.get(HTMLConstants.HREF), HTMLConstants.A_HREF_DEFAULT);
            a.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_ROOT_NODE, locale));
            li.add(a);

            li.add(ulChildren);
        } else {
            ul = ulChildren;
        }

        // Get HTML data
        String resultat = this.writeHtmlData(ul);
        return resultat;
    }

    /**
     * Utility method used to generate recursive HTML tree of portal objects.
     *
     * @param parent parent page or portal
     * @param context controller context, which contains locales and URL generation data
     * @param idPrefix avoid multiples identifiers with this prefix
     * @param virtualEndNodesText virtual end nodes text, null if these nodes aren't to be displayed
     * @param options format options
     * @return HTML "ul" node
     * @throws IOException
     */
    private Element generateRecursiveHtmlTreePortalObjects(PortalObject parent, ControllerContext context, String idPrefix, String virtualEndNodesText,
            Set<String> options) throws IOException {
        Locale[] locales = context.getServerInvocation().getRequest().getLocales();

        Collection<PortalObject> children = parent.getChildren(PortalObject.PAGE_MASK);
        if (CollectionUtils.isEmpty(children)) {
            return null;
        }

        // Contrôle des droits et tri des pages
        PortalAuthorizationManager authManager = this.portalAuthorizationManagerFactory.getManager();

        SortedSet<Page> sortedPages;
        if (options.contains(SORT_ALPHABETICALLY)) {
            PortalObjectNameComparator comparator = new PortalObjectNameComparator(locales);
            sortedPages = new TreeSet<Page>(comparator);
        } else {
            sortedPages = new TreeSet<Page>(PortalObjectOrderComparator.getInstance());
        }

        for (PortalObject child : children) {
            PortalObjectPermission permission = new PortalObjectPermission(child.getId(), PortalObjectPermission.VIEW_MASK);

            if (authManager.checkPermission(permission)) {
                Page page = (Page) child;
                PageType pageType = PageType.getPageType(page, context);

                // Check display if current page is a dynamic page
                boolean checkDynamicDisplay = !(options.contains(HIDE_DYNAMIC_PAGES) && !PageType.STATIC_PAGE.equals(pageType));
                // Check display if current page is a template
                boolean checkTemplateDisplay = !(options.contains(TEMPLATED_PAGES_FILTER) && !PortalObjectUtils.isTemplate(page));
                // Check display if current page isn't a template
                boolean checkNonTemplateDisplay = !(options.contains(NON_TEMPLATED_PAGES_FILTER) && PortalObjectUtils.isTemplate(page));

                if (checkDynamicDisplay && checkTemplateDisplay && checkNonTemplateDisplay) {
                    sortedPages.add(page);
                }
            }
        }

        if (CollectionUtils.isEmpty(sortedPages)) {
            return null;
        }

        // Generate HTML node for each page
        Element ul = new DOMElement(QName.get(HTMLConstants.UL));
        ul.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_NAVIGATION_ITEM);

        for (Page page : sortedPages) {
            String pageId = this.formatHtmlSafeEncodingId(page.getId());
            String pageName = PortalObjectUtils.getDisplayName(page, locales);

            // URL
            ViewPageCommand showPage = new ViewPageCommand(page.getId());
            String url = new PortalURLImpl(showPage, context, null, null).toString();
            url = url + "?init-state=true";

            Element li = new DOMElement(QName.get(HTMLConstants.LI));
            li.addAttribute(QName.get(HTMLConstants.ID), idPrefix + pageId);
            li.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_NAVIGATION_ITEM);
            li.addAttribute(QName.get(HTMLConstants.REL), HTML_REL_PAGE);
            ul.add(li);

            Element a = new DOMElement(QName.get(HTMLConstants.A));
            a.addAttribute(QName.get(HTMLConstants.HREF), url);
            a.setText(pageName);
            li.add(a);

            // Recursive generation
            Element ulChildren = this.generateRecursiveHtmlTreePortalObjects(page, context, idPrefix, virtualEndNodesText, options);
            if (ulChildren != null) {
                li.add(ulChildren);
            }
        }

        // Virtual end node
        if (StringUtils.isNotEmpty(virtualEndNodesText)) {
            String parentId = this.formatHtmlSafeEncodingId(parent.getId());

            Element liVirtualEndNode = new DOMElement(QName.get(HTMLConstants.LI));
            liVirtualEndNode.addAttribute(QName.get(HTMLConstants.ID), idPrefix + parentId + InternalConstants.SUFFIX_VIRTUAL_END_NODES_ID);
            liVirtualEndNode.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_NAVIGATION_ITEM);
            liVirtualEndNode.addAttribute(QName.get(HTMLConstants.REL), HTML_REL_PAGE);

            Element aVirtualEndNode = new DOMElement(QName.get(HTMLConstants.A));
            aVirtualEndNode.addAttribute(QName.get(HTMLConstants.HREF), HTMLConstants.A_HREF_DEFAULT);
            aVirtualEndNode.setText(virtualEndNodesText);
            liVirtualEndNode.add(aVirtualEndNode);

            ul.add(liVirtualEndNode);
        }

        return ul;
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

        Element table = new DOMElement(QName.get(HTMLConstants.DIV));
        table.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYBOX_TABLE);

        for (InstanceDefinition instance : instances) {
            // Get portlet
            Portlet portlet;
            try {
                portlet = instance.getPortlet();
            } catch (PortletInvokerException e) {
                // Portlet non déployé
                continue;
            }

            // HTML elements initialization
            Element row = new DOMElement(QName.get(HTMLConstants.DIV));
            row.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYBOX_ROW);
            table.add(row);
            Element leftCell = new DOMElement(QName.get(HTMLConstants.DIV));
            leftCell.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYBOX_CELL);
            row.add(leftCell);
            Element middleCell = new DOMElement(QName.get(HTMLConstants.DIV));
            middleCell.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYBOX_CELL);
            row.add(middleCell);
            Element rightCell = new DOMElement(QName.get(HTMLConstants.DIV));
            rightCell.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYBOX_CELL);
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
            Element input = new DOMElement(QName.get(HTMLConstants.INPUT));
            input.addAttribute(QName.get(HTMLConstants.TYPE), HTMLConstants.INPUT_TYPE_SUBMIT);
            input.addAttribute(QName.get(HTMLConstants.VALUE),
                    this.internationalizationService.getString(InternationalizationConstants.KEY_ADD_PORTLET_SUBMIT_VALUE, locale));
            input.addAttribute(QName.get(HTMLConstants.ONCLICK), "selectPortlet('" + instance.getId() + "', this.form)");
            rightCell.add(input);
        }

        // Get HTML data
        String resultat = this.writeHtmlData(table);
        return resultat;
    }


    /**
     * {@inheritDoc}
     */
    public String formatHtmlWindowsSettings(Page currentPage, List<Window> windows, ControllerContext context) throws IOException {
        if (context == null) {
            return null;
        }

        HttpServletRequest httpRequest = context.getServerInvocation().getServerContext().getClientRequest();
        Locale locale = httpRequest.getLocale();

        Portal portal = currentPage.getPortal();
        String portalContextPath = context.getServerInvocation().getServerContext().getPortalContextPath();
        String commandUrl = portalContextPath + "/commands";

        // Portal styles
        String portalStylesProperty = portal.getDeclaredProperty("osivia.liste_styles");
        List<String> portalStyles = new ArrayList<String>();
        if (StringUtils.isNotEmpty(portalStylesProperty)) {
            portalStyles.addAll(Arrays.asList(portalStylesProperty.split(",")));
        }

        // HTML "div" fancyboxes parent node
        Element divParent = new DOMElement(QName.get(HTMLConstants.DIV));
        divParent.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYBOX_CONTAINER);

        if (CollectionUtils.isEmpty(windows)) {
            divParent.setText(HTMLConstants.TEXT_DEFAULT);
        } else {
            // Loop on each page window
            for (Window window : windows) {
                boolean checkboxChecked;

                String windowId = window.getId().toString(PortalObjectPath.SAFEST_FORMAT);
                String fancyboxId = PREFIX_ID_FANCYBOX_WINDOW_SETTINGS + windowId;

                // Fancybox
                Element divFancyboxContent = new DOMElement(QName.get(HTMLConstants.DIV));
                divFancyboxContent.addAttribute(QName.get(HTMLConstants.ID), fancyboxId);
                divParent.add(divFancyboxContent);

                // Form
                Element form = new DOMElement(QName.get(HTMLConstants.FORM));
                form.addAttribute(QName.get(HTMLConstants.ACTION), commandUrl);
                form.addAttribute(QName.get(HTMLConstants.METHOD), HTMLConstants.FORM_METHOD_GET);
                form.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYBOX_FORM);
                divFancyboxContent.add(form);

                // Hidden fields
                Element inputHiddenAction = new DOMElement(QName.get(HTMLConstants.INPUT));
                inputHiddenAction.addAttribute(QName.get(HTMLConstants.TYPE), HTMLConstants.INPUT_TYPE_HIDDEN);
                inputHiddenAction.addAttribute(QName.get(HTMLConstants.NAME), HTML_NAME_ACTION);
                inputHiddenAction.addAttribute(QName.get(HTMLConstants.VALUE), HTML_VALUE_ACTION_CHANGE_WINDOW_PROPERTIES);
                form.add(inputHiddenAction);

                Element inputHiddenWindowId = new DOMElement(QName.get(HTMLConstants.INPUT));
                inputHiddenWindowId.addAttribute(QName.get(HTMLConstants.TYPE), HTMLConstants.INPUT_TYPE_HIDDEN);
                inputHiddenWindowId.addAttribute(QName.get(HTMLConstants.NAME), HTML_NAME_WINDOW_ID);
                inputHiddenWindowId.addAttribute(QName.get(HTMLConstants.VALUE), windowId);
                form.add(inputHiddenWindowId);

                // Table
                Element table = new DOMElement(QName.get(HTMLConstants.DIV));
                table.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYBOX_TABLE);
                form.add(table);

                // Styles rows
                String windowStylesProperty = window.getDeclaredProperty("osivia.style");
                List<String> windowStyles = new ArrayList<String>();
                if (StringUtils.isNotEmpty(windowStylesProperty)) {
                    windowStyles.addAll(Arrays.asList(windowStylesProperty.split(",")));
                }
                this.insertStylesRows(table, locale, portalStyles, windowStyles);

                // Title bar display row
                String hideTitle = window.getDeclaredProperty("osivia.hideTitle");
                checkboxChecked = !"1".equals(hideTitle);
                table.add(this.generateRow(
                        this.internationalizationService.getString(InternationalizationConstants.KEY_WINDOW_PROPERTIES_TITLE_DISPLAY, locale),
                        HTMLConstants.INPUT_TYPE_CHECKBOX, "displayTitle", "1", checkboxChecked));

                // Title
                String title = window.getDeclaredProperty("osivia.title");
                if (title == null) {
                    title = StringUtils.EMPTY;
                }
                table.add(this.generateRow(this.internationalizationService.getString(InternationalizationConstants.KEY_WINDOW_PROPERTIES_TITLE, locale),
                        HTMLConstants.INPUT_TYPE_TEXT, "title", title, false));

                // Icons display
                String hideDecorators = window.getDeclaredProperty("osivia.hideDecorators");
                checkboxChecked = !"1".equals(hideDecorators);
                table.add(this.generateRow(
                        this.internationalizationService.getString(InternationalizationConstants.KEY_WINDOW_PROPERTIES_ICONS_DISPLAY, locale),
                        HTMLConstants.INPUT_TYPE_CHECKBOX, "displayDecorators", "1", checkboxChecked));

                // AJAX links and forms
                String ajaxLinks = window.getProperty("osivia.ajaxLink");
                checkboxChecked = "1".equals(ajaxLinks);
                table.add(this.generateRow(this.internationalizationService.getString(InternationalizationConstants.KEY_WINDOW_PROPERTIES_AJAX_LINK, locale),
                        HTMLConstants.INPUT_TYPE_CHECKBOX, "ajaxLink", "1", checkboxChecked));

                // Print
                String print = window.getProperty("osivia.printPortlet");
                checkboxChecked = "1".equals(print);
                table.add(this.generateRow(this.internationalizationService.getString(InternationalizationConstants.KEY_WINDOW_PROPERTIES_PRINT, locale),
                        HTMLConstants.INPUT_TYPE_CHECKBOX, "printPortlet", "1", checkboxChecked));

                // Hide empty portlet
                String hideEmptyPortlet = window.getProperty("osivia.hideEmptyPortlet");
                checkboxChecked = "1".equals(hideEmptyPortlet);

                // Conditional scope
                this.insertConditionalScopeRow(table, locale, window);

                // Customize ID
                String customizeId = window.getDeclaredProperty("osivia.idPerso");
                if (customizeId == null) {
                    customizeId = StringUtils.EMPTY;
                }
                table.add(this.generateRow(this.internationalizationService.getString(InternationalizationConstants.KEY_WINDOW_PROPERTIES_CUSTOM_ID, locale),
                        HTMLConstants.INPUT_TYPE_TEXT, "idPerso", customizeId, false));

                // Shared cache ID
                String cacheId = window.getProperty("osivia.cacheID");
                if (cacheId == null) {
                    cacheId = StringUtils.EMPTY;
                }
                table.add(this.generateRow(
                        this.internationalizationService.getString(InternationalizationConstants.KEY_WINDOW_PROPERTIES_SHARED_CACHE_ID, locale),
                        HTMLConstants.INPUT_TYPE_TEXT, "cacheID", cacheId, false));

                // Bash activation
                String bashActivation = window.getDeclaredProperty("osivia.bshActivation");
                boolean isBashActive = "1".equals(bashActivation);
                String scriptContent = window.getProperty("osivia.bshScript");
                if (scriptContent == null) {
                    scriptContent = StringUtils.EMPTY;
                }
                this.insertDynamicPropertiesRows(table, locale, isBashActive, scriptContent);

                // Selection service dependency
                String cacheEvents = window.getProperty("osivia.cacheEvents");
                checkboxChecked = "selection".equals(cacheEvents);
                table.add(this.generateRow(
                        this.internationalizationService.getString(InternationalizationConstants.KEY_WINDOW_PROPERTIES_SELECTION_DEPENDENCY, locale),
                        HTMLConstants.INPUT_TYPE_CHECKBOX,
                        "selectionDep", "1", checkboxChecked));

                // Buttons
                Element buttonsContainer = new DOMElement(QName.get(HTMLConstants.DIV));
                buttonsContainer.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYBOX_CENTER_CONTENT);
                form.add(buttonsContainer);

                // Submit button
                Element submitButton = new DOMElement(QName.get(HTMLConstants.INPUT));
                submitButton.addAttribute(QName.get(HTMLConstants.TYPE), HTMLConstants.INPUT_TYPE_SUBMIT);
                submitButton.addAttribute(QName.get(HTMLConstants.VALUE),
                        this.internationalizationService.getString(InternationalizationConstants.KEY_WINDOW_PROPERTIES_PROPERTIES_SUBMIT, locale));
                buttonsContainer.add(submitButton);

                // Cancel button
                Element cancelButton = new DOMElement(QName.get(HTMLConstants.INPUT));
                cancelButton.addAttribute(QName.get(HTMLConstants.TYPE), HTMLConstants.INPUT_TYPE_BUTTON);
                cancelButton.addAttribute(QName.get(HTMLConstants.VALUE),
                        this.internationalizationService.getString(InternationalizationConstants.KEY_WINDOW_PROPERTIES_PROPERTIES_CANCEL, locale));
                cancelButton.addAttribute(QName.get(HTMLConstants.ONCLICK), "closeFancybox()");
                buttonsContainer.add(cancelButton);
            }
        }

        // Get HTML data
        String resultat = this.writeHtmlData(divParent);
        return resultat;
    }


    /**
     * Utility method, used to write HTML data.
     *
     * @param htmlElement HTML element to write
     * @return HTML data
     * @throws IOException
     */
    protected String writeHtmlData(Element htmlElement) throws IOException {
        if (htmlElement == null) {
            return StringUtils.EMPTY;
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        OutputStream bufferedOutput = new BufferedOutputStream(output);
        HTMLWriter htmlWriter = null;
        String resultat = null;
        try {
            htmlWriter = new HTMLWriter(bufferedOutput);
            htmlWriter.setEscapeText(false);
            htmlWriter.write(htmlElement);

            resultat = output.toString(CharEncoding.UTF_8);

            bufferedOutput.close();
            output.close();
            htmlWriter.close();
        } catch (IOException e) {
            throw e;
        }
        return resultat;
    }


    /**
     * Utility method used to insert styles rows.
     *
     * @param tableParent parent table node
     * @param locale current locale
     * @param portalStyles portal styles
     * @param windowStyles window styles
     */
    private void insertStylesRows(Element tableParent, Locale locale, List<String> portalStyles, List<String> windowStyles) {
        String displayStyle = StringUtils.EMPTY;
        for (String windowStyle : windowStyles) {
            displayStyle += windowStyle + " ";
        }
        if (StringUtils.isEmpty(displayStyle)) {
            displayStyle = this.internationalizationService.getString(InternationalizationConstants.KEY_WINDOW_PROPERTIES_NO_STYLE, locale);
        }

        Set<String> styles = new HashSet<String>(portalStyles);
        styles.addAll(windowStyles);

        // Styles row
        Element rowStyles = new DOMElement(QName.get(HTMLConstants.DIV));
        rowStyles.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYBOX_ROW);
        tableParent.add(rowStyles);

        // Styles left cell
        Element leftCellStyles = new DOMElement(QName.get(HTMLConstants.DIV));
        leftCellStyles.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYBOX_CELL + " " + HTMLConstants.CLASS_FANCYBOX_LABEL);
        leftCellStyles.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_WINDOW_PROPERTIES_STYLES, locale));
        rowStyles.add(leftCellStyles);

        // Styles right cell
        Element rightCellStyles = new DOMElement(QName.get(HTMLConstants.DIV));
        rightCellStyles.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYBOX_CELL);
        rightCellStyles.setText(displayStyle);
        rowStyles.add(rightCellStyles);

        // Styles right cell display link
        Element aStylesDisplay = new DOMElement(QName.get(HTMLConstants.A));
        aStylesDisplay.addAttribute(QName.get(HTMLConstants.HREF), HTMLConstants.A_HREF_DEFAULT);
        aStylesDisplay.addAttribute(QName.get(HTMLConstants.ONCLICK), "toggleRow(this, '" + HTML_CLASS_TOGGLE_STYLES_ROW + "')");
        aStylesDisplay.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_WINDOW_PROPERTIES_STYLES_DISPLAY_LINK, locale));
        rightCellStyles.add(aStylesDisplay);

        if (CollectionUtils.isNotEmpty(styles)) {
            // Styles display toggle row
            Element rowToggle = new DOMElement(QName.get(HTMLConstants.DIV));
            rowToggle.addAttribute(QName.get(HTMLConstants.CLASS), HTML_CLASS_TOGGLE_ROW + " " + HTML_CLASS_TOGGLE_STYLES_ROW + " "
                    + HTMLConstants.CLASS_FANCYBOX_ROW);
            rowToggle.addAttribute(QName.get(HTMLConstants.STYLE), HTMLConstants.STYLE_DISPLAY_NONE);
            tableParent.add(rowToggle);

            // Styles display toggle empty left cell
            Element leftCellToggle = new DOMElement(QName.get(HTMLConstants.DIV));
            leftCellToggle.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYBOX_CELL);
            leftCellToggle.setText(HTMLConstants.TEXT_DEFAULT);
            rowToggle.add(leftCellToggle);

            // Styles display toggle right cell
            Element rightCellToggle = new DOMElement(QName.get(HTMLConstants.DIV));
            rightCellToggle.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYBOX_CELL);
            rowToggle.add(rightCellToggle);

            // Styles display table
            Element table = new DOMElement(QName.get(HTMLConstants.DIV));
            table.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYBOX_TABLE);
            rightCellToggle.add(table);

            // Loop on each style
            for (String style : styles) {
                boolean checkboxChecked = windowStyles.contains(style);
                Element row = this.generateRow(style, HTMLConstants.INPUT_TYPE_CHECKBOX, HTML_NAME_STYLE, style, checkboxChecked);
                table.add(row);
            }
        }
    }


    /**
     * Utility method used to insert conditional scope row.
     *
     * @param tableParent parent table node
     * @param locale current locale
     * @param window window
     */
    private void insertConditionalScopeRow(Element tableParent, Locale locale, Window window) {
        String conditionalScope = window.getProperty("osivia.conditionalScope");
        Map<String, String> scopes = new LinkedHashMap<String, String>();

        List<ProfilBean> profils = this.getProfilManager().getListeProfils();
        for (ProfilBean profil : profils) {
            scopes.put(profil.getName(),
                    this.internationalizationService.getString(InternationalizationConstants.KEY_WINDOW_PROPERTIES_CONDITIONAL_SCOPE_PROFIL, locale) + " "
                            + profil.getName());
        }
        if (!scopes.isEmpty()) {
            // Row
            Element row = new DOMElement(QName.get(HTMLConstants.DIV));
            row.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYBOX_ROW);
            tableParent.add(row);

            // Left cell
            Element leftCell = new DOMElement(QName.get(HTMLConstants.DIV));
            leftCell.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYBOX_CELL + " " + HTMLConstants.CLASS_FANCYBOX_LABEL);
            leftCell.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_WINDOW_PROPERTIES_CONDITIONAL_SCOPE_DISPLAY, locale));
            row.add(leftCell);

            // Right cell
            Element rightCell = new DOMElement(QName.get(HTMLConstants.DIV));
            rightCell.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYBOX_CELL);
            row.add(rightCell);

            // Select
            Element select = new DOMElement(QName.get(HTMLConstants.SELECT));
            select.addAttribute(QName.get(HTMLConstants.NAME), "conditionalScope");
            rightCell.add(select);

            // All profiles option
            Element optionAllProfiles = new DOMElement(QName.get(HTMLConstants.OPTION));
            optionAllProfiles.addAttribute(QName.get(HTMLConstants.VALUE), StringUtils.EMPTY);
            if (StringUtils.isNotEmpty(conditionalScope)) {
                optionAllProfiles.addAttribute(QName.get(HTMLConstants.SELECTED), HTMLConstants.INPUT_SELECTED);
            }
            optionAllProfiles.setText(this.internationalizationService.getString(
                    InternationalizationConstants.KEY_WINDOW_PROPERTIES_CONDITIONAL_SCOPE_ALL_PROFILES, locale));
            select.add(optionAllProfiles);

            for (String scope : scopes.keySet()) {
                // Scope option
                Element optionScope = new DOMElement(QName.get(HTMLConstants.OPTION));
                optionScope.addAttribute(QName.get(HTMLConstants.VALUE), scope);
                if (StringUtils.equals(conditionalScope, scope)) {
                    optionScope.addAttribute(QName.get(HTMLConstants.SELECTED), HTMLConstants.INPUT_SELECTED);
                }
                optionScope.setText(scopes.get(scope));
                select.add(optionScope);
            }
        }
    }

    /**
     * Utility method used to insert dynamic properties rows.
     *
     * @param tableParent parent table node
     * @param locale current locale
     * @param isBashActive bash active indicator
     * @param scriptContent script content
     */
    private void insertDynamicPropertiesRows(Element tableParent, Locale locale, boolean isBashActive, String scriptContent) {
        // Label - checkbox input unique ID link
        String checkboxId = UUID.randomUUID().toString();

        // Dynamic properties label
        String dynamicPropertiesLabel;
        if (isBashActive) {
            dynamicPropertiesLabel = InternationalizationConstants.KEY_WINDOW_PROPERTIES_SHELL_SCRIPT;
        } else {
            dynamicPropertiesLabel = "-";
        }

        // Dynamic properties row
        Element rowDynamicProperties = new DOMElement(QName.get(HTMLConstants.DIV));
        rowDynamicProperties.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYBOX_ROW);
        tableParent.add(rowDynamicProperties);

        // Dynamic properties left cell
        Element leftCellDynamicProperties = new DOMElement(QName.get(HTMLConstants.DIV));
        leftCellDynamicProperties.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYBOX_CELL + " " + HTMLConstants.CLASS_FANCYBOX_LABEL);
        leftCellDynamicProperties.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_WINDOW_PROPERTIES_DYNAMIC_PROPERTIES,
                locale));
        rowDynamicProperties.add(leftCellDynamicProperties);

        // Dynamic properties right cell
        Element rightCellDynamicProperties = new DOMElement(QName.get(HTMLConstants.DIV));
        rightCellDynamicProperties.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYBOX_CELL);
        rightCellDynamicProperties.setText(dynamicPropertiesLabel);
        rowDynamicProperties.add(rightCellDynamicProperties);

        // Dynamic properties right cell display link
        Element aDynamicPropertiesDisplay = new DOMElement(QName.get(HTMLConstants.A));
        aDynamicPropertiesDisplay.addAttribute(QName.get(HTMLConstants.HREF), HTMLConstants.A_HREF_DEFAULT);
        aDynamicPropertiesDisplay.addAttribute(QName.get(HTMLConstants.ONCLICK), "toggleRow(this, '" + HTML_CLASS_TOGGLE_DYNAMIC_PROPERTIES_ROW + "')");
        aDynamicPropertiesDisplay.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_CHANGE, locale));
        rightCellDynamicProperties.add(aDynamicPropertiesDisplay);

        // Dynamic properties display toggle row
        Element rowToggle = new DOMElement(QName.get(HTMLConstants.DIV));
        rowToggle.addAttribute(QName.get(HTMLConstants.CLASS), HTML_CLASS_TOGGLE_ROW + " " + HTML_CLASS_TOGGLE_DYNAMIC_PROPERTIES_ROW + " "
                + HTMLConstants.CLASS_FANCYBOX_ROW);
        rowToggle.addAttribute(QName.get(HTMLConstants.STYLE), HTMLConstants.STYLE_DISPLAY_NONE);
        tableParent.add(rowToggle);

        // Dynamic properties display toggle left cell
        Element leftCellToggle = new DOMElement(QName.get(HTMLConstants.DIV));
        leftCellToggle.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYBOX_CELL);
        rowToggle.add(leftCellToggle);

        // Dynamic properties display toggle checkbox label
        Element label = new DOMElement(QName.get(HTMLConstants.LABEL));
        label.addAttribute(QName.get(HTMLConstants.FOR), checkboxId);
        label.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_WINDOW_PROPERTIES_DYNAMIC_PROPERTIES_BEAN_SHELL, locale));
        leftCellToggle.add(label);

        // Dynamic properties display toggle checkbox
        Element checkbox = new DOMElement(QName.get(HTMLConstants.INPUT));
        checkbox.addAttribute(QName.get(HTMLConstants.ID), checkboxId);
        checkbox.addAttribute(QName.get(HTMLConstants.TYPE), HTMLConstants.INPUT_TYPE_CHECKBOX);
        checkbox.addAttribute(QName.get(HTMLConstants.NAME), "bshActivation");
        checkbox.addAttribute(QName.get(HTMLConstants.VALUE), "1");
        checkbox.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_SMALL_INPUT);
        if (isBashActive) {
            checkbox.addAttribute(QName.get(HTMLConstants.CHECKED), HTMLConstants.INPUT_CHECKED);
        }
        leftCellToggle.add(checkbox);

        // Dynamic properties display toggle right cell
        Element rightCellToggle = new DOMElement(QName.get(HTMLConstants.DIV));
        rightCellToggle.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYBOX_CELL);
        rowToggle.add(rightCellToggle);

        // Dynamic properties display toggle right cell textarea
        Element textarea = new DOMElement(QName.get(HTMLConstants.TEXTAREA));
        textarea.addAttribute(QName.get(HTMLConstants.NAME), "bshScript");
        textarea.addAttribute(QName.get(HTMLConstants.ROWS), "10");
        textarea.addAttribute(QName.get(HTMLConstants.COLS), "75");
        textarea.setText(scriptContent);
        rightCellToggle.add(textarea);

        // Dynamic properties display toggle right cell example
        Element example = new DOMElement(QName.get(HTMLConstants.PRE));
        StringBuffer sb = new StringBuffer();
        sb.append("/*\n");
        sb.append("implicits variables :\n");
        sb.append("   - pageParamsEncoder : parameters encoder (decoded to List&lt;String&gt;)\n");
        sb.append("   - windowsProperties : window dynamic properties (Map&lt;String, String&gt;)\n");
        sb.append("        > osivia.dynamicCSSClasses : css class names separated by a space (eq : \"css1 css2\")\n");
        sb.append("*/\n");
        sb.append("\n");
        sb.append("import java.util.List;\n");
        sb.append("\n");
        sb.append("List cssSelectorValues =  pageParamsEncoder.decode(\"selectors\", \"cssSelector\");\n");
        sb.append("\n");
        sb.append("if (cssSelectorValues != null) {\n");
        sb.append("    windowProperties.put(\"osivia.dynamicCSSClasses\", cssSelectorValues.get(0));\n");
        sb.append("}\n");
        sb.append("rightCellToggle.add(example);\n");
        example.setText(sb.toString());
        rightCellToggle.add(example);
    }


    private Element generateRow(String label, String inputType, String inputName, String inputValue, boolean checkboxChecked) {
        // Row
        Element row = new DOMElement(QName.get(HTMLConstants.DIV));
        row.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYBOX_ROW);

        // Left cell
        Element leftCell = new DOMElement(QName.get(HTMLConstants.DIV));
        leftCell.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYBOX_CELL + " " + HTMLConstants.CLASS_FANCYBOX_LABEL);
        leftCell.setText(label);
        row.add(leftCell);

        // Right cell
        Element rightCell = new DOMElement(QName.get(HTMLConstants.DIV));
        rightCell.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYBOX_CELL);
        row.add(rightCell);

        // Right cell input
        Element input = new DOMElement(QName.get(HTMLConstants.INPUT));
        input.addAttribute(QName.get(HTMLConstants.TYPE), inputType);
        input.addAttribute(QName.get(HTMLConstants.NAME), inputName);
        if (inputValue != null) {
            input.addAttribute(QName.get(HTMLConstants.VALUE), inputValue);
        }
        if (HTMLConstants.INPUT_TYPE_CHECKBOX.equals(inputType)) {
            input.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_SMALL_INPUT);
            if (checkboxChecked) {
                input.addAttribute(QName.get(HTMLConstants.CHECKED), HTMLConstants.INPUT_CHECKED);
            }
        }
        rightCell.add(input);

        return row;
    }


    @Override
    public ControllerResponse invoke(ControllerCommand command) throws Exception {
        ControllerResponse response = (ControllerResponse) command.invokeNext();

        if ((response instanceof PageRendition) && (command instanceof PageCommand)) {
            // Teste si le mode assistant est activé
            Object windowSettingMode = command.getControllerContext()
                    .getAttribute(ControllerCommand.SESSION_SCOPE, InternalConstants.ATTR_WINDOWS_SETTING_MODE);
            if (!InternalConstants.VALUE_WINDOWS_SETTING_WIZARD_MODE.equals(windowSettingMode)) {
                return response;
            }

            PageRendition rendition = (PageRendition) response;
            PageCommand pageCommand = (PageCommand) command;
            Page page = pageCommand.getPage();
            ControllerContext context = command.getControllerContext();

            // This is for inject the pageSettings
            ControllerRequestDispatcher dispatcher = context.getRequestDispatcher(this.getTargetContextPath(), this.getPageSettingPath());

            // Internationalization service
            dispatcher.setAttribute(InternalConstants.ATTR_INTERNATIONALIZATION_SERVICE, this.internationalizationService);
            // Formatter
            dispatcher.setAttribute(InternalConstants.ATTR_WINDOWS_FORMATTER, this);
            // Context
            dispatcher.setAttribute(InternalConstants.ATTR_WINDOWS_CONTROLLER_CONTEXT, context);
            // URL générique de commande
            String portalContextPath = context.getServerInvocation().getServerContext().getPortalContextPath();
            dispatcher.setAttribute(InternalConstants.ATTR_WINDOWS_COMMAND_URL, portalContextPath + "/commands");
            // Current page
            dispatcher.setAttribute(InternalConstants.ATTR_WINDOWS_PAGE, page);

            PageType pageType = PageType.getPageType(page, context);
            if (!pageType.isTemplated()) {
                // Portlets settings injection
                this.injectPortletSettings(dispatcher, page, rendition, context);
            }

            dispatcher.include();

            context.setAttribute(ControllerCommand.REQUEST_SCOPE, InternalConstants.ATTR_WINDOWS_SETTINGS_CONTENT, dispatcher.getMarkup());
        }

        return response;
    }


    @SuppressWarnings("unchecked")
    private void injectPortletSettings(ControllerRequestDispatcher dispatcher, Page currentPage, PageRendition rendition, ControllerContext context)
            throws Exception {
        HttpServletRequest request = context.getServerInvocation().getServerContext().getClientRequest();

        List<Window> windows = new ArrayList<Window>();

        String layoutId = currentPage.getProperty(ThemeConstants.PORTAL_PROP_LAYOUT);
        PortalLayout pageLayout = this.layoutService.getLayout(layoutId, true);

        this.synchronizeRegionContexts(rendition, currentPage);

        for (Object regionCtxObjet : rendition.getPageResult().getRegions()) {
            RegionRendererContext renderCtx = (RegionRendererContext) regionCtxObjet;

            // On vérifie que cette région fait partie du layout (elle contient des portlets)
            if (pageLayout.getLayoutInfo().getRegionNames().contains(renderCtx.getId())) {
                Map<String, String> regionProperties = renderCtx.getProperties();

                PortalObjectId popupWindowId = (PortalObjectId) context.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupModeWindowID");

                if (popupWindowId == null) {
                    regionProperties.put(InternalConstants.ATTR_WINDOWS_WIZARD_MODE, InternalConstants.VALUE_WINDOWS_WIZARD_TEMPLATE_MODE);
                    regionProperties.put(InternalConstants.ATTR_WINDOWS_ADD_PORTLET_URL, "#add-portlet");
                }

                // Le mode Ajax est incompatble avec le mode "admin".
                // Le passage du mode admin en mode normal n'est pas bien géré par le portail, quand il s'agit d'une requête Ajax.
                DynaRenderOptions.NO_AJAX.setOptions(regionProperties);
                for (Object windowCtx : renderCtx.getWindows()) {

                    WindowRendererContext wrc = (WindowRendererContext) windowCtx;
                    Map<String, String> windowProperties = wrc.getProperties();
                    String windowId = wrc.getId();

                    if (!windowId.endsWith("PIA_EMPTY")) {
                        URLContext urlContext = context.getServerInvocation().getServerContext().getURLContext();
                        PortalObjectId poid = PortalObjectId.parse(windowId, PortalObjectPath.SAFEST_FORMAT);
                        DynamicWindow window = (DynamicWindow) this.getPortalObjectContainer().getObject(poid);

                        if (!window.isSessionWindow() && (popupWindowId == null)) {
                            // Window settings mode
                            windowProperties.put(InternalConstants.ATTR_WINDOWS_SETTING_MODE, InternalConstants.VALUE_WINDOWS_SETTING_WIZARD_MODE);

                            // Commande suppression
                            windowProperties.put(InternalConstants.ATTR_WINDOWS_DELETE_PORTLET_URL, "#delete-portlet");

                            // Commande paramètres
                            windowProperties.put(InternalConstants.ATTR_WINDOWS_DISPLAY_SETTINGS_URL, "#" + PREFIX_ID_FANCYBOX_WINDOW_SETTINGS + windowId);

                            windows.add(window);

                            // Commandes de déplacement
                            MoveWindowCommand upC = new MoveWindowCommand(windowId, MoveWindowCommand.UP);
                            String upUrl = context.renderURL(upC, urlContext, URLFormat.newInstance(true, true));
                            windowProperties.put(InternalConstants.ATTR_WINDOWS_UP_COMMAND_URL, upUrl);

                            MoveWindowCommand downC = new MoveWindowCommand(windowId, MoveWindowCommand.DOWN);
                            String downUrl = context.renderURL(downC, urlContext, URLFormat.newInstance(true, true));
                            windowProperties.put(InternalConstants.ATTR_WINDOWS_DOWN_COMMAND_URL, downUrl);

                            MoveWindowCommand previousC = new MoveWindowCommand(windowId, MoveWindowCommand.PREVIOUS_REGION);
                            String previousRegionUrl = context.renderURL(previousC, urlContext, URLFormat.newInstance(true, true));
                            windowProperties.put(InternalConstants.ATTR_WINDOWS_PREVIOUS_REGION_COMMAND_URL, previousRegionUrl);

                            MoveWindowCommand nextRegionC = new MoveWindowCommand(windowId, MoveWindowCommand.NEXT_REGION);
                            String nextRegionUrl = context.renderURL(nextRegionC, urlContext, URLFormat.newInstance(true, true));
                            windowProperties.put(InternalConstants.ATTR_WINDOWS_NEXT_REGION_COMMAND_URL, nextRegionUrl);

                            // Admin window title
                            String instanceDisplayName = null;
                            InstanceDefinition defInstance = this.getInstanceContainer().getDefinition(window.getContent().getURI());
                            if (defInstance != null) {
                                instanceDisplayName = defInstance.getDisplayName().getString(request.getLocale(), true);
                            }
                            if (instanceDisplayName != null) {
                                windowProperties.put(InternalConstants.ATTR_WINDOWS_INSTANCE_DISPLAY_NAME, instanceDisplayName);

                            }
                        }
                    }
                }
            }
        }

        dispatcher.setAttribute(InternalConstants.ATTR_WINDOWS_CURRENT_LIST, windows);
    }


    /**
     * Synchronize context regions with layout
     *
     * if a region is not present in the context, creates a new one
     *
     * @param rendition
     * @param page
     * @throws Exception
     */
    private void synchronizeRegionContexts(PageRendition rendition, Page page) throws Exception {

        String layoutId = page.getProperty(ThemeConstants.PORTAL_PROP_LAYOUT);
        PortalLayout layout = this.layoutService.getLayout(layoutId, true);

        for (Object region : layout.getLayoutInfo().getRegionNames()) {

            String regionName = (String) region;
            RegionRendererContext renderCtx = rendition.getPageResult().getRegion(regionName);
            if (renderCtx == null) {
                // Empty region - must create blank window
                Map<String, String> windowProps = new HashMap<String, String>();
                windowProps.put(ThemeConstants.PORTAL_PROP_WINDOW_RENDERER, "emptyRenderer");
                windowProps.put(ThemeConstants.PORTAL_PROP_DECORATION_RENDERER, "emptyRenderer");
                windowProps.put(ThemeConstants.PORTAL_PROP_PORTLET_RENDERER, "emptyRenderer");

                WindowResult wr = new WindowResult("PIA_EMPTY", "", Collections.EMPTY_MAP, windowProps, null, WindowState.NORMAL, Mode.VIEW);
                WindowContext settings = new WindowContext(regionName + "_PIA_EMPTY", regionName, "0", wr);
                rendition.getPageResult().addWindowContext(settings);

                renderCtx = rendition.getPageResult().getRegion2(regionName);
            }
        }
    }


    /**
     * @return the pageSettingPath
     */
    public String getPageSettingPath() {
        return this.pageSettingPath;
    }

    /**
     * @param pageSettingPath
     *            the pageSettingPath to set
     */
    public void setPageSettingPath(String pageSettingPath) {
        this.pageSettingPath = pageSettingPath;
    }

    /**
     * @return the roleModule
     */
    public RoleModule getRoleModule() throws Exception {
        if (this.roleModule == null) {
            this.roleModule = (RoleModule) this.getIdentityServiceController().getIdentityContext().getObject(IdentityContext.TYPE_ROLE_MODULE);
        }
        return this.roleModule;
    }

    /**
     * @param roleModule
     *            the roleModule to set
     */
    public void setRoleModule(RoleModule roleModule) {
        this.roleModule = roleModule;
    }

    /**
     * @return the authorizationDomainRegistry
     */
    public AuthorizationDomainRegistry getAuthorizationDomainRegistry() {
        return this.authorizationDomainRegistry;
    }

    /**
     * @param authorizationDomainRegistry
     *            the authorizationDomainRegistry to set
     */
    public void setAuthorizationDomainRegistry(AuthorizationDomainRegistry authorizationDomainRegistry) {
        this.authorizationDomainRegistry = authorizationDomainRegistry;
    }

    /**
     * @return the identityServiceController
     */
    public IdentityServiceController getIdentityServiceController() {
        return this.identityServiceController;
    }

    /**
     * @param identityServiceController
     *            the identityServiceController to set
     */
    public void setIdentityServiceController(IdentityServiceController identityServiceController) {
        this.identityServiceController = identityServiceController;
    }

    /**
     * @return the targetContextPath
     */
    public String getTargetContextPath() {
        return this.targetContextPath;
    }

    /**
     * @param targetContextPath
     *            the targetContextPath to set
     */
    public void setTargetContextPath(String targetContextPath) {
        this.targetContextPath = targetContextPath;
    }

    /**
     * @return the instanceContainer
     */
    public InstanceContainer getInstanceContainer() {
        return this.instanceContainer;
    }

    /**
     * @param instanceContainer
     *            the instanceContainer to set
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
     * Getter for layoutService.
     *
     * @return the layoutService
     */
    public LayoutService getLayoutService() {
        return this.layoutService;
    }

    /**
     * Setter for layoutService.
     *
     * @param layoutService the layoutService to set
     */
    public void setLayoutService(LayoutService layoutService) {
        this.layoutService = layoutService;
    }

    /**
     * Getter for themeService.
     *
     * @return the themeService
     */
    public ThemeService getThemeService() {
        return this.themeService;
    }

    /**
     * Setter for themeService.
     *
     * @param themeService the themeService to set
     */
    public void setThemeService(ThemeService themeService) {
        this.themeService = themeService;
    }

    /**
     * Getter for profilManager.
     *
     * @return the profilManager
     */
    public IProfilManager getProfilManager() {
        return this.profilManager;
    }

    /**
     * Setter for profilManager.
     *
     * @param profilManager the profilManager to set
     */
    public void setProfilManager(IProfilManager profilManager) {
        this.profilManager = profilManager;
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
