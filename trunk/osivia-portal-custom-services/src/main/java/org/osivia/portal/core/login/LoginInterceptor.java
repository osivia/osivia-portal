package org.osivia.portal.core.login;

import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.aspects.server.UserInterceptor;
import org.jboss.portal.identity.User;
import org.jboss.portal.server.ServerInterceptor;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.login.IUserDatasModuleRepository;
import org.osivia.portal.api.login.UserDatasModuleMetadatas;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.spi.ICMSIntegration;


public class LoginInterceptor extends ServerInterceptor implements IUserDatasModuleRepository {

	protected static final Log logger = LogFactory.getLog(LoginInterceptor.class);

	Map<String, UserDatasModuleMetadatas> userModules = new Hashtable<String, UserDatasModuleMetadatas>();
	SortedSet<UserDatasModuleMetadatas> sortedModules = new TreeSet<UserDatasModuleMetadatas>(moduleComparator);

	ICMSService cmsService ;

	public ICMSService getCMSService () throws Exception	{
		if( cmsService == null)
			cmsService  = Locator.findMBean(ICMSService.class,"osivia:service=NuxeoService");
		
		return cmsService;
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

	protected void invoke(ServerInvocation invocation) throws Exception, InvocationException {

		User user = (User) invocation.getAttribute(Scope.PRINCIPAL_SCOPE, UserInterceptor.USER_KEY);

		if (user != null) {

			String userPagesPreloaded = (String) invocation.getAttribute(Scope.SESSION_SCOPE, "osivia.userLoginDone");

			if (!"1".equals(userPagesPreloaded)) {

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
