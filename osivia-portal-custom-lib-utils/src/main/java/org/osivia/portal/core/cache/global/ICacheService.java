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
package org.osivia.portal.core.cache.global;



public interface ICacheService {
	
	/* Cache du bandeau */
	
	public long getHeaderCount();
	public void incrementHeaderCount( )	;
	
	/* Cache des profils */
	
	public long getProfilsCount();
	public void incrementProfilsCount( )	;

	
	/* Données paramétrages */
	
    public long getGlobalParametersCount() ;
    public void incrementGlobalParametersCount( )   ;
	
    /* Mass import */

    public void setImportRunning(boolean importRunning) ;
    public boolean isImportRunning() ;
}
