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
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
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
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.dynamic.ITemplatePortalObject;
import org.osivia.portal.core.formatters.IFormatter;
import org.osivia.portal.core.page.PageUtils;
import org.osivia.portal.core.page.PortalURLImpl;
import org.osivia.portal.core.portalobjects.DynamicWindow;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;
import org.osivia.portal.core.profils.IProfilManager;
import org.osivia.portal.core.profils.ProfilBean;


public class AssistantPageCustomizerInterceptor extends ControllerInterceptor implements IFormatter {

    /** Windows settings fancyboxes prefix. */
    private static final String PREFIX_ID_FANCYBOX_WINDOW_SETTINGS = "window-settings-";

    /** HTML Fancybox container class. */
    private static final String HTML_CLASS_FANCYBOX_CONTAINER = "fancybox-content";
    /** HTML Fancybox form class. */
    private static final String HTML_CLASS_FANCYBOX_FORM = "fancybox-form";
    /** HTML class "navigation-item" for "li" nodes. */
    private static final String HTML_CLASS_NAVIGATION_ITEM = "navigation-item";
    /** HTML class "fancybox-table" for "div" nodes. */
    private static final String HTML_CLASS_FANCYBOX_TABLE = "fancybox-table";
    /** HTML class "fancybox-table-row" for "div" nodes. */
    private static final String HTML_CLASS_FANCYBOX_ROW = "fancybox-table-row";
    /** HTML class "fancybox-table-cell" for "div" nodes. */
    private static final String HTML_CLASS_FANCYBOX_CELL = "fancybox-table-cell";
    /** HTML class "label" for "div" nodes. */
    private static final String HTML_CLASS_FANCYBOX_LABEL = "label";
    /** HTML class "toggle-row". */
    private static final String HTML_CLASS_TOGGLE_ROW = "toggle-row";
    /** HTML class "styles-toggle-row". */
    private static final String HTML_CLASS_TOGGLE_STYLES_ROW = "styles-toggle-row";
    /** HTML class "dynamic-properties-toggle-row". */
    private static final String HTML_CLASS_TOGGLE_DYNAMIC_PROPERTIES_ROW = "dynamic-properties-toggle-row";
    /** HTML class "small-input" for small inputs like checkboxes. */
    private static final String HTML_CLASS_SMALL_INPUT = "small-input";
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
    /** HTML default href for "a" nodes. */
    private static final String HTML_HREF_DEFAULT = "#";
    /** HTML default text. */
    private static final String HTML_TEXT_DEFAULT = "&nbsp;";
    /** HTML display none style. */
    private static final String HTML_STYLE_DISPLAY_NONE = "display: none;";


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

        policies.put("local", "Contenus du portail courant");
        policies.put("global", "Tous les contenus");

        String sitePolicy = po.getProperty("osivia.portal.publishingPolicy");

