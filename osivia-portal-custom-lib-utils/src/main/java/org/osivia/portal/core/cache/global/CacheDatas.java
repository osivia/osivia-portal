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

import java.io.Serializable;

public class CacheDatas implements Serializable{

	private static final long serialVersionUID = -4356842856191357397L;
	
    private long headerCount = 1;
    private long profilsCount = 1;
    private long globalParametersCount = 1;
    public boolean importRunning = false;
    
    
    public boolean isImportRunning() {
        return importRunning;
    }
   
    public void setImportRunning(boolean importRunning) {
        this.importRunning = importRunning;
    }

    public long getGlobalParametersCount() {
        return globalParametersCount;
    }
    
    public void setGlobalParametersCount(long globalParametersCount) {
        this.globalParametersCount = globalParametersCount;
    }
    public long getHeaderCount() {
        return headerCount;
    }
    public void setHeaderCount(long headerCount) {
        this.headerCount = headerCount;
    }
    public long getProfilsCount() {
        return profilsCount;
    }
    public void setProfilsCount(long profilsCount) {
        this.profilsCount = profilsCount;
    }
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

	
}
