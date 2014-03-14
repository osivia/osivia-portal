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
 *
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

import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.aspects.server.UserInterceptor;
import org.jboss.portal.identity.User;
import org.jboss.portal.security.impl.jacc.JACCPortalPrincipal;
import org.jboss.portal.server.ServerInterceptor;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.login.IUserDatasModuleRepository;
import org.osivia.portal.api.login.UserDatasModuleMetadatas;
import org.osivia.portal.core.cms.CMSPage;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.cms.spi.ICMSIntegration;


public class LoginInterceptor extends ServerInterceptor implements IUserDatasModuleRepository {

	protected static final Log logger = LogFactory.getLog(LoginInterceptor.class);

	Map<String, UserDatasModuleMetadatas> userModules = new Hashtable<String, UserDatasModuleMetadatas>();
	SortedSet<UserDatasModuleMetadatas> sortedModules = new TreeSet<UserDatasModuleMetadatas>(moduleComparator);

	
	private static ICMSServiceLocator cmsServiceLocator ;

	public static ICMSService getCMSService() throws Exception {
		
		if( cmsServiceLocator == null){
			cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
		}
	
		return cmsServiceLocator.getCMSService();

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

		if (user != null) {

			String userPagesPreloaded = (String) invocation.getAttribute(Scope.SESSION_SCOPE, "osivia.userLoginDone");

			if (!"1".equals(userPagesPreloaded)) {
			    
			    
             
                
                /* JSS 20131113 : pas de preload pour certains groupes
                 * 
                 * 
                 * */
                
                boolean noPreload = false;
                
                String preloadingDisabledDGroups = System.getProperty("preloading.disabledGroups");
                
                if( preloadingDisabledDGroups != null){
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
                    
                if( noPreload)  {
                    
                    invocation.setAttribute(Scope.REQUEST_SCOPE, "osivia.userPreloadedPages", new ArrayList<CMSPage>());
                    
                }   else    {


				/* Appel pages préchargées */

				try {

					CMSServiceCtx cmsContext = new CMSServiceCtx();

					cmsContext.setServerInvocation(invocation);

					invocation.setAttribute(Scope.REQUEST_SCOPE, "osivia.userPreloadedPages", getCMSService()
							.computeUserPreloadedPages(cmsContext));
				} catch (Exception e) {
					// Don't block login
					logger.error("Can't compute cms pages for user " + user.getUserName());
				}

				
                
                }
				/* Appel module userdatas */

				Map<String, Object> userDatas = new Hashtable<String, Object>();

				for (UserDatasModuleMetadatas module : sortedModules) {
					module.getModule().computeUserDatas(invocation.getServerContext().getClientRequest(), userDatas);
				}

				invocation.setAttribute(Scope.SESSION_SCOPE, "osivia.userDatas", userDatas);

				// Job is marked as done

				invocation.setAttribute(Scope.SESSION_SCOPE, "osivia.userLoginDone", "1");
			}

		}

		invocation.invokeNext();
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