        String inheritedLabel = null;
        if ("satellite".equals(sitePolicy)) {
            inheritedLabel = "Contenus du portail";
        }
        ;
        if (inheritedLabel == null) {
            inheritedLabel = "Tous les contenus";
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
    public String formatHtmlTreePortalObjects(Page currentPage, ControllerContext context, String idPrefix) throws IOException {
        return this.formatHtmlTreePortalObjects(currentPage, context, idPrefix, false, false, false);
    }


    /**
     * {@inheritDoc}
     */
    public String formatHtmlTreePortalObjects(Page currentPage, ControllerContext context, String idPrefix, boolean displayRoot,
            boolean displayVirtualEndNodes, boolean sortAlphabetically) throws IOException {
        if ((currentPage == null) || (context == null)) {
            return null;
        }

        Locale locale = context.getServerInvocation().getRequest().getLocale();
        ResourceBundle resourceBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, locale);

        String virtualEndNodesText = null;
        if (displayVirtualEndNodes) {
            virtualEndNodesText = resourceBundle.getString(KEY_VIRTUAL_END_NODES);
        }

        Portal portal = currentPage.getPortal();

        Element ul;

        // Recursive tree generation
        Element ulChildren = this.generateRecursiveHtmlTreePortalObjects(portal, context, idPrefix, virtualEndNodesText, sortAlphabetically);

        // Root generation
        if (displayRoot) {
            String portalId = this.formatHtmlSafeEncodingId(portal.getId());

            ul = new DOMElement(QNAME_NODE_UL);

            Element li = new DOMElement(QNAME_NODE_LI);
            li.addAttribute(QNAME_ATTRIBUTE_ID, idPrefix + portalId);
            li.addAttribute(QNAME_ATTRIBUTE_CLASS, HTML_CLASS_NAVIGATION_ITEM);
            ul.add(li);

            Element a = new DOMElement(QNAME_NODE_A);
            a.addAttribute(QNAME_ATTRIBUTE_HREF, HTML_HREF_DEFAULT);
            a.setText(resourceBundle.getString(KEY_ROOT_NODE));
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
     * @param sortAlphabetically sort alphabetically indicator
     * @return HTML "ul" node
     * @throws IOException
     */
    private Element generateRecursiveHtmlTreePortalObjects(PortalObject parent, ControllerContext context, String idPrefix, String virtualEndNodesText,
            boolean sortAlphabetically) throws IOException {
        Locale[] locales = context.getServerInvocation().getRequest().getLocales();

        Collection<PortalObject> children = parent.getChildren(PortalObject.PAGE_MASK);
        if (CollectionUtils.isEmpty(children)) {
            return null;
        }

        // Contrôle des droits et tri des pages
        PortalAuthorizationManager authManager = this.portalAuthorizationManagerFactory.getManager();

        SortedSet<Page> sortedPages;
        if (sortAlphabetically) {
            sortedPages = new TreeSet<Page>(PageUtils.nameComparator);
        } else {
            sortedPages = new TreeSet<Page>(PageUtils.orderComparator);
        }

        for (PortalObject child : children) {
            PortalObjectPermission permission = new PortalObjectPermission(child.getId(), PortalObjectPermission.VIEW_MASK);

            if (authManager.checkPermission(permission)) {
                Page page = (Page) child;
                sortedPages.add(page);
            }
        }

        if (CollectionUtils.isEmpty(sortedPages)) {
            return null;
        }

        // Generate HTML node for each page
        Element ul = new DOMElement(QNAME_NODE_UL);
        ul.addAttribute(QNAME_ATTRIBUTE_CLASS, HTML_CLASS_NAVIGATION_ITEM);

        for (Page page : sortedPages) {
            String pageId = this.formatHtmlSafeEncodingId(page.getId());
            String pageName = PortalObjectUtils.getDisplayName(page, locales);

            // URL
            ViewPageCommand showPage = new ViewPageCommand(page.getId());
            String url = new PortalURLImpl(showPage, context, null, null).toString();
            url = url + "?init-state=true";

            Element li = new DOMElement(QNAME_NODE_LI);
            li.addAttribute(QNAME_ATTRIBUTE_ID, idPrefix + pageId);
            li.addAttribute(QNAME_ATTRIBUTE_CLASS, HTML_CLASS_NAVIGATION_ITEM);
            li.addAttribute(QNAME_ATTRIBUTE_REL, HTML_REL_PAGE);
            ul.add(li);

            Element a = new DOMElement(QNAME_NODE_A);
            a.addAttribute(QNAME_ATTRIBUTE_HREF, url);
            a.setText(pageName);
            li.add(a);

            // Recursive generation
            Element ulChildren = this.generateRecursiveHtmlTreePortalObjects(page, context, idPrefix, virtualEndNodesText, sortAlphabetically);
            if (ulChildren != null) {
                li.add(ulChildren);
            }
        }

        // Virtual end node
        if (StringUtils.isNotEmpty(virtualEndNodesText)) {
            String parentId = this.formatHtmlSafeEncodingId(parent.getId());

            Element liVirtualEndNode = new DOMElement(QNAME_NODE_LI);
            liVirtualEndNode.addAttribute(QNAME_ATTRIBUTE_ID, idPrefix + parentId + SUFFIX_VIRTUAL_END_NODES_ID);
            liVirtualEndNode.addAttribute(QNAME_ATTRIBUTE_CLASS, HTML_CLASS_NAVIGATION_ITEM);
            liVirtualEndNode.addAttribute(QNAME_ATTRIBUTE_REL, HTML_REL_PAGE);

            Element aVirtualEndNode = new DOMElement(QNAME_NODE_A);
            aVirtualEndNode.addAttribute(QNAME_ATTRIBUTE_HREF, HTML_HREF_DEFAULT);
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
        ResourceBundle resourceBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, locale);

        List<InstanceDefinition> instances = new ArrayList<InstanceDefinition>(this.instanceContainer.getDefinitions());
        if (CollectionUtils.isEmpty(instances)) {
            return StringUtils.EMPTY;
        }
        Collections.sort(instances, new InstanceComparator(locale));

        Element table = new DOMElement(QNAME_NODE_DIV);
        table.addAttribute(QNAME_ATTRIBUTE_CLASS, HTML_CLASS_FANCYBOX_TABLE);

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
            Element row = new DOMElement(QNAME_NODE_DIV);
            row.addAttribute(QNAME_ATTRIBUTE_CLASS, HTML_CLASS_FANCYBOX_ROW);
            table.add(row);
            Element leftCell = new DOMElement(QNAME_NODE_DIV);
            leftCell.addAttribute(QNAME_ATTRIBUTE_CLASS, HTML_CLASS_FANCYBOX_CELL);
            row.add(leftCell);
            Element middleCell = new DOMElement(QNAME_NODE_DIV);
            middleCell.addAttribute(QNAME_ATTRIBUTE_CLASS, HTML_CLASS_FANCYBOX_CELL);
            row.add(middleCell);
            Element rightCell = new DOMElement(QNAME_NODE_DIV);
            rightCell.addAttribute(QNAME_ATTRIBUTE_CLASS, HTML_CLASS_FANCYBOX_CELL);
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
            Element img = new DOMElement(QNAME_NODE_IMG);
            img.addAttribute(QNAME_ATTRIBUTE_SRC, iconLocation);
            leftCell.add(img);

            // Portlet display name
            String displayName = instance.getDisplayName().getString(locale, true);
            if (StringUtils.isEmpty(displayName)) {
                displayName = instance.getId();
            }
            middleCell.setText(displayName);

            // Submit
            Element input = new DOMElement(QNAME_NODE_INPUT);
            input.addAttribute(QNAME_ATTRIBUTE_TYPE, INPUT_TYPE_SUBMIT);
            input.addAttribute(QNAME_ATTRIBUTE_VALUE, resourceBundle.getString(KEY_ADD_PORTLET_SUBMIT_VALUE));
            input.addAttribute(QNAME_ATTRIBUTE_ONCLICK, "selectPortlet('" + instance.getId() + "', this.form)");
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
        ResourceBundle resourceBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, locale);

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
        Element divParent = new DOMElement(IFormatter.QNAME_NODE_DIV);
        divParent.addAttribute(QNAME_ATTRIBUTE_CLASS, HTML_CLASS_FANCYBOX_CONTAINER);
        if (CollectionUtils.isEmpty(windows)) {
            divParent.setText(HTML_TEXT_DEFAULT);
        }

        // Loop on each page window
        for (Window window : windows) {
            boolean checkboxChecked;

            String windowId = window.getId().toString(PortalObjectPath.SAFEST_FORMAT);
            String fancyboxId = PREFIX_ID_FANCYBOX_WINDOW_SETTINGS + windowId;

            // Fancybox
            Element divFancyboxContent = new DOMElement(QNAME_NODE_DIV);
            divFancyboxContent.addAttribute(QNAME_ATTRIBUTE_ID, fancyboxId);
            divParent.add(divFancyboxContent);

            // Form
            Element form = new DOMElement(QNAME_NODE_FORM);
            form.addAttribute(QNAME_ATTRIBUTE_ACTION, commandUrl);
            form.addAttribute(QNAME_ATTRIBUTE_METHOD, FORM_METHOD_GET);
            form.addAttribute(QNAME_ATTRIBUTE_CLASS, HTML_CLASS_FANCYBOX_FORM);
            divFancyboxContent.add(form);

            // Hidden fields
            Element inputHiddenAction = new DOMElement(QNAME_NODE_INPUT);
            inputHiddenAction.addAttribute(QNAME_ATTRIBUTE_TYPE, INPUT_TYPE_HIDDEN);
            inputHiddenAction.addAttribute(QNAME_ATTRIBUTE_NAME, HTML_NAME_ACTION);
            inputHiddenAction.addAttribute(QNAME_ATTRIBUTE_VALUE, HTML_VALUE_ACTION_CHANGE_WINDOW_PROPERTIES);
            form.add(inputHiddenAction);

            Element inputHiddenWindowId = new DOMElement(QNAME_NODE_INPUT);
            inputHiddenWindowId.addAttribute(QNAME_ATTRIBUTE_TYPE, INPUT_TYPE_HIDDEN);
            inputHiddenWindowId.addAttribute(QNAME_ATTRIBUTE_NAME, HTML_NAME_WINDOW_ID);
            inputHiddenWindowId.addAttribute(QNAME_ATTRIBUTE_VALUE, windowId);
            form.add(inputHiddenWindowId);

            // Table
            Element table = new DOMElement(QNAME_NODE_DIV);
            table.addAttribute(QNAME_ATTRIBUTE_CLASS, HTML_CLASS_FANCYBOX_TABLE);
            form.add(table);

            // Styles rows
            String windowStylesProperty = window.getDeclaredProperty("osivia.style");
            List<String> windowStyles = new ArrayList<String>();
            if (StringUtils.isNotEmpty(windowStylesProperty)) {
                windowStyles.addAll(Arrays.asList(windowStylesProperty.split(",")));
            }
            this.insertStylesRows(table, resourceBundle, portalStyles, windowStyles);

            // Title bar display row
            String hideTitle = window.getDeclaredProperty("osivia.hideTitle");
            checkboxChecked = !"1".equals(hideTitle);
            table.add(this.generateRow("Affichage barre de titre :", INPUT_TYPE_CHECKBOX, "displayTitle", "1", checkboxChecked));

            // Title
            String title = window.getDeclaredProperty("osivia.title");
            if (title == null) {
                title = StringUtils.EMPTY;
            }
            table.add(this.generateRow("Titre :", INPUT_TYPE_TEXT, "title", title, false));

            // Icons display
            String hideDecorators = window.getDeclaredProperty("osivia.hideDecorators");
            checkboxChecked = !"1".equals(hideDecorators);
            table.add(this.generateRow("Affichage des icônes :", INPUT_TYPE_CHECKBOX, "displayDecorators", "1", checkboxChecked));

            // AJAX links and forms
            String ajaxLinks = window.getProperty("osivia.ajaxLink");
            checkboxChecked = "1".equals(ajaxLinks);
            table.add(this.generateRow("Liens et formulaires en AJAX :", INPUT_TYPE_CHECKBOX, "ajaxLink", "1", checkboxChecked));

            // Print
            String print = window.getProperty("osivia.printPortlet");
            checkboxChecked = "1".equals(print);
            table.add(this.generateRow("Impression :", INPUT_TYPE_CHECKBOX, "printPortlet", "1", checkboxChecked));

            // Hide empty portlet
            String hideEmptyPortlet = window.getProperty("osivia.hideEmptyPortlet");
            checkboxChecked = "1".equals(hideEmptyPortlet);
            table.add(this.generateRow("Masquer ce portlet si contenu vide :", INPUT_TYPE_CHECKBOX, "hideEmptyPortlet", "1", checkboxChecked));

            // Conditional scope
            this.insertConditionalScopeRow(table, resourceBundle, window);

            // Customize ID
            String customizeId = window.getDeclaredProperty("osivia.idPerso");
            if (customizeId == null) {
                customizeId = StringUtils.EMPTY;
            }
            table.add(this.generateRow("Id. personnalisation :", INPUT_TYPE_TEXT, "idPerso", customizeId, false));

            // Shared cache ID
            String cacheId = window.getProperty("osivia.cacheID");
            if (cacheId == null) {
                cacheId = StringUtils.EMPTY;
            }
            table.add(this.generateRow("Id. cache partagé :", INPUT_TYPE_TEXT, "cacheID", cacheId, false));

            // Bash activation
            String bashActivation = window.getDeclaredProperty("osivia.bshActivation");
            boolean isBashActive = "1".equals(bashActivation);
            String scriptContent = window.getProperty("osivia.bshScript");
            if (scriptContent == null) {
                scriptContent = StringUtils.EMPTY;
            }
            this.insertDynamicPropertiesRows(table, resourceBundle, isBashActive, scriptContent);

            
            // Hide empty portlet
            String cacheEvents = window.getProperty("osivia.cacheEvents");
            checkboxChecked = "selection".equals(cacheEvents);
            table.add(this.generateRow("Dépendance / service sélection :", INPUT_TYPE_CHECKBOX, "selectionDep", "1", checkboxChecked));

            
            // Buttons
            Element buttonsContainer = new DOMElement(QNAME_NODE_DIV);
            buttonsContainer.addAttribute(QNAME_ATTRIBUTE_CLASS, "fancybox-center-content");
            form.add(buttonsContainer);

            // Submit button
            Element submitButton = new DOMElement(QNAME_NODE_INPUT);
            submitButton.addAttribute(QNAME_ATTRIBUTE_TYPE, INPUT_TYPE_SUBMIT);
            submitButton.addAttribute(QNAME_ATTRIBUTE_VALUE, "Changer les paramètres");
            buttonsContainer.add(submitButton);

            // Cancel button
            Element cancelButton = new DOMElement(QNAME_NODE_INPUT);
            cancelButton.addAttribute(QNAME_ATTRIBUTE_TYPE, INPUT_TYPE_BUTTON);
            cancelButton.addAttribute(QNAME_ATTRIBUTE_VALUE, "Annuler");
            cancelButton.addAttribute(QNAME_ATTRIBUTE_ONCLICK, "closeFancybox()");
            buttonsContainer.add(cancelButton);
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
    private String writeHtmlData(Element htmlElement) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        OutputStream bufferedOutput = new BufferedOutputStream(output);
        HTMLWriter htmlWriter = null;
        String resultat = null;
        try {
            htmlWriter = new HTMLWriter(bufferedOutput);
            htmlWriter.setEscapeText(false);
            htmlWriter.write(htmlElement);

            resultat = output.toString(CharEncoding.UTF_8);
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                bufferedOutput.close();
                output.close();
                htmlWriter.close();
            } catch (IOException e) {
                throw e;
            }
        }
        return resultat;
    }


    /**
     * Utility method used to insert styles rows.
     * 
     * @param tableParent parent table node
     * @param resourceBundle resource bundle
     * @param portalStyles portal styles
     * @param windowStyles window styles
     */
    private void insertStylesRows(Element tableParent, ResourceBundle resourceBundle, List<String> portalStyles, List<String> windowStyles) {
        String displayStyle = StringUtils.EMPTY;
        for (String windowStyle : windowStyles) {
            displayStyle += windowStyle + " ";
        }
        if (StringUtils.isEmpty(displayStyle)) {
            displayStyle = resourceBundle.getString(KEY_WINDOW_PROPERTIES_NO_STYLE);
        }

        Set<String> styles = new HashSet<String>(portalStyles);
        styles.addAll(windowStyles);

        // Styles row
        Element rowStyles = new DOMElement(QNAME_NODE_DIV);
        rowStyles.addAttribute(QNAME_ATTRIBUTE_CLASS, HTML_CLASS_FANCYBOX_ROW);
        tableParent.add(rowStyles);

        // Styles left cell
        Element leftCellStyles = new DOMElement(QNAME_NODE_DIV);
        leftCellStyles.addAttribute(QNAME_ATTRIBUTE_CLASS, HTML_CLASS_FANCYBOX_CELL + " " + HTML_CLASS_FANCYBOX_LABEL);
        leftCellStyles.setText(resourceBundle.getString(KEY_WINDOW_PROPERTIES_STYLES));
        rowStyles.add(leftCellStyles);

        // Styles right cell
        Element rightCellStyles = new DOMElement(QNAME_NODE_DIV);
        rightCellStyles.addAttribute(QNAME_ATTRIBUTE_CLASS, HTML_CLASS_FANCYBOX_CELL);
        rightCellStyles.setText(displayStyle);
        rowStyles.add(rightCellStyles);

        // Styles right cell display link
        Element aStylesDisplay = new DOMElement(QNAME_NODE_A);
        aStylesDisplay.addAttribute(QNAME_ATTRIBUTE_HREF, HTML_HREF_DEFAULT);
        aStylesDisplay.addAttribute(QNAME_ATTRIBUTE_ONCLICK, "toggleRow(this, '" + HTML_CLASS_TOGGLE_STYLES_ROW + "')");
        aStylesDisplay.setText(resourceBundle.getString(KEY_WINDOW_PROPERTIES_STYLES_DISPLAY_LINK));
        rightCellStyles.add(aStylesDisplay);

        if (CollectionUtils.isNotEmpty(styles)) {
            // Styles display toggle row
            Element rowToggle = new DOMElement(QNAME_NODE_DIV);
            rowToggle.addAttribute(QNAME_ATTRIBUTE_CLASS, HTML_CLASS_TOGGLE_ROW + " " + HTML_CLASS_TOGGLE_STYLES_ROW + " " + HTML_CLASS_FANCYBOX_ROW);
            rowToggle.addAttribute(QNAME_ATTRIBUTE_STYLE, HTML_STYLE_DISPLAY_NONE);
            tableParent.add(rowToggle);

            // Styles display toggle empty left cell
            Element leftCellToggle = new DOMElement(QNAME_NODE_DIV);
            leftCellToggle.addAttribute(QNAME_ATTRIBUTE_CLASS, HTML_CLASS_FANCYBOX_CELL);
            leftCellToggle.setText(HTML_TEXT_DEFAULT);
            rowToggle.add(leftCellToggle);

            // Styles display toggle right cell
            Element rightCellToggle = new DOMElement(QNAME_NODE_DIV);
            rightCellToggle.addAttribute(QNAME_ATTRIBUTE_CLASS, HTML_CLASS_FANCYBOX_CELL);
            rowToggle.add(rightCellToggle);

            // Styles display table
            Element table = new DOMElement(QNAME_NODE_DIV);
            table.addAttribute(QNAME_ATTRIBUTE_CLASS, HTML_CLASS_FANCYBOX_TABLE);
            rightCellToggle.add(table);

            // Loop on each style
            for (String style : styles) {
                boolean checkboxChecked = windowStyles.contains(style);
                Element row = this.generateRow(style, INPUT_TYPE_CHECKBOX, HTML_NAME_STYLE, style, checkboxChecked);
                table.add(row);
            }
        }
    }


    /**
     * Utility method used to insert conditional scope row.
     * 
     * @param tableParent parent table node
     * @param resourceBundle resource bundle
     * @param window window
     */
    private void insertConditionalScopeRow(Element tableParent, ResourceBundle resourceBundle, Window window) {
        String conditionalScope = window.getProperty("osivia.conditionalScope");
        Map<String, String> scopes = new LinkedHashMap<String, String>();

        List<ProfilBean> profils = this.getProfilManager().getListeProfils();
        for (ProfilBean profil : profils) {
            scopes.put(profil.getName(), "Profil " + profil.getName());
        }
        if (!scopes.isEmpty()) {
            // Row
            Element row = new DOMElement(QNAME_NODE_DIV);
            row.addAttribute(QNAME_ATTRIBUTE_CLASS, HTML_CLASS_FANCYBOX_ROW);
            tableParent.add(row);

            // Left cell
            Element leftCell = new DOMElement(QNAME_NODE_DIV);
            leftCell.addAttribute(QNAME_ATTRIBUTE_CLASS, HTML_CLASS_FANCYBOX_CELL + " " + HTML_CLASS_FANCYBOX_LABEL);
            leftCell.setText("Affichage conditionné au profil :");
            row.add(leftCell);

            // Right cell
            Element rightCell = new DOMElement(QNAME_NODE_DIV);
            rightCell.addAttribute(QNAME_ATTRIBUTE_CLASS, HTML_CLASS_FANCYBOX_CELL);
            row.add(rightCell);

            // Select
            Element select = new DOMElement(QNAME_NODE_SELECT);
            select.addAttribute(QNAME_ATTRIBUTE_NAME, "conditionalScope");
            rightCell.add(select);

            // All profiles option
            Element optionAllProfiles = new DOMElement(QNAME_NODE_OPTION);
            optionAllProfiles.addAttribute(QNAME_ATTRIBUTE_VALUE, StringUtils.EMPTY);
            if (StringUtils.isNotEmpty(conditionalScope)) {
                optionAllProfiles.addAttribute(QNAME_ATTRIBUTE_SELECTED, SELECTED);
            }
            optionAllProfiles.setText("Tous les profils");
            select.add(optionAllProfiles);

            for (String scope : scopes.keySet()) {
                // Scope option
                Element optionScope = new DOMElement(QNAME_NODE_OPTION);
                optionScope.addAttribute(QNAME_ATTRIBUTE_VALUE, scope);
                if (StringUtils.equals(conditionalScope, scope)) {
                    optionScope.addAttribute(QNAME_ATTRIBUTE_SELECTED, SELECTED);
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
     * @param resourceBundle resource bundle
     * @param isBashActive bash active indicator
     * @param scriptContent script content
     */
    private void insertDynamicPropertiesRows(Element tableParent, ResourceBundle resourceBundle, boolean isBashActive, String scriptContent) {
        // Label - checkbox input unique ID link
        String checkboxId = UUID.randomUUID().toString();

        // Dynamic properties label
        String dynamicPropertiesLabel;
        if (isBashActive) {
            dynamicPropertiesLabel = "Script Shell";
        } else {
            dynamicPropertiesLabel = "-";
        }

        // Dynamic properties row
        Element rowDynamicProperties = new DOMElement(QNAME_NODE_DIV);
        rowDynamicProperties.addAttribute(QNAME_ATTRIBUTE_CLASS, HTML_CLASS_FANCYBOX_ROW);
        tableParent.add(rowDynamicProperties);

        // Dynamic properties left cell
        Element leftCellDynamicProperties = new DOMElement(QNAME_NODE_DIV);
        leftCellDynamicProperties.addAttribute(QNAME_ATTRIBUTE_CLASS, HTML_CLASS_FANCYBOX_CELL + " " + HTML_CLASS_FANCYBOX_LABEL);
        leftCellDynamicProperties.setText("Propriétés dynamiques :");
        rowDynamicProperties.add(leftCellDynamicProperties);

        // Dynamic properties right cell
        Element rightCellDynamicProperties = new DOMElement(QNAME_NODE_DIV);
        rightCellDynamicProperties.addAttribute(QNAME_ATTRIBUTE_CLASS, HTML_CLASS_FANCYBOX_CELL);
        rightCellDynamicProperties.setText(dynamicPropertiesLabel);
        rowDynamicProperties.add(rightCellDynamicProperties);

        // Dynamic properties right cell display link
        Element aDynamicPropertiesDisplay = new DOMElement(QNAME_NODE_A);
        aDynamicPropertiesDisplay.addAttribute(QNAME_ATTRIBUTE_HREF, HTML_HREF_DEFAULT);
        aDynamicPropertiesDisplay.addAttribute(QNAME_ATTRIBUTE_ONCLICK, "toggleRow(this, '" + HTML_CLASS_TOGGLE_DYNAMIC_PROPERTIES_ROW + "')");
        aDynamicPropertiesDisplay.setText("Modifier");
        rightCellDynamicProperties.add(aDynamicPropertiesDisplay);

        // Dynamic properties display toggle row
        Element rowToggle = new DOMElement(QNAME_NODE_DIV);
        rowToggle.addAttribute(QNAME_ATTRIBUTE_CLASS, HTML_CLASS_TOGGLE_ROW + " " + HTML_CLASS_TOGGLE_DYNAMIC_PROPERTIES_ROW + " " + HTML_CLASS_FANCYBOX_ROW);
        rowToggle.addAttribute(QNAME_ATTRIBUTE_STYLE, HTML_STYLE_DISPLAY_NONE);
        tableParent.add(rowToggle);

        // Dynamic properties display toggle left cell
        Element leftCellToggle = new DOMElement(QNAME_NODE_DIV);
        leftCellToggle.addAttribute(QNAME_ATTRIBUTE_CLASS, HTML_CLASS_FANCYBOX_CELL);
        rowToggle.add(leftCellToggle);

        // Dynamic properties display toggle checkbox label
        Element label = new DOMElement(QNAME_NODE_LABEL);
        label.addAttribute(QNAME_ATTRIBUTE_FOR, checkboxId);
        label.setText("Activer le script beanshell :");
        leftCellToggle.add(label);

        // Dynamic properties display toggle checkbox
        Element checkbox = new DOMElement(QNAME_NODE_INPUT);
        checkbox.addAttribute(QNAME_ATTRIBUTE_ID, checkboxId);
        checkbox.addAttribute(QNAME_ATTRIBUTE_TYPE, INPUT_TYPE_CHECKBOX);
        checkbox.addAttribute(QNAME_ATTRIBUTE_NAME, "bshActivation");
        checkbox.addAttribute(QNAME_ATTRIBUTE_VALUE, "1");
        checkbox.addAttribute(QNAME_ATTRIBUTE_CLASS, HTML_CLASS_SMALL_INPUT);
        if (isBashActive) {
            checkbox.addAttribute(QNAME_ATTRIBUTE_CHECKED, CHECKED);
        }
        leftCellToggle.add(checkbox);

        // Dynamic properties display toggle right cell
        Element rightCellToggle = new DOMElement(QNAME_NODE_DIV);
        rightCellToggle.addAttribute(QNAME_ATTRIBUTE_CLASS, HTML_CLASS_FANCYBOX_CELL);
        rowToggle.add(rightCellToggle);

        // Dynamic properties display toggle right cell textarea
        Element textarea = new DOMElement(QNAME_NODE_TEXTAREA);
        textarea.addAttribute(QNAME_ATTRIBUTE_NAME, "bshScript");
        textarea.addAttribute(QNAME_ATTRIBUTE_ROWS, "10");
        textarea.addAttribute(QNAME_ATTRIBUTE_COLS, "75");
        textarea.setText(scriptContent);
        rightCellToggle.add(textarea);

        // Dynamic properties display toggle right cell example
        Element example = new DOMElement(QNAME_NODE_PRE);
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
        Element row = new DOMElement(QNAME_NODE_DIV);
        row.addAttribute(QNAME_ATTRIBUTE_CLASS, HTML_CLASS_FANCYBOX_ROW);

        // Left cell
        Element leftCell = new DOMElement(QNAME_NODE_DIV);
        leftCell.addAttribute(QNAME_ATTRIBUTE_CLASS, HTML_CLASS_FANCYBOX_CELL + " " + HTML_CLASS_FANCYBOX_LABEL);
        leftCell.setText(label);
        row.add(leftCell);

        // Right cell
        Element rightCell = new DOMElement(QNAME_NODE_DIV);
        rightCell.addAttribute(QNAME_ATTRIBUTE_CLASS, HTML_CLASS_FANCYBOX_CELL);
        row.add(rightCell);

        // Right cell input
        Element input = new DOMElement(QNAME_NODE_INPUT);
        input.addAttribute(QNAME_ATTRIBUTE_TYPE, inputType);
        input.addAttribute(QNAME_ATTRIBUTE_NAME, inputName);
        if (inputValue != null) {
            input.addAttribute(QNAME_ATTRIBUTE_VALUE, inputValue);
        }
        if (INPUT_TYPE_CHECKBOX.equals(inputType)) {
            input.addAttribute(QNAME_ATTRIBUTE_CLASS, HTML_CLASS_SMALL_INPUT);
            if (checkboxChecked) {
                input.addAttribute(QNAME_ATTRIBUTE_CHECKED, CHECKED);
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
            if (!Constants.VALUE_WINDOWS_SETTING_WIZARD_MODE.equals(command.getControllerContext().getAttribute(ControllerCommand.SESSION_SCOPE,
                    "osivia.windowSettingMode"))) {
                return response;
            }

            PageRendition rendition = (PageRendition) response;
            PageCommand pageCommand = (PageCommand) command;
            Page page = pageCommand.getPage();
            ControllerContext context = command.getControllerContext();

            // This is for inject the pageSettings
            ControllerRequestDispatcher dispatcher = context.getRequestDispatcher(this.getTargetContextPath(), this.getPageSettingPath());

            // Formatter
            dispatcher.setAttribute(Constants.ATTR_WINDOWS_FORMATTER, this);
            // Context
            dispatcher.setAttribute(Constants.ATTR_WINDOWS_CONTROLLER_CONTEXT, context);
            // URL générique de commande
            String portalContextPath = context.getServerInvocation().getServerContext().getPortalContextPath();
            dispatcher.setAttribute(Constants.ATTR_WINDOWS_COMMAND_URL, portalContextPath + "/commands");
            // Current page
            dispatcher.setAttribute(Constants.ATTR_WINDOWS_PAGE, page);

            if (!(page instanceof ITemplatePortalObject)) {
                // Pour mémo : soit un template, soit une page classique
                this.injectPortletSetting(dispatcher, page, rendition, context);
            }

            dispatcher.include();

            context.setAttribute(ControllerCommand.REQUEST_SCOPE, Constants.ATTR_WINDOWS_SETTINGS_CONTENT, dispatcher.getMarkup());
        }

        return response;
    }


    @SuppressWarnings("unchecked")
    private void injectPortletSetting(ControllerRequestDispatcher dispatcher, Page currentPage, PageRendition rendition, ControllerContext context)
            throws Exception {
        HttpServletRequest request = context.getServerInvocation().getServerContext().getClientRequest();

        List<Window> windows = new ArrayList<Window>();

        String layoutId = currentPage.getProperty(ThemeConstants.PORTAL_PROP_LAYOUT);
        PortalLayout pageLayout = this.layoutService.getLayout(layoutId, true);

        this.synchronizeRegionContexts(rendition, currentPage);

        for (Object regionCtxObjet : rendition.getPageResult().getRegions()) {

            RegionRendererContext renderCtx = (RegionRendererContext) regionCtxObjet;

            // on vérifie que cette réion fait partie du layout
            // (elle contient des portlets)
            if (pageLayout.getLayoutInfo().getRegionNames().contains(renderCtx.getId())) {
                Map<String, String> regionProperties = renderCtx.getProperties();

                String regionId = renderCtx.getId();

                Map regionPorperties = renderCtx.getProperties();

                PortalObjectId popupWindowId = (PortalObjectId) context.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupModeWindowID");

                if (popupWindowId == null) {
                regionProperties.put(Constants.ATTR_WINDOWS_WIZARD_MODE, Constants.VALUE_WINDOWS_WIZARD_TEMPLATE_MODE);
                regionProperties.put(Constants.ATTR_WINDOWS_ADD_PORTLET_URL, "#add-portlet");
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
                        Window window = (Window) this.getPortalObjectContainer().getObject(poid);

                        if (!((DynamicWindow) window).isSessionWindow()) {

                            if (popupWindowId == null) {

                // Window settings mode
                            windowProperties.put(Constants.ATTR_WINDOWS_SETTING_MODE, Constants.VALUE_WINDOWS_SETTING_WIZARD_MODE);

                            // Commande suppression
                            windowProperties.put(Constants.ATTR_WINDOWS_DELETE_PORTLET_URL, "#delete-portlet");

                            // Commande paramètres
                            windowProperties.put(Constants.ATTR_WINDOWS_DISPLAY_SETTINGS_URL, "#" + PREFIX_ID_FANCYBOX_WINDOW_SETTINGS + windowId);

                            windows.add(window);

                            // Commandes de déplacement
                            MoveWindowCommand upC = new MoveWindowCommand(windowId, MoveWindowCommand.UP);
                            String upUrl = context.renderURL(upC, urlContext, URLFormat.newInstance(true, true));
                            windowProperties.put(Constants.ATTR_WINDOWS_UP_COMMAND_URL, upUrl);

                            MoveWindowCommand downC = new MoveWindowCommand(windowId, MoveWindowCommand.DOWN);
                            String downUrl = context.renderURL(downC, urlContext, URLFormat.newInstance(true, true));
                            windowProperties.put(Constants.ATTR_WINDOWS_DOWN_COMMAND_URL, downUrl);

                            MoveWindowCommand previousC = new MoveWindowCommand(windowId, MoveWindowCommand.PREVIOUS_REGION);
                            String previousRegionUrl = context.renderURL(previousC, urlContext, URLFormat.newInstance(true, true));
                            windowProperties.put(Constants.ATTR_WINDOWS_PREVIOUS_REGION_COMMAND_URL, previousRegionUrl);

                            MoveWindowCommand nextRegionC = new MoveWindowCommand(windowId, MoveWindowCommand.NEXT_REGION);
                            String nextRegionUrl = context.renderURL(nextRegionC, urlContext, URLFormat.newInstance(true, true));
                            windowProperties.put(Constants.ATTR_WINDOWS_NEXT_REGION_COMMAND_URL, nextRegionUrl);

                            // Titre de la fenetre d'administration
                            String instanceDisplayName = null;
                            InstanceDefinition defInstance = this.getInstanceContainer().getDefinition(window.getContent().getURI());
                            if (defInstance != null) {
                                instanceDisplayName = defInstance.getDisplayName().getString(request.getLocale(), true);
                            }

                            if (instanceDisplayName != null) {
                                windowProperties.put("osivia.instanceDisplayName", instanceDisplayName);
                            }
                            }

                        }

                    }
                }
            }
        }

        dispatcher.setAttribute(Constants.ATTR_WINDOWS_CURRENT_LIST, windows);
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

}
