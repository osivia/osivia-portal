/*
 * (C) Copyright 2014 OSIVIA (http://www.osivia.com)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */
package org.osivia.portal.core.login;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import javax.portlet.PortletRequest;
import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.aspects.server.UserInterceptor;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Context;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.PortalObjectPermission;
import org.jboss.portal.identity.User;
import org.jboss.portal.security.impl.jacc.JACCPortalPrincipal;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManager;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManagerFactory;
import org.jboss.portal.server.ServerInterceptor;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.customization.CustomizationContext;
import org.osivia.portal.api.directory.entity.DirectoryPerson;
import org.osivia.portal.api.directory.v2.DirServiceFactory;
import org.osivia.portal.api.directory.v2.model.Person;
import org.osivia.portal.api.directory.v2.service.PersonService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.log.LoggerMessage;
import org.osivia.portal.api.login.IUserDatasModule;
import org.osivia.portal.api.login.IUserDatasModuleRepository;
import org.osivia.portal.api.login.UserDatasModuleMetadatas;
import org.osivia.portal.core.cms.CMSPage;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.customization.ICustomizationService;
import org.osivia.portal.core.error.IPortalLogger;
import org.osivia.portal.core.profils.ProfilBean;
import org.osivia.portal.core.profils.ProfilManager;

/**
 * Login interceptor.
 * 
 * @see ServerInterceptor
 * @see IUserDatasModuleRepository
 */
public class LoginInterceptor extends ServerInterceptor implements IUserDatasModuleRepository {

    /** Customization service. */
    private ICustomizationService customizationService;
    /** Portal authorization manager factory. */
    private PortalAuthorizationManagerFactory portalAuthorizationManagerFactory;
    /** Portal object container. */
    private PortalObjectContainer portalObjectContainer;

    /** CMS service locator. */
    private ICMSServiceLocator cmsServiceLocator;


    /** Modules comparator. */
    private final Comparator<UserDatasModuleMetadatas> modulesComparator;

    /** User modules. */
    private final Map<String, UserDatasModuleMetadatas> userModules;
    /** Sorted modules. */
    private final SortedSet<UserDatasModuleMetadatas> sortedModules;

    /** Log. */
    private final Log log;
    /** Admin portal object identifier. */
    private final PortalObjectId adminId;


    /**
     * Constructor.
     */
    public LoginInterceptor() {
        super();

        // Modules comparator
        this.modulesComparator = new Comparator<UserDatasModuleMetadatas>() {
            public int compare(UserDatasModuleMetadatas m1, UserDatasModuleMetadatas m2) {
                return m1.getOrder() - m2.getOrder();
            }
        };

        this.userModules = new ConcurrentHashMap<>();
        this.sortedModules = new TreeSet<>(modulesComparator);

        // Log
        this.log = LogFactory.getLog(this.getClass());
        // Admin portal object identifier
        this.adminId = PortalObjectId.parse("/admin", PortalObjectPath.CANONICAL_FORMAT);
    }


