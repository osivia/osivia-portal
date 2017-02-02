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



/**
 * The Class stores the checked value for a page/node.
 */
public class ImportCheckerNode implements Serializable {
    
   
        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 6192751704519095530L;
        
        /**
         * Instantiates a new import checker node.
         *
         * @param nodeName the node name
         * @param hashCode the hash code
         */
        public ImportCheckerNode(String nodeName, String md5Digest) {
            super();
            this.nodeName = nodeName;
            this.md5Digest = md5Digest;
        }
        
        /** The node name. */
        private String nodeName;
        
        /**
         * Gets the node name.
         *
         * @return the node name
         */
        public String getNodeName() {
            return nodeName;
        }
        
        /**
         * Sets the node name.
         *
         * @param nodeName the new node name
         */
        public void setNodeName(String nodeName) {
            this.nodeName = nodeName;
        }
        
        /**
         * Gets the hash code.
         *
         * @return the hash code
         */
        public String getMd5Digest() {
            return md5Digest;
        }
        
   
        
        /** The hash code. */
        private String md5Digest;

        
        public void setMd5Digest(String md5Digest) {
            this.md5Digest = md5Digest;
        }
        
  
}
