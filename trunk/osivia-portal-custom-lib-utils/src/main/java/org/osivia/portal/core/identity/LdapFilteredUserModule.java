package org.osivia.portal.core.identity;

import java.util.HashSet;
import java.util.Set;

import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.ldap.LDAPUserModuleImpl;

public class LdapFilteredUserModule extends LDAPUserModuleImpl {
	
	/* Cette méthode est bouchonnée car incohérente en back-office
	 */
	
	public Set findUsers(int offset, int limit) throws IdentityException	{
		if( limit == 1000)
			return new HashSet();
		else
			return super.findUsers(offset, limit);
	}
	

}