    private void synchronizeSortedModules() {
        this.sortedModules.clear();

        for (UserDatasModuleMetadatas module : this.userModules.values()) {
            this.sortedModules.add(module);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("rawtypes")
    protected void invoke(ServerInvocation invocation) throws Exception, InvocationException {

        User user = (User) invocation.getAttribute(Scope.PRINCIPAL_SCOPE, UserInterceptor.USER_KEY);

        String remoteUser = invocation.getServerContext().getClientRequest().getRemoteUser();

        /*
         * Traitement spécifique pour utilisateurs non connectés
         * A déléguer dans un customizer
         */

        if (remoteUser != null && user == null) {
            if (remoteUser.endsWith("@ATEN")) {
                HttpSession session = invocation.getServerContext().getClientRequest().getSession();

                ProfilBean profilDefaut = new ProfilBean("@ATEN", "default", "info-parents", "");
                session.setAttribute(ProfilManager.ATTRIBUTE_PROFILE_NAME, profilDefaut);
            }
        }


        if (user != null) {

            String userPagesPreloaded = (String) invocation.getAttribute(Scope.SESSION_SCOPE, "osivia.userLoginDone");

            if (!"1".equals(userPagesPreloaded)) {
                if (!isAdministrator()) {
                    // JSS 20131113 : pas de preload pour certains groupes
                    boolean noPreload = false;

                    String preloadingDisabledDGroups = System.getProperty("preloading.disabledGroups");

                    if (preloadingDisabledDGroups != null) {
                        List<String> groups = Arrays.asList(preloadingDisabledDGroups.split(","));

                        // Get the current authenticated subject through the JACC
                        // contract
                        Subject subject = (Subject) PolicyContext.getContext("javax.security.auth.Subject.container");

                        // utilisation mapping standard du portail
                        JACCPortalPrincipal pp = new JACCPortalPrincipal(subject);

                        Iterator iter = pp.getRoles().iterator();
                        while (iter.hasNext()) {
                            Principal principal = (Principal) iter.next();
                            if (groups.contains(principal.getName())) {
                                noPreload = true;
                                break;
                            }
                        }
                    }

                    if (noPreload) {
                        invocation.setAttribute(Scope.REQUEST_SCOPE, "osivia.userPreloadedPages", new ArrayList<CMSPage>());
                    } else {
                        // Appel pages préchargées
                        try {

                            CMSServiceCtx cmsContext = new CMSServiceCtx();

                            cmsContext.setServerInvocation(invocation);

                            invocation.setAttribute(Scope.REQUEST_SCOPE, "osivia.userPreloadedPages",
                                    this.getCmsService().computeUserPreloadedPages(cmsContext));
                        } catch (Exception e) {
                            // Don't block login
                            log.error("Can't compute cms pages for user " + user.getUserName());
                        }
                    }
                }

                // Appel module userdatas
                this.loadUserDatas(invocation);

                // Job is marked as done
                invocation.setAttribute(Scope.SESSION_SCOPE, "osivia.userLoginDone", "1");
                IPortalLogger.logger.info(new LoggerMessage("user login", true));
            }
        }


        List<?> domainsAttribute = (List<?>) invocation.getAttribute(Scope.SESSION_SCOPE, InternalConstants.USER_DOMAINS_ATTRIBUTE);
        if (domainsAttribute == null) {
            // Portal
            Portal portal = this.getPortal(invocation);
            // Portal domains
            if (portal != null) {
                String[] properties = StringUtils.split(portal.getDeclaredProperty("osivia.site.domains"), ",");
                List<String> domains;
                if (properties == null) {
                    domains = new ArrayList<>(0);
                } else {
                    domains = new ArrayList<>(properties.length);
                    for (String property : properties) {
                        domains.add(StringUtils.trim(property));
                    }
                }
                invocation.setAttribute(Scope.SESSION_SCOPE, InternalConstants.USER_DOMAINS_ATTRIBUTE, domains);
            }
        }


        invocation.invokeNext();
    }


    /**
     * Check if the current user is administrator.
     * 
     * @return true if the current user is administrator.
     */
    private boolean isAdministrator() {
        PortalAuthorizationManager manager = this.portalAuthorizationManagerFactory.getManager();
        PortalObjectPermission permission = new PortalObjectPermission(this.adminId, PortalObjectPermission.VIEW_MASK);
        return manager.checkPermission(permission);
    }


    /**
     * Get portal.
     * 
     * @param invocation server invocation
     * @return portal
     */
    private Portal getPortal(ServerInvocation invocation) {
        // HTTP servlet request
        HttpServletRequest request = invocation.getServerContext().getClientRequest();
        // Host
        String host = request.getServerName();

        // Root context
        Context context = this.portalObjectContainer.getContext();
        // Portals
        Collection<PortalObject> portals = context.getChildren(PortalObject.PORTAL_MASK);


        // Portal
        Portal portal = null;
        for (PortalObject portalObject : portals) {
            if ((portalObject instanceof Portal) && StringUtils.equals(host, portalObject.getDeclaredProperty("osivia.site.hostName"))) {
                portal = (Portal) portalObject;
                break;
            }
        }
        if (portal == null) {
            portal = context.getDefaultPortal();
        }

        return portal;
    }


    /**
     * Get CMS service.
     * 
     * @return CMS service
     */
    private ICMSService getCmsService() {
        if (this.cmsServiceLocator == null) {
            this.cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        }

        return this.cmsServiceLocator.getCMSService();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void reload(PortletRequest portletRequest) {
        ControllerContext ctx = (ControllerContext) portletRequest.getAttribute("osivia.controller");

        this.loadUserDatas(ctx.getServerInvocation());
    }


    private void loadUserDatas(ServerInvocation invocation) {
        Map<String, Object> contextDatas = new Hashtable<String, Object>();
        String userPrincipal = invocation.getServerContext().getClientRequest().getUserPrincipal().getName();
        DirectoryPerson person = null;

        HttpServletRequest httpRequest = invocation.getServerContext().getClientRequest();
        for (UserDatasModuleMetadatas module : this.sortedModules) {
            // compatibilty v3.2 - provide informations about logged users with a map or with a user object

            List<String> excludedFeederNames = (List<String>) httpRequest.getAttribute("osivia.valve.feeder");
            if ((excludedFeederNames == null) || !excludedFeederNames.contains(module.getName())) {
                // exclusion du module si déjà appelé par valve
                person = module.getModule().computeLoggedUser(httpRequest);
            }
        }

        // add person in session
        if (person != null) {
            contextDatas.put(Constants.ATTR_LOGGED_PERSON, person);
        }

        Map<String, Object> userDatas = new ConcurrentHashMap<String, Object>();
        contextDatas.put("osivia.userDatas", userDatas);

        // call customizer to populate userDatas
        CustomizationContext context = new CustomizationContext(contextDatas);
        this.customizationService.customize(IUserDatasModule.CUSTOMIZER_ID, context);

        invocation.setAttribute(Scope.SESSION_SCOPE, "osivia.userDatas", userDatas);
        invocation.setAttribute(Scope.SESSION_SCOPE, Constants.ATTR_LOGGED_PERSON, person);
        invocation.setAttribute(Scope.SESSION_SCOPE, "osivia.userDatas.refreshTimestamp", System.currentTimeMillis());

        // Debug user map

        if (log.isDebugEnabled()) {
            StringBuffer sb = new StringBuffer();
            sb.append("userDatas[");
            for (String key : userDatas.keySet()) {
                sb.append(key + "=" + userDatas.get(key) + ";");
            }
            sb.append("]   person[");
            if (person != null) {
                for (String key : person.getExtraProperties().keySet()) {
                    sb.append(key + "=" + person.getExtraProperties().get(key) + ";");
                }
            }
            sb.append("]");

            log.debug(new String(sb));
        }

        // @since v4.4, new person object
        PersonService service = DirServiceFactory.getService(PersonService.class);
        if (service != null) {
            Person p = service.getPerson(userPrincipal);
            if (p != null) {
                contextDatas.put(Constants.ATTR_LOGGED_PERSON_2, p);
                invocation.setAttribute(Scope.SESSION_SCOPE, Constants.ATTR_LOGGED_PERSON_2, p);
            }
        }

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void register(UserDatasModuleMetadatas moduleMetadatas) {
        this.userModules.put(moduleMetadatas.getName(), moduleMetadatas);
        this.synchronizeSortedModules();

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void unregister(UserDatasModuleMetadatas moduleMetadatas) {
        this.userModules.remove(moduleMetadatas.getName());
        this.synchronizeSortedModules();

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public UserDatasModuleMetadatas getModule(String name) {
        return this.userModules.get(name);
    }


    /**
     * Setter for customizationService.
     *
     * @param customizationService the customizationService to set
     */
    public void setCustomizationService(ICustomizationService customizationService) {
        this.customizationService = customizationService;
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
     * Setter for portalObjectContainer.
     * 
     * @param portalObjectContainer the portalObjectContainer to set
     */
    public void setPortalObjectContainer(PortalObjectContainer portalObjectContainer) {
        this.portalObjectContainer = portalObjectContainer;
    }

}
