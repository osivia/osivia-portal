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

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.jboss.portal.core.model.portal.PortalObject;
import org.osivia.portal.core.imports.ImportCheckerDatas;



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
    public ImportCheckerDatas getImportCheckerDatas() ;
    public void setImportCheckerDatas(ImportCheckerDatas importCheckerDatas);
    
    /* Export and control of portal configuration  */
    
    public void configExport(OutputStream output, PortalObject portalObject, String filter) throws Exception;
    public void startCheckPortalObject( String portalObjectToCheckOnCluster)  throws Exception ;
    public String generateControlKey(String portalCheckObject) throws Exception;
    public void stopCheckPortalObject( )  throws Exception ;
    public boolean isChecking( ) ;
   
    
}
