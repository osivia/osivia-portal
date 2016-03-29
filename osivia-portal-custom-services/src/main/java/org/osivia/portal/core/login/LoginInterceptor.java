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
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.portlet.PortletRequest;
import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.aspects.server.UserInterceptor;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.identity.User;
import org.jboss.portal.security.impl.jacc.JACCPortalPrincipal;
import org.jboss.portal.server.ServerInterceptor;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.customization.CustomizationContext;
import org.osivia.portal.api.directory.entity.DirectoryPerson;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.login.IUserDatasModule;
import org.osivia.portal.api.login.IUserDatasModuleRepository;
import org.osivia.portal.api.login.UserDatasModuleMetadatas;
import org.osivia.portal.core.cms.CMSPage;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.customization.ICustomizationService;
import org.osivia.portal.core.profils.ProfilBean;
import org.osivia.portal.core.profils.ProfilManager;


public class LoginInterceptor extends ServerInterceptor implements IUserDatasModuleRepository {

    protected static final Log logger = LogFactory.getLog(LoginInterceptor.class);

    Map<String, UserDatasModuleMetadatas> userModules = new Hashtable<String, UserDatasModuleMetadatas>();
    SortedSet<UserDatasModuleMetadatas> sortedModules = new TreeSet<UserDatasModuleMetadatas>(moduleComparator);

    /** Customization service. */
    private ICustomizationService customizationService;

    private static ICMSServiceLocator cmsServiceLocator;

    public static ICMSService getCMSService() throws Exception {

        if (cmsServiceLocator == null) {
            cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        }

        return cmsServiceLocator.getCMSService();

    }

    /**
     * Setter for customizationService.
     * 
     * @param customizationService the customizationService to set
     */
    public void setCustomizationService(ICustomizationService customizationService) {
        this.customizationService = customizationService;
    }


    public static final Comparator<UserDatasModuleMetadatas> moduleComparator = new Comparator<UserDatasModuleMetadatas>() {

        public int compare(UserDatasModuleMetadatas m1, UserDatasModuleMetadatas m2) {
            return m1.getOrder() - m2.getOrder();

        }
    };


    private void synchronizeSortedModules() {

        sortedModules = new TreeSet<UserDatasModuleMetadatas>(moduleComparator);

        for (UserDatasModuleMetadatas module : userModules.values()) {
            sortedModules.add(module);
        }

    }

    @SuppressWarnings("rawtypes")
    protected void invoke(ServerInvocation invocation) throws Exception, InvocationException {

        User user = (User) invocation.getAttribute(Scope.PRINCIPAL_SCOPE, UserInterceptor.USER_KEY);

        String remoteUser = invocation.getServerContext().getClientRequest().getRemoteUser();

        /* Traitement spécifique pour utilisateurs non connectés
         * TODO A déléguer dans un customizer
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


                /*
                 * JSS 20131113 : pas de preload pour certains groupes
                 */

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


                    /* Appel pages préchargées */

                    try {

                        CMSServiceCtx cmsContext = new CMSServiceCtx();

                        cmsContext.setServerInvocation(invocation);

                        invocation.setAttribute(Scope.REQUEST_SCOPE, "osivia.userPreloadedPages", getCMSService().computeUserPreloadedPages(cmsContext));
                    } catch (Exception e) {
                        // Don't block login
                        logger.error("Can't compute cms pages for user " + user.getUserName());
                    }


                }
                /* Appel module userdatas */

                loadUserDatas(invocation);

                // Job is marked as done

                invocation.setAttribute(Scope.SESSION_SCOPE, "osivia.userLoginDone", "1");
            }

        }

        invocation.invokeNext();
    }

    public void reload(PortletRequest portletRequest) {
        ControllerContext ctx = (ControllerContext) portletRequest.getAttribute("osivia.controller");

        loadUserDatas(ctx.getServerInvocation());

    }

    private void loadUserDatas(ServerInvocation invocation) {
        Map<String, Object> contextDatas = new Hashtable<String, Object>();
        DirectoryPerson person = null;

        for (UserDatasModuleMetadatas module : sortedModules) {
            // compatibilty v3.2 - provide informations about logged users with a map or with a user object
            person = module.getModule().computeLoggedUser(invocation.getServerContext().getClientRequest());
        }

        // add person in session
        if (person != null)
            contextDatas.put(Constants.ATTR_LOGGED_PERSON, person);

        Map<String, Object> userDatas = new Hashtable<String, Object>();
        contextDatas.put("osivia.userDatas", userDatas);

        // call customizer to populate userDatas
        CustomizationContext context = new CustomizationContext(contextDatas);
        this.customizationService.customize(IUserDatasModule.CUSTOMIZER_ID, context);

        invocation.setAttribute(Scope.SESSION_SCOPE, "osivia.userDatas", userDatas);
        invocation.setAttribute(Scope.SESSION_SCOPE, Constants.ATTR_LOGGED_PERSON, person);
        invocation.setAttribute(Scope.SESSION_SCOPE, "osivia.userDatas.refreshTimestamp", System.currentTimeMillis());
    }

    public void register(UserDatasModuleMetadatas moduleMetadatas) {
        userModules.put(moduleMetadatas.getName(), moduleMetadatas);
        synchronizeSortedModules();

    }

    public void unregister(UserDatasModuleMetadatas moduleMetadatas) {
        userModules.remove(moduleMetadatas.getName());
        synchronizeSortedModules();

    }
}
