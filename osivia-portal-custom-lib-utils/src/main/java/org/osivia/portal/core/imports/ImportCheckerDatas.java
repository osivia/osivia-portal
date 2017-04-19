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
package org.osivia.portal.core.imports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * this data is sent between cluster nodes to check if updates are synchronized
 */


public class ImportCheckerDatas implements Serializable {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8803972331428550362L;
    
    /** The checker timestamp. */
    private final long checkerTimestamp ;   
    
    /** The checker timestamp. */
    private final String referenceDigest ;    
    
    /** The checker timestamp. */
    private boolean checking = false;    

    
    
    public boolean isChecking() {
        return checking;
    }



    
    public void setChecking(boolean checking) {
        this.checking = checking;
    }



    /**
     * Instantiates a new import checker datas.
     *
     * @param pageId the page id
     */
    public ImportCheckerDatas(String portalObjectId, String referenceDigest) {
        super();
        this.portalObjectId = portalObjectId;
        this.checkerTimestamp = System.currentTimeMillis();
        this.referenceDigest = referenceDigest;
    }

    
    
    public String getReferenceDigest() {
        return referenceDigest;
    }


    /**
     * Gets the checker timestamp.
     *
     * @return the checker timestamp
     */
    public  long getCheckerTimestamp() {
        return checkerTimestamp;
    }


    
    /** The nodes. */
    List<ImportCheckerNode> nodes = new ArrayList<ImportCheckerNode>();
    
    
    /**
     * Gets the nodes.
     *
     * @return the nodes
     */
    public List<ImportCheckerNode> getNodes() {
        return nodes;
    }

    /** The page id. */
    private String portalObjectId;
    
    
    /**
     * Gets the page id.
     *
     * @return the page id
     */
    public String getPortalObjectId() {
        return portalObjectId;
    }

    
    /**
     * Sets the page id.
     *
     * @param pageId the new page id
     */
    public void setPortalObjectId(String portalObjectId) {
        this.portalObjectId = portalObjectId;
    }



}
