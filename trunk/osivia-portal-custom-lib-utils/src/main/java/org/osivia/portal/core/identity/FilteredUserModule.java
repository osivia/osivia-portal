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
package org.osivia.portal.core.identity;

import java.util.HashSet;
import java.util.Set;

import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.db.HibernateUserModuleImpl;

public class FilteredUserModule extends HibernateUserModuleImpl {
	
	/* Cette méthode est bouchonnée car incohérente en back-office
	 */
	
	public Set findUsers(int offset, int limit) throws IdentityException	{
		if( limit == 1000)
			return new HashSet();
		else
			return super.findUsers(offset, limit);
	}

}
