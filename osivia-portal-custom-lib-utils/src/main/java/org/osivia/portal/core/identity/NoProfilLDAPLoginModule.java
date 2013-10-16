package org.osivia.portal.core.identity;

import java.security.Principal;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.InitialContext;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;

import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.MembershipModule;
import org.jboss.portal.identity.RoleModule;
import org.jboss.portal.identity.UserModule;
import org.jboss.portal.identity.UserProfileModule;
import org.jboss.portal.identity.auth.UserPrincipal;
import org.jboss.security.auth.spi.LdapLoginModule;
import org.osivia.portal.core.profils.FilteredRole;


/**
 * affecttation d'un rôle 'role-no-profil' pour l'es utilisateurs n'ayant aucun
 * profil dans l'ENT
 * 
 * @author cap2j
 */
public class NoProfilLDAPLoginModule extends LdapLoginModule {
	private static final org.jboss.logging.Logger log = org.jboss.logging.Logger
			.getLogger(NoProfilLDAPLoginModule.class);

	protected String userModuleJNDIName;
	protected String roleModuleJNDIName;
	protected String membershipModuleJNDIName;
	protected String userProfileModuleJNDIName;
	protected String sProfils;

	private UserModule userModule;
	private RoleModule roleModule;
	private MembershipModule membershipModule;
	private UserProfileModule userProfileModule;
	private List<String> profils;

	public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
		super.initialize(subject, callbackHandler, sharedState, options);

		userModuleJNDIName = (String) options.get("userModuleJNDIName");
		roleModuleJNDIName = (String) options.get("roleModuleJNDIName");
		membershipModuleJNDIName = (String) options.get("membershipModuleJNDIName");
		userProfileModuleJNDIName = (String) options.get("userProfileModuleJNDIName");
		sProfils = (String) options.get("profils");
	}

	protected UserModule getUserModule() throws Exception {
		if (userModule == null) {
			userModule = (UserModule) new InitialContext().lookup(userModuleJNDIName);
		}
		if (userModule == null) {
			throw new IdentityException("Cannot obtain UserModule using JNDI name:" + userModuleJNDIName);
		}

		return userModule;
	}

	protected RoleModule getRoleModule() throws Exception {

		if (roleModule == null) {
			roleModule = (RoleModule) new InitialContext().lookup(roleModuleJNDIName);
		}
		if (roleModule == null) {
			throw new IdentityException("Cannot obtain RoleModule using JNDI name:" + roleModuleJNDIName);
		}
		return roleModule;
	}

	protected MembershipModule getMembershipModule() throws Exception {

		if (membershipModule == null) {
			membershipModule = (MembershipModule) new InitialContext().lookup(membershipModuleJNDIName);
		}
		if (membershipModule == null) {
			throw new IdentityException("Cannot obtain MembershipModule using JNDI name:" + membershipModuleJNDIName);
		}
		return membershipModule;
	}

	protected UserProfileModule getUserProfileModule() throws Exception {

		if (userProfileModule == null) {
			userProfileModule = (UserProfileModule) new InitialContext().lookup(userProfileModuleJNDIName);
		}
		if (userProfileModule == null) {
			throw new IdentityException("Cannot obtain UserProfileModule using JNDI name:" + userProfileModuleJNDIName);
		}
		return userProfileModule;
	}

	protected List<String> getProfils()  {

		if (profils == null) {
			profils = Arrays.asList(sProfils.split("\\|"));
			if (profils.size() == 1 && "unknown".equals(profils.get(0)))
				profils = new ArrayList<String>();
		}
		return profils;
	}

	protected boolean validatePassword(String inputPassword, String string1) {
		return true;
	}

	protected Group[] getRoleSets() throws LoginException {
		// obtain user principals
		Set principals = subject.getPrincipals();

		Group[] destinationRolesGroup = super.getRoleSets();

		if (getProfils().size() > 0) {

			try {
				for (Iterator iterator = principals.iterator(); iterator.hasNext();) {
					Object o = iterator.next();
					if (!(o instanceof Group)) {
						continue;
					}
					Group group = (Group) o;

					if (group.getName().equals("Roles")) {

						// Test si un des profils est affecté à l'utilisateur

						boolean profilAffecte = false;
						for (String profil : getProfils()) {
							Principal role = createIdentity(profil);
							if (group.isMember(role))
								profilAffecte = true;

						}

						// Si aucun profil, on affecte le role 'Aucun profile'

						if (!profilAffecte) {
							for (int i = 0; i < destinationRolesGroup.length; i++) {
								Group destinationGroup = destinationRolesGroup[i];
								if (destinationGroup.getName().equals("Roles")) {

									Principal role = createIdentity(FilteredRole.UNCHECKED_ROLE_NAME);
									if (!destinationGroup.isMember(role)) {
										destinationGroup.addMember(role);
									}
								}
							}
						}

					}
				}
			} catch (Exception e) {
				// just a try
				log.error("Error when adding additional role: ", e);
			}
		}

		return destinationRolesGroup;
	}

	/**
	 * Subclass to use the PortalPrincipal to make the username easier to
	 * retrieve by the portal.
	 */
	protected Principal createIdentity(String rolename) throws Exception {
		return new UserPrincipal(rolename);
	}

}
